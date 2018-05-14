package com.my51c.see51.yzxvoip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginHandler extends Handler implements ILoginListener {
    private static final String TAG = "LoginHandler------>";
    private static List<Handler> handlers = new ArrayList<Handler>();
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private Timer mTimer;
    private String mAccount;
    private String token;

    public LoginHandler(Context context) {
        mContext = context;
        handlers.add(this);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle data = null;

        switch (msg.what) {
            case RestTools.LOGIN_STATUS_INPROCESS:

                break;
            case RestTools.LOGIN_STATUS_SUCCESS:
                System.out.println("登陆成功   finish activity");
                stopLoginTimer();
                sendFinishMsg();
//			((Activity) mContext).finish();
                break;
            case RestTools.LOGIN_STATUS_FAIL:
                stopLoginTimer();
                if (msg.arg1 != 0) {
                    MyToast.showLoginToast(mContext, "登录失败：" + YzxLogin2Activity.loginErrorCode.get(msg.arg1));
                } else {
                    MyToast.showLoginToast(mContext, "登录失败");
                }
                break;
            case RestTools.LOGIN_STATUS_TIMEOUT:
                MyToast.showLoginToast(mContext, "登录超时");
                break;
            case RestTools.LOGIN_REST_TOKEN_FAIL:
                stopLoginTimer();
                MyToast.showLoginToast(mContext, "获取Token失败");
                break;

            case RestTools.LOGIN_REST_TOKEN_OK:
//			showProgressDialog();
                data = msg.getData();
                token = data.getString("imtoken");
                CustomLog.d("save Token:" + token);
                CustomLog.e("LOGIN_REST_TOKEN_OK begin connect");
                Log.e("LoginHandler-------->", "链接token。。。UCSManager.connect(token, this);。。。。。kaishi。。。。" + token);
                UCSManager.connect(token, this);//edit  hyw 20161121
                Log.e("LoginHandler-------->", "链接token。。。UCSManager.connect(token, this);。。。。。jieshu。。。。");
                /*20161118 edit by hyw*/
                //点击进入直拨打电话。
				/*Intent intent = new Intent(mContext, DirectEntryActivity.class);
				mContext.startActivity(intent);*/
				/*20161118 edit by hyw*/
                sendEmptyMessage(RestTools.LOGIN_STATUS_INPROCESS);
                break;
            case RestTools.LOGIN_REST_HAS_REGISTER:
                break;
            case RestTools.LOGIN_REST_REGISTER_FAIL:
                stopLoginTimer();
                MyToast.showLoginToast(mContext, "注册失败");
                break;
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("登录中...");
        mProgressDialog.show();
    }

    public void startLoginTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("登录超时");
                mProgressDialog.dismiss();
                mTimer = null;
                sendEmptyMessage(RestTools.LOGIN_STATUS_TIMEOUT);
            }
        }, 40000);
    }

    public void stopLoginTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void sendFinishMsg() {
        for (Handler h : handlers) {
            h.sendEmptyMessage(RestTools.LOGIN_REST_FINISH);
        }
    }

    @Override
    public void onLogin(UcsReason reason) {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
        if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
            UserInfo user = new UserInfo(RestTools.mPhoneNum, RestTools.mNickName, 1, 1);
            Log.e(TAG, "-----------------onLogin()进直拨--------");
            //保存到数据库
            UserInfoDBManager.getInstance().insert(user, token);
            //	RestTools.queryGroupInfo(mContext,user.getAccount(),null);
            sendEmptyMessageDelayed(RestTools.LOGIN_STATUS_SUCCESS, 1000);
            Intent intent = new Intent(mContext, IMChatActivity.class);
            intent.putExtra("mLocalUser", user.getAccount());
            //intent.putExtra("connectTcp", false);
            mContext.startActivity(intent);
			/*20161118 edit by hyw*/
            //点击进入直拨打电话。
			/*Intent intent = new Intent(mContext, IMChatActivity.class);
			mContext.startActivity(intent);*/
            //移出登录监听回调
            UCSManager.removeLoginListener(this);
        } else {
            sendEmptyMessage(RestTools.LOGIN_STATUS_FAIL);
        }
    }
}
