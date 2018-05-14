package com.my51c.see51.yzxvoip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.CallPhonePopupWindow;
import com.my51c.see51.yzxvoip.model.ContactFrament;
import com.my51c.see51.yzxvoip.model.LoginOutState;
import com.my51c.see51.yzxvoip.model.LoginState;
import com.my51c.see51.yzxvoip.model.State;
import com.my51c.see51.yzxvoip.model.TeleFragment;
import com.synertone.netAssistant.R;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.listener.MessageListener;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;

/*
@SuppressLint("NewApi")
public class IMChatActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imchat);
	}
}*/
@SuppressLint("NewApi")
public class IMChatActivity extends BaseActivity implements
        OnClickListener,
        MessageListener, ILoginListener, OnDbChangeListener {
    public static final String TAG = "IMChatActivity";
    private static final int NOTIFAY_VOICE_VIBRATOR = 406;
    private static boolean isDirect = false;
    private static IMChatActivity mInstance;
    private NoScrollViewPager mViewPager;
    private ImageView iv_direct;
    private ArrayList<Long> clickDirect = new ArrayList<Long>();
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private String mLocalUser;
    private ImageButton mChatroom;

    //private List<IObserverListener> observes = new ArrayList<IObserverListener>();
    private String[] actionTitle = new String[]{"拨号", "通讯录", "设置"};
    private IMManager imManager;
    private MainBottomBar bottomMsg, bottomContact, bottomSetting;
    private List<MainBottomBar> mTabIndicator = new ArrayList<MainBottomBar>();
    // private ActionBar mActionBar;
    private YzxTopBar mActionBar;
    private UserSetting userSetting;
    private ContactFrament mContactFrament;
    private TextView conversations_cout;
    private ImageView conversations_cout_bg;
    //是否需要连接
    private boolean isNeedConnect;

    //private ConversationFragment conversationFragment;
    //private SettingFragment settingFragment;
    private TeleFragment teleFragment;
    private boolean isTeleFragment = false;//当前是否为通话页面
    //网络链接成功之后，状态调用action进行相应处理
    private State state;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
//				//踢线踢线
//				if(MainApplication.getInstance().getResumeActivity() instanceof IMMessageBgActivity){
//					//标记IMMessageBgActivity含有dialog
//					((IMMessageBgActivity)MainApplication.getInstance().getResumeActivity()).setHaveDialog(true);
//				}
                    //启动登出界面
/*				Intent intent = new Intent();
				Uri uri = Uri.parse("yzx://"+getApplicationInfo().packageName).buildUpon().appendPath("login_out").build();
				CustomLog.i("收到踢线消息启动登出Activty uri = "+uri.toString());
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(intent);*/
                    //Intent intent = new Intent(IMChatActivity.this,IMLoginOutActivity.class);
                    //startActivity(intent);
                    break;
                case 102:
                    MyToast.showLoginToast(IMChatActivity.this, "token超时,请重新登陆");
                    break;
                case NOTIFAY_VOICE_VIBRATOR:
                    //振铃和声音
                    Log.i(TAG, "收到消息，添加振铃");
                    //NotificationTools.showNotification(userSetting,(ChatMessage)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    public static IMChatActivity getmInstance() {
        return mInstance;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "IMChatActivity onNewIntent() taskId = " + getTaskId());
    }

    protected void onDestroy() {
        Log.i(TAG, "IMChatActivity onDestroy()");
        mHandler.removeCallbacks(null);
        UserSettingsDbManager.getInstance().removeObserver(this);
        //UCSManager.removeISdkStatusListener(this);
        //setNetErrorMsg();
        super.onDestroy();
    }

    /*	@Override
        public void onConfigurationChanged(Configuration newConfig) {
            // TODO Auto-generated method stub
            super.onConfigurationChanged(newConfig);
            try {
                super.onConfigurationChanged(newConfig);
                setContentView(R.layout.activity_imchat);
            } catch (Exception ex) {
            }
        }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomLog.v("IMChatActivity onCreate() taskId = " + getTaskId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imchat);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        Log.i(TAG, "宽 = " + outMetrics.widthPixels + "，高 = " + outMetrics.heightPixels + "，屏幕密度 = " + outMetrics.density);

        UserSettingsDbManager.getInstance().addObserver(this);

        mInstance = this;
        mLocalUser = getIntent().getStringExtra("mLocalUser");
        if (StringUtils.isEmpty(mLocalUser)) {
            CustomLog.d("IMChatActivity 被回收了");
            UserInfo user = UserInfoDBManager.getInstance().getCurrentLoginUser();
            if (user != null) {
                mLocalUser = user.getAccount();
            } else {
                mLocalUser = "";
            }
        }

        //imManager = IMManager.getInstance(getApplicationContext());
        initView();


        initDatas();

        mViewPager.setAdapter(mAdapter);
        mViewPager.setNoScroll(true);
        //mViewPager.setOnPageChangeListener();
    /*		isNeedConnect = getIntent().getBooleanExtra("connectTcp", false);
		if(isNeedConnect || !UCSManager.isConnect()){
			if(!UCSManager.isConnect()){
				CustomLog.d("------------------服务被Kill-------------------");
			}
			//链接平台
			connect();
		}else{
			if(!getIntent().hasExtra("connectTcp")){
				CustomLog.d("-------------界面被回收connectTcp == null------------");
				//链接平台
				connect();
			}else{
				//连接成功才进这里
				state = new LoginState();
			}
		}
		imManager.setISdkStatusListener(this);
		imManager.setSendMsgListener(this);*/
        //初始化通讯录
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ContactTools.getSourceDateList().size() < 1) {
                    ContactTools.initContacts(IMChatActivity.this);
                }
            }
        }).start();
    }

    public boolean isConnect() {
        return isNeedConnect;
    }

    private void initView() {
        bottomMsg = (MainBottomBar) findViewById(R.id.id_bottombar_msg);
        bottomContact = (MainBottomBar) findViewById(R.id.id_bottombar_contact);
        bottomSetting = (MainBottomBar) findViewById(R.id.id_bottombar_setting);
        bottomMsg.setOnClickListener(this);
        bottomContact.setOnClickListener(this);
        bottomSetting.setOnClickListener(this);

        bottomMsg.setBottomViewNormalAlpha(0.0f);

        mViewPager = (NoScrollViewPager) findViewById(R.id.id_viewpager);
        mActionBar = (YzxTopBar) findViewById(R.id.actionBar_chat);
        mActionBar.setBackVisibility(View.GONE);
        mActionBar.setTitleVisibility(View.GONE);
        mActionBar.setConverVisibility(View.VISIBLE);
        mActionBar.setMessageBackgroudResource(R.drawable.message_view_press_bg);
        mActionBar.setMessageColor(getResources().getColor(R.color.black));
        mActionBar.setMessageOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //通话记录
                mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
                mViewPager.setCurrentItem(0, false);
                mActionBar.setTitleVisibility(View.GONE);
                mActionBar.setConverVisibility(View.VISIBLE);
                mActionBar.setTeleBackgroudResource(R.drawable.tele_view_bg);
                mActionBar.setTeleColor(getResources().getColor(R.color.white));
                mActionBar.setMessageBackgroudResource(R.drawable.message_view_press_bg);
                mActionBar.setMessageColor(getResources().getColor(R.color.black));
                isTeleFragment = true;
            }
        });
        mActionBar.setTeleOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通讯录
                mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
                mViewPager.setCurrentItem(1, false);
                mActionBar.setTitleVisibility(View.GONE);
                mActionBar.setConverVisibility(View.VISIBLE);
                mActionBar.setTeleBackgroudResource(R.drawable.tele_view_press_bg);
                mActionBar.setTeleColor(getResources().getColor(R.color.black));
                mActionBar.setMessageBackgroudResource(R.drawable.message_view_bg);
                mActionBar.setMessageColor(getResources().getColor(R.color.white));
                isTeleFragment = false;
            }
        });
//		mActionBar.setTitle(actionTitle[0]);
		/*conversations_cout = (TextView) findViewById(R.id.conversations_cout);
		conversations_cout_bg = (ImageView) findViewById(R.id.conversations_cout_bg);*/
    }

    private void initDatas() {
        userSetting = UserSettingsDbManager.getInstance().getById(UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount());
        CustomLog.v("之前  mTabs length = " + mTabs.size());
        // 构造会话Fragment
        //conversationFragment = new ConversationFragment();
        // 构造通讯录Fragment
        // 构造设置Fragment
		/*settingFragment = new SettingFragment();
		mTabs.add(conversationFragment);*/
        // 构造电话Fragment
        teleFragment = new TeleFragment();
        mTabs.add(teleFragment);
        // 构造通讯录Fragment
        mContactFrament = new ContactFrament();
        Bundle args = new Bundle();
        args.putString("mLocalUser", mLocalUser);
        mContactFrament.setArguments(args);
        mTabs.add(mContactFrament);

		/*settingFragment.setArguments(args);
		mTabs.add(settingFragment);*/

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                CustomLog.v("instantiateItem " + mTabs.get(position));
                return super.instantiateItem(container, position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                CustomLog.v("destroyItem " + mTabs.get(position));
                super.destroyItem(container, position, object);
            }
        };
        CustomLog.v("之后mTabs length = " + mTabs.size());
        initTabIndicator();
        //conversationFragment.setRefreshUnReadMessageListener(this);
        // RestTools.queryGroupInfo(LoginUserBean.getLocalUserId(getApplicationContext()),null);
    }

    private void initTabIndicator() {

        mTabIndicator.add(bottomMsg);
        mTabIndicator.add(bottomContact);
        mTabIndicator.add(bottomSetting);

        bottomMsg.setOnClickListener(this);
        bottomContact.setOnClickListener(this);
        bottomSetting.setOnClickListener(this);

    }

    protected void onResume() {
        super.onResume();
        AppData.getNotificationManager().cancelAll();
        AppData.getInstance().setResumeActivity(this);

        //checkUnReadMsg();
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.id_bottombar_msg:
                CallPhonePopupWindow callPhotoPopWin = new CallPhonePopupWindow(this);
                callPhotoPopWin.showAtLocation(findViewById(R.id.id_bottombar_msg), Gravity.CENTER, 0, 0);
                mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
                mViewPager.setCurrentItem(0, false);
                if (!isTeleFragment) {
                    // 通话记录
                    mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
                    mViewPager.setCurrentItem(0, false);
                    // mActionBar.setTitle(actionTitle[0]);
                    mActionBar.setTitleVisibility(View.GONE);
                    mActionBar.setConverVisibility(View.VISIBLE);
                    mActionBar.setMessageBackgroudResource(R.drawable.message_view_press_bg);
                    mActionBar.setMessageColor(getResources().getColor(R.color.black));
                    mActionBar.setTeleBackgroudResource(R.drawable.tele_view_bg);
                    mActionBar.setTeleColor(getResources().getColor(R.color.white));
                    isTeleFragment = true;
                } else {
                    // 通讯录
                    mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
                    mViewPager.setCurrentItem(1, false);
                    mActionBar.setTitleVisibility(View.GONE);
                    mActionBar.setConverVisibility(View.VISIBLE);
                    mActionBar.setTeleBackgroudResource(R.drawable.tele_view_press_bg);
                    mActionBar.setTeleColor(getResources().getColor(R.color.black));
                    mActionBar.setMessageBackgroudResource(R.drawable.message_view_bg);
                    mActionBar.setMessageColor(getResources().getColor(R.color.white));
                    isTeleFragment = false;
                }

                break;
            case R.id.id_bottombar_contact:
                mTabIndicator.get(1).setBottomViewNormalAlpha(0.0f);
                mViewPager.setCurrentItem(1, false);
                mActionBar.setConverVisibility(View.GONE);
                mActionBar.setTitleVisibility(View.VISIBLE);
                mActionBar.setTitle(actionTitle[1]);
                break;
            case R.id.id_bottombar_setting:
                mTabIndicator.get(2).setBottomViewNormalAlpha(0.0f);
                //mViewPager.setCurrentItem(3, false);
                mActionBar.setConverVisibility(View.GONE);
                mActionBar.setTitleVisibility(View.VISIBLE);
                mActionBar.setTitle(actionTitle[2]);
                break;
        }

    }

/*	@Override
	public void onPageScrollStateChanged(int status) {
		if (status == 2) {
			mContactFrament.dismissSideBarDialog();
			mContactFrament.resumeSortList();
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

		if (positionOffset > 0) {
			MainBottomBar left = mTabIndicator.get(position);
			MainBottomBar right = mTabIndicator.get(position + 1);

			right.setBottomViewNormalAlpha(1 - positionOffset);
			left.setBottomViewNormalAlpha(positionOffset);
		}
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setTitle(actionTitle[position]);
	}*/

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setBottomViewNormalAlpha(1.0f);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            //moveTaskToBack(false);
            //返回桌面
			/*Intent home = new Intent(Intent.ACTION_MAIN);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);*/
			/*Intent intent = new Intent(IMChatActivity.this, HomeActivity.class);
			startActivity(intent);*/
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*	@Override
        public void onSdkStatus(UcsReason reason) {
            Log.i(TAG, "onSdkStatus status = "+reason.getReason());
            CustomLog.e("onSdkStatus reason: " + reason.getReason() + "    "
                    + reason.getMsg());
            int status = 0;
            if (reason.getReason() == UcsErrorCode.NET_ERROR_KICKOUT) {// 服务器强制下线通知
                CustomLog.i("收到服务器强制下线通知");
                mHandler.sendEmptyMessage(101);
                //UCSManager.disconnect();
            } else if (reason.getReason() == UcsErrorCode.NET_ERROR_TOKENERROR) {
                CustomLog.i("token超时,请重新登录");
            } else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTOK){
                CustomLog.i("TCPCONNECTOK errorcode = " +reason.getReason());
                //((ConversationFragment)mTabs.get(0)).handSdkStatus(400);
                status = 400;
            } else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTFAIL){
                CustomLog.i("TCPCONNECTFAIL errorcode = " +reason.getReason());
                //((ConversationFragment)mTabs.get(0)).handSdkStatus(408);
                status = 408;
            } else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTING){
                CustomLog.i("TCPCONNECTING errorcode = " +reason.getReason());
                //((ConversationFragment)mTabs.get(0)).handSdkStatus(406);
                status = 406;
            } else if(reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT){
                CustomLog.i("NETUNCONNECT errorcode = " +reason.getReason());
                //((ConversationFragment)mTabs.get(0)).handSdkStatus(402);
                status = 402;
            } else if(reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETCONNECTED){
                CustomLog.i("NETCONNECTED errorcode = " +reason.getReason());
                //((ConversationFragment)mTabs.get(0)).handSdkStatus(406);
                if(UCSManager.isConnect()){
                    status = 400;
                }else{
                    //进行相应处理
                    state.action(this, userSetting.getToken());
                    status = 406;
                }
            }
            //notifyObserver(status);
        }*/
    public void onSendMsgRespone(ChatMessage msg) {
        Log.i(TAG, "发送消息： " + msg.getContent() + "，相应码：" + msg.getSendStatus());
    }

    @Override
    public void onReceiveMessage(List msgs) {
        List<ChatMessage> messages = (List<ChatMessage>) msgs;
        Log.i(TAG, "onReceiveMessage msg size : " + msgs.size());
        if (mHandler.hasMessages(NOTIFAY_VOICE_VIBRATOR)) {
            mHandler.removeMessages(NOTIFAY_VOICE_VIBRATOR);
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = messages.get(messages.size() - 1);
        msg.what = NOTIFAY_VOICE_VIBRATOR;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDownloadAttachedProgress(String msgId, String filePaht,
                                           int sizeProgrss, int currentProgress) {

    }

	/*@Override
	public void onRefreshUnReadMessage() {
		checkUnReadMsg();
	}*/

    /**
     * 检查未读消息
     */
/*	private void checkUnReadMsg() {
		int unreadcount = IMManager.getInstance(this).getUnreadCountAll();
		Log.i(TAG, "未读消息："+unreadcount+"条");
		if (unreadcount != 0) {
			if (unreadcount > 99) {
				conversations_cout.setText("99+");
			} else {
				conversations_cout.setText(String.valueOf(unreadcount));
			}
			if (unreadcount < 10) {
				conversations_cout_bg.setVisibility(View.VISIBLE);
				conversations_cout_bg
						.setBackgroundResource(R.drawable.unreadsmall);
			} else {
				conversations_cout_bg.setVisibility(View.VISIBLE);
				conversations_cout_bg
						.setBackgroundResource(R.drawable.unreadbig);
			}
		} else {
			conversations_cout_bg.setVisibility(View.GONE);
			conversations_cout.setText("");
		}
	}*/
    public void onLogin(UcsReason reason) {
        //CustomLog.v("onLogin status errorCode = "+reason.getReason());
        Log.i(TAG, "onLogin status errorCode = " + reason.getReason());
        int sdkStatus = 0;
        if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
            CustomLog.i("connect sdk successfully -----  enjoy --------");
            isNeedConnect = false;
//			((ConversationFragment)mTabs.get(0)).handSdkStatus(400);
            state = new LoginState();
            sdkStatus = 400;
        } else if (reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT) {
//			((ConversationFragment)mTabs.get(0)).handSdkStatus(402);
            sdkStatus = 402;
        } else {
            //((ConversationFragment)mTabs.get(0)).handSdkStatus(408);
            sdkStatus = 408;
        }
        //notifyObserver(sdkStatus);
    }

    public void setState(State state) {
        this.state = state;
    }

    public void connect() {
        state = new LoginOutState();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                    RestTools.queryGroupInfo(IMChatActivity.this, UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount(), null);
                    String token = userSetting.getToken();
                    //只要是进入了connect，表示未登录状态
                    UCSManager.connect(token, IMChatActivity.this);
                } catch (InterruptedException e) {
                }
            }
        }.start();
    }

    /*	public void putObserver(IObserverListener mObserver){
            synchronized (observes) {
                if(!observes.contains(mObserver)){
                    boolean successful = observes.add(mObserver);
                    if(successful){
                        Log.d(TAG, "add mObserver successful hashCode = "+mObserver.hashCode());
                    }else{
                        Log.d(TAG, "add mObserver fail hashCode = "+mObserver.hashCode());
                    }
                }
            }
        }

        public void removeObserver(IObserverListener mObserver){
            synchronized (observes) {
                if(observes.contains(mObserver)){
                    boolean successful = observes.remove(mObserver);
                    if(successful){
                        Log.d(TAG, "remove mObserver successful hashCode = "+mObserver.hashCode());
                    }else{
                        Log.d(TAG, "remove mObserver fail hashCode = "+mObserver.hashCode());
                    }
                }
            }
        }

        public void notifyObserver(int sdkStatus){
            if(observes == null || observes.size() == 0){
                return;
            }
            synchronized (observes) {
                for(IObserverListener observer : observes){
                    observer.notify(sdkStatus);
                }
            }
        }*/
    @Override
    public void onChange(String notifyId) {
        Log.i(TAG, "reQuery userSettings onChange id = " + notifyId);
        userSetting = UserSettingsDbManager.getInstance().getById(UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount());
    }
}
