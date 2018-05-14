package com.my51c.see51.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.my51c.see51.adapter.SDRecordFileListAdapter;
import com.my51c.see51.adapter.SDRecordFileListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.SDFileInfo;
import com.my51c.see51.listener.OnDeleteSDFileListener;
import com.my51c.see51.listener.OnGetFileDataOverListener;
import com.my51c.see51.listener.OnGetSDFileDataListener;
import com.my51c.see51.listener.OnGetSDFileListListener;
import com.my51c.see51.listener.OnGetVideoConnectionStatusListener;
import com.my51c.see51.media.MediaStreamFactory;
import com.my51c.see51.media.MediaStreamer.MediaEvent;
import com.my51c.see51.media.MediaStreamer.MediaEventListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SDRecordFileActivity extends SherlockListActivity implements OnRefreshListener {

    static final int MSG_UPDATE = 0;
    static final int MSG_ClEAR_PROGRESSBAR = 1;
    static final int MSG_START_DOWNLOAD = 2;
    static final int MSG_NEXT_DOWNLOAD = 3;
    static final int MSG_PERCENT_PROCESSBAR = 4;
    static final int MSG_UPDATE_DATA = 5;
    static final int MSG_REFRESH = 6;
    static final int MSG_DELETE_SUCCESS = 10;
    private static final int MSG_TIME_OUT = 2001;
    private static final int MSG_DISCONNECT = 2002;
    public static String nowdate;
    private final String TAG = "SDRecordFileActivity";
    private DeviceListView folderFileListView;
    private View progressView;
    private View waitTextView;
    private View emptyView;
    private AppData appData;
    private RemoteInteractionStreamer mediaStreamer;
    private ArrayList<SDFileInfo> mFileList;
    private SDRecordFileListAdapter mAdapter;
    private SDFileInfo mSelSDInfo;
    private ViewHolder mSelholder;
    private boolean bdownload = false;
    private RandomAccessFile mFileDownload;
    private String deviceID;
    private String dialogFileName;
    private String dateHour = null;
    private TextView titleName;
    private boolean isFirstItem = false;
    private String strFileList = null;
    private ArrayList<SDFileInfo> dlInfoList;
    private ArrayList<ViewHolder> dlHolderList;
    private boolean isDownLoading = false;
    private boolean canRefresh = true;
    private boolean isDlRemove = false;
    private ToastCommom toast = new ToastCommom();
    private String url;
    private String nvrDeviceId = "";
    private boolean bCanDownloadContinus = true;
    //private boolean bRestartSend = false;
    private OnGetVideoConnectionStatusListener mOnGetVideoConnectionStatusListener = new OnGetVideoConnectionStatusListener() {

        @Override
        public void onVideoDisconnection(String deviceid, int nIndex) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onVideoConnection(String deviceid, int nIndex) {
            // TODO Auto-generated method stub
            if (mediaStreamer != null && mSelSDInfo != null && mSelSDInfo.getnCurSize() < mSelSDInfo.getnFileSize()) {
                Log.d("SDRecordFileActivity", "on restart sd download!" + " mSelSDInfo cursize: " + mSelSDInfo.getnCurSize());
                mediaStreamer.getSDFileData(mSelSDInfo.getSzFileName(), nvrDeviceId, mSelSDInfo.getnCurSize());
                if (bCanDownloadContinus == false && mFileDownload != null) {
                    try {
                        mFileDownload.seek(0);
                        mSelSDInfo.setnCurSize(0);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //bRestartSend = true;
                }
            }
        }
    };
    private MyHandler mHandler = new MyHandler(this);
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
    private OnGetSDFileListListener mOnGetSDFileListListener = new OnGetSDFileListListener() {
        @Override
        public void onGetFileList(byte[] devbuf) {
            // TODO Auto-generated method stub
            strFileList = byteToString(devbuf);
            mHandler.sendEmptyMessage(MSG_UPDATE_DATA);//锟节凤拷Ui锟竭筹拷锟斤拷锟睫革拷锟斤拷ListView锟襟定碉拷锟斤拷锟捷讹拷锟斤拷锟斤拷锟酵拷锟斤拷锟斤拷锟斤拷锟节凤拷UI锟竭筹拷锟叫革拷锟斤拷锟斤拷锟竭程控硷拷锟侥达拷锟斤拷
            //fix:锟斤拷锟竭筹拷锟铰伙拷玫锟斤拷锟斤拷锟斤拷锟斤拷却锟斤拷锟斤拷锟斤拷叱蹋锟斤拷锟街憋拷锟斤拷锟斤拷锟斤拷叱锟斤拷锟轿拷涓持�
        }
    };
    private OnDeleteSDFileListener mOnDeleteSDFileListener = new OnDeleteSDFileListener() {

        @Override
        public void OnDeleteSDFileSuccess() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
        }

        @Override
        public void OnDeleteSDFileFailed() {
            // TODO Auto-generated method stub

        }
    };
    private OnGetSDFileDataListener mOnGetSDFileDataListener = new OnGetSDFileDataListener() {
        int nInvalidTimes = 0;

        @Override
        public void onGetFileDataPiece(byte[] devbuf, long nPos) {
            // TODO Auto-generated method stub
            Log.d(TAG, "********get npos: " + nPos + "DATA SIZE: " + devbuf.length);
            if (!isDlRemove) {
                if (mFileDownload != null) {

                    try {
                        if (nPos != -1) {

                            if (nPos > mSelSDInfo.getnCurSize()) {
                                Log.d("Invalid time", "nPos: " + nPos + " Cursize: " + mSelSDInfo.getnCurSize() + " DATA SIZE: " + devbuf.length);

                                if (nInvalidTimes < 20) {
                                    nInvalidTimes++;
                                } else {
                                    if (mediaStreamer != null && mSelSDInfo != null)
                                        mediaStreamer.getSDFileData(mSelSDInfo.getSzFileName(), nvrDeviceId, mSelSDInfo.getnCurSize());
                                    nInvalidTimes = 0;
                                }


                                return;
                            }

                            nInvalidTimes = 0;
                            bCanDownloadContinus = true;
                            mFileDownload.seek(nPos);

                        } else {
                            bCanDownloadContinus = false;
                        }

                        Log.d("writefile", "nPos: " + nPos + " nextPos :" + (nPos + devbuf.length) + " length: " + devbuf.length);
                        mFileDownload.write(devbuf, 0, devbuf.length);

                        long nTotalSize = mSelSDInfo.getnFileSize();
                        long nCurSize = 0;
                        if (nPos == -1) {
                            nCurSize = mSelSDInfo.getnCurSize() + devbuf.length;
                        } else {
                            nCurSize = nPos + devbuf.length;
                        }
                        mSelSDInfo.setnCurSize(nCurSize);
                        double dper = (double) nCurSize / nTotalSize * 100;

                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_PERCENT_PROCESSBAR;
                        msg.arg1 = (int) dper;
                        mHandler.sendMessage(msg);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    private OnGetFileDataOverListener mOnGetFileDataOverListener = new OnGetFileDataOverListener() {

        @Override
        public void onGetFileDataOver() {
            // TODO Auto-generated method stub
            if (!isDlRemove) {
                mSelSDInfo.setWaitingForDl(false);
                mHandler.sendEmptyMessage(MSG_NEXT_DOWNLOAD);//nextDownload();	mAdapter.showbuttonType(mSelholder, 3);
            }
        }
    };

    public static String byteToString(byte[] src) {
        int len = 0;
        for (; len < src.length; len++) {
            if (src[len] == 0) {
                break;
            }
        }
        return new String(src, 0, len);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.sdrecord_activity);

        dateHour = getIntent().getStringExtra("dateHour");
        isFirstItem = getIntent().getBooleanExtra("isFirstItem", false);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.sd_back_layout);
        ImageView searchImg = (ImageView) findViewById(R.id.sd_search_img);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });

        searchImg.setVisibility(View.GONE);
        titleName = (TextView) findViewById(R.id.sd_titleName);
        titleName.setText(dateHour);

        folderFileListView = (DeviceListView) findViewById(android.R.id.list);
        folderFileListView.setItemsCanFocus(true);
        folderFileListView.setonRefreshListener(this);

        mFileList = new ArrayList<SDFileInfo>();
        dlInfoList = new ArrayList<SDFileInfo>();
        dlHolderList = new ArrayList<SDRecordFileListAdapter.ViewHolder>();
        mAdapter = new SDRecordFileListAdapter(this, mFileList);
        folderFileListView.setAdapter(mAdapter);

        //folderFileListView.setOnItemClickListener(this);
        progressView = findViewById(R.id.progress_get_devices_image);
        waitTextView = findViewById(R.id.loading);
        emptyView = findViewById(R.id.emptyView);

        appData = (AppData) getApplication();
        Bundle bundle = getIntent().getExtras();
        nvrDeviceId = bundle.getString("nvrDeviceId");
        deviceID = bundle.getString("id");
        url = bundle.getString("url");
        nowdate = getnowdate();

    }

    private void createRemoteOperaction()//锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
    {
        Map<String, String> paramp = new HashMap<String, String>();
        paramp.put("UserName", "admin");
        paramp.put("Password", "admin");
        paramp.put("Id", deviceID);
        Log.i(TAG, "createRemoteOperaction:deviceID" + deviceID + "-url:" + url);
        if (SDRecordFolderActivity.isLocal) {
            mediaStreamer = new RemoteInteractionStreamer(url, paramp);
        } else {
            mediaStreamer = MediaStreamFactory.createRemoteInteractionMediaStreamer(url, paramp);
        }

        if (mediaStreamer != null) {
            //appData.setRemoteInteractionStreamer(mediaStreamer);
            mediaStreamer.open();
            mediaStreamer.setDevId(deviceID);
        } else {
            Log.i(TAG, "--mediaStreamer == null");
            //appData.setRemoteInteractionStreamer(null);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaStreamer != null) {
            mediaStreamer.close();
            mediaStreamer.setOnGetSDFileListListener(null);
            mediaStreamer.setOnGetSDFileDataListener(null);
            mediaStreamer.setOnGetFileDataOverListener(null);
            mediaStreamer.setmOnGetVideoConnectionStatusListener(null);
            mediaStreamer = null;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (PlayAcy.isPlayBack) {
            PlayAcy.isPlayBack = false;
        }

        createRemoteOperaction();
        if (mediaStreamer != null) {
            mediaStreamer.setOnGetSDFileListListener(mOnGetSDFileListListener);
            mediaStreamer.setOnGetSDFileDataListener(mOnGetSDFileDataListener);
            mediaStreamer.setOnGetFileDataOverListener(mOnGetFileDataOverListener);
            mediaStreamer.getSDFolderFileListByDate(dateHour, nvrDeviceId);
            mediaStreamer.setMediaDataListener(mMediaEventListener);
            mediaStreamer.setmOnGetVideoConnectionStatusListener(mOnGetVideoConnectionStatusListener);

            mHandler.sendEmptyMessageDelayed(MSG_REFRESH, 800);
        }

        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mediaStreamer != null) {
            mediaStreamer.setMediaDataListener(null);
        }
    }

    public void backMainActivity() {
        if (isDownLoading) {
            final Dialog dialog = new Dialog(SDRecordFileActivity.this, R.style.Erro_Dialog);
            dialog.setContentView(R.layout.del_dialog);
            TextView delTx = (TextView) dialog.findViewById(R.id.erroTx);
            Button cancel = (Button) dialog.findViewById(R.id.del_cancel);
            Button ok = (Button) dialog.findViewById(R.id.del_ok);
            delTx.setText(getString(R.string.fiel_downlaoding));
            cancel.setText(getString(R.string.download_cancel));
            ok.setText(getString(R.string.download_continue));
            cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 删锟斤拷未锟斤拷锟斤拷锟斤拷锟侥硷拷锟斤拷锟剿筹拷
                    delNonCompleteVideo(mSelSDInfo);
                    SDRecordFileActivity.this.finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    dialog.dismiss();
                }
            });
            ok.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            SDRecordFileActivity.this.finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        backMainActivity();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if (canRefresh) {
            canRefresh = false;
            refreshsdfile();
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    canRefresh = true;
                }
            }, 1500);
        } else {
            Log.i(TAG, "hold on 3s");
        }
    }

    private void playVideo(String fileName, String name) {

        //Log.d("open file", fileName);
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        if (file.length() == 0) {
            toast.ToastShow(this, getString(R.string.errorfile), Toast.LENGTH_LONG);
            return;
        }

        if (fileName.endsWith(".jpg")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
            startActivity(intent);
        } else {
            Intent intent = new Intent(SDRecordFileActivity.this, PlayAcy.class);
            intent.putExtra("isFromSD", true);
            intent.putExtra("string", fileName);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    public void refreshsdfile() {
        for (SDFileInfo info : dlInfoList) {
            info.setWaitingForDl(false);
        }
        dlInfoList.clear();
        dlHolderList.clear();
        if (isDownLoading) {
            removeDownload();
            isDownLoading = false;
        }
        progressView.setVisibility(View.VISIBLE);
        waitTextView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
        if (mediaStreamer != null) {
            mediaStreamer.getSDFolderFileListByDate(dateHour, nvrDeviceId);
        }
    }

    public String getnowdate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new java.util.Date());
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
    */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (canRefresh == false)
            return;

        try {
            ViewHolder vhold = (ViewHolder) v.getTag();
//		mSelholder = vhold;
            SDFileInfo tmp = (SDFileInfo) vhold.tvfilename.getTag();
//		mSelSDInfo = tmp;
            Log.i(TAG, "锟斤拷锟絠tem锟斤拷" + position);

            if (vhold.ivdownload.getVisibility() == View.VISIBLE) {
                Log.i(TAG, "锟窖硷拷锟斤拷锟斤拷锟斤拷锟叫憋拷" + position);
                dlInfoList.add(tmp);
                dlHolderList.add(vhold);
                tmp.setWaitingForDl(true);
                if (!isDownLoading) {
                    isDownLoading = true;
                    startdownload();
                }
                mAdapter.showbuttonType(vhold, 2);
                return;
            }

            if (vhold.ivcanceldownload.getVisibility() == View.VISIBLE) {
                Log.i(TAG, "锟斤拷锟狡筹拷锟斤拷锟斤拷锟叫憋拷" + position);
                tmp.setWaitingForDl(false);
                if (tmp.getSzFileName().equals(mSelSDInfo.getSzFileName())) {//删锟斤拷锟斤拷锟斤拷锟斤拷锟截碉拷锟侥硷拷
                    Log.i(TAG, "");
                    removeDownload();
                } else {                                                        //未锟斤拷锟截碉拷锟侥硷拷锟狡筹拷锟斤拷锟截讹拷锟斤拷
                    dlInfoList.remove(tmp);
                    dlHolderList.remove(vhold);
                    tmp.setWaitingForDl(false);
                }
                mAdapter.showbuttonType(vhold, 1);
                return;
            }

            if (vhold.ivplay.getVisibility() == View.VISIBLE) {
                playVideo(tmp.getRealPath() + tmp.getSzFileName(), tmp.getSzFileName());
                System.out.println("------" + tmp.getRealPath() + tmp.getSzFileName());
                mAdapter.showbuttonType(vhold, 3);
                return;
            }
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeDownload() {
        isDlRemove = true;
        if (mediaStreamer != null) {
            mediaStreamer.close();
            mediaStreamer.setOnGetSDFileListListener(null);
            mediaStreamer.setOnGetSDFileDataListener(null);
            mediaStreamer.setOnGetFileDataOverListener(null);
            mediaStreamer = null;
        }

        try {
            if (mFileDownload != null) {
                mFileDownload.close();
                mFileDownload = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bdownload = false;
        mSelSDInfo.setbDown(bdownload);
        mSelholder.numbar.setVisibility(View.GONE);
        delNonCompleteVideo(mSelSDInfo);
        if (dlInfoList.size() != 0) {
            dlInfoList.remove(0);
            dlHolderList.remove(0);
        }
        if (dlInfoList.size() != 0) {
            startdownload();
            isDownLoading = true;
        } else {
            Log.i(TAG, "全锟斤拷锟斤拷锟斤拷锟斤拷桑锟斤拷锟斤拷锟斤拷斜锟轿拷锟�");
            isDownLoading = false;
        }
    }

    public void startdownload() {
        if (isDlRemove) {
            createRemoteOperaction();
            if (mediaStreamer != null) {
                Log.i(TAG, "锟斤拷锟铰达拷锟斤拷TCP锟斤拷锟接ｏ拷锟襟定硷拷锟斤拷");
                mediaStreamer.setOnGetSDFileListListener(mOnGetSDFileListListener);
                mediaStreamer.setOnGetSDFileDataListener(mOnGetSDFileDataListener);
                mediaStreamer.setOnGetFileDataOverListener(mOnGetFileDataOverListener);
            }
            isDlRemove = false;
        }
        mSelholder = dlHolderList.get(0);
        mSelSDInfo = dlInfoList.get(0);
        mSelSDInfo.setnCurSize(0);
        delNonCompleteVideo(mSelSDInfo);
        Log.i(TAG, "锟斤拷始锟斤拷锟截ｏ拷" + mSelSDInfo.getnCurSize() * 100 / mSelSDInfo.getnFileSize());
        if (mSelSDInfo == null) {
            return;
        }
        String videopath = mSelSDInfo.getRealPath();
        File mVideoPath = new File(videopath);
        if (!mVideoPath.exists()) {
            mVideoPath.mkdirs();
        }
        Log.d("FILESIZE,", "mFileDownload path: " + mVideoPath + mSelSDInfo.getSzFileName());
        try {
            mFileDownload = new RandomAccessFile(mVideoPath + File.separator + mSelSDInfo.getSzFileName(), "rw");
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (mediaStreamer != null) {
            Log.i(TAG, "锟斤拷锟斤拷锟斤拷锟斤拷锟侥硷拷锟斤拷" + mSelSDInfo.getSzFileName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mediaStreamer.getSDFileData(mSelSDInfo.getSzFileName(), nvrDeviceId, mSelSDInfo.getnCurSize());
        }

        bdownload = true;
        mSelSDInfo.setbDown(bdownload);
        mSelholder.numbar.setVisibility(View.VISIBLE);
    }

    public void nextDownload() {
        try {
            if (mFileDownload != null) {
                mFileDownload.close();
                mFileDownload = null;
            }
            bdownload = false;
            mSelSDInfo.setbDown(bdownload);
            mSelholder.numbar.setVisibility(View.GONE);

            File tmpFile = new File(mSelSDInfo.getRealPath() + mSelSDInfo.getSzFileName());
            boolean bCompleteDown = false;

            Log.d("FILESIZE,", "tmpFile length: " + tmpFile.length() + "sel info file size: " + mSelSDInfo.getnFileSize()
                    + " FILEPATH: " + mSelSDInfo.getRealPath() + mSelSDInfo.getSzFileName()
                    + " FILE LENGTH: " + tmpFile.length());
            if (tmpFile.exists() && tmpFile.isFile()) {
                bCompleteDown = tmpFile.length() == mSelSDInfo.getnFileSize();
            } else {
                bCompleteDown = false;
            }

            if (bCompleteDown) {
                mAdapter.showbuttonType(mSelholder, 3);
            } else {
                mAdapter.showbuttonType(mSelholder, 1);
                toast.ToastShow(this, getString(R.string.download_failed), Toast.LENGTH_LONG);
            }


            if (dlInfoList.size() != 0) {
                dlInfoList.remove(0);
                dlHolderList.remove(0);
            }
            if (dlInfoList.size() != 0) {
                startdownload();
                isDownLoading = true;
            } else {
                Log.i(TAG, "全锟斤拷锟斤拷锟斤拷锟斤拷桑锟斤拷锟斤拷锟斤拷斜锟轿拷锟�");
                isDownLoading = false;
            }
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void computepercent(int dper)//		buf+curSizw/totall size
    {

        mSelholder.numbar.setProgress(dper);
    }

    public void delNonCompleteVideo(SDFileInfo mSDFileInfo) {

        File f = new File(mSDFileInfo.getRealPath() + "/" + mSDFileInfo.getSzFileName());
        if (f.exists()) {
            f.delete();
            Log.i(TAG, "删锟斤拷未锟斤拷锟斤拷锟斤拷锟斤拷募锟斤拷锟�" + mSDFileInfo.getSzFileName());
        } else {
            Log.i(TAG, "锟侥硷拷锟斤拷锟斤拷锟斤拷");
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<SDRecordFileActivity> mRef;

        public MyHandler(SDRecordFileActivity mAct) {
            mRef = new WeakReference<SDRecordFileActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef == null)
                return;

            SDRecordFileActivity mActivity = mRef.get();

            if (mActivity != null) {
                switch (msg.what) {
                    case MSG_TIME_OUT:
                    case MSG_DISCONNECT:
                        mActivity.emptyView.setVisibility(View.INVISIBLE);
                        break;
                    case MSG_UPDATE:
                        mActivity.mAdapter.notifyDataSetChanged();
                        mActivity.progressView.setVisibility(View.INVISIBLE);
                        mActivity.waitTextView.setVisibility(View.INVISIBLE);
                        mActivity.emptyView.setVisibility(View.INVISIBLE);
                        break;
                    case MSG_ClEAR_PROGRESSBAR:
                        mActivity.progressView.setVisibility(View.INVISIBLE);
                        mActivity.waitTextView.setVisibility(View.INVISIBLE);
                        mActivity.emptyView.setVisibility(View.INVISIBLE);
                        break;
                    case MSG_START_DOWNLOAD:

                        break;

                    case MSG_PERCENT_PROCESSBAR: {
                        mActivity.computepercent(msg.arg1);
                    }
                    break;
                    case MSG_DELETE_SUCCESS:
                        mActivity.refreshsdfile();
                        break;
                    case MSG_NEXT_DOWNLOAD: {
                        mActivity.nextDownload();
                    }
                    break;
                    case MSG_UPDATE_DATA:
                        synchronized (mActivity.mFileList) {
                            mActivity.mFileList.clear();

                            if (mActivity.strFileList != null) {
                                String[] strItem = mActivity.strFileList.split("\\|");

                                for (int i = 1; i < strItem.length; i++) {
                                    String[] itemText = strItem[i].split(",");

                                    if (itemText.length != 2)
                                        continue;

                                    int filesize = Integer.parseInt(itemText[1]);
                                    if (filesize < 1024)
                                        continue;

                                    SDFileInfo tmp = new SDFileInfo();
                                    tmp.setSzDeviceid(mActivity.deviceID);
                                    tmp.setSzFileName(itemText[0]);
                                    tmp.setnFileSize(filesize);
                                    if (!itemText[0].contains("tmp")) {
                                        mActivity.mFileList.add(0, tmp);
                                    }
                                }
                                mActivity.mHandler.sendEmptyMessage(MSG_UPDATE);
                            }
                        }
                        break;
                    case MSG_REFRESH:
                        mActivity.refreshsdfile();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
