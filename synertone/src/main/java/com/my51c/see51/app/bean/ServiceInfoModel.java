package com.my51c.see51.app.bean;

import java.util.List;

/**
 * Created by snt1206 on 2018/2/5.
 */

public class ServiceInfoModel extends BaseModel{

    /**
     * serviceInfo : {"oId":"1","name":"voip","sourIP":"192.168.0.1","sourPort":"80","destIP":"192.168.0.2","destPort":"90","protocol":"1","priority":"0","linkType":"0","transferPolicyList":[{"linkId":"1","linkName":"bond2","weight":"15"}]}
     */

    private ServiceInfoBean serviceInfo;

    public ServiceInfoBean getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfoBean serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public static class ServiceInfoBean {
        /**
         * oId : 1
         * name : voip
         * sourIP : 192.168.0.1
         * sourPort : 80
         * destIP : 192.168.0.2
         * destPort : 90
         * protocol : 1
         * priority : 0
         * linkType : 0
         * transferPolicyList : [{"linkId":"1","linkName":"bond2","weight":"15"}]
         */

        private String oId;
        private String name;
        private String sourIP;
        private String sourPort;
        private String destIP;
        private String destPort;
        private String protocol;
        private String priority;
        private String linkType;
        private List<TransferPolicyListBean> transferPolicyList;

        public String getOId() {
            return oId;
        }

        public void setOId(String oId) {
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

        public String getLinkType() {
            return linkType;
        }

        public void setLinkType(String linkType) {
            this.linkType = linkType;
        }

        public List<TransferPolicyListBean> getTransferPolicyList() {
            return transferPolicyList;
        }

        public void setTransferPolicyList(List<TransferPolicyListBean> transferPolicyList) {
            this.transferPolicyList = transferPolicyList;
        }

        public static class TransferPolicyListBean {
            /**
             * linkId : 1
             * linkName : bond2
             * weight : 15
             */

            private String linkId;
            private String linkName;
            private String weight;
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
