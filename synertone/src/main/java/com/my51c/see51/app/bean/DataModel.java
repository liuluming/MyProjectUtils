package com.my51c.see51.app.bean;

import java.util.List;

/*{"leds":{"power":1,"satnet":1,"satlink":1,"tx":1,"rx":0},
    "qstatus":0,"rx":14.1,"maxrx":15.2,"tx":76.9,"maxtx":78.8,
	"lanports":[{"port":1,"state":1,"speed":1,"mode":1,"vmode":0},
               {"port":2,"state":2,"speed":0,"mode":0,"vmode":0},
               {"port":3,"state":2,"speed":0,"mode":0,"vmode":0},
               {"port":4,"state":2,"speed":0,"mode":0,"vmode":0}]}*/
public class DataModel {
    LedModel leds;
    String qstatus;
    String rx;
    String maxrx;
    String tx;
    String maxtx;
    List<LanPorts> lanports;

    public LedModel getLeds() {
        return leds;
    }

    public void setLeds(LedModel leds) {
        this.leds = leds;
    }

    public String getQstatus() {
        return qstatus;
    }

    public void setQstatus(String qstatus) {
        this.qstatus = qstatus;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getMaxrx() {
        return maxrx;
    }

    public void setMaxrx(String maxrx) {
        this.maxrx = maxrx;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public String getMaxtx() {
        return maxtx;
    }

    public void setMaxtx(String maxtx) {
        this.maxtx = maxtx;
    }

    public List<LanPorts> getLanports() {
        return lanports;
    }

    public void setLanports(List<LanPorts> lanports) {
        this.lanports = lanports;
    }


}
