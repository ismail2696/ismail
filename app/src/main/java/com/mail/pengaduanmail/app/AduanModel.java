package com.mail.pengaduanmail.app;

/**
 * Created by fandis on 02/02/2018.
 */

public class AduanModel {
    private String id;
    private String perihal;
    private String deskripsi;
    private Double latt;
    private Double longi;
    private String gambar;
    private String judul;

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerihal() {
        return perihal;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Double getLatt() {
        return latt;
    }

    public void setLatt(Double latt) {
        this.latt = latt;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
