// src/model/DetailTransaksi.java
package model;

public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int produkId;
    private String namaProduk;
    private int qty;
    private double hargaSatuan;
    private double subtotal;

    public DetailTransaksi() {}

    public double hitungSubtotal() {
        subtotal = qty * hargaSatuan;
        return subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTransaksiId() { return transaksiId; }
    public void setTransaksiId(int transaksiId) { this.transaksiId = transaksiId; }
    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}