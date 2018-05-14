package com.my51c.see51.ui;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.my51c.see51.widget.NewSwitch;
import com.my51c.see51.widget.SegmentedGroup;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AVSettingAcy extends BaseActivity {

    private static final String[] allVideoFomat = {"720P", "D1", "VGA", "DCIF", "CIF", "QVGA",
            "720P/VGA", "720P/QVGA", "720P/CIF", "D1/DCIF", "D1/CIF", "VGA/QVGA", "CIF/QVGA"};
    private static final String[] frameRate1Scale = {"30", "24", "15", "8"};
    private static final String[] bitRate1Scale = {"500", "1000", "2000", "3000", "5000"};
    private static final String[] frameRate2Scale = {"15", "8", "5", "3"};
    private static final String[] bitRate2Scale = {"100", "120", "150", "200"};
    public MyHandler handler = new MyHandler(this);
    DeviceLocalInfo localDeviceInfo;
    LocalService localService;
    TextView videoFormat;
    TextView firstChannel;
    TextView frameRate1Tx;
    SegmentedGroup frameRate1;
    TextView bitRate1Tx;
    SegmentedGroup bitRate1;
    int[] br1Values = {500, 1000, 2000, 3000, 5000};
    TextView secondChannel;
    TextView frameRate2Tx;
    SegmentedGroup frameRate2;
    TextView bitRate2Tx;
    SegmentedGroup bitRate2;
    int[] br2Values = {100, 120, 150, 200};
    NewSwitch mirror;
    NewSwitch flip;
    NewSwitch audio;
    ProgressDialog pd;
    private String TAG = "AVSettingAcy";
    private ArrayList<String> supportVideoFomat = new ArrayList<String>();
    private ArrayList<Integer> videoFomatIndex = new ArrayList<Integer>();
    private ArrayList<Integer> bitRate1Value = new ArrayList<Integer>();//
    private ArrayList<Integer> bitRate2Value = new ArrayList<Integer>();
    private int whichItem;
    private TimeOutAsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avparameteracy);

        setViewClick();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //����иı䣬�����Ի�����ʾ�Ƿ񱣴�
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (localDeviceInfo.equals(LocalSettingActivity.mDevice.getLocalInfo())) {
                finish();
            } else {
                new AlertDialog.Builder(this).setMessage(R.string.settingDevPara)
                        .setPositiveButton(R.string.continue_setting, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setViewClick() {
        // TODO Auto-generated method stub
        whichItem = getIntent().getIntExtra("whichItem", 1);
        localService = ((AppData) getApplication()).getLocalService();
        localDeviceInfo = (DeviceLocalInfo) LocalSettingActivity.mDevice.getLocalInfo().clone();// clone �豸��Ϣ

        videoFormat = (TextView) findViewById(R.id.videoFormatSpinner);
        for (int i = 0; i < allVideoFomat.length; i++) {
            if (((1 << i) & localDeviceInfo.getOfferSize()) == (1 << i)) {
                supportVideoFomat.add(allVideoFomat[i]);
                videoFomatIndex.add(i);
            }
        }

        int tmp = videoFomatIndex.indexOf(localDeviceInfo.getImageSize());

        videoFormat.setEnabled(false);
        String[] twoChannelStrings = supportVideoFomat.get(tmp == -1 ? 0 : tmp).split("/");

        firstChannel = (TextView) findViewById(R.id.firstChannelTextView);
        //firstChannel.setText(twoChannelStrings[0]);
        firstChannel.setText("1080P/720P");
        frameRate1Tx = (TextView) findViewById(R.id.textView2);
        frameRate1 = (SegmentedGroup) findViewById(R.id.frameRateSpinner1);
        int framRate1ID[] = {R.id.frameRate1_c1, R.id.frameRate1_c2, R.id.frameRate1_c3, R.id.frameRate1_c4};
        try {
            Log.i(TAG, "--getFramerate1" + localDeviceInfo.getFramerate1());
            frameRate1Tx.setText(getString(R.string.frameRate) + " " + frameRate1Scale[localDeviceInfo.getFramerate1()]);
            frameRate1.check(framRate1ID[localDeviceInfo.getFramerate1()]);
            Log.i(TAG, "framRate1:" + localDeviceInfo.getFramerate1());
        } catch (Exception e2) {
            e2.printStackTrace();
            frameRate1Tx.setText(getString(R.string.frameRate) + " " + localDeviceInfo.getFramerate1());
            frameRate1.check(0);
        }
        frameRate1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.frameRate1_c1:
                        frameRate1Tx.setText(getString(R.string.frameRate) + " " + 30);
                        localDeviceInfo.setFramerate1((short) 0);
                        break;
                    case R.id.frameRate1_c2:
                        frameRate1Tx.setText(getString(R.string.frameRate) + " " + 24);
                        localDeviceInfo.setFramerate1((short) 1);
                        break;
                    case R.id.frameRate1_c3:
                        frameRate1Tx.setText(getString(R.string.frameRate) + " " + 15);
                        localDeviceInfo.setFramerate1((short) 2);
                        break;
                    case R.id.frameRate1_c4:
                        frameRate1Tx.setText(getString(R.string.frameRate) + " " + 8);
                        localDeviceInfo.setFramerate1((short) 3);
                        break;

                    default:
                        break;
                }
            }
        });

        bitRate1 = (SegmentedGroup) findViewById(R.id.bitRateSpinner1);
        bitRate1Tx = (TextView) findViewById(R.id.textView4);
        for (int i = 0; i < br1Values.length; i++) {
            bitRate1Value.add(br1Values[i]);
        }

        int bitRate1ID[] = {R.id.bitRate1_c1, R.id.bitRate1_c2, R.id.bitRate1_c3, R.id.bitRate1_c4, R.id.bitRate1_c5};
        try {
            bitRate1Tx.setText(getString(R.string.codeRate) + " " + localDeviceInfo.getBitrate1());
            bitRate1.check(bitRate1ID[bitRate1Value.indexOf(localDeviceInfo.getBitrate1())]);
            Log.i(TAG, "bitRate1:" + bitRate1Value.indexOf(localDeviceInfo.getBitrate1()));
        } catch (Exception e1) {
            //����Խ�磬��ֵ���ڿ�ѡֵ��
            Log.i(TAG, "--" + e1);
            bitRate1.check(0);
        }
        bitRate1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.bitRate1_c1:
                        bitRate1Tx.setText(getString(R.string.codeRate) + " " + bitRate1Value.get(0));
                        localDeviceInfo.setBitrate1(bitRate1Value.get(0));
                        break;
                    case R.id.bitRate1_c2:
                        bitRate1Tx.setText(getString(R.string.codeRate) + " " + bitRate1Value.get(1));
                        localDeviceInfo.setBitrate1(bitRate1Value.get(1));
                        break;
                    case R.id.bitRate1_c3:
                        bitRate1Tx.setText(getString(R.string.codeRate) + " " + bitRate1Value.get(2));
                        localDeviceInfo.setBitrate1(bitRate1Value.get(2));
                        break;
                    case R.id.bitRate1_c4:
                        bitRate1Tx.setText(getString(R.string.codeRate) + " " + bitRate1Value.get(3));
                        localDeviceInfo.setBitrate1(bitRate1Value.get(3));
                        break;
                    case R.id.bitRate1_c5:
                        bitRate1Tx.setText(getString(R.string.codeRate) + " " + bitRate1Value.get(4));
                        localDeviceInfo.setBitrate1(bitRate1Value.get(4));
                        break;
                    default:
                        break;
                }
            }
        });

        secondChannel = (TextView) findViewById(R.id.secondChanneltextView);
        if (twoChannelStrings[1] != null) {
            secondChannel.setText(twoChannelStrings[1]);
        }
        frameRate2Tx = (TextView) findViewById(R.id.secondChannelFrameRate);
        frameRate2 = (SegmentedGroup) findViewById(R.id.frameRateSpinner2);
        int framRate2ID[] = {R.id.frameRate2_c1, R.id.frameRate2_c2, R.id.frameRate2_c3, R.id.frameRate2_c4};
        try {
            Log.i(TAG, "--getFramerate2" + localDeviceInfo.getFramerate2());
            frameRate2Tx.setText(getString(R.string.frameRate) + " " + frameRate2Scale[localDeviceInfo.getFramerate2()]);
            frameRate2.check(framRate2ID[localDeviceInfo.getFramerate2()]);
            Log.i(TAG, "framRate2:" + localDeviceInfo.getFramerate2());
        } catch (Exception e1) {
            e1.printStackTrace();
            frameRate2Tx.setText(getString(R.string.frameRate) + " " + localDeviceInfo.getFramerate2());
            frameRate2.check(0);
        }
        frameRate2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.frameRate2_c1:
                        frameRate2Tx.setText(getString(R.string.frameRate) + " " + 15);
                        localDeviceInfo.setFramerate2((short) 0);
                        break;
                    case R.id.frameRate2_c2:
                        frameRate2Tx.setText(getString(R.string.frameRate) + " " + 8);
                        localDeviceInfo.setFramerate2((short) 1);
                        break;
                    case R.id.frameRate2_c3:
                        frameRate2Tx.setText(getString(R.string.frameRate) + " " + 5);
                        localDeviceInfo.setFramerate2((short) 2);
                        break;
                    case R.id.frameRate2_c4:
                        frameRate2Tx.setText(getString(R.string.frameRate) + " " + 3);
                        localDeviceInfo.setFramerate2((short) 3);
                        break;

                    default:
                        break;
                }
            }
        });

        bitRate2Tx = (TextView) findViewById(R.id.textView7);
        bitRate2 = (SegmentedGroup) findViewById(R.id.bitRateSpinner2);
        for (int i = 0; i < br2Values.length; i++) {
            bitRate2Value.add(br2Values[i]);
        }
        int bitRate2ID[] = {R.id.bitRate2_c1, R.id.bitRate2_c2, R.id.bitRate2_c3, R.id.bitRate2_c4};
        try {
            bitRate2Tx.setText(getString(R.string.codeRate) + " " + localDeviceInfo.getBitrate2());
            bitRate2.check(bitRate2ID[bitRate2Value.indexOf(localDeviceInfo.getBitrate2())]);
            Log.i(TAG, "bitRate2:" + bitRate2Value.indexOf(localDeviceInfo.getBitrate2()));
        } catch (Exception e) {
            e.printStackTrace();
            bitRate2.check(0);
        }
        bitRate2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.bitRate2_c1:
                        bitRate2Tx.setText(getString(R.string.codeRate) + " " + bitRate2Value.get(0));
                        localDeviceInfo.setBitrate2(bitRate2Value.get(0));
                        break;
                    case R.id.bitRate2_c2:
                        bitRate2Tx.setText(getString(R.string.codeRate) + " " + bitRate2Value.get(1));
                        localDeviceInfo.setBitrate2(bitRate2Value.get(1));
                        break;
                    case R.id.bitRate2_c3:
                        bitRate2Tx.setText(getString(R.string.codeRate) + " " + bitRate2Value.get(2));
                        localDeviceInfo.setBitrate2(bitRate2Value.get(2));
                        break;
                    case R.id.bitRate2_c4:
                        bitRate2Tx.setText(getString(R.string.codeRate) + " " + bitRate2Value.get(3));
                        localDeviceInfo.setBitrate2(bitRate2Value.get(3));
                        break;
                    default:
                        break;
                }
            }
        });


        mirror = (NewSwitch) findViewById(R.id.mirrorSwitch);
        mirror.setChecked(localDeviceInfo.getMirror() == 1);
        mirror.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                localDeviceInfo.setMirror(isChecked ? 1 : 0);
            }

        });

        flip = (NewSwitch) findViewById(R.id.flipSwitch);
        flip.setChecked(localDeviceInfo.getFlip() == 1);
        flip.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                localDeviceInfo.setFlip(isChecked ? 1 : 0);
            }
        });

        audio = (NewSwitch) findViewById(R.id.audioSwitch);
        audio.setChecked(localDeviceInfo.getEnableAudio() == 1);
        audio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                localDeviceInfo.setEnableAudio(isChecked ? 1 : 0);
            }
        });

        TextView yesButton = (TextView) findViewById(R.id.saveTx);
        yesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                asyncTask = new TimeOutAsyncTask(AVSettingAcy.this);
                showSettingDialog(localDeviceInfo, localService);
            }
        });

        LinearLayout noButton = (LinearLayout) findViewById(R.id.backLayout);
        noButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    public void showSettingDialog(final DeviceLocalInfo info, final LocalService localService) {//�����豸����

        //���ü�������
        OnSetDevInfoListener mOnSetDevInfoListener = new OnSetDevInfoListener() {

            @Override
            public void onSetDevInfoSuccess() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_SUCESS);
            }

            @Override
            public void onSetDevInfoFailed() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_FAILED);
            }
        };

        OnSet3GInfoListener mOnSet3GInfoListener = new OnSet3GInfoListener() {

            @Override
            public void onSet3GInfoFailed() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_FAILED);
            }

            @Override
            public void onSet3GInfoSuccess() {
                // TODO Auto-generated method stub
                System.out.println("-----------mOnSet3GInfoListener:onSet3GInfoSuccess");
                handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_SUCESS);
            }

        };

        OnSetDeviceListener onSetlistener = new OnSetDeviceListener() {

            @Override
            public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
                // TODO Auto-generated method stub
                //Log.d(TAG, " onSetDeviceSucess " );
                if (devInfo.getCamSerial().equals(info.getCamSerial())) {
                    handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_SUCESS);
                }

                if (LocalSettingActivity.mb3gdevice) {
                    localService.search3g(devInfo);
                } else {
                    localService.search();
                }
            }

            @Override
            public void onSetDeviceFailed(DeviceLocalInfo devInfo) {
                // TODO Auto-generated method stub
                if (devInfo.getCamSerial().equals(info.getCamSerial())) {
                    handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_FAILED);
                }
            }
        };

        //�󶨼���
        if (LocalSettingActivity.isLocal) {
            localService.setListener(onSetlistener);
        } else {
            LocalSettingActivity.mediastream.setOnSetDevInfoListener(mOnSetDevInfoListener);
        }

        //��ʾ�ȴ��Ի���
        pd = new ProgressDialog(this);
        pd.setTitle(R.string.sure);
        pd.setMessage(this.getString(LocalSettingActivity.settingParaMsg[whichItem]));

        pd.setCancelable(true);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (LocalSettingActivity.isLocal) {
                    localService.setListener(null);
                } else {
                    //mediastream.setOnSetDevInfoListener(null);
                }
            }
        });
        pd.show();

        asyncTask.execute(0);

    }

    private static class MyHandler extends Handler {
        private WeakReference<AVSettingAcy> mRef;

        public MyHandler(AVSettingAcy mAct) {
            mRef = new WeakReference<AVSettingAcy>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mRef == null)
                return;

            AVSettingAcy activity = mRef.get();
            if (activity != null) {
                if (msg.what == GeneralInfoAcy.MSG_SET_SUCESS) {
                    GeneralInfoAcy.showToast(LocalSettingActivity.setParaSuccessMsg[activity.whichItem], activity.getApplicationContext());
                    GeneralInfoAcy.refreshDeviceInfo(activity.localDeviceInfo);

                    activity.asyncTask.cancel(true);
                    activity.pd.cancel();
                    activity.finish();
                    activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else if (msg.what == GeneralInfoAcy.MSG_SET_FAILED) {
                    GeneralInfoAcy.showToast(LocalSettingActivity.setParaFailedMsg[activity.whichItem], activity.getApplicationContext());
                    activity.asyncTask.cancel(true);
                    activity.pd.cancel();
                } else if (msg.what == GeneralInfoAcy.MSG_SET_TIMEOUT) {
                    if (activity.whichItem != 2) {
                        GeneralInfoAcy.showToast(R.string.setFail, activity.getApplicationContext());
                    }
                    activity.pd.cancel();
                } else if (msg.what == 3) {
                    activity.pd.show();
                }
            }
        }
    }


    public static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {

        private WeakReference<AVSettingAcy> mRef;

        public TimeOutAsyncTask(AVSettingAcy mAct) {
            mRef = new WeakReference<AVSettingAcy>(mAct);
        }
         /*
     	* ִ�����̣�
     	* 1.onPreExecute()
     	* 2.doInBackground()-->onProgressUpdate()
     	* 3.onPostExecute()
     	*/

        @Override
        protected void onPreExecute() {
            //��һ��ִ�з���
            super.onPreExecute();

            if (mRef == null)
                return;

            AVSettingAcy activity = mRef.get();

            if (activity != null) {

                if (LocalSettingActivity.isLocal) {
                    if (LocalSettingActivity.mb3gdevice) {

                        if (LocalSettingActivity.is3Gor4G) {
                            activity.localService.setDevice3GParam(activity.localDeviceInfo);
                            LocalSettingActivity.is3Gor4G = false;
                        } else {
                            activity.localService.setDeviceParam(activity.localDeviceInfo);
                        }
                    } else {
                        activity.localService.setDeviceParam(activity.localDeviceInfo);
                    }
                } else {
                    {
                        LocalSettingActivity.mediastream.setDevInfo(activity.localDeviceInfo);
                    }
                }
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                if (LocalSettingActivity.isLocal) {
                    Thread.sleep(5000);
                } else {
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //��һ��ִ�з���
            super.onPreExecute();
            if (mRef == null)
                return;

            AVSettingAcy activity = mRef.get();

            if (activity != null) {
                activity.handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_TIMEOUT);
            }
        }
    }
}
