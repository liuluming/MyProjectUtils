package com.my51c.see51.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.Device3GShortParam;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.SelectionDevice;
import com.my51c.see51.listener.OnGet3GInfoListener;
import com.my51c.see51.listener.OnGetBLPInfoListener;
import com.my51c.see51.listener.OnGetDevInfoListener;
import com.my51c.see51.listener.OnGetRFInfoListener;
import com.my51c.see51.media.MediaStreamFactory;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.protocal.RFPackage;
import com.my51c.see51.service.GvapEvent;
import com.my51c.see51.service.GvapEvent.GvapEventListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.widget.MyLoadingDialog;
import com.synertone.netAssistant.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//bm-add-1101 
//bm-add-1101

public class DeviceInfoActivity extends SherlockFragmentActivity implements GvapEventListener, OnClickListener {
    private static final String TAG = "DeviceInfoActivity";
    private static final int MSG_UNBINDED_SUCCESS = 0;
    private static final int MSG_UNBINDED_FAILED = 1;
    private static final int MSG_DEVICE_OFFLINE = 2;
    private static final int MSG_DEVICE_INFO_NOT_READY = 3;
    private static final int MSG_DEVICE_INFO_READY = 4;
    private static final int MSG_DEVICE_INFO_GET_SUCCESS = 5;
    private static final int MSG_DEVICE_INFO_GET_FAIL = 6;
    private static final int MSG_DEVICE_INFO_GET_OVERTIME = 7;
    private static final int MSG_DEVICE_NAME_MODIFY_SUCCESS = 8;
    private static final int MSG_DEVICE_NAME_MODIFY_FAILED = 9;
    private static final int MSG_DEVICE_RF_INFO_GET_FAIL = 10;
    private static final int MSG_DEVICE_3G_INFO_GET_SUCCESS = 11;
    private static final int MSG_DEVICE_3G_INFO_GET_FAIL = 12;
    private static final int MSG_DEVICE_LOCATION_SET_SUCCESS = 13;
    private static final int MSG_DEVICE_LOCATION_SET_FAIL = 14;
    public ArrayList<String> blpList = null;
    public ArrayList<String> curtainList = null;
    public MyLocationListenner myListener;
    boolean isLocal = true;
    InputMethodManager inputMethodManager;
    MyLoadingDialog waitdialog;
    TimeOutAsyncTask asyncTask;
    private String deviceID;
    private String version;
    private String name;
    private TextView nameTextView;
    private TextView idTextView;
    private TextView txtSoftVersion;
    private TextView txtHardwareVersion;
    private Button removeBtn;
    private ImageView img;
    private ImageView btnModifyName;
    private String snapFilePath;
    private LinearLayout backgroud;
    private LinearLayout sdcardBtn;
    private LinearLayout cloudrecordBtn;
    private LinearLayout rfdeviceBtn;
    private LinearLayout settingBtn;
    private String strModifyname;
    private LocalService localService;
    private RemoteInteractionStreamer devInfoMediaStream;
    private AppData appData;
    private Device devInfoDevice;
    private boolean bdevclick = false;
    private boolean brfclick = false;
    private boolean mb3gdevice = false;
    private LinearLayout backLayout, renameLayout;
    private Button locationBtn;
    //锟斤拷锟斤拷
    private double latitude;
    //纬锟斤拷
    private double longitude;
    private String locationStr;
    private LocationClient mLocClient;
    private boolean isLocationGeted = false;
    private boolean isNameChange = false;
    private OnGetBLPInfoListener mOnGetBlpInfoListener = new OnGetBLPInfoListener() {

        @Override
        public void onGetBLPInfoFailed() {
        }

        @Override
        public void onGetBLPInfoSuccess(byte[] devbuf) {
            // TODO Auto-generated method stub
            if (devInfoDevice != null) {
                String parsestr;

                parsestr = byteToString(devbuf);
                parseBLPstr(parsestr);
                for (int i = 0; i < blpList.size(); i++) {
                    System.out.println(blpList.get(i));
                }
            }

//			if(brfclick == true)
//			{
//				jumptorfdevice();
//			}
//			brfclick = false;
//			cancelwaitdialog();
        }
    };
    //----bm-modi-1101 begain
    private MyHandler devInfoHandler = new MyHandler(this);
    private OnGetDevInfoListener mOnGetDevInfoListener = new OnGetDevInfoListener() {

        @Override
        public void onGetDevInfoFailed() {
            // TODO Auto-generated method stub
            cancelwaitdialog();
            devInfoHandler.sendEmptyMessage(MSG_DEVICE_INFO_GET_FAIL);
            bdevclick = false;
        }

        @Override
        public void onGetDevInfoSuccess(byte[] devbuf) {
            // TODO Auto-generated method stub

            if (devInfoDevice != null) {
                ByteBuffer tempinfo = ByteBuffer.wrap(devbuf, 0, devbuf.length);
                devInfoDevice.setLocalInfo(new DeviceLocalInfo(tempinfo));
            }

            if (mb3gdevice == false) {
                if (bdevclick == true) {
                    jumptodevinfo();
                }

                bdevclick = false;
                cancelwaitdialog();
            } else {
                if (devInfoDevice.get3GParam() != null) {
                    if (bdevclick == true) {
                        jumptodevinfo();
                    }

                    bdevclick = false;
                    cancelwaitdialog();
                } else {
                    devInfoMediaStream.get3GDeviceInfo();
                }
            }
        }
    };
    private OnGetRFInfoListener mOnGetRFInfoListener = new OnGetRFInfoListener() {

        @Override
        public void onGetRFInfoFailed() {
            cancelwaitdialog();

            devInfoHandler.sendEmptyMessage(MSG_DEVICE_RF_INFO_GET_FAIL);
        }

        @Override
        public void onGetRFInfoSuccess(byte[] devbuf) {
            // TODO Auto-generated method stub

            if (devInfoDevice != null) {
                String parsestr;

                parsestr = byteToString(devbuf);
                devInfoDevice.setRFInfo(new RFPackage(parsestr));
            }

            if (brfclick == true) {
                jumptorfdevice();
            }
            brfclick = false;
            cancelwaitdialog();
        }
    };
    private OnGet3GInfoListener mOnGet3GInfoListener = new OnGet3GInfoListener() {

        @Override
        public void onGet3GInfoFailed() {
            // TODO Auto-generated method stub
            cancelwaitdialog();
            devInfoHandler.sendEmptyMessage(MSG_DEVICE_3G_INFO_GET_FAIL);
        }

        @Override
        public void onGet3GInfoSuccess(byte[] devbuf) {
            // TODO Auto-generated method stub
            if (devInfoDevice != null) {
                devInfoDevice.set3GParam(new Device3GShortParam(ByteBuffer.wrap(devbuf)));
            }

            if (devInfoDevice.getLocalInfo() == null) {
                devInfoMediaStream.getDevInfo();
                return;
            }

            if (bdevclick == true) {
                jumptodevinfo();
                bdevclick = false;
                cancelwaitdialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devinfo_acy);
        findView();
        Bundle bundle = getIntent().getExtras();
        deviceID = bundle.getString("id");
        name = bundle.getString("name");
        isLocal = bundle.getBoolean("isLocal");
        version = bundle.getString("version");
        String[] versions = version.split("/");
        txtHardwareVersion.setText(getString(R.string.hardwareversion) + versions[0]);
        txtSoftVersion.setText(getString(R.string.softwareversion) + versions[1]);
        nameTextView.setText(name);
        idTextView.setText(deviceID);

        localService = ((AppData) getApplication()).getLocalService();
        appData = (AppData) getApplication();

        snapFilePath = AppData.getWokringPath();
        snapFilePath += "snapshot" + File.separator + deviceID + ".jpg";
        File file = new File(snapFilePath);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(snapFilePath, options);
            img.setImageBitmap(bitmap);
        }

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//锟斤拷锟斤拷锟斤拷锟斤拷锟�
        waitdialog = new MyLoadingDialog(this);
        waitdialog.setCancelable(true);
        waitdialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        initLocation();
    }

    public void findView() {
        nameTextView = (TextView) findViewById(R.id.devinfo_devName);
        nameTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        renameLayout = (LinearLayout) findViewById(R.id.renameLayout);
        backgroud = (LinearLayout) findViewById(R.id.deviceInfo_panel);
        idTextView = (TextView) findViewById(R.id.serialNumberGvap);
        removeBtn = (Button) findViewById(R.id.btn_removeDevice);
        txtSoftVersion = (TextView) findViewById(R.id.txtSoftVersion);
        txtHardwareVersion = (TextView) findViewById(R.id.txtHardwareVersion);
        sdcardBtn = (LinearLayout) findViewById(R.id.btn_sdcard_record);
        cloudrecordBtn = (LinearLayout) findViewById(R.id.btn_cloud_record);
        rfdeviceBtn = (LinearLayout) findViewById(R.id.btn_rfdevice);
        settingBtn = (LinearLayout) findViewById(R.id.btn_settings);
        backLayout = (LinearLayout) findViewById(R.id.back_layout);
        img = (ImageView) findViewById(R.id.snapshot);
        locationBtn = (Button) findViewById(R.id.btn_update_location);
        btnModifyName = (ImageView) findViewById(R.id.remotedevice_modifyname);

        renameLayout.setOnClickListener(this);
        backgroud.setOnClickListener(this);
        idTextView.setOnClickListener(this);
        removeBtn.setOnClickListener(this);
        sdcardBtn.setOnClickListener(this);
        cloudrecordBtn.setOnClickListener(this);
        rfdeviceBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        parsedevicetype(deviceID);

        if (isLocal) {
            removeBtn.setVisibility(View.GONE);
            locationBtn.setVisibility(View.GONE);
            btnModifyName.setVisibility(View.GONE);
            cloudrecordBtn.setVisibility(View.INVISIBLE);
            devInfoDevice = appData.getLocalList().getDevice(deviceID);
        } else {
            removeBtn.setVisibility(View.VISIBLE);
            locationBtn.setVisibility(View.VISIBLE);
            btnModifyName.setVisibility(View.VISIBLE);
            devInfoDevice = appData.getAccountInfo().getCurrentList().getDevice(deviceID);
        }

        devInfoMediaStream = appData.getRemoteInteractionStreamer();
        if (devInfoMediaStream == null) {
            createRemoteOperaction();
        } else {
            if (!deviceID.equals(devInfoMediaStream.getDevId())) {
                createRemoteOperaction();
            }
        }

        if (devInfoMediaStream != null) {
            if (!isLocal) {
                devInfoMediaStream.setOnGetDevInfoListener(mOnGetDevInfoListener);
                if (mb3gdevice) {
                    devInfoMediaStream.setOnGet3GInfoListener(mOnGet3GInfoListener);
                }
            }

            devInfoMediaStream.setOnGetRFInfoListener(mOnGetRFInfoListener);
            devInfoMediaStream.setOnGetBLPInfoListener(mOnGetBlpInfoListener);
            //devInfoMediaStream.getRFDeviceInfo();
            //devInfoMediaStream.getDevInfo();
        }

        bdevclick = false;
        brfclick = false;

    }

    private void createRemoteOperaction() {
        if (devInfoDevice == null)
            return;

        Map<String, String> paramp = new HashMap<String, String>();
        paramp.put("UserName", "admin");
        paramp.put("Password", "admin");
        paramp.put("Id", devInfoDevice.getID());

        Log.i(TAG, "createRemoteOperaction:deviceID" + deviceID + "-url:" + devInfoDevice.getPlayURL());

        if (isLocal) {
            devInfoMediaStream = new RemoteInteractionStreamer(devInfoDevice.getPlayURL(), paramp);
        } else {
            devInfoMediaStream = MediaStreamFactory.createRemoteInteractionMediaStreamer(devInfoDevice.getPlayURL(), paramp);
        }

        if (devInfoMediaStream != null) {
            appData.setRemoteInteractionStreamer(devInfoMediaStream);
            devInfoMediaStream.open();
            devInfoMediaStream.setDevId(deviceID);
        } else {
            appData.setRemoteInteractionStreamer(null);
        }
    }

    @SuppressLint("NewApi")
    public void parsedevicetype(String minID) {
        if (minID.isEmpty() || minID.equals(""))
            return;

        String strType1 = minID.substring(0, 4);
        String strType2 = minID.substring(0, 3);
        mb3gdevice = strType1.equals("3321") || strType1.equals("3322")
                || strType1.equals("3421") || strType1.equals("3422")
                || strType2.equals("a02") || strType2.equals("a42")
                || strType2.equals("a82") || strType2.equals("a83")
                || strType2.equals("a84");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        return super.onCreateOptionsMenu(menu);
    }

