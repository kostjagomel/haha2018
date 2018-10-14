package com.intervale.testbvg.https.model;

public class Params {

    String srcName = "dasdasd";
    String srcLastname = "sadasdasd";
    String dstName = "sdasdasdasd";
    String dstLastname = "sadasdasd";

    public Params() {
    }

    public Params(String srcName, String srcLastname, String dstName, String dstLastname) {
        this.srcName = srcName;
        this.srcLastname = srcLastname;
        this.dstName = dstName;
        this.dstLastname = dstLastname;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getSrcLastname() {
        return srcLastname;
    }

    public void setSrcLastname(String srcLastname) {
        this.srcLastname = srcLastname;
    }

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public String getDstLastname() {
        return dstLastname;
    }

    public void setDstLastname(String dstLastname) {
        this.dstLastname = dstLastname;
    }
}
