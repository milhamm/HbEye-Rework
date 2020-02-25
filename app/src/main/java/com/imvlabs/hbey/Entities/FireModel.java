package com.imvlabs.hbey.Entities;

/**
 * Created by Kautsar Fadly F on 19/09/2017.
 */

public class FireModel {
    private String nama,umur,kelamin,hbLevel,waktu,status,longitude,latitude;

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
    }

    public String getHbLevel() {
        return hbLevel;
    }

    public void setHbLevel(String hbLevel) {
        this.hbLevel = hbLevel;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUmur() {
        return umur;
    }

    public void setUmur(String umur) {
        this.umur = umur;
    }

    public FireModel(String nama, String umur, String kelamin, String hbLevel, String waktu, String status, String longitude, String latitude){
        this.nama = nama;
        this.umur = umur;
        this.kelamin = kelamin;
        this.hbLevel = hbLevel;
        this.waktu = waktu;
        this.status = status;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public FireModel(){
    }
}
