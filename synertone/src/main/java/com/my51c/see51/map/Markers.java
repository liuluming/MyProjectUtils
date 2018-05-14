package com.my51c.see51.map;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-12-22.
 */
public class Markers implements Serializable {
    //经度
    private double latitude;

    //纬度
    private double longitude;

    //摄像头名称
    private String name;

    //位置名称
    private String id;

    public Markers(double latitude, double longitude, String name, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.id = id;
    }

    public Markers() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }
}
