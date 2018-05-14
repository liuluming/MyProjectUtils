package com.my51c.see51.ui;
// bm-add 1101


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.Schedule;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.my51c.see51.widget.PickerView;
import com.my51c.see51.widget.SegmentedGroup;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class NewPlanOfRecordActivity extends BaseActivity implements OnClickListener {

    private RelativeLayout dateLayout, repeatLayout;
    private TextView startDateTx, stopDateTx, repeatTx;
    private Button completeBtn;
    private SegmentedGroup statusGroup;
    private LinearLayout back;
    private Button okBtn;
    private DeviceLocalInfo localDeviceInfo;
    private int[] repeatStrs = {R.string.mon, R.string.tue, R.string.wed, R.string.thr, R.string.fri,
            R.string.sta, R.string.sun, R.string.every, R.string.work};
    private List<String> repeatStrList = new ArrayList<String>();
    private String picker1Tx = "00";
    private String picker2Tx = "00";
    private String picker3Tx = "00";
    private String picker4Tx = "00";
    private Schedule curSchedule;
    private ToastCommom toast = new ToastCommom();
    private TimeOutAsyncTask asyncTask;
    private Dialog waitDialog;
    private LocalService localService;
    private DeviceLocalInfo curInfo;
    private int schedulNum;
    private MyHandler handler = new MyHandler(this);
    private OnSetDevInfoListener mOnSetDevInfoListener = new OnSetDevInfoListener() {

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
    private OnSetDeviceListener onSetlistener = new OnSetDeviceListener() {

        @Override
        public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
            // TODO Auto-generated method stub
            //Log.d(TAG, " onSetDeviceSucess " );
            if (devInfo.getCamSerial().equals(localDeviceInfo.getCamSerial())) {
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
            if (devInfo.getCamSerial().equals(localDeviceInfo.getCamSerial())) {
                handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_FAILED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);
        findView();
        setData();
    }

    public void findView() {
        back = (LinearLayout) findViewById(R.id.backLayout);
        dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        repeatLayout = (RelativeLayout) findViewById(R.id.repeatLayout);
        startDateTx = (TextView) findViewById(R.id.startTime);
        stopDateTx = (TextView) findViewById(R.id.stopTime);
        repeatTx = (TextView) findViewById(R.id.repeatTime);
        statusGroup = (SegmentedGroup) findViewById(R.id.statusGroup);
        okBtn = (Button) findViewById(R.id.okBtn);

        dateLayout.setOnClickListener(this);
        repeatLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        localDeviceInfo = LocalSettingActivity.mDevice.getLocalInfo();
        localService = ((AppData) getApplication()).getLocalService();
        if (LocalSettingActivity.isLocal) {
            localService.setListener(onSetlistener);
        } else {
            LocalSettingActivity.mediastream.setOnSetDevInfoListener(mOnSetDevInfoListener);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public void setData() {
        schedulNum = getIntent().getIntExtra("schedulNum", 0);
        for (int i = 0; i < repeatStrs.length; i++) {
            repeatStrList.add(getResources().getString(repeatStrs[i]));
        }

        curSchedule = new Schedule();
        curSchedule.setbStatus((byte) 1);//����Ĭ��״̬
        curSchedule.setnDay((byte) 1);

        statusGroup.setOnCheckedChangeListener(new SegmentedGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.record_forever:
                        curSchedule.setbStatus((byte) 1);
                        break;
                    case R.id.record_alrm:
                        curSchedule.setbStatus((byte) 2);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.backLayout:
                backMainActivity();
                break;
            case R.id.dateLayout:
                showDateDialog(1);
                break;
            case R.id.repeatLayout:
                showDateDialog(2);
                break;
            case R.id.okBtn:
                okBtnClick();
                break;
            default:
                break;
        }
    }

    public void okBtnClick() {
        String startTimes[] = startDateTx.getText().toString().split(":");
        int startHourToMin = Integer.parseInt(startTimes[0]) * 60;
        int startMin = Integer.parseInt(startTimes[1]);

        String stopTimes[] = stopDateTx.getText().toString().split(":");
        int stopHourToMin = Integer.parseInt(stopTimes[0]) * 60;
        int stopMin = Integer.parseInt(stopTimes[1]);

        if ((startHourToMin + startMin) >= (stopHourToMin + stopMin)) {
            toast.ToastShow(getApplicationContext(), getResources().getString(R.string._time_set_mentioon), 1000);
        } else {
            curInfo = (DeviceLocalInfo) localDeviceInfo.clone();
            Schedule[] schedules = localDeviceInfo.getAschedule();
            if (schedulNum < 8) {
                schedules[schedulNum] = curSchedule;
            } else {
                schedules[7] = curSchedule;
            }
            curInfo.setASchedule(schedules);
            showWaitDialog(curInfo);
        }

    }

    public void showDateDialog(final int type) {
        final Dialog dateDialog = new Dialog(NewPlanOfRecordActivity.this, R.style.Erro_Dialog);
        dateDialog.setContentView(R.layout.dialog_plan);
        Window window = dateDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //�˴���������dialog��ʾ��λ��
        window.setWindowAnimations(R.style.dialog_animation_style);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = dateDialog.getWindow().getAttributes();
        lp.width = dm.widthPixels; //���ÿ��
        dateDialog.getWindow().setAttributes(lp);
        dateDialog.show();

        PickerView picker1 = (PickerView) dateDialog.findViewById(R.id.picker1);
        PickerView picker2 = (PickerView) dateDialog.findViewById(R.id.picker2);
        PickerView picker3 = (PickerView) dateDialog.findViewById(R.id.picker3);
        PickerView picker4 = (PickerView) dateDialog.findViewById(R.id.picker4);
        TextView tx1 = (TextView) dateDialog.findViewById(R.id.tx1);
        TextView tx2 = (TextView) dateDialog.findViewById(R.id.tx2);
        TextView tx3 = (TextView) dateDialog.findViewById(R.id.tx3);
        TextView cancel = (TextView) dateDialog.findViewById(R.id.cancel);
        TextView comfirm = (TextView) dateDialog.findViewById(R.id.comfirm);

        List<String> hours = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        picker1.setData(hours);
        picker2.setData(minutes);
        picker3.setData(hours);
        picker4.setData(minutes);
        if (type == 1) {
            picker1.setSelected("00");
            picker2.setSelected("00");
            picker3.setSelected("00");
            picker4.setSelected("00");
            picker1Tx = "00";
            picker2Tx = "00";
            picker3Tx = "00";
            picker4Tx = "00";
        } else {
            picker1Tx = repeatStrList.get(0);
            tx1.setVisibility(View.GONE);
            tx2.setVisibility(View.GONE);
            tx3.setVisibility(View.GONE);
            picker2.setVisibility(View.GONE);
            picker3.setVisibility(View.GONE);
            picker4.setVisibility(View.GONE);
            hours.clear();
            for (int i = 0; i < repeatStrs.length; i++) {
                hours.add(getResources().getString(repeatStrs[i]));
            }
            picker1.setData(hours);
            picker1.setSelected(0);
        }

        picker1.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                picker1Tx = text;
            }
        });
        picker2.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                picker2Tx = text;
            }
        });
        picker3.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                picker3Tx = text;
            }
        });
        picker4.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                picker4Tx = text;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dateDialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dateDialog.dismiss();
                if (type == 1) {
                    curSchedule.setnStartHour((byte) Integer.parseInt(picker1Tx));
                    curSchedule.setnStartMin((byte) Integer.parseInt(picker2Tx));
                    curSchedule.setnDurationHour((byte) Integer.parseInt(picker3Tx));
                    curSchedule.setnDurationMin((byte) Integer.parseInt(picker4Tx));
                    startDateTx.setText(picker1Tx + ":" + picker2Tx);
                    stopDateTx.setText(picker3Tx + ":" + picker4Tx);
                } else {
                    int nDay = repeatStrList.indexOf(picker1Tx) + 1;
                    repeatTx.setText(picker1Tx);
                    curSchedule.setnDay((byte) nDay);
                }

            }
        });
    }

    public void showWaitDialog(DeviceLocalInfo info) {
        waitDialog = new Dialog(NewPlanOfRecordActivity.this, R.style.rf_control_dialog);
        waitDialog.setContentView(R.layout.login_dialog);
        TextView dialogTx = (TextView) waitDialog.findViewById(R.id.tx);
        dialogTx.setText(getResources().getString(R.string.rf_setting));
        waitDialog.show();
        asyncTask = new TimeOutAsyncTask(this, info);//������������
        asyncTask.execute(0);
        waitDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!asyncTask.isCancelled()) {
                        asyncTask.cancel(true);
                    }
                }
                return false;
            }
        });
        waitDialog.setOnCancelListener(new Dialog.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        localService = null;
        localDeviceInfo = null;
    }

    public void backMainActivity() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<NewPlanOfRecordActivity> mRef;
        private DeviceLocalInfo localDeviceInfo;

        public TimeOutAsyncTask(NewPlanOfRecordActivity mAct, DeviceLocalInfo info) {
            this.localDeviceInfo = info;
            mRef = new WeakReference<NewPlanOfRecordActivity>(mAct);
        }

        @Override
        protected void onPreExecute() {
            //��һ��ִ�з���
            super.onPreExecute();

            if (mRef == null)
                return;

            NewPlanOfRecordActivity activity = mRef.get();
            if (activity != null) {
                if (LocalSettingActivity.isLocal) {
                    activity.localService.setDeviceParam(localDeviceInfo);
                } else {
                    LocalSettingActivity.mediastream.setDevInfo(localDeviceInfo);
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

            NewPlanOfRecordActivity activity = mRef.get();
            if (activity != null) {
                activity.handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_TIMEOUT);
            }
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<NewPlanOfRecordActivity> mRef;

        public MyHandler(NewPlanOfRecordActivity mAct) {
            mRef = new WeakReference<NewPlanOfRecordActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mRef == null)
                return;

            NewPlanOfRecordActivity activity = mRef.get();
            if (activity != null) {
                if (msg.what == GeneralInfoAcy.MSG_SET_SUCESS) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(R.string.set_success), 1000);
                    LocalSettingActivity.mDevice.setLocalInfo(activity.curInfo);

                    if (activity.asyncTask != null) {
                        activity.asyncTask.cancel(true);
                    }
                    if (activity.waitDialog != null) {
                        activity.waitDialog.dismiss();
                    }
                    activity.backMainActivity();
                } else if (msg.what == GeneralInfoAcy.MSG_SET_FAILED) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(R.string.setFail), 1000);
                    if (activity.asyncTask != null) {
                        activity.asyncTask.cancel(true);
                    }
                    if (activity.waitDialog != null) {
                        activity.waitDialog.dismiss();
                    }
                } else if (msg.what == GeneralInfoAcy.MSG_SET_TIMEOUT) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(R.string.setFail), 1000);
                    if (activity.waitDialog != null) {
                        activity.waitDialog.dismiss();
                    }
                }
            }
        }
    }
}

