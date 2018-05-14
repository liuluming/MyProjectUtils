package com.my51c.see51.app.bean;

/**
 * Created by snt1231 on 2018/1/19.
 */

public class NetAdapterStateModel extends BaseModel{


    /**
     * statusInfo : {"oId":"1","adapterName":"wan1","linkStatus":"0","recv":"123","tran":"123","ip":"192.168.0.88","gateway":"192.168.0.1","mask":"255.255.255.255","dns1":"8.8.8.8","dns2":"114.114.114.114","mac":"12:34:56:78:90","simInfo":{"sim":"0","mno":"0","signal":"10","standard":"0","msisdn":"12345678900"}}
     */

    private StatusInfoBean statusInfo;

    public StatusInfoBean getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfoBean statusInfo) {
        this.statusInfo = statusInfo;
    }

    public static class StatusInfoBean {
        /**
         * oId : 1
         * adapterName : wan1
         * linkStatus : 0
         * recv : 123
         * tran : 123
         * ip : 192.168.0.88
         * gateway : 192.168.0.1
         * mask : 255.255.255.255
         * dns1 : 8.8.8.8
         * dns2 : 114.114.114.114
         * mac : 12:34:56:78:90
         * simInfo : {"sim":"0","mno":"0","signal":"10","standard":"0","msisdn":"12345678900"}
         */

        private String oId;
        private String adapterName;
        private String linkStatus;
        private String recv;
        private String tran;
        private String ip;
        private String gateway;
        private String mask;
        private String dns1;
        private String dns2;
        private String mac;
        private SimInfoBean simInfo;

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

        public String getLinkStatus() {
            return linkStatus;
        }

        public void setLinkStatus(String linkStatus) {
            this.linkStatus = linkStatus;
        }

        public String getRecv() {
            return recv;
        }

        public void setRecv(String recv) {
            this.recv = recv;
        }

        public String getTran() {
            return tran;
        }

        public void setTran(String tran) {
            this.tran = tran;
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

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public SimInfoBean getSimInfo() {
            return simInfo;
        }

        public void setSimInfo(SimInfoBean simInfo) {
            this.simInfo = simInfo;
        }

        public static class SimInfoBean {
            /**
             * sim : 0
             * mno : 0
             * signal : 10
             * standard : 0
             * msisdn : 12345678900
             */

            private String sim;
            private String mno;
            private String signal;
            private String standard;
            private String msisdn;

            public String getSim() {
                return sim;
            }

            public void setSim(String sim) {
                this.sim = sim;
            }

            public String getMno() {
                return mno;
            }

            public void setMno(String mno) {
                this.mno = mno;
            }

            public String getSignal() {
                return signal;
            }

            public void setSignal(String signal) {
                this.signal = signal;
            }

            public String getStandard() {
                return standard;
            }

            public void setStandard(String standard) {
                this.standard = standard;
            }

            public String getMsisdn() {
                return msisdn;
            }

            public void setMsisdn(String msisdn) {
                this.msisdn = msisdn;
            }
        }
    }
}
