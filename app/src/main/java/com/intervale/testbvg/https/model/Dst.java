package com.intervale.testbvg.https.model;

public class Dst {
    String pan="5454545454545454";
    String type = "card";

    public Dst() {
    }

    public Dst(String pan, String type) {
        this.pan = pan;
        this.type = type;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
