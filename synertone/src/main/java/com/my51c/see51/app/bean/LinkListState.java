package com.my51c.see51.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by snt1231 on 2018/1/12.
 */

public class LinkListState extends BaseModel{

    private List<LinkListBean> linkList;

    public List<LinkListBean> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<LinkListBean> linkList) {
        this.linkList = linkList;
    }

    public static class LinkListBean implements Parcelable{
        /**
         * oId : 2
         * name : bond1
         * mode : 1
         * recv : 123
         * tran : 123
         * channelList : [{"oId":"3","channel":"tap2","linkStatus":"0","adapterId":"3","adapterName":"wan3","simInfo":{"sim":"0","mno":"0","signal":"0","standard":"0"}}]
         */

        private String oId;
        private String name;
        private String mode;
        private String recv;
        private String tran;
        private List<ChannelListBean> channelList;

        protected LinkListBean(Parcel in) {
            oId = in.readString();
            name = in.readString();
            mode = in.readString();
            recv = in.readString();
            tran = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(oId);
            dest.writeString(name);
            dest.writeString(mode);
            dest.writeString(recv);
            dest.writeString(tran);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<LinkListBean> CREATOR = new Creator<LinkListBean>() {
            @Override
            public LinkListBean createFromParcel(Parcel in) {
                return new LinkListBean(in);
            }

            @Override
            public LinkListBean[] newArray(int size) {
                return new LinkListBean[size];
            }
        };

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

        public List<ChannelListBean> getChannelList() {
            return channelList;
        }

        public void setChannelList(List<ChannelListBean> channelList) {
            this.channelList = channelList;
        }

        public static class ChannelListBean {
            /**
             * oId : 3
             * channel : tap2
             * linkStatus : 0
             * adapterId : 3
             * adapterName : wan3
             * simInfo : {"sim":"0","mno":"0","signal":"0","standard":"0"}
             */

            private String oId;
            private String channel;
            private String linkStatus;
            private String adapterId;
            private String adapterName;
            private SimInfoBean simInfo;

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

            public String getLinkStatus() {
                return linkStatus;
            }

            public void setLinkStatus(String linkStatus) {
                this.linkStatus = linkStatus;
            }

            public String getAdapterId() {
                return adapterId;
            }

            public void setAdapterId(String adapterId) {
                this.adapterId = adapterId;
            }

            public String getAdapterName() {
                return adapterName;
            }

            public void setAdapterName(String adapterName) {
                this.adapterName = adapterName;
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
                 * signal : 0
                 * standard : 0
                 */

                private String sim;
                private String mno;
                private String signal;
                private String standard;

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
            }
        }
    }
}
