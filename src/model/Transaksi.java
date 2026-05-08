// src/model/Transaksi.java
package model;

import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int id;
    private String noTransaksi;
    private String tanggal;
    private String tipeTransaksi;
    private double totalHarga;
    private String createdAt;
    private int pelangganId;
    private String namaPelanggan;
    private int penggunaId;
    private String namaPengguna;

    private List<DetailTransaksi> detailList = new ArrayList<>();

    public Transaksi() {}

    public void tambahDetail(DetailTransaksi detail) {
        detailList.add(detail);
        hitungTotal();
    }

    public double hitungTotal() {
        totalHarga = 0;
        for (DetailTransaksi d : detailList) {
            totalHarga += d.hitungSubtotal();
        }
        return totalHarga;
    }

    public void cetakStruk() {
        System.out.println("No Transaksi: " + noTransaksi);
        System.out.println("Tanggal: " + tanggal);
        System.out.println("Total: " + totalHarga);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNoTransaksi() { return noTransaksi; }
    public void setNoTransaksi(String noTransaksi) { this.noTransaksi = noTransaksi; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public String getTipeTransaksi() { return tipeTransaksi; }
    public void setTipeTransaksi(String tipeTransaksi) { this.tipeTransaksi = tipeTransaksi; }
    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public int getPelangganId() { return pelangganId; }
    public void setPelangganId(int pelangganId) { this.pelangganId = pelangganId; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
    public int getPenggunaId() { return penggunaId; }
    public void setPenggunaId(int penggunaId) { this.penggunaId = penggunaId; }
    public String getNamaPengguna() { return namaPengguna; }
    public void setNamaPengguna(String namaPengguna) { this.namaPengguna = namaPengguna; }
    public List<DetailTransaksi> getDetailList() { return detailList; }
    public void setDetailList(List<DetailTransaksi> detailList) { this.detailList = detailList; }
}