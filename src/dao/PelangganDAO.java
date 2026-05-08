// src/dao/PelangganDAO.java
package dao;

import config.DatabaseConnection;
import model.Pelanggan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganDAO {

    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Get all pelanggan without pagination (backward compatible)
     */
    public List<Pelanggan> getAll() {
        return getAll(1, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get pelanggan with pagination - uses LIMIT and OFFSET for better performance on large tables
     * 
     * @param page 1-based page number
     * @param pageSize number of records per page
     * @return list of pelanggan for the specified page
     */
    public List<Pelanggan> getAll(int page, int pageSize) {
        List<Pelanggan> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM pelanggan ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pelanggan p = new Pelanggan();
                p.setId(rs.getInt("id"));
                p.setNama(rs.getString("nama"));
                p.setNoTelp(rs.getString("no_telp"));
                p.setAlamat(rs.getString("alamat"));
                p.setNoPlat(rs.getString("no_plat"));
                p.setCreatedAt(rs.getString("created_at"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of pelanggan records
     * 
     * @return total number of records
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM pelanggan";
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

    public boolean insert(Pelanggan p) {
        String sql = "INSERT INTO pelanggan (nama, no_telp, alamat, no_plat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoTelp());
            ps.setString(3, p.getAlamat());
            ps.setString(4, p.getNoPlat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Pelanggan p) {
        String sql = "UPDATE pelanggan SET nama=?, no_telp=?, alamat=?, no_plat=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoTelp());
            ps.setString(3, p.getAlamat());
            ps.setString(4, p.getNoPlat());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM pelanggan WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
