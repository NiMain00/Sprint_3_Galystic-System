// src/model/Produk.java
package model;

public class Produk {
    protected int id;
    protected String kode;
    protected String nama;
    protected double harga;
    protected String createdAt;

    public Produk() {}

    public Produk(int id, String kode, String nama, double harga) {
        this.id = id;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return kode + " - " + nama; }
}