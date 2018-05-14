package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

/*密码修改*/
public class PasswordModifiActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "PasswordModifiActivity";
    private Button mSavePasswork;
    private EditText mOldPasswork, mNewPasswork, mRetSetPass;
    private RequestQueue mRequestQueue;
    //获取文本的方法
    private String oldPassWorkStr, newPassWorkStr, retSetPassStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_modifi_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        mSavePasswork = (Button) findViewById(R.id.password_modifi_btnsave);
        mOldPasswork = (EditText) findViewById(R.id.oldpassword);
        mNewPasswork = (EditText) findViewById(R.id.newpassword);
        mRetSetPass = (EditText) findViewById(R.id.resetpassword);
        mSavePasswork.setOnClickListener(this);
    }

    private void getTextPassStr() {
        oldPassWorkStr = mOldPasswork.getText().toString();
        newPassWorkStr = mNewPasswork.getText().toString();
        retSetPassStr = mRetSetPass.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_modifi_btnsave:
                getTextPassStr();
                if (oldPassWorkStr != null) {
                    if (newPassWorkStr != null) {
                        postPassWork();
                    } else {
                        Toast.makeText(PasswordModifiActivity.this,
                                "新密码不能为空", 0).show();
                    }
                } else {
                    Toast.makeText(PasswordModifiActivity.this,
                            "旧密码不能为空", 0).show();
                }
                break;
        }
    }

    //post密码修改
    private void postPassWork() {
        progresshow = true;
        showDia();
        JSONObject jsonObject = XTHttpJSON.postPassWork(oldPassWorkStr, newPassWorkStr);
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_MOREADV_MODIPASS, jsonObject,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        Toast.makeText(PasswordModifiActivity.this,
                                response.toString(), 0).show();
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    protected void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(PasswordModifiActivity.this,
                    "设置成功", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(PasswordModifiActivity.this,
                    "设置失败", 0).show();
        }
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

    public void passWordOnFinish(View v) {
        finish();
    }

}
