package com.my51c.see51.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.method.wifilist.WifiAdmin;
import com.method.wifilist.WifiConstant;
import com.my51c.see51.adapter.SetListAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.common.SystemConfigSp;
import com.my51c.see51.config.ServerConfig;
import com.my51c.see51.data.AccountInfo;
import com.my51c.see51.data.AlarmInfo;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceSee51Info;
import com.my51c.see51.data.Group;
import com.my51c.see51.data.GvapDBAdapter;
import com.my51c.see51.data.SelectionDevice;
import com.my51c.see51.guide.DeviceIdActivity;
import com.my51c.see51.guide.GuidSmartId;
import com.my51c.see51.guide.Guide;
import com.my51c.see51.listener.OnRegisterSucessListener;
import com.my51c.see51.map.CameraLocation;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.protocal.GvapPackage;
import com.my51c.see51.protocal.GvapXmlParser;
import com.my51c.see51.service.GvapEvent;
import com.my51c.see51.service.GvapEvent.GvapEventListener;
import com.my51c.see51.ui.ChangeUserPasswdActivity.OnChangePasswdListener;
import com.my51c.see51.ui.LoginFragment.OnLoginListener;
import com.my51c.see51.ui.MyDialogFragmentActivity.MyAlertDialogFragment.OnRegisterListener;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.List;
//bm-add-1101 
//snt-add

public class MainActivity extends SlidingFragmentActivity implements
        OnLoginListener, GvapEventListener, OnRegisterListener,
        OnItemClickListener, OnCheckedChangeListener, OnChangePasswdListener,
        android.view.View.OnClickListener {

    public static final int DIALOG_ID_LOGIN = 0;
    public static final int DIALOG_ID_REGISTER = 1;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_RUN = "first";
    public static final String ALARM_NOTIFICATION = "alarmNotification";
    public final static String GUIDE_FINISH_ACTION = "guide_finish_action";
    public static boolean isLocalList = false;
    public static AccountInfo account;
    public static Boolean isLogin = false;
    public static int openNum = 0;
    public static boolean canRefresh = true;
    public static boolean FromPlatAcy = false;
    public static boolean fromFav = false;
    public static boolean isLoginClicked = false;
    public static AccountInfo registerAccountInfo;
    public static boolean isRegistered;
    private static AlertDialog nonetworkAlertDialog;
    public Button menuBtn;
    public Button menuBack;
    public Button menuSet;
    public SlidingMenu sm;
    public Dialog mDialog;
    IntentFilter intentFilter = new IntentFilter();
    RefreshDevListReceiver devlistreceiver = new RefreshDevListReceiver();
    private String TAG = "MainActivity";
    private AppData appData;
    private GvapDBAdapter dbAdapter;
    private NotificationManager notificationManager;
    private SharedPreferences preferences;
    private boolean displayHome;
    private Boolean isAlarmNotification;
    private Fragment mContent;
    private SetFragment setFragment;
    private OnRegisterSucessListener onRegisterSucessListener;
    private Thread reLoginThread = null;
    private boolean bReLoginFlag = true;//no use now, by marshal
    private int tryLoginTimes = 0;
    private boolean bNetConnnect = false;
    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if ((mobNetInfo == null || !mobNetInfo.isConnected())
                    && (wifiNetInfo == null || !wifiNetInfo.isConnected())) {
                setNetStatus(false);

                nonetworkAlertDialog.show();

            } else {
                setNetStatus(true);
            }
        }
    };
    Runnable reLoginRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (isLogin)    //always runing check if need reloagin
            {
                Log.i(TAG, "--isLogin:always runing check if need relogin");
                if (!bNetConnnect)//bNetConnnect����������״̬
                {
                    Log.i(TAG, "--����������");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.d(TAG, "5s continue");
                    continue;

                } else {
                    Log.i(TAG, "--����������");
                }

                if (!appData.getAccountInfo().isLogined()) {
                    Log.i(TAG, "--�û�δ��¼-ConnectReset");
                    ConnectReset();
                } else {
                    Log.i(TAG, "--�û��ѵ�¼");
                }
                try {
                    Log.i(TAG, "--reLoginRunnable sleep 15s");
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    };
    private Button mapBtn;
    private boolean isMapMode = false;
    private boolean isMapShowed = false;
    private String strModifyPass;
    private MyHandler mHandler = new MyHandler(this);
    private String errorString;
    private int registRetryTimes = 0;

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void removeWifi(int id) {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        if (!wifiAdmin.getSSID().replace("\"", "").equals(WifiConstant.constant_SSID)) {
            WifiManager wifiManager = wifiAdmin.getWifiManager();
            int curId = wifiAdmin.getNetworkId();
            wifiManager.removeNetwork(curId);
            wifiManager.enableNetwork(id, false);
        }
    }

    public void setOnRegisterSucessListener(OnRegisterSucessListener listener) {
        onRegisterSucessListener = listener;
    }

    public void showLoginErroDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Erro_Dialog);
        dialog.setContentView(R.layout.login_erro_dialog);
        TextView erroTx = (TextView) dialog.findViewById(R.id.erroTx);
        Button reTry = (Button) dialog.findViewById(R.id.erro_reTry);
        Button cancel = (Button) dialog.findViewById(R.id.erro_cancel);
        erroTx.setText(getString(R.string.invalidPassword));
        reTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onLoginClicked(account);// ִ��һ�ε�¼����
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showAddDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Erro_Dialog);
        dialog.setContentView(R.layout.add_dev_dialog);
        LinearLayout addInstall = (LinearLayout) dialog.findViewById(R.id.add_install);
        LinearLayout idAdd = (LinearLayout) dialog.findViewById(R.id.addbyid);
        LinearLayout voiceAdd = (LinearLayout) dialog.findViewById(R.id.addbyvoice);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
        addInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onJumpGuideActivity("complex");
            }
        });
        idAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onJumpGuideActivity("simple");
            }
        });
        voiceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, GuidSmartId.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void setNetStatus(boolean bNet) {
        bNetConnnect = bNet;
        if (appData != null) {
            appData.setNetStatus(bNet);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        SystemConfigSp.instance().init(getApplicationContext());
        if (TextUtils.isEmpty(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERIP))) {
            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERIP, ServerConfig.loginServerAddr);
        }

        if (SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERPORT) == 0) {
            SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERPORT, ServerConfig.loginServerPort);
        }

        if (TextUtils.isEmpty(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERIP))) {
            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERIP, ServerConfig.regServerAddr);
        }

        if (SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERPORT) == 0) {
            SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERPORT, ServerConfig.regServerPort);
        }


        nonetworkAlertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.sure)
                .setMessage(R.string.nonetwork)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).create();

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
        menuBtn = (Button) findViewById(R.id.menuBtn);
        menuBack = (Button) findViewById(R.id.menuBack);
        mapBtn = (Button) findViewById(R.id.mapBtn);
        menuSet = (Button) findViewById(R.id.setBtn);

        menuBtn.setOnClickListener(this);
        menuBack.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        menuSet.setOnClickListener(this);


        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            // show home as up so we can toggle
