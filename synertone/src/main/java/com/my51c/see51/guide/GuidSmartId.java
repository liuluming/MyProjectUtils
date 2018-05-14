package com.my51c.see51.guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.method.wifilist.ConnectWifi;
import com.method.wifilist.ConnectWifi.WifiCipherType;
import com.method.wifilist.WifiAdmin;
import com.method.wifilist.WifiConstant;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.SmartIdAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.service.LocalService;
import com.synertone.netAssistant.R;
import com.xqe.method.DelEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GuidSmartId extends BaseActivity implements OnClickListener, DeviceListListener {

    public static final int ADD_BY_AP = 100;
    public static final int ADD_BY_LOVALID = 101;
    public static final int ADD_BY_ID = 102;
    public static final int ADD_BY_VOICE = 103;
    public static String TAG = "GuidSmartId";
    public static String devId = null;
    private final String TYPE_RETICLE_WIFI = "reticle+wifi";
    private final String TYPE_RETICLE_3G = "3G_reticle";
    private DelEditText ssidEt;
    private ImageButton scanBtn, refreshBtn;
    private ListView devList;
    private Button nextBtn, restoreBtn;
    private ArrayList<String> allssidList, ssidList = null;
    private ArrayList<HashMap<String, String>> adapterList;
    private WifiManager mWifiManager;
    private SmartIdAdapter myAdapter;
    private ProgressBar waitProgress;
    private LinearLayout back;
    private boolean isDevOnLine = true;
    private String curssid = null;
    private int networkid = 0;
    private String strBindtype;

    private DeviceList localDevList;
    private LocalService localservice;
    private AppData appData;
    private ArrayList<String> localIdList = null;
    private HashSet<String> totallIdList = null;
    private Handler connectHandler = null;
    private Timer timer;
    private TimerTask timerTask;
    private ListUpdateHandler listUpdateHandler;
    private Runnable connectRunnable = new Runnable() {

        @Override
        public void run() {
            ConnectWifi connectWifi = new ConnectWifi(mWifiManager);
//			appData.getGVAPService().stop();
            connectWifi.connect(devId, "12345678", WifiCipherType.WIFICIPHER_WPA);
            mHandler.sendEmptyMessageDelayed(2, 2000);
        }
    };
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//			case 0:
//				if(totallIdList==null|totallIdList.size()==0){
//					refreshBtn.performClick();
//					System.out.println("--totallIdList==null");
//				}else{
//					System.out.println("--"+ssidList.size());
//					waitProgress.setVisibility(View.GONE);
////					devList.setVisibility(View.VISIBLE);
//				}
//				break;
                case 1:
                    if (!mWifiManager.isWifiEnabled()) {
                        checkWifi();
                    } else {
                        getSsidList();
                    }
                    break;
                case 2:
                    if (new WifiAdmin(getApplicationContext()).getSSID().replace("\"", "").equals(devId)) {

                        Log.i(TAG, "--" + devId + "已连接");
                        WifiConstant.constant_WIFI_CHENGED = true;

                        sendMessageDelayed(this.obtainMessage(3), 3000);
                        Log.i(TAG, "--延迟3s");
                    } else {
                        connectHandler.removeCallbacks(connectRunnable);
                        nextBtn.performClick();
                    }
                    break;
                case 3:
                    Log.i(TAG, "--开始跳转");
                    waitProgress.setVisibility(View.GONE);
                    Intent intent = new Intent(GuidSmartId.this, GuideSmartWifi.class);
                    intent.putExtra("isAp", true);
                    intent.putExtra("DeviceId", devId);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_devid_acy);
        findView();
        checkWifi();
        getCurWifiInfo();

        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));
        localservice = appData.getLocalService();
        localIdList = new ArrayList<String>();
        ssidList = new ArrayList<String>();
        allssidList = new ArrayList<String>();
        totallIdList = new HashSet<String>();
        connectHandler = new Handler();
        listUpdateHandler = new ListUpdateHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocalIdList();
        getSsidList();
        setDevListView();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                listUpdateHandler.sendEmptyMessage(1);
            }
        };
        if (totallIdList.size() == 0 | totallIdList == null) {
            waitProgress.setVisibility(View.VISIBLE);
            timer.schedule(timerTask, 10000);//10s后继续显示waitProgress
        }

