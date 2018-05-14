package com.my51c.see51.app.bean;

/**
 * Created by snt1231 on 2018/1/24.
 */

public class PPPoEModel extends AdapterModel{
    private String username=""; //账号
    private String password=""; //口令
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
