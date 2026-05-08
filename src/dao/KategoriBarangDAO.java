// src/dao/KategoriBarangDAO.java
package dao;

import config.DatabaseConnection;
import model.KategoriBarang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriBarangDAO {

    public List<KategoriBarang> getAll() {
        List<KategoriBarang> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori_barang ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                KategoriBarang k = new KategoriBarang();
                k.setId(rs.getInt("id"));
                k.setNamaKategori(rs.getString("nama_kategori"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KategoriBarang k) {
        String sql = "INSERT INTO kategori_barang (nama_kategori) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNamaKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KategoriBarang k) {
        String sql = "UPDATE kategori_barang SET nama_kategori=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNamaKategori());
            ps.setInt(2, k.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kategori_barang WHERE id=?";
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