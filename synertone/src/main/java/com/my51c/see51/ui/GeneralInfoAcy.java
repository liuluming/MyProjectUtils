package com.my51c.see51.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device3GShortParam;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.guide.wifiListAdapter;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class GeneralInfoAcy extends BaseActivity {

    public static final int MSG_SET_SUCESS = 0;
    public static final int MSG_SET_FAILED = 1;
    public static final int MSG_SET_TIMEOUT = 2;
    private static final String[] utcTimeZone = {
            "GMT-12", "GMT-11", "GMT-10", "GMT-09", "GMT-08", "GMT-07", "GMT-06", "GMT-05",
            "GMT-04", "GMT-03", "GMT-02", "GMT-01", "GMT+00", "GMT+01", "GMT+02", "GMT+03",
            "GMT+04", "GMT+05", "GMT+06", "GMT+07", "GMT+08", "GMT+09", "GMT+10", "GMT+11", "GMT+12", " GMT+13", "GMT+14", "GMT+0630",
            "GMT+0530"
    };
    public Dialog changePswDialog;
    public MyHandler handler = new MyHandler(this);
    LocalService localService;
    EditText deviceName;
    Spinner timeZone;
    TextView dateEditText;
    TextView timeEditText;
    TimeOutAsyncTask asyncTask;
    ProgressDialog pd;
    private DeviceLocalInfo localDeviceInfo;
    private ArrayAdapter<String> adapter;
    private int whichItem;
    private RelativeLayout timeZoneLlayout;
    private TextView timeZoneTx;
    private ArrayList<String> timeZoneList = null;
    private View lastCheckedOption = null;

    public static void showToast(int resId, Context context) {
        Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void refreshDeviceInfo(DeviceLocalInfo localInfo) {
        LocalSettingActivity.mDevice.setLocalInfo(localInfo);
    }

    public static void refreshDevice3GInfo(Device3GShortParam remote3GInfo) {
        LocalSettingActivity.mDevice.set3GParam(remote3GInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generalsettingacy);
        setViewClick();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    public void setViewClick() {

        whichItem = getIntent().getIntExtra("whichItem", 1);
        localService = ((AppData) getApplication()).getLocalService();
        localDeviceInfo = (DeviceLocalInfo) LocalSettingActivity.mDevice.getLocalInfo().clone();// clone �豸��Ϣ
        deviceName = (EditText) findViewById(R.id.deviceNameEditText1);
        deviceName.setText(localDeviceInfo.getDeviceName());
        deviceName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                try {
                    localDeviceInfo.setDeviceName(deviceName.getText().toString());
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        RelativeLayout changePassword = (RelativeLayout) findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                changePswDialog = new Dialog(GeneralInfoAcy.this, R.style.Erro_Dialog);
                changePswDialog.setContentView(R.layout.change_psw_dialog);
                final EditText prePswEt = (EditText) changePswDialog.findViewById(R.id.prepsw);
                final EditText newPswEt = (EditText) changePswDialog.findViewById(R.id.newpsw);
                final EditText confirNewPsw = (EditText) changePswDialog.findViewById(R.id.confir_newpsw);
                Button ok = (Button) changePswDialog.findViewById(R.id.okBtn);
                Button cancle = (Button) changePswDialog.findViewById(R.id.cancleBtn);

                ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // �ж������Ƿ�������ȷ��Ȼ������������
                        String psw = prePswEt.getText().toString();
                        String new_psw = newPswEt.getText().toString();
                        String psw_again = confirNewPsw.getText().toString();

                        if (psw.equals(localDeviceInfo.getPwd())) {
                            if (new_psw.length() > 6 && new_psw.length() < 16) {
                                if (new_psw.equals(psw_again)) {
                                    localDeviceInfo.setNewPwd(new_psw);
                                    localDeviceInfo.setChangePWD(1);
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.passwordinmatch), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.passwordLenRequire), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.invalidPassword), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                cancle.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        changePswDialog.dismiss();
                    }
                });

                changePswDialog.show();
            }
        });

        TextView softwareVision = (TextView) findViewById(R.id.softwareVersionTextView);
        softwareVision.setText(localDeviceInfo.getCameraVer());

        TextView serialTextView = (TextView) findViewById(R.id.serialIDTextView);
        serialTextView.setText(localDeviceInfo.getCamSerial());

