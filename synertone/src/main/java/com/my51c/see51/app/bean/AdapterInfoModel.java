package com.my51c.see51.app.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by snt1231 on 2018/2/5.
 */

public class AdapterInfoModel extends BaseModel{

    /**
     * adapterInfo : {"oId":"1","adapterName":"wan1","protocol":"1","interface":"wan","type":"0","ip":"192.168.0.88","gateway":"192.168.0.1","mask":"255.255.255.255","dns1":"8.8.8.8","dns2":"114.114.114.114","username":"admin","password":"123456","apn":"apn"}
     */

    private AdapterInfoBean adapterInfo;

    public AdapterInfoBean getAdapterInfo() {
        return adapterInfo;
    }

    public void setAdapterInfo(AdapterInfoBean adapterInfo) {
        this.adapterInfo = adapterInfo;
    }

    public static class AdapterInfoBean {
        /**
         * oId : 1
         * adapterName : wan1
         * protocol : 1
         * interface : wan
         * type : 0
         * ip : 192.168.0.88
         * gateway : 192.168.0.1
         * mask : 255.255.255.255
         * dns1 : 8.8.8.8
         * dns2 : 114.114.114.114
         * username : admin
         * password : 123456
         * apn : apn
         */

        private String oId;
        private String adapterName;
        private String protocol;
        @SerializedName("interface")
        private String interfaceX;
        private String type;
        private String ip;
        private String gateway;
        private String mask;
        private String dns1;
        private String dns2;
        private String username;
        private String password;
        private String apn;

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

        public String getInterfaceX() {
            return interfaceX;
        }

        public void setInterfaceX(String interfaceX) {
            this.interfaceX = interfaceX;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getApn() {
            return apn;
        }

        public void setApn(String apn) {
            this.apn = apn;
        }
    }
}
