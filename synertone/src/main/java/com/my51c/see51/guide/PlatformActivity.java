package com.my51c.see51.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.method.wifilist.WifiAdmin;
import com.method.wifilist.WifiConstant;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.AccountInfo;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.DeviceSee51Info;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.protocal.GvapPackage;
import com.my51c.see51.protocal.GvapXmlParser;
import com.my51c.see51.service.GVAPService;
import com.my51c.see51.service.GvapEvent;
import com.my51c.see51.service.GvapEvent.GvapEventListener;
import com.my51c.see51.ui.MainActivity;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;

public class PlatformActivity extends BaseActivity implements GvapEventListener, OnClickListener {
    private final static String TAG = "PlatformActivity";
    private static final int Connect_See51_Sucess = 2000;
    private static final int Bind_Sucess = 2001;
    private static final int Connect_See51_False = 4000;
    private static final int Bind_False = 4001;
    public static boolean isBack = false;
    public static GVAPService gvapService;
    private static AppData appData;
    private static String deviceId;
    public MyLocationListenner myListener;
    boolean needRefresh = false;
    private ImageView imgPla, imgAct;
    private ProgressBar proPla, proAct;
    private View viewStepBind;
    private Button btnVideo;
    private TextView textViewIsConnectSee51, textViewIsBindSuccess;
    private instHandler mHandler;
    private DeviceList deviceList;
    private String network = "wifi";
    private boolean isAp = false;
    private Thread netThread;
    private LinearLayout backLayout;
    //����
    private double latitude;
    //γ��
    private double longitude;
    private String locationStr;
    private LocationClient mLocClient;
    private boolean isLocationGeted = false;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (network.equals("ap")) {
                Log.i(TAG, "--Add by AP");

                appData.getGVAPService().logout();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo netinfo = cm.getActiveNetworkInfo();
                //Log.d("Platform", "wait getActiveNetworkInfo");
                while (netinfo == null) {
                    netinfo = cm.getActiveNetworkInfo();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                }

                //Log.d("Platform", "wait TYPE_WIFI");

                while (!netinfo.isConnected() || netinfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    try {
                        Thread.sleep(2000);
                        netinfo = cm.getActiveNetworkInfo();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                }
                WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                String id = mWifiManager.getConnectionInfo().getSSID();

                //Log.d("Platform", "wait match ssid");
                while (id == null || isMatch(id)) {
                    id = mWifiManager.getConnectionInfo().getSSID();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                }

            }
            //c85261000003
            Log.i(TAG, "gvapService start");
            if (gvapService != null) {
                gvapService.init();
                gvapService.start();
                gvapService.addGvapEventListener(PlatformActivity.this);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (gvapService != null)
                gvapService.login(new AccountInfo("guest", "guest"));
        }
    };

