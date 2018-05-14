package com.my51c.see51.app.bean;

import java.util.List;

public class WarnInfoBean {
    /**
     * code : 0
     * msg : return_msg
     * alarmList : [{"alarmCode":"110000","trigger":0,"alarmTime":"20160421111111"}]
     */

    private String code;
    private String msg;
    private List<AlarmListBean> alarmList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AlarmListBean> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<AlarmListBean> alarmList) {
        this.alarmList = alarmList;
    }


    public static class AlarmListBean {
        public static final int ITEM = 0;
        public static final int SECTION = 1;
        public int type;
        public int sectionPosition;
        public int listPosition;
        /**
         * alarmCode : 110000
         * trigger : 0
         * alarmTime : 20160421111111
         */

        private String alarmCode;
        private int trigger;
        private String alarmTime;
        private String alarmContent;
        private String alarmType;


        public String getAlarmType() {
            return alarmType;
        }

        public void setAlarmType(String alarmType) {
            this.alarmType = alarmType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAlarmContent() {
            return alarmContent;
        }

        public void setAlarmContent(String alarmContent) {
            this.alarmContent = alarmContent;
        }

        public String getAlarmCode() {
            return alarmCode;
        }

        public void setAlarmCode(String alarmCode) {
            this.alarmCode = alarmCode;
        }

        public int getTrigger() {
            return trigger;
        }

        public void setTrigger(int trigger) {
            this.trigger = trigger;
        }

        public String getAlarmTime() {
            return alarmTime;
        }

        public void setAlarmTime(String alarmTime) {
            this.alarmTime = alarmTime;
        }
    }

}
