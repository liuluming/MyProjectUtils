package com.my51c.see51.app.domian;

public class InteferNum {

    public String name;
    public int ipssum;
    public String ip1;
    public String ip2;
    public String ip3;
    public String ip4;
    public String ip5;
    public String ip6;
    public String ipsnum;
    public String count;
    public String timeout;
    public String interval;
    public String lost;
    public String connect;
    public boolean interferTag;

    public InteferNum() {
        super();
    }

    public InteferNum(String name, int ipssum, String ip1, String ip2,
                      String ip3, String ipsnum, String count, String timeout,
                      String interval, String lost, String connect) {
        super();
        this.name = name;
        this.ipssum = ipssum;
        this.ip1 = ip1;
        this.ip2 = ip2;
        this.ip3 = ip3;
        this.ipsnum = ipsnum;
        this.count = count;
        this.timeout = timeout;
        this.interval = interval;
        this.lost = lost;
        this.connect = connect;
    }

    public boolean isInterferTag() {
        return interferTag;
    }

    public void setInterferTag(boolean interferTag) {
        this.interferTag = interferTag;
    }

    public String getIp6() {
        return ip6;
    }

    public void setIp6(String string) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIpssum() {
        return ipssum;
    }

    public void setIpssum(int ipssum) {
        this.ipssum = ipssum;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getIp3() {
        return ip3;
    }

    public void setIp3(String ip3) {
        this.ip3 = ip3;
    }

    public String getIpsnum() {
        return ipsnum;
    }

    public void setIpsnum(String ipsnum) {
        this.ipsnum = ipsnum;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getLost() {
        return lost;
    }

    public void setLost(String lost) {
        this.lost = lost;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }

    public String getIp5() {
        return ip5;
    }

    public void setIp5(String ip5) {
        this.ip5 = ip5;
    }


}
