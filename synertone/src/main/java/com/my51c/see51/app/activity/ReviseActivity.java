package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.LoginActivity;
import com.my51c.see51.app.PersonalActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.my51c.see51.listener.NoDoubleClickListener;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.synertone.netAssistant.R.drawable.error;

/*个人中心密码修改*/
public class ReviseActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "ReviseActivity";
    private Button mReviseSavePasswork;
    private EditText mReviseOldPasswork, mReviseNewPasswork, mReviseRetSetPass;
    private RequestQueue mRequestQueue;
    private TextView textView1, querenmima, xinmima_tv, yuanshimima_tv;
    private String mCode, mPwdToken;
    //获取文本的方法
    private String oldPassWorkStr, newPassWorkStr, retSetPassStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revise_password_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        if (AppData.accountModel != null) {
            mPwdToken = AppData.accountModel.getSessionToken();
        }
        initView();
        initEvent();
    }

    private void initEvent() {
        mReviseSavePasswork.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
               System.out.print("------------------------onNoDoubleClick--------------------------");
                getTextPassStr();
                if (!"".equals(oldPassWorkStr)) {
                    if (newPassWorkStr.length() >= 6 && newPassWorkStr.length() <= 8 && retSetPassStr.length() >= 6 && retSetPassStr.length() <= 8
                            && oldPassWorkStr.length() >= 6 && oldPassWorkStr.length() <= 8) {
                        if (newPassWorkStr.equals(oldPassWorkStr)) {
                            Toast.makeText(mContext, "原始密码与新密码不能相同", 0).show();
                        } else if (newPassWorkStr.equals(retSetPassStr)) {
                            postPassWork(oldPassWorkStr, newPassWorkStr, retSetPassStr);
                        } else {
                            Toast.makeText(mContext, "新密码与确认密码不一致,请重新输入", 0).show();

                            return;
                        }
                    } else {
                        Toast.makeText(mContext, "原始密码与新密码的长度为6至8位,且为数字或字母!", 0).show();
                        return;
                    }
                } else {
                    Toast.makeText(mContext, "原始密码不能为空", 0).show();
                    return;
                }
            }
        });
    }

    private void initView() {
        textView1 = (TextView) findViewById(R.id.revise_tv);
        textView1.setTypeface(AppData.fontXiti);
        querenmima = (TextView) findViewById(R.id.querenmima);
        querenmima.setTypeface(AppData.fontXiti);
        xinmima_tv = (TextView) findViewById(R.id.xinmima_tv);
        xinmima_tv.setTypeface(AppData.fontXiti);
        yuanshimima_tv = (TextView) findViewById(R.id.yuanshimima_tv);
        yuanshimima_tv.setTypeface(AppData.fontXiti);

        mReviseSavePasswork = (Button) findViewById(R.id.revise_password_btnsave);
        mReviseOldPasswork = (EditText) findViewById(R.id.revise_oldpassword);
        mReviseNewPasswork = (EditText) findViewById(R.id.revise_newpassword);
        mReviseRetSetPass = (EditText) findViewById(R.id.revise_resetpassword);
        if (AppData.accountModel != null) {
            mPwdToken = AppData.accountModel.getSessionToken();
        }
    }

    private void getTextPassStr() {
        oldPassWorkStr = mReviseOldPasswork.getText().toString();
        newPassWorkStr = mReviseNewPasswork.getText().toString();
        retSetPassStr = mReviseRetSetPass.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.revise_password_cancel:
                Intent mIntent = new Intent(ReviseActivity.this, PersonalActivity.class);
                startActivity(mIntent);
                finish();
                break;
        }
    }

    //post密码修改
    private void postPassWork(final String oldPassWorkStr, final String newPassWorkStr, final String retSetPassStr) {
        progresshow = true;
        showDia();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPasswd", XTHttpJSON.string2MD5(oldPassWorkStr));
            jsonObject.put("newPasswd1", XTHttpJSON.string2MD5(newPassWorkStr));
            jsonObject.put("newPasswd2", XTHttpJSON.string2MD5(retSetPassStr));
            jsonObject.put("sessionToken", mPwdToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_MODIFYPASS_ADDRESS, jsonObject,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        try {
                            //使用JSONObject给response转换编码
                            JSONObject jsonObject = new JSONObject(response.toString());
                            mCode = jsonObject.getString("code");
                            String msg = jsonObject.optString("msg");
                            if (mCode.equals("0")) {
                                Intent intent = new Intent(ReviseActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "修改成功", 0).show();
                            } else if (mCode.equals("2")) {
                                Toast.makeText(getApplicationContext(), "密码错误", 0).show();
                            } else if (mCode.equals("-1")) {
                                Toast.makeText(getApplicationContext(), "修改失败", 0).show();
                            } else if (mCode.equals("-2")) {
                                showLoginDialog();
                           }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "连接网元服务器失败", Toast.LENGTH_SHORT).show();
                Log.i(TAG, error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,0,0f));
        mRequestQueue.add(request);
    }

    private void showDia() {
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progresshow = false;
            }
        });
        pd.setMessage("正在加载数据。。。。。");
        pd.show();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    public void revisePWOnFinish(View v) {
        finish();
    }

}
