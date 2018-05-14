package com.my51c.see51.guide;

import android.content.Context;

import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.service.LocalService;

public class Connect implements DeviceListListener {
    private LocalService localService;
    private DeviceList deviceList;
    private int deviceCount = 0;

    private AppData appData;

    public Connect(Context c) {
        appData = (AppData) c.getApplicationContext();
        initDevice();
    }

    public void initDevice() {
//		localService = new LocalService();
//		deviceList = new DeviceList();
//		localService.init(deviceList);
//		deviceList.addListListener(this);
        localService = appData.getLocalService();
        deviceList = appData.getLocalList();
        deviceList.addListListener(this);
    }

    public LocalService getLocalService() {
        return localService;
    }


    public DeviceList getDeviceList() {
        return deviceList;
    }


    public int getDeviceCount() {
        return deviceCount;
    }

    @Override
    public void onListUpdate() {
        // TODO Auto-generated method stub
        deviceCount = deviceList.getDeviceCount();
    }
}
