package com.my51c.see51.yzxvoip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.synertone.netAssistant.R;
import com.yzx.api.UCSCall;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.yzx.api.UCSCall.setSpeakerphone;

/**
 * @author xhb
 * @Title AudioConverseActivity
 * @Description 语音电话界面
 * @Company yunzhixun
 * @date 2016-1-2 下午5:54:30
 */
public class AudioConverseActivity extends ConverseActivity implements OnClickListener {
    private static final int CHAT_MODE_AUDIO = 1;//音频聊天
    private static final int CHAT_MODE_VIDEO = 2;//视频聊天
    private static final int AUDIO_CONVERSE_CLOSE = 1; // 关闭界面
    private static final int ACTION_NETWORK_STATE = 2;    // 更新网络状态
    private static final String TAG = "AudioConverseActivity";
    public static String IncomingCallId;    // 来电时的callId，作用是防止有些时间挂断电话的信令来了，但是通话界面还没有开启，这个时候在来来电信令，电话就不挂断。
    /**
     * @Description 界面上控件的监听事件
     * @author xhb
     * @date 2016-1-3 上午11:36:19
     * @return void    返回类型
     * @see OnClickListener#onClick(View)
     */
    public static boolean isContact = false;//点击了通讯录为true，默认为false
    public String calledUid;
    public String calledPhone;
    public String userName;
    public String phoneNumber;
    public String nickName;
    public boolean inCall = false; // true:来电；false:去电
    public int currVolume = 0;
    private int mCallType = 1; //1语音聊天  2视频聊天
    private TextView converse_client;
    private TextView converse_information;
    private LinearLayout ll_network_call_time;
    private LinearLayout ll_mute_pad_speaker;
    private ImageView converse_network;
    private TextView converse_call_time;
    private TextView dial_close;
    private EditText dial_number;
    private ImageButton converse_call_mute;
    private ImageButton converse_call_dial;
    private ImageButton converse_call_speaker;
    private ImageButton converse_call_hangup;
    private ImageButton converse_call_answer;
    private ImageButton converse_call_endcall;
    private ImageButton converse_call_cantact;//added by hyw 20161125 通讯录
    private ImageButton dial_endcall;
    private ImageButton ib_digit0;
    private ImageButton ib_digit1;
    private ImageButton ib_digit2;
    private ImageButton ib_digit3;
    private ImageButton ib_digit4;
    private ImageButton ib_digit5;
    private ImageButton ib_digit6;
    private ImageButton ib_digit7;
    private ImageButton ib_digit8;
    private ImageButton ib_digit9;
    private ImageButton ib_digit_star;
    private ImageButton ib_digit_husa;
    private LinearLayout key_layout;
    private TextView key_converse_name;
    private LinearLayout converse_main;
    private boolean open_headset = false;
    private int calltype = 1;
    private boolean speakerPhoneState = false;
    private String timer;    // 通话时间
    private String callStartTime = null; //通话开始时间
    private String callId = "";
    private boolean incallAnswer = false;
    private int sound; // 触摸提示音的状态，0：关，1：开
    private AudioManager mAudioManager;
    private RequestQueue mQueue; // volley的请求队列
    private String adress;
    private String telAdress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUDIO_CONVERSE_CLOSE:    // 关闭界面
                    setSpeakerphone(AudioConverseActivity.this, false);
                    AudioConverseActivity.this.finish();
                    break;
                case 0x123:
                    telAdress = (String) msg.obj;
                    Log.e("adress==", telAdress);
                    break;
                case ACTION_NETWORK_STATE:    // 更新网络状态
                    switch (msg.arg1) {
                        case 1:
                            converse_network.setBackgroundResource(R.drawable.audio_signal3);
                            break;
                        case 2:
                            converse_network.setBackgroundResource(R.drawable.audio_signal2);
                            break;
                        case 3:
                            converse_network.setBackgroundResource(R.drawable.audio_signal1);
                            break;
                        case 4:
                            converse_network.setBackgroundResource(R.drawable.audio_signal0);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UIDfineAction.ACTION_DIAL_STATE)) {
                int state = intent.getIntExtra("state", 0);
                CustomLog.v(DfineAction.TAG_TCP, "AUDIO_CALL_STATE:" + state);
                if (UIDfineAction.dialState.keySet().contains(state)) {
                    CustomLog.v(state + UIDfineAction.dialState.get(state));
                    //获得通话状态信息
                    if (state == 300226) {//对方挂断电话
                        ll_network_call_time.setVisibility(View.GONE);
                        converse_information.setVisibility(View.VISIBLE);
                    }
                    converse_information.setText(UIDfineAction.dialState.get(state));
                }
                if ((calltype == 1 && state == UCSCall.CALL_VOIP_RINGING_180)
                        || (calltype == 5 && state == UCSCall.CALL_VOIP_TRYING_183)) {
                    setSpeakerphone(AudioConverseActivity.this, true);
                    UCSCall.stopCallRinging(AudioConverseActivity.this);
                    UCSCall.stopRinging(AudioConverseActivity.this);
                }
                if (state == UCSCall.NOT_NETWORK) {
                    converse_information.setText("当前处于无网络状态");
                    UCSCall.stopRinging(AudioConverseActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                }
                if (state == UCSCall.HUNGUP_REFUSAL || state == UCSCall.HUNGUP_MYSELF
                        || state == UCSCall.HUNGUP_OTHER || state == UCSCall.HUNGUP_MYSELF_REFUSAL
                        || state == UCSCall.HUNGUP_RTP_TIMEOUT || state == UCSCall.HUNGUP_OTHER_REASON) {
                    CustomLog.v("收到挂断信息");
                    UCSCall.stopRinging(AudioConverseActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                } else {
                    if ((state >= 300210 && state <= 300250) &&
                            (state != 300221 && state != 300222 && state != 300247)
                            || state == UCSCall.HUNGUP_NOT_ANSWER || state == UCSCall.CALL_NUMBER_IS_EMPTY) {
                        handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                    }
                }
            } else if (intent.getAction().equals(UIDfineAction.ACTION_ANSWER)) {
                AudioConverseActivity.this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                incallAnswer = true;
                ll_network_call_time.setVisibility(View.VISIBLE);
                //接通后通知服务开启计时器
                sendBroadcast(new Intent(UIDfineAction.ACTION_START_TIME));
                converse_information.setVisibility(View.GONE);
                //接通后关闭回铃音
                converse_call_answer.setVisibility(View.GONE);
                converse_call_hangup.setVisibility(View.GONE);
                converse_call_endcall.setVisibility(View.VISIBLE);
                ll_mute_pad_speaker.setVisibility(View.VISIBLE);
                //接通电话后按钮变为可用
                converse_call_mute.setClickable(true);
                converse_call_cantact.setClickable(true);//added by hyw 20161125
                converse_call_speaker.setClickable(true);
                converse_call_dial.setClickable(true);

                //记录通话开始时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm");
                callStartTime = dateFormat.format(new Date());

                UCSCall.stopRinging(AudioConverseActivity.this);
                UCSCall.stopCallRinging(AudioConverseActivity.this);//edit by hyw 20161128
                setSpeakerphone(AudioConverseActivity.this, false);
                CustomLog.v("Speakerphone state:" + mAudioManager.isSpeakerphoneOn());
                converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                open_headset = true;
            } else if (intent.getAction().equals(UIDfineAction.ACTION_DIAL_HANGUP)) {
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
            } else if (intent.getAction().equals(UIDfineAction.ACTION_CALL_TIME)) {
                timer = intent.getStringExtra("timer");
                if (converse_call_time != null) {
                    converse_call_time.setText(timer);
                }
            } else if (intent.getAction().equals(UIDfineAction.ACTION_NETWORK_STATE)) {
                int networkState = intent.getIntExtra("state", 0);
                Message msg = handler.obtainMessage();
                msg.what = ACTION_NETWORK_STATE;
                msg.arg1 = networkState;
                handler.sendMessageDelayed(msg, 1000);
            } else if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                //发现个别手机会接通电话前收到这个广播并把扬声器打开了，所以open_headset作为判断必须接通后再接收耳机插拔广播才有效
                if (intent.getIntExtra("state", 0) == 1 && open_headset) {
                    CustomLog.e(DfineAction.TAG_TCP, "Speaker false");
                    converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                    speakerPhoneState = UCSCall.isSpeakerphoneOn(AudioConverseActivity.this);
                    setSpeakerphone(AudioConverseActivity.this, false);
                } else if (intent.getIntExtra("state", 0) == 0 && open_headset) {//headset disconnected
                    CustomLog.e("headset unplug");
                    if (speakerPhoneState) {
                        CustomLog.e("Speaker true");
                        converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
                        setSpeakerphone(AudioConverseActivity.this, true);
                    } else {
                        mAudioManager.setSpeakerphoneOn(false);
                        converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                    }
                }
            }
        }
    };
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_converse);
        mContext = AudioConverseActivity.this;
        mQueue = Volley.newRequestQueue(getApplicationContext());
        initview();
        initListener();
        initData();
        IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_DIAL_STATE);
        ift.addAction(UIDfineAction.ACTION_ANSWER);
        ift.addAction(UIDfineAction.ACTION_CALL_TIME);
        ift.addAction(UIDfineAction.ACTION_DIAL_HANGUP);
        ift.addAction(UIDfineAction.ACTION_NETWORK_STATE);
        ift.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(br, ift);
        try {
            //如果系统触摸音是关的就不用管，开的就把它给关掉，因为在个别手机上有可能会影响音质
            mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
            sound = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
            CustomLog.v("AudioConverseActivity sound: " + sound);
            if (sound == 1) {
                Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                mAudioManager.unloadSoundEffects();
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            CustomLog.v("SettingNotFound SOUND_EFFECTS_ENABLED ...");
        }

        if (inCall) {
            CustomLog.v("IncomingCallId = " + IncomingCallId + ",callId = " + getIntent().getStringExtra("callId"));
            if (getIntent().hasExtra("callId")) {
                callId = getIntent().getStringExtra("callId");
                if (callId.equals(IncomingCallId)) {
                    sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER));
                    converse_information.setVisibility(View.VISIBLE);
                    converse_information.setText("对方已挂机");
                    UCSCall.stopRinging(AudioConverseActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                    return;
                }
            }
        }
        //通话接通前按钮不可用
        converse_call_mute.setClickable(false);
        converse_call_speaker.setClickable(false);
        converse_call_dial.setClickable(false);
        converse_call_cantact.setClickable(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        try {
            super.onConfigurationChanged(newConfig);
            setContentView(R.layout.activity_audio_converse);
        } catch (Exception ex) {
        }
    }

    /**
     * @return void    返回类型
     * @Description 初始化界面上控件的监听器
     * @date 2016-1-3 上午10:13:27
     * @author xhb
     */
    private void initListener() {
        converse_call_mute.setOnClickListener(this);    //静音
        converse_call_cantact.setOnClickListener(this);//通讯录  added by hyw 20161125
        converse_call_speaker.setOnClickListener(this);    //扬声器
        converse_call_answer.setOnClickListener(this);    //接通
        converse_call_hangup.setOnClickListener(this);    //挂断
        converse_call_endcall.setOnClickListener(this);    //结束通话
        dial_endcall.setOnClickListener(this);            //结束通话（键盘界面中的按钮）
        converse_call_dial.setOnClickListener(this);    //打开键盘
        dial_close.setOnClickListener(this);            //关闭键盘
        ib_digit0.setOnClickListener(this);                // DTMF 按键
        ib_digit1.setOnClickListener(this);
        ib_digit2.setOnClickListener(this);
        ib_digit3.setOnClickListener(this);
        ib_digit4.setOnClickListener(this);
        ib_digit5.setOnClickListener(this);
        ib_digit6.setOnClickListener(this);
        ib_digit7.setOnClickListener(this);
        ib_digit8.setOnClickListener(this);
        ib_digit9.setOnClickListener(this);
        ib_digit_star.setOnClickListener(this);
        ib_digit_husa.setOnClickListener(this);
    }

    /**
     * @return void    返回类型
     * @Description 初始化view
     * @date 2015-12-15 下午2:58:08
     * @author xhb
     */
    private void initview() {
        converse_client = (TextView) findViewById(R.id.converse_name);
        converse_information = (TextView) findViewById(R.id.converse_information);
        converse_main = (LinearLayout) findViewById(R.id.converse_main);
        key_layout = (LinearLayout) findViewById(R.id.key_layout);
        key_converse_name = (TextView) findViewById(R.id.key_converse_name);
        dial_number = (EditText) findViewById(R.id.text_dtmf_number);
        ll_network_call_time = (LinearLayout) findViewById(R.id.ll_network_call_time);
        ll_mute_pad_speaker = (LinearLayout) findViewById(R.id.id_layout_mps);
        converse_network = (ImageView) findViewById(R.id.converse_network);
        converse_call_time = (TextView) findViewById(R.id.converse_call_time);
        converse_call_mute = (ImageButton) findViewById(R.id.converse_call_mute);
        converse_call_cantact = (ImageButton) findViewById(R.id.converse_call_cantact);//added by hyw 20161125

        converse_call_speaker = (ImageButton) findViewById(R.id.converse_call_speaker);
        converse_call_answer = (ImageButton) findViewById(R.id.audio_call_answer);
        converse_call_hangup = (ImageButton) findViewById(R.id.audio_call_hangup);
        converse_call_endcall = (ImageButton) findViewById(R.id.audio_call_endcall);
        dial_endcall = (ImageButton) findViewById(R.id.dial_endcall);
        converse_call_dial = (ImageButton) findViewById(R.id.converse_call_dial);
        dial_close = (TextView) findViewById(R.id.dial_close);

        ib_digit0 = (ImageButton) findViewById(R.id.digit0);
        ib_digit1 = (ImageButton) findViewById(R.id.digit1);
        ib_digit2 = (ImageButton) findViewById(R.id.digit2);
        ib_digit3 = (ImageButton) findViewById(R.id.digit3);
        ib_digit4 = (ImageButton) findViewById(R.id.digit4);
        ib_digit5 = (ImageButton) findViewById(R.id.digit5);
        ib_digit6 = (ImageButton) findViewById(R.id.digit6);
        ib_digit7 = (ImageButton) findViewById(R.id.digit7);
        ib_digit8 = (ImageButton) findViewById(R.id.digit8);
        ib_digit9 = (ImageButton) findViewById(R.id.digit9);
        ib_digit_star = (ImageButton) findViewById(R.id.digit_star);
        ib_digit_husa = (ImageButton) findViewById(R.id.digit_husa);
    }

    /**
     * @return void    返回类型
     * @Description 初始化数据
     * @date 2015-12-15 下午2:57:53
     * @author xhb
     */
    private void initData() {
        if (getIntent().hasExtra("call_type")) {//直拨 免费电话
            calltype = getIntent().getIntExtra("call_type", 1);
        }
        if (getIntent().hasExtra("callType")) {//音频或视频
            mCallType = getIntent().getIntExtra("callType", 1);
        }
        //判断是否是来电信息，默认是去电，（来电信息是由ConnectionService中的onIncomingCall回调中发送广播，开启通话界面，inCall为true）
        if (getIntent().hasExtra("inCall")) {
            inCall = getIntent().getBooleanExtra("inCall", false);
            //判断网络类型，2G时提示一下
            int netstate = NetWorkTools.getCurrentNetWorkType(this);
            if (netstate == NetWorkTools.NETWORK_EDGE)
                Toast.makeText(this, "网络状态差", Toast.LENGTH_SHORT).show();
        }
        //获得要拨打的号码，智能拨打和免费通话：phoneNumber代表ClientID，直拨和回拨代表ClientID绑定的手机号码
        if (getIntent().hasExtra("userId")) {
            calledUid = getIntent().getStringExtra("userId");
        }
        if (getIntent().hasExtra("call_phone")) {
            calledPhone = getIntent().getStringExtra("call_phone");
        }
        if (getIntent().hasExtra("userName")) {
            userName = getIntent().getStringExtra("userName");
        }
        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            try {
                getLocate(phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getIntent().hasExtra("nickName")) {
            nickName = getIntent().getStringExtra("nickName");
        }

		/*if(phoneNumber != null && phoneNumber.length() > 0) {
            // 先显示通讯录中的昵称
			userName = ContactTools.getConTitle(phoneNumber);
			// 在从IM会话中获取通话记录昵称
			if(TextUtils.isEmpty(userName)) {
				@SuppressWarnings("unchecked")
				List<ConversationInfo> conversationLists = IMManager.getInstance(this).getConversationList(CategoryId.PERSONAL);
				if(conversationLists != null && conversationLists.size() > 0) {
					for (ConversationInfo conversationInfo : conversationLists) {
						if(phoneNumber.equals(conversationInfo.getTargetId())) {
							CustomLog.i("conversation number ...");
							userName = conversationInfo.getConversationTitle();
						}
					}
				}
			}
		}*/
        if (userName != null && !"".equals(userName)) {
            converse_client.setText(userName);
        } else if (calledUid != null && !"".equals(calledUid)) {
            converse_client.setText(calledUid);
        } else if (calledPhone != null && !"".equals(calledPhone)) {
            converse_client.setText(calledPhone);
        }
        if (inCall) {
            //来电
            if (userName != null && !"".equals(userName)) {
                converse_client.setText(userName);
            } else if (nickName != null && !"".equals(nickName)) {
                converse_client.setText(nickName);
            } else if (phoneNumber != null && !"".equals(phoneNumber)) {
                converse_client.setText(phoneNumber);
            }
            converse_call_answer.setVisibility(View.VISIBLE);
            converse_call_hangup.setVisibility(View.VISIBLE);
            converse_call_endcall.setVisibility(View.GONE);
            if (CHAT_MODE_AUDIO == mCallType) {
                converse_information.setText("语音聊天");
            } else {
                converse_information.setText("视频聊天");
            }
            setSpeakerphone(AudioConverseActivity.this, true);
            startRing(AudioConverseActivity.this);
        } else {
            //去电
            converse_call_answer.setVisibility(View.GONE);
            converse_call_hangup.setVisibility(View.VISIBLE);
            converse_call_endcall.setVisibility(View.GONE);
            converse_information.setText("呼叫请求中");
            //进行拨号

            UCSCall.startCallRinging(AudioConverseActivity.this, "dialling_tone.pcm");
            Intent intent = new Intent(UIDfineAction.ACTION_DIAL);
            sendBroadcast(intent.putExtra(UIDfineAction.CALL_UID, calledUid).putExtra(UIDfineAction.CALL_PHONE, calledPhone).putExtra("type", calltype));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mCallType == CHAT_MODE_AUDIO ||
                (mCallType == CHAT_MODE_VIDEO && incallAnswer == false)) {
            if (inCall == true && incallAnswer == false) { // 呼入未接
                addCallRecord(2, inCall, userName, phoneNumber, calledPhone, mCallType, telAdress, callStartTime, timer);
            } else if (inCall == false && incallAnswer == false) { // 呼出未接
                addCallRecord(3, inCall, userName, phoneNumber, calledPhone, mCallType, telAdress, callStartTime, timer);
            } else {
                addCallRecord(1, inCall, userName, phoneNumber, calledPhone, mCallType, telAdress, callStartTime, timer);
            }
        }
        unregisterReceiver(br);
        UCSCall.stopCallRinging(AudioConverseActivity.this);
        CustomLog.i("audioConverseActivity onDestroy() ...");
        if (sound == 1) {  // 如果系统触摸提示音是开的，前面把它给关系，现在退出页面要把它还原
            Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
            mAudioManager.loadSoundEffects();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.converse_call_mute:    // 静音
                Log.e(TAG, "点击了静音按钮，现在获取到静音的状态是-------》" + UCSCall.isMicMute());
                if (UCSCall.isMicMute()) {//静音
                    Log.e(TAG, "静音==true-----关闭静音");
                    converse_call_mute.setBackgroundResource(R.drawable.converse_mute);
                    UCSCall.setMicMute(false);//关闭静音
                } else {//有声音
                    Log.e(TAG, "静音==false-----》有声音");
                    converse_call_mute.setBackgroundResource(R.drawable.converse_mute_down);
                    UCSCall.setMicMute(true);//设置为静音
                }
                //UCSCall.setMicMute(!UCSCall.isMicMute());
                break;
            case R.id.converse_call_speaker:    //扬声器
				/*Log.e(TAG,"点击了扬声器按钮，现在获取到扬声器的状态是-------》"+UCSCall.isSpeakerphoneOn(AudioConverseActivity.this));
				if(UCSCall.isSpeakerphoneOn(AudioConverseActivity.this)){//true为开启
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
					Log.e(TAG,"扬声器的状态是---true----》");
					//关闭扬声器
					UCSCall.setSpeakerphone(AudioConverseActivity.this,false);//false为关闭
				}else{//false为关闭
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
					Log.e(TAG,"扬声器的状态是---false----》");
					//打开扬声器
					UCSCall.setSpeakerphone(AudioConverseActivity.this,true);//true为打开
				}
				///UCSCall.setSpeakerphone(AudioConverseActivity.this, !UCSCall.isSpeakerphoneOn(AudioConverseActivity.this));*/
                //edit by hyw 20161128
                //现获取audiomanager的的服务
                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.ROUTE_SPEAKER);
                if (audioManager != null) {
                    currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//获取当前的声音
                    //1、判断当前扬声器的状态
                    Log.e(TAG, "点击了扬声器按钮，现在获取到扬声器的状态是-------》" + audioManager.isSpeakerphoneOn());
                    if (audioManager.isSpeakerphoneOn()) {//关闭
                        Log.e(TAG, "扬声器的状态是---true----》----关闭它，并设置当前音量，当前音量为：" + currVolume);
                        converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                        audioManager.setSpeakerphoneOn(false);
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);

                    } else if (!audioManager.isSpeakerphoneOn()) {//打开
                        Log.e(TAG, "扬声器的状态是---false----》----打开它");
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
                        audioManager.setSpeakerphoneOn(true);
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                                AudioManager.STREAM_VOICE_CALL);
                    }
                }
                break;
            case R.id.converse_call_cantact://通讯录
                //添加通讯录,,跳到通讯录对话框
                Log.e(TAG, "点击了通讯录按钮，现在获取到通讯录的状态是-------》" + isContact);
                if (isContact) {
                    Log.e(TAG, "通讯录的状态是---true---变为false-》");
                    converse_call_cantact.setBackgroundResource(R.drawable.converse_contact);
                    isContact = false;

                    //把通讯录隐藏的动作，或者在另一个页面跳转到此页面
                } else {
                    Log.e(TAG, "通讯录的状态是---false---变为true-》");
                    converse_call_cantact.setBackgroundResource(R.drawable.converse_contact_down);
                    isContact = true;
                    //添加跳到通讯录的动作
                }
                break;
            case R.id.audio_call_answer:    // 接通
                if (CHAT_MODE_AUDIO == mCallType) {
                    incallAnswer = true;
                    ll_network_call_time.setVisibility(View.VISIBLE);
                    CustomLog.v(DfineAction.TAG_TCP, "接通电话");
                    UCSCall.stopRinging(AudioConverseActivity.this);
                    UCSCall.stopCallRinging(AudioConverseActivity.this);//停止送话铃音。
                    Log.e(TAG, "已接通，停止响铃声--------------》");
                    UCSCall.answer(AudioConverseActivity.this, "");
                } else if (CHAT_MODE_VIDEO == mCallType) {
                    //startVideoActivity();
                }
                break;
            case R.id.audio_call_hangup:    //挂断
                UCSCall.stopRinging(AudioConverseActivity.this);
                UCSCall.hangUp(AudioConverseActivity.this, "");
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
                break;
            case R.id.audio_call_endcall:    // 结束通话
                CustomLog.v(DfineAction.TAG_TCP, "结束电话");
                UCSCall.stopRinging(AudioConverseActivity.this);
                UCSCall.hangUp(AudioConverseActivity.this, "");
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
                break;
            case R.id.dial_endcall:        // 结束通话（键盘界面中的按钮）
                CustomLog.v(DfineAction.TAG_TCP, "结束电话");
                UCSCall.stopRinging(AudioConverseActivity.this);
                UCSCall.hangUp(AudioConverseActivity.this, "");
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
                break;
            case R.id.converse_call_dial:    //打开键盘
                CustomLog.v(DfineAction.TAG_TCP, "打开键盘");
                key_layout.setVisibility(View.VISIBLE);
                converse_main.removeView(converse_information);
                key_layout.addView(converse_information, 1);
                converse_main.removeView(ll_network_call_time);
                key_layout.addView(ll_network_call_time, 2);
                if (inCall) {    // 来电
                    if (TextUtils.isEmpty(userName) != true) {
                        key_converse_name.setText(userName);
                    } else if ("".equals(getIntent().getStringExtra("nickName"))) {
                        key_converse_name.setText(getIntent().getStringExtra("phoneNumber"));
                    } else {
                        key_converse_name.setText(getIntent().getStringExtra("nickName"));
                    }
                } else {    // 去电
                    if (getIntent().getStringExtra("userName") != null) {
                        key_converse_name.setText(getIntent().getStringExtra("userName"));
                    } else if (getIntent().getStringExtra("userId") != null) {
                        key_converse_name.setText(getIntent().getStringExtra("userId"));
                    } else {
                        key_converse_name.setText(getIntent().getStringExtra("call_phone"));
                    }
                }
                converse_main.setVisibility(View.GONE);
                break;
            case R.id.dial_close:    // 关闭键盘
                CustomLog.v(DfineAction.TAG_TCP, "关闭键盘");
                key_layout.removeView(converse_information);
                key_layout.removeView(ll_network_call_time);
                key_layout.setVisibility(View.GONE);
                converse_main.setVisibility(View.VISIBLE);
                converse_main.addView(converse_information, 2);
                converse_main.addView(ll_network_call_time, 3);
                break;
            case R.id.digit0:    // DTMF 0
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_0, dial_number);
                break;
            case R.id.digit1:    // DTMF 1
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_1, dial_number);
                break;
            case R.id.digit2:    // DTMF 2
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_2, dial_number);
                break;
            case R.id.digit3:    // DTMF 3
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_3, dial_number);
                break;
            case R.id.digit4:    // DTMF 4
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_4, dial_number);
                break;
            case R.id.digit5:    // DTMF 5
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_5, dial_number);
                break;
            case R.id.digit6:    // DTMF 6
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_6, dial_number);
                break;
            case R.id.digit7:    // DTMF 7
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_7, dial_number);
                break;
            case R.id.digit8:    // DTMF 8
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_8, dial_number);
                break;
            case R.id.digit9:    // DTMF 9
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_9, dial_number);
                break;
            case R.id.digit_husa:    // DTMF #
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_POUND, dial_number);
                break;
            case R.id.digit_star:    // DTMF *
                UCSCall.sendDTMF(AudioConverseActivity.this, KeyEvent.KEYCODE_STAR, dial_number);
                break;
            default:
                break;
        }
    }

    /**
     * @return void    返回类型
     * @Description 开启视频聊天界面
     * @author xhb
     * @date 2016-1-20 上午11:36:19
     */
	/*private void startVideoActivity(){
		if(inCall){ //视频来电
			Intent intentVideo = new Intent(AudioConverseActivity.this, VideoConverseActivity.class);
			intentVideo.putExtra("phoneNumber", phoneNumber).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentVideo.putExtra("inCall", true);
			intentVideo.putExtra("nickName", nickName);
			intentVideo.putExtra("callId", callId);
			startActivity(intentVideo);
			this.finish();
		}
	}*/
    private void getLocate(final String phoneNumber) throws Exception {

        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET,
                UIDfineAction.URL + phoneNumber, new Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                Log.d("onResponse", arg0);
                String province = null, city = null;
                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    province = jsonObject.optJSONObject("result").optString("province");
                    city = jsonObject.optJSONObject("result").optString("city");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (province.equals(city)) {
                    province = "";
                }
                adress = province + " " + city;
                Message msg = new Message();
                msg.obj = adress.replace("市", " ");
                msg.what = 0x123;
                handler.sendMessage(msg);
                Log.e("response", province + " " + city);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.d("onErrorResponse", arg0.toString());
            }
        });


        mQueue.add(request);
    }
}
