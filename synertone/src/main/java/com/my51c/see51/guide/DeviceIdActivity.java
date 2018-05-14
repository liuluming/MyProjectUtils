package com.my51c.see51.guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
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
import java.util.Timer;
import java.util.TimerTask;

public class DeviceIdActivity extends SherlockFragmentActivity implements DeviceListListener, OnClickListener {
    private final String TAG = "DeviceIdActivity";
    private final String TYPE_RETICLE_WIFI = "reticle+wifi";
    //	private final String TYPE_ONLY_WIFI = "onlyWifi";
//	private final String TYPE_ONLY_3G = "only3G";
    private final String TYPE_RETICLE_3G = "3G_reticle";
    DeviceList localDevList;
    private Button btnNextDeviceId;
    private Button btnRestoreDevice;
    private ImageButton imgBtnDimension;
    private ImageButton btnRefresh;
    private DelEditText edtDeviceId;
    private AppData appData;
    private SimpleAdapter adapter;
    private ListView mListView = null;
    private ArrayList<HashMap<String, Object>> map_LocalList = null;
    private ProgressBar proDeviceId;
    private Timer timer;
    private TimerTask timerTask;
    private MyHandler myHandler;
    //	private final String TYPE_ONLY_RETICLE = "onlyReticle";
    private String strBindtype;
    private LocalService localservice;
    private TextView title;
    private int num = 0;
    private TextView percent, waitTx;
    private boolean isAddByVoice = false;
    private Dialog dialog;
    private boolean isPaused = false;
    private ImageView EtImg;
    private LinearLayout devListLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_deviceid);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.id_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        LayoutInflater.from(this);

        strBindtype = getIntent().getStringExtra("BindStyle");
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));
        map_LocalList = new ArrayList<HashMap<String, Object>>();
        myHandler = new MyHandler(this);

        localservice = appData.getLocalService();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findView();
        setOnClickListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do whatever you want, e.g. finish()
                backMainActivity();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void edtTextchangeListener() {
        edtDeviceId.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                selectionStart = edtDeviceId.getSelectionStart();
                selectionEnd = edtDeviceId.getSelectionEnd();
                //Log.i("gongbiao1", "" + selectionStart);
                if (temp.length() == 12) {
//					edtDeviceId.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    btnNextDeviceId.setVisibility(View.VISIBLE);
                    btnRestoreDevice.setVisibility(View.VISIBLE);
                } else if (temp.length() > 12) {
//					edtDeviceId.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    btnNextDeviceId.setVisibility(View.VISIBLE);
                    btnRestoreDevice.setVisibility(View.VISIBLE);
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    edtDeviceId.setText(s);
                    edtDeviceId.setSelection(tempSelection);

                } else if (temp.length() < 12) {
                    Drawable drawable = getResources().getDrawable(R.drawable.error);
//					edtDeviceId.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    btnNextDeviceId.setVisibility(View.GONE);
                    btnRestoreDevice.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        //Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        //Log.d(TAG, "onStop");
    }

    private void findView() {
        mListView = (ListView) findViewById(R.id.devList);
        btnNextDeviceId = (Button) findViewById(R.id.btnNextDeviceId);
        btnRestoreDevice = (Button) findViewById(R.id.btnRestoreDeviceId);

        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);

        edtDeviceId = (DelEditText) findViewById(R.id.edtDeviceId);

        imgBtnDimension = (ImageButton) findViewById(R.id.imgBtnDimension);

        proDeviceId = (ProgressBar) findViewById(R.id.proDeviceId);
        EtImg = (ImageView) findViewById(R.id.imageViewWifi);

        devListLayout = (LinearLayout) findViewById(R.id.devListLayout);
        title = (TextView) findViewById(R.id.guid_device_id_title);

    }

    private void setOnClickListener() {
        btnNextDeviceId.setOnClickListener(this);
        btnRestoreDevice.setOnClickListener(this);
        imgBtnDimension.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    public void getDeviceList() {
        map_LocalList.clear();
        localDevList = appData.getLocalList();
        localDevList.addListListener(this);

        synchronized (localDevList) {
            for (Device device : localDevList) {
                DeviceLocalInfo devInfo = device.getLocalInfo();
                if (devInfo != null) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    String deviceId = devInfo.getCamSerial() + ", " + devInfo.getDeviceName();
                    map.put("item", deviceId);
                    map.put("deviceinfo", devInfo);
                    map_LocalList.add(map);
                }
            }
        }
    }

    private void restoreDevice() {
        String deviceId = edtDeviceId.getText().toString();
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnNextDeviceId:
                ClickNext();
                break;
//		case R.id.exitGuideDevList:
//			appData.exit();
//			break;
            case R.id.btnRestoreDeviceId: {
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

            }
            break;
            case R.id.imgBtnDimension:

                Intent intent = new Intent();
                intent.setClass(DeviceIdActivity.this, DimensionActivity.class);
                intent.putExtra("isguide", true);
                DeviceIdActivity.this.startActivity(intent);
                break;
            case R.id.btnRefresh:
                refresh();
                break;
        }
    }

    public void refresh() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo == null) {
            //Log.d(TAG, "netInfo Not Connected");
        }
        if (netinfo.isConnected()) {
            int t = cm.getActiveNetworkInfo().getType();
            //Log.d(TAG, "Network type = " + t);
            if (t == ConnectivityManager.TYPE_WIFI) {
                // ����wifi����ethernet�����������������豸
                // TODO Auto-generated method stub
                proDeviceId.setVisibility(View.VISIBLE);
                appData.getLocalList().clear();
                appData.getLocalService().search();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        //Log.d(TAG, "onResume");
        super.onResume();

        this.getDeviceList();
        adapter = new SimpleAdapter(this, map_LocalList, R.layout.corner_list_item, new String[]
                {"item"}, new int[]{R.id.item_title});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemListSelectedListener());
        String deviceId = DimensionActivity.deviceId;

        if (deviceId != null) {
            //Log.d(TAG, "QRCODE:"+deviceId);
            if (deviceId.length() == 12) {
                edtDeviceId.setText(deviceId);
//				edtDeviceId.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnNextDeviceId.setVisibility(View.VISIBLE);
                btnRestoreDevice.setVisibility(View.VISIBLE);
            } else {
                deviceId = "";
                Toast toastQRcode = Toast.makeText(getApplicationContext(), getString(R.string.guideQRCodeError), Toast.LENGTH_SHORT);
                toastQRcode.setGravity(Gravity.CENTER, 0, 0);
                toastQRcode.show();
            }
        }

        if (!map_LocalList.isEmpty()) {
            proDeviceId.setVisibility(View.GONE);
        }
        edtTextchangeListener();
        PlatformActivity.isBack = false;

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                myHandler.sendEmptyMessage(1);//visible
            }
        };
        if (localDevList.getDeviceCount() == 0) {
            proDeviceId.setVisibility(View.VISIBLE);
            timer.schedule(timerTask, 10000);//10s�������ʾproDeviceId
        }

        if (strBindtype.equals("complex")) {

            title.setText(getString(R.string.guideComplexInstall));
        } else if (strBindtype.equals("simple")) {
            title.setText(getString(R.string.guideSimleInstall));
        } else if (strBindtype.equals("addByVoice")) {//useness by xqe

            title.setText("�������");
            isAddByVoice = true;
            edtDeviceId.setText(GuidSmartId.devId);
            devListLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause");
        isPaused = true;
        localDevList.removeListener(this);
        timerTask.cancel();
        timer.cancel();
    }

    @Override
    public void onListUpdate() {
        // TODO Auto-generated method stub
        System.out.println("--------------onListUpdate");
        myHandler.sendEmptyMessage(0);//getDevList,notifyDataSetChange
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
                        myHandler.sendEmptyMessage(1);//visible
                    }
                };
                timer.schedule(timerTask, 10000);//10s��ִ��
            }
        }
    }

    public void ClickNext() {
        Intent intent = new Intent();
        String deviceId = edtDeviceId.getText().toString();
        String deviceType = TYPE_RETICLE_WIFI;
        intent.putExtra("DeviceId", deviceId);


        String devMode = deviceId.substring(0, 3);
        if (devMode.equals("a82") || devMode.equals("a83") || devMode.equals("a84")) {
            // 3G + ����
            deviceType = TYPE_RETICLE_3G;
        } else {
            // ֻ������
            deviceType = TYPE_RETICLE_WIFI;
        }

        localDevList.removeListener(this);
        intent.putExtra("DeviceType", deviceType);
        //Log.d(TAG, "deviceId:" + deviceId);

        if (strBindtype.equals("complex"))//��װ�����
        {
            intent.setClass(DeviceIdActivity.this, GuideSetActivity.class);
        } else//id���к����
        {
            intent.setClass(DeviceIdActivity.this, PlatformActivity.class);
            intent.putExtra("network", "wifi");
            intent.putExtra("DeviceId", deviceId);
        }

        DeviceIdActivity.this.startActivity(intent);
    }

    public void backMainActivity() {
        DeviceIdActivity.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    static class MyHandler extends Handler {
        WeakReference<DeviceIdActivity> mActivity;

        MyHandler(DeviceIdActivity activity) {
            mActivity = new WeakReference<DeviceIdActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceIdActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    theActivity.getDeviceList();
                    theActivity.adapter.notifyDataSetChanged();
                    if (!theActivity.map_LocalList.isEmpty()) {
                        theActivity.proDeviceId.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    theActivity.proDeviceId.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class OnItemListSelectedListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String deviceId = ((String) map_LocalList.get(arg2).get("item")).substring(0, 12);
            edtDeviceId.setText(deviceId);
            edtDeviceId.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            btnNextDeviceId.setVisibility(View.VISIBLE);
            btnRestoreDevice.setVisibility(View.VISIBLE);
        }
    }

}
