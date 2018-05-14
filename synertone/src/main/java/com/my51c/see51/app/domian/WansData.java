package com.my51c.see51.app.domian;

public class WansData {

    public String enable;
    public String num;
    public String upspeed;
    public String downspeed;

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUpspeed() {
        return upspeed;
    }

    public void setUpspeed(String upspeed) {
        this.upspeed = upspeed;
    }

    public String getDownspeed() {
        return downspeed;
    }

    public void setDownspeed(String downspeed) {
        this.downspeed = downspeed;
    }

    @Override
    public String toString() {
        return "Wans [enable=" + enable + ", num=" + num + ", upspeed="
                + upspeed + ", downspeed=" + downspeed + "]";
    }

}
