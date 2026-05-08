// src/model/KategoriBarang.java
package model;

public class KategoriBarang {
    private int id;
    private String namaKategori;

    public KategoriBarang() {}

    public KategoriBarang(int id, String namaKategori) {
        this.id = id;
        this.namaKategori = namaKategori;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    @Override
    public String toString() { return namaKategori; }
}