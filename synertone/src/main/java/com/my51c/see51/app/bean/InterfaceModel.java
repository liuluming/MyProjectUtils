package com.my51c.see51.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by snt1231 on 2018/1/24.
 */

public class InterfaceModel extends BaseModel{

    /**
     * code : 0
     * msg : return_msg
     * interfaceList : [{"interface":"eth0","type":"0"},{"interface":"eth1","type":"0"},{"interface":"eth2","type":"0"}]
     */
    private List<InterfaceListBean> interfaceList;
    public List<InterfaceListBean> getInterfaceList() {
        return interfaceList;
    }

    public void setInterfaceList(List<InterfaceListBean> interfaceList) {
        this.interfaceList = interfaceList;
    }

    public static class InterfaceListBean {
        /**
         * interface : eth0
         * type : 0
         */

        @SerializedName("interface")
        private String interfaceX="";
        private String type="";

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
    }
}
