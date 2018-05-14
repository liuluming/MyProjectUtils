package com.my51c.see51.app.bean;

/**
 * Created by snt1231 on 2018/1/24.
 */

public class StaticIPModel extends AdapterModel {
    private String ip=""; //ip地址
    private String gateway="";//网关地址
    private String mask=""; //子网掩码
    private String dns1="";//DNS1服务器
    private String dns2="";//DNS2服务器
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }
}
