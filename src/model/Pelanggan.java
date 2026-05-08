// src/model/Pelanggan.java
package model;

public class Pelanggan {
    private int id;
    private String nama;
    private String noTelp;
    private String alamat;
    private String noPlat;
    private String createdAt;

    public Pelanggan() {}

    public String getDataPelanggan() {
        return nama + " - " + noPlat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNoPlat() { return noPlat; }
    public void setNoPlat(String noPlat) { this.noPlat = noPlat; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return nama + " [" + noPlat + "]"; }
}