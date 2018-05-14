package com.my51c.see51.widget;

import android.widget.ImageView;


public class MyComment {

    private String username;
    private String content;
    private String publish_time;
    private ImageView userHead;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ImageView getUserHead() {
        return userHead;
    }

    public void setUserHead(ImageView userHead) {
        this.userHead = userHead;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String toString() {
        return username + ":" + content + "--" + publish_time;
    }

}
