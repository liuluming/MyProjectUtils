package com.my51c.see51.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.my51c.see51.adapter.SDRecordFileListAdapter.ViewHolder;
import com.my51c.see51.adapter.SDRecordFolderListAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.SDFileInfo;
import com.my51c.see51.listener.OnGetSDFileListListener;
import com.my51c.see51.media.MediaStreamFactory;
import com.my51c.see51.media.MediaStreamer.MediaEvent;
import com.my51c.see51.media.MediaStreamer.MediaEventListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

import java.io.DataOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SDRecordFolderActivity extends SherlockActivity implements OnRefreshListener {

    static final int MSG_UPDATE = 0;
    static final int MSG_ClEAR_PROGRESSBAR = 1;
    static final int MSG_START_DOWNLOAD = 2;
    static final int MSG_STOP_DOWNLOAD = 3;
    static final int MSG_PERCENT_PROCESSBAR = 4;
    static final int MSG_FOLDER_UPDATE = 5;
    static final int MSG_UPDATE_DATA = 6;
    private static final int MSG_TIME_OUT = 2001;
    private static final int MSG_DISCONNECT = 2002;
    public static String fileDate;
    public static boolean isLocal = false;
    private final String TAG = "SDRecordFolderActivity";
    DataOutputStream dos;
    private DeviceListView listViewSDRecord;
    private View progressView;
    private View waitTextView;
    private View emptyView;
    private AppData appData;
    private RemoteInteractionStreamer mediaStreamer;
    private ArrayList<SDFileInfo> mFileList;
    private ArrayList<SDFileInfo> mFolderist = null;
    private ArrayList<String> folderNameList;
    private SDRecordFolderListAdapter folderAdapter;
    private SDFileInfo mSelSDInfo;
    private ViewHolder mSelholder;
    private boolean bdownload = false;
    private File mFileDownload;
    private String deviceID;
    private String url;
    private boolean isFolder = false;
    private String strFileList = null;
    private boolean isNVR = false;
    private String nvrDeviceId = "";
    private TextView title;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_OUT:
                case MSG_DISCONNECT:
                    emptyView.setVisibility(View.INVISIBLE);
                    break;
                case MSG_FOLDER_UPDATE:
//				folderAdapter.setOnClickListener(SDRecordActivity.this);
                    listViewSDRecord.setAdapter(folderAdapter);
                    listViewSDRecord.setOnItemClickListener(new onFolderItemClick());
                    folderAdapter.notifyDataSetChanged();
                    progressView.setVisibility(View.INVISIBLE);
                    waitTextView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                    break;

                case MSG_ClEAR_PROGRESSBAR:
                    progressView.setVisibility(View.INVISIBLE);
                    waitTextView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                    break;

                case MSG_UPDATE_DATA:
                    mFileList.clear();
                    folderNameList.clear();
                    mFolderist.clear();
                    String[] strItem = strFileList.split("\\|");
                    Log.i(TAG, "--strItem.length:" + strItem.length);
                    if (strItem != null) {
                        if (strItem.length < 2) {
                            mHandler.sendEmptyMessage(MSG_FOLDER_UPDATE);
                            return;
                        } else {
                            {
                                Log.i(TAG, "--folder");
                                isFolder = true;
                                for (int i = 1; i < strItem.length; i++) {
                                    String[] itemText = strItem[strItem.length - i].split(",");

                                    if (itemText.length != 2)
                                        continue;
                                    folderNameList.add(itemText[0]);
                                }
                                mHandler.sendEmptyMessage(MSG_FOLDER_UPDATE);
                            }

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private MediaEventListener mMediaEventListener = new MediaEventListener() {

        @Override
        public void OnMediaDataException(MediaEvent event) {
            // TODO Auto-generated method stub
            Message msg = new Message();
            switch (event) {
                case CONN_TIME_OUT:
                    msg.what = MSG_TIME_OUT;
                    break;

                case CONN_DISCONNECT:
                    msg.what = MSG_DISCONNECT;
                    break;
            }

            mHandler.sendMessage(msg);
        }
    };
    private OnGetSDFileListListener mOnGetSDFileListListener = new OnGetSDFileListListener() {//getInfo����
        @Override
        public void onGetFileList(byte[] devbuf) {
            // TODO Auto-generated method stub
            Log.i(TAG, "--mOnGetSDFileListListener:" + devbuf.length + "------devbuf:" + devbuf);
            strFileList = byteToString(devbuf);
            Log.i(TAG, "--mOnGetSDFileListListener:" + strFileList);
            mHandler.sendEmptyMessage(MSG_UPDATE_DATA);
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        Log.i(TAG, "--------onCreate----------");
        setContentView(R.layout.sdrecord_activity);

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.sd_back_layout);
        ImageView searchImg = (ImageView) findViewById(R.id.sd_search_img);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });
        searchImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SDRecordFolderActivity.this, SDCalendarActivity.class);
                intent.putExtra("nvrDeviceId", nvrDeviceId);
                intent.putExtra("isAllClick", false);
                startActivity(intent);
            }
        });

        title = (TextView) findViewById(R.id.sd_titleName);
        listViewSDRecord = (DeviceListView) findViewById(android.R.id.list);
        listViewSDRecord.setItemsCanFocus(true);
        listViewSDRecord.setonRefreshListener(this);

        mFileList = new ArrayList<SDFileInfo>();
        mFolderist = new ArrayList<SDFileInfo>();
        folderNameList = new ArrayList<String>();
        folderAdapter = new SDRecordFolderListAdapter(getApplicationContext(), folderNameList);

        progressView = findViewById(R.id.progress_get_devices_image);
        waitTextView = findViewById(R.id.loading);
        emptyView = findViewById(R.id.emptyView);

        appData = (AppData) getApplication();
        isNVR = getIntent().getBooleanExtra("isNVR", false);
        if (isNVR) {
            nvrDeviceId = getIntent().getStringExtra("nvrDeviceId");
            title.setText(nvrDeviceId);
        }
        deviceID = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        isLocal = getIntent().getBooleanExtra("isLocal", false);
        Log.i(TAG, "url:" + url);
        fileDate = getnowdate();

    }

    private void createRemoteOperaction()//��ȡ������������
    {
        Map<String, String> paramp = new HashMap<String, String>();
        paramp.put("UserName", "admin");
        paramp.put("Password", "admin");
        paramp.put("Id", deviceID);


        if (isLocal) {
            mediaStreamer = new RemoteInteractionStreamer(url, paramp);
        } else {
            mediaStreamer = MediaStreamFactory.createRemoteInteractionMediaStreamer(url, paramp);
        }

        if (mediaStreamer != null) {
            appData.setRemoteInteractionStreamer(mediaStreamer);
            mediaStreamer.open();
            mediaStreamer.setDevId(deviceID);
        } else {
            appData.setRemoteInteractionStreamer(null);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        mediaStreamer = appData.getRemoteInteractionStreamer();

        if (mediaStreamer == null) {
            createRemoteOperaction();
        } else {
            if (!deviceID.equals(mediaStreamer.getDevId())) {
                createRemoteOperaction();
            }
        }

        if (!PlayAcy.isPlayBack) {
            Log.i(TAG, "!PlayAcy back, refresh" + fileDate);
            if (mediaStreamer != null) {
                mediaStreamer.setOnGetSDFileListListener(mOnGetSDFileListListener);
                mediaStreamer.getSDFileListByDate(fileDate, nvrDeviceId);
                mediaStreamer.setMediaDataListener(mMediaEventListener);
                //refreshsdfile();
            }
        } else {
            Log.i(TAG, "PlayAcy back");
            PlayAcy.isPlayBack = false;
        }
        super.onResume();
    }
//bm-add=1101	

    @Override
    protected void onStop() {
//		listViewSDRecord.removeAllViews();
        super.onStop();
        if (mediaStreamer != null) {
            mediaStreamer.setMediaDataListener(null);
        }
    }

    // bm-add-1101
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaStreamer != null) {
            mediaStreamer.setOnGetSDFileListListener(null);
        }

    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        refreshsdfile();
    }

    public void refreshsdfile() {
        progressView.setVisibility(View.VISIBLE);
        waitTextView.setVisibility(View.VISIBLE);
        folderNameList.clear();
        folderAdapter.notifyDataSetChanged();
        if (mediaStreamer != null) {
            mediaStreamer.getSDFileListByDate(fileDate, nvrDeviceId);
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

    public String getnowdate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new java.util.Date());
    }

    public void backMainActivity() {
        SDRecordFolderActivity.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private class onFolderItemClick implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(SDRecordFolderActivity.this, SDRecordFileActivity.class);
            intent.putExtra("dateHour", folderNameList.get(position - 1));
            intent.putExtra("nvrDeviceId", nvrDeviceId);
            intent.putExtra("url", url);
            intent.putExtra("id", deviceID);
            if (position == 1) {
                intent.putExtra("isFirstItem", true);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        }

    }
}
