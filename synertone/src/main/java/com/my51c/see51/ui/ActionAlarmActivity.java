package com.my51c.see51.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.adapter.AlarmListAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.AlarmInfo;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

import java.util.ArrayList;

public class ActionAlarmActivity extends SherlockFragmentActivity implements OnClickListener,
        OnRefreshListener {
    private DeviceListView listView;
    private AppData appData;
    private AlarmListAdapter adapter;
    private ArrayList<AlarmInfo> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alarm_list);
        listView = (DeviceListView) findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setonRefreshListener(this);

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.alram_back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ActionAlarmActivity.this.finish();
            }
        });

        appData = (AppData) this.getApplication();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (appData.getAccountInfo() != null) {
            alarmList = appData.getAccountInfo().getAlarmList();
            if (alarmList != null)
                adapter = new AlarmListAdapter(this, alarmList, this);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case android.R.id.home:
                ActionAlarmActivity.this.finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        appData = (AppData) this.getApplication();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if (appData.getAccountInfo() != null) {
            alarmList = appData.getAccountInfo().getAlarmList();
            if (alarmList != null) {
                adapter = new AlarmListAdapter(ActionAlarmActivity.this, alarmList, this);
                listView.setAdapter(adapter);
                try {
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        }

    }

}
