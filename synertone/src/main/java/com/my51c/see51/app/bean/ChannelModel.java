package com.my51c.see51.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by snt1206 on 2018/1/13.
 */
public class ChannelModel extends BaseModel{


    private List<ChannelListBean> channelList;

    public List<ChannelListBean> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<ChannelListBean> channelList) {
        this.channelList = channelList;
    }

    public static class ChannelListBean implements Parcelable{
        /**
         * oId : 1
         * channel : tap0
         * encrypt : 0
         * compress : 0
         * adapter : 1
         * adapterName : wan1
         * recv : 123
         * tran : 123
         * status : 0
         */

        private String oId;
        private String channel;
        private String encrypt;
        private String compress;
        private String adapter;
        private String adapterName;
        private String recv;
        private String tran;
        private String status;

        public ChannelListBean(Parcel in) {
            oId = in.readString();
            channel = in.readString();
            encrypt = in.readString();
            compress = in.readString();
            adapter = in.readString();
            adapterName = in.readString();
            recv = in.readString();
            tran = in.readString();
            status = in.readString();
        }

        public static final Creator<ChannelListBean> CREATOR = new Creator<ChannelListBean>() {
            @Override
            public ChannelListBean createFromParcel(Parcel in) {
                return new ChannelListBean(in);
            }

            @Override
            public ChannelListBean[] newArray(int size) {
                return new ChannelListBean[size];
            }
        };

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(oId);
            dest.writeString(channel);
            dest.writeString(encrypt);
            dest.writeString(compress);
            dest.writeString(adapter);
            dest.writeString(adapterName);
            dest.writeString(recv);
            dest.writeString(tran);
            dest.writeString(status);
        }
    }
}
