// src/dao/ProdukDAO.java
package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Barang;
import model.Jasa;
import model.Produk;

public class ProdukDAO {

    private static final int DEFAULT_PAGE_SIZE = 20;

    // ===== BARANG =====

    /**
     * Get all barang without pagination (backward compatible)
     */
    public List<Barang> getAllBarang() {
        return getAllBarang(1, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get barang with pagination - uses LIMIT and OFFSET for better performance on large tables
     * 
     * @param page 1-based page number
     * @param pageSize number of records per page
     * @return list of barang for the specified page
     */
    public List<Barang> getAllBarang(int page, int pageSize) {
        List<Barang> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT p.*, b.kategori_id, b.deskripsi, k.nama_kategori " +
                     "FROM produk p JOIN barang b ON p.id = b.id " +
                     "LEFT JOIN kategori_barang k ON b.kategori_id = k.id ORDER BY p.id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Barang br = new Barang();
                br.setId(rs.getInt("id"));
                br.setKode(rs.getString("kode"));
                br.setNama(rs.getString("nama"));
                br.setHarga(rs.getDouble("harga"));
                br.setKategoriId(rs.getInt("kategori_id"));
                br.setNamaKategori(rs.getString("nama_kategori"));
                br.setDeskripsi(rs.getString("deskripsi"));
                br.setCreatedAt(rs.getString("created_at"));
                list.add(br);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of barang records
     * 
     * @return total number of records
     */
    public int countBarang() {
        String sql = "SELECT COUNT(*) FROM barang";
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
     * Calculate total number of pages for barang
     * 
     * @param pageSize number of records per page
     * @return total page count
     */
    public int getPageCountBarang(int pageSize) {
        int total = countBarang();
        return (int) Math.ceil((double) total / pageSize);
    }

    public boolean insertBarang(Barang b) {
        String sqlProduk = "INSERT INTO produk (kode, nama, harga) VALUES (?, ?, ?)";
        String sqlBarang = "INSERT INTO barang (id, kategori_id, deskripsi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement(sqlProduk, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, b.getKode());
                ps1.setString(2, b.getNama());
                ps1.setDouble(3, b.getHarga());
                ps1.executeUpdate();

                ResultSet keys = ps1.getGeneratedKeys();
                if (keys.next()) {
                    int produkId = keys.getInt(1);
                    PreparedStatement ps2 = conn.prepareStatement(sqlBarang);
                    ps2.setInt(1, produkId);
                    ps2.setInt(2, b.getKategoriId());
                    ps2.setString(3, b.getDeskripsi());
                    ps2.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBarang(Barang b) {
        String sqlProduk = "UPDATE produk SET kode=?, nama=?, harga=? WHERE id=?";
        String sqlBarang = "UPDATE barang SET kategori_id=?, deskripsi=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement(sqlProduk);
                ps1.setString(1, b.getKode());
                ps1.setString(2, b.getNama());
                ps1.setDouble(3, b.getHarga());
                ps1.setInt(4, b.getId());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(sqlBarang);
                ps2.setInt(1, b.getKategoriId());
                ps2.setString(2, b.getDeskripsi());
                ps2.setInt(3, b.getId());
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBarang(int id) {
        String sqlBarang = "DELETE FROM barang WHERE id=?";
        String sqlProduk = "DELETE FROM produk WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete from child table first
                try (PreparedStatement ps1 = conn.prepareStatement(sqlBarang)) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }
                // Then parent
                try (PreparedStatement ps2 = conn.prepareStatement(sqlProduk)) {
                    ps2.setInt(1, id);
                    int rows = ps2.executeUpdate();
                    conn.commit();
                    return rows > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// ===== JASA =====

    /**
     * Get all jasa without pagination (backward compatible)
     */
    public List<Jasa> getAllJasa() {
        return getAllJasa(1, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get jasa with pagination - uses LIMIT and OFFSET for better performance on large tables
     * 
     * @param page 1-based page number
     * @param pageSize number of records per page
     * @return list of jasa for the specified page
     */
    public List<Jasa> getAllJasa(int page, int pageSize) {
        List<Jasa> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT p.*, j.deskripsi FROM produk p JOIN jasa j ON p.id = j.id ORDER BY p.id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jasa j = new Jasa();
                j.setId(rs.getInt("id"));
                j.setKode(rs.getString("kode"));
                j.setNama(rs.getString("nama"));
                j.setHarga(rs.getDouble("harga"));
                j.setDeskripsi(rs.getString("deskripsi"));
                j.setCreatedAt(rs.getString("created_at"));
                list.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of jasa records
     * 
     * @return total number of records
     */
    public int countJasa() {
        String sql = "SELECT COUNT(*) FROM jasa";
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
     * Calculate total number of pages for jasa
     * 
     * @param pageSize number of records per page
     * @return total page count
     */
    public int getPageCountJasa(int pageSize) {
        int total = countJasa();
        return (int) Math.ceil((double) total / pageSize);
    }

    public boolean insertJasa(Jasa j) {
        String sqlProduk = "INSERT INTO produk (kode, nama, harga) VALUES (?, ?, ?)";
        String sqlJasa = "INSERT INTO jasa (id, deskripsi) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement(sqlProduk, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, j.getKode());
                ps1.setString(2, j.getNama());
                ps1.setDouble(3, j.getHarga());
                ps1.executeUpdate();

                ResultSet keys = ps1.getGeneratedKeys();
                if (keys.next()) {
                    int produkId = keys.getInt(1);
                    PreparedStatement ps2 = conn.prepareStatement(sqlJasa);
                    ps2.setInt(1, produkId);
                    ps2.setString(2, j.getDeskripsi());
                    ps2.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateJasa(Jasa j) {
        String sqlProduk = "UPDATE produk SET kode=?, nama=?, harga=? WHERE id=?";
        String sqlJasa = "UPDATE jasa SET deskripsi=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement(sqlProduk);
                ps1.setString(1, j.getKode());
                ps1.setString(2, j.getNama());
                ps1.setDouble(3, j.getHarga());
                ps1.setInt(4, j.getId());
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(sqlJasa);
                ps2.setString(1, j.getDeskripsi());
                ps2.setInt(2, j.getId());
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteJasa(int id) {
        String sqlJasa = "DELETE FROM jasa WHERE id=?";
        String sqlProduk = "DELETE FROM produk WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete from child table first
                try (PreparedStatement ps1 = conn.prepareStatement(sqlJasa)) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }
                // Then parent
                try (PreparedStatement ps2 = conn.prepareStatement(sqlProduk)) {
                    ps2.setInt(1, id);
                    int rows = ps2.executeUpdate();
                    conn.commit();
                    return rows > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// ===== PRODUK KHUSUS BARANG (untuk transaksi) =====

    /**
     * Get all produk (barang only for transaction) without pagination
     */
    public List<Produk> getAllProduk() {
        return getAllProduk(1, DEFAULT_PAGE_SIZE);
    }

    /**
     * Get produk with pagination for transaction purposes
     * 
     * @param page 1-based page number
     * @param pageSize number of records per page
     * @return list of produk for the specified page
     */
    public List<Produk> getAllProduk(int page, int pageSize) {
        List<Produk> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT p.* FROM produk p " +
                    "JOIN barang b ON p.id = b.id " +
                    "ORDER BY p.kode LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produk p = new Produk();
                p.setId(rs.getInt("id"));
                p.setKode(rs.getString("kode"));
                p.setNama(rs.getString("nama"));
                p.setHarga(rs.getDouble("harga"));
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


public Produk getById(int id) {
        String sql = "SELECT * FROM produk WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Produk p = new Produk();
                p.setId(rs.getInt("id"));
                p.setKode(rs.getString("kode"));
                p.setNama(rs.getString("nama"));
                p.setHarga(rs.getDouble("harga"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if produkId is a barang
    public boolean isBarang(int produkId) {
        String sql = "SELECT 1 FROM barang WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, produkId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Check if produkId is a jasa
    public boolean isJasa(int produkId) {
        String sql = "SELECT 1 FROM jasa WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, produkId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== AUTO GENERATE KODE =====
    
    // Get last kode barang (e.g., "BRG-005")
    public String getLastKodeBarang() {
        String sql = "SELECT p.kode FROM produk p JOIN barang b ON p.id = b.id ORDER BY p.id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("kode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get last kode jasa (e.g., "JSA-005")
    public String getLastKodeJasa() {
        String sql = "SELECT p.kode FROM produk p JOIN jasa j ON p.id = j.id ORDER BY p.id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("kode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Generate next kode for barang (BRG-XXX)
    public String generateNextKodeBarang() {
        String lastKode = getLastKodeBarang();
        int nextNum = 1;
        if (lastKode != null && lastKode.startsWith("BRG-")) {
            try {
                nextNum = Integer.parseInt(lastKode.substring(4)) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1;
            }
        }
        if (nextNum > 999) {
            return "BRG-" + nextNum;
        }
        return String.format("BRG-%03d", nextNum);
    }

    // Generate next kode for jasa (JSA-XXX)
    public String generateNextKodeJasa() {
        String lastKode = getLastKodeJasa();
        int nextNum = 1;
        if (lastKode != null && lastKode.startsWith("JSA-")) {
            try {
                nextNum = Integer.parseInt(lastKode.substring(4)) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1;
            }
        }
        if (nextNum > 999) {
            return "JSA-" + nextNum;
        }
        return String.format("JSA-%03d", nextNum);
    }
}
