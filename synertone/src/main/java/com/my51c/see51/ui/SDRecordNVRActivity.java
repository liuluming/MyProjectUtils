package com.my51c.see51.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.listener.OnGetNVRDeviceListListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

import java.util.ArrayList;

public class SDRecordNVRActivity extends BaseActivity implements OnRefreshListener, OnItemClickListener {

    static final int MSG_UPDATE = 0;
    static final int MSG_ClEAR_PROGRESSBAR = 1;
    static final int MSG_START_DOWNLOAD = 2;
    static final int MSG_STOP_DOWNLOAD = 3;
    static final int MSG_PERCENT_PROCESSBAR = 4;
    static final int MSG_FOLDER_UPDATE = 5;
    static final int MSG_UPDATE_DATA = 6;
    public static boolean isNVR = false;
    private final String TAG = "SDRecordNVRActivity";
    private DeviceListView listViewSDRecord;
    private View progressView;
    private View waitTextView;
    private View emptyView;
    private AppData appData;
    private RemoteInteractionStreamer mediaStreamer;
    private String deviceID;
    private String url;
    private boolean isLocal = false;
    private String strFileList = null;
    private ArrayList<String> nvrDevList = new ArrayList<String>();
    private NVRAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    adapter = new NVRAdapter(nvrDevList);
                    listViewSDRecord.setAdapter(adapter);
                    listViewSDRecord.setOnItemClickListener(SDRecordNVRActivity.this);
                    adapter.notifyDataSetChanged();
                    progressView.setVisibility(View.INVISIBLE);
                    waitTextView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                    break;
                case MSG_UPDATE_DATA:
                    nvrDevList.clear();
                    String[] strItem = strFileList.split("\\|");
                    Log.i(TAG, "--strItem.length:" + strItem.length);
                    if (strItem != null) {
                        if (strItem.length < 2) {
                            return;
                        } else {

                            for (int i = 1; i < strItem.length; i++) {
                                String[] itemText = strItem[strItem.length - i].split(",");
                                if (itemText.length != 2)
                                    continue;
                                nvrDevList.add(itemText[0]);
                            }
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private OnGetNVRDeviceListListener mOnGetNVRDeviceListListener = new OnGetNVRDeviceListListener() {
        @Override
        public void OnGetNVRDeviceListSuccess(byte[] devbuf) {
            // TODO Auto-generated method stub
            strFileList = byteToString(devbuf);
            Log.i(TAG, "--mOnGetSDFileListListener:" + strFileList);
            mHandler.sendEmptyMessage(MSG_UPDATE_DATA);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--------onCreate----------");
        setContentView(R.layout.sdrecord_nvr_activity);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.sd_back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });

        listViewSDRecord = (DeviceListView) findViewById(R.id.listView);
        listViewSDRecord.setItemsCanFocus(true);
        listViewSDRecord.setonRefreshListener(this);

        progressView = findViewById(R.id.progress_get_devices_image);
        waitTextView = findViewById(R.id.loading);
        emptyView = findViewById(R.id.emptyView);

        appData = (AppData) getApplication();
        Bundle bundle = getIntent().getExtras();
        isLocal = bundle.getBoolean("isLocal");
        deviceID = bundle.getString("id");
        url = bundle.getString("url");
        isLocal = bundle.getBoolean("isLocal");
        Log.i(TAG, "url:" + url);
        isNVR = true;
        mediaStreamer = appData.getRemoteInteractionStreamer();
        if (mediaStreamer != null) {
            mediaStreamer.setOnGetNVRDeviceListListener(mOnGetNVRDeviceListListener);
            mediaStreamer.getNVRDeviceList();
        }
    }

    @Override
    protected void onResume() {
        isNVR = true;
        super.onResume();
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        refreshsdfile();
    }

    public void refreshsdfile() {
        progressView.setVisibility(View.VISIBLE);
        waitTextView.setVisibility(View.VISIBLE);
        nvrDevList.clear();
        if (mediaStreamer != null) {
            mediaStreamer.getNVRDeviceList();
        }
    }

    protected String byteToString(byte[] src) {
        int len = 0;
        for (; len < src.length; len++) {
            if (src[len] == 0) {
                break;
            }
        }
        return new String(src, 0, src.length);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        System.out.println("--------------" + nvrDevList.get(position - 1));
        Intent intent = new Intent(SDRecordNVRActivity.this, SDRecordFolderActivity.class);
        intent.putExtra("isLocal", isLocal);
        intent.putExtra("id", deviceID);
        intent.putExtra("url", url);
        intent.putExtra("isNVR", true);
        intent.putExtra("nvrDeviceId", nvrDevList.get(position - 1));
        isNVR = false;
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    public void backMainActivity() {
        isNVR = false;
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    //bm-add-1101
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaStreamer != null) {
            mediaStreamer.setOnGetNVRDeviceListListener(null);
        }
    }

    private class NVRAdapter extends BaseAdapter {

        private ArrayList<String> nvrDevList;

        public NVRAdapter(ArrayList<String> nvrDevList) {
            this.nvrDevList = nvrDevList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return nvrDevList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return nvrDevList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.sdrecord_folder_item, null);
                holder.nvrDevName = (TextView) convertView.findViewById(R.id.sd_folderName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nvrDevName.setText(nvrDevList.get(position));

            return convertView;
        }

        public class ViewHolder {
            public TextView nvrDevName;
        }
    }
}
