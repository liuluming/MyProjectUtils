package com.my51c.see51.widget;

public class ScreenBean {
    private int sWidth;
    private int sHeight;

    public ScreenBean(int sWidth, int sHeight) {
        super();
        this.sWidth = sWidth;
        this.sHeight = sHeight;
    }

    public int getsWidth() {
        return sWidth;
    }

    public void setsWidth(int sWidth) {
        this.sWidth = sWidth;
    }

    public int getsHeight() {
        return sHeight;
    }

    public void setsHeight(int sHeight) {
        this.sHeight = sHeight;
    }

}