//    	timeZone = (Spinner)findViewById(R.id.timeZoneSpinner);
//    	adapter = new  ArrayAdapter<String>(this.getApplicationContext(),android.R.layout.simple_spinner_item,utcTimeZone);
//    	timeZone.setAdapter(adapter);
//    	timeZone.setSelection(localDeviceInfo.getNtp_timezone());
//    	timeZone.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				//Log.d(TAG, " timeZone spinner select" + arg2);
//				//Log.d(TAG, " (byte) arg2  " + (byte) arg2);
//				localDeviceInfo.setNtp_timezone((byte) arg2);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//    	});

        timeZoneList = new ArrayList<String>();
        for (int i = 0; i < utcTimeZone.length; i++) {
            timeZoneList.add(utcTimeZone[i]);
        }
        timeZoneLlayout = (RelativeLayout) findViewById(R.id.tableRow7);
        timeZoneTx = (TextView) findViewById(R.id.timeZoneTx);
        timeZoneTx.setText(timeZoneList.get(localDeviceInfo.getNtp_timezone()));
        timeZoneLlayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showTimeZoneDialog();
            }
        });

        dateEditText = (TextView) findViewById(R.id.dateEditText);
        dateEditText.setText(new String() + localDeviceInfo.getnYear()
                + "-" + (localDeviceInfo.getnMon() & 0x0f)
                + "-" + (int) localDeviceInfo.getnDay());

        timeEditText = (TextView) findViewById(R.id.timeEditText);
        timeEditText.setText(new String() + (int) localDeviceInfo.getnHour()
                + ":" + (int) localDeviceInfo.getnMin() + ":"
                + (int) localDeviceInfo.getnSec());

        Button getPhoneTime = (Button) findViewById(R.id.getMobileTime);
        getPhoneTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                dateEditText.setText(new String() + calendar.get(Calendar.YEAR)
                        + "-" + (calendar.get(Calendar.MONTH) + 1)
                        + "-" + (calendar.get(Calendar.DATE)));
                timeEditText.setText(new String() + (calendar.get(Calendar.HOUR_OF_DAY)) +
                        ":" + (calendar.get(Calendar.MINUTE)) +
                        ":" + (calendar.get(Calendar.SECOND)));
                localDeviceInfo.setnYear(calendar.get(Calendar.YEAR));
                localDeviceInfo.setnMon((byte) (calendar.get(Calendar.MONTH) + 1));
                localDeviceInfo.setnDay((byte) calendar.get(Calendar.DATE));
                localDeviceInfo.setnHour((byte) calendar.get(Calendar.HOUR_OF_DAY));
                localDeviceInfo.setnMin((byte) calendar.get(Calendar.MINUTE));
                localDeviceInfo.setnSec((byte) calendar.get(Calendar.SECOND));
            }
        });

        final Handler rebotHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getApplicationContext(), getString(R.string.reboot), Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), getString(R.string.setFail), Toast.LENGTH_LONG).show(); //
                        break;
                }
            }
        };
        final OnSetDeviceListener listener = new OnSetDeviceListener() {

            @Override
            public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
                // TODO Auto-generated method stub
                if (devInfo.getCamSerial().equals(localDeviceInfo.getCamSerial())) {
                    rebotHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onSetDeviceFailed(DeviceLocalInfo devInfo) {
                // TODO Auto-generated method stub
                if (devInfo.getCamSerial().equals(localDeviceInfo.getCamSerial())) {
                    rebotHandler.sendEmptyMessage(1);
                }
            }
        };

        Button reboot = (Button) findViewById(R.id.btnReboot);
        reboot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //localDeviceInfo.setCmd(0x77);
                new AlertDialog.Builder(GeneralInfoAcy.this).setTitle(R.string.sure)
                        .setMessage(R.string.reboot)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                localService.setListener(listener);
                                localService.rebootDevice(localDeviceInfo);
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */

                            }
                        })
                        .create()
                        .show();
            }
        });

        Button defaultSetting = (Button) findViewById(R.id.btnDefaultSetting);
        defaultSetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(GeneralInfoAcy.this).setTitle(R.string.sure)
                        .setMessage(R.string.defaultSetting)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                localService.setDefaultPara(localDeviceInfo);
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                            }
                        })
                        .create()
                        .show();
            }
        });

        if (LocalSettingActivity.isLocal == false) {
            reboot.setVisibility(View.GONE);
            defaultSetting.setVisibility(View.GONE);
        }

        TextView yesButton = (TextView) findViewById(R.id.saveTx);
        yesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

//				localService.setDeviceParam(localDeviceInfo);
                //���ȷ����ťͬ����ǰʱ��
                Calendar calendar = Calendar.getInstance();
                localDeviceInfo.setnYear(calendar.get(Calendar.YEAR));
                localDeviceInfo.setnMon((byte) (calendar.get(Calendar.MONTH) + 1));
                localDeviceInfo.setnDay((byte) calendar.get(Calendar.DATE));
                localDeviceInfo.setnHour((byte) calendar.get(Calendar.HOUR_OF_DAY));
                localDeviceInfo.setnMin((byte) calendar.get(Calendar.MINUTE));
                localDeviceInfo.setnSec((byte) calendar.get(Calendar.SECOND));

