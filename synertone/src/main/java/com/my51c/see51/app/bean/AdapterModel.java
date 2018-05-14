package com.my51c.see51.app.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by snt1231 on 2018/1/25.
 */

public class AdapterModel {
    private String oId="";
    private String adapterName="";
    private String protocol="";//1 DHCP ;2 静态IP;3 PPPOE
    private String type=""; //网口类型 0 以太网口;1  移动电话网络
    @SerializedName("interface")
    private String interfaceX="";

    public String getOId() {
        return oId;
    }

    public void setOId(String oId) {
        this.oId = oId;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterfaceX() {
        return interfaceX;
    }

    public void setInterfaceX(String interfaceX) {
        this.interfaceX = interfaceX;
    }
}
