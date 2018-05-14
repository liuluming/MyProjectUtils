package com.my51c.see51.app.bean;

import org.litepal.crud.DataSupport;

public class StarCodeModel extends DataSupport {

    /**
     * satenum : 1
     * satename : 协同一号
     * satelon : 115.5
     * mode : 1
     * freq : 1156.58
     * zfreq : 11056.58
     * bw : 3
     * type : 1
     */
    private long id;
    private String sessionToken;
    private String satenum;
    private String satename;
    private String satelon;
    private String mode;//0 信标 1载波自动 载波手动 2DVB
    private String freq;//信标频率
    private String zfreq;//载波频率
    private String bw;
    private String type;
    private String centerFreq;//中心频率
    private String signRate;//符号率
    private boolean isAdd = false;

    public String getCenterFreq() {
        return centerFreq;
    }

    public void setCenterFreq(String centerFreq) {
        this.centerFreq = centerFreq;
    }

    public String getSignRate() {
        return signRate;
    }

    public void setSignRate(String signRate) {
        this.signRate = signRate;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public String getSatenum() {
        return satenum;
    }

    public void setSatenum(String satenum) {
        this.satenum = satenum;
    }

    public String getSatename() {
        return satename;
    }

    public void setSatename(String satename) {
        this.satename = satename;
    }

    public String getSatelon() {
        return satelon;
    }

    public void setSatelon(String satelon) {
        this.satelon = satelon;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getZfreq() {
        return zfreq;
    }

    public void setZfreq(String zfreq) {
        this.zfreq = zfreq;
    }

    public String getBw() {
        return bw;
    }

    public void setBw(String bw) {
        this.bw = bw;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

