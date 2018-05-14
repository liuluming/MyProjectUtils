package com.my51c.see51.app.bean;

/**
 * Created by snt1231 on 2017/3/31.
 */

public class AccountModel {
    private String user;
    private String passwd;
    private String sessionToken;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
