package com.my51c.see51.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.common.IPUtils;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.my51c.see51.widget.NewSwitch;
import com.synertone.netAssistant.R;

public class WiredSettingAcy extends BaseActivity {

    DeviceLocalInfo localDeviceInfo;
    LocalService localService;
    NewSwitch dhcp;
    EditText ipAddr;
    EditText mask;
    EditText gateWay;
    EditText dns;
    LinearLayout detailLayout;
    ProgressDialog pd;
    private int whichItem;
    private TimeOutAsyncTask asyncTask;
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GeneralInfoAcy.MSG_SET_SUCESS) {
                GeneralInfoAcy.showToast(LocalSettingActivity.setParaSuccessMsg[whichItem], getApplicationContext());
                GeneralInfoAcy.refreshDeviceInfo(localDeviceInfo);
//            	refreshDevice3GInfo(device3GInfo);
//
                asyncTask.cancel(true);
                pd.cancel();
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if (msg.what == GeneralInfoAcy.MSG_SET_FAILED) {
                GeneralInfoAcy.showToast(LocalSettingActivity.setParaFailedMsg[whichItem], getApplicationContext());
                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == GeneralInfoAcy.MSG_SET_TIMEOUT) {
                if (whichItem != 2) {                   //设置无线参数时不检查超时
                    GeneralInfoAcy.showToast(R.string.setFail, getApplicationContext());
                }
                pd.cancel();
            } else if (msg.what == 3) {
                pd.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wiredsettingacy);
        setViewClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果有改变，弹出对话框提示是否保存
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
        localDeviceInfo = (DeviceLocalInfo) LocalSettingActivity.mDevice.getLocalInfo().clone();// clone 设备信息

        detailLayout = (LinearLayout) findViewById(R.id.detailLayout);
        dhcp = (NewSwitch) findViewById(R.id.dhcpSwitch);                     //兼容 api-14 中的switch 和 之前的checkbox
        dhcp.setChecked(localDeviceInfo.getEnableDeviceDHCP() == 1);
        dhcp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                ipAddr.setEnabled(!isChecked);
                mask.setEnabled(!isChecked);
                gateWay.setEnabled(!isChecked);
                dns.setEnabled(!isChecked);
                if (isChecked) {
                    detailLayout.setVisibility(View.GONE);
                } else {
                    detailLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        ipAddr = (EditText) findViewById(R.id.ipAddrEditText);
        ipAddr.setText(localDeviceInfo.getDeviceIP());
        ipAddr.setEnabled(!dhcp.isChecked());
        ipAddr.setFilters(filters);

        mask = (EditText) findViewById(R.id.maskEditText);
        mask.setText(localDeviceInfo.getDeviceMasK());
        mask.setEnabled(!dhcp.isChecked());
        mask.setFilters(filters);

        gateWay = (EditText) findViewById(R.id.gateWayEditText);
        gateWay.setText(localDeviceInfo.getDeviceGateWay());
        gateWay.setEnabled(!dhcp.isChecked());
        gateWay.setFilters(filters);

        dns = (EditText) findViewById(R.id.dnsEditText);
        dns.setText(localDeviceInfo.getDNS0());
        dns.setEnabled(!dhcp.isChecked());
        dns.setFilters(filters);

        if (dhcp.isChecked()) {
            detailLayout.setVisibility(View.GONE);
        } else {
            detailLayout.setVisibility(View.VISIBLE);
        }

        TextView yesButton = (TextView) findViewById(R.id.saveTx);
        yesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!dhcp.isChecked()) {
                    if (!IPUtils.checkIP(ipAddr.getText().toString())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalidIP), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!IPUtils.checkIP(mask.getText().toString())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalidMask), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!IPUtils.checkIP(gateWay.getText().toString())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalidGateWay), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!IPUtils.checkIP(dns.getText().toString())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalidDNS), Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                if (!dhcp.isChecked()) {
                    localDeviceInfo.setEnableDeviceDHCP(0);         //关dhcp
                    localDeviceInfo.setDeviceIP(ipAddr.getText().toString());
                    localDeviceInfo.setDeviceMasK(mask.getText().toString());
                    localDeviceInfo.setDeviceGateWay(gateWay.getText().toString());
                    localDeviceInfo.setDNS0(dns.getText().toString());
                } else {
                    localDeviceInfo.setEnableDeviceDHCP(1);        //开dhcp
                }

                asyncTask = new TimeOutAsyncTask();
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

    public void showSettingDialog(final DeviceLocalInfo info, final LocalService localService) {//设置设备参数

        //设置监听动作
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

        //绑定监听
        if (LocalSettingActivity.isLocal) {
            localService.setListener(onSetlistener);
        } else {
            LocalSettingActivity.mediastream.setOnSetDevInfoListener(mOnSetDevInfoListener);
        }

        //显示等待对话框
        pd = new ProgressDialog(WiredSettingAcy.this);
        pd.setTitle(R.string.sure);
        pd.setMessage(WiredSettingAcy.this.getString(LocalSettingActivity.settingParaMsg[whichItem]));

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

    public class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {

     	/*
     	* 执行流程
     	* 1.onPreExecute()
     	* 2.doInBackground()-->onProgressUpdate()
     	* 3.onPostExecute()
     	*/

        @Override
        protected void onPreExecute() {
            //第一个执行方法
            super.onPreExecute();
            if (LocalSettingActivity.isLocal) {
                if (LocalSettingActivity.mb3gdevice)//ONLY IN Net3GSettingDialogFragment
                {
                    System.out.println("TimeOutAsyncTask---mb3gdevice");

                    if (LocalSettingActivity.is3Gor4G) {//ONLY IN Net3GSettingDialogFragment
                        localService.setDevice3GParam(localDeviceInfo);
                        LocalSettingActivity.is3Gor4G = false;
                    } else {
                        localService.setDeviceParam(localDeviceInfo);
                    }
                } else {
                    localService.setDeviceParam(localDeviceInfo);
                }
            } else {
//	            	if(LocalSettingActivity.mb3gdevice && whichItem == 5)//only in Net3GSettingDialogFragment
//	            	{
//	            		LocalSettingActivity.mediastream.send3GDevInfo(device3GInfo);
//	            	}
//	            	else
                {
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
            //第一个执行方法
            super.onPreExecute();
            handler.sendEmptyMessage(GeneralInfoAcy.MSG_SET_TIMEOUT);
        }
    }

}