    private void startwaitdialog() {
        asyncTask = new TimeOutAsyncTask(this); //bm-modi-1101
        asyncTask.execute(0);
        waitdialog.show();
    }

    private void cancelwaitdialog() {
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
            asyncTask = null;    //bm-add-1101
        }
        //pd.dismiss();
        waitdialog.dismiss();
    }

    protected String byteToString(byte[] src) {
        int len = 0;
        for (; len < src.length; len++) {
            if (src[len] == 0) {
                break;
            }
        }
        return new String(src, 0, len);
    }

    public void parseBLPstr(String buf) {
        blpList = new ArrayList<String>();
        String tempStr = new String(buf);
        String[] lineArray = tempStr.split("#");
        for (int i = 0; i < lineArray.length; i++) {
            blpList.add(lineArray[i]);
        }
    }

    public void jumptodevinfo() {

        Intent intent = new Intent();
        intent.putExtra("id", deviceID);
        intent.setClass(DeviceInfoActivity.this, LocalSettingActivity.class);
        if (isLocal) {
            intent.putExtra("isLocal", true);
        } else {
            intent.putExtra("isLocal", false);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    public void jumptorfdevice() {
        Intent intent = new Intent();
        intent.putExtra("id", deviceID);
        if (isLocal) {
            intent.putExtra("isLocal", true);
        } else {
            intent.putExtra("isLocal", false);
        }
        intent.setClass(DeviceInfoActivity.this, RFDeviceInfoActivity.class);
        intent.putExtra("blpList", blpList);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do whatever you want, e.g. finish()
                backMainActivity();
                break;

            default: {

            }
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backMainActivity();
        }
        return false;
    }

    public void backMainActivity() {
        if (devInfoMediaStream != null) {
            devInfoMediaStream.close();
            devInfoMediaStream = null;
        }
        appData.setRemoteInteractionStreamer(null);

        DeviceInfoActivity.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void onRemoveDevice() {
        final Dialog delDialog = new Dialog(DeviceInfoActivity.this, R.style.Erro_Dialog);
        delDialog.setContentView(R.layout.del_dialog);
        Button sure = (Button) delDialog.findViewById(R.id.del_ok);
        sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isLocal) {

                } else {

                    Device dev = appData.getAccountInfo().getCurrentList()
                            .getDevice(deviceID);
                    appData.getGVAPService().removeGvapEventListener(DeviceInfoActivity.this);
                    appData.getGVAPService().restartRegServer();
                    appData.getGVAPService().addGvapEventListener(DeviceInfoActivity.this);
                    appData.getGVAPService().unbind(
                            appData.getAccountInfo(),
                            appData.getAccountInfo().getCurrentList()
                                    .getDevice(deviceID));

                    SelectionDevice selDev = new SelectionDevice();
                    selDev.setDeviceid(deviceID);
                    selDev.setLocal(false);

                    if (dev != null && dev.isbRemoteSelected()) {
                        dev.setbRemoteSelected(false);
                        appData.removeSelectDev(selDev);
                    }
                }
                delDialog.dismiss();
            }
        });
        Button cancel = (Button) delDialog.findViewById(R.id.del_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                delDialog.dismiss();
            }
        });
        delDialog.show();
    }

    @Override
    public void onClick(View view) {
        //Log.d("DeviceInfoActivity", "onClick " + view.getId());
        // TODO Auto-generated method stub

        switch (view.getId()) {
            case R.id.back_layout:
                backMainActivity();
                break;
            case R.id.renameLayout: {
                if (isLocal) {
                    return;
                }

                try {
                    appData.getGVAPService().removeGvapEventListener(DeviceInfoActivity.this);
                    appData.getGVAPService().restartRegServer();
                    appData.getGVAPService().addGvapEventListener(DeviceInfoActivity.this);
                    final Dialog renameDialog = new Dialog(DeviceInfoActivity.this, R.style.Erro_Dialog);
                    renameDialog.setContentView(R.layout.rename_dialog);
                    final EditText rename = (EditText) renameDialog.findViewById(R.id.rename_Edit);
                    Button ok = (Button) renameDialog.findViewById(R.id.rename_ok);
                    Button cancel = (Button) renameDialog.findViewById(R.id.rename_cancel);
                    ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            String regname = rename.getText().toString();
                            if (regname.length() < 0 || regname.length() >= 20) {
                                Toast.makeText(getApplicationContext(), getString(R.string.devicenamemodifylimit), 1000).show();
                                return;
                            }

                            strModifyname = rename.getText().toString();
                            if (strModifyname.equals(name)) {
                                Toast.makeText(getApplicationContext(), getString(R.string.changedevnamesuccess), 1000).show();
                            } else {
                                isNameChange = true;
                                appData.getGVAPService().removeGvapEventListener(DeviceInfoActivity.this);
                                appData.getGVAPService().restartRegServer();
                                appData.getGVAPService().addGvapEventListener(DeviceInfoActivity.this);
                                appData.getGVAPService().changeDevName(deviceID, strModifyname, appData.getAccountInfo());
                            }
                            renameDialog.dismiss();
                        }

                    });
                    cancel.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            renameDialog.dismiss();
                        }
                    });

                    renameDialog.show();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            break;
            case R.id.btn_removeDevice:
                onRemoveDevice();
                break;
            case R.id.btn_sdcard_record: {
                if (devInfoMediaStream == null) {
                    devInfoHandler.sendEmptyMessage(MSG_DEVICE_OFFLINE);
                    return;
                }

                Intent intent = new Intent();
                if (deviceID.substring(0, 1).equals("d")) {
                    intent.setClass(DeviceInfoActivity.this, SDRecordNVRActivity.class);
                } else {
                    intent.putExtra("isNVR", false);
                    intent.setClass(DeviceInfoActivity.this, SDRecordFolderActivity.class);
                }
                intent.putExtra("isLocal", isLocal);
                intent.putExtra("id", deviceID);
                intent.putExtra("url", devInfoDevice.getPlayURL());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
            break;
            case R.id.btn_cloud_record: {
                if (isLocal)
                    break;

                Intent intent = new Intent();
                intent.putExtra("id", deviceID);
                intent.setClass(DeviceInfoActivity.this, CloudRecordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
            break;
            case R.id.btn_rfdevice: {
                brfclick = true;
                Intent intent = new Intent();
                intent.putExtra("id", deviceID);
                if (isLocal) {
                    intent.putExtra("isLocal", true);
                } else {
                    intent.putExtra("isLocal", false);
                }
                intent.putExtra("blpList", blpList);
                intent.setClass(DeviceInfoActivity.this, RFDeviceInfoActivity.class);

                if (devInfoMediaStream == null) {
                    devInfoHandler.sendEmptyMessage(MSG_DEVICE_OFFLINE);
                    return;
                } else if (devInfoDevice.getRFInfo() == null) {
                    devInfoMediaStream.getRFDeviceInfo();
                    devInfoHandler.sendEmptyMessage(MSG_DEVICE_INFO_NOT_READY);
                    return;
                }
                if (blpList == null) {
                    devInfoMediaStream.getBLPDeviceInfo();
                }
                if (curtainList == null) {
                    devInfoMediaStream.getCurtainInfo();
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
            break;
            case R.id.btn_settings: {
                Intent intent = new Intent();
                intent.putExtra("id", deviceID);
                intent.setClass(DeviceInfoActivity.this, LocalSettingActivity.class);
                if (isLocal) {
                    intent.putExtra("isLocal", true);
                } else {
                    bdevclick = true;
                    if (devInfoMediaStream == null) {
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_OFFLINE);
                        return;
                    } else if (devInfoDevice.getLocalInfo() == null || (mb3gdevice && devInfoDevice.get3GParam() == null)) {
                        devInfoMediaStream.getDevInfo();
                        devInfoMediaStream.get3GDeviceInfo();
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_INFO_NOT_READY);
                        return;
                    } else {
                        intent.putExtra("isLocal", false);
                    }
                }

                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

            }
            break;
            case R.id.btn_update_location:
                appData.getGVAPService().removeGvapEventListener(DeviceInfoActivity.this);
                appData.getGVAPService().restartRegServer();
                appData.getGVAPService().addGvapEventListener(DeviceInfoActivity.this);
                appData.getGVAPService().setLocation(deviceID, locationStr, appData.getAccountInfo());
                break;
            default:
                // finishEditName();
                break;
        }
    }

    public boolean isLocalDevice(String deviceId) {
        DeviceList localDevList = ((AppData) this.getApplication()).getLocalList();
        Device device = localDevList.getDevice(deviceId);
        if (device != null) {
            localService = ((AppData) this.getApplication()).getLocalService();
            return true;
        } else
            return false;
    }

    @Override
    public void onGvapEvent(GvapEvent event) {
        // TODO Auto-generated method stub
        switch (event) {

            case OPERATION_SUCCESS:
                if (event.getCommandID() == GvapCommand.CMD_UNBIND) {
                    appData.getAccountInfo().getDevList().clear();
                    appData.getGVAPService().getDeviceList();
                    devInfoHandler.sendEmptyMessage(MSG_UNBINDED_SUCCESS);
                }

                if (event.getCommandID() == GvapCommand.CMD_UPDATE_DEVINFO) {
                    Log.i(TAG, "CMD_UPDATE_DEVINFO");
                    appData.getAccountInfo().getCurrentList().getDevice(deviceID).getSee51Info().setDeviceName(strModifyname);
                    //appData.getAccountInfo().getDevList().clear();
                    appData.getGVAPService().getDeviceList();

                    if (isNameChange) {
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_NAME_MODIFY_SUCCESS);
                    } else {
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_LOCATION_SET_SUCCESS);
                    }

                    isNameChange = false;

                }

                break;
            case OPERATION_TIMEOUT:
            case OPERATION_FAILED:
            case CONNECTION_RESET:
            case CONNECT_TIMEOUT:
            case CONNECT_FAILED:
            case NETWORK_ERROR:
                if (event.getCommandID() == GvapCommand.CMD_UNBIND) {
                    devInfoHandler.sendEmptyMessage(MSG_UNBINDED_FAILED);
                }

                if (event.getCommandID() == GvapCommand.CMD_UPDATE_DEVINFO) {
                    if (isNameChange) {
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_NAME_MODIFY_FAILED);
                    } else {
                        devInfoHandler.sendEmptyMessage(MSG_DEVICE_LOCATION_SET_FAIL);
                    }
                    isNameChange = false;
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //devInfoMediaStream.close();
        //devInfoMediaStream = null;
        super.onStop();
        cancelwaitdialog();
    }
//----bm-modi-1101 end

    //--bm-modi-1101 begain
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        devInfoHandler.removeCallbacksAndMessages(null);

        if (mLocClient != null) {
            mLocClient.unRegisterLocationListener(myListener);
        }

        if (devInfoMediaStream != null) {
            devInfoMediaStream.setOnGetRFInfoListener(null);
            devInfoMediaStream.setOnGetBLPInfoListener(null);
            devInfoMediaStream = null;
        }
    }

    private void initLocation() {
        myListener = new MyLocationListenner();
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//锟斤拷GPS
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        if (mLocClient != null || mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<DeviceInfoActivity> mRef;

        public MyHandler(DeviceInfoActivity mAct) {
            mRef = new WeakReference<DeviceInfoActivity>(mAct);
        }

        @Override
        public void handleMessage(Message message) {

            if (mRef == null)
                return;

            DeviceInfoActivity activity = mRef.get();
            if (activity != null) {
                switch (message.what) {
                    case MSG_UNBINDED_SUCCESS:
                        Toast.makeText(activity.getApplicationContext(),
                                activity.getString(R.string.unbindSuccess),
                                Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;
                    case MSG_UNBINDED_FAILED:
                        Toast.makeText(activity.getApplicationContext(),
                                activity.getString(R.string.unbindFailed),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_DEVICE_INFO_NOT_READY: {
                        activity.startwaitdialog();
                        //Toast.makeText(getApplicationContext(), getString(R.string.msgdeviceinfonotready), 1000).show();
                    }
                    break;
                    case MSG_DEVICE_RF_INFO_GET_FAIL: {
                        if (activity.brfclick == true) {
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msgdeviceinfogetfail), 1000).show();
                            Intent intent = new Intent();
                            intent.putExtra("id", activity.deviceID);
                            if (activity.isLocal) {
                                intent.putExtra("isLocal", true);
                            } else {
                                intent.putExtra("isLocal", false);
                            }
                            intent.setClass(activity, RFDeviceInfoActivity.class);

                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                        }
                        activity.brfclick = false;

                    }
                    break;

                    case MSG_DEVICE_INFO_GET_FAIL: {
                        if (activity.bdevclick == true) {
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msgdeviceinfogetfail), 1000).show();
                        }
                        activity.bdevclick = false;
                    }
                    break;
                    case MSG_DEVICE_OFFLINE: {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msgdeviceoffline), 1000).show();
                    }
                    break;
                    case MSG_DEVICE_INFO_GET_OVERTIME: {
                        if (activity.bdevclick == true) {
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msgdeviceovertime), 2000).show();//锟斤拷取锟借备锟斤拷息锟斤拷时!
                        }
                        activity.bdevclick = false;

                        if (activity.brfclick == true) {
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msgrfovertime), 2000).show();//锟斤拷取锟借备锟斤拷息锟斤拷时!
                        }
                        activity.brfclick = false;
                        activity.cancelwaitdialog();
                        try {
                            activity.devInfoMediaStream.setbConnected(false);
                        } catch (NullPointerException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    break;
                    case MSG_DEVICE_NAME_MODIFY_SUCCESS: {
                        activity.name = activity.strModifyname;
                        activity.nameTextView.setText(activity.name);
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.changedevnamesuccess), 1000).show();
                    }
                    break;
                    case MSG_DEVICE_NAME_MODIFY_FAILED: {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.changedevnamefailed), 1000).show();
                    }
                    break;
                    case MSG_DEVICE_LOCATION_SET_SUCCESS: {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.update_location_success), 1000).show();
                    }
                    break;
                    case MSG_DEVICE_3G_INFO_GET_FAIL: {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.update_location_failed), 1000).show();
                    }

                    break;
                    default:
                        break;
                }
            }

        }
    }
//--bm-modi-1101 end

    //--bm-modi-1101 begain
    static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<Activity> ref;

        public TimeOutAsyncTask(Activity activity) {
            ref = new WeakReference<Activity>(activity);
        }
//--bm-modi-1101 end

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Log.d(TAG, " onPreExecute " );
        }

        @Override
        protected String doInBackground(Integer... params) {
            // TODO Auto-generated method stub

            try {
                //Log.d(TAG, " sleep " );
                Thread.sleep(20000);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                //Log.d(TAG, " InterruptedException " );
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPreExecute();
//--bm-modi-1101 begain
            DeviceInfoActivity mGet = (DeviceInfoActivity) ref.get();
            if (mGet != null) {
                if (mGet.devInfoHandler != null)
                    mGet.devInfoHandler.sendEmptyMessage(MSG_DEVICE_INFO_GET_OVERTIME);
            }
//--bm-modi-1101 end
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
                Log.i(TAG, "锟斤拷取锟斤拷纬锟饺ｏ拷" + locationStr);
                isLocationGeted = true;
            }
        }
    }
}
