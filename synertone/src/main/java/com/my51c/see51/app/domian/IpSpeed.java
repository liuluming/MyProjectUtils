package com.my51c.see51.app.domian;

public class IpSpeed {

    public String enable;
    public String ip;
    public String downmax;
    public String upmax;
    public int prior;
    public String user;
    public boolean ipSpeedTag;//表示原有的设置标签
    public int ipSpeedDelTag;
    public boolean ipAddSpeedTag;//表示新增的设置标签

    public boolean isIpAddSpeedTag() {
        return ipAddSpeedTag;
    }

    public void setIpAddSpeedTag(boolean ipAddSpeedTag) {
        this.ipAddSpeedTag = ipAddSpeedTag;
    }


    public int getIpSpeedDelTag() {
        return ipSpeedDelTag;
    }

    public void setIpSpeedDelTag(int ipSpeedDelTag) {
        this.ipSpeedDelTag = ipSpeedDelTag;
    }

    public boolean isIpSpeedTag() {
        return ipSpeedTag;
    }

    public void setIpSpeedTag(boolean ipSpeedTag) {
        this.ipSpeedTag = ipSpeedTag;
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

    public String getDownmax() {
        return downmax;
    }

    public void setDownmax(String downmax) {
        this.downmax = downmax;
    }

    public String getUpmax() {
        return upmax;
    }

    public void setUpmax(String upmax) {
        this.upmax = upmax;
    }

    public int getPrior() {
        return prior;
    }

    public void setPrior(int prior) {
        this.prior = prior;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
