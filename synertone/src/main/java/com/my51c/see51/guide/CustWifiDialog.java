package com.my51c.see51.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.WifiListAdapter;
import com.my51c.see51.adapter.WifiListAdapter.ViewHolder;
import com.synertone.netAssistant.R;

import java.util.List;


public class CustWifiDialog extends BaseActivity implements OnClickListener, OnItemClickListener {
    private final int RESULT_SUCCESS = 1;
    private WifiManager mWifiManager;
    private WifiReceiver wifiReceiver;
    private List<ScanResult> wifiList;
    private WifiListAdapter adapter;
    private ListView wifiListview;
    private View waitView;
    private Button btnCancel;
    private ImageView imgRefreshwifi;
    private ScanResult mSelResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_wifi_list);

        wifiListview = (ListView) findViewById(R.id.wifilist);
        waitView = findViewById(R.id.refreshwaitview);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        imgRefreshwifi = (ImageView) findViewById(R.id.btnrefreshwifi);

        btnCancel.setOnClickListener(this);
        imgRefreshwifi.setOnClickListener(this);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (mWifiManager.isWifiEnabled()) {
            wifiList = mWifiManager.getScanResults();
        }

        adapter = new WifiListAdapter(this, wifiList, this);
        wifiListview.setAdapter(adapter);
        wifiListview.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();
        waitView.setVisibility(View.GONE);
        wifiListview.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnCancel: {
                Intent intent = new Intent(CustWifiDialog.this, GuideSmartWifi.class);
                setResult(RESULT_OK, intent);
                finish();
            }
            break;

            case R.id.btnrefreshwifi: {
                waitView.setVisibility(View.VISIBLE);
                wifiListview.setVisibility(View.GONE);
                mWifiManager.startScan();
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ScanResult scanResult = (ScanResult) (((ViewHolder) arg1.getTag()).essid.getTag());
        Intent intent = new Intent(CustWifiDialog.this, GuideSmartWifi.class);
        intent.putExtra("ssid", scanResult.SSID);
        intent.putExtra("capabilities", scanResult.capabilities);
        setResult(RESULT_SUCCESS, intent);
        finish();
    }

    private final class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList = mWifiManager.getScanResults();
            adapter.notifyDataSetChanged();
            waitView.setVisibility(View.GONE);
            wifiListview.setVisibility(View.VISIBLE);
        }
    }

}
