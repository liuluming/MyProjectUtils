package com.my51c.see51.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.Device3GShortParam;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.service.LocalService;
import com.my51c.see51.service.LocalService.OnSetDeviceListener;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSettingActivity extends SherlockFragmentActivity implements OnItemClickListener {

    public final static int[] settingParaMsg = new int[]{
            R.string.settingGeneralPara,
            R.string.settingWiredParameter,
            R.string.settingWirelessParameter,
            R.string.setttingMediaParameter,
            R.string.settingAlarm,
            R.string.net3gParameters
            //R.string.settingSMTP,
            //R.string.settingFTP
    };
    public final static int[] setParaSuccessMsg = new int[]{
            R.string.setGeneralParaSuccess,
            R.string.setWiredParaSuccess,
            R.string.setWirelessParaSuccess,
            R.string.setMediaParaSuccess,
            R.string.setAlarmSuccess,
            R.string.setGeneralParaSuccess
            //R.string.setSMTPSuccess,
            //R.string.setFTPSuccess
    };
    public final static int[] setParaFailedMsg = new int[]{
            R.string.setGeneralParaFailed,
            R.string.setWiredParaFailed,
            R.string.setWirelessParaFailed,
            R.string.setMediaParaFailed,
            R.string.setAlarmFailed,
            R.string.setGeneralParaFailed
            //R.string.setSMTPFailed,
            //R.string.setFTPFailed
    };
    private final static String TAG = "LocalSetting";
    private final static int[] itemImage = new int[]{
            R.drawable.local_info_advanced_x2,
            R.drawable.local_info_wired_x2,
            R.drawable.local_info_wireless_x2,
            R.drawable.local_info_video_x2,
            R.drawable.param_nav_motion_x2,
            R.drawable.local_info_wireless_x2
            //R.drawable.param_nav_smtp_x2,
            //R.drawable.param_nav_ftp_x2
    };
    private final static int[] itemsTitle = new int[]{
            R.string.general,
            R.string.wiredNetworkParameters,
            R.string.wirelessNetworkParameters,
            R.string.videoAudioParameters,
            R.string.setAlarm,
            R.string.net3gParameters
            //R.string.setSMTP,
            //R.string.setFTP
    };
    private final static int[] itemDescription = new int[]{
            R.string.setGeneralPara,
            R.string.setWiredParameter,
            R.string.setWirelessParameter,
            R.string.setMediaParameter,
            R.string.setAlarm,
            R.string.net3gParameters
            //R.string.setSMTP,
            //R.string.setFTP
    };
    static public Device mDevice;                   //�ڸ�������ҳ����ͨ�����ȫ�ֶ���õ��豸��Ϣ
    static public boolean isLocal = true;
    static public RemoteInteractionStreamer mediastream;
    static public boolean mb3gdevice = false;
    public static ArrayList<String> ssidList;
    static public boolean is3Gor4G = false;
    public static String conRec;
    public static String alarmRec;
    public static int screenWidth, screenHeight;
    private final int MAX_ITEMS = 6;         // 7��ѡ��
    private String mID;
    private AppData appData;
    private WifiManager mWifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_setting_layout);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //20160822byhyw
        conRec = getString(R.string.continuous_recording);
        alarmRec = getString(R.string.alarm_recording);
        appData = (AppData) getApplication();

        Bundle getData = getIntent().getExtras();
        mID = getData.getString("id");
        isLocal = getData.getBoolean("isLocal");


        ssidList = new ArrayList<String>();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> wifiList = mWifiManager.getScanResults();
        if (wifiList != null) {
            for (ScanResult scanResult : wifiList) {
                ssidList.add(scanResult.SSID);
            }
        }

        mDevice = new Device();
        if (isLocal) {
            mDevice = appData.getLocalList().getDevice(mID);
            Log.i(TAG, "--mID:" + mID + "--mDevice:" + mDevice);
        } else {
            mDevice = appData.getAccountInfo().getCurrentList().getDevice(mID);
            mediastream = appData.getRemoteInteractionStreamer();
        }

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
        TextView titleName = (TextView) findViewById(R.id.titleName);

        if (isLocal) {
            titleName.setText(mDevice.getLocalInfo().getDeviceName() + "(" + mDevice.getLocalInfo().getCamSerial() + ")");
        } else {
            titleName.setText(mDevice.getSee51Info().getDeviceName() + "(" + mDevice.getSee51Info().getDiviceID() + ")");
        }

        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LocalSettingActivity.this.finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        parsedevicetype(mID);


        ListView lv = (ListView) findViewById(R.id.localSettingList);
        lv.setOnItemClickListener(this);
        SimpleAdapter adapter = new SimpleAdapter(
                this, getData(),
                R.layout.local_setting_item, new String[]{"img", "title", "info"},
                new int[]{R.id.localsettingItemImage, R.id.itemTitle, R.id.itemDescription});
        lv.setAdapter(adapter);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;

    }

    private List<? extends Map<String, ?>> getData() {
        // TODO Auto-generated method stub

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int li = 0; li < MAX_ITEMS - 1; li++) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", itemImage[li]);
            map.put("title", getString(itemsTitle[li]));
            map.put("info", getString(itemDescription[li]));

            if (isLocal) {
                if (mb3gdevice) {
                    if (li == 2) {
                        map.put("title", getString(itemsTitle[5]));
                        map.put("info", getString(itemDescription[5]));
                    }
                }

            } else {
                if (mb3gdevice) {
                    if (li == 1) {
                        map.put("title", getString(itemsTitle[5]));
                        map.put("info", getString(itemDescription[5]));
                    }

                    if (li == 2)
                        continue;
                } else {
                    if (li == 1 || li == 2)
                        continue;
                }
            }

            list.add(map);
        }
        return list;
    }

    @SuppressLint("NewApi")
    public void parsedevicetype(String minID) {
        if (minID.isEmpty() || minID.equals(""))
            return;

        String strType1 = minID.substring(0, 4);
        String strType2 = minID.substring(0, 3);
        mb3gdevice = strType1.equals("3321") || strType1.equals("3322")
                || strType1.equals("3421") || strType1.equals("3422")
                || strType2.equals("a02") || strType2.equals("a42")
                || strType2.equals("a82") || strType2.equals("a83")
                || strType2.equals("a84");

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        junmpToActivity(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do whatever you want, e.g. finish()
                LocalSettingActivity.this.finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void junmpToActivity(int which) {
        switch (which) {
            case 0:
                Intent Intent0 = new Intent(this, GeneralInfoAcy.class);
                Intent0.putExtra("whichItem", which);
                startActivity(Intent0);
                break;
            case 1:

                Intent intent1;
                if (isLocal) {
                    //WiredSettingAcy  which
                    intent1 = new Intent(this, WiredSettingAcy.class);
                    intent1.putExtra("whichItem", which);
                    startActivity(intent1);
                } else {
                    if (mb3gdevice) {
                        //Net3GSettingAcy  5
                        intent1 = new Intent(this, Net3GSettingAcy.class);
                        intent1.putExtra("whichItem", 5);
                        startActivity(intent1);
                        break;
                    } else {
                        //AVSettingAcy   3
                        intent1 = new Intent(this, AVSettingAcy.class);
                        intent1.putExtra("whichItem", 3);
                        startActivity(intent1);
                    }
                }
                break;
            case 2:

                Intent intent2;
                if (isLocal) {
                    if (mb3gdevice) {
                        //Net3GSettingAcy	5
                        intent2 = new Intent(this, Net3GSettingAcy.class);
                        intent2.putExtra("whichItem", 5);
                        startActivity(intent2);
                        break;
                    } else {
                        //WirelessSettingAcy	which
                        intent2 = new Intent(this, WirelessSettingAcy.class);
                        intent2.putExtra("whichItem", which);
                        startActivity(intent2);
                    }

                } else {
                    if (mb3gdevice) {
                        //AVSettingAcy	3
                        intent2 = new Intent(this, AVSettingAcy.class);
                        intent2.putExtra("whichItem", 3);
                        startActivity(intent2);
                    } else {
                        //AlarmCloudRecordAcy	4
                        intent2 = new Intent(this, AlarmCloudRecordAcy.class);
                        intent2.putExtra("whichItem", 4);
                        startActivity(intent2);
                    }

                }
                break;
            case 3:
                Intent intent3;
                if (isLocal) {
                    //AVSettingAcy	which
                    intent3 = new Intent(this, AVSettingAcy.class);
                    intent3.putExtra("whichItem", which);
                    startActivity(intent3);
                } else {
                    if (mb3gdevice) {
                        //AlarmCloudRecordAcy	4
                        intent3 = new Intent(this, AlarmCloudRecordAcy.class);
                        intent3.putExtra("whichItem", 4);
                        startActivity(intent3);
                    }
                }
                break;
            case 4:
                //AlarmCloudRecordAcy	which
                Intent intent4 = new Intent(this, AlarmCloudRecordAcy.class);
                intent4.putExtra("whichItem", which);
                startActivity(intent4);
                break;
            default:
                break;
        }
    }

    public void showToast(int resId) {
        Toast toast = Toast.makeText(this, getString(resId), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

//	WaitingDialogFragment end

    public void refreshDeviceInfo(DeviceLocalInfo localInfo) {
        mDevice.setLocalInfo(localInfo);
    }

    public void refreshDevice3GInfo(Device3GShortParam remote3GInfo) {
        mDevice.set3GParam(remote3GInfo);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediastream != null) {
            mediastream = null;
        }
    }

    static public class WaitingDialogFragment extends SherlockDialogFragment {

        public static final int MSG_SET_SUCESS = 0;
        public static final int MSG_SET_FAILED = 1;
        public static final int MSG_SET_TIMEOUT = 2;
        static DeviceLocalInfo deviceLocalInfo;
        static Device3GShortParam device3GInfo;
        ProgressDialog pd;
        TimeOutAsyncTask asyncTask;
        LocalService localService;
        DeviceList localDevList;
        Toast toast;
        int which;
        Activity mActivity;
        Handler handler;
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

        public static WaitingDialogFragment newInstance(int which, DeviceLocalInfo info) {
            WaitingDialogFragment frag = new WaitingDialogFragment();
            Bundle args = new Bundle();
            args.putInt("which", which);
            deviceLocalInfo = info;
            frag.setArguments(args);
            return frag;
        }

        public static WaitingDialogFragment newInstance(int which, Device3GShortParam info) {
            WaitingDialogFragment frag = new WaitingDialogFragment();
            Bundle args = new Bundle();
            args.putInt("which", which);
            device3GInfo = info;
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            which = getArguments().getInt("which");

            OnSetDeviceListener onSetlistener = new OnSetDeviceListener() {

                @Override
                public void onSetDeviceSucess(DeviceLocalInfo devInfo) {
                    // TODO Auto-generated method stub
                    //Log.d(TAG, " onSetDeviceSucess " );
                    if (devInfo.getCamSerial().equals(deviceLocalInfo.getCamSerial())) {
                        handler.sendEmptyMessage(MSG_SET_SUCESS);
                    }
                    //localDevList.clear();

                    if (mb3gdevice) {
                        localService.search3g(devInfo);
                    } else {
                        localService.search();
                    }
                }

                @Override
                public void onSetDeviceFailed(DeviceLocalInfo devInfo) {
                    // TODO Auto-generated method stub
                    if (devInfo.getCamSerial().equals(deviceLocalInfo.getCamSerial())) {
                        handler.sendEmptyMessage(MSG_SET_FAILED);
                    }
                }
            };

            if (isLocal) {
                localService.setListener(onSetlistener);
            } else {

                if (mb3gdevice && which == 5) {
                    mediastream.setOnSet3GInfoListener(mOnSet3GInfoListener);
                } else {
                    mediastream.setOnSetDevInfoListener(mOnSetDevInfoListener);
                }
            }

            pd = new ProgressDialog(getActivity());
            pd.setTitle(R.string.sure);
            pd.setMessage(getString(settingParaMsg[which]));

            pd.setCancelable(true);
            pd.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    if (isLocal) {
                        localService.setListener(null);
                    } else {
                        //mediastream.setOnSetDevInfoListener(null);
                    }
                }
            });
            pd.show();

//bm-modi-1101
            asyncTask = new TimeOutAsyncTask(this);
            asyncTask.execute(0);
            return pd;
        }

        @Override
        public void onStop() {
            super.onStop();
            if (!asyncTask.isCancelled()) {
                asyncTask.cancel(true);
                if (isLocal) {
                    localService.setListener(null);
                } else {
                    //mediastream.setOnSetDevInfoListener(null);
                }
            }
        }

        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_SUCESS) {
                ((LocalSettingActivity) mActivity).showToast(setParaSuccessMsg[which]);
                ((LocalSettingActivity) mActivity).refreshDeviceInfo(deviceLocalInfo);
                ((LocalSettingActivity) mActivity).refreshDevice3GInfo(device3GInfo);

                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == MSG_SET_FAILED) {
                ((LocalSettingActivity) mActivity).showToast(setParaFailedMsg[which]);
                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == MSG_SET_TIMEOUT) {
                if (which != 2) {                     //�������߲���ʱ����鳬ʱ
                    ((LocalSettingActivity) mActivity).showToast(R.string.setFail);
                }
                pd.cancel();
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
            handler = new MyHandler(this);
            localService = ((AppData) activity.getApplication()).getLocalService();
            localDevList = ((AppData) activity.getApplication()).getLocalList();

        }

        //bm-add-1101 begain
        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            handler.removeCallbacksAndMessages(null);
        }

        static class MyHandler extends Handler {
            WeakReference<WaitingDialogFragment> frg;

            MyHandler(WaitingDialogFragment wf) {
                frg = new WeakReference<WaitingDialogFragment>(wf);
            }

            @Override
            public void handleMessage(Message msg) {
                WaitingDialogFragment wdf = frg.get();
                wdf.handleMessage(msg);
            }
        }

        //bm-add-1101 end
        //bm-modi-1101 begain
        static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {

            /*
            * ִ�����̣�
            * 1.onPreExecute()
            * 2.doInBackground()-->onProgressUpdate()
            * 3.onPostExecute()
            */
            private WeakReference<SherlockDialogFragment> ref;

            public TimeOutAsyncTask(SherlockDialogFragment mFragment) {
                ref = new WeakReference<SherlockDialogFragment>(mFragment);
            }

            @Override
            protected void onPreExecute() {
                //��һ��ִ�з���
                super.onPreExecute();

                if (ref == null)
                    return;

                WaitingDialogFragment mAct = (WaitingDialogFragment) ref.get();
                if (mAct != null) {
                    if (isLocal) {
                        if (mb3gdevice) {
                            System.out.println("TimeOutAsyncTask---mb3gdevice");

                            if (is3Gor4G) {
                                mAct.localService.setDevice3GParam(deviceLocalInfo);
                                is3Gor4G = false;
                            } else {
                                mAct.localService.setDeviceParam(deviceLocalInfo);
                            }
                        } else {
                            mAct.localService.setDeviceParam(deviceLocalInfo);
                        }
                    } else {
                        if (mb3gdevice && mAct.which == 5) {
                            mediastream.send3GDevInfo(device3GInfo);
                        } else {
                            mediastream.setDevInfo(deviceLocalInfo);
                        }
                    }
                }
            }

            //bm-modi-1101 end
            @Override
            protected String doInBackground(Integer... params) {
                // TODO Auto-generated method stub
                try {
                    //Log.d(TAG, " sleep " );
                    if (isLocal) {
                        Thread.sleep(5000);
                    } else {
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                //��һ��ִ�з���
                super.onPreExecute();
                //bm-modi-1101 begain
                WaitingDialogFragment mAct = (WaitingDialogFragment) ref.get();
                if (mAct != null)
                    mAct.handler.sendEmptyMessage(MSG_SET_TIMEOUT);
                //bm-modi-1101 end
            }
        }
    }
}
