// src/dao/LaporanDAO.java
package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LaporanDAO {

    public List<Object[]> getLaporanTransaksi(String periodeAwal, String periodeAkhir, String tipe) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.no_transaksi, t.tanggal, t.tipe_transaksi, " +
                     "COALESCE(p.nama, '-') AS pelanggan, t.total_harga, u.nama_lengkap " +
                     "FROM transaksi t " +
                     "LEFT JOIN pelanggan p ON t.pelanggan_id = p.id " +
                     "LEFT JOIN pengguna u ON t.pengguna_id = u.id " +
                     "WHERE t.tanggal BETWEEN ? AND ? ";
        if (!tipe.equals("Semua")) {
            sql += "AND t.tipe_transaksi = ? ";
        }
        sql += "ORDER BY t.tanggal DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periodeAwal);
            ps.setString(2, periodeAkhir);
            if (!tipe.equals("Semua")) {
                ps.setString(3, tipe);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("no_transaksi"),
                    rs.getString("tanggal"),
                    rs.getString("tipe_transaksi"),
                    rs.getString("pelanggan"),
                    rs.getDouble("total_harga"),
                    rs.getString("nama_lengkap")
                };
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalPendapatan(String periodeAwal, String periodeAkhir, String tipe) {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) AS total FROM transaksi " +
                     "WHERE tanggal BETWEEN ? AND ? ";
        if (!tipe.equals("Semua")) {
            sql += "AND tipe_transaksi = ? ";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periodeAwal);
            ps.setString(2, periodeAkhir);
            if (!tipe.equals("Semua")) {
                ps.setString(3, tipe);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Statistik bulanan
    public Map<String, Double> getStatistikBulanan(int tahun, String tipe) {

        Map<String, Double> map = new LinkedHashMap<>();
        String[] bulanNama = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                              "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        for (String b : bulanNama) {
            map.put(b, 0.0);
        }

        String sql = "SELECT MONTH(tanggal) AS bulan, SUM(total_harga) AS total " +
                     "FROM transaksi WHERE YEAR(tanggal) = ? ";
        if (!tipe.equals("Semua")) {
            sql += "AND tipe_transaksi = ? ";
        }
        sql += "GROUP BY MONTH(tanggal) ORDER BY MONTH(tanggal)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tahun);
            if (!tipe.equals("Semua")) {
                ps.setString(2, tipe);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int bulan = rs.getInt("bulan");
                double total = rs.getDouble("total");
                map.put(bulanNama[bulan - 1], total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Statistik tahunan (12 tahun terakhir, berbasis tahun akhir)
    public Map<String, Double> getStatistikTahunan(int tahunAkhir, String tipe) {
        Map<String, Double> map = new LinkedHashMap<>();

        int tahunAwal = tahunAkhir - 11;
        for (int t = tahunAwal; t <= tahunAkhir; t++) {
            map.put(String.valueOf(t), 0.0);
        }

        String sql = "SELECT YEAR(tanggal) AS tahun, SUM(total_harga) AS total " +
                     "FROM transaksi " +
                     "WHERE YEAR(tanggal) BETWEEN ? AND ? ";
        if (!tipe.equals("Semua")) {
            sql += "AND tipe_transaksi = ? ";
        }
        sql += "GROUP BY YEAR(tanggal) ORDER BY YEAR(tanggal)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tahunAwal);
            ps.setInt(2, tahunAkhir);
            if (!tipe.equals("Semua")) {
                ps.setString(3, tipe);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int tahun = rs.getInt("tahun");
                double total = rs.getDouble("total");
                map.put(String.valueOf(tahun), total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public int getJumlahTransaksi(String periodeAwal, String periodeAkhir) {
        String sql = "SELECT COUNT(*) AS jml FROM transaksi WHERE tanggal BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periodeAwal);
            ps.setString(2, periodeAkhir);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("jml");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Dashboard stats
    public int getTotalTransaksiHariIni() {
        String sql = "SELECT COUNT(*) AS jml FROM transaksi WHERE tanggal = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("jml");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getPendapatanHariIni() {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) AS total FROM transaksi WHERE tanggal = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getPendapatanBulanIni() {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) AS total FROM transaksi " +
                     "WHERE MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalPelanggan() {
        String sql = "SELECT COUNT(*) AS jml FROM pelanggan";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("jml");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Object[]> getProfitPerProduk(String periodeAwal, String periodeAkhir) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT 
                p.nama AS nama_produk,
                p.kode,
                COALESCE(SUM((dt.harga_satuan - COALESCE(b.harga_beli, 0)) * dt.qty), 0) AS total_profit,
                COALESCE(SUM(dt.qty), 0) AS total_qty,
                COUNT(DISTINCT t.id) AS jumlah_transaksi
            FROM detail_transaksi dt
            JOIN transaksi t ON dt.transaksi_id = t.id
            JOIN produk p ON dt.produk_id = p.id
            LEFT JOIN barang b ON p.id = b.id
            WHERE t.tanggal BETWEEN ? AND ?
            GROUP BY p.id, p.nama, p.kode
            ORDER BY total_profit DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periodeAwal);
            ps.setString(2, periodeAkhir);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("nama_produk"),
                    rs.getString("kode"),
                    rs.getDouble("total_profit"),
                    rs.getInt("total_qty"),
                    rs.getInt("jumlah_transaksi")
                };
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
