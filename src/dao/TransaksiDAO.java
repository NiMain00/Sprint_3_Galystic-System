// src/dao/TransaksiDAO.java
package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.DetailTransaksi;
import model.Transaksi;

public class TransaksiDAO {

    public String generateNoTransaksi(String tipe) {

        String prefix;

        if (tipe.equals("Barang")) {
            prefix = "TRX-B-";
        } else if (tipe.equals("Jasa")) {
            prefix = "TRX-J-";
        } else {
            prefix = "TRX-C-";
        }

        String sql = "SELECT MAX(no_transaksi) AS max_no FROM transaksi WHERE no_transaksi LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");

            ResultSet rs = ps.executeQuery();

            int next = 1;

            if (rs.next() && rs.getString("max_no") != null) {

                String lastNo = rs.getString("max_no");

                // Ambil 5 digit terakhir
                String numberPart = lastNo.substring(lastNo.length() - 5);

                next = Integer.parseInt(numberPart) + 1;
            }

            return prefix + String.format("%05d", next);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return prefix + "00001";
    }



    public boolean insertTransaksi(Transaksi t) {
        String sqlTrx = "INSERT INTO transaksi (no_transaksi, tanggal, tipe_transaksi, total_harga, pelanggan_id, pengguna_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, produk_id, qty, harga_satuan, subtotal) " +
                           "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, t.getNoTransaksi());
                ps1.setString(2, t.getTanggal());
                ps1.setString(3, t.getTipeTransaksi());
                ps1.setDouble(4, t.getTotalHarga());
                if (t.getPelangganId() > 0) {
                    ps1.setInt(5, t.getPelangganId());
                } else {
                    ps1.setNull(5, Types.INTEGER);
                }
                ps1.setInt(6, t.getPenggunaId());
                ps1.executeUpdate();

                ResultSet keys = ps1.getGeneratedKeys();
                if (keys.next()) {
                    int trxId = keys.getInt(1);
                    for (DetailTransaksi d : t.getDetailList()) {
                        PreparedStatement ps2 = conn.prepareStatement(sqlDetail);
                        ps2.setInt(1, trxId);
                        ps2.setInt(2, d.getProdukId());
                        ps2.setInt(3, d.getQty());
                        ps2.setDouble(4, d.getHargaSatuan());
                        ps2.setDouble(5, d.getSubtotal());
                        ps2.executeUpdate();
                    }
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

    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, p.nama AS nama_pelanggan, u.nama_lengkap AS nama_pengguna " +
                     "FROM transaksi t " +
                     "LEFT JOIN pelanggan p ON t.pelanggan_id = p.id " +
                     "LEFT JOIN pengguna u ON t.pengguna_id = u.id " +
                     "ORDER BY t.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setId(rs.getInt("id"));
                t.setNoTransaksi(rs.getString("no_transaksi"));
                t.setTanggal(rs.getString("tanggal"));
                t.setTipeTransaksi(rs.getString("tipe_transaksi"));
                t.setTotalHarga(rs.getDouble("total_harga"));
                t.setNamaPelanggan(rs.getString("nama_pelanggan"));
                t.setNamaPengguna(rs.getString("nama_pengguna"));
                t.setPelangganId(rs.getInt("pelanggan_id"));
                t.setPenggunaId(rs.getInt("pengguna_id"));
                t.setCreatedAt(rs.getString("created_at"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DetailTransaksi> getDetailByTransaksiId(int transaksiId) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT dt.*, p.nama AS nama_produk FROM detail_transaksi dt " +
                     "JOIN produk p ON dt.produk_id = p.id WHERE dt.transaksi_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaksiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setId(rs.getInt("id"));
                d.setTransaksiId(rs.getInt("transaksi_id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setNamaProduk(rs.getString("nama_produk"));
                d.setQty(rs.getInt("qty"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                d.setSubtotal(rs.getDouble("subtotal"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteTransaksi(int id) {
        String sqlDetail = "DELETE FROM detail_transaksi WHERE transaksi_id=?";
        String sqlTransaksi = "DELETE FROM transaksi WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete from child table first
                try (PreparedStatement ps1 = conn.prepareStatement(sqlDetail)) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }
                // Then parent
                try (PreparedStatement ps2 = conn.prepareStatement(sqlTransaksi)) {
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
}