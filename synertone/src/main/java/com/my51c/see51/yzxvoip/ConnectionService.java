package com.my51c.see51.yzxvoip;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.yzx.api.CallType;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
//import com.yzx.im_demo.VideoConverseActivity;


/**
 * 后台服务/连接控制器
 */
public class ConnectionService extends Service implements ConnectionListener, CallStateListener {

    private final String playFileName = "/sdcard/playout_t.pcm";
    private FileOutputStream fosPlay = null;
    private byte[] testData;
    private int dataLen = 0;
    private int readIndex = 0;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UIDfineAction.ACTION_DIAL)) {
                int type = intent.getIntExtra("type", 1);
                String uid = intent.getStringExtra(UIDfineAction.CALL_UID);
                String phone = intent.getStringExtra(UIDfineAction.CALL_PHONE);
                if (phone == null || phone.length() == 0) {
                    //整合版本账号即手机号
                    phone = uid;
                }
                CustomLog.i(DfineAction.TAG_TCP, "》》》》》》》》》》》  ... " + type);
                //type:
                //		1：免费
                // 		2: 直拨
                //		3:视频点对点
                switch (type) {
                    case 1:
                        //方法有进行重载，可以传入透传字段也可以不传入。
                        //UCSCall.dial(ConnectionService.this,CallType.VOIP, uid);
                        UCSCall.dial(ConnectionService.this, CallType.VOIP, uid, "");
                        break;
                    case 2:
                        UCSCall.dial(ConnectionService.this, CallType.DIRECT, phone, "");
                        break;
                    case 3:
                        UCSCall.dial(ConnectionService.this, CallType.VIDEO, uid, "");
                        break;
                }
            } else if (intent.getAction().equals(UCSService.ACTION_INIT_SUCCESS)) {
                CustomLog.i(DfineAction.TAG_TCP, "启动成功  ... ");
                //关闭系统日志打印
                UCSService.openSdkLog(ConnectionService.this, true);//输出sdk日志到sd卡中 yunzhixun/log/YZX_SDK_日期.txt
            }
        }
    };
    private int second = 0;
    private int minute = 0;
    private int hour = 0;
    private Timer timer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();


        if (android.os.Build.VERSION.SDK_INT >= 14) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());*/
        }
        //添加连接监听器
        UCSService.addConnectionListener(ConnectionService.this);
        //添加电话监听器
        UCSCall.addCallStateListener(ConnectionService.this);
        //关闭未接听时视频预览
        //UCSCall.setCameraPreViewStatu(ConnectionService.this,false);
        // 控制是否开启外部音频传输 true:开启；false：不开启
        UCSCall.setExtAudioTransEnable(this, false);
        //初始化SDK
        Log.e("ConnctServices---->", "in   ---------onCreate--in-- initAction: ");
        UCSService.initAction(this);
        Log.e("ConnctServices---->", "in   end-initAction---------start_init: ");
        UCSService.init(this, true);
        Log.e("ConnctServices---->", "in   end-init---------: ");
        //UCSService.init(this,false);//关掉系统日志打印, modified by zhj 20160121
        //初始化action动作
        UIDfineAction.initAction(ConnectionService.this.getPackageName());

        IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_DIAL);
        ift.addAction(UCSService.ACTION_INIT_SUCCESS);
        registerReceiver(br, ift);
    }

    @Override
    public void onDestroy() {
        CustomLog.i(DfineAction.TAG_TCP, "onDestroy ... ");
        unregisterReceiver(br);
        //断开云联接
        UCSService.uninit();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    //连接失败或断线回调
    @Override
    public void onConnectionFailed(UcsReason reason) {
        CustomLog.i(DfineAction.TAG_TCP, "CONNECTION_FAILED:" + reason.getReason());
        if (reason.getMsg().length() > 0) {
            CustomLog.i(DfineAction.TAG_TCP, "CONNECTION_FAILED:" + reason.getMsg());
        }
        if (reason.getReason() == 300207) {    // 踢线
            sendBroadcast(new Intent(UIDfineAction.ACTION_NET_ERROR_KICKOUT));
        }
    }

    //连接成功回调
    @Override
    public void onConnectionSuccessful() {
        CustomLog.v("connetion successful .... ");
        if (RestTools.mPhoneNum != null && RestTools.mPhoneNum.length() > 0) {
            LoginConfig.saveCurrentClientId(ConnectionService.this, RestTools.mPhoneNum);
            LoginConfig.saveCurrentUserId(ConnectionService.this, RestTools.mPhoneNum);
        }
    }

    /**
     * 通话走时
     *
     * @author: xiaozhenhua
     * @data:2014-6-24 上午10:19:56
     */
    public void startCallTimer() {
        stopCallTimer();
        if (timer == null) {
            timer = new Timer();
        }
        second = 0; //秒
        minute = 0; //分
        hour = 0;   //时
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                StringBuffer timer = new StringBuffer();
                second++;
                if (second >= 60) {
                    minute++;
                    second = 0;
                }
                if (minute >= 60) {
                    hour++;
                    minute = 0;
                }
                if (hour != 0) {
                    if (hour < 10) {
                        timer.append(0);
                    }
                    timer.append(hour);
                    timer.append(":");
                }
                if (minute < 10) {
                    timer.append(0);
                }
                timer.append(minute);
                timer.append(":");
                if (second < 10) {
                    timer.append(0);
                }
                timer.append(second);
//				CustomLog.i(DfineAction.TAG_TCP,"timer:"+timer.toString());
                sendBroadcast(new Intent(UIDfineAction.ACTION_CALL_TIME).putExtra("callduration", hour * 3600 + minute * 60 + second).putExtra("timer", timer.toString()));
            }
        }, 0, 1000);
    }

    public void stopCallTimer() {
        if (timer != null) {
            CustomLog.i(DfineAction.TAG_TCP, "cancel() timer");
            timer.cancel();
            timer = null;
        }
    }


    //对方正在响铃回调
    @Override
    public void onAlerting(String arg0) {
        CustomLog.i(DfineAction.TAG_TCP, "onAlerting CURRENT_ID:" + arg0);
        sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_RINGING_180));
    }

    //对方接通回调
    @Override
    public void onAnswer(String arg0) {
        CustomLog.i(DfineAction.TAG_TCP, "onAnswer CURRENT_ID:" + arg0);
        sendBroadcast(new Intent(UIDfineAction.ACTION_ANSWER));
        startCallTimer();
    }

    //拨打失败回调，请打印出错误码reason.getReason()，官网查询错误码含义
    @Override
    public void onDialFailed(String arg0, UcsReason reason) {
        CustomLog.i("onDialFailed CURRENT_ID:" + arg0 + "SERVICE:" + reason.getReason() + "   MSG:" + reason.getMsg());
        voipSwitch(reason);
    }

    private void voipSwitch(UcsReason reason) {
        stopCallTimer();
        switch (reason.getReason()) {
            case 300006:    // calledNumner is null
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_NUMBER_IS_EMPTY));
                break;
            case 300210:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ERROR));
                break;
            case 300211:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NOT_ENOUGH_BALANCE));
                break;
            case 300212:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_BUSY));
                break;
            case 300213:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_REFUSAL));
                break;
            case 300214: // 被叫号码不在线
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NUMBER_OFFLINE));
                break;
            case 300244: // 被叫号码不存在
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_CALLID_NOT_EXIST));
                break;
            case 300215: //被叫号码错误
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NUMBER_WRONG));
                break;
            case 300216:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ACCOUNT_FROZEN));
                break;
            case 300217:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_REJECT_ACCOUNT_FROZEN));
                break;
            case 300218:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ACCOUNT_EXPIRED));
                break;
            case 300219:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_CALLYOURSELF));
                break;
            case 300220:
            case 300224:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NETWORK_TIMEOUT));
                break;
            case 300221:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NOT_ANSWER));
                break;
            case 300222:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_TRYING_183));
                break;
            case 300223:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_SESSION_EXPIRATION));
                break;
            case 300225:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_MYSELF));
                break;
            case 300226:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER));
                break;
            case 300267:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_WHILE_2G));
                break;
            case 300248:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_MYSELF_REFUSAL));
                break;
            case 300249:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VIDEO_DOES_NOT_SUPPORT));
                break;
            case 300318:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.NOT_NETWORK));
                break;
            case 300227:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_RTP_TIMEOUT));
                break;
            case 300228:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER_REASON));
                break;
            case 300250:
                sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_FAIL_BLACKLIST));
                break;
            default:
                if (reason.getReason() >= 10000 && reason.getReason() <= 20000) {//透传错误码
                    CustomLog.i(DfineAction.TAG_TCP, "KC_REASON:" + reason.getReason());
                    sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_HANGUP));
                } else if (reason.getReason() >= 300233 && reason.getReason() <= 300243) {
                    sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
                }
                break;
        }
    }

    @Override
    public void onHangUp(String callId, UcsReason reason) {
        CustomLog.i("onHangUp CURRENT_ID:" + callId + "SERVICE:" + reason.getReason());
        AudioConverseActivity.IncomingCallId = callId;
        //	VideoConverseActivity.IncomingCallId = callId;
        UCSCall.stopCallRinging(ConnectionService.this);
        voipSwitch(reason);
    }

    /**
     * 接收新消息
     * nickName 显示主叫昵称
     * userdata 暂时没用到
     */
    @Override
    public void onIncomingCall(String callId, String callType, String callerNumber, String nickName, String userdata) {
        CustomLog.v("收到新的来电 callType=" + callType + "phone :" + callerNumber);
        CustomLog.v("透传信息..." + userdata);
        Intent intent = new Intent();
        if (callType.equals("0")) {
            intent.setClass(ConnectionService.this, AudioConverseActivity.class);
            intent.putExtra("callType", 1);
        } else if (callType.equals("2")) {
            //会议
        } else {
            //视频电话
            CustomLog.v("视频电话.......");
//			intent.setClass(ConnectionService.this,VideoConverseActivity.class);
            intent.setClass(ConnectionService.this, AudioConverseActivity.class);//音视频统一入口界面
            intent.putExtra("callType", 2);
        }
        CustomLog.v("InComing phone :" + callerNumber + "InComing nickName :" + nickName);
        intent.putExtra("phoneNumber", callerNumber).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("inCall", true);
        intent.putExtra("nickName", nickName);
        intent.putExtra("callId", callId);
        startActivity(intent);

    }

    /*	*
         * @param 0 无法获取网络状态
         * @param 1 网络状态极好
         * @param 2 网络状态良好
         * @param 3 网络状态一般
         * @param 4 网络状态极差*/
    @Override
    public void onNetWorkState(int reason, String message) {
        sendBroadcast(new Intent(UIDfineAction.ACTION_NETWORK_STATE).putExtra("state", reason)
                .putExtra("videomsg", message));
    }

    @Override
    public void onDTMF(int dtmfCode) {
        CustomLog.d(DfineAction.TAG_TCP, "DTMF:" + dtmfCode);
    }

    @Override
    public void onCameraCapture(String filePaht) {
        CustomLog.d(DfineAction.TAG_TCP, "CAMERACAPTURE:" + filePaht);
    }

    /**
     * 对方视频模式回调
     */
    @Override
    public void onRemoteCameraMode(UCSCameraType type) {
        if (type == UCSCameraType.REMOTECAMERA) {
            sendBroadcast(new Intent(UIDfineAction.ACTION_NETWORK_STATE).putExtra("state", 10));
        }
    }

    @Override
    public void singlePass(int arg0) {

    }

    @Override
    public void onDecryptStream(byte[] arg0, byte[] arg1, int arg2, int[] arg3) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onEncryptStream(byte[] arg0, byte[] arg1, int arg2, int[] arg3) {

        // TODO Auto-generated method stub

    }

    /**
     * @param sample_rate      采样率
     * @param bytes_per_sample 采样深度,每个样点的字节数
     * @param num_of_channels  通道数
     * @return void    返回类型
     * @Description 使用指定参数值对外部音频播放设备进行初始化(拨打时回调)
     * @date 2016-3-30 下午4:08:36
     * @author xhb
     */
    @Override
    public void initPlayout(int sample_rate, int bytes_per_sample, int num_of_channels) {
        CustomLog.i("initPlayout sample_rate = " + sample_rate +
                " bytes_per_sample = " + bytes_per_sample +
                " num_of_channels = " + num_of_channels);
        try {
            fosPlay = new FileOutputStream(playFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sample_rate      采样率
     * @param bytes_per_sample 采样深度,每个样点的字节数
     * @param num_of_channels  通道数
     * @return void    返回类型
     * @Description 使用指定参数值对外部音频设备的采集进行初始化(接通时回调)
     * @date 2016-3-30 下午4:10:04
     * @author xhb
     */
    @Override
    public void initRecording(int sample_rate, int bytes_per_sample, int num_of_channels) {
        CustomLog.i("initRecording sample_rate = " + sample_rate +
                " bytes_per_sample = " + bytes_per_sample +
                " num_of_channels = " + num_of_channels);
        testData = convertStream2byteArrry("cuiniao.pcm");
        dataLen = testData.length;
        //Log.i(TAG, "testData len = " + dataLen);
        readIndex = 0;
    }

    /**
     * @param inData 需填充为从外部音频设备采集的码流
     * @param inSize 数据长度
     * @return int    返回类型
     * @Description 从外部音频设备读取指定数据长度的采集PCM码流
     * @return 0：成功；-1：失败
     * @date 2016-3-30 下午4:15:23
     * @author xhb
     */
    @Override
    public int readRecordingData(byte[] inData, int inSize) {
//		CustomLog.i("readRecordingData inSize = " + inSize);
        if (dataLen > inSize) {
            if (readIndex + inSize > dataLen) {
                readIndex = 0;
            }
            System.arraycopy(testData, readIndex, inData, 0, inSize);
            readIndex += inSize;
            return 0;
        }
        return -1;
    }

    /**
     * @param outData 解码后给外部音频设备播放的码流
     * @param outSize 数据长度
     * @return int    返回类型
     * @Description 将解码后指定数据长度的PCM码流给外部设备进行播放
     * @return 0：成功；-1：失败
     * @date 2016-3-30 下午4:11:50
     * @author xhb
     */
    @Override
    public int writePlayoutData(byte[] outData, int outSize) {
//		CustomLog.i("writePlayoutData outSize = " + outSize);
        if (fosPlay != null) {
            // recording to files
            try {
                fosPlay.write(outData, 0, outSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
        return -1;
    }

    public byte[] convertStream2byteArrry(String filepath) {
        InputStream inStream = null;
        try {
            inStream = this.getResources().getAssets().open(filepath);
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        int length = 0;
        try {
            length = inStream.available();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] buffer = new byte[length];
        try {
            inStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.i(TAG, "buffer.length = " + buffer.length);
        return buffer;
    }
}
