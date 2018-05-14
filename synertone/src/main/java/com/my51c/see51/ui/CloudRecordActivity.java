package com.my51c.see51.ui;

import android.content.Intent;
import android.net.Uri;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.my51c.see51.adapter.CloudRecordFileListAdapter;
import com.my51c.see51.adapter.CloudRecordFileListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.CloudFileInfo;
import com.my51c.see51.data.CloudHandle;
import com.my51c.see51.data.Device;
import com.my51c.see51.media.cloudsdk;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CloudRecordActivity extends SherlockActivity implements OnClickListener, OnRefreshListener, OnItemClickListener {
    static final int MSG_UPDATE = 0;
    static final int MSG_ClEAR_PROGRESSBAR = 1;
    static final int MSG_START_DOWNLOAD = 2;
    static final int MSG_STOP_DOWNLOAD = 3;
    static final int MSG_PERCENT_PROCESSBAR = 4;
    static final int MSG_UPDATE_DATA = 5;
    static final int GHDSC_OK = 0;
    static final int GHDSC_ERROR = -1;
    static final int GHDSC_ERROR_NET_INIT = -2;
    static final int GHDSC_ERROR_INVALID_PARAM = -3;
    static final int GHDSC_ERROR_LACK_DEPENDENCY = -4;
    static final int GHDSC_ERROR_INIT_DEPENDENCY = -5;
    static final int GHDSC_ERROR_CONNECT_FAILED = -6;
    static final int GHDSC_ERROR_LOGIN_FAILED = -7;
    static final int GHDSC_ERROR_NOT_EXIST = -8;
    static final int HDS_EVENT = 0;
    static final int HDS_VIDEO = 1;
    public static String curStartTime;
    public static String curEndTime;
    DataOutputStream dos;
    private DeviceListView listViewSDRecord;
    private ArrayList<CloudFileInfo> mFileList;
    private CloudRecordFileListAdapter adapter;
    private View progressView;
    private View waitTextView;
    private View emptyView;
    private cloudsdk csdk;
    private CloudHandle chParam;
    private String deviceID;
    private CloudFileInfo mSelSDInfo;
    private ViewHolder mSelholder;
    private File mFileDownload;
    private boolean bdownload = false;
    private Device mDevice = null;
    private AppData appData;
    private Timer timer;
    private TimerTask timerTask;
    private int nFilePer = 0;
    //bm-modi-1101 begain
    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloudrecord_activity);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.cloud_back_layout);
        ImageView searchImg = (ImageView) findViewById(R.id.cloud_search_img);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });
        searchImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(CloudRecordActivity.this, SDCalendarActivity.class);
                intent.putExtra("isAllClick", true);
                intent.putExtra("deviceID", deviceID);
                startActivity(intent);
            }
        });
        appData = (AppData) getApplication();

        listViewSDRecord = (DeviceListView) findViewById(android.R.id.list);
        listViewSDRecord.setItemsCanFocus(true);
        listViewSDRecord.setonRefreshListener(this);

        mFileList = new ArrayList<CloudFileInfo>();
        adapter = new CloudRecordFileListAdapter(getApplicationContext(), mFileList);
        adapter.setOnClickListener(this);
        listViewSDRecord.setAdapter(adapter);
        listViewSDRecord.setOnItemClickListener(this);

        progressView = findViewById(R.id.progress_get_devices_image);
        waitTextView = findViewById(R.id.loading);
        emptyView = findViewById(R.id.emptyView);

        Bundle bundle = getIntent().getExtras();
        deviceID = bundle.getString("id");

        getnowdate();
    }

    private void starttimer() {
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mHandler.sendEmptyMessage(MSG_PERCENT_PROCESSBAR);
                }
            };
        }


        if (timer != null && timerTask != null)
            timer.schedule(timerTask, 0, 1000);

        nFilePer = 0;
    }

    private void stoptimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

    }

    public void backMainActivity() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    //bm-modi-1101 end

    public void getnowdate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        curStartTime = String.valueOf(cal.getTimeInMillis() / 1000);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        curEndTime = String.valueOf(cal.getTimeInMillis() / 1000);
    }

    public void computepercent(int nAddSize) {
        mSelholder.numbar.setProgress(nAddSize);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (!PlayAcy.isPlayBack) {
            mFileList.clear();
            adapter.notifyDataSetChanged();
            progressView.setVisibility(View.VISIBLE);
            waitTextView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            mDevice = appData.getAccountInfo().getCurrentList().getDevice(deviceID);
            String cloudurl = ParseCloudAddr(mDevice);
            csdk = new cloudsdk();
            chParam = new CloudHandle();
            chParam.setStrADKPath("/temp/");
            csdk.Native_GHDSCClient_Init(chParam);
            csdk.Native_GHDSCClient_Create(chParam);

            String paramaddr = "AddrRemote[=]";
            paramaddr += cloudurl;
            paramaddr += "[|]TOConnect[=]60[|]TORW[=]60[|]";
            chParam.setStrParam(paramaddr);
            chParam.setStrUsername("guest");
            chParam.setStrPassword("guest");
            chParam.setStrSN(deviceID);
            new Thread() {
                @Override
                public void run() {
                    int nRet = csdk.Native_GHDSCClient_Connect(chParam);
                    if (nRet == GHDSC_OK) {
                        queryvideoinfo(curStartTime, curEndTime);
                    }

                    mHandler.sendEmptyMessage(MSG_UPDATE);
                }
            }.start();
        } else {
            PlayAcy.isPlayBack = false;
        }
        super.onResume();
    }

    private String ParseCloudAddr(Device device) {
        String cloudUrl = null;
        if (device == null)
            return cloudUrl;

        String playurl = null;
        if (mDevice.getSee51Info() != null)
            playurl = mDevice.getSee51Info().getDataURL();

        if (playurl == null)
            return cloudUrl;

        String[] urls = playurl.split(";");
        int i = 0;
        if (urls != null) {
            for (i = 0; i < urls.length; i++) {
                if (urls[i].toLowerCase().startsWith("cloud://")) {
                    cloudUrl = urls[i].substring(8);
                    String strs[] = cloudUrl.split(":");
                    String port = strs[1];
                    Log.i("CloudRecordAcy", "--PORT:" + port);
                    cloudUrl = strs[0] + ":5557";
                    break;
                }
            }
        }

        if (cloudUrl == null) {
            Log.i("CloudRecordAcy", "url为空，设置默认地址");
            cloudUrl = "221.214.50.79:5557";
        }
        return cloudUrl;
    }

    private void queryvideoinfo(String strStart, String strEnd) {
        String strWhere;
        strWhere = "video_time >= ";
        strWhere += strStart;
        strWhere += " and video_time <= ";
        strWhere += strEnd;

        chParam.setStrQueryVideoCountWhere(strWhere);
        if (GHDSC_OK == csdk.Native_GHDSCClient_Query_Count_Video(chParam)) {
            if (chParam.getlQueryVideoCount() != 0) {
                chParam.setStrQueryVideoDataWhere(strWhere);
                chParam.setStrQueryVideoDataColumn("video_time,video_type,video_status,video_size");
                chParam.setlQueryVideoDataPos(0);
                chParam.setlQueryVideoDataCount(chParam.getlQueryVideoCount());

                if (GHDSC_OK == csdk.Native_GHDSCClient_Query_Data_Video(chParam)) {
                    DealWithXml(HDS_VIDEO, chParam.getbQueryVideoData());
                }
            }
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //csdk.Native_GHDSCClient_Destory(chParam);
        //csdk.Native_GHDSCClient_Fini(chParam);
        super.onStop();
    }

    //bm-add-1101 begain
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        ViewHolder vhold = (ViewHolder) view.getTag();
        mSelholder = vhold;
        CloudFileInfo tmp = (CloudFileInfo) vhold.tvfilename.getTag();
        mSelSDInfo = tmp;

        if (vhold.ivdownload.getVisibility() == View.VISIBLE) {
            startdownload();
            adapter.showbuttonType(vhold, 2);
            return;
        }

        if (vhold.ivcanceldownload.getVisibility() == View.VISIBLE) {
            stopdownload();
            adapter.showbuttonType(vhold, 1);
            return;
        }

        if (vhold.ivplay.getVisibility() == View.VISIBLE) {
            playVideo(tmp.getRealPath() + tmp.getRealFileName(), tmp.getRealFileName());
            adapter.showbuttonType(vhold, 3);
            return;
        }
    }
