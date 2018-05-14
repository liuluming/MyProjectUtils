package com.my51c.see51.yzxvoip.model;

import android.graphics.Bitmap;

public class SortModel {

    private String name; //�û���
    private String sortLetters; //��ʼ��ĸ
    private String id; // �˺�
    private Bitmap bitmap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id.replace(" ", "");
    }

    public void setId(String id) {
        this.id = id;
    }
}
