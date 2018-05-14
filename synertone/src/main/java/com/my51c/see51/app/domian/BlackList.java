package com.my51c.see51.app.domian;

public class BlackList {

    public String enable;
    public String ip;
    public String mac;
    public String user;
    public boolean blackListTag;
    public int blackDelTag;

    public int getBlackDelTag() {
        return blackDelTag;
    }

    public void setBlackDelTag(int blackDelTag) {
        this.blackDelTag = blackDelTag;
    }

    public boolean isBlackListTag() {
        return blackListTag;
    }

    public void setBlackListTag(boolean blackListTag) {
        this.blackListTag = blackListTag;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