    private boolean isMatch(String id) {
        if (id.startsWith("\"")) {
            id = id.substring(1, id.length() - 1);
        }
        return id.matches("^\\d{12}$");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_platform);

//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		Drawable title_bg = getResources().getDrawable(R.drawable.title_bg);
//		actionBar.setBackgroundDrawable(title_bg);
        backLayout = (LinearLayout) findViewById(R.id.platformacy_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (WifiConstant.constant_WIFI_CHENGED) {
                    WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
                    if (WifiConstant.constant_SSID.equals(wifiAdmin.getSSID().replace("\"", ""))) {
                        WifiConstant.constant_WIFI_CHENGED = false;
                    }
                }
                isBack = true;
                finish();
            }
        });

        deviceId = getIntent().getStringExtra("DeviceId");
        network = getIntent().getStringExtra("network");
        System.out.println("--network:" + network);
        isAp = getIntent().getBooleanExtra("isAp", false);
        if (isAp) {
            final int id = WifiConstant.constant_NETWORK_ID;
            Log.i(TAG, "--Connect to wifi" + id);
//			new Thread(){
//				@Override
//				public void run() {
//					removeWifi(id);
//					appData.getGVAPService().stop();
//					appData.getGVAPService().start();
//					super.run();
//				}
//			}.start();
        }
        //Log.d("Platform", "network: " + network);
        if (network == null) {
            network = "wifi";
        }
        //Log.d("Platform", "network: " + network);
        Device device = new Device();
        DeviceLocalInfo lo = new DeviceLocalInfo();
        lo.setCamSerial(deviceId);
        device.setLocalInfo(lo);

        DeviceSee51Info see51Info = new DeviceSee51Info(deviceId);
        device.setSee51Info(see51Info);
        deviceList = new DeviceList();
        deviceList.add(deviceId, device);

        findView();
        mHandler = new instHandler(this);
        gvapService = new GVAPService();
        gvapService.bNetStatus = true;
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));

        MainActivity.FromPlatAcy = true;

        netThread = new Thread(runnable);
        netThread.start();

        initLocation();

    }

    public void removeWifi(int id) {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        WifiManager wifiManager = wifiAdmin.getWifiManager();
        wifiManager.removeNetwork(wifiAdmin.getNetworkId());
        wifiAdmin.closeWifi();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        wifiAdmin.openWifi();
        wifiManager.enableNetwork(id, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // gvapService.addGvapEventListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netThread.isAlive()) {
            netThread.interrupt();
        }
        gvapService.removeGvapEventListener(this);
        if (gvapService != null) {
            gvapService.release();
            gvapService = null;
        }
    }

    private void findView() {

        btnVideo = (Button) findViewById(R.id.btnVideo);

        btnVideo.setOnClickListener(this);

        imgPla = (ImageView) findViewById(R.id.imgPla);
        imgAct = (ImageView) findViewById(R.id.imgAct);

        proPla = (ProgressBar) findViewById(R.id.proPla);
        proAct = (ProgressBar) findViewById(R.id.proAct);

        textViewIsConnectSee51 = (TextView) findViewById(R.id.textViewIsConnectSee51);
        textViewIsBindSuccess = (TextView) findViewById(R.id.textViewIsBindSuccess);

        viewStepBind = findViewById(R.id.viewStepBind);

        viewStepBind.setVisibility(View.INVISIBLE);
    }

    private void onOperationSuccess(GvapCommand cmd, GvapPackage response) {
        Log.i(TAG, "onOperationSuccess  cmd = " + cmd);
        switch (cmd) {
            case CMD_LOGIN: {
                gvapService.getVersions();
                break;
            }
            case CMD_GET_VERSIONS:
                int ver = response.getIntegerParamWithDefault("pub-version", -1);
                deviceList.setServeVersion(ver);
                gvapService.getDeviceStatus(deviceList);
                break;
            case CMD_GET_DEVSTATUS:
                Log.i(TAG, "CMD_GET_DEVSTATUS");
                GvapXmlParser.parseDevInfo(new String(response.getContent()), deviceList);
                int ret = deviceList.getDevice(deviceId).getSee51Info().getStatus();
                Message msg = new Message();
                //Log.d("Platform", "onOperationSuccess  ret = " + ret);
                if (ret == 2) {
                    msg.what = Connect_See51_Sucess;
                    mHandler.sendMessage(msg);
                    //Log.d("Platform", "bind account: " + appData.getAccountInfo().getUsername());
                    //Log.d("Platform", "bind deviceId: " + deviceId);
                    gvapService.removeGvapEventListener(this);
                    gvapService.restartRegServer(); // ��������ע���������socket
                    gvapService.addGvapEventListener(this);
                    gvapService.bind(appData.getAccountInfo(), deviceList.getDevice(deviceId));
                } else {
                    // ֻҪû�յ�2����Ϊ����ͷû�����ӵ�ƽ̨����Ҫ������ѯ
                    // msg.what = Connect_See51_False;
                    // msg.obj = getString(R.string.guideCamDoNotConnectSee51);
                    // mHandler.sendMessage(msg);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (gvapService != null)
                                gvapService.getDeviceStatus(deviceList);
                        }
                    }).start();
                }
                break;
            case CMD_GET_DEVINFO:
                break;
            case CMD_GET_USRINFO:
            case CMD_UPDATE_USERINFO:
            case CMD_UPDATE_DEVINFO:
                Log.i(TAG, "CMD_UPDATE_DEVINFO");
                DeviceList devList = appData.getAccountInfo().getDevList();//bug:�б������°��豸
                for (Device dev : devList) {
                    String gpsStr = dev.getSee51Info().getLocation();
                    Log.i(TAG, "GPS��" + dev.getID() + ":" + gpsStr);
                }
                break;
            case CMD_REGISTER:
            case CMD_BIND:
                Log.i(TAG, "--onOperationSuccess:CMD_BIND");
                needRefresh = true;
                mHandler.sendEmptyMessage(Bind_Sucess);
                //�󶨳ɹ�������GPS
                if (gvapService.isInit()) {
                    Log.i(TAG, "�󶨳ɹ����ϱ�GPS��Ϣ" + deviceId + "--" + appData.getAccountInfo().getUsername());
                    gvapService.setLocation(deviceId, locationStr, appData.getAccountInfo());
                    Log.i(TAG, "�ϱ���ǰλ��" + locationStr);
                }
//			gvapService.release();
                break;
            case CMD_UNBIND:
            case CMD_HB:
            case CMD_NOTIFY_DEVSTATUS: {
                break;
            }
        }
    }

    private void onOperationFalse(GvapCommand cmd, GvapPackage response) {

        Message msg = new Message();
        if (cmd == null) {
            //Log.e("Platform", "onOperationFalse cmd == null");
            return;
        }
        Log.i(TAG, "onOperationFalse  cmd: " + cmd + "  retcode: " + response.getStatusCode());
        switch (cmd) {
            case CMD_LOGIN:
                MainActivity.openNum++;
                if (isAp) {
                    Log.i(TAG, "gvapService start");
                    if (gvapService != null) {
                        gvapService.init();
                        gvapService.start();
                        gvapService.addGvapEventListener(PlatformActivity.this);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (gvapService != null)
                        gvapService.login(new AccountInfo("guest", "guest"));
                    Log.i(TAG, "--����ʧ�ܣ�����(AP)");
                } else {
                    if (gvapService != null) {
                        gvapService.removeGvapEventListener(this);
                        gvapService.stop();
                        gvapService.release();
                        gvapService = new GVAPService();
                        gvapService.init();
                        gvapService.addGvapEventListener(this);
                        gvapService.start();
                        gvapService.login(new AccountInfo("guest", "guest"));
                    }
                    Log.i(TAG, "--����ʧ�ܣ�����(��AP)");
                }
                break;
            case CMD_GET_VERSIONS:
            case CMD_GET_DEVSTATUS: // ��ȡ�豸��Ϣ
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (gvapService != null)
                            gvapService.getDeviceStatus(deviceList);
                    }
                }).start();

                break;
            case CMD_GET_DEVINFO:
            case CMD_GET_USRINFO:
            case CMD_UPDATE_USERINFO:
            case CMD_UPDATE_DEVINFO:
                Log.i(TAG, "-------CMD_UPDATE_DEVINFO����ʧ��");
                break;
            case CMD_REGISTER:
                break;
            case CMD_BIND: {
                Log.i(TAG, "onOperationFalse:CMD_BIND");
                int retcode = response.getStatusCode();
                //Log.d("Platform", "retcode : " + retcode);
                switch (retcode) {
                    case 405:
                    case 406:
                        msg.what = Bind_False;
                        msg.obj = getString(R.string.guideAccountOrPwdError);
                        mHandler.sendMessage(msg);
                        break;
                    case 407:
                    case 411:
                        gvapService.getDeviceStatus(deviceList);
                        // msg.what = Bind_False;
                        // msg.obj = getString(R.string.guideCamDoNotLoginSee51);
                        // mHandler.sendMessage(msg);
                        break;
                    case 412:
                        if (deviceList.getDevice(deviceId).getSee51Info().getUsername().equals(appData.getAccountInfo().getUsername())) {
                            needRefresh = true;
                            mHandler.sendEmptyMessage(Bind_Sucess);
                        } else {
                            msg.what = Bind_False;
                            msg.obj = getString(R.string.guideCamHasBeenBind);
                            mHandler.sendMessage(msg);
                        }
                        break;
                    default:
                        break;
                }
            }
            break;
            case CMD_UNBIND:
            case CMD_HB:
            case CMD_NOTIFY_DEVSTATUS: {
                break;
            }
        }
    }

    private void onOperationTimeOut(GvapCommand cmd, GvapPackage response) {

        Log.i(TAG, "onOperationTimeOut  cmd == " + cmd);
        if (cmd == null) {
            //Log.e("Platform", "onOperationTimeOut cmd == null");
            return;
        }
        switch (cmd) {
            case CMD_LOGIN:
            case CMD_GET_VERSIONS:
            case CMD_GET_DEVSTATUS:
                MainActivity.openNum++;
                if (isAp) {
                    Log.i(TAG, "gvapService start");
                    if (gvapService != null) {
                        gvapService.init();
                        gvapService.start();
                        gvapService.addGvapEventListener(PlatformActivity.this);
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (gvapService != null)
                        gvapService.login(new AccountInfo("guest", "guest"));
                    Log.i(TAG, "----------������ʱ������(APģʽ)");
                } else {
                    if (gvapService != null) {
                        gvapService.removeGvapEventListener(this);
                        gvapService.stop();
                        gvapService.release();
                        gvapService = new GVAPService();
                        gvapService.init();
                        gvapService.addGvapEventListener(this);
                        gvapService.start();
                        gvapService.login(new AccountInfo("guest", "guest"));
                    }
                    Log.i(TAG, "----------������ʱ������(��APģʽ)");
                }

                break;
            case CMD_BIND:

                Log.i(TAG, "--onOperationTimeOut:CMD_BIND");
                if (gvapService != null) {
                    gvapService.removeGvapEventListener(this);
                    gvapService.restartRegServer(); // ��������ע���������socket
                    gvapService.addGvapEventListener(this);
                    gvapService.bind(appData.getAccountInfo(), deviceId);
                }
                break;
        }
    }

    @Override
    public void onGvapEvent(GvapEvent event) {
        // TODO Auto-generated method stub
        switch (event) {
            case OPERATION_SUCCESS: {
                Log.i(TAG, "--PlatformActivity�����ɹ�--onOperationSuccess");
                onOperationSuccess(event.getCommandID(), (GvapPackage) event.attach());
                break;
            }
            case OPERATION_FAILED:
                Log.i(TAG, "--����ʧ�ܣ�ʧ�ܵ�ԭ��һ��Ϊ�������ܾ�");
                Log.i(TAG, "--PlatformActivity����ʧ��--onOperationFalse");
                onOperationFalse(event.getCommandID(), (GvapPackage) event.attach());
                break;

            case CONNECT_TIMEOUT:
                Log.i(TAG, "--���ӷ�������ʱ");
                Log.i(TAG, "--PlatformActivity������ʱ--onOperationTimeOut");
                onOperationTimeOut(event.getCommandID(), (GvapPackage) event.attach());
                break;
            case OPERATION_TIMEOUT:
                Log.i(TAG, "--������ʱ��һ��������ԭ��");
                Log.i(TAG, "--PlatformActivity������ʱ--onOperationTimeOut");
                onOperationTimeOut(event.getCommandID(), (GvapPackage) event.attach());
                break;
            //case CONNECTION_RESET:
            case CONNECT_FAILED:
                Log.i(TAG, "--���ӷ�����ʧ��");
                Log.i(TAG, "--PlatformActivity������ʱ--onOperationTimeOut");
                onOperationTimeOut(event.getCommandID(), (GvapPackage) event.attach());
                break;
            case NETWORK_ERROR: {
                Log.i(TAG, "--�������");
                Log.i(TAG, "--PlatformActivity������ʱ--onOperationTimeOut");
                onOperationTimeOut(event.getCommandID(), (GvapPackage) event.attach());
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
//		case R.id.btnBack:
//			isBack = true;
//			finish();
//			break;
//		case R.id.btnExit:
//			appData.exit();
//			break;
            case R.id.btnVideo:

                // Intent intent = new Intent (this,MainActivity.class);
                // intent.putExtra("GuideHasLogin", true);

//			if (network.equals("wifi"))
//			{
//				needRefresh = false;
//			}
                sendBroadcast(new Intent(MainActivity.GUIDE_FINISH_ACTION));
                WifiConstant.constant_WIFI_CHENGED = false;
                appData.exit();
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "--keydown");
        if (WifiConstant.constant_WIFI_CHENGED) {
            WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
            if (WifiConstant.constant_SSID.equals(wifiAdmin.getSSID().replace("\"", ""))) {
                WifiConstant.constant_WIFI_CHENGED = false;
            }
        }
        isBack = true;
        finish();
        return false;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        MainActivity.FromPlatAcy = false;
        super.onStop();

    }

    private void initLocation() {
        myListener = new MyLocationListenner();
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//��GPS
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        if (mLocClient != null || mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }
    }

    static class instHandler extends Handler {
        WeakReference<PlatformActivity> mActivity;

        instHandler(PlatformActivity activity) {
            mActivity = new WeakReference<PlatformActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PlatformActivity theActivity = mActivity.get();
            switch (msg.what) {
                case Connect_See51_Sucess:
                    theActivity.proPla.setVisibility(View.GONE);
                    theActivity.imgPla.setBackgroundResource(R.drawable.right);
                    theActivity.textViewIsConnectSee51.setText(R.string.guideCamConnectSee51Sucess);
                    theActivity.viewStepBind.setVisibility(View.VISIBLE);

                    break;
                case Bind_Sucess:
                    theActivity.proAct.setVisibility(View.GONE);
                    theActivity.imgAct.setBackgroundResource(R.drawable.right);
                    theActivity.textViewIsBindSuccess.setText(R.string.guideCamHasAdded);
                    theActivity.btnVideo.setVisibility(View.VISIBLE);
                    break;

                case Connect_See51_False:
                    // theActivity.proPla.setVisibility(View.GONE);
                    // theActivity.imgPla.setBackgroundResource(R.drawable.error);
                    break;
                case Bind_False:
                    theActivity.proAct.setVisibility(View.GONE);
                    theActivity.imgAct.setBackgroundResource(R.drawable.error);
                    theActivity.textViewIsBindSuccess.setText((String) msg.obj);
                    theActivity.btnVideo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (!isLocationGeted) {
                locationStr = latitude + "," + longitude;
                Log.i(TAG, "��ȡ��γ�ȣ�" + locationStr);
                isLocationGeted = true;
            }
        }
    }
}
