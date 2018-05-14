package com.my51c.see51.yzxvoip;
/**
 * Created by snt1179 on 2016/11/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;
import com.yzxtcp.tools.CustomLog;

import java.util.HashMap;
import java.util.List;

public class YzxLogin2Activity extends BaseActivity {

    public static final HashMap<Integer, String> loginErrorCode = new HashMap<Integer, String>();
    private static final String TAG = "YzxLogin2Activity";
    private static int VERI_CODE_SPAN = 60;// 获取短信验证码间隔时间，单位为秒

    static {
        loginErrorCode.put(2001, "验证码过期");
        loginErrorCode.put(2002, "验证码不正确");
        loginErrorCode.put(2003, "手机号码为空");
        loginErrorCode.put(2004, "验证码为空");
        loginErrorCode.put(2005, "昵称为空");
        loginErrorCode.put(2006, "查无数据");
        loginErrorCode.put(2007, "发送短信验证码失败");
        loginErrorCode.put(2008, "创建用户失败");
        loginErrorCode.put(2009, "参数不正确");
        loginErrorCode.put(2011, "手机号码未注册");
        loginErrorCode.put(2012, "手机号码不正确");
        loginErrorCode.put(2013, "加入群组失败");
        loginErrorCode.put(2099, "其他错误");
        loginErrorCode.put(-1, "其他错误");
    }

    Handler veriHandler = null;
    Runnable VerRunnable = null;
    private EditText edt_account;
    private EditText edt_veri_code;
    private Button btn_login;
    private Button btn_obtain_vericode;
    private ImageView address_setting;
    private Handler mHandler;
    private String mAccount;
    //是否主动退出
    private boolean isBack;
    private UserInfo user;
    private int countbackTime;// 短信验证码间隔倒数时间，单位为秒

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "YzxLogin2Activity onNewIntent()");
        isBack = false;
        initView(this);
    }

    /* @Override
     public void onConfigurationChanged(Configuration newConfig) {
         // TODO Auto-generated method stub
         super.onConfigurationChanged(newConfig);
         try {
             super.onConfigurationChanged(newConfig);
             setContentView(R.layout.activity_login_v2_yzx);
         } catch (Exception ex) {
         }
     }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "YzxLogin2Activity onCreate()");
        setContentView(R.layout.activity_login_v2_yzx);
        RestTools.initUrl(YzxLogin2Activity.this);//初始化AS地址
        initView(this);
        initData(this);
        isBack = false;
    }

    private void initData(YzxLogin2Activity imLoginActivity) {

        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (NetWorkTools.isNetWorkConnect(YzxLogin2Activity.this) == false) {
                    MyToast.showLoginToast(YzxLogin2Activity.this,
                            "网络连接不可用，请重试");
                    return;
                }
                mAccount = edt_account.getText().toString();

                if (StringUtils.isEmpty(mAccount)) {
                    MyToast.showLoginToast(YzxLogin2Activity.this, "手机号不能为空");
                    return;
                }
                if (mAccount.length() != 11 || !StringUtils.isPhoneNumber(mAccount)) {
                    MyToast.showLoginToast(YzxLogin2Activity.this, "请输入正确的手机号码");
                    return;
                }

                //检查验证码是否存在
                Log.e("YzxActivity2lOgin----->", "检查验证码是否存在========");
                String veriCode = null;
                List<UserInfo> userList = UserInfoDBManager.getInstance().getAll();
                for (int i = 0; i < userList.size(); i++) {
                    if (mAccount.equals(userList.get(i).getAccount())) {
                        veriCode = userList.get(i).getVeriCode();
                        break;
                    }
                }

                if (veriCode == null || veriCode.length() == 0) {
                    veriCode = edt_veri_code.getText().toString();
                }
                if (veriCode == null || veriCode.length() == 0) {
                    MyToast.showLoginToast(YzxLogin2Activity.this, "请先输入验证码");
                    return;
                }

                mHandler.sendEmptyMessage(RestTools.LOGIN_REST_STARTGETTOKEN);
                //RestTools.getToken(mAccount, mHandler);
                RestTools.getToken(YzxLogin2Activity.this, mAccount, veriCode, mHandler);
                btn_login.setClickable(false);
                getSharedPreferences("YZX_DEMO", 1).edit()
                        .putString("YZX_ACCOUNT_INDEX", mAccount).commit();
                final String veriCodeTmp = veriCode;
                enableVeriCodeBtn(true);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //RestTools.getToken(IMLoginV2Activity.this,mAccount, mHandler);
                        //使用验证码登录认证
                        RestTools.getToken(YzxLogin2Activity.this, mAccount, veriCodeTmp, mHandler);
                    }
                }, 100);
            }
        });

        //获取验证码
        btn_obtain_vericode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                enableVeriCodeBtn(false);
                startCountback();

                //隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_account.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_veri_code.getWindowToken(), 0);

                if (NetWorkTools.isNetWorkConnect(YzxLogin2Activity.this) == false) {
                    MyToast.showLoginToast(YzxLogin2Activity.this,
                            "网络连接不可用，请重试");
                    enableVeriCodeBtn(true);
                    return;
                }
                mAccount = edt_account.getText().toString();

                if (StringUtils.isEmpty(mAccount)) {
                    MyToast.showLoginToast(YzxLogin2Activity.this, "手机号不能为空");
                    enableVeriCodeBtn(true);
                    return;
                }
                if (mAccount.length() != 11 || !StringUtils.isPhoneNumber(mAccount)) {
                    MyToast.showLoginToast(YzxLogin2Activity.this, "请输入正确的手机号码");
                    enableVeriCodeBtn(true);
                    return;
                }

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        RestTools.getVeriCode(YzxLogin2Activity.this, mAccount, mHandler);
                    }
                }, 100);
            }

        });

        mHandler = new LoginHandler(this) {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case RestTools.LOGIN_STATUS_FAIL:
//				case RestTools.LOGIN_REST_UNREGISTER:
                        this.closeProgressDialog();
                        btn_login.setClickable(true);
                        break;
                    case RestTools.LOGIN_REST_STARTGETTOKEN:
                        this.showProgressDialog();
                        this.startLoginTimer();
                        break;
                    case RestTools.LOGIN_REST_UNREGISTER:
                        this.closeProgressDialog();
                        stopLoginTimer();
                        btn_login.setClickable(true);
                        Intent intent = new Intent(YzxLogin2Activity.this, IMRegisterV2Activity.class);//如果没有注册昵称，跳到注册昵称界面。
                        intent.putExtra("phoneNum", mAccount);
                        startActivity(intent);
                        break;
                    case RestTools.LOGIN_REST_FINISH:
                        YzxLogin2Activity.this.finish();
                        edt_veri_code.setText("");
                        break;
                    case RestTools.LOGIN_STATUS_TIMEOUT:
                        btn_login.setClickable(true);
                        break;
                    case RestTools.OBTAIN_VERI_CODE://判断获取验证码
                    {
                        if (msg.arg1 == 0) {
                            String strTime = msg.getData().getString("expireTime", "0");
                            int expireTime = Integer.valueOf(strTime);
                            MyToast.showLoginToast(YzxLogin2Activity.this, "验证码已成功发送，请注意查收");
                            CustomLog.v("获取验证码成功 expireTime = " + expireTime);
                            if (expireTime <= 0) {
                                enableVeriCodeBtn(true);
                                MyToast.showLoginToast(YzxLogin2Activity.this, "验证码已过期, expireTime：" + expireTime);
                            }

                        } else {
                            enableVeriCodeBtn(true);
                            MyToast.showLoginToast(YzxLogin2Activity.this, "获取验证码失败，请重试");
                            CustomLog.v("获取验证码失败, 错误码：" + msg.arg1);
                        }
                        break;
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initView(Context context) {
        //清空登录用户
        UserInfo oldUser = UserInfoDBManager.getInstance().getCurrentLoginUser();
        if (oldUser != null) {
            oldUser.setLoginStatus(0);
            UserInfoDBManager.getInstance().update(oldUser);
        }
        user = UserInfoDBManager.getInstance().getDefaultLoginUser(false);
        edt_account = (EditText) findViewById(R.id.edt_account);
        edt_veri_code = (EditText) findViewById(R.id.edt_veri_code);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_obtain_vericode = (Button) findViewById(R.id.btn_obtain_veri_code);
        edt_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 11) {
                    btn_obtain_vericode.setClickable(false);
                    btn_obtain_vericode.setTextColor(getResources().getColor(R.color.login_btn_normal));
                    btn_obtain_vericode.setBackgroundResource(R.drawable.btnstyle_normal);
                } else {
                    btn_obtain_vericode.setClickable(true);
                    btn_obtain_vericode.setTextColor(getResources().getColor(R.color.btn_press_color));
                    btn_obtain_vericode.setBackgroundResource(R.drawable.buttonstyle);
                }
            }
        });
        btn_obtain_vericode.setClickable(true);
        address_setting = (ImageView) findViewById(R.id.address_setting);
        btn_login.setClickable(true);
        if (user != null) {
            mAccount = user.getAccount();
        } else {
            mAccount = "";
        }
        if (mAccount != null) {
            edt_account.setText(mAccount);
            edt_account.setSelection(mAccount.length());
        }
        address_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //        Intent intent = new Intent(YzxLogin2Activity.this, AddressConfigActivity.class);
                //启动配置页面
                //     startActivity(intent);
            }
        });
    }

    /**
     * @param bEnable 是否使能
     * @return void    返回类型
     * @Description 设置获取验证码按钮使能状态
     * @date 2015年12月27日 下午3:53:27
     * @author zhj
     */
    void enableVeriCodeBtn(boolean bEnable) {
        if (bEnable) {
            if (veriHandler != null) {
                veriHandler.removeCallbacks(VerRunnable);
            }
            btn_obtain_vericode.setText(YzxLogin2Activity.this.getString(R.string.obtain_veri_code));
            btn_obtain_vericode.setClickable(true);
            btn_obtain_vericode.setBackgroundResource(R.drawable.buttonstyle);
            btn_obtain_vericode.setTextColor(getResources().getColor(R.color.btn_press_color));
        } else {
            btn_obtain_vericode.setClickable(false);
            btn_obtain_vericode.setBackgroundResource(R.drawable.buttonstyle_disable);
            btn_obtain_vericode.setTextColor(getResources().getColor(R.color.text_gray));
        }
    }

    /**
     * @return void    返回类型
     * @Description 启动验证码失效倒计时
     * @date 2015年12月17日 上午10:05:21
     * @author zhj
     */
    private void startCountback() {
        enableVeriCodeBtn(false);
        countbackTime = VERI_CODE_SPAN;
        veriHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    btn_obtain_vericode.setText(countbackTime + "s");
                } else {
                    this.removeCallbacks(VerRunnable); //结束循环
                    enableVeriCodeBtn(true);
                }
            }
        };

        VerRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
                veriHandler.postDelayed(this, 1000);
                veriHandler.sendEmptyMessage(--countbackTime);

            }
        };

        veriHandler.postDelayed(VerRunnable, 1000); //启动循环

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }


}
