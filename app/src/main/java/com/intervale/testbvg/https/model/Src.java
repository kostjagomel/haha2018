package com.intervale.testbvg.https.model;

public class Src {
    String type="card";
    String pan = "5666666666666666";
    String expiry = "1119";
    String csc = "123";

    public Src(){};

    public Src(String type, String pan, String expiry, String csc) {
        this.type = type;
        this.pan = pan;
        this.expiry = expiry;
        this.csc = csc;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCsc() {
        return csc;
    }

    public void setCsc(String csc) {
        this.csc = csc;
    }
}
