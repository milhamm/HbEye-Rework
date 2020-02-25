package com.imvlabs.hbey.Entities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kautsar Fadly F on 11/09/2017.
 */

public class Recent extends RealmObject {
    @PrimaryKey
    private int id;
    private String nama;
    private boolean kelamin;
    private int umur;
    private byte[] gambar;
    private double hb;
    private Date waktu;
    private String status;

    public Recent(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public boolean isKelamin() {
        return kelamin;
    }

    public void setKelamin(boolean kelamin) {
        this.kelamin = kelamin;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public byte[] getGambar() {
        return gambar;
    }

    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }

    public double getHb() {
        return hb;
    }

    public void setHb(double hb) {
        this.hb = hb;
    }

    public Date getWaktu() {
        return waktu;
    }

    public void setWaktu(Date waktu) {
        this.waktu = waktu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Recent(int id, String nama, boolean kelamin, int umur, byte[] gambar, double hb, Date waktu, String status) {
        this.id = id;
        this.nama = nama;
        this.kelamin = kelamin;
        this.umur = umur;
        this.gambar = gambar;
        this.hb = hb;
        this.waktu = waktu;
        this.status = status;
    }
}