//bm-add-1101 end

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
//		queryvideoinfo(curStartTime, curEndTime);
        mHandler.sendEmptyMessageDelayed(500, MSG_UPDATE_DATA);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        ViewHolder vhold = (ViewHolder) v.getTag();
        mSelholder = vhold;
        CloudFileInfo tmp = (CloudFileInfo) vhold.tvfilename.getTag();
        mSelSDInfo = tmp;

        switch (v.getId()) {
            case R.id.iv_download: {
                startdownload();
                adapter.showbuttonType(vhold, 2);
            }
            break;
            case R.id.iv_cancel: {
                stopdownload();
                adapter.showbuttonType(vhold, 1);
            }
            break;
            case R.id.iv_play: {
                playVideo(tmp.getRealPath() + tmp.getRealFileName(), tmp.getRealFileName());
                adapter.showbuttonType(vhold, 3);
            }
            break;
            default:
                break;
        }
    }

    public void startdownload() {
        stopdownload();

        if (mSelSDInfo == null) {
            return;
        }

        String videopath = mSelSDInfo.getRealPath();
        File mVideoPath = new File(videopath);
        if (!mVideoPath.exists()) {
            mVideoPath.mkdirs();
        }

        mFileDownload = new File(mVideoPath, mSelSDInfo.getRealFileName());

        chParam.setlDownloadVideoInfoTime(mSelSDInfo.getlTime());
        chParam.setiDownloadVideoInfoType(mSelSDInfo.getlType());


        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFileDownload)));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            dos = null;
            e.printStackTrace();
        }
        bdownload = true;
        mSelholder.numbar.setVisibility(View.VISIBLE);
        mSelSDInfo.setbDown(bdownload);
        starttimer();
        new Thread() {
            @Override
            public void run() {

                int nRet = csdk.Native_GHDSCClient_Download_Info_Video(chParam);
                if (nRet == GHDSC_OK) {
                    chParam.setlDownloadVideoDataContex(chParam.getlDownloadVideoInfoContex());
                    chParam.setlDownloadVideoDataTime(mSelSDInfo.getlTime());
                    chParam.setiDownloadVideoDataType(mSelSDInfo.getlType());
                    chParam.setlDownloadVideoDataPos(0);
                    chParam.setlDownloadVideoDataCount(mSelSDInfo.getnFileSize());
                    if (GHDSC_OK == csdk.Native_GHDSCClient_Download_data_Video(chParam)) {
                        try {
                            dos.write(chParam.getbDownloadVideoData());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(MSG_STOP_DOWNLOAD);

                    }

                }
                mHandler.sendEmptyMessage(MSG_UPDATE);

            }
        }.start();


    }

    public void stopdownload() {

        if (dos != null) {
            try {
                dos.flush();
                dos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                dos = null;
                e.printStackTrace();
            }
        }
        stoptimer();
        bdownload = false;
        mSelholder.numbar.setVisibility(View.GONE);
        mSelSDInfo.setbDown(bdownload);

    }

    private void playVideo(String fileName, String name) {

        //Log.d("open file", fileName);
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }
        if (fileName.endsWith(".jpg")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
            startActivity(intent);
        } else {
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.fromFile(file), "video/mp4");
//			startActivity(intent);
            Intent intent = new Intent(CloudRecordActivity.this, PlayAcy.class);
            intent.putExtra("string", fileName);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    void DealWithXml(int nType, byte[] buf) {
        switch (nType) {
            case HDS_EVENT:
                break;
            case HDS_VIDEO: {
                parseVideoXml(buf);
            }
            break;

        }
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

    void parseVideoXml(byte[] buf) {
        Log.i("CloudRecordActivity", byteToString(buf));
        mFileList.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String orgstring = null;
        try {
            orgstring = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String xmlstring = "<?xml version=\"1.0\" encoding=\"utf-8\"?><videos>";
        xmlstring += orgstring;
        xmlstring += "</videos>";

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xmlstring.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            doc = builder.parse(is);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Element rootElement = null;
        if (doc != null) {
            rootElement = doc.getDocumentElement();
        }


        NodeList items = null;
        if (rootElement != null) {
            items = rootElement.getElementsByTagName("video");
        }

        if (items == null)
            return;

        for (int i = 0; i < items.getLength(); i++) {
            CloudFileInfo info = new CloudFileInfo();
            info.setSzDeviceid(deviceID);
            Node item = items.item(i);
            NodeList properties = item.getChildNodes();
            for (int j = 0; j < properties.getLength(); j++) {
                Node property = properties.item(j);
                String nodeName = property.getNodeName();
                if (nodeName.equals("video_time")) {
                    info.setlTime(Long.parseLong(property.getFirstChild().getNodeValue()));
                } else if (nodeName.equals("video_type")) {
                    info.setlType(Integer.parseInt(property.getFirstChild().getNodeValue()));
                } else if (nodeName.equals("video_status")) {
                    info.setlStatus(Integer.parseInt(property.getFirstChild().getNodeValue()));
                } else if (nodeName.equals("video_size")) {
                    info.setnFileSize(Long.parseLong(property.getFirstChild().getNodeValue()));
                }
            }
            if (info.getnFileSize() / 1024 > 20) {
                mFileList.add(info);
            }
        }

        Collections.sort(mFileList, new Comparator<CloudFileInfo>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,�Ƚ����ݴ�Сʱ,����ȵ���ʱ��
             */
            @Override
            public int compare(CloudFileInfo lhs, CloudFileInfo rhs) {
                long date1 = lhs.getlTime();
                long date2 = rhs.getlTime();
                // �������ֶν����������������ɲ���after����
                if (date1 > date2) {
                    return -1;
                }

                if (date1 == date2) {
                    return 0;
                }
                return 1;
            }
        });
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    private static class MyHandler extends Handler {
        private WeakReference<CloudRecordActivity> mRef;

        public MyHandler(CloudRecordActivity mAct) {
            mRef = new WeakReference<CloudRecordActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef == null)
                return;

            CloudRecordActivity activity = mRef.get();

            if (activity != null) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        activity.adapter.notifyDataSetChanged();
                        activity.progressView.setVisibility(View.INVISIBLE);
                        activity.waitTextView.setVisibility(View.INVISIBLE);
                        activity.emptyView.setVisibility(View.INVISIBLE);
                        break;
                    case MSG_ClEAR_PROGRESSBAR:
                        activity.progressView.setVisibility(View.INVISIBLE);
                        activity.waitTextView.setVisibility(View.INVISIBLE);
                        activity.emptyView.setVisibility(View.INVISIBLE);
                        break;
                    case MSG_START_DOWNLOAD:

                        break;

                    case MSG_PERCENT_PROCESSBAR: {
                        if (activity.nFilePer < 90)
                            activity.nFilePer += 10;
                        else if (activity.nFilePer < 99)
                            activity.nFilePer += 1;
                        activity.computepercent(activity.nFilePer);
                    }
                    break;

                    case MSG_STOP_DOWNLOAD: {
                        activity.stopdownload();
                    }
                    break;
                    case MSG_UPDATE_DATA:
                        activity.queryvideoinfo(curStartTime, curEndTime);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
