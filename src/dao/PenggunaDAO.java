// src/dao/PenggunaDAO.java
package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pengguna;
import util.AuditLogger;
import util.LoginAttemptTracker;
import util.PasswordUtil;

public class PenggunaDAO {

    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Login method - authenticates user by username and password
     */
    public Pengguna login(String username, String password) {
        // Check if account is locked
        if (LoginAttemptTracker.isLockedOut(username)) {
            int remaining = LoginAttemptTracker.getRemainingLockoutSeconds(username);
            AuditLogger.logLoginFailed(username, "Account locked. Try again in " + remaining + " seconds");
            return null;
        }
        
        String sql = "SELECT * FROM pengguna WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
// Verify password using hashing
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    // Reset failed attempts on success
                    LoginAttemptTracker.resetAttempts(username);
                    
                    Pengguna p = new Pengguna();
                    p.setId(rs.getInt("id"));
                    p.setUsername(rs.getString("username"));
                    p.setPassword(storedPassword);
                    p.setNamaLengkap(rs.getString("nama_lengkap"));
                    p.setRole(rs.getString("role"));
                    p.setCreatedAt(rs.getString("created_at"));
                    
                    AuditLogger.logLoginSuccess(username);
                    return p;
                } else if (storedPassword.equals(password)) {
                    // Backward compatibility: plaintext password found
                    // Upgrade to hashed password
                    String hashed = PasswordUtil.hashPassword(password);
                    updatePasswordHash(rs.getInt("id"), hashed);
                    
                    // Reset failed attempts on success
                    LoginAttemptTracker.resetAttempts(username);
                    
                    Pengguna p = new Pengguna();
                    p.setId(rs.getInt("id"));
                    p.setUsername(rs.getString("username"));
                    p.setPassword(hashed);
                    p.setNamaLengkap(rs.getString("nama_lengkap"));
                    p.setRole(rs.getString("role"));
                    p.setCreatedAt(rs.getString("created_at"));
                    
                    AuditLogger.logLoginSuccess(username);
                    AuditLogger.logPasswordChange(username);
                    return p;
                } else {
                    // Password mismatch
                    boolean locked = LoginAttemptTracker.recordFailedAttempt(username);
                    if (locked) {
                        AuditLogger.logLoginFailed(username, "Account locked due to multiple failed attempts");
                    } else {
                        int remaining = LoginAttemptTracker.getRemainingAttempts(username);
                        AuditLogger.logLoginFailed(username, "Invalid password. " + remaining + " attempts remaining");
                    }
                }
            } else {
                // Username not found
                AuditLogger.logLoginFailed(username, "User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AuditLogger.logLoginFailed(username, "Database error: " + e.getMessage());
        }
        return null;
    }

/**
     * Get all pengguna without pagination (backward compatible)
     */
    public List<Pengguna> getAll() {
        return getAll(1, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get pengguna with pagination - uses LIMIT and OFFSET for better performance on large tables
     * 
     * @param page 1-based page number
     * @param pageSize number of records per page
     * @return list of pengguna for the specified page
     */
    public List<Pengguna> getAll(int page, int pageSize) {
        List<Pengguna> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM pengguna ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pengguna p = new Pengguna();
                p.setId(rs.getInt("id"));
                p.setUsername(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                p.setNamaLengkap(rs.getString("nama_lengkap"));
                p.setRole(rs.getString("role"));
                p.setCreatedAt(rs.getString("created_at"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of pengguna records
     * 
     * @return total number of records
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM pengguna";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculate total number of pages
     * 
     * @param pageSize number of records per page
     * @return total page count
     */
    public int getPageCount(int pageSize) {
        int total = count();
        return (int) Math.ceil((double) total / pageSize);
    }

public boolean insert(Pengguna p) {
        // Hash password before storing
        String hashedPassword = PasswordUtil.hashPassword(p.getPassword());
        
        String sql = "INSERT INTO pengguna (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, p.getNamaLengkap());
            ps.setString(4, p.getRole());
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                AuditLogger.logUserCreate("system", p.getUsername());
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

public boolean update(Pengguna p) {
        // Handle "unchanged" special case - keep existing password
        String passwordToStore = p.getPassword();
        if ("unchanged".equals(passwordToStore)) {
            // Get existing password from database
            Pengguna existing = getById(p.getId());
            if (existing != null) {
                passwordToStore = existing.getPassword();
            } else {
                return false;
            }
        } else if (passwordToStore != null && !PasswordUtil.isHashed(passwordToStore)) {
            // Hash new password
            passwordToStore = PasswordUtil.hashPassword(passwordToStore);
        }
        
        String sql = "UPDATE pengguna SET username=?, password=?, nama_lengkap=?, role=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getUsername());
            ps.setString(2, passwordToStore);
            ps.setString(3, p.getNamaLengkap());
            ps.setString(4, p.getRole());
            ps.setInt(5, p.getId());
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                AuditLogger.logUserUpdate("system", p.getUsername());
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        // Get username before deleting for audit log
        String username = null;
       try {
            Pengguna p = getById(id);
            if (p != null) {
                username = p.getUsername();
            }
        } catch (Exception e) {
            // ignore
        }
        
        String sql = "DELETE FROM pengguna WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean result = ps.executeUpdate() > 0;
            if (result && username != null) {
                AuditLogger.logUserDelete("system", username);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
public Pengguna getById(int id) {
        String sql = "SELECT * FROM pengguna WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pengguna p = new Pengguna();
                p.setId(rs.getInt("id"));
                p.setUsername(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                p.setNamaLengkap(rs.getString("nama_lengkap"));
                p.setRole(rs.getString("role"));
                p.setCreatedAt(rs.getString("created_at"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Update password hash for a user (for migration from plaintext to hashed)
     * @param id User ID
     * @param hashedPassword New hashed password
     * @return true if successful
     */
    private boolean updatePasswordHash(int id, String hashedPassword) {
        String sql = "UPDATE pengguna SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