//		二维码
        if (DimensionActivity.backToGuidSmartId) {
            String deviceId = DimensionActivity.deviceId;
            ssidEt.setText(deviceId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause");
        localDevList.removeListener(this);
        timerTask.cancel();
        timer.cancel();
    }

    private void findView() {

        back = (LinearLayout) findViewById(R.id.add_devid_backLayout);
        ssidEt = (DelEditText) findViewById(R.id.ssidEt);
        scanBtn = (ImageButton) findViewById(R.id.scanImg);
        refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        devList = (ListView) findViewById(R.id.devList);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        restoreBtn = (Button) findViewById(R.id.restoreBtn);
        waitProgress = (ProgressBar) findViewById(R.id.progressBar);

        back.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        restoreBtn.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ssidEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (ssidEt.length() == 12) {
                    nextBtn.setVisibility(View.VISIBLE);
                    restoreBtn.setVisibility(View.VISIBLE);
                    ssidEt.setSelection(ssidEt.length());
                } else if (ssidEt.length() > 12) {
                    ssidEt.setText(ssidEt.getText().toString().substring(0, 12));
                    ssidEt.setSelection(ssidEt.length());
                } else {
                    nextBtn.setVisibility(View.GONE);
                    restoreBtn.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void onListUpdate() {
//	to be done
        Log.i(TAG, "--onListUpdate");
        listUpdateHandler.sendEmptyMessage(0);
        if (localDevList != null) {
            if (localDevList.getDeviceCount() > 0) {
                timer.cancel();
            } else {
                timerTask.cancel();
                timer.cancel();
                timer = new Timer();
                timerTask = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listUpdateHandler.sendEmptyMessage(1);//显示
                    }
                };
                timer.schedule(timerTask, 10000);
                Log.i(TAG, "--10s后执行");
            }
        }
    }

    private void getLocalIdList() {
        totallIdList.clear();
        localIdList.clear();
        localDevList = appData.getLocalList();
        localDevList.addListListener(this);

        synchronized (localDevList) {
            for (Device device : localDevList) {
                DeviceLocalInfo devInfo = device.getLocalInfo();
                if (devInfo != null) {
                    String deviceId = devInfo.getCamSerial();
                    Log.i(TAG, "--getLocalIdList:" + deviceId);
                    localIdList.add(deviceId);
                    totallIdList.add(deviceId);
                }
            }
        }
    }

    private void getSsidList() {
        ssidList.clear();
        allssidList.clear();

        mWifiManager.startScan();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        if (wifiList != null) {
//			getCurWifiInfo();
            for (ScanResult scanResult : wifiList) {
                allssidList.add(scanResult.SSID);
            }
        }

        for (int i = 0; i < allssidList.size(); i++) {
            String s = allssidList.get(i);
            try {
                if ((s.substring(0, 1).equals("a")) | (s.substring(0, 1).equals("b")) | (s.substring(0, 1).equals("c")) && s.length() == 12) {
                    ssidList.add(s);
                    totallIdList.add(s);
                    Log.i(TAG, "--getSsidList:" + s);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void setDevListView() {
        if (totallIdList != null) {
            final ArrayList<String> list = new ArrayList<String>();
            for (String s : totallIdList) {
                list.add(s);
            }
            myAdapter = new SmartIdAdapter(list, GuidSmartId.this);
            devList.setAdapter(myAdapter);//bug
            devList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    ssidEt.setText(list.get(position));
                }
            });
        }
    }

    public void refresh() {
        if (!mWifiManager.isWifiEnabled()) {
            waitProgress.setVisibility(View.GONE);
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo == null) {
            //Log.d(TAG, "netInfo Not Connected");
            return;
        }

        if (netinfo.isConnected()) {
            int t = cm.getActiveNetworkInfo().getType();
            //Log.d(TAG, "Network type = " + t);
            if (t == ConnectivityManager.TYPE_WIFI) {
                // 仅在wifi或者ethernet网络下搜索局域网设备
                // TODO Auto-generated method stub
                waitProgress.setVisibility(View.VISIBLE);
                appData.getLocalList().clear();
                appData.getLocalService().search();
                getLocalIdList();
                getSsidList();
                myAdapter.notifyDataSetChanged();//mAdapter
            }
        }
    }

    public void checkWifi() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            System.out.println("wifi is off");
            WifiConstant.constant_WIFI_OFF = true;
            waitProgress.setVisibility(View.VISIBLE);
            mWifiManager.setWifiEnabled(true);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 3000);
        }
    }

    public void getCurWifiInfo() {
        if (mWifiManager.isWifiEnabled()) {
            WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
            curssid = wifiAdmin.getSSID().replace("\"", "");
            networkid = wifiAdmin.getNetworkId();
            WifiConstant.constant_NETWORK_ID = networkid;
            WifiConstant.constant_SSID = curssid;
        }
    }

    private void restoreDevice() {
        String deviceId = ssidEt.getText().toString();
        if (deviceId.equals(""))
            return;

        DeviceLocalInfo devInfo = null;
        boolean bfind = false;
        synchronized (localDevList) {
            for (Device device : localDevList) {
                devInfo = device.getLocalInfo();
                if (devInfo != null) {
                    if (deviceId.equals(devInfo.getCamSerial())) {
                        bfind = true;
                        break;
                    }
                }
            }
        }

        if (bfind) {
            localservice.setDefaultPara(devInfo);
            localservice.search();
            Toast.makeText(this, getString(R.string.reboot) + "...", Toast.LENGTH_LONG).show();
        }

    }

    private int checkDevId(String id) {

        if (totallIdList.contains(id))//收到广播或ap
        {
            if (localIdList.contains(id))//广播
            {
                Log.i(TAG, "--ADD_BY_LOVALID:" + id);
                return ADD_BY_LOVALID;
            } else                    //ap
            {
                Log.i(TAG, "--ADD_BY_AP:" + id);
                return ADD_BY_AP;
            }
        } else                        //非AP，非广播
        {
            if (id.substring(0, 1).equals("c") && (id.substring(2, 3).equals("1") | id.substring(2, 3).equals("5")))//声波添加
            {
                Log.i(TAG, "--ADD_BY_VOICE:" + id);
                return ADD_BY_VOICE;
            } else                    //序列号添加
            {
                Log.i(TAG, "--ADD_BY_ID:" + id);
                return ADD_BY_ID;
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.add_devid_backLayout:
                WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (mWifiManager.isWifiEnabled()) {
                    if (WifiConstant.constant_WIFI_OFF) {
                        Log.i(TAG, "--关闭wifi");
                        new WifiAdmin(getApplicationContext()).closeWifi();
                    } else if ((!WifiConstant.constant_WIFI_OFF) && WifiConstant.constant_WIFI_CHENGED) {
                        Log.i(TAG, "--还原wifi");
//					appData.getGVAPService().start();
                        removeWifi(WifiConstant.constant_NETWORK_ID);
                        WifiConstant.constant_WIFI_CHENGED = false;
                    }
                }
                backMainActivity();
                break;
            case R.id.refreshBtn:
                refresh();
                break;
            case R.id.scanImg:
                Intent intent = new Intent();
                intent.setClass(GuidSmartId.this, DimensionActivity.class);
                intent.putExtra("isSmartguide", true);
                GuidSmartId.this.startActivity(intent);
                break;
            case R.id.nextBtn:
                if (ssidEt.length() == 12) {
                    devId = ssidEt.getText().toString();
                    jumpToAcy(checkDevId(devId));
                } else {
                    Toast toast;
                    toast = Toast.makeText(getApplicationContext(), "请输入正确的设备id", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                break;
            case R.id.restoreBtn:
                new AlertDialog.Builder(this).setTitle(R.string.sure).setMessage(R.string.defaultSetting)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                restoreDevice();
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                    /* User clicked cancel so do some stuff */
                            }
                        })
                        .create()
                        .show();
                break;
            default:
                break;
        }
    }

    private void jumpToAcy(int i) {
        Intent intent = new Intent();
        switch (i) {
            case ADD_BY_AP:
//			appData.getGVAPService().stop(); // 此处切换到摄像头的wifi，先停止与gvap服务器的连接。
                waitProgress.setVisibility(View.VISIBLE);
                connectHandler.post(connectRunnable);
                System.out.println("------------AP添加");
                break;
            case ADD_BY_LOVALID://安装并添加
                System.out.println("------------安装并添加");
                ClickNext("complex");
                break;
            case ADD_BY_VOICE:
                System.out.println("------------声波添加");
                intent.setClass(GuidSmartId.this, GuidePrepareActivity.class);
                intent.putExtra("DeviceId", devId);
                startActivity(intent);
                finish();
                break;
            case ADD_BY_ID:
                System.out.println("------------序列号添加");
                ClickNext("simple");
                break;
            default:
                break;
        }
    }

    public void ClickNext(String s) {

        Intent intent = new Intent();
        String deviceId = ssidEt.getText().toString();
        String deviceType = TYPE_RETICLE_WIFI;
        intent.putExtra("DeviceId", deviceId);


        String devMode = deviceId.substring(0, 3);
        if (devMode.equals("a82") || devMode.equals("a83") || devMode.equals("a84")) {
            // 3G + 网口
            deviceType = TYPE_RETICLE_3G;
        } else {
            // 只有网口
            deviceType = TYPE_RETICLE_WIFI;
        }

        localDevList.removeListener(this);
        intent.putExtra("DeviceType", deviceType);
        //Log.d(TAG, "deviceId:" + deviceId);

        if (s.equals("complex"))//安装并添加
        {
            Log.i(TAG, "--" + deviceType);
            PlatformActivity.isBack = false;
            intent.putExtra("smart_complex", true);
            intent.setClass(GuidSmartId.this, GuideSetActivity.class);
        } else//id序列号添加
        {
            intent.setClass(GuidSmartId.this, PlatformActivity.class);
            intent.putExtra("network", "wifi");
            intent.putExtra("DeviceId", deviceId);
        }

        GuidSmartId.this.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            connectHandler.removeCallbacks(connectRunnable);
            Log.i(TAG, "--SSID" + WifiConstant.constant_SSID + "--ID" + WifiConstant.constant_NETWORK_ID);
            WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager.isWifiEnabled()) {
                if (WifiConstant.constant_WIFI_OFF) {
                    Log.i(TAG, "--关闭wifi");
                    new WifiAdmin(getApplicationContext()).closeWifi();
                } else if ((!WifiConstant.constant_WIFI_OFF) && WifiConstant.constant_WIFI_CHENGED) {
                    Log.i(TAG, "--还原wifi");
//					appData.getGVAPService().start();
                    removeWifi(WifiConstant.constant_NETWORK_ID);
                    WifiConstant.constant_WIFI_CHENGED = false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void removeWifi(int id) {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        if (!wifiAdmin.getSSID().replace("\"", "").equals(WifiConstant.constant_SSID)) {
            WifiManager wifiManager = wifiAdmin.getWifiManager();
            int curId = wifiAdmin.getNetworkId();
            wifiManager.removeNetwork(curId);
            wifiManager.enableNetwork(id, false);
        }
    }

    public void backMainActivity() {
        GuidSmartId.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    static class ListUpdateHandler extends Handler {

        WeakReference<GuidSmartId> mActivity;

        public ListUpdateHandler(GuidSmartId activity) {
            mActivity = new WeakReference<GuidSmartId>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
            GuidSmartId theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    Log.i(TAG, "--receive listUpdateHandler.sendEmptyMessage(0)");
                    theActivity.getLocalIdList();
                    theActivity.getSsidList();
                    theActivity.myAdapter.notifyDataSetChanged();
                    theActivity.setDevListView();
                    if (!theActivity.totallIdList.isEmpty()) {
                        theActivity.waitProgress.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    theActivity.waitProgress.setVisibility(View.VISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    }


}
