package com.imvlabs.hbey.Entities;


import java.util.Date;

import io.realm.RealmObject;

/**
 * A model for result to store in DB
 */
public class ResultData extends RealmObject {

    private byte[] picture;
    private double hbLevel;
    private Date taken_date;

    public ResultData() {
    }

    public ResultData(byte[] picture, double hbLevel) {
        this.taken_date = new Date();
        this.picture = picture;
        this.hbLevel = hbLevel;
    }

    public Date getTaken_date() {
        return taken_date;
    }

    public byte[] getPicture() {
        return picture;
    }

    public double getHbLevel() {
        return hbLevel;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public void setHbLevel(double hbLevel) {
        this.hbLevel = hbLevel;
    }

    public void setTaken_date(Date taken_date) {
        this.taken_date = taken_date;
    }
}