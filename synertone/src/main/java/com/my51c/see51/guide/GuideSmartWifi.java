package com.my51c.see51.guide;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.method.wifilist.WifiAdmin;
import com.method.wifilist.WifiConstant;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GuideSmartWifi extends SherlockFragmentActivity implements OnClickListener, DeviceListListener, OnSetDeviceListener {

    private static final String TAG = "GuideSmartWifi";
    private static final int JUMP_TO_PLATFORMACY = 0;
    private static final int SEND_DEVMSG = 1;
    private static final int GETAPPDATA_FAILED = 2;
    private final int RESULT_SUCCESS = 1;
    private WifiManager mWifiManager;
    private String mConnectedSsid;
    private EditText editSSID;
    private EditText editpass;
    private ImageButton btnWifiList;
    private CompoundButton cbPassShow;
    private Button btnConnect;
    private List<ScanResult> wifilist;
    private String devId = null;
    private boolean isAp = false;
    private DeviceList localDevList;
    private LocalService localService;
    private DeviceLocalInfo deviceLocalInfo;
    private AppData appData;
    private String network = "wifi";
    private int networkid = 0;
    private String curssid = null;
    private Dialog waitDialog;
    private boolean isDevFinded = false;
    private boolean isWaiting = false;
    private boolean isFindFailed = false;
    private boolean isGetAppdata = false;
    //	private Timer FindTimer = null;
//	private TimerTask FindTimerTask = null;
    private int countNum = 0;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JUMP_TO_PLATFORMACY:
                    if (WifiConstant.constant_WIFI_OFF) {
                        if (!mWifiManager.isWifiEnabled()) {
                            Log.i(TAG, "--�ѹر�WIFI����ת");
                            WifiConstant.constant_WIFI_CHENGED = false;
                            jumpPlatformActivity();
                        } else {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(JUMP_TO_PLATFORMACY), 2000);
                            Log.i(TAG, "--δ�ر�Wifi�������ȴ�");
                        }
                    } else {
                        if (new WifiAdmin(getApplicationContext()).getSSID().replace("\"", "").equals(WifiConstant.constant_SSID)) {
                            Log.i(TAG, "--������WIFI����ת");
//						appData.getGVAPService().start();
                            WifiConstant.constant_WIFI_CHENGED = false;
                            jumpPlatformActivity();
                        } else {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(JUMP_TO_PLATFORMACY), 2000);
                            Log.i(TAG, "--δ����Wifi�������ȴ�");
                        }
                    }

                    break;
                case SEND_DEVMSG:
                    if (isDevFinded) {
                        Log.i(TAG, "--�ҵ��豸�����͹㲥");
                        sendDeviceInfo();
                    } else {

                        countNum++;
                        if (countNum < 8) {
                            findDevice();
                            Log.i(TAG, "--�豸δ�ҵ�,find again");
                        } else {
                            Log.i(TAG, "--time out ǿ����ת");
//						waitDialog.dismiss();
                            sendDeviceInfo();
                        }


                    }
                    break;
                case GETAPPDATA_FAILED:
                    findDevice();
                    break;


                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_smartwifi);
        devId = getIntent().getStringExtra("DeviceId");
        isAp = getIntent().getBooleanExtra("isAp", false);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.wifi_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        ((AppData) getApplication()).addActivity(new WeakReference<Activity>(this));
//		getAppData();

        if (isAp) {
            getAppData();
            networkid = WifiConstant.constant_NETWORK_ID;
            curssid = WifiConstant.constant_SSID;
        } else {
            getAppData_Voice();
        }

        editSSID = (EditText) findViewById(R.id.editEssid);
        editpass = (EditText) findViewById(R.id.editWifipassword);
        btnWifiList = (ImageButton) findViewById(R.id.btnWifilist);
        cbPassShow = (CompoundButton) findViewById(R.id.checkpasswdshow);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        btnWifiList.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
//		editpass.setText("20140304");

        cbPassShow.setChecked(false);
        cbPassShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    editpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        if (mWifiManager.isWifiEnabled()) {
            mConnectedSsid = mWifiManager.getConnectionInfo().getSSID();

            int i = mConnectedSsid.length();
            if (i == 0)
                return;

            if ((mConnectedSsid.startsWith("\"")) && (mConnectedSsid.endsWith("\""))) {
                String str2 = mConnectedSsid;
                int j = i + -1;
                String str3 = str2.substring(1, j);
                mConnectedSsid = str3;
            }

            editSSID.setText(mConnectedSsid);
            editSSID.setSelection(mConnectedSsid.length());
            if (isAp) {
                editSSID.setText(curssid);
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_SUCCESS) {
            mConnectedSsid = arg2.getStringExtra("ssid");
            editSSID.setText(mConnectedSsid);
        }
    }

    public void getAppData_Voice() {
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));

        localService = appData.getLocalService();

        localDevList = appData.getLocalList();
        localDevList.addListListener(this);

        try {
            deviceLocalInfo = localDevList.getDevice(devId).getLocalInfo();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            localDevList.clear();
            localService.search();
            e.printStackTrace();
        }
    }

    public void getAppData() {
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));

        localService = appData.getLocalService();

        localDevList = appData.getLocalList();
        localDevList.addListListener(this);

        try {
            Log.i(TAG, "--devId:" + devId);
            deviceLocalInfo = localDevList.getDevice(devId).getLocalInfo();
            isGetAppdata = true;
            Log.i(TAG, "--getAppData success");
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            localDevList.clear();
            localService.search();
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "--getAppData again");
                    getAppData();//����ȡΪ�գ����»�ȡ
                }
            }, 1000);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnWifilist: {
                Intent intent = new Intent(GuideSmartWifi.this, CustWifiDialog.class);
                startActivityForResult(intent, RESULT_SUCCESS);
            }
            break;

            case R.id.btnConnect: {
//			getAppData();
                if (editSSID.getText().toString().equals("") | editpass.getText().toString().equals("")) {
                    Toast toast;
                    toast = Toast.makeText(getApplicationContext(), R.string.inputIncomplete, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    if (!isAp) {
                        Intent intent = new Intent(GuideSmartWifi.this, GuideSmartSound.class);
                        intent.putExtra("DeviceId", devId);
                        wifilist = mWifiManager.getScanResults();
                        intent.putExtra("essid", editSSID.getText().toString());
                        intent.putExtra("passwd", editpass.getText().toString());
                        intent.putExtra("capabilities", "");
                        for (ScanResult item : wifilist) {
                            if (item.SSID.equalsIgnoreCase(editSSID.getText().toString())) {
                                intent.putExtra("capabilities", item.capabilities);
                                break;
                            }
                        }

                        startActivity(intent);
                    } else {
//				show the progressbar
                        Log.i(TAG, "----add by ap");
                        showWaitingDialog();
                        findDevice();
                    }


                }
            }
            break;

            default:
                break;
        }
    }

    public boolean findDevice() {
//		getAppData();
        if (!isGetAppdata) {
            mHandler.sendEmptyMessageDelayed(GETAPPDATA_FAILED, 1500);
        } else {
            isDevFinded = false;
            if (!isDevFinded) {

                localDevList = appData.getLocalList();//bug �豸δ�ҵ� -->cause:localDevListδ��apģʽ�¸���

//				Log.i(TAG, "--"+localDevList.toString());
                synchronized (localDevList) {
                    for (Device device : localDevList) {
                        DeviceLocalInfo devInfo = device.getLocalInfo();
                        if (devInfo != null) {
                            //Log.d(TAG, "deviceId:" + deviceId + ",	devInfo.getCamSerial()" + devInfo.getCamSerial());
                            if (devId.equals(devInfo.getCamSerial())) {
                                //Log.d(TAG, "devInfo.getCamSerial()" + devInfo.getCamSerial());
                                isDevFinded = true;
                                deviceLocalInfo = localDevList.getDevice(devId).getLocalInfo();
                                localService.setListener(this);
                                Log.i(TAG, "--find Device Success");
                                break;
                            }
                        }
                    }
                }
                mHandler.sendEmptyMessageDelayed(SEND_DEVMSG, 1000);//1s�����ҵ��豸->sendDevInfo(),����->findDevice
            }
        }

        return isDevFinded;
    }


    private void sendDeviceInfo() {
        try {
            deviceLocalInfo = localDevList.getDevice(devId).getLocalInfo();
            deviceLocalInfo.setWiFiSSID(editSSID.getText().toString());
            deviceLocalInfo.setWiFiPwd(editpass.getText().toString());
            deviceLocalInfo.setEnableWiFi(1);
            localService.setDeviceParam(deviceLocalInfo);//

            network = "ap";

        } catch (NullPointerException e) {
            // TODO: handle exception
            e.printStackTrace();
            if (localService == null) {
                //Log.e(TAG, "localService is null");

            } else {
                localService.search();
            }
        }
    }

    @Override
    public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
        // TODO Auto-generated method stub
        Log.i(TAG, "--�㲥���ͳɹ�,���ӻ�ԭwifi��׼����ת");
        removeWifi(WifiConstant.constant_NETWORK_ID);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(JUMP_TO_PLATFORMACY), 2000);//���ӻ�ԭwifi��׼����ת
    }

    @Override
    public void onSetDeviceFailed(DeviceLocalInfo devInfo) {
        // TODO Auto-generated method stub
        Log.i(TAG, "--�㲥����ʧ��,���·���");
        sendDeviceInfo();
    }

    public void jumpPlatformActivity() {
        Intent intent = new Intent(GuideSmartWifi.this, PlatformActivity.class);
        intent.putExtra("isAp", true);
        intent.putExtra("network", network);
        intent.putExtra("DeviceId", devId);
        System.out.println("--GuideSmartWifi" + networkid);
        startActivity(intent);
//		this.localDevList.removeListener(this);
//		close progressbar

        waitDialog.dismiss();
        finish();
    }

    public void removeWifi(int id) {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        WifiManager wifiManager = wifiAdmin.getWifiManager();

        if (WifiConstant.constant_WIFI_OFF) {        //��ԭwifi״̬
            wifiAdmin.closeWifi();
        } else {
            wifiManager.removeNetwork(wifiAdmin.getNetworkId());
            wifiManager.enableNetwork(id, true);
        }

    }

    @Override
    public void onListUpdate() {
        // TODO Auto-generated method stub
        Log.i(TAG, "--onListUpdate");
    }


    @Override
    protected void onStop() {
        Log.i(TAG, "--------onStop");
        this.localDevList.removeListener(this);
        this.localService.setListener(null);
        super.onStop();
    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    public void showWaitingDialog() {
        if (!isWaiting) {
            waitDialog = new Dialog(GuideSmartWifi.this, R.style.Login_Dialog);
            waitDialog.setContentView(R.layout.waiting_dialog);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        finish();
                    return false;
                }
            });
            waitDialog.show();
            isWaiting = true;
        }
    }
}
