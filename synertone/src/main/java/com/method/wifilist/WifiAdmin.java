package com.method.wifilist;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

public class WifiAdmin {
    // ����WifiManager����   
    public static WifiManager mWifiManager;
    // ����һ��WifiLock
    WifiLock mWifiLock;
    // ����WifiInfo����
    private WifiInfo mWifiInfo;
    // ɨ��������������б�
    private List<ScanResult> mWifiList;
    // ���������б�
    private List<WifiConfiguration> mWifiConfiguration;


    // ������   
    public WifiAdmin(Context context) {
        // ȡ��WifiManager����   
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // ȡ��WifiInfo����   
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // ��WIFI   
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // �ر�WIFI   
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // ��鵱ǰWIFI״̬   
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // ����һ��WifiLock   
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // ����WifiLock   
    public void acquireWifiLock() {        //���Ӧ�ó���������Ļ���ص������ʹ��WiFi��
        mWifiLock.acquire();                //����Ե��� acquireWifiLock����סWiFi���ò�������ֹWiFi����˯��״̬��
    }

    // ����WifiLock   
    public void releaseWifiLock() {        //��Ӧ�ó�����ʹ��WiFiʱ��Ҫ���� releaseWifiLock���ͷ�WiFi��
        // �ж�ʱ������   						//֮��WiFi���Խ���˯��״̬�Խ�ʡ��Դ
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // �õ����úõ�����   
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // ָ�����úõ������������   
    public void connectConfiguration(int index) {
        // �����������úõ�������������   
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // �������úõ�ָ��ID������   
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // �õ�ɨ����   
        mWifiList = mWifiManager.getScanResults();
        // �õ����úõ���������   
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // �õ������б�   
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // �鿴ɨ����   
    public StringBuilder lookUpScan(List<ScanResult> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // ��ScanResult��Ϣת����һ���ַ�����   
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level   
            stringBuilder.append((list.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // �õ�MAC��ַ   
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // �õ�������BSSID   
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // �õ�IP��ַ   
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // �õ����ӵ�ID   
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // �õ�WifiInfo��������Ϣ��   
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    // ���һ�����粢����   
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    // �Ͽ�ָ��ID������   
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

}
