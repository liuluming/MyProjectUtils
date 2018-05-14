package com.my51c.see51.ui;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.SelectionDevice;
import com.my51c.see51.listener.OnAVQSetListener;
import com.my51c.see51.listener.OnAlarmEnableListener;
import com.my51c.see51.listener.OnGetSnapShotListener;
import com.my51c.see51.listener.OnGetVideoConnectionStatusListener;
import com.my51c.see51.media.AACRecord;
import com.my51c.see51.media.H264toMP4;
import com.my51c.see51.media.MediaStreamer;
import com.my51c.see51.media.PlayMediaItem;
import com.my51c.see51.media.PlayMediaItem.RecState;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class FourPlayerActivity extends SherlockFragmentActivity implements OnClickListener {
    private final String TAG = "FourPlayerActivity";
    Button btnFlipH;
    Button btnFlipV;
    Button btnSnapshoot;
    Button btnRecord;
    Button btnAlarm;
    Button btnDefinition;
    Button btnUp;
    Button btnDown;
    Button btnLeft;
    Button btnRight;
    H264toMP4 recorder;
    MyHandler mHandler;
    private PlayMediaItem[] windowItem = new PlayMediaItem[4];
    private ViewFlipper[] viewFlipperArray = new ViewFlipper[4];
    private int[] viewshowboolean = new int[]{-1, -1, -1, -1};
    private View[] viewDisLayout = new View[4];
    private boolean enableAudio = false;
    private boolean enableIntercom = false;
    private ToastCommom toast = new ToastCommom();
    private AppData app;
    private List<SelectionDevice> m_selectdevice;
    private int nSelectDevNum = 0;
    private TextView titleName;
    private ImageView singleView;
    private int currentIndx = 0;
    private boolean isSingleView = false;
    private TableRow viewRow1, viewRow2;
    private View landScapeControlBar;
    private View topLayout;
    private Button btnAudio;
    private Button btnIntercom;
    private MediaStreamer curMediaStreamer;
    private GestureDetector gestureScanner;
    private OnAlarmEnableListener mOnAlarmEnableListener = new OnAlarmEnableListener() {
        @Override
        public void onAction() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(PlayMediaItem.MSG_ALARM_STATE_CHANGED);
        }
    };
    private OnAVQSetListener mOnAVQSetListener = new OnAVQSetListener() {
        @Override
        public void onAction() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(PlayMediaItem.MSG_AVQ_CHANGED);
        }
    };
    private OnGetSnapShotListener mOnGetSnapShotListener = new OnGetSnapShotListener() {

        @Override
        public void onAction() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(PlayMediaItem.MSG_SNAP_OK);
        }

        @Override
        public void onFailAction() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(PlayMediaItem.MSG_SNAP_FAIL);
        }

    };
    private OnGetVideoConnectionStatusListener mOnGetVideoConnectionStatusListener = new OnGetVideoConnectionStatusListener() {
        Message msg;
        MSGHandler msghandler = new MSGHandler();

        @Override
        public void onVideoConnection(String deviceid, int nIndex) {
            // TODO Auto-generated method stub

            msghandler.setDeviceid(deviceid);
            msghandler.setnIndex(nIndex);
            msg = mHandler.obtainMessage(PlayMediaItem.MSG_GET_IFRAME, msghandler);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onVideoDisconnection(String deviceid, int nIndex) {
            // TODO Auto-generated method stub
            msghandler.setDeviceid(deviceid);
            msghandler.setnIndex(nIndex);
            msg = mHandler.obtainMessage(PlayMediaItem.MSG_DISCONNECT, msghandler);
            mHandler.sendMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fourplayerwindow);

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.fourwindows_back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FourPlayerActivity.this.finish();
            }
        });

        mHandler = new MyHandler(this);
        gestureScanner = new GestureDetector(new gestureListener());
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Configuration cf = this.getResources().getConfiguration();
        onConfigurationChanged(cf);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        landScapeControlBar = findViewById(R.id.fourwindows_controlpanel);
        topLayout = findViewById(R.id.fourwindows_topLayout);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            topLayout.setVisibility(View.GONE);
            landScapeControlBar.setVisibility(View.GONE);
        } else {

            landScapeControlBar.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        app = (AppData) getApplication();
        m_selectdevice = app.getM_selectdevice();
        nSelectDevNum = m_selectdevice.size();

        for (int i = 0; i < nSelectDevNum; i++) {
            windowItem[i] = new PlayMediaItem(i);
        }

        findView();

        SelectionDevice devitem = null;
        for (int i = 0; i < nSelectDevNum; i++) {
            devitem = m_selectdevice.get(i);
            windowItem[i].init(this, devitem.getDeviceid(), devitem.getDevicename(), devitem.getUrl(), devitem.isLocal());
        }


        for (int i = 0; i < 4; i++) {
            viewFlipperArray[i].removeAllViews();
            viewshowboolean[i] = -1;
        }

        for (int i = 0; i < nSelectDevNum; i++) {
            viewshowboolean[i] = 1;
            viewFlipperArray[i].addView(windowItem[i].getGlSurfaceView());
            viewFlipperArray[i].setOnTouchListener(new viewFlipperTouchListener(i));
        }

        for (int i = 0; i < nSelectDevNum; i++) {

            if (windowItem[i].getGlSurfaceView() != null) {
                windowItem[i].getGlSurfaceView().onResume();
            }
            windowItem[i].setmOnGetVideoConnectionStatusListener(mOnGetVideoConnectionStatusListener);
            windowItem[i].start();

        }

        for (int i = 0; i < viewFlipperArray.length; i++) {
            viewFlipperArray[i].setOnClickListener(new viewFlipperListener(i));
        }
    }

    private void setDefaultStreamer() {
        if (curMediaStreamer == null) {
            if (windowItem.length != 0) {
                if (windowItem[0] != null) {
                    curMediaStreamer = windowItem[0].getMediaStreamer();
                    windowItem[0].setmOnGetVideoConnectionStatusListener(mOnGetVideoConnectionStatusListener);
                    titleName.setText(m_selectdevice.get(0).getDevicename() + "-" + m_selectdevice.get(0).getDeviceid());
                }
            }
        }
    }

    public void showview(int nIndex, boolean bshow) {

        if (windowItem[nIndex] != null) {
            viewFlipperArray[nIndex].removeAllViews();
            if (bshow) {
                if (windowItem[nIndex].getGlSurfaceView() != null) {
                    viewFlipperArray[nIndex].addView(windowItem[nIndex].getGlSurfaceView());
                    viewshowboolean[nIndex] = 1;
                }
            } else {
                if (viewDisLayout[nIndex] != null) {
                    viewFlipperArray[nIndex].addView(viewDisLayout[nIndex]);
                    viewshowboolean[nIndex] = 0;
                }
            }

        }

    }

    public void setViewFlipperBac(int index) {
        for (int i = 0; i < viewFlipperArray.length; i++) {

            if (i == index) {
                viewFlipperArray[i].setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                viewFlipperArray[i].setBackgroundColor(getResources().getColor(R.color.black));
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        for (int i = 0; i < nSelectDevNum; i++) {
            if (windowItem[i].getGlSurfaceView() != null) {
                windowItem[i].getGlSurfaceView().onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        for (int i = 0; i < nSelectDevNum; i++) {
            viewFlipperArray[i].removeAllViews();
            windowItem[i].stop();
            windowItem[i].uninit();
            windowItem[i] = null;
            viewFlipperArray[i] = null;
        }

        if (app != null) {
            app = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        gestureScanner = null;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }

    public void findView() {
        viewFlipperArray[0] = (ViewFlipper) findViewById(R.id.viewFlipperPlay1);
        viewFlipperArray[1] = (ViewFlipper) findViewById(R.id.viewFlipperPlay2);
        viewFlipperArray[2] = (ViewFlipper) findViewById(R.id.viewFlipperPlay3);
        viewFlipperArray[3] = (ViewFlipper) findViewById(R.id.viewFlipperPlay4);

        LayoutInflater mLi = LayoutInflater.from(this);

        viewDisLayout[0] = mLi.inflate(R.layout.disconnect, null);
        viewDisLayout[1] = mLi.inflate(R.layout.disconnect, null);
        viewDisLayout[2] = mLi.inflate(R.layout.disconnect, null);
        viewDisLayout[3] = mLi.inflate(R.layout.disconnect, null);


        titleName = (TextView) findViewById(R.id.titleName);
        singleView = (ImageView) findViewById(R.id.singleView);
        viewRow1 = (TableRow) findViewById(R.id.viewRow1);
        viewRow2 = (TableRow) findViewById(R.id.viewRow2);

        btnFlipH = (Button) findViewById(R.id.imageButtonFlipH);
        btnFlipV = (Button) findViewById(R.id.imageButtonFlipV);
        btnIntercom = (Button) findViewById(R.id.imageButtonInterCom);
        btnAudio = (Button) findViewById(R.id.imageButtonAudio);
        btnSnapshoot = (Button) findViewById(R.id.imageButtonSnap);
        btnRecord = (Button) findViewById(R.id.imageButtonRecord);
        btnAlarm = (Button) findViewById(R.id.imageButtonAlarm);
        btnDefinition = (Button) findViewById(R.id.imageButtonDefinition);

        btnUp = (Button) findViewById(R.id.imageButtonUpH);
        btnDown = (Button) findViewById(R.id.imageButtonDownH);
        btnLeft = (Button) findViewById(R.id.imageButtonLeftH);
        btnRight = (Button) findViewById(R.id.imageButtonRightH);

        singleView.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnIntercom.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);
        btnFlipH.setOnClickListener(this);
        btnFlipV.setOnClickListener(this);
        btnSnapshoot.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnDefinition.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.singleView:
                for (int i = 0; i < viewFlipperArray.length; i++) {
                    if (i != currentIndx) {

                        if (isSingleView) {
                            viewFlipperArray[i].setVisibility(View.VISIBLE);

                            if (windowItem[i] != null && windowItem[i].getGlSurfaceView() != null) {

                                if (viewshowboolean[i] == 1) {
                                    viewFlipperArray[i].addView(windowItem[i].getGlSurfaceView());
                                } else if (viewshowboolean[i] == 0) {
                                    viewFlipperArray[i].addView(viewDisLayout[i]);
                                }
                            }

                        } else {
                            viewFlipperArray[i].setVisibility(View.GONE);
                            viewFlipperArray[i].removeAllViews();

                        }
                    }
                }


                if (currentIndx < 2) {
                    viewRow2.setVisibility(isSingleView ? View.VISIBLE : View.GONE);
                } else {
                    viewRow1.setVisibility(isSingleView ? View.VISIBLE : View.GONE);
                }

                isSingleView = !isSingleView;
                break;
            case R.id.imageButtonFlipH:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                Log.d(TAG, "imageButtonFlipH");
                curMediaStreamer.flipH();
                break;
            case R.id.imageButtonFlipV:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                Log.d(TAG, "imageButtonFlipV");
                curMediaStreamer.flipV();
                break;

            case R.id.imageButtonUpH:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                curMediaStreamer.rollUp();
                break;
            case R.id.imageButtonDownH:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                curMediaStreamer.rollDown();
                break;
            case R.id.imageButtonLeftH:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                curMediaStreamer.turnLeft();
                break;
            case R.id.imageButtonRightH:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                curMediaStreamer.turnRight();
                break;
            case R.id.imageButtonAudio:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;
                enableAudio = !enableAudio;
                onSetAudio(enableAudio, currentIndx);
                break;
            case R.id.imageButtonInterCom:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;
                enableIntercom = !enableIntercom;
                onSetInterCom(enableIntercom, currentIndx);
                break;
            case R.id.imageButtonAlarm:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                if (curMediaStreamer.m_bAlarmEnable == 0) {
                    curMediaStreamer.m_bAlarmEnable = 1;
                } else {
                    curMediaStreamer.m_bAlarmEnable = 0;
                }

                curMediaStreamer.setAlarmEnable();

                RefreshAlarmEnableState();
                break;
            case R.id.imageButtonDefinition:

                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                if (windowItem[currentIndx].getRecState() != RecState.STOP) {
                    toast.ToastShow(getApplicationContext(), getString(R.string.stoprecord), 800);
                    break;
                }

                if (curMediaStreamer.m_nDefinitionCurrent == 3) {
                    curMediaStreamer.m_nDefinitionCurrent = 1;//��
                } else if (curMediaStreamer.m_nDefinitionCurrent == 5) {
                    curMediaStreamer.m_nDefinitionCurrent = 3;
                } else if (curMediaStreamer.m_nDefinitionCurrent == 1) {
                    curMediaStreamer.m_nDefinitionCurrent = 5;//��
                }

                curMediaStreamer.setDefinition();
                RefreshAVQState();//UI
                break;
            case R.id.imageButtonSnap:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                if (windowItem[currentIndx] == null || windowItem[currentIndx].getDrawSurfaceThread() == null || !windowItem[currentIndx].getDrawSurfaceThread().isAlive())
                    break;

                windowItem[currentIndx].setOnGetSnapShotListener(mOnGetSnapShotListener);
                windowItem[currentIndx].setmOnGetVideoConnectionStatusListener(mOnGetVideoConnectionStatusListener);
                windowItem[currentIndx].startSnapPic();
                break;
            case R.id.imageButtonRecord:
                if (curMediaStreamer == null) {
                    //setDefaultStreamer();
                    return;
                }

                if (curMediaStreamer == null)
                    break;

                if (windowItem[currentIndx] == null)
                    break;

                if (windowItem[currentIndx].getFps() == -1) {
                    toast.ToastShow(getApplicationContext(), getString(R.string.videoloading), 800);
                } else {                                    //��Ƶ׼������
                    if (windowItem[currentIndx].getRecState() != RecState.STOP) {
                        windowItem[currentIndx].doRecordAction(false);
                        Drawable img = getResources().getDrawable(R.drawable.record);
                        btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                        break;
                    }

                    if (windowItem[currentIndx].doRecordAction(true)) {
                        Drawable img = getResources().getDrawable(R.drawable.video_record_high_light);
                        btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    }
                }
                break;
            default:
                break;

        }
    }

    private void RefreshAlarmEnableState() {
        if (curMediaStreamer == null) {
            //setDefaultStreamer();
            return;
        }

        if (curMediaStreamer == null)
            return;

        if (curMediaStreamer.m_bAlarmEnable == 0) {
            Drawable img = getResources().getDrawable(R.drawable.alarm_off);
            btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        } else {
            Drawable img = getResources().getDrawable(R.drawable.alarm_on);
            btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
    }

    private void RefreshAVQState() {
        if (curMediaStreamer == null) {
            //setDefaultStreamer();
            return;
        }

        if (curMediaStreamer == null)
            return;

        if (curMediaStreamer.m_nDefinitionCurrent == 1) //1 3 5 max mid min
        {
            Drawable img = getResources().getDrawable(R.drawable.avq_high);
            btnDefinition.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        } else if (curMediaStreamer.m_nDefinitionCurrent == 3) {
            Drawable img = getResources().getDrawable(R.drawable.avq_mid);
            btnDefinition.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        } else if (curMediaStreamer.m_nDefinitionCurrent == 5) {
            Drawable img = getResources().getDrawable(R.drawable.avq_min);
            btnDefinition.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
    }

    public void onRecordStatus(boolean bRecord, int i) {
        if (windowItem[i] == null)
            return;

        if (bRecord) {
            Drawable imgpressed = getResources().getDrawable(R.drawable.video_record_high_light);
            btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, imgpressed, null, null);

        } else {

            Drawable img = getResources().getDrawable(R.drawable.record);
            btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        }
    }

    public void onSetAudio(boolean bAudio, int i) {
        if (windowItem[i] == null)
            return;

        if (bAudio) {
            windowItem[i].startAudio();

            Drawable img = getResources().getDrawable(R.drawable.player_intercom_on);
            btnAudio.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            windowItem[i].stopAudio();
            Drawable img = getResources().getDrawable(R.drawable.player_intercom_off);
            btnAudio.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        }
    }

    public void onSetInterCom(boolean bIntercom, int i) {
        if (windowItem[i] == null)
            return;

        String s = windowItem[i].getDeviceID().substring(0, 1);
        if (s.equals("c")) {
            AACRecord.RECORDER_SAMPLERATE = 16000;
        } else {
            AACRecord.RECORDER_SAMPLERATE = 8000;
        }
        if (bIntercom) {
            windowItem[i].onSetInterComAction(bIntercom);

            final Drawable drawableTop = getResources().getDrawable(R.drawable.mic_on);
            btnIntercom.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);

        } else {
            windowItem[i].onSetInterComAction(bIntercom);

            final Drawable drawableTop = getResources().getDrawable(R.drawable.mic_off);
            btnIntercom.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);

        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<FourPlayerActivity> ref;

        public MyHandler(FourPlayerActivity mAct) {
            ref = new WeakReference<FourPlayerActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (ref == null)
                return;

            FourPlayerActivity mActivity = ref.get();
            if (mActivity != null) {
                MSGHandler msghandler;
                switch (msg.what) {
                    case 0:
                        mActivity.RefreshAVQState();
                        mActivity.RefreshAlarmEnableState();
                        break;
                    case PlayMediaItem.MSG_ALARM_STATE_CHANGED:
                        mActivity.RefreshAlarmEnableState();
                        break;
                    case PlayMediaItem.MSG_AVQ_CHANGED:
                        mActivity.RefreshAVQState();
                        break;
                    case PlayMediaItem.MSG_SNAP_OK:
                        mActivity.toast.ToastShow(mActivity.getApplicationContext(), mActivity.getString(R.string.snapok), 800);
                        break;
                    case PlayMediaItem.MSG_SNAP_FAIL:
                        mActivity.toast.ToastShow(mActivity.getApplicationContext(), mActivity.getString(R.string.snapfail), 800);
                        break;
                    case PlayMediaItem.MSG_DISCONNECT:
                        msghandler = (MSGHandler) msg.obj;
                        mActivity.toast.ToastShow(mActivity.getApplicationContext(), mActivity.getString(R.string.device)
                                + msghandler.getDeviceid() + mActivity.getString(R.string.offlinetip), 800);
                        mActivity.showview(msghandler.getnIndex(), false);
                        break;
                    case PlayMediaItem.MSG_GET_IFRAME:
                        mActivity.setDefaultStreamer();
                        msghandler = (MSGHandler) msg.obj;
                        mActivity.showview(msghandler.getnIndex(), true);
                        break;
                    case PlayMediaItem.MSG_TIME_OUT:
                        msghandler = (MSGHandler) msg.obj;
                        mActivity.toast.ToastShow(mActivity.getApplicationContext(), mActivity.getString(R.string.device)
                                + msghandler.getDeviceid() + mActivity.getString(R.string.offlinetip), 800);
                        mActivity.showview(msghandler.getnIndex(), false);

                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private class MSGHandler {
        private String deviceid;
        private int nIndex;

        public String getDeviceid() {
            return deviceid;
        }

        public void setDeviceid(String deviceid) {
            this.deviceid = deviceid;
        }

        public int getnIndex() {
            return nIndex;
        }

        public void setnIndex(int nIndex) {
            this.nIndex = nIndex;
        }
    }

    private class gestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // TODO Auto-generated method stub
            for (int i = 0; i < viewFlipperArray.length; i++) {
                if (i != currentIndx) {

                    if (isSingleView) {
                        viewFlipperArray[i].setVisibility(View.VISIBLE);

                        if (windowItem[i] != null && windowItem[i].getGlSurfaceView() != null) {
                            viewFlipperArray[i].removeAllViews();
                            if (viewshowboolean[i] == 1) {
                                viewFlipperArray[i].addView(windowItem[i].getGlSurfaceView());
                            } else if (viewshowboolean[i] == 0) {
                                viewFlipperArray[i].addView(viewDisLayout[i]);
                            }

                        }

                    } else {
                        viewFlipperArray[i].setVisibility(View.GONE);

                        if (windowItem[i] != null && windowItem[i].getGlSurfaceView() != null)
                            viewFlipperArray[i].removeAllViews();

                    }
                }
            }

            if (currentIndx < 2) {
                viewRow2.setVisibility(isSingleView ? View.VISIBLE : View.GONE);
            } else {
                viewRow1.setVisibility(isSingleView ? View.VISIBLE : View.GONE);
            }

            isSingleView = !isSingleView;

            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    private class viewFlipperTouchListener implements OnTouchListener {
        private int index;

        public viewFlipperTouchListener(int index) {
            this.index = index;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            gestureScanner.onTouchEvent(event);
            return false;
        }
    }

    private class viewFlipperListener implements OnClickListener {

        private int index;

        public viewFlipperListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            onSetAudio(false, currentIndx);
            onSetInterCom(false, currentIndx);

            if (curMediaStreamer != null) {
                curMediaStreamer.setOnAlarmEnableListener(null);
                curMediaStreamer.setOnAVQSetListener(null);
            }
            setViewFlipperBac(index);
            if (windowItem[index] != null) {
                titleName.setText(m_selectdevice.get(index).getDevicename() + "-" + m_selectdevice.get(index).getDeviceid());
                currentIndx = index;
                curMediaStreamer = null;
                curMediaStreamer = windowItem[currentIndx].getMediaStreamer();

                windowItem[index].setOnGetSnapShotListener(mOnGetSnapShotListener);
                windowItem[index].setmOnGetVideoConnectionStatusListener(mOnGetVideoConnectionStatusListener);
            } else {
                titleName.setText("");
                curMediaStreamer = null;
            }

            if (windowItem[currentIndx] != null && windowItem[currentIndx].getRecState() != RecState.STOP) {
                onRecordStatus(true, currentIndx);
            } else {
                onRecordStatus(false, currentIndx);
            }

            if (curMediaStreamer != null) {
                curMediaStreamer.setOnAlarmEnableListener(mOnAlarmEnableListener);
                curMediaStreamer.setOnAVQSetListener(mOnAVQSetListener);
            }
            mHandler.sendEmptyMessage(0);
        }
    }
}
