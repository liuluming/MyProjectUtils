package com.my51c.see51.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by snt1231 on 2018/1/17.
 */

public class NetAdapterModel extends BaseModel{


    /**
     * adapterList : [{"oId":"1","adapterName":"wan1","status":"0","enable":"0","recv":"123","tran":"123"},{"oId":"2","adapterName":"wan2","status":"0","enable":"0","recv":"123","tran":"123"}]
     * code : 0
     * msg : return_msg
     */
    private List<AdapterListBean> adapterList;

    public List<AdapterListBean> getAdapterList() {
        return adapterList;
    }

    public void setAdapterList(List<AdapterListBean> adapterList) {
        this.adapterList = adapterList;
    }

    public static class AdapterListBean implements Parcelable{
        /**
         * oId : 1
         * adapterName : wan1
         * status : 0
         * enable : 0
         * recv : 123
         * tran : 123
         */

        private String oId;
        private String adapterName;
        private String status;
        private String enable;
        private String recv;
        private String tran;

        protected AdapterListBean(Parcel in) {
            oId = in.readString();
            adapterName = in.readString();
            status = in.readString();
            enable = in.readString();
            recv = in.readString();
            tran = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(oId);
            dest.writeString(adapterName);
            dest.writeString(status);
            dest.writeString(enable);
            dest.writeString(recv);
            dest.writeString(tran);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<AdapterListBean> CREATOR = new Creator<AdapterListBean>() {
            @Override
            public AdapterListBean createFromParcel(Parcel in) {
                return new AdapterListBean(in);
            }

            @Override
            public AdapterListBean[] newArray(int size) {
                return new AdapterListBean[size];
            }
        };

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEnable() {
            return enable;
        }

        public void setEnable(String enable) {
            this.enable = enable;
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
    }
}
