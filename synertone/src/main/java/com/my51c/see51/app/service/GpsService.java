package com.my51c.see51.app.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.my51c.see51.common.AppData;

public class GpsService extends Service {

    private MyGPSBinder mbinder;
    private LocationClient mLocationClient;
    private AppData application;

    @Override
    public void onCreate() {
        super.onCreate();

        mbinder = new MyGPSBinder();
        application = (AppData) getApplication();
        mLocationClient = application.mLocationClient;
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000 * 30);//设置发起定位请求的间隔时间
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mbinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        application.mLocationClient.stop();
        mLocationClient.stop();
        super.onDestroy();
    }

    private class MyGPSBinder extends Binder {
        GpsService getService() {
            return GpsService.this;
        }
    }
}