//			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        // set the Above View Fragment
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new LoginFragment();
        if (setFragment == null)
            setFragment = new SetFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();
        menuSet.setVisibility(View.VISIBLE);

        // set the Behind View Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, setFragment).commit();
        // customize the SlidingMenu
        sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setMode(SlidingMenu.LEFT);
        sm.setBehindScrollScale(0.25f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setFadeDegree(0.25f);

        //sm.setBackgroundImage(R.drawable.slide_menu_bg);
        sm.setBackgroundColor(getResources().getColor(R.color.header_bar_color));
        /*
		sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        isAlarmNotification = settings.getBoolean(ALARM_NOTIFICATION, false);

        // ��ʼ��appdata
        appData = (AppData) getApplication();
        if (!appData.getGVAPService().isRunning()) {
            if (!appData.init()) {
            }
        }
        appData.getGVAPService().addGvapEventListener(this);


        notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        setDevListReceiver();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add("menu").setIcon(R.drawable.menu)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        //Log.d(TAG ,"onPrepareOptionsMenu(Menu menu)");
//		if(isFirst)
//		{
//			isFirst = false;
//		}
//		else
//			toggle();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                backParent();
                break;
        }
        if (item.getTitle().equals("menu")) {
            toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        //	super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Boolean first = settings.getBoolean(FIRST_RUN, true);
        if (first) {
            editor.putBoolean(FIRST_RUN, false);
        }
        editor.commit();

        super.onStop();//edit by hyw 20161201
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //bm-add-1101 begain
        mHandler.removeCallbacksAndMessages(null);

        if (nonetworkAlertDialog != null) {
            nonetworkAlertDialog = null;
        }

        if (account != null) {
            account = null;
        }
        //bm-add-1101 end

        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }

        if (devlistreceiver != null) {
            unregisterReceiver(devlistreceiver);
            devlistreceiver = null;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onLoginClicked(AccountInfo account) {
        // TODO Auto-generated method stub
        appData.getGVAPService().logout();
        appData.clearSelectDev();
        if (appData.getGVAPService().getUsrAccount() != null &&
                appData.getGVAPService().getUsrAccount().getDevList() != null)
            appData.getGVAPService().getUsrAccount().getDevList().clear();
        appData.getGVAPService().login(account);
        // appData.setAccountInfo(account);
        MainActivity.account = account;
        tryLoginTimes = 0;
//		showDialog(DIALOG_ID_LOGIN);
        showLoginDialog(DIALOG_ID_LOGIN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRegisterClicked(AccountInfo account) {
        // TODO Auto-generated method stub
        appData.getGVAPService().removeGvapEventListener(this);
        appData.getGVAPService().restartRegServer();
        appData.getGVAPService().addGvapEventListener(this);
        appData.getGVAPService().register(account);
        registerAccountInfo = account;
        isRegistered = false;
        registRetryTimes = 0;
//		showDialog(DIALOG_ID_REGISTER);
        showLoginDialog(DIALOG_ID_REGISTER);
    }

    private void onGetVersionsSuccess(GvapPackage response) {
        if (!appData.getAccountInfo().isGuest()) {
            int ver = response.getIntegerParamWithDefault("dev-version", -1);
            if (ver != appData.getAccountInfo().getDevList().getVersion()) {
                appData.getGVAPService().getDeviceList();
                appData.getAccountInfo().getDevList().setServeVersion(ver); // �����б�ķ������汾
            } else
            // ���ذ汾��������汾��ͬ
            {
                appData.getGVAPService().getDeviceStatus(
                        appData.getAccountInfo().getDevList());
            }
        }
		/*
		int ver = response.getIntegerParamWithDefault("pub-version", -1);
		if (ver != appData.getPublicList().getVersion())
		{
			// ���ذ汾��������汾��ͬ����ȡ���°汾
			appData.getGVAPService().getPublicList();
			appData.getPublicList().setServeVersion(ver); // �����б�ķ������汾
		} else
		// ���ذ汾��������汾��ͬ,ֱ�ӻ�ȡ�豸״̬
		{
			appData.getGVAPService().getDeviceStatus(appData.getPublicList());
		}
		*/
    }

    private void updateDeviceInfo(GvapPackage request, GvapPackage response) // �����豸״̬����Ϣ
    {
        if (!appData.getAccountInfo().isGuest()) {
            String groupId = request.getParent_group();
            if (groupId == null) {
                GvapXmlParser.parseDevInfo(new String(response.getContent()),
                        appData.getAccountInfo().getDevList());
                appData.getAccountInfo().getDevList().listUpdated();
            } else {
                GvapXmlParser.parseDevInfo(new String(response.getContent()),
                        appData.getAccountInfo().getDevList(groupId));
                appData.getAccountInfo().getDevList(groupId).listUpdated();
            }

        }
        GvapXmlParser.parseDevInfo(new String(response.getContent()),
                appData.getPublicList());
        appData.getPublicList().listUpdated();
    }

    private void onOperationSuccess(GvapCommand cmd, GvapPackage response, GvapPackage request) {
        Message msg;
        Log.i(TAG, "onOperationSuccess  cmd == " + cmd);
        //Log.d(TAG, "onOperationSuccess retcode: " + response.getStatusCode());
        switch (cmd) {
            case CMD_LOGIN: {
                if (account != null) {
                    account.setLogined(true);
                    appData.setAccountInfo(account);
                }
                appData.getAccountInfo().setLogined(true);
                appData.getGVAPService().getVersions();  // ��ȡ�汾��Ϣ
                // �����˻���Ϣ
//			 dbAdapter.saveAccount(appData.getAccountInfo(), true);
                // isLogin = true;
                if (appData.isReLogin()) {
                    appData.setReLogin(false);
                } else {
                    msg = mHandler.obtainMessage(0, GvapCommand.CMD_LOGIN);
                    mHandler.sendMessage(msg);
                }

                //sendBroadcast(new Intent(LoginFragment.LOGIN_SUC_ACTION));//20160814  by hyw
                //snt-del
                appData.getGVAPService().setHeartBeat_expire(
                        Integer.parseInt(response.getParam("expire")));
                break;
            }
            case CMD_GET_VERSIONS: {
                onGetVersionsSuccess(response);
                break;
            }
            case CMD_GET_DEVLIST: {
                String parent_group = request.getParent_group();
                if (parent_group != null)//�����б�
                {

                    if (appData != null && !"".equals(appData)) {//edit by hyw 20161201
                        onGetDeviListSuccess(response, appData.getAccountInfo()
                                .getDevList(parent_group));
                    }

                } else                    //��Ŀ¼�б�
                {
                    onGetDeviListSuccess(response, appData.getAccountInfo()
                            .getDevList());
                }
                break;
            }
            case CMD_GET_PUBLIST: {
                String parent_group = request.getParent_group();
                if (parent_group != null) {
                    onGetDeviListSuccess(response, appData.getDevList(parent_group));
                } else
                    onGetDeviListSuccess(response, appData.getPublicList());
                break;
            }
            case CMD_GET_DEVSTATUS: // ��ȡ�豸����״̬
                String groupId = request.getParent_group();
                updateDeviceInfo(request, response);
                if (!appData.getAccountInfo().isGuest()) {
                    if (groupId != null) {
                        appData.getGVAPService().getDeviceInfo(
                                appData.getAccountInfo().getDevList(groupId));
                    } else {
                        appData.getGVAPService().getDeviceInfo(//��ȡ�б����豸��ϸ��Ϣ��CMD_GET_DEVINFO��
                                appData.getAccountInfo().getDevList());
                    }
                }
                //appData.getGVAPService().getDeviceInfo(appData.getPublicList());
                break;
            case CMD_GET_DEVINFO: {
                updateDeviceInfo(request, response);//����xml
                // �����б���Ϣ
                if (!appData.getAccountInfo().isGuest()) {
                    // dbAdapter.saveDeviceList(appData.getPublicList(),
                    // appData.getAccountInfo().getUsername());
                }
                // dbAdapter.saveDeviceList(appData.getPublicList(), null);
                break;
            }

            case CMD_UPDATE_USERINFO: {
                msg = mHandler.obtainMessage(0, GvapCommand.CMD_UPDATE_USERINFO);
                mHandler.sendMessage(msg);
            }
            break;
            case CMD_GET_USRINFO:
            case CMD_UPDATE_DEVINFO:
                //����gps�ɹ�
//			Log.i("Main", "����GPS�ɹ�");
//			Toast.makeText(getApplicationContext(), "����GPA�ɹ�", Toast.LENGTH_LONG).show();
                break;
            case CMD_REGISTER:
                msg = mHandler.obtainMessage(0, GvapCommand.CMD_REGISTER);
                mHandler.sendMessage(msg);
                break;
            case CMD_BIND:
                break;
            case CMD_UNBIND:
                msg = mHandler.obtainMessage(0, GvapCommand.CMD_UNBIND);
                mHandler.sendMessage(msg);
                break;
            case CMD_HB:
            case CMD_NOTIFY_DEVSTATUS: {
                break;
            }
        }
    }

    /**
     * getParamList��ȡ�б�
     */
    private void onGetDeviListSuccess(GvapPackage response, DeviceList devList) {
        if (fromFav) {
            devList.clear();
            fromFav = false;
        }
        List<String> list = response.getParamList("device-id");
        List<String> listGro = response.getParamList("group-id");
        if (listGro != null) {
            for (String gro : listGro) {
                Group group = new Group(gro);
                devList.addGroup(group);
            }
            devList.updateSuccess();// �����б���³ɹ���־
            appData.getGVAPService().getGroupInfo(devList);
        }
        if (list != null) {
            //Log.d("GVAPService", "CMD_GET_DEVLIST: list != null ");
            Group group = devList.getParent_group();
            if (group != null)
                group.setDevCount(list.size());
            for (String dev : list) {
                DeviceSee51Info info = new DeviceSee51Info(dev);

                List<SelectionDevice> mylist = appData.getM_selectdevice();
                boolean bFound = false;
                if (mylist != null) {
                    for (int i = 0; i < mylist.size(); i++) {
                        SelectionDevice seldev = mylist.get(i);

                        if (seldev.getDeviceid().equals(info.getDiviceID()) && seldev.isLocal() == false) {
                            bFound = true;
                            break;
                        }
                    }
                }

                devList.addBySee51Info(info, bFound);    // �����µ��豸
            }

            devList.updateSuccess();            // �����б���³ɹ���־
            appData.getGVAPService().getDeviceStatus(devList);
        }


    }

    private void onOperationFailed(GvapCommand cmd, GvapPackage response) {
        Message msg;
        if (cmd == null) {
            Log.i(TAG, "onOperationFalse cmd == null");
            return;
        }
        //Log.d(TAG, "onOperationFalse  cmd == " + cmd);
        //Log.d(TAG, "onOperationFalse retcode: " + response.getStatusCode());
        int retcode = response.getStatusCode();
        switch (cmd) {
            case CMD_LOGIN:

                switch (retcode) {
                    default:
                    case 403:
                        Log.i(TAG, "����ʧ�ܣ�relogin");
                        appData.setReLogin(true);
                        appData.getGVAPService().logout();
                        appData.clearSelectDev();
                        if (appData.getGVAPService().getUsrAccount() != null &&
                                appData.getGVAPService().getUsrAccount().getDevList() != null)
                            appData.getGVAPService().getUsrAccount().getDevList().clear();
                        appData.getGVAPService().login(account);
                        break;

                    case 405:
                    case 406:
                        Log.i(TAG, "����ʧ�ܣ�handleFailed");
                        msg = mHandler.obtainMessage(1, GvapCommand.CMD_LOGIN);
                        mHandler.sendMessage(msg);
                        break;
                }
                break;
            case CMD_REGISTER:
                Log.i(TAG, "����ʧ�ܣ�CMD_REGISTER");
                switch (retcode) {
                    case 409:
                        errorString = getString(R.string.username_has_beenused);
                        msg = mHandler.obtainMessage(1, GvapCommand.CMD_REGISTER);
                        mHandler.sendMessage(msg);
                        break;

                    default:
                        errorString = getString(R.string.registerfailed);
                        msg = mHandler.obtainMessage(1, GvapCommand.CMD_REGISTER);
                        mHandler.sendMessage(msg);
                        appData.getGVAPService().restartRegServer();
                        break;
                }

                break;

            case CMD_UPDATE_USERINFO: {
                Log.i(TAG, "����ʧ�ܣ�CMD_UPDATE_USERINFO");
                msg = mHandler.obtainMessage(1, GvapCommand.CMD_UPDATE_USERINFO);
                mHandler.sendMessage(msg);
            }
            break;

            case CMD_BIND:
                break;
            case CMD_UNBIND:
                break;
            default:
                Log.i(TAG, "����ʧ�ܣ�default��relogin");
                appData.setReLogin(true);
                appData.getGVAPService().logout();
                appData.clearSelectDev();
                if (appData.getGVAPService().getUsrAccount() != null &&
                        appData.getGVAPService().getUsrAccount().getDevList() != null)
                    appData.getGVAPService().getUsrAccount().getDevList().clear();
                appData.getGVAPService().login(account);

                break;
        }
    }

    public void onOperationTimeout(GvapCommand cmd, GvapPackage response) {
        Message msg;
        if (cmd == null) {
            Log.i(TAG, "onOperationTimeout cmd == null");
            return;
        }
        //Log.d(TAG, "onOperationTimeout  cmd == " + cmd);
        switch (cmd) {
            case CMD_LOGIN:
                if (appData.isReLogin()) {
                    Log.i(TAG, "--850 appData.isReLogin");
                    break;
                } else {
                    Log.i(TAG, "--855 tryLoginTimes++");
                    tryLoginTimes++;
                    if (tryLoginTimes < 2) {
                        appData.getGVAPService().logout();
                        appData.clearSelectDev();
                        if (appData.getGVAPService().getUsrAccount() != null &&
                                appData.getGVAPService().getUsrAccount().getDevList() != null)
                            appData.getGVAPService().getUsrAccount().getDevList().clear();

                        appData.getGVAPService().login(account);

                        break;
                    } else {
                        errorString = getString(R.string.timeout);
                        msg = mHandler.obtainMessage(3, GvapCommand.CMD_LOGIN);
                        mHandler.sendMessage(msg);
                    }
                }
//			msg = mHandler.obtainMessage(1, GvapCommand.CMD_LOGIN);//2015.10.09
//			mHandler.sendMessage(msg);
                break;
            case CMD_REGISTER:
                appData.getGVAPService().removeGvapEventListener(this);
                appData.getGVAPService().restartRegServer();
                appData.getGVAPService().addGvapEventListener(this);
                if (registerAccountInfo != null && registRetryTimes < 1) {
                    appData.getGVAPService().register(registerAccountInfo);
                    registRetryTimes++;
                } else {
                    //errorString = getString(R.string.registerfailed);
                    errorString = getString(R.string.registerfailed) + getString(R.string.timeout);
                    msg = mHandler.obtainMessage(1, GvapCommand.CMD_REGISTER);
                    mHandler.sendMessage(msg);
                }

                break;

            case CMD_UPDATE_USERINFO: {
                msg = mHandler.obtainMessage(1, GvapCommand.CMD_UPDATE_USERINFO);
                mHandler.sendMessage(msg);
            }
            break;
            case CMD_UNBIND:
                break;
            case CMD_GET_DEVLIST:
                Log.i(TAG, "��ȡԶ���б�ʱ");
                break;
            default:
                break;
        }
    }

    public void onConnectReset() {
        boolean i = reLoginThread == null;
        ///Log.i(TAG, "--reloginThread=null?��"+i+"--isLogin:"+isLogin);
        if (reLoginThread == null /*&&isLogin*/) {
            Log.i(TAG, "--run reLoginRunnable");
//				if(FromPlatAcy){
//				   GvapServer.openNum++;
//				}
            reLoginThread = new Thread(reLoginRunnable);
            reLoginThread.start();
        }
    }

    public void ConnectReset() {
        if (appData.getAccountInfo() != null) {
            Log.i(TAG, "--ConnectReset-relogin�ǳ�����");
            //Log.d(TAG, "relogin");
            appData.setReLogin(true);
            appData.getGVAPService().logout();
            appData.clearSelectDev();
            if (appData.getGVAPService().getUsrAccount() != null &&
                    appData.getGVAPService().getUsrAccount().getDevList() != null)
                appData.getGVAPService().getUsrAccount().getDevList().clear();

            appData.getAccountInfo().setLogined(false);
            appData.getGVAPService().login(account);

        }
    }

    public void onConnectFailed() {
        //Log.d(TAG, "onConnectFailed");
        if (appData.getAccountInfo() != null
                && appData.getAccountInfo().isLogined()) {
            Log.i(TAG, "---------onConnectFailed--->relogin");
            appData.setReLogin(true);
            appData.getGVAPService().logout();
            appData.clearSelectDev();
            if (appData.getGVAPService().getUsrAccount() != null &&
                    appData.getGVAPService().getUsrAccount().getDevList() != null)
                appData.getGVAPService().getUsrAccount().getDevList().clear();

            appData.getAccountInfo().setLogined(false);
            appData.getGVAPService().login(account);
        }
    }

    private void onNetworkError() {
        Log.i(TAG, "onConnectFailed");
        appData.getGVAPService().setUserServerLoginStatus(false);
        if (appData.getAccountInfo() != null
                && appData.getAccountInfo().isLogined()) {
            Log.i(TAG, "relogin");
            appData.setReLogin(true);
            appData.getGVAPService().logout();
            appData.clearSelectDev();
            if (appData.getGVAPService().getUsrAccount() != null &&
                    appData.getGVAPService().getUsrAccount().getDevList() != null)
                appData.getGVAPService().getUsrAccount().getDevList().clear();

            appData.getAccountInfo().setLogined(false);
            appData.getGVAPService().login(account);
        }
    }

    private void onGetServerRequest(GvapPackage response) {
        String alarmtype = response.getResourceName();

        if (alarmtype.equals("alarm")) {
            String deviceId = response.getParam("device-id");
            Device device = appData.getAccountInfo().getDevList().getDevice(deviceId);
            String devName = "ipcamera";
            if (device != null) {
                devName = device.getSee51Info().getDeviceName();
            }
            AlarmInfo alarmInfo = new AlarmInfo(deviceId, devName,
                    response.getParam("message"), response.getParam("title"));
            //Log.d(TAG, "onGetServerRequest");
            appData.getAccountInfo().addAlarmInfo(alarmInfo);
            addNotificaction();
        }
    }

    @Override
    public void onGvapEvent(GvapEvent event) {

        switch (event) {
            case OPERATION_SUCCESS:
                Log.i(TAG, "-----------�����ɹ�");
                onOperationSuccess(event.getCommandID(),//�������request.getCommandID()
                        (GvapPackage) event.attach(),//�������ظ���
                        (GvapPackage) event.getRequest());//�������from sendOverList��
                break;
            case OPERATION_FAILED:
                Log.i(TAG, "-----------����ʧ��");
                onOperationFailed(event.getCommandID(),
                        (GvapPackage) event.attach());
                break;
            case OPERATION_TIMEOUT:
                Log.i(TAG, "-----------������ʱ");
                onOperationTimeout(event.getCommandID(), (GvapPackage) event.attach());
                break;
            case CONNECTION_RESET:
                Log.i(TAG, "-----------��������");
                onConnectReset();
                break;
            case CONNECT_TIMEOUT:
                Log.i(TAG, "-----------���ӳ�ʱ");
                break;
            case CONNECT_FAILED:
                Log.i(TAG, "-----------����ʧ��");
                onConnectFailed();
                break;
            case NETWORK_ERROR:
                Log.i(TAG, "-----------�������");
                onNetworkError();
                break;
            case SERVER_REQUEST:
                Log.i(TAG, "-----------SERVER_REQUEST-����");
                onGetServerRequest((GvapPackage) event.attach());
                break;
            case OPEN_FAILED:
                Log.i(TAG, "-----------OPEN_FAILED");
                openNum++;
                appData.getGVAPService().stop();
                appData.getGVAPService().start();
                break;

        }
    }

    protected void onQuit() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage(R.string.quit);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setPositiveButton(android.R.string.ok,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            if (isLogin) {
                                isLogin = false;
                                bReLoginFlag = false;
                                reLoginThread = null;
                                setFragment.setIsLoginBtnName(isLogin);
                                appData.getGVAPService().logout();
                                appData.clearSelectDev();
                                if (appData.getGVAPService().getUsrAccount() != null &&
                                        appData.getGVAPService().getUsrAccount().getDevList() != null)
                                    appData.getGVAPService().getUsrAccount().getDevList().clear();

                                appData.getAccountInfo().setLogined(false);
                                appData.setAccountInfo(null);
                                FragmentManager fm = MainActivity.this
                                        .getSupportFragmentManager();
                                fm.findFragmentByTag("favorite").onDestroy();
//								actionBar.removeTabAt(1);
                                //Log.d(TAG, "removeTabAt(1)  onQuit()");
                                // actionBar.addTab(mLoginTab, 1, true);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        appData.release();
                        //appData.exitUI();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(0);

                        MainActivity.this.finish();

                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    protected void onLogout() {
        getSlidingMenu().showContent();
        if (isLogin) {
            isLogin = false;
            bReLoginFlag = false;
            reLoginThread = null;
            setFragment.setIsLoginBtnName(isLogin);
            appData.getGVAPService().logout();
            appData.clearSelectDev();
            if (appData.getGVAPService().getUsrAccount() != null &&
                    appData.getGVAPService().getUsrAccount().getDevList() != null)
                appData.getGVAPService().getUsrAccount().getDevList().clear();

            appData.getAccountInfo().setLogined(false);
            appData.setAccountInfo(null);
            setFragment.setLoginState(getString(R.string.notLogedIn));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, mContent).commit();
            mapBtn.setVisibility(View.GONE);
            menuSet.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //snt-modi from && to ||
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getRepeatCount() == 0) {
            //snt-del begain
//			if (isRootDirectory())
//			{
//				moveTaskToBack(true);
//			} else
//			{
//				try {
//					backParent();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return true;
//			}
//			//moveTaskToBack(false);
//		}
			/*Log.e(TAG, "-----------------------KEYCODE_BACK===="+keyCode);
			MainActivity.this.finish();*/
            //snt-del end
//---snt-modi  begain
            return false;
        }

        try {
            if (isLogin) {
                isLogin = false;
                bReLoginFlag = false;
                reLoginThread = null;
                setFragment.setIsLoginBtnName(isLogin);
                appData.getGVAPService().logout();
                appData.clearSelectDev();
                if (appData.getGVAPService().getUsrAccount() != null &&
                        appData.getGVAPService().getUsrAccount().getDevList() != null)
                    appData.getGVAPService().getUsrAccount().getDevList().clear();

                appData.getAccountInfo().setLogined(false);
                appData.setAccountInfo(null);
                FragmentManager fm = MainActivity.this
                        .getSupportFragmentManager();
                Fragment favfragment = fm.findFragmentByTag("favorite");
                if (favfragment != null)
                    favfragment.onDestroy();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        appData.release();


        this.finish();

        return super.onKeyDown(keyCode, event);

//-----snt-modi end 
    }

    public boolean isRootDirectory() {
        if (appData.getAccountInfo() != null) {
            DeviceList currentList = appData.getAccountInfo().getCurrentList();
            if (currentList != null) {
                return currentList.getParent_group() == null;
            }
        }
        return true;
    }

    public void backParent() {
        if (appData.getAccountInfo() != null) {
            DeviceList currentList = appData.getAccountInfo().getCurrentList();
            String grandParent_group = currentList.getGrandParent_group();
            DeviceList devList;
            if (grandParent_group != null) {
                devList = appData.getAccountInfo().getDevList(grandParent_group);
                appData.getAccountInfo().setCurrentList(devList);
            } else {
                menuBtn.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                devList = appData.getAccountInfo().getDevList(); // ������
                appData.getAccountInfo().setCurrentList(devList);
                displayHomeButton(false);
            }
            FavoriteFragment.adapter.setDeviceList(devList);
            devList.listUpdated();
        }
    }

    public void displayHomeButton(boolean isDisplay) {
        if (isDisplay) {
            if (!displayHome) {
//				actionBar.setHomeButtonEnabled(true);
//				actionBar.setDisplayHomeAsUpEnabled(true);
                displayHome = true;
            }
        } else {
            if (displayHome) {
//				actionBar.setHomeButtonEnabled(false);
//				actionBar.setDisplayHomeAsUpEnabled(false);
                displayHome = false;
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        ProgressDialog dialog = new ProgressDialog(this);
        switch (id) {
            case DIALOG_ID_LOGIN:
                dialog.setTitle(R.string.login);
                dialog.setMessage(getString(R.string.userlogining));
                dialog.setCancelable(true);
                break;
            case DIALOG_ID_REGISTER:
                dialog.setTitle(R.string.register);
                dialog.setMessage(getString(R.string.registerring));
                dialog.setCancelable(true);
            default:
                break;
        }

        return dialog;
    }


    public void showLoginDialog(int id) {
        mDialog = new Dialog(MainActivity.this, R.style.Login_Dialog);
        mDialog.setContentView(R.layout.login_dialog);
        TextView tx = (TextView) mDialog.findViewById(R.id.tx);
        if (id == DIALOG_ID_LOGIN)
            tx.setText(getString(R.string.userlogining));
        else if (id == DIALOG_ID_REGISTER)
            tx.setText(getString(R.string.registerring));
        mDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void addNotificaction() {
        //Log.d(TAG, "addNotificaction");
        if (isAlarmNotification) {
            Intent intent = new Intent(this, ActionAlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new Notification.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.alarm))
                    .setContentText(getString(R.string.strangers_illegal_instrusion))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .build();
            notification.defaults = Notification.DEFAULT_SOUND;
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //Log.d(TAG, "onItemClicked");
        // TODO Auto-generated method stub
        int listItemId = arg1.getId();
        switch (listItemId) {
            case SetListAdapter.ALRAMHISTORY:
                Intent intent = new Intent(this, ActionAlarmActivity.class);
                startActivity(intent);
                break;
            case SetListAdapter.SWITCHACCOUNT:
                onLogout();
                appData.clearSelectDev();
                if (appData.getGVAPService().getUsrAccount() != null &&
                        appData.getGVAPService().getUsrAccount().getDevList() != null)
                    appData.getGVAPService().getUsrAccount().getDevList().clear();

                break;
            case SetListAdapter.EDITPASSWD: {

                if (appData.getAccountInfo() == null
                        || !appData.getAccountInfo().isLogined()
                        || appData.getAccountInfo().isGuest()) {


                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new LoginFragment()).commit();

                    Toast.makeText(this, getString(R.string.pleaseLoginFirst), Toast.LENGTH_LONG).show();

                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new ChangeUserPasswdActivity()).commit();
                }

                getSlidingMenu().showContent();
            }
            break;

            case SetListAdapter.ABOUT: {
                Intent intentabout = new Intent(this, AboutActivity.class);
                startActivity(intentabout);
            }
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (isChecked) {
            editor.putBoolean(ALARM_NOTIFICATION, true);
            isAlarmNotification = true;
        } else {
            editor.putBoolean(ALARM_NOTIFICATION, false);
            isAlarmNotification = false;
        }
        editor.commit();
    }

    public void onJumpGuideActivity(String strType) {
        String inBindType = "complex";
        inBindType = strType;

        Intent intent;
        if (appData.getAccountInfo() == null
                || !appData.getAccountInfo().isLogined()
                || appData.getAccountInfo().isGuest()) {
            intent = new Intent(MainActivity.this, Guide.class);
            intent.putExtra("BindStyle", inBindType);
        } else {
            intent = new Intent(MainActivity.this, DeviceIdActivity.class);
            intent.putExtra("BindStyle", inBindType);
        }

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.menuBack:
                backParent();
                break;
            case R.id.menuBtn:
                sm.toggle();
                break;
            case R.id.setBtn: {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.set_server_addr, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setView(view);
                alertDialog.show();
                Window alertwindow = alertDialog.getWindow();
                alertwindow.setContentView(R.layout.set_server_addr);
                TextView tv_title = (TextView) alertwindow.findViewById(R.id.tv_dialog_title);
                tv_title.setText(getString(R.string.setting));

                Button yesbtn = (Button) alertwindow.findViewById(R.id.yesbtn);
                Button nobtn = (Button) alertwindow.findViewById(R.id.nobtn);

                final EditText userServerUrlEditText = (EditText) alertwindow.findViewById(R.id.userServerUrlEditText);
                final EditText userServerPortEditText = (EditText) alertwindow.findViewById(R.id.userServerPortEditText);
                final EditText regServerUrlEditText = (EditText) alertwindow.findViewById(R.id.regServerUrlEditText);
                final EditText regServerPortEditText = (EditText) alertwindow.findViewById(R.id.regServerPortEditText);

                String inputText = "";
                userServerUrlEditText.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERIP));
                inputText += SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERPORT);
                userServerPortEditText.setText(inputText);

                inputText = "";
                regServerUrlEditText.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERIP));
                inputText += SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERPORT);
                regServerPortEditText.setText(inputText);

                yesbtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        String getText;
                        int getint;

                        getText = userServerUrlEditText.getText().toString();
                        SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERIP, getText);

                        getint = Integer.parseInt(userServerPortEditText.getText().toString());
                        SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.LOGINSERVERPORT, getint);

                        getText = regServerUrlEditText.getText().toString();
                        SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERIP, getText);

                        getint = Integer.parseInt(regServerPortEditText.getText().toString());
                        SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.REGISTERSERVERPORT, getint);

                        appData.getGVAPService().release();
                        appData.getGVAPService().init();
                        appData.getGVAPService().addGvapEventListener(MainActivity.this);
                        appData.getGVAPService().start();

                        alertDialog.dismiss();
                    }
                });

                nobtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                    }
                });

            }
            break;
            case R.id.btnNewCamera: {

                if (appData.getAccountInfo() == null
                        || !appData.getAccountInfo().isLogined()
                        || appData.getAccountInfo().isGuest()) {
//					Intent intent = new Intent(MainActivity.this, Guide.class);
//					startActivity(intent);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new LoginFragment()).commit();
                    getSlidingMenu().showContent();
                    Toast.makeText(this, getString(R.string.pleaseLoginFirst), Toast.LENGTH_LONG).show();


                } else {
                    showAddDialog();
                }

            }
            break;

            case R.id.btnFourViews: {

                List<SelectionDevice> m_selectdevice = appData.getM_selectdevice();

                if (m_selectdevice == null || m_selectdevice.size() <= 0) {
                    Toast.makeText(this, getString(R.string.checkdevicefist), Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intent = new Intent(this, FourPlayerActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.btnLocalCamera:

                mapBtn.setVisibility(View.GONE);
                menuSet.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new LocalFragment()).commit();
                isLocalList = true;
                getSlidingMenu().showContent();
                break;

            case R.id.btnMyCamera:
                if (isLogin) {

                    mapBtn.setVisibility(View.VISIBLE);
                    menuSet.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new FavoriteFragment()).commit();

//				if(isMapShowed){
//					//��ͼģʽ
//					getSupportFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, new MapListFragment()).commit();		
//				}else{
//					//�б�ģʽ
//					getSupportFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, new FavoriteFragment()).commit();	
//				}
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new LoginFragment()).commit();
                    menuSet.setVisibility(View.VISIBLE);
                }

                isLocalList = false;
//			actionBar.setTitle(R.string.userdevice);
                getSlidingMenu().showContent();
                break;
            case R.id.btnProgramExit:
                mapBtn.setVisibility(View.GONE);
                menuSet.setVisibility(View.VISIBLE);
                onQuit();
                break;

            case R.id.btnLogin:
                if (isLogin) {
                    onLogout();
                    appData.clearSelectDev();
                    if (appData.getGVAPService().getUsrAccount() != null &&
                            appData.getGVAPService().getUsrAccount().getDevList() != null)
                        appData.getGVAPService().getUsrAccount().getDevList().clear();

                    setFragment.setIsLoginBtnName(isLogin);
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new LoginFragment()).commit();
                    getSlidingMenu().showContent();
                    menuSet.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.showWarn:

                break;
            case R.id.warnMsg:
                Intent intent = new Intent(this, ActionAlarmActivity.class);
                startActivity(intent);
                break;
//--snt-del begain
//		case R.id.changePsw:
//		{	
//			mapBtn.setVisibility(View.GONE);
//			menuSet.setVisibility(View.GONE);
//			if (appData.getAccountInfo() == null
//					|| !appData.getAccountInfo().isLogined()
//					|| appData.getAccountInfo().isGuest())
//			{
//
//				
//				getSupportFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, new LoginFragment()).commit();
//				
//				Toast.makeText(this, getString(R.string.pleaseLoginFirst), Toast.LENGTH_LONG ).show();
//				
//			}
//			else
//			{
//				getSupportFragmentManager().beginTransaction()
//				.replace(R.id.content_frame, new ChangeUserPasswdActivity()).commit();	
//			}
//			
//			getSlidingMenu().showContent();
//		}
//			break;
//------snt-del end

            case R.id.about: {
                Intent intentabout = new Intent(this, AboutActivity.class);
                startActivity(intentabout);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
            break;

            case R.id.progamexit: {
                try {
                    if (isLogin) {
                        isLogin = false;
                        bReLoginFlag = false;
                        reLoginThread = null;
                        setFragment.setIsLoginBtnName(isLogin);
                        appData.getGVAPService().logout();
                        appData.clearSelectDev();
                        if (appData.getGVAPService().getUsrAccount() != null &&
                                appData.getGVAPService().getUsrAccount().getDevList() != null)
                            appData.getGVAPService().getUsrAccount().getDevList().clear();

                        appData.getAccountInfo().setLogined(false);
                        appData.setAccountInfo(null);
                        FragmentManager fm = MainActivity.this
                                .getSupportFragmentManager();
                        Fragment favfragment = fm.findFragmentByTag("favorite");
                        if (favfragment != null)
                            favfragment.onDestroy();
//					actionBar.removeTabAt(1);
                        //Log.d(TAG, "removeTabAt(1)  onQuit()");
                        // actionBar.addTab(mLoginTab, 1, true);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                appData.release();
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(0);

                this.finish();
            }
            break;
//		case R.id.loginLayout:   // lxx-del 0814 
//			if(isLogin)
//			{
//				appData.clearSelectDev();
//				if(appData.getGVAPService().getUsrAccount() != null &&
//				   appData.getGVAPService().getUsrAccount().getDevList() != null)
//				appData.getGVAPService().getUsrAccount().getDevList().clear();
//				onLogout();
//				setFragment.setIsLoginBtnName(isLogin);
//			}
//			else
//			{	
//				getSupportFragmentManager().beginTransaction()
//				.replace(R.id.content_frame, new LoginFragment()).commit();		
//				getSlidingMenu().showContent();
//			
//			}
//			menuSet.setVisibility(View.VISIBLE);
//			break;
// lxx-del 0814 end

            case R.id.local_video:
                Intent intentabout = new Intent(this, LocalFileAcy.class);
                startActivity(intentabout);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                break;
            case R.id.mapBtn:
//			if (isLogin) {
//				if(isMapMode){
//					//��ͼģʽ
//					isMapMode = false;
//					isMapShowed = true;
//					mapBtn.setText(getString(R.string.maptx));
//					getSupportFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, new FavoriteFragment()).commit();		
//				}else{
//					//�б�ģʽ
//					isMapMode = true;
//					isMapShowed = false;
//					mapBtn.setText(getString(R.string.listtx));
//					getSupportFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, new MapListFragment()).commit();	
//				}
//				isLocalList = false;
//			}
                startActivity(new Intent(MainActivity.this, CameraLocation.class));
                break;
        }
    }

    @Override
    public void onChangePasswdClicked(String strOldPasswd, String strNewPasswd,
                                      String strNewPasswdAgain) {
        // TODO Auto-generated method stub


        Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.sure)).setPositiveButton(getString(R.string.comfirm), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //��ť�¼�
                    }
                });

        if (strOldPasswd.equals(appData.getAccountInfo().getPassword())) {
            if (strNewPasswd.length() >= 6 && strNewPasswd.length() <= 16) {
                if (strNewPasswd.equals(strNewPasswdAgain)) {
                    if (strOldPasswd.equals(strNewPasswd)) {
                        appData.getAccountInfo().setPassword(strNewPasswd);
                        //Toast.makeText(getApplicationContext(), getString(R.string.changepasswordsuccess), Toast.LENGTH_LONG ).show();
                        dialog.setMessage(getString(R.string.passwordmatch));
                        dialog.show();
                    } else {
                        strModifyPass = strNewPasswd;
                        appData.getGVAPService().removeGvapEventListener(MainActivity.this);
                        appData.getGVAPService().restartRegServer();
                        appData.getGVAPService().addGvapEventListener(MainActivity.this);
                        appData.getGVAPService().changePassword(appData.getAccountInfo(), strNewPasswd);
                    }
                } else {
                    dialog.setMessage(getString(R.string.passwordinmatch));
                    dialog.show();
                }
            } else {
                dialog.setMessage(getString(R.string.passwordLenRequire));
                dialog.show();
            }
        } else {
            dialog.setMessage(getString(R.string.orginpassworderror));
            dialog.show();
        }

    }

    public void setDevListReceiver() {
        IntentFilter filter = new IntentFilter(GUIDE_FINISH_ACTION);

        registerReceiver(devlistreceiver, filter);
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mRef;

        public MyHandler(MainActivity mAct) {
            mRef = new WeakReference<MainActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            GvapCommand cmd = (GvapCommand) msg.obj;

            switch (msg.what) {
                case 0:
                    handleSucess(cmd);
                    break;
                case 1:
                    handleFailed(cmd);
                    break;
                case 2:
                    FavoriteFragment.refreshImg.performClick();
                    break;
                case 3:
                    handleTimeOut(cmd);
                    break;
                default:
                    break;
            }
        }

        @SuppressWarnings("deprecation")
        private void handleSucess(GvapCommand commandID) {
            if (mRef == null)
                return;

            MainActivity activity = mRef.get();

            if (activity != null) {
                switch (commandID) {
                    case CMD_LOGIN:
                        activity.appData.setAccountInfo(account);
                        activity.appData.getAccountInfo().setLogined(true);
                        activity.appData.getGVAPService().setUserServerLoginStatus(true);
                        if (!activity.appData.getAccountInfo().isGuest()) {
                            if (activity.mDialog != null)
                                activity.mDialog.dismiss();
                            isLogin = true;
                            activity.setFragment.setIsLoginBtnName(isLogin);

                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, new FavoriteFragment()).commitAllowingStateLoss();

                            activity.setFragment.setLoginState(account.getUsername());
                            isLocalList = false;
                            activity.mapBtn.setVisibility(View.VISIBLE);
                            activity.menuSet.setVisibility(View.GONE);
                        }
                        break;
                    case CMD_REGISTER:
                        isRegistered = true;
                        activity.onRegisterSucessListener.onRegisterSucess();

                        if (activity.mDialog != null)
                            activity.mDialog.dismiss();
                        Toast toast = Toast.makeText(activity.getApplicationContext(),
                                activity.getString(R.string.registersuccess), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //	activity.sendBroadcast(new Intent(LoginFragment.LOGIN_FRA_ACTION));  //snt-modi-bm-1101
                        break;
                    case CMD_UPDATE_USERINFO: {
                        activity.appData.getAccountInfo().setPassword(activity.strModifyPass);

                        Builder dialog = new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.sure)).setPositiveButton(activity.getString(R.string.comfirm), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        //��ť�¼�
                                    }
                                });
                        dialog.setMessage(activity.getString(R.string.changepasswordsuccess));
                        dialog.show();

                    }
                    break;
                    default:
                        break;
                }
            }
        }

        @SuppressWarnings("deprecation")
        private void handleFailed(GvapCommand commandID) {
            // TODO Auto-generated method stub

            if (commandID == null) {
                return;
            }

            if (mRef == null)
                return;

            MainActivity activity = mRef.get();

            if (activity != null) {
                switch (commandID) {
                    case CMD_LOGIN:

                        activity.appData.getGVAPService().setUserServerLoginStatus(false);
                        if (!account.getUsername().equals("guest")) {
                            try {
                                if (activity.mDialog != null)
                                    activity.mDialog.dismiss();

                            } catch (IllegalArgumentException e) {
                            }
                            try {
                                if (!FromPlatAcy) {
                                    activity.showLoginErroDialog();
                                }
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }
                        break;
                    case CMD_REGISTER:
                        if (activity.mDialog != null)
                            activity.mDialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), activity.errorString,
                                Toast.LENGTH_LONG).show();
                        break;
                    case CMD_UPDATE_USERINFO: {
                        activity.strModifyPass = null;
                        Builder dialog = new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.sure)).setPositiveButton(activity.getString(R.string.comfirm), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        dialog.setMessage(activity.getString(R.string.changepasswordfailed));
                        dialog.show();
                    }
                    default:
                        break;
                }
            }
        }


        private void handleTimeOut(GvapCommand commandID) {
            if (mRef == null)
                return;

            MainActivity activity = mRef.get();

            if (activity != null) {

                switch (commandID) {
                    case CMD_LOGIN:
                        activity.appData.getGVAPService().setUserServerLoginStatus(false);
                        if (!account.getUsername().equals("guest")) {
                            try {
                                if (activity.mDialog != null)
                                    activity.mDialog.dismiss();

                            } catch (IllegalArgumentException e) {
                            }

                            Toast.makeText(activity.getApplicationContext(), activity.errorString,
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private class RefreshDevListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            appData.getGVAPService().getDeviceList();
        }
    }


}
