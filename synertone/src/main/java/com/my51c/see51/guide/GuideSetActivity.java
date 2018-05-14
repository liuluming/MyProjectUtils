package com.my51c.see51.guide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.method.wifilist.WifiAdmin;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.Device3GInfo;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.guide.WiFiHelper.WifiScanResult;
import com.my51c.see51.guide.WiFiHelper.WifiStatusEventListener;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.synertone.netAssistant.R;
import com.xqe.method.DelEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GuideSetActivity extends SherlockFragmentActivity implements DeviceListListener, WifiStatusEventListener, OnSetDeviceListener {
    public static final String TYPE_ONLY_AP = "onlyAp";
    private final static int SEARCHED_DEVICE_RETICLE_LINK = 0;
    private final static int RETRYSET = 1;
    private final static int RETRYSEARCH = 2;
    private final static int SUCCESS = 3;
    private final static int FAILURE = 4;
    private final static int ONLYWIFILINKEDAP = 5;
    private final static int SEARCHED_DEVICE_ONLY_WIFI = 6;
    private final static int SET_WIFI_OK = 7;
    private final static int INTENTPLATFORMACTIVITY = 8;
    private final static int WIFICONNECTED = 9;
    private final static int SUCCESS_3G = 10;
    private final static int FAILED_3G = 11;
    private final String TAG = "SetActivity";
    private final int RETICLE_LINK = 0;
    private final int SET_WIFI = 1;
    private final int ONLY_WIFI = 2;
    private final int SELECT_3G = 3;
    private final int INTERNET_3G = 4;
    private final String TYPE_RETICLE_WIFI = "reticle+wifi";
    private final String TYPE_ONLY_WIFI = "onlyWifi";
    private final String TYPE_ONLY_3G = "only3G";
    private final String TYPE_RETICLE_3G = "3G_reticle";
    private final String TYPE_ONLY_RETICLE = "onlyReticle";
    private Button btnReticleNext, btnSetWifiOK, btn3gReticleLink, btn3GLink, btnOnlyWifi;
    private Button btnWifiLink;
    private Button btnSet3gOK;
    private ImageButton btnSelectWifi;
    private LinearLayout lt_reticle_link, lt_set_wifi, lt_3G_select, lt_3G_internet, lt_only_wifi;
    private ImageView imgSearchDevice;
    private ImageView imgSet;
    private ImageView imgSearchDeviceAp;
    private ImageView imgLinkDevice;
    private ProgressBar proSearchDevice;
    private ProgressBar proSet;
    private ProgressBar proSearchDeviceAp;
    private ProgressBar progressBarWait;
    private ProgressBar proLinkDevice;
    private ProgressBar progressBarWait3G;
    private TextView txtSearchResult;
    private TextView txtSetResult;
    private TextView txtDeviceId;
    private TextView txtSearchDeviceAp;
    private TextView txtLinkDevice;
    private TextView txtSet3GResult;
    private EditText edtSSID;
    private DelEditText edtSSIDPwd;
    private CheckBox showPsw;
    private EditText edt3gUser;
    private EditText edt3gpass;
    private EditText edt3gapn;
    private EditText edt3gphonenum;
    private LinearLayout linLayout;
    private RelativeLayout setLayout;
    private RelativeLayout searchLayout;
    private LayoutInflater inflater;
    private Button_OnClickListener btnListener;
    private DeviceList localDevList;
    private LocalService localService;
    private DeviceLocalInfo deviceLocalInfo;
    private Thread jumpThread;
    private Thread setThread;
    private Thread searchThread;
    private String deviceId, deviceType;
    private String network = "wifi";
    private String SSID;
    private String Password;
    private String str3guser;
    private String str3gpass;
    private String str3gapn;
    private String str3gphonenum;
//	public static final String SMART_SELECT_3G = "onlyAp";
    private boolean retry = false;
    private boolean findDevice;
    private boolean reticleSelectedWifi;
    private boolean threeGSelectReticle;
    private boolean continueSearch;
    private boolean reSetDhcp;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ssidList;
    private WifiManager mWifiManager;
    private AppData appData;
    private MyHandler mHandler;
    private WiFiHelper mWiFiHelper;
    private Device3GInfo device3Ginfo;
    private boolean isAp = false;
    private boolean smart_complex = false;
    private LinearLayout backLayout;
    private TextView title;
    private Runnable jumpRun = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            WiFiHelper mWiFiHelper = null;
            try {
                Thread.sleep(1000);
                mWiFiHelper = WiFiHelper.getInstance(GuideSetActivity.this, "^\\d{12}$");
                if (mWiFiHelper != null) {
                    if (!SSID.equals(mWiFiHelper.getCurrentSSID())) {
                        network = "ap";
                    }
                }
                mWiFiHelper.connect(SSID, Password);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jumpPlatformActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_set);

        inflater = LayoutInflater.from(this);
        btnListener = new Button_OnClickListener();
        mHandler = new MyHandler(this);
        deviceId = getIntent().getStringExtra("DeviceId");
        deviceType = getIntent().getStringExtra("DeviceType");
        isAp = getIntent().getBooleanExtra("isAp", false);


        ssidList = new ArrayList<String>();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        if (wifiList != null) {
            for (ScanResult scanResult : wifiList) {
                ssidList.add(scanResult.SSID);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ssidList);
        }
        findeView();
        linLayout.addView(lt_3G_select);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add("exit").setIcon(R.drawable.x)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                clickBack();
                break;
        }
        if (item.getTitle().equals("exit")) {
            appData.exit();
            //Log.d(TAG, "GuideSetingActivity exit$$$$$$$$$$$$$$$$$");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume");
        this.setOnClickListener();
        this.getAppData();
        this.initFace();

    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy()");
        if (mWiFiHelper != null) {
            mWiFiHelper.stop();
            mWiFiHelper.setWifiStatusEventListener(null);
        }
        super.onDestroy();
    }

    private void findeView() {
        linLayout = (LinearLayout) findViewById(R.id.LyCabSelect);
        lt_set_wifi = (LinearLayout) inflater.inflate(R.layout.guide_set_wifi, null).findViewById(R.id.LayoutSetWifi);
        lt_reticle_link = (LinearLayout) inflater.inflate(R.layout.guide_reticle_link, null).findViewById(R.id.layoutReticleLink);
        lt_3G_select = (LinearLayout) inflater.inflate(R.layout.guide_3g_select, null).findViewById(R.id.layout3GSelect);
        lt_3G_internet = (LinearLayout) inflater.inflate(R.layout.guide_3g_internet, null).findViewById(R.id.layout3GInternet);
        lt_only_wifi = (LinearLayout) inflater.inflate(R.layout.guide_only_wifi, null).findViewById(R.id.layoutOnlyWifi);

        btnReticleNext = (Button) lt_reticle_link.findViewById(R.id.btnReticleNext);
        btnSetWifiOK = (Button) lt_set_wifi.findViewById(R.id.btnSetWifiOK);
        btn3gReticleLink = (Button) lt_3G_select.findViewById(R.id.btn3gReticleLink);
        btn3GLink = (Button) lt_3G_select.findViewById(R.id.btn3GLink);
        btnWifiLink = (Button) lt_3G_select.findViewById(R.id.btnWifiLink);
        //btn3gNext = (Button) lt_3G_internet.findViewById(R.id.btn3gNext);
        btnSet3gOK = (Button) lt_3G_internet.findViewById(R.id.btnSet3GOK);
        btnOnlyWifi = (Button) lt_only_wifi.findViewById(R.id.btnOnlyWifi);
        btnSelectWifi = (ImageButton) lt_set_wifi.findViewById(R.id.btnSelectWifi);

        imgSearchDevice = (ImageView) lt_reticle_link.findViewById(R.id.imgSearchDevice);
        imgSet = (ImageView) lt_reticle_link.findViewById(R.id.imgSet);
        imgSearchDeviceAp = (ImageView) lt_only_wifi.findViewById(R.id.imgSearchDeviceAp);
        imgLinkDevice = (ImageView) lt_only_wifi.findViewById(R.id.imgLinkDevice);

        proSearchDevice = (ProgressBar) lt_reticle_link.findViewById(R.id.proSearchDevice);
        proSet = (ProgressBar) lt_reticle_link.findViewById(R.id.proSet);
        proSearchDeviceAp = (ProgressBar) lt_only_wifi.findViewById(R.id.proSearchDeviceAp);
        progressBarWait = (ProgressBar) lt_set_wifi.findViewById(R.id.progressBarWait);
        proLinkDevice = (ProgressBar) lt_only_wifi.findViewById(R.id.proLinkDevice);
        progressBarWait3G = (ProgressBar) lt_3G_internet.findViewById(R.id.progressBar3GWait);

        txtSearchResult = (TextView) lt_reticle_link.findViewById(R.id.txtSearchResult);
        txtSetResult = (TextView) lt_reticle_link.findViewById(R.id.txtSetResult);
        txtDeviceId = (TextView) lt_only_wifi.findViewById(R.id.txtDeviceId);
        txtSearchDeviceAp = (TextView) lt_only_wifi.findViewById(R.id.txtSearchDeviceAp);
        txtLinkDevice = (TextView) lt_only_wifi.findViewById(R.id.txtLinkDevice);

        edtSSID = (EditText) lt_set_wifi.findViewById(R.id.edtSSID);
        edtSSIDPwd = (DelEditText) lt_set_wifi.findViewById(R.id.edtSSIDPwd);
        showPsw = (CheckBox) lt_set_wifi.findViewById(R.id.checkpasswdshow);

        edt3gUser = (EditText) lt_3G_internet.findViewById(R.id.edt3GUser);
        edt3gpass = (EditText) lt_3G_internet.findViewById(R.id.edt3Gpasswd);
        edt3gapn = (EditText) lt_3G_internet.findViewById(R.id.edt3Gapn);
        edt3gphonenum = (EditText) lt_3G_internet.findViewById(R.id.edt3Gphone);
        txtSet3GResult = (TextView) lt_3G_internet.findViewById(R.id.txtSet3GResult);

        setLayout = (RelativeLayout) lt_reticle_link.findViewById(R.id.setLayout);
        searchLayout = (RelativeLayout) lt_only_wifi.findViewById(R.id.searchLayout);

        edtSSID.setText(new WifiAdmin(getApplicationContext()).getSSID().replace("\"", ""));

        title = (TextView) findViewById(R.id.guidset_title);
        backLayout = (LinearLayout) findViewById(R.id.guidset_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                clickBack();
            }
        });

    }

    public void setOnClickListener() {
        btnReticleNext.setOnClickListener(btnListener);
        btnSetWifiOK.setOnClickListener(btnListener);
        btn3gReticleLink.setOnClickListener(btnListener);
        btn3GLink.setOnClickListener(btnListener);
        //btn3gNext.setOnClickListener(btnListener);
        btnOnlyWifi.setOnClickListener(btnListener);
        btnSelectWifi.setOnClickListener(btnListener);
        btnWifiLink.setOnClickListener(btnListener);
        btnSet3gOK.setOnClickListener(btnListener);


        showPsw.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    edtSSIDPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edtSSIDPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    public void getAppData() {
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));
        localService = appData.getLocalService();
        localDevList = appData.getLocalList();
        localDevList.addListListener(this);

        try {
            deviceLocalInfo = localDevList.getDevice(deviceId).getLocalInfo();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            localDevList.clear();
            localService.search();
            e.printStackTrace();
        }

    }

    private void initFace() {
        if (PlatformActivity.isBack) {
            linLayout.removeAllViews();
            if (deviceType.equals(TYPE_ONLY_RETICLE)) {
                changeLayout(RETICLE_LINK);
            } else if (deviceType.equals(TYPE_RETICLE_WIFI)) {
                System.out.println("----------TYPE_RETICLE_WIFI");
                if (reticleSelectedWifi) {
                    System.out.println("----------SET_WIFI");
                    changeLayout(SET_WIFI);
                } else {
                    System.out.println("----------RETICLE_LINK");
                    changeLayout(RETICLE_LINK);
                }
            } else if (deviceType.equals(TYPE_ONLY_WIFI)) {
                changeLayout(SET_WIFI);
            } else if (deviceType.equals(TYPE_ONLY_3G)) {
                changeLayout(INTERNET_3G);
            } else if (deviceType.equals(TYPE_RETICLE_3G)) {
                if (threeGSelectReticle) {
                    changeLayout(RETICLE_LINK);
                } else {
                    changeLayout(INTERNET_3G);
                    System.out.println("----------3G��INTERNET_3G-----------");
                }
            }
        } else if (deviceType.equals(TYPE_ONLY_AP)) {
            linLayout.removeAllViews();
            changeLayout(ONLY_WIFI);
        } else {
            linLayout.removeAllViews();
            changeLayout(SELECT_3G);
            System.out.println("-------SELECT_3G");

        }
    }

    public void changeLayout(int layout) {
        linLayout.removeAllViews();
        switch (layout) {
            case RETICLE_LINK:
                //Log.d(TAG, "lyaout" + layout + "REYICLE_LINK" + RETICLE_LINK);
                title.setText(getString(R.string.by_network_cable));

                linLayout.addView(lt_reticle_link);
                if (!findDevice("layout_reticle_link")) {
                    searchDevice();
                }
                break;
            case SET_WIFI:

                title.setText(getString(R.string.by_ap));//by_ap:ѡ��WiFi

                linLayout.addView(lt_set_wifi);
                break;
            case ONLY_WIFI:
                title.setText("");
                mWiFiHelper = WiFiHelper.getInstance(GuideSetActivity.this, "^\\d{12}$");
                mWiFiHelper.start();
                mWiFiHelper.setWifiStatusEventListener(this);
                linLayout.addView(lt_only_wifi);
                txtDeviceId.setText(mWiFiHelper.getCurrentSSID());
                if (deviceId.equals(mWiFiHelper.getCurrentSSID()) || findDevice("device")) {
                    mHandler.sendEmptyMessage(ONLYWIFILINKEDAP);
                    if (!findDevice("layout_only_wifi"))
                        searchDevice();
                } else {
                    appData.getGVAPService().stop(); // �˴��л�������ͷ��wifi����ֹͣ��gvap�����������ӡ�
                    mWiFiHelper.getScanResults();
                    mWiFiHelper.setAlertDialogContext(this);
                    mWiFiHelper.pushWIFI();
                    String password = null;
                    mWiFiHelper.connect(deviceId, password);
                }
                network = "ap";
                break;
            case SELECT_3G:
                linLayout.addView(lt_3G_select);

                title.setText(getString(R.string.plSelect));

                if (deviceType.equals(TYPE_RETICLE_WIFI)) {
                    btn3gReticleLink.setVisibility(View.VISIBLE);
                    btn3GLink.setVisibility(View.GONE);
                    btnWifiLink.setVisibility(View.VISIBLE);
                } else if (deviceType.equals(TYPE_RETICLE_3G)) {
                    btn3gReticleLink.setVisibility(View.VISIBLE);
                    btn3GLink.setVisibility(View.VISIBLE);
                    btnWifiLink.setVisibility(View.GONE);
                }
//			network = "3g";
                break;
            case INTERNET_3G: {

                title.setText(getString(R.string.by_3G));

                linLayout.addView(lt_3G_internet);
                localService.setListener(this);
                deviceLocalInfo = localDevList.getDevice(deviceId).getLocalInfo();
                device3Ginfo = new Device3GInfo(deviceLocalInfo.toByteBuffer());

                edt3gUser.setText(device3Ginfo.getsz3GUser());
                edt3gpass.setText(device3Ginfo.getsz3GPWD());
                edt3gapn.setText(device3Ginfo.getsz3GAPN());
                edt3gphonenum.setText(device3Ginfo.getszDialNum());
            }
            break;
        }
    }

    @Override
    public void OnWifiDisconnected(String ssid) {
        // TODO Auto-generated method stub
        txtDeviceId.setText(R.string.disconnectWifi);
    }

    @Override
    public void OnWifiConnected(String ssid) {
        // TODO Auto-generated method stub
        //Log.d(TAG, "OnWifiConnected ssid=" + ssid);
        mHandler.sendEmptyMessage(WIFICONNECTED);
        if (ssid == null) {
            return;
        }
        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        if (ssid.matches("^\\d{12}$")) {
            if (deviceId.equals(mWiFiHelper.getCurrentSSID())) {
                mHandler.sendEmptyMessage(ONLYWIFILINKEDAP);
                if (!findDevice("layout_only_wifi"))
                    searchDevice();
            }
        }

    }

    @Override
    public void OnWifiConnectFailed(String ssid) {
        // TODO Auto-generated method stub
        //Log.d(TAG, "wifiConectFailed");
    }

    @Override
    public void OnWifiScanOver(List<WifiScanResult> result) {
        // TODO Auto-generated method stub
        // List<WifiScanResult> list = mWiFiHelper.getScanResults();
        // for (WifiScanResult wifiScanResult : list)
        // {
        // if (deviceId.equals(wifiScanResult.ssid))
        // {
        // mHandler.sendEmptyMessage(WIFI_FINDED_ID);
        // }
        // }
    }

    @Override
    public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
        // TODO Auto-generated method stub
        //Log.d(TAG, "onSetDeviceSucess");
        if (linLayout.getChildAt(0).getId() == R.id.layoutReticleLink) {
            mHandler.sendEmptyMessage(SUCCESS);
        } else if (linLayout.getChildAt(0).getId() == R.id.layout3GInternet) {
            mHandler.sendEmptyMessage(SUCCESS_3G);
        }
    }

    @Override
    public void onSetDeviceFailed(DeviceLocalInfo devInfo) {
        // TODO Auto-generated method stub
        //Log.d(TAG, "onSetDeviceFailed");
        if (linLayout.getChildAt(0).getId() == R.id.layoutReticleLink)
            mHandler.sendEmptyMessage(FAILURE);
        else if (linLayout.getChildAt(0).getId() == R.id.LayoutSetWifi) {
            if (jumpThread != null)
                jumpThread.interrupt();
            Toast.makeText(this, R.string.setFail, Toast.LENGTH_SHORT).show();
            progressBarWait.setVisibility(View.INVISIBLE);
        } else if (linLayout.getChildAt(0).getId() == R.id.layout3GInternet) {
            mHandler.sendEmptyMessage(FAILED_3G);
        }

    }

    public void setDHCP() {
        if (deviceLocalInfo == null) {
            localDevList.clear();
            localService.search();

            return;
        }

        if (deviceLocalInfo.getEnableDeviceDHCP() == 1)
            mHandler.sendEmptyMessage(SUCCESS);
        else {
            reSetDhcp = true;
            deviceLocalInfo.setEnableDeviceDHCP(1);
            localService.setListener(this);
            localService.setDeviceParam(deviceLocalInfo);
            setThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (reSetDhcp) {
                        try {
                            Thread.sleep(4000);
                            mHandler.sendEmptyMessage(RETRYSET);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });
            setThread.start();
        }
    }

    public boolean findDevice(String layout) {
        findDevice = false;
        if (!findDevice) {
            synchronized (localDevList) {
                for (Device device : localDevList) {
                    DeviceLocalInfo devInfo = device.getLocalInfo();
                    if (devInfo != null) {
                        //Log.d(TAG, "deviceId:" + deviceId + ",	devInfo.getCamSerial()" + devInfo.getCamSerial());
                        if (deviceId.equals(devInfo.getCamSerial())) {
                            //Log.d(TAG, "devInfo.getCamSerial()" + devInfo.getCamSerial());
                            findDevice = true;
                            if (layout.equals("layout_reticle_link"))
                                mHandler.sendEmptyMessage(SEARCHED_DEVICE_RETICLE_LINK);
                            else if (layout.equals("layout_only_wifi"))
                                mHandler.sendEmptyMessage(SEARCHED_DEVICE_ONLY_WIFI);
                            break;
                        }
                    }
                }
            }
        }
        return findDevice;
    }

    public void showAlertDialog(Context context) {
        new AlertDialog.Builder(context).setMessage(R.string.orSetWifi).setPositiveButton(R.string.yes01, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLayout(SET_WIFI);
                reticleSelectedWifi = true;
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.no01, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumpPlatformActivity();
                reticleSelectedWifi = false;
                dialog.dismiss();

            }
        })

                .show();
    }

    public void jumpPlatformActivity() {
        Intent intent = new Intent(GuideSetActivity.this, PlatformActivity.class);
        if (isAp) {
            intent.putExtra("isAp", true);
        }
        intent.putExtra("network", network);
        intent.putExtra("DeviceId", deviceId);
        startActivity(intent);
        finish();
        if (mWiFiHelper != null)
            mWiFiHelper.stop();
        this.localDevList.removeListener(this);
        Log.i(TAG, "--jumpPlatformActivity complete");
    }

    public void clickBack() {
        if (linLayout.getChildAt(0).getId() == R.id.layoutReticleLink) {
            if (deviceType.equals(TYPE_RETICLE_3G))        //change by xqe
            {
                changeLayout(SELECT_3G);
            } else {
                finish();
            }
        } else if (linLayout.getChildAt(0).getId() == R.id.LayoutSetWifi) {
            changeLayout(SELECT_3G);    //change by xqe
//			finish();

        } else if (linLayout.getChildAt(0).getId() == R.id.layoutOnlyWifi) {
            finish();
        } else if (linLayout.getChildAt(0).getId() == R.id.layout3GSelect) {
            finish();
        } else if (linLayout.getChildAt(0).getId() == R.id.layout3GInternet) {
            changeLayout(SELECT_3G);
        }
    }

    public void searchDevice() {
        continueSearch = true;
        searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (continueSearch) {
                    try {
                        localDevList.clear();
                        localService.search();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        searchThread.start();
    }

    @Override
    public void onListUpdate() {
        // TODO Auto-generated method stub
        //Log.d(TAG, "onListUpdate() deviceId " + deviceId);
        synchronized (localDevList) {


            if (localDevList == null) {
                //Log.e(TAG, " localDevList == null ");
            } else {
                if (lt_reticle_link != null && lt_reticle_link.equals(linLayout.getChildAt(0).getId())) {
                    this.findDevice("layout_reticle_link");
                    continueSearch = false;
                } else if (lt_set_wifi != null && lt_set_wifi.equals(linLayout.getChildAt(0))) {
                    if (localDevList.getDevice(deviceId) == null) {
                        //Log.e(TAG, " localDevList.getDevice(deviceId) == null ");
                        for (Device device : localDevList) {
                            //Log.d(TAG, "device id :" + device.getID());
                        }

                    } else if (localDevList.getDevice(deviceId).getLocalInfo() == null) {
                        //Log.e(TAG, " localDevList.getDevice(deviceId).getLocalInfo() == null ");
                    } else {
                        if (retry) {
                            retry = false;
                            mHandler.sendEmptyMessage(SET_WIFI_OK);
                        }
                        //Log.d(TAG, "getCamSerial :" + localDevList.getDevice(deviceId).getLocalInfo().getCamSerial());

                    }
                } else if (lt_only_wifi.equals(linLayout.getChildAt(0))) {
                    //Log.e(TAG, "currentlayout:"+linLayout.getChildAt(0).getClass());
                    if (findDevice("layout_only_wifi"))
                        continueSearch = false;
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    static class MyHandler extends Handler {
        WeakReference<GuideSetActivity> mActivity;

        MyHandler(GuideSetActivity activity) {
            mActivity = new WeakReference<GuideSetActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GuideSetActivity theActivity = mActivity.get();
            switch (msg.what) {
                case SEARCHED_DEVICE_RETICLE_LINK:
                    theActivity.proSearchDevice.setVisibility(View.GONE);
                    theActivity.imgSearchDevice.setVisibility(View.VISIBLE);
                    theActivity.txtSearchResult.setText(R.string.searchedCamera);
                    theActivity.setLayout.setVisibility(View.VISIBLE);
                    theActivity.setDHCP();
                    break;
                case RETRYSET:
                    theActivity.txtSetResult.setText(R.string.guideConnectTimeout);
                    theActivity.deviceLocalInfo.setEnableDeviceDHCP(1);
                    theActivity.localService.setDeviceParam(theActivity.deviceLocalInfo);
                    break;
                case RETRYSEARCH:
                    theActivity.searchDevice();
                    break;
                case SUCCESS:

                    theActivity.reSetDhcp = false;
                    theActivity.proSet.setVisibility(View.GONE);
                    theActivity.imgSet.setVisibility(View.VISIBLE);
                    theActivity.txtSetResult.setText(R.string.setDHcp);
                    theActivity.btnReticleNext.setVisibility(View.VISIBLE);
                    theActivity.btnReticleNext.setClickable(true);

                    break;
                case FAILURE:
                    theActivity.reSetDhcp = false;
                    theActivity.proSet.setVisibility(View.GONE);
                    theActivity.imgSet.setBackgroundResource(R.drawable.error);
                    theActivity.txtSetResult.setText(R.string.setFail);
                    theActivity.btnReticleNext.setClickable(false);
                    theActivity.btnReticleNext.setVisibility(View.INVISIBLE);
                    break;
                case ONLYWIFILINKEDAP:
                    theActivity.proLinkDevice.setVisibility(View.GONE);
                    theActivity.imgLinkDevice.setVisibility(View.VISIBLE);
                    theActivity.txtLinkDevice.setText(R.string.conCamAPed);
                    theActivity.searchLayout.setVisibility(View.VISIBLE);
                    break;
                case SEARCHED_DEVICE_ONLY_WIFI:
                    theActivity.proSearchDeviceAp.setVisibility(View.GONE);
                    theActivity.imgSearchDeviceAp.setVisibility(View.VISIBLE);
                    theActivity.txtSearchDeviceAp.setText(R.string.searchedCamera);
                    theActivity.btnOnlyWifi.setVisibility(View.VISIBLE);
                    break;
                case SET_WIFI_OK:
                    theActivity.btnSetWifiOK.performClick();
                    break;
                case WIFICONNECTED:
                    theActivity.txtDeviceId.setText(theActivity.mWiFiHelper.getCurrentSSID());
                    break;
                case SUCCESS_3G: {
                    theActivity.progressBarWait3G.setVisibility(View.GONE);
                    theActivity.txtSet3GResult.setVisibility(View.GONE);
                    theActivity.txtSet3GResult.setText(theActivity.getString(R.string.set3GSuccess));
                    theActivity.localService.search3g(theActivity.deviceLocalInfo);
                    theActivity.jumpPlatformActivity();
                }
                break;
                case FAILED_3G: {
                    theActivity.progressBarWait3G.setVisibility(View.GONE);
                    theActivity.txtSet3GResult.setVisibility(View.VISIBLE);
                    theActivity.txtSet3GResult.setText(theActivity.getString(R.string.setFail));
                }
                break;
                default:
                    break;
            }
        }
    }

    class Button_OnClickListener implements OnClickListener {

        @SuppressWarnings("deprecation")
        @SuppressLint("NewApi")
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.btnReticleNext: {

                    jumpPlatformActivity();
                    reticleSelectedWifi = false;

                }
                break;
                case R.id.btnWifiLink: {
                    changeLayout(SET_WIFI);
                    reticleSelectedWifi = true;
                }
                break;
                case R.id.btn3gReticleLink:
                    changeLayout(RETICLE_LINK);
                    threeGSelectReticle = true;
                    break;
                case R.id.btn3GLink:
                    changeLayout(INTERNET_3G);
                    threeGSelectReticle = false;
                    break;
                case R.id.btnSet3GOK: {

                    if (!device3Ginfo.getsz3GUser().equals(edt3gUser.getText().toString()) ||
                            !device3Ginfo.getsz3GPWD().equals(edt3gpass.getText().toString()) ||
                            !device3Ginfo.getsz3GAPN().equals(edt3gapn.getText().toString()) ||
                            !device3Ginfo.getszDialNum().equals(edt3gphonenum.getText().toString())) {
                        Device3GInfo temp = new Device3GInfo(device3Ginfo.toByteBuffer());
                        temp.setsz3GUser(edt3gUser.getText().toString());
                        temp.setsz3GPWD(edt3gpass.getText().toString());
                        temp.setsz3GAPN(edt3gapn.getText().toString());
                        temp.setszDialNum(edt3gphonenum.getText().toString());

                        localService.setDevice3GParam(new DeviceLocalInfo(temp.toByteBuffer()));
                        progressBarWait3G.setVisibility(View.VISIBLE);
                        txtSet3GResult.setVisibility(View.VISIBLE);
                        txtSet3GResult.setText(getString(R.string.setting3G));
                        return;
                    }

                    jumpPlatformActivity();
                }
                break;
                case R.id.btnOnlyWifi:
                    changeLayout(SET_WIFI);
                    mWiFiHelper.stop();
                    break;
                case R.id.btnSelectWifi:
                    final Dialog dialog = new Dialog(GuideSetActivity.this, R.style.Erro_Dialog);
                    dialog.setContentView(R.layout.wifilist_dialog);
                    Button cancelBtn = (Button) dialog.findViewById(R.id.cancleBtn);
                    cancelBtn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
                    ListView listView = (ListView) dialog.findViewById(R.id.wifiListView);
                    wifiListAdapter.isSingleChoice = false;
                    listView.setAdapter(new wifiListAdapter(ssidList, GuideSetActivity.this, R.drawable.ic_switch_wifi_on));
                    listView.setOnItemClickListener(new ListView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int whichButton, long arg3) {
                            // TODO Auto-generated method stub
                            edtSSID.setText(ssidList.get(whichButton));
                            edtSSID.setTextColor(getResources().getColor(R.color.black));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case R.id.btnSetWifiOK:
                    SSID = edtSSID.getText().toString().trim();
                    Password = edtSSIDPwd.getText().toString().trim();
                    if (SSID.equals("") | Password.equals("")) {
                        Toast toast;
                        toast = Toast.makeText(getApplicationContext(), R.string.inputIncomplete, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        try {
                            //Log.d(TAG, "deviceId :" + deviceId);
                            deviceLocalInfo = localDevList.getDevice(deviceId).getLocalInfo();
                            deviceLocalInfo.setWiFiSSID(SSID);
                            deviceLocalInfo.setWiFiPwd(Password);
                            deviceLocalInfo.setEnableWiFi(1);
                            localService.setDeviceParam(deviceLocalInfo);
                            progressBarWait.setVisibility(View.VISIBLE);

                            jumpThread = new Thread(jumpRun);
                            jumpThread.start();
                        } catch (NullPointerException e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            if (localService == null) {
                                //Log.e(TAG, "localService is null");

                            } else {
                                retry = true;
                                localService.search();
                            }
                        }
                    }
                    break;
//			case R.id.btnExit:
//				appData.exit();
//				break;
            }
        }
    }
}
