package com.my51c.see51.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.widget.LocUtil;
import com.my51c.see51.widget.ScreenBean;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


public class PlayAcy extends BaseActivity implements OnCompletionListener, OnClickListener {
    public static boolean isPlayBack = false;
    private String path;
    private Uri uri;
    private int mVolume = -1;
    private int mMaxVolume;
    private float mBrightness = -1f;
    private int finNum = 0;
    private ImageView pic;
    private TextView textTime;
    private int time;
    private VideoView mVideoView;
    private GestureDetector gestDetector;
    private ScaleGestureDetector scaleDetector;
    private ScreenBean screenBean;
    private Button back;
    private TextView preview, systemTime, chargePercent;
    private ImageView battery_image, chargeLogo;
    private Button playBtn, previousBtn, nextBtn;
    private SeekBar seekBar;
    private TextView currentTime, duration;
    public Handler seekBarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long vCurrent = mVideoView.getCurrentPosition();
            long vDuration = mVideoView.getDuration();
            if (vDuration == 0)
                vDuration = -1;

            int max = seekBar.getMax();
            int s = (int) vCurrent * max / (int) vDuration;
            seekBar.setProgress(s);
            currentTime.setText(toTime((int) vCurrent));
            duration.setText(toTime((int) mVideoView.getDuration()));
        }
    };
    private String name;
    private BatteryReceiver batteryReceiver;
    private String timeS;
    private ArrayList<String> lstFile = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private int position;
    private boolean isPlaying = true;
    private Button pausing;
    private TextView changePercent;
    private Animation topin, topout, in, out;
    private RelativeLayout topLayout, buttomLayout;
    private boolean isGone = true;
    private boolean isFromSDFolderAcy = false;
    private boolean isFromSDRecordAcy = false;
    private Handler missHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pic.setVisibility(View.GONE);
            changePercent.setVisibility(View.GONE);
            textTime.setVisibility(View.GONE);
        }
    };
    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Calendar c = Calendar.getInstance();
            String hour = toSystemTime(c.get(Calendar.HOUR_OF_DAY));
            String minute = toSystemTime(c.get(Calendar.MINUTE));

            timeS = hour + ":" + minute;
            systemTime.setText(timeS);
            super.handleMessage(msg);
        }
    };

    public static String toTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        minute %= 60;//minute = minute%60;����
        int second = time % 60;
        String text = String.format("%02d:%02d:%02d", hour, minute, second);
        return text;
    }

    @Override
    public void onCreate(Bundle icicle) {

        getIntentInfo();
        super.onCreate(icicle);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.acy_play);
        init();
        setseekbar();
        setReceiver();
        getTime();
    }

    public void getIntentInfo() {
        Intent intent = getIntent();
        path = intent.getStringExtra("string");
        name = intent.getStringExtra("name");
        uri = Uri.parse(path.toString());
    }

    private void init() {

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        pic = (ImageView) findViewById(R.id.bright_volume);
        changePercent = (TextView) findViewById(R.id.percent);

        mMaxVolume = LocUtil.getMaxVolume(this);
        textTime = (TextView) findViewById(R.id.playTime);


        back = (Button) findViewById(R.id.play_acy_back);
        preview = (TextView) findViewById(R.id.preView);
        preview.setText(name);
        systemTime = (TextView) findViewById(R.id.system_time);
        battery_image = (ImageView) findViewById(R.id.battery_imageView);
        chargePercent = (TextView) findViewById(R.id.batteryPercent);
        chargeLogo = (ImageView) findViewById(R.id.chargeLogo);

        seekBar = (SeekBar) findViewById(R.id.video_seekbar);
        previousBtn = (Button) findViewById(R.id.previous);
        playBtn = (Button) findViewById(R.id.play);
        nextBtn = (Button) findViewById(R.id.next);
        currentTime = (TextView) findViewById(R.id.current_time);
        duration = (TextView) findViewById(R.id.total_time);

        pausing = (Button) findViewById(R.id.video_pausing);
        topLayout = (RelativeLayout) findViewById(R.id.topLayout);
        buttomLayout = (RelativeLayout) findViewById(R.id.buttomLayout);

        back.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        pausing.setOnClickListener(this);


        mMaxVolume = LocUtil.getMaxVolume(this);
        gestDetector = new GestureDetector(this, new SingleGestureListener());
        screenBean = LocUtil.getScreenPix(this);
        if (uri == null) {
            return;
        } else {
            mVideoView.setVideoURI(uri);
//			mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
            mVideoView.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    backMainAcy();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        finNum = event.getPointerCount();
        if (1 == finNum) {
            gestDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    endGesture();
                    break;
            }
        }
        return true;
    }

    public void backMainAcy() {
        isPlayBack = true;
        PlayAcy.this.finish();
    }

    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        missHandler.removeMessages(0);
        missHandler.sendEmptyMessageDelayed(0, 500);
    }

    public void changeProgress(float f) {

        long l = (long) (-f * mVideoView.getDuration() * 0.1 / screenBean.getsWidth()) + mVideoView.getCurrentPosition();
        mVideoView.seekTo(l);
        time = (int) (mVideoView.getCurrentPosition() / 1000);

        int minute = time / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = time % 60;
        String text = String.format("%02d:%02d:%02d", hour, minute, second);

        textTime.setVisibility(View.VISIBLE);
        pausing.setVisibility(View.GONE);
        playBtn.setBackgroundResource(R.drawable.play_acy_play);
        textTime.setText(text);
        System.out.println(mVideoView.getDuration());

    }

    public void changeBrightness(float percent) {


        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;

            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;

            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            pic.setImageResource(R.drawable.bright);
            changePercent.setVisibility(View.VISIBLE);
            pic.setVisibility(View.VISIBLE);
        }
        //��������
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        int number = (int) (lpa.screenBrightness * 100);
        changePercent.setText(number + "%");
        getWindow().setAttributes(lpa);
    }

    public void changeVolume(float percent) {

        if (mVolume == -1) {
            mVolume = LocUtil.getCurVolume(this);
            if (mVolume < 0)
                mVolume = 0;
            pic.setImageResource(R.drawable.volume_full);
            changePercent.setVisibility(View.VISIBLE);
            pic.setVisibility(View.VISIBLE);
        }
        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        if (index == 0) {
            pic.setImageResource(R.drawable.volume_off);
        } else {
            pic.setImageResource(R.drawable.volume_full);
        }
        LocUtil.setCurVolume(this, index);
        int number = index * 100 / 15;
        changePercent.setText(number + "%");
    }

    private void setAnimations() {
        topin = AnimationUtils.loadAnimation(this, R.anim.topin);
        topout = AnimationUtils.loadAnimation(this, R.anim.topout);
        in = AnimationUtils.loadAnimation(this, R.anim.in);
        out = AnimationUtils.loadAnimation(this, R.anim.out);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		new seekBarThread().interrupt();
        unregister();
    }

    public void setseekbar() {

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int max = (int) mVideoView.getDuration();
                int sCurrent = seekBar.getProgress();
                mVideoView.seekTo(max * sCurrent / seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean arg2) {

            }
        });
        new seekBarThread(this).start();

    }

    //	��ʾ��ص�����ʱ��
    private void getTime() {

        Thread thread = new delayThread(this, 300);
        thread.start();
    }

    private String toSystemTime(int i) {
        String timeS = null;
        if (i < 10) {
            timeS = "0" + i;
        } else {
            timeS = i + "";
        }
        return timeS;
    }

    public void setReceiver() {
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    public void unregister() {
        try {
            unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                System.out.println("�㲥δע��");
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backMainAcy();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.play_acy_back:
                backMainAcy();
                break;
            case R.id.previous:
                if (position == 0) {
                    position = lstFile.size() - 1;
                } else {
                    position = position - 1;
                }
                playOrPause(position);
                break;
            case R.id.next:
                if (position == lstFile.size() - 1) {
                    position = 0;
                } else {
                    position = position + 1;
                }
                playOrPause(position);
                break;
            case R.id.play:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pausing.setVisibility(View.VISIBLE);
                    playBtn.setBackgroundResource(R.drawable.play_acy_pause);
                } else if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                    pausing.setVisibility(View.GONE);
                    playBtn.setBackgroundResource(R.drawable.play_acy_play);
                }
                break;
            case R.id.video_pausing:
                mVideoView.start();
                pausing.setVisibility(View.GONE);
                playBtn.setBackgroundResource(R.drawable.play_acy_play);
                break;

            default:
                break;
        }
    }

    public void playOrPause(int i) {
        mVideoView.pause();
        mVideoView.setVideoURI(Uri.parse(lstFile.get(i)));
        mVideoView.start();
        pausing.setVisibility(View.GONE);
        playBtn.setBackgroundResource(R.drawable.play_acy_play);
        preview.setText(names.get(i));
    }

    static class seekBarThread extends Thread {
        private WeakReference<PlayAcy> mRef;

        public seekBarThread(PlayAcy mAct) {
            mRef = new WeakReference<PlayAcy>(mAct);
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mRef == null)
                    return;

                PlayAcy mActivity = mRef.get();
                if (mActivity != null) {
                    mActivity.seekBarHandler.sendEmptyMessage(0);
                }
            }
        }

    }

    private static class delayThread extends Thread {
        int seconds;
        private WeakReference<PlayAcy> mRef;

        public delayThread(PlayAcy mAct, int i) {
            seconds = i;
            mRef = new WeakReference<PlayAcy>(mAct);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(seconds);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (mRef == null)
                    return;

                PlayAcy mActivity = mRef.get();
                if (mActivity != null) {
                    mActivity.updateHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    private class SingleGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (2 == finNum) {
                return false;
            }

            float moldX = e1.getX();
            float moldY = e1.getY();
            float y = e2.getY();
            if (moldX > screenBean.getsWidth() * 4.0 / 5)// ����Ļ��4/5�����»���
                changeVolume((moldY - y) / screenBean.getsHeight());
            else if (moldX < screenBean.getsWidth() / 5.0)// ����Ļ1/5�����»���
                changeBrightness((moldY - y) / screenBean.getsHeight());
            else if ((moldX > screenBean.getsWidth() / 5.0) && (moldX < screenBean.getsWidth() * 4.0 / 5.0))
                changeProgress(distanceX);
            return false;
//				return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (isGone) {
                setAnimations();
                buttomLayout.setAnimation(out);
                topLayout.setAnimation(topout);

                buttomLayout.setVisibility(View.VISIBLE);
                topLayout.setVisibility(View.VISIBLE);
                isGone = false;
            } else {
                buttomLayout.setVisibility(View.GONE);
                topLayout.setVisibility(View.GONE);
                isGone = true;
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                pausing.setVisibility(View.VISIBLE);
                playBtn.setBackgroundResource(R.drawable.play_acy_pause);
            } else if (!mVideoView.isPlaying()) {
                mVideoView.start();
                pausing.setVisibility(View.GONE);
                playBtn.setBackgroundResource(R.drawable.play_acy_play);
            }
            return true;
        }
    }

    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            String statusString = "";
            int num = level * 100 / scale;
            chargePercent.setText(num + "%");
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusString = "unknown";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    chargeLogo.setVisibility(View.VISIBLE);
                    battery_image.setImageResource(R.drawable.level);
                    battery_image.getDrawable().setLevel(level);
                    statusString = "charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusString = "discharging";
                    chargeLogo.setVisibility(View.GONE);
                    battery_image.setImageResource(R.drawable.level);
                    battery_image.getDrawable().setLevel(level);
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusString = "not charging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusString = "full";
                    break;
            }
            String healthString = "";
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    healthString = "unknown";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthString = "good";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthString = "overheat";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthString = "dead";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthString = "voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthString = "unspecified failure";
                    break;
            }
            String acString = "";
            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    acString = "plugged ac";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    acString = "plugged usb";
                    break;
            }
        }
    }

}
