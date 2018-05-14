package com.my51c.see51.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by snt1206 on 2018/1/13.
 */

public class QueryServiceModel extends BaseModel{

    /**
     * code : 0
     * msg : return_msg
     * serviceList : [{"oId":"1","name":"AAA","sourIP":"192.168.0.1","sourPort":"80","destIP":"192.168.0.2","destPort":"90","protocol":"1","priority":"0","transferPolicyList":[{"linkName":"AAA","weight":"0"},{"linkName":"BBB","weight":"0"},{"linkName":"CCC","weight":"0"}]}]
     */
    private List<ServiceListBean> serviceList;

    public List<ServiceListBean> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceListBean> serviceList) {
        this.serviceList = serviceList;
    }

    public static class ServiceListBean implements Serializable{
        /**
         * oId : 1
         * name : AAA
         * sourIP : 192.168.0.1
         * sourPort : 80
         * destIP : 192.168.0.2
         * destPort : 90
         * protocol : 1
         * priority : 0
         * transferPolicyList : [{"linkName":"AAA","weight":"0"},{"linkName":"BBB","weight":"0"},{"linkName":"CCC","weight":"0"}]
         */

        private String oId="";
        private String name="";
        private String sourIP="";
        private String sourPort="";
        private String destIP="";
        private String destPort="";
        private String protocol="";
        private String priority="";

        public String getOId() {
            return oId;
        }

        public void setOId(String oId) {
            this.oId = oId;
        }

        public String getoId() {
            return oId;
        }

        public void setoId(String oId) {
            this.oId = oId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSourIP() {
            return sourIP;
        }

        public void setSourIP(String sourIP) {
            this.sourIP = sourIP;
        }

        public String getSourPort() {
            return sourPort;
        }

        public void setSourPort(String sourPort) {
            this.sourPort = sourPort;
        }

        public String getDestIP() {
            return destIP;
        }

        public void setDestIP(String destIP) {
            this.destIP = destIP;
        }

        public String getDestPort() {
            return destPort;
        }

        public void setDestPort(String destPort) {
            this.destPort = destPort;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public static class TransferPolicyListBean {
            /**
             * linkName : AAA
             * weight : 0
             */

            private String linkName="";
            private String linkId="";
            private String weight="";
            private boolean choose=false;

            public String getLinkId() {
                return linkId;
            }

            public void setLinkId(String linkId) {
                this.linkId = linkId;
            }

            public String getLinkName() {
                return linkName;
            }

            public void setLinkName(String linkName) {
                this.linkName = linkName;
            }

            public String getWeight() {
                return weight;
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public boolean isChoose() {
                return choose;
            }

            public void setChoose(boolean choose) {
                this.choose = choose;
            }
        }
    }
}
