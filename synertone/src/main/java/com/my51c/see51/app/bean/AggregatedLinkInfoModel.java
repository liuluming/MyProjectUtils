package com.my51c.see51.app.bean;

import java.util.List;

/**
 * Created by snt1231 on 2018/2/5.
 */

public class AggregatedLinkInfoModel extends BaseModel {


    /**
     * linkinfo : {"oId":"1","name":"bond0","mode":"0","channelList":[{"channelId":"1","channel":"tap0"},{"channelId":"2","channel":"tap0"},{"channelId":"3","channel":"tap0"}]}
     */

    private LinkinfoBean linkinfo;

    public LinkinfoBean getLinkinfo() {
        return linkinfo;
    }

    public void setLinkinfo(LinkinfoBean linkinfo) {
        this.linkinfo = linkinfo;
    }

    public static class LinkinfoBean {
        /**
         * oId : 1
         * name : bond0
         * mode : 0
         * channelList : [{"channelId":"1","channel":"tap0"},{"channelId":"2","channel":"tap0"},{"channelId":"3","channel":"tap0"}]
         */

        private String oId;
        private String name;
        private String mode;
        private List<ChannelListBean> channelList;

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

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public List<ChannelListBean> getChannelList() {
            return channelList;
        }

        public void setChannelList(List<ChannelListBean> channelList) {
            this.channelList = channelList;
        }

        public static class ChannelListBean {
            /**
             * channelId : 1
             * channel : tap0
             */

            private String channelId;
            private String channel;
            private boolean choose=false;

            public boolean isChoose() {
                return choose;
            }

            public void setChoose(boolean choose) {
                this.choose = choose;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }
        }
    }
}
