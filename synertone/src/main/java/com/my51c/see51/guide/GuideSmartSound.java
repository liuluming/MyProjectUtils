package com.my51c.see51.guide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mediatek.elian.ElianNative;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.simple.ripple.RippleLayout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class GuideSmartSound extends SherlockFragmentActivity implements OnClickListener {

    private final String TYPE_RETICLE_WIFI = "reticle+wifi";
    private final String TYPE_RETICLE_3G = "3G_reticle";
    ImageView imageview;
    RippleLayout layout;
    TextView tvTime;
    TimeCount time;
    private byte AuthModeOpen = 0x00;
    private byte AuthModeShared = 0x01;
    private byte AuthModeAutoSwitch = 0x02;
    private byte AuthModeWPA = 0x03;
    private byte AuthModeWPAPSK = 0x04;
    private byte AuthModeWPANone = 0x05;
    private byte AuthModeWPA2 = 0x06;
    private byte AuthModeWPA2PSK = 0x07;
    private byte AuthModeWPA1WPA2 = 0x08;
    private byte AuthModeWPA1PSKWPA2PSK = 0x09;
    private byte mAuthMode;
    private String mssid;
    private String mpasswd;
    private String mcapabilities;
    private ElianNative elian;
    private MediaPlayer mediaPlayer;
    private Button nextBtn;
    private TextView smartTitle1, smartTitle2;
    private int num = 0;
    private TextView percent, waitTx;
    private boolean isAddByVoice = false;
    private String devId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_smartsound);

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.smartsound_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        ((AppData) getApplication()).addActivity(new WeakReference<Activity>(this));

        layout = (RippleLayout) findViewById(R.id.ripple_layout);
        imageview = (ImageView) findViewById(R.id.centerImage);
        tvTime = (TextView) findViewById(R.id.tvTime);
        nextBtn = (Button) findViewById(R.id.btnNext);
        smartTitle1 = (TextView) findViewById(R.id.smarttitle1);
        smartTitle2 = (TextView) findViewById(R.id.smarttitle2);
        nextBtn.setOnClickListener(this);
        imageview.setOnClickListener(this);
        Intent intent = getIntent();
        mssid = intent.getStringExtra("essid");
        mpasswd = intent.getStringExtra("passwd");
        devId = getIntent().getStringExtra("DeviceId");
        mcapabilities = intent.getStringExtra("capabilities");

        mAuthMode = getWifiCapabilities(mcapabilities);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.beling);

        boolean result = ElianNative.LoadLib();
        if (!result) {
            Log.d("GudieSmartSound", "can't load elianjni lib");
        }

        elian = new ElianNative();
        time = new TimeCount(6000, 1000);
        VoiceStart();
    }


    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        elian.StopSmartConnection();
    }

    private byte getWifiCapabilities(String capabilities) {
        byte nAuthcode = AuthModeOpen;
        boolean WpaPsk = capabilities.contains("WPA-PSK");
        boolean Wpa2Psk = capabilities.contains("WPA2-PSK");
        boolean Wpa = capabilities.contains("WPA-EAP");
        boolean Wpa2 = capabilities.contains("WPA2-EAP");

        if (capabilities.contains("WEP")) {
            nAuthcode = AuthModeOpen;
            return nAuthcode;
        }

        if (WpaPsk && Wpa2Psk) {
            nAuthcode = AuthModeWPA1PSKWPA2PSK;
            return nAuthcode;
        } else if (Wpa2Psk) {
            nAuthcode = AuthModeWPA2PSK;
            return nAuthcode;
        } else if (WpaPsk) {
            nAuthcode = AuthModeWPAPSK;
            return nAuthcode;
        }

        if (Wpa && Wpa2) {
            nAuthcode = AuthModeWPA1WPA2;
            return nAuthcode;
        } else if (Wpa2) {
            nAuthcode = AuthModeWPA2;
            return nAuthcode;
        } else if (Wpa) {
            nAuthcode = AuthModeWPA;
            return nAuthcode;
        }

        return nAuthcode;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //��Ĭ���ַ���������ַ�������ϵͳ��أ�����windowsĬ��ΪGB2312
            byte[] bs = str.getBytes();
            return new String(bs, newCharset);    //���µ��ַ����������ַ���
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.centerImage: {
                VoiceStart();
            }
            break;
            case R.id.btnNext:
                ClickNext();
                break;


            default:
                break;
        }
    }


    public void ClickNext() {
        Intent intent = new Intent();
        String deviceId = devId;
        String deviceType = TYPE_RETICLE_WIFI;
        intent.putExtra("DeviceId", deviceId);


        String devMode = deviceId.substring(0, 3);
        if (devMode.equals("a82") || devMode.equals("a83") || devMode.equals("a84")) {
            // 3G + ����
            deviceType = TYPE_RETICLE_3G;
        } else {
            // ֻ������
            deviceType = TYPE_RETICLE_WIFI;
        }

        intent.putExtra("DeviceType", deviceType);
        //Log.d(TAG, "deviceId:" + deviceId);

        intent.setClass(GuideSmartSound.this, PlatformActivity.class);
        intent.putExtra("network", "wifi");
        intent.putExtra("DeviceId", deviceId);

        GuideSmartSound.this.startActivity(intent);
    }

    public void VoiceStart() {
        elian.InitSmartConnection(null, 1, 0);
        elian.StartSmartConnection(mssid, mpasswd, "", mAuthMode);
        Log.d("MYCONNECT", "mssid =" + mssid + "mpasswd =" + mpasswd + "nAuthMode" + mAuthMode);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mediaPlayer.start();
        layout.startRippleAnimation();
        time.start();
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//��������Ϊ��ʱ��,�ͼ�ʱ��ʱ����
        }

        @Override
        public void onFinish() {//��ʱ���ʱ����

            if (layout.isRippleAnimationRunning()) {
                layout.stopRippleAnimation();
            }
            tvTime.setText(getString(R.string.Send_Sonic));
            imageview.setClickable(true);
            nextBtn.setVisibility(View.VISIBLE);
            smartTitle1.setText(getString(R.string.send_sonic_complete));

            //elian.StopSmartConnection();
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {//��ʱ������ʾ
            imageview.setClickable(false);
            tvTime.setText("" + millisUntilFinished / 1000);
            smartTitle1.setText(getString(R.string.sending_sonic));
            nextBtn.setVisibility(View.GONE);
        }
    }


}
