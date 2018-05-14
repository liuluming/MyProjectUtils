package com.my51c.see51.app.bean;

/**
 * Created by snt1231 on 2018/1/24.
 */

public class APNModel extends AdapterModel{
    private String apn="";//APN
    private String username=""; //用户名
    private String password=""; //密码

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
