package com.my51c.see51.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.guide.wifiListAdapter;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.my51c.see51.widget.NewSwitch;
import com.my51c.see51.widget.PickerView;
import com.my51c.see51.widget.SegmentedGroup;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlarmCloudRecordAcy extends BaseActivity implements OnCheckedChangeListener, OnClickListener {

    private static final String[] videosize = {
            "10s", "15s", "20s", "25s", "30s", "35s", LocalSettingActivity.conRec + "30s", LocalSettingActivity.conRec + "1min",
            LocalSettingActivity.conRec + "3min", LocalSettingActivity.conRec + "5min",
    };
    private final String TAG = "AlarmCloudRecordAcy";
    public MyHandler handler = new MyHandler(this);
    DeviceLocalInfo localDeviceInfo;
    NewSwitch alarmQuickSettingSwitch;
    NewSwitch cameraAudioSwitch;
    NewSwitch alarmMotionDetectorSwitch;
    NewSwitch alarmPirInputTriggerSwitch;
    NewSwitch StorageSDSwitch;
    NewSwitch StorageCloudSwitch;
    NewSwitch ClearSDcardSwitch;
    SegmentedGroup alarmconditionRadioGroup;
    SegmentedGroup storagevideostreamType;
    boolean bSDCheck = false;
    boolean bCloudCheck = false;
    String picker1Tx = "00";
    String picker2Tx = "00";
    String picker3Tx = "00";
    private LocalService localService;
    private ArrayList<String> fileSizeList = new ArrayList<String>();
    private ArrayList<String> fileSizeListCopy = new ArrayList<String>();
    private int whichItem;
    private ProgressDialog pd;
    private TimeOutAsyncTask asyncTask;
    private RelativeLayout fileSizeLayout;
    private TextView fileSizeTx;
    private TextView clearSDtX;
    private RelativeLayout recordPlanLayout;
    private NewSwitch recordPlanSwitch;
    private RelativeLayout snapLayout1, snapLayout2, snapLayout3;
    private RelativeLayout btnLayout1, btnLayout2, btnLayout3;
    private TextView snapTx1, snapTx2, snapTx3;
    private NewSwitch snapSwitch1, snapSwitch2, snapSwitch3;
    private ToastCommom toast = new ToastCommom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmrecordacy);
        setViewClick();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        whichItem = getIntent().getIntExtra("whichItem", 1);
        localService = ((AppData) getApplication()).getLocalService();
        localDeviceInfo = (DeviceLocalInfo) LocalSettingActivity.mDevice.getLocalInfo().clone();
        setCurStatus();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void setViewClick() {
        // TODO Auto-generated method stub

        alarmQuickSettingSwitch = (NewSwitch) findViewById(R.id.alarmQuickSettingSwitch);
        cameraAudioSwitch = (NewSwitch) findViewById(R.id.cameraAudioSwitch);
        alarmMotionDetectorSwitch = (NewSwitch) findViewById(R.id.alarmMotionDetectorSwitch);
        alarmPirInputTriggerSwitch = (NewSwitch) findViewById(R.id.alarmPirInputTriggerSwitch);
        StorageSDSwitch = (NewSwitch) findViewById(R.id.StorageSDSwitch);
        StorageCloudSwitch = (NewSwitch) findViewById(R.id.StorageCloudSwitch);
        ClearSDcardSwitch = (NewSwitch) findViewById(R.id.ClearSDcardSwitch);
        alarmconditionRadioGroup = (SegmentedGroup) findViewById(R.id.alarmconditionRadioGroup);
        storagevideostreamType = (SegmentedGroup) findViewById(R.id.storagevideostreamType);
        clearSDtX = (TextView) findViewById(R.id.clearSDtX);
        fileSizeLayout = (RelativeLayout) findViewById(R.id.fileSizeLayout);
        TextView yesButton = (TextView) findViewById(R.id.saveTx);
        LinearLayout noButton = (LinearLayout) findViewById(R.id.backLayout);
        recordPlanLayout = (RelativeLayout) findViewById(R.id.record_plan);
        recordPlanSwitch = (NewSwitch) findViewById(R.id.record_planSwitch);
        snapLayout1 = (RelativeLayout) findViewById(R.id.snapLayout1);
        snapTx1 = (TextView) findViewById(R.id.snapTx1);
        snapSwitch1 = (NewSwitch) findViewById(R.id.snapSwitch1);
        snapLayout2 = (RelativeLayout) findViewById(R.id.snapLayout2);
        snapTx2 = (TextView) findViewById(R.id.snapTx2);
        snapSwitch2 = (NewSwitch) findViewById(R.id.snapSwitch2);
        snapLayout3 = (RelativeLayout) findViewById(R.id.snapLayout3);
        snapTx3 = (TextView) findViewById(R.id.snapTx3);
        snapSwitch3 = (NewSwitch) findViewById(R.id.snapSwitch3);
        btnLayout1 = (RelativeLayout) findViewById(R.id.btnLayout1);
        btnLayout2 = (RelativeLayout) findViewById(R.id.btnLayout2);
        btnLayout3 = (RelativeLayout) findViewById(R.id.btnLayout3);

        recordPlanSwitch.setNoTouch(true);
        snapSwitch1.setNoTouch(true);
        snapSwitch2.setNoTouch(true);
        snapSwitch3.setNoTouch(true);

        ClearSDcardSwitch.setOnCheckedChangeListener(this);
        alarmQuickSettingSwitch.setOnCheckedChangeListener(this);
        cameraAudioSwitch.setOnCheckedChangeListener(this);
        alarmMotionDetectorSwitch.setOnCheckedChangeListener(this);
        alarmPirInputTriggerSwitch.setOnCheckedChangeListener(this);
        StorageSDSwitch.setOnCheckedChangeListener(this);
        StorageCloudSwitch.setOnCheckedChangeListener(this);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        recordPlanLayout.setOnClickListener(this);
        snapLayout1.setOnClickListener(this);
        snapLayout2.setOnClickListener(this);
        snapLayout3.setOnClickListener(this);
        btnLayout1.setOnClickListener(this);
        btnLayout2.setOnClickListener(this);
        btnLayout3.setOnClickListener(this);
        fileSizeLayout.setOnClickListener(this);

        alarmconditionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.alarmconditionTogether:
                        localDeviceInfo.setbgioinenable((byte) (localDeviceInfo.getbgioinenable() | 0x2));
                        break;

                    case R.id.alarmconditioninividual:
                        localDeviceInfo.setbgioinenable((byte) (localDeviceInfo.getbgioinenable() & 0xFD));
                        break;

                    default:
                        break;
                }
            }
        });
        storagevideostreamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.alarmstoragelargestream:
                        localDeviceInfo.setuCloudSaveStream((byte) 0);
                        break;
                    case R.id.alarmstoragesmallstream:
                        localDeviceInfo.setuCloudSaveStream((byte) 1);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    public void setCurStatus() {
        clearSDtX.setText("");
        clearSDtX.setText(getString(R.string.clearsdcard) + getString(R.string.sd_str) + localDeviceInfo.getSdTotal()
                + getString(R.string.rest_str) + localDeviceInfo.getSdFree() + "M)");
        alarmQuickSettingSwitch.setChecked(localDeviceInfo.getbAlarmEnable() == 1);
        cameraAudioSwitch.setChecked(localDeviceInfo.getnAlarmAudioPlay() == 1);
        alarmMotionDetectorSwitch.setChecked(localDeviceInfo.getMotionenable() == 1);
        alarmPirInputTriggerSwitch.setChecked((localDeviceInfo.getbgioinenable() & 0x1) == 1);
        switch (localDeviceInfo.getuCloudSaveType()) {
            case 0: {
                StorageSDSwitch.setChecked(false);
                StorageCloudSwitch.setChecked(false);
                bSDCheck = false;
                bCloudCheck = false;
            }
            break;
            case 1: {
                StorageSDSwitch.setChecked(true);
                StorageCloudSwitch.setChecked(false);
                bSDCheck = true;
                bCloudCheck = false;
            }
            break;
            case 2: {
                StorageSDSwitch.setChecked(false);
                StorageCloudSwitch.setChecked(true);
                bSDCheck = false;
                bCloudCheck = true;
            }
            break;
            case 3: {
                StorageSDSwitch.setChecked(true);
                StorageCloudSwitch.setChecked(true);
                bSDCheck = true;
                bCloudCheck = true;
            }
            break;
        }

        for (int i = 0; i < videosize.length; i++) {
            fileSizeList.add(videosize[i]);
            fileSizeListCopy.add(videosize[i]);
        }
        fileSizeTx = (TextView) findViewById(R.id.fileSizeTx);
        if (localDeviceInfo.getuCloudSaveTime() < 7) {
            fileSizeTx.setText(fileSizeList.get(localDeviceInfo.getuCloudSaveTime()));
        } else {
            switch (localDeviceInfo.getuCloudSaveTime()) {
                case 7:
                    fileSizeTx.setText(fileSizeList.get(7));
                    break;

                case 11:
                    fileSizeTx.setText(fileSizeList.get(8));
                    break;

                case 15:
                    fileSizeTx.setText(fileSizeList.get(9));
                    break;

                default:
                    break;
            }
        }
        if ((localDeviceInfo.getbgioinenable() & 0x2) == 2) {
            alarmconditionRadioGroup.check(R.id.alarmconditionTogether);
        } else {
            alarmconditionRadioGroup.check(R.id.alarmconditioninividual);
        }

        if (localDeviceInfo.getuCloudSaveStream() == 0) {
            storagevideostreamType.check(R.id.alarmstoragelargestream);
        } else {
            storagevideostreamType.check(R.id.alarmstoragesmallstream);
        }

        int snapTime1 = localDeviceInfo.getSnaptime1();
        int snapTime2 = localDeviceInfo.getSnaptime2();
        short uSnapInterval = localDeviceInfo.getuSnapInterval();
        snapSwitch1.setChecked(snapTime1 != 0);
        snapSwitch2.setChecked(snapTime2 != 0);
        snapSwitch3.setChecked(uSnapInterval != 0);
        snapTx1.setText(getSnapTimeStr(snapTime1));
        snapTx2.setText(getSnapTimeStr(snapTime2));
        snapTx3.setText(getSpaceTimeStr(uSnapInterval));
    }

    public String getSnapTimeStr(int seconds) {
        int snapSecond = seconds % 60;
        int snapMin = (seconds / 60) % 60;
        int snapHour = seconds / 3600;

        String secondStr = snapSecond > 10 ? snapSecond + "" : "0" + snapSecond;
        String minStr = snapMin > 10 ? snapMin + "" : "0" + snapMin;
        String hourStr = snapHour > 10 ? snapHour + "" : "0" + snapHour;

        return hourStr + ":" + minStr + ":" + secondStr;
    }

    public String getSpaceTimeStr(int minutes) {
        int spaceMin = minutes % 60;
        int spaceHour = minutes / 60;

        String minStr = spaceMin > 10 ? spaceMin + "" : "0" + spaceMin;
        String hourStr = spaceHour > 10 ? spaceHour + "" : "0" + spaceHour;

        return hourStr + getResources().getString(R.string._hour) + minStr + getResources().getString(R.string._minute);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.saveTx:
                if (LocalSettingActivity.isLocal) {
                    asyncTask = new TimeOutAsyncTask(this);
                    showSettingDialog(localDeviceInfo, localService);
                } else {
                    asyncTask = new TimeOutAsyncTask(this);
                    showSettingDialog(localDeviceInfo, localService);
                }
                break;
            case R.id.backLayout:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.record_plan:
                Intent intent = new Intent(AlarmCloudRecordAcy.this, PlanOfRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.snapLayout1:
                showSnapSelectDialog(1);
                break;
            case R.id.snapLayout2:
                showSnapSelectDialog(2);
                break;
            case R.id.snapLayout3:
                showSnapSelectDialog(3);
                break;
            case R.id.fileSizeLayout:
                showSnapSelectDialog(4);
                break;
            case R.id.btnLayout1:
                if (snapSwitch1.isChecked()) {
                    snapSwitch1.toggleUI();
                    snapTx1.setText("00:00:00");
                    snapTx1.setTextColor(getResources().getColor(R.color.highhui));
                    localDeviceInfo.setSnaptime1(0);
                } else {
                    showSnapSelectDialog(1);
                }
                break;
            case R.id.btnLayout2:
                if (snapSwitch2.isChecked()) {
                    snapSwitch2.toggleUI();
                    snapTx2.setText("00:00:00");
                    snapTx2.setTextColor(getResources().getColor(R.color.highhui));
                    localDeviceInfo.setSnaptime2(0);
                } else {
                    showSnapSelectDialog(2);
                }
                break;
            case R.id.btnLayout3:
                if (snapSwitch3.isChecked()) {
                    snapSwitch3.toggleUI();
                    snapTx3.setText(picker1Tx + getResources().getString(R.string._hour) + picker2Tx + getResources().getString(R.string._minute));
                    snapTx3.setTextColor(getResources().getColor(R.color.highhui));
                    localDeviceInfo.setuSnapInterval((short) 0);
                } else {
                    showSnapSelectDialog(3);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.StorageCloudSwitch:
                if (isChecked) {
                    bCloudCheck = true;
                    if (bSDCheck)
                        localDeviceInfo.setuCloudSaveType((byte) 3);
                    else
                        localDeviceInfo.setuCloudSaveType((byte) 2);
                } else {
                    bCloudCheck = false;
                    if (bSDCheck)
                        localDeviceInfo.setuCloudSaveType((byte) 1);
                    else
                        localDeviceInfo.setuCloudSaveType((byte) 0);
                }
                break;
            case R.id.StorageSDSwitch:
                if (isChecked) {
                    bSDCheck = true;
                    if (bCloudCheck)
                        localDeviceInfo.setuCloudSaveType((byte) 3);
                    else
                        localDeviceInfo.setuCloudSaveType((byte) 1);
                } else {
                    bSDCheck = false;
                    if (bCloudCheck)
                        localDeviceInfo.setuCloudSaveType((byte) 2);
                    else
                        localDeviceInfo.setuCloudSaveType((byte) 0);
                }
                break;
            case R.id.alarmPirInputTriggerSwitch:
                if (isChecked)
                    localDeviceInfo.setbgioinenable((byte) (localDeviceInfo.getbgioinenable() | 0x1));
                else
                    localDeviceInfo.setbgioinenable((byte) (localDeviceInfo.getbgioinenable() & 0xFE));
                break;
            case R.id.alarmMotionDetectorSwitch:
                localDeviceInfo.setMotionenable((byte) (isChecked ? 1 : 0));
                break;
            case R.id.cameraAudioSwitch:
                localDeviceInfo.setnAlarmAudioPlay((byte) (isChecked ? 1 : 0));
                break;
            case R.id.alarmQuickSettingSwitch:
                localDeviceInfo.setbAlarmEnable((byte) (isChecked ? 1 : 0));
                break;
            case R.id.ClearSDcardSwitch:
                if (localDeviceInfo.getnSdinsert() == 3)        //sd��������ʹ��
                {
                    System.out.println("sd��������ʹ��");
                    System.out.println("GET bFromatSD:" + localDeviceInfo.getbFormatSD());
                    localDeviceInfo.setbFormatSD((byte) (isChecked ? 1 : 0));
                    System.out.println("GET bFromatSD:" + localDeviceInfo.getbFormatSD());
                }
                break;

            default:
                break;
        }
    }

    public void showSnapSelectDialog(final int id) {
        final Dialog snapDialog = new Dialog(AlarmCloudRecordAcy.this, R.style.Erro_Dialog);
        snapDialog.setContentView(R.layout.dialog_snap);
        Window window = snapDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //�˴���������dialog��ʾ��λ��
        window.setWindowAnimations(R.style.dialog_animation_style);
        /*����dialog���ռ����Ļ*/
        DisplayMetrics dm = new DisplayMetrics();
        AlarmCloudRecordAcy.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = snapDialog.getWindow().getAttributes();
        lp.width = dm.widthPixels; //���ÿ��
        snapDialog.getWindow().setAttributes(lp);
        snapDialog.show();

        PickerView picker1 = (PickerView) snapDialog.findViewById(R.id.picker1);
        PickerView picker2 = (PickerView) snapDialog.findViewById(R.id.picker2);
        PickerView picker3 = (PickerView) snapDialog.findViewById(R.id.picker3);
        List<String> hours = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        picker1.setData(hours);
        picker2.setData(minutes);
        picker3.setData(seconds);
        picker1.setSelected(0);
        picker2.setSelected(0);
        picker3.setSelected(0);
        TextView cancel = (TextView) snapDialog.findViewById(R.id.cancel);
        TextView comfirm = (TextView) snapDialog.findViewById(R.id.comfirm);
        TextView hourTx = (TextView) snapDialog.findViewById(R.id.hourTx);
        TextView minTx = (TextView) snapDialog.findViewById(R.id.minTx);
        TextView secondTx = (TextView) snapDialog.findViewById(R.id.secondTx);
        picker1Tx = "00";
        picker2Tx = "00";
        picker3Tx = "00";
        if (id == 3) {
            secondTx.setVisibility(View.GONE);
            picker3.setVisibility(View.GONE);
        } else if (id == 4) {//�ı�Ϊ�����ļ���С�Ի���
            picker1.setData(fileSizeListCopy);
            picker1Tx = fileSizeList.get(0);
            picker1.setSelected(0);//setSelected֮�󣬴���list˳��ᱻ����
            picker2.setVisibility(View.GONE);
            picker3.setVisibility(View.GONE);
            hourTx.setVisibility(View.GONE);
            minTx.setVisibility(View.GONE);
            secondTx.setVisibility(View.GONE);
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                snapDialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String selectStr = "";
                if (id == 4) {
                    int position = fileSizeList.indexOf(picker1Tx);
                    fileSizeTx.setText(fileSizeList.get(position));
                    if (position < 7) {
                        localDeviceInfo.setuCloudSaveTime((byte) position);
                    } else {
                        switch (position) {
                            case 7:
                                localDeviceInfo.setuCloudSaveTime((byte) 7);
                                break;
                            case 8:
                                localDeviceInfo.setuCloudSaveTime((byte) 11);
                                break;
                            case 9:
                                localDeviceInfo.setuCloudSaveTime((byte) 15);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (id == 3) {
                    selectStr = picker1Tx + picker2Tx;
                    int hour = Integer.parseInt(picker1Tx);
                    int minute = Integer.parseInt(picker2Tx);
                    int uSnapInterval = (hour * 60 + minute);
                    localDeviceInfo.setuSnapInterval((short) uSnapInterval);
                    snapTx3.setText(picker1Tx + getResources().getString(R.string._hour) + picker2Tx + getResources().getString(R.string._minute));
                    if (uSnapInterval == 0)
                        snapSwitch3.setChecked(false);
                    else
                        snapSwitch3.setChecked(true);
                    snapTx3.setTextColor(snapSwitch3.isChecked() ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.highhui));
                } else if (id == 2) {
                    selectStr = picker1Tx + picker2Tx + picker3Tx;
                    int hour = Integer.parseInt(picker1Tx);
                    int minute = Integer.parseInt(picker2Tx);
                    int second = Integer.parseInt(picker3Tx);
                    int snaptime2 = hour * 60 * 60 + minute * 60 + second;
                    localDeviceInfo.setSnaptime2(snaptime2);
                    snapTx2.setText(picker1Tx + ":" + picker2Tx + ":" + picker3Tx);
                    if (snaptime2 == 0)
                        snapSwitch2.setChecked(false);
                    else
                        snapSwitch2.setChecked(true);
                    snapTx2.setTextColor(snapSwitch2.isChecked() ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.highhui));
                } else if (id == 1) {
                    selectStr = picker1Tx + picker2Tx + picker3Tx;
                    int hour = Integer.parseInt(picker1Tx);
                    int minute = Integer.parseInt(picker2Tx);
                    int second = Integer.parseInt(picker3Tx);
                    int snaptime1 = hour * 60 * 60 + minute * 60 + second;
                    localDeviceInfo.setSnaptime1(snaptime1);
                    snapTx1.setText(picker1Tx + ":" + picker2Tx + ":" + picker3Tx);
                    if (snaptime1 == 0)
                        snapSwitch1.setChecked(false);
                    else
                        snapSwitch1.setChecked(true);
                    snapTx1.setTextColor(snapSwitch1.isChecked() ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.highhui));
                }
                Log.i(TAG, "��ǰѡ��" + selectStr);
                snapDialog.dismiss();
            }
        });
    }

    public void showFileSizeDialog() {

        final Dialog dialog = new Dialog(this, R.style.Erro_Dialog);
        dialog.setContentView(R.layout.filesize_dialog);
        final ListView sizeList = (ListView) dialog.findViewById(R.id.sizeList);
        final wifiListAdapter adapter = new wifiListAdapter(fileSizeList, getApplicationContext(), R.drawable.lin_bac);
        wifiListAdapter.isSingleChoice = true;
        int choice = localDeviceInfo.getuCloudSaveTime();
        if (choice < 7) {
            wifiListAdapter.choiceItem = localDeviceInfo.getuCloudSaveTime();
        } else {
            wifiListAdapter.choiceItem = 6;
        }
        sizeList.setAdapter(adapter);
        sizeList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                wifiListAdapter.choiceItem = position;
                adapter.notifyDataSetChanged();
                localDeviceInfo.setuCloudSaveTime((byte) position);
                fileSizeTx.setText(fileSizeList.get(position));
                dialog.dismiss();
            }
        });

        ImageView closeImg = (ImageView) dialog.findViewById(R.id.closeImg);
        closeImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showSettingDialog(final DeviceLocalInfo info, final LocalService localService) {//�����豸����

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

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

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        localService = null;
        localDeviceInfo = null;
    }

    private static class MyHandler extends Handler {
        private WeakReference<AlarmCloudRecordAcy> mRef;

        public MyHandler(AlarmCloudRecordAcy mAct) {
            mRef = new WeakReference<AlarmCloudRecordAcy>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mRef == null)
                return;

            AlarmCloudRecordAcy activity = mRef.get();
            if (activity != null) {
                if (msg.what == GeneralInfoAcy.MSG_SET_SUCESS) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(LocalSettingActivity.setParaSuccessMsg[activity.whichItem]), 1000);
                    GeneralInfoAcy.refreshDeviceInfo(activity.localDeviceInfo);
                    activity.asyncTask.cancel(true);
                    activity.pd.cancel();
                    activity.finish();
                    activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else if (msg.what == GeneralInfoAcy.MSG_SET_FAILED) {
                    activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(LocalSettingActivity.setParaFailedMsg[activity.whichItem]), 1000);
                    activity.asyncTask.cancel(true);
                    activity.pd.cancel();
                } else if (msg.what == GeneralInfoAcy.MSG_SET_TIMEOUT) {
                    if (activity.whichItem != 2) {
                        activity.toast.ToastShow(activity.getApplicationContext(), activity.getResources().getString(R.string.setFail), 1000);
                    }
                    activity.pd.cancel();
                } else if (msg.what == 3) {
                    activity.pd.show();
                }
            }
        }
    }

    public static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<AlarmCloudRecordAcy> mRef;

        public TimeOutAsyncTask(AlarmCloudRecordAcy mAct) {
            mRef = new WeakReference<AlarmCloudRecordAcy>(mAct);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRef == null)
                return;

            AlarmCloudRecordAcy activity = mRef.get();

            if (mRef != null) {
                if (LocalSettingActivity.isLocal) {
                    activity.localService.setDeviceParam(activity.localDeviceInfo);
                } else {
                    LocalSettingActivity.mediastream.setDevInfo(activity.localDeviceInfo);
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
            AlarmCloudRecordAcy activity = mRef.get();

            if (mRef != null) {
                activity.handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_TIMEOUT);
            }
        }
    }
}
