package com.my51c.see51.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.protocal.RFPackage;
import com.my51c.see51.ui.RFDeviceTypeGridFragment.OnGridItemClick;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashSet;

public class RFDeviceInfoActivity extends SherlockFragmentActivity implements OnGridItemClick {
    public RemoteInteractionStreamer mediastream;
    public boolean isLocal = false;
    private RFPackage mRFPackage;
    private Device mDevice;
    private String deviceID;
    private RFDeviceTypeGridFragment fragment1;
    private RFDeviceListFragment fragment2;
    private BLPDeviceInfoFragment blpFragment;
    private AppData appData;
    private ArrayList<String> blpList = new ArrayList<String>();

    public Device getParseDevice() {
        return mDevice;
    }

    public ArrayList<String> getblpList() {
        return blpList;
    }

    public int getBlpNum() {
        return computeBlpNum();
    }

    public int computeBlpNum() {

        int num;
        if (blpList == null) {
            num = 0;
        } else {
            HashSet<String> set = new HashSet<String>();
            for (int i = 0; i < blpList.size(); i++) {
                String[] infos = blpList.get(i).split(",");
                set.add(infos[0]);
            }
            num = set.size();
        }
        return num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rfdeviceinfo);

        if (findViewById(R.id.fragmentContent) != null) {
            if (savedInstanceState != null)
                return;
        }

        Bundle bundle = getIntent().getExtras();
        blpList = bundle.getStringArrayList("blpList");
        deviceID = bundle.getString("id");
        isLocal = bundle.getBoolean("isLocal");
        appData = (AppData) getApplication();
        mediastream = appData.getRemoteInteractionStreamer();

        if (isLocal) {
            mDevice = appData.getLocalList().getDevice(deviceID);
        } else {
            mDevice = appData.getAccountInfo().getCurrentList().getDevice(deviceID);
        }

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.rf_back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });

        ImageView addImg = (ImageView) findViewById(R.id.addImg);
        addImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("id", deviceID);
                if (isLocal) {
                    intent.putExtra("isLocal", true);
                } else {
                    intent.putExtra("isLocal", false);
                }

                intent.setClass(RFDeviceInfoActivity.this, RfDeviceAddActivity.class);
                startActivity(intent);
            }
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment1 = new RFDeviceTypeGridFragment();
        fragmentTransaction.replace(R.id.fragmentContent, fragment1);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                // Do whatever you want, e.g. finish()
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    backMainActivity();
                } else {
                    getSupportFragmentManager().popBackStackImmediate(null, 0);
                }

                break;

            default: {
                if (item.getTitle().equals("Add")) {
                    Intent intent = new Intent();
                    intent.putExtra("id", deviceID);
                    if (isLocal) {
                        intent.putExtra("isLocal", true);
                    } else {
                        intent.putExtra("isLocal", false);
                    }

                    intent.setClass(RFDeviceInfoActivity.this, RfDeviceAddActivity.class);
                    startActivity(intent);
                }
            }
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void backMainActivity() {
        RFDeviceInfoActivity.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void OnItemClick(int position) {
        // TODO Auto-generated method stub
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (position == 8) {
            System.out.println("Ѫѹ��");
            blpFragment = new BLPDeviceInfoFragment();
            fragmentTransaction.replace(R.id.fragmentContent, blpFragment);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("blpList", blpList);
            blpFragment.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            fragment2 = new RFDeviceListFragment();
            fragmentTransaction.replace(R.id.fragmentContent, fragment2);
            Bundle bundle = new Bundle();
            bundle.putInt("itemnum", position);
            System.out.println("-----------OnItemClick:" + position);
            fragment2.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void onRefreshRFInfo() {
        if (fragment1 != null)
            fragment1.onRefreshRFDevice();

        if (fragment2 != null)
            fragment2.onRefreshRFDevice();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add("Add").setIcon(R.drawable.add_new_camera)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }
}
