package com.my51c.see51.app.bean;

import java.io.Serializable;

public class ArrContentList implements Serializable {
    String name;
    int mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }
}