//				����ָ������豸��Ϣ
                asyncTask = new TimeOutAsyncTask(GeneralInfoAcy.this);
                showSettingDialog(localDeviceInfo, localService);
            }
        });

        LinearLayout noButton = (LinearLayout) findViewById(R.id.backLayout);
        noButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    public void showTimeZoneDialog() {

        final Dialog dialog = new Dialog(this, R.style.Erro_Dialog);
        dialog.setContentView(R.layout.filesize_dialog);
        final ListView sizeList = (ListView) dialog.findViewById(R.id.sizeList);
        final wifiListAdapter adapter = new wifiListAdapter(timeZoneList, getApplicationContext(), R.drawable.icon_menu_time);
        wifiListAdapter.isSingleChoice = true;
        wifiListAdapter.choiceItem = localDeviceInfo.getNtp_timezone();
        sizeList.setAdapter(adapter);
        sizeList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                wifiListAdapter.choiceItem = position;
                adapter.notifyDataSetChanged();
                localDeviceInfo.setNtp_timezone((byte) position);
                timeZoneTx.setText(timeZoneList.get(position));
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

        //���ü�������
        OnSetDevInfoListener mOnSetDevInfoListener = new OnSetDevInfoListener() {

            @Override
            public void onSetDevInfoSuccess() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(MSG_SET_SUCESS);
            }

            @Override
            public void onSetDevInfoFailed() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(MSG_SET_FAILED);
            }
        };

        OnSet3GInfoListener mOnSet3GInfoListener = new OnSet3GInfoListener() {

            @Override
            public void onSet3GInfoFailed() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(MSG_SET_FAILED);
            }

            @Override
            public void onSet3GInfoSuccess() {
                // TODO Auto-generated method stub
                System.out.println("-----------mOnSet3GInfoListener:onSet3GInfoSuccess");
                handler.sendEmptyMessage(MSG_SET_SUCESS);
            }

        };

        OnSetDeviceListener onSetlistener = new OnSetDeviceListener() {

            @Override
            public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
                // TODO Auto-generated method stub
                //Log.d(TAG, " onSetDeviceSucess " );
                if (devInfo.getCamSerial().equals(info.getCamSerial())) {
                    handler.sendEmptyMessage(MSG_SET_SUCESS);
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
                    handler.sendEmptyMessage(MSG_SET_FAILED);
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
        pd = new ProgressDialog(GeneralInfoAcy.this);
        pd.setTitle(R.string.sure);
        pd.setMessage(GeneralInfoAcy.this.getString(LocalSettingActivity.settingParaMsg[whichItem]));

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
        private WeakReference<GeneralInfoAcy> mRef;

        public MyHandler(GeneralInfoAcy mAct) {
            mRef = new WeakReference<GeneralInfoAcy>(mAct);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mRef == null)
                return;

            GeneralInfoAcy mActivity = mRef.get();
            if (mActivity != null) {
                if (msg.what == MSG_SET_SUCESS) {
                    showToast(LocalSettingActivity.setParaSuccessMsg[mActivity.whichItem], mActivity.getApplicationContext());
                    refreshDeviceInfo(mActivity.localDeviceInfo);
                    mActivity.asyncTask.cancel(true);
                    mActivity.pd.cancel();
                    mActivity.finish();
                    mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else if (msg.what == MSG_SET_FAILED) {
                    showToast(LocalSettingActivity.setParaFailedMsg[mActivity.whichItem], mActivity.getApplicationContext());
                    mActivity.asyncTask.cancel(true);
                    mActivity.pd.cancel();
                } else if (msg.what == MSG_SET_TIMEOUT) {
                    if (mActivity.whichItem != 2) {                     //�������߲���ʱ����鳬ʱ
                        showToast(R.string.setTimeout, mActivity.getApplicationContext());
                    }
                    mActivity.asyncTask.cancel(true);
                    mActivity.pd.cancel();
                } else if (msg.what == 3) {
                    mActivity.pd.show();
                }
            }
        }
    }

    public static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<GeneralInfoAcy> mRef;

        public TimeOutAsyncTask(GeneralInfoAcy mAct) {
            mRef = new WeakReference<GeneralInfoAcy>(mAct);
        }

        /*
        * ִ�����̣�
        * 1.onPreExecute()
        * 2.doInBackground()-->onProgressUpdate()
        * 3.onPostExecute()
        */
        private void setParas() {

            if (mRef == null)
                return;

            GeneralInfoAcy mActivity = mRef.get();
            if (mActivity != null) {
                if (LocalSettingActivity.isLocal) {
                    if (LocalSettingActivity.mb3gdevice)//ONLY IN Net3GSettingDialogFragment
                    {
                        System.out.println("TimeOutAsyncTask---mb3gdevice");

                        if (LocalSettingActivity.is3Gor4G) {//ONLY IN Net3GSettingDialogFragment
                            mActivity.localService.setDevice3GParam(mActivity.localDeviceInfo);
                            LocalSettingActivity.is3Gor4G = false;
                        } else {
                            mActivity.localService.setDeviceParam(mActivity.localDeviceInfo);
                        }
                    } else {
                        mActivity.localService.setDeviceParam(mActivity.localDeviceInfo);
                    }
                } else {
                    LocalSettingActivity.mediastream.setDevInfo(mActivity.localDeviceInfo);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            //��һ��ִ�з���
            super.onPreExecute();
            setParas();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                if (LocalSettingActivity.isLocal) {
                    //Thread.sleep(10000);
                    for (int i = 0; i < 3; i++) {
                        Thread.sleep(3000);
                        setParas();
                    }
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

            GeneralInfoAcy mActivity = mRef.get();
            if (mActivity != null) {
                mActivity.handler.sendEmptyMessage(MSG_SET_TIMEOUT);
            }
        }
    }
}
