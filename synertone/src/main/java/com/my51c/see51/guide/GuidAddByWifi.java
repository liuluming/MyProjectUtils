package com.my51c.see51.guide;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.method.wifilist.ConnectWifi;
import com.method.wifilist.ConnectWifi.WifiCipherType;
import com.method.wifilist.WifiAdmin;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.service.LocalService;
import com.synertone.netAssistant.R;
import com.xqe.method.DelEditText;

import java.util.ArrayList;

public class GuidAddByWifi extends BaseActivity implements OnClickListener {
    public static int curSsid = 0;
    public static boolean isWifiOn = true;
    private final String TYPE_RETICLE_WIFI = "reticle+wifi";
    private final String TYPE_RETICLE_3G = "3G_reticle";
    private TextView ssidTx;
    private DelEditText pswEt;
    private CheckBox showPwsCb;
    private Button nextBtn;
    private ArrayList<String> ssidList;
    private WifiManager mWifiManager;
    private LinearLayout back;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo netInfo;
    private ProgressBar waitBar;
    Handler nextHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

//			System.out.println("��ת����");
            if (new WifiAdmin(getApplicationContext()).getSSID().replace("\"", "").equals(ssidTx.getText().toString().replace("\"", ""))) {

                waitBar.setVisibility(View.GONE);

                Intent intent = new Intent(GuidAddByWifi.this, GuideSetActivity.class);
                intent.putExtra("isAp", true);
//				intent.putExtra("DeviceType", GuideSetActivity.TYPE_ONLY_AP);
                intent.putExtra("DeviceId", ssidTx.getText().toString());
                startActivity(intent);

            } else {
                nextBtn.performClick();
            }

            super.handleMessage(msg);
        }

    };
    private DeviceList localDevList;
    private LocalService localService;
    private DeviceLocalInfo deviceLocalInfo;
    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guid_addbywifi);
        findView();
    }

    private void findView() {
        // TODO Auto-generated method stub
        back = (LinearLayout) findViewById(R.id.wifi_add_backLayout);
        ssidTx = (TextView) findViewById(R.id.wifi_ssidEt);
        pswEt = (DelEditText) findViewById(R.id.wifi_pswEt);
        showPwsCb = (CheckBox) findViewById(R.id.wifi_showPswCb);
        nextBtn = (Button) findViewById(R.id.wifi_NextBtn);
        waitBar = (ProgressBar) findViewById(R.id.wifi_waitprogressBar);

        ssidTx.setText(GuidSmartId.devId);
        back.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        showPwsCb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    pswEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pswEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    public void saveWifiState() {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        curSsid = wifiAdmin.getNetworkId();
        if (curSsid == 0) {//δ����wifi
            isWifiOn = false;
        }
    }

    public void removeWifi() {
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
        wifiManager.enableNetwork(curSsid, false);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.wifi_add_backLayout:
                backMainActivity();
                removeWifi();
                break;
            case R.id.wifi_NextBtn:
                waitBar.setVisibility(View.VISIBLE);

                mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                ConnectWifi connectWifi = new ConnectWifi(mWifiManager);
                connectWifi.connect(ssidTx.getText().toString(), pswEt.getText().toString(), WifiCipherType.WIFICIPHER_WPA);
                nextHandler.sendEmptyMessageDelayed(0, 2000);
                break;

            default:
                break;
        }
    }

    public void backMainActivity() {
        GuidAddByWifi.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
