package com.my51c.see51.yzxvoip;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.yzxvoip.model.TelListsInfo;
import com.my51see.see.ITelephony;
import com.yzx.api.UCSCall;
import com.yzxtcp.tools.CustomLog;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
//import com.android.internal.telephony.ITelephony;

public class ConverseActivity extends BaseActivity {
    private static final String TAG = ConverseActivity.class.getSimpleName();
    public boolean isDownHome;
    // 用于监听系统电话呼入
    private TelephonyManager telMgr;
    private MonitoringSystemCallListener mIncomingCallMonitor;
    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager mKeyguardManager = null;
    private KeyguardLock mKeyguardLock = null;
    private InnerRecevier innerReceiver;
    private UserSetting userSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setVolumeControlStream(AudioManager.STREAM_RING);
        addTelophonyManagerListener();
        initProwerManager();
        enterIncallMode();
        super.onCreate(savedInstanceState);

        IntentFilter mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        innerReceiver = new InnerRecevier();
        getApplicationContext().registerReceiver(innerReceiver, mFilter);
        userSetting = UserSettingsDbManager.getInstance().getById(UserInfoDBManager
                .getInstance().getCurrentLoginUser().getAccount());
    }

    @Override
    protected void onDestroy() {
        cancalTelophonyManagerListener();
        releaseWakeLock();
        super.onDestroy();
        getApplicationContext().unregisterReceiver(innerReceiver);
    }

    /**
     * 添加电话状态监听器
     */
    private void addTelophonyManagerListener() {
        if (telMgr == null) {
            // 如果在通话时，侧自动挂断系统来电
            telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mIncomingCallMonitor = new MonitoringSystemCallListener();
            telMgr.listen(mIncomingCallMonitor, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    /**
     * 取消电话监听
     */
    private void cancalTelophonyManagerListener() {
        if (telMgr != null && mIncomingCallMonitor != null) {
            telMgr.listen(mIncomingCallMonitor, PhoneStateListener.LISTEN_NONE);
        }
    }

    private void initProwerManager() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "CALL_ACTIVITY" + "#"
                + getClass().getName());
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    }

    /**
     * 开锁
     */
    private void enterIncallMode() {
        if (!mWakeLock.isHeld())
            mWakeLock.acquire();

        // 初始化键盘锁，可以锁定或解开键盘锁
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        // 禁用显示键盘锁定
        mKeyguardLock.disableKeyguard();
    }

    /**
     * 通话结速时重新上锁
     */
    private void releaseWakeLock() {
        try {
            if (mWakeLock.isHeld()) {
                if (mKeyguardLock != null) {
                    mKeyguardLock.reenableKeyguard();
                    mKeyguardLock = null;
                }
                mWakeLock.release();
            }
        } catch (Exception e) {
            CustomLog.e("AndroidRuntime", e.toString());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * @param dailFlag 呼入呼出标志，0：呼入；1：呼出；2:呼入未接；3：呼出未接
     * @return void    返回类型
     * @Description 把拨打记录保存到数据库中
     * @date 2016-1-3 上午11:10:19
     * @author xhb
     */
    protected void addCallRecord(final int dailFlag, final boolean inCall,
                                 final String userName, final String phoneNumber, final String calledPhone,
                                 final int mCallType, final String telAdress, final String startTime, final String timer) {
        new Thread() {
            public void run() {
                TelListsInfo user = new TelListsInfo();
                user.setGravator("");
                CustomLog.i("phoneNumber:" + phoneNumber);
                user.setIsTop(0);
                if (inCall) {
                    if (userName != null && userName.length() > 0) {
                        user.setName(userName);
                    } else if (phoneNumber != null && phoneNumber.length() > 0) {
                        user.setName(phoneNumber);
                    }
                    if (phoneNumber != null && phoneNumber.length() > 0) {
                        user.setTelephone(phoneNumber);
                    }
                    user.setDialFlag(dailFlag == 2 ? 2 : 0);
                } else {
                    if (userName != null && userName.length() > 0) {
                        user.setName(userName);
                    } else if (calledPhone != null && calledPhone.length() > 0) {
                        user.setName(calledPhone);
                    }
                    if (calledPhone != null && calledPhone.length() > 0) {
                        user.setTelephone(calledPhone);
                    }
                    user.setDialFlag(dailFlag);
                }
                user.setTelMode(mCallType); // 1：代表语音通话,2:代表视频通话
                String callStartTime = startTime;
                if (callStartTime == null || callStartTime.length() == 0) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm");
                    callStartTime = dateFormat.format(new Date());
                }
                if (telAdress != null) {
                    user.setTelAdress(telAdress);
                    Log.e("adress==", telAdress);
                }
                user.setTime(callStartTime);
                user.setLoginPhone(getSharedPreferences("YZX_DEMO", 1).getString("YZX_ACCOUNT_INDEX", ""));
                CustomLog.v("telephoneUserInfos:" + user.toString());
                int result = TelListsInfoDBManager.getInstance().insert(user);
                Log.i(TAG, "insert id = " + result);
                user.setId(result);

                TelUsersInfo telUsersInfo = new TelUsersInfo();
                telUsersInfo.setTelListsInfo(user);
                telUsersInfo.setDialMessage(timer);
                TelUserInfoDBManager.getInstance().insert(telUsersInfo);
                Intent listIntent = new Intent();
                listIntent.setAction("tel_list_data_update");
                listIntent.putExtra("listData", user);
                AppData.getInstance().sendBroadcast(listIntent);
                Intent usersIntent = new Intent();
                usersIntent.setAction(UIDfineAction.ACTION_TEL_USER_INFO_DATA_UPDATE);
                usersIntent.putExtra("userData", telUsersInfo);
                AppData.getInstance().sendBroadcast(usersIntent);
            }
        }.start();
    }

    protected void startRing(Context context) {
        if (userSetting.getMsgNotify() == 1 && userSetting.getMsgVoice() == 1) {
            UCSCall.startRinging(context, true);
        }
    }

    class MonitoringSystemCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Class<?> c = Class.forName(tm.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);

                        ITelephony telephonyService = (ITelephony) m.invoke(tm);
                        telephonyService.endCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // home键
                        isDownHome = true;
                        CustomLog.v("------------------" + isDownHome);
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        // 长按home键
                    }
                }
            }
        }
    }
}
