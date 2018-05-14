package com.my51c.see51.app.domian;

public class BwProjectNum {

    public String name;//名字
    public String srcip;//源ip地址
    public String srcport;//源端口号
    public String desip;//目标ip地址
    public String desport; //目标地址
    public int pro;//协议 0 all  1 tcp 2 udp 3 icmp
    public String policy; //策略
    public boolean bwProjectTag;

    public boolean isBwProjectTag() {
        return bwProjectTag;
    }

    public void setBwProjectTag(boolean bwProjectTag) {
        this.bwProjectTag = bwProjectTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrcip() {
        return srcip;
    }

    public void setSrcip(String srcip) {
        this.srcip = srcip;
    }

    public String getSrcport() {
        return srcport;
    }

    public void setSrcport(String srcport) {
        this.srcport = srcport;
    }

    public String getDesip() {
        return desip;
    }

    public void setDesip(String desip) {
        this.desip = desip;
    }

    public String getDesport() {
        return desport;
    }

    public void setDesport(String desport) {
        this.desport = desport;
    }

    public int getPro() {
        return pro;
    }

    public void setPro(int pro) {
        this.pro = pro;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

}
