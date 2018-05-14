package com.my51c.see51.app.bean;

public class WarnBean {

    public static final int ITEM = 0;
    public static final int SECTION = 1;
    public int type;
    public int sectionPosition;
    public int listPosition;
    private String warnContent;
    private String warnTime;

    public WarnBean() {

    }

    public WarnBean(String warnContent, String warnTime, int type) {
        super();
        this.warnContent = warnContent;
        this.warnTime = warnTime;
        this.type = type;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public void setWarnContent(String warnContent) {
        this.warnContent = warnContent;
    }

    public String getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(String warnTime) {
        this.warnTime = warnTime;
    }


}
