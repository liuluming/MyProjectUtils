package com.my51c.see51.app.bean;

/**
 * Created by snt1206 on 2018/2/5.
 */

public class ChannelInfoModel extends BaseModel{

    /**
     * channelInfo : {"oId":"1","channel":"tap0","ip":"192.168.0.1","port":"80","username":"admin","password":"123456","encrypt":"0","compress":"0","adapter":"1","adapterName":"wan1"}
     */

    private ChannelInfoBean channelInfo;

    public ChannelInfoBean getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfoBean channelInfo) {
        this.channelInfo = channelInfo;
    }

    public static class ChannelInfoBean {
        /**
         * oId : 1
         * channel : tap0
         * ip : 192.168.0.1
         * port : 80
         * username : admin
         * password : 123456
         * encrypt : 0
         * compress : 0
         * adapter : 1
         * adapterName : wan1
         */

        private String oId;
        private String channel;
        private String ip;
        private String port;
        private String username;
        private String password;
        private String encrypt;
        private String compress;
        private String adapter;
        private String adapterName;

        public String getOId() {
            return oId;
        }

        public void setOId(String oId) {
            this.oId = oId;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
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

        public String getEncrypt() {
            return encrypt;
        }

        public void setEncrypt(String encrypt) {
            this.encrypt = encrypt;
        }

        public String getCompress() {
            return compress;
        }

        public void setCompress(String compress) {
            this.compress = compress;
        }

        public String getAdapter() {
            return adapter;
        }

        public void setAdapter(String adapter) {
            this.adapter = adapter;
        }

        public String getAdapterName() {
            return adapterName;
        }

        public void setAdapterName(String adapterName) {
            this.adapterName = adapterName;
        }
    }
}
