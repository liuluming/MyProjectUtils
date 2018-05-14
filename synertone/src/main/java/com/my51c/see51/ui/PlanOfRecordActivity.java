package com.my51c.see51.ui;
// bm-add 1101


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.Schedule;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.my51c.see51.widget.SweepLayout;
import com.my51c.see51.widget.SweepListView;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PlanOfRecordActivity extends BaseActivity implements OnClickListener {

    private Button newPlanBtn;
    private LinearLayout back;
    private SweepListView sweepListView;
    private DeviceLocalInfo localDeviceInfo;
    private LocalService localService;
    private List<Schedule> scheduleList = null;
    private ScheduleAdapter adapter;
    private int[] repeatStrs = {R.string.mon, R.string.tue, R.string.wed, R.string.thr, R.string.fri,
            R.string.sta, R.string.sun, R.string.every, R.string.work};
    private ToastCommom toast = new ToastCommom();
    private TimeOutAsyncTask asyncTask;
    private Dialog waitDialog;
    private int deletePosition;
    private DeviceLocalInfo curInfo;
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
        setContentView(R.layout.activity_record_plan);
        back = (LinearLayout) findViewById(R.id.backLayout);
        newPlanBtn = (Button) findViewById(R.id.newPlanBtn);
        back.setOnClickListener(this);
        newPlanBtn.setOnClickListener(this);
        sweepListView = (SweepListView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getScheduleData();
    }

    public void getScheduleData() {

        localService = ((AppData) getApplication()).getLocalService();
        if (LocalSettingActivity.isLocal) {
            localService.setListener(onSetlistener);
        } else {
            LocalSettingActivity.mediastream.setOnSetDevInfoListener(mOnSetDevInfoListener);
        }
        localDeviceInfo = LocalSettingActivity.mDevice.getLocalInfo();
        scheduleList = new ArrayList<Schedule>();
        for (Schedule schedule : localDeviceInfo.getAschedule()) {
            if (schedule.getbStatus() != 0) {
                scheduleList.add(schedule);
            }
        }
        //schedule��������
        sortSchedules(scheduleList, localDeviceInfo);
        LocalSettingActivity.mDevice.setLocalInfo(localDeviceInfo);

        newPlanBtn.setVisibility(scheduleList.size() >= 8 ? View.GONE : View.VISIBLE);
        adapter = new ScheduleAdapter();
        sweepListView.setAdapter(adapter);
    }

    public void sortSchedules(List<Schedule> scheduleList, DeviceLocalInfo info) {

        Schedule[] schedules = new Schedule[8];
        for (int i = 0; i < 8; i++) {
            if (i < scheduleList.size()) {
                schedules[i] = scheduleList.get(i);
            } else {
                schedules[i] = new Schedule();
            }
        }
        info.setASchedule(schedules);
        LocalSettingActivity.mDevice.setLocalInfo(info);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.backLayout:
                backMainActivity();
                break;
            case R.id.newPlanBtn:
                Intent intent = new Intent(getApplicationContext(), NewPlanOfRecordActivity.class);
                intent.putExtra("schedulNum", scheduleList.size());
                startActivity(intent);
                break;
            case R.id.deleteL:
                deletePosition = (Integer) v.getTag();

                Schedule[] schedules = localDeviceInfo.getAschedule();
                schedules[deletePosition].setbStatus((byte) 0);
                curInfo = (DeviceLocalInfo) localDeviceInfo.clone();
                curInfo.setASchedule(schedules);

                int firVisible = sweepListView.getFirstVisiblePosition();
                SweepLayout sweepLayout = (SweepLayout) sweepListView.getChildAt(deletePosition - firVisible).findViewById(R.id.sweeplayout);
                sweepLayout.shrik(100);
                //���localService����
                showWaitDialog(curInfo);
                break;
            default:
                break;
        }
    }

    public void showWaitDialog(DeviceLocalInfo curInfo) {
        waitDialog = new Dialog(PlanOfRecordActivity.this, R.style.rf_control_dialog);
        waitDialog.setContentView(R.layout.login_dialog);
        TextView dialogTx = (TextView) waitDialog.findViewById(R.id.tx);
        dialogTx.setText(getResources().getString(R.string.rf_setting));
        waitDialog.show();
        asyncTask = new TimeOutAsyncTask(curInfo);//������������
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public void backMainActivity() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<PlanOfRecordActivity> mRef;
        private DeviceLocalInfo localDeviceInfo;

        public TimeOutAsyncTask(PlanOfRecordActivity mAct) {
            mRef = new WeakReference<PlanOfRecordActivity>(mAct);
        }

        public TimeOutAsyncTask(DeviceLocalInfo curInfo) {
            this.localDeviceInfo = curInfo;
        }

        @Override
        protected void onPreExecute() {
            //��һ��ִ�з���
            super.onPreExecute();

            if (mRef == null)
                return;
            PlanOfRecordActivity activity = mRef.get();

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
            PlanOfRecordActivity activity = mRef.get();

            if (activity != null) {
                activity.handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_TIMEOUT);
            }
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<PlanOfRecordActivity> mRef;

        public MyHandler(PlanOfRecordActivity mAct) {
            mRef = new WeakReference<PlanOfRecordActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mRef != null)
                return;

            PlanOfRecordActivity activity = mRef.get();
            if (activity != null) {
                if (msg.what == GeneralInfoAcy.MSG_SET_SUCESS) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(R.string.set_success), 1000);
                    activity.scheduleList.remove(activity.deletePosition);
                    activity.adapter.notifyDataSetChanged();
                    activity.sortSchedules(activity.scheduleList, activity.curInfo);
                    activity.newPlanBtn.setVisibility(activity.scheduleList.size() >= 8 ? View.GONE : View.VISIBLE);
                    if (activity.asyncTask != null) {
                        activity.asyncTask.cancel(true);
                    }
                    if (activity.waitDialog != null) {
                        activity.waitDialog.dismiss();
                    }
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

    static class ViewHolder {
        TextView nDay, nTime, nStatus;
        LinearLayout deleteL;
        SweepLayout sweepLayout;
    }

    private class ScheduleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return scheduleList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return scheduleList.get(position);
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.record_plan_list_item, null);
                holder.nDay = (TextView) convertView.findViewById(R.id.nDay);
                holder.nTime = (TextView) convertView.findViewById(R.id.nTime);
                holder.nStatus = (TextView) convertView.findViewById(R.id.nStatus);
                holder.deleteL = (LinearLayout) convertView.findViewById(R.id.deleteL);
                holder.sweepLayout = (SweepLayout) convertView.findViewById(R.id.sweeplayout);
                holder.deleteL.setTag(position);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Schedule schedule = scheduleList.get(position);
            String startHour = schedule.getnStartHour() >= 10 ? "" + schedule.getnStartHour() : "0" + schedule.getnStartHour();
            String startMin = schedule.getnStartMin() >= 10 ? "" + schedule.getnStartMin() : "0" + schedule.getnStartMin();
            String stopHour = schedule.getnDurationHour() >= 10 ? "" + schedule.getnDurationHour() : "0" + schedule.getnDurationHour();
            String stopMin = schedule.getnDurationMin() >= 10 ? "" + schedule.getnDurationMin() : "0" + schedule.getnDurationMin();
            String timeStr = startHour + ":" + startMin + getResources().getString(R.string._until) + stopHour + ":" + stopMin;
            holder.nTime.setText(timeStr);


            if (schedule.getbStatus() == 1) {
                holder.nStatus.setText(getResources().getString(R.string._record_plan));
            } else if (schedule.getbStatus() == 2) {
                holder.nStatus.setText(getResources().getString(R.string._record_when_alrm));
            } else {
                holder.nStatus.setText(getResources().getString(R.string._record_when_alrm));
            }
            holder.nDay.setText(repeatStrs[schedule.getnDay() - 1]);

            holder.deleteL.setOnClickListener(PlanOfRecordActivity.this);

            return convertView;
        }
    }
}
