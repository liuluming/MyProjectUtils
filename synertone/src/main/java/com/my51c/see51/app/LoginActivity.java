package com.my51c.see51.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
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
import com.my51c.see51.app.activity.aggregation.TrunkActivity;
import com.my51c.see51.app.bean.AccountModel;
import com.my51c.see51.app.bean.StarCodeModel;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.SharedPreferenceManager;
import com.synertone.netAssistant.R;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import static com.my51c.see51.app.http.XTHttpUtil.POST_LOGIN_ADDRESS;

public class LoginActivity extends BaseActivity {
    private final static String TAG = "LoginActivity";
    private EditText mEdUser, mEdPassW;
    private String mStrUser, mStrPassW;
    private String mToken, mGetUser, mGetPassWord, mCode;
    private RequestQueue mRequestQueue;
    private CheckBox mCheckButton;
    private Button mLoginButton;
    private boolean mFlag = false;
    private SntAsyn mytask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        initData();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

    }

    private void initView() {
        mCheckButton = (CheckBox) findViewById(R.id.checkChoose);
        mCheckButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckButton.setBackgroundResource(R.drawable.login_checkbox_on);
                    SharedPreferenceManager.saveBoolean(LoginActivity.this, "isNotChecked", false);
                } else {
                    mCheckButton.setBackgroundResource(R.drawable.login_checkbox_off);
                    SharedPreferenceManager.saveBoolean(LoginActivity.this, "isNotChecked", true);
                }
            }
        });
        mEdUser = (EditText) findViewById(R.id.login_ed_user);
        mEdPassW = (EditText) findViewById(R.id.login_ed_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);*/
                onLogin();
            }
        });
        // 用户名改变的时候清空密码输入框
        mEdUser.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mEdPassW.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initData() {
        mGetUser = SharedPreferenceManager.getString(LoginActivity.this, "user");
        mGetPassWord = SharedPreferenceManager.getString(LoginActivity.this, "passwd");
        boolean isNotChecked = SharedPreferenceManager.getBoolean(mContext, "isNotChecked");
        if (isNotChecked) {
            mEdUser.setText("");
            mEdPassW.setText("");
            mCheckButton.setChecked(false);
        } else {
            mEdUser.setText(mGetUser);
            mEdPassW.setText(mGetPassWord);
            mCheckButton.setChecked(true);
        }
    }

    // 点击登录
    public void onLogin() {
        mStrUser = mEdUser.getText().toString();
        mStrPassW = mEdPassW.getText().toString();
        SharedPreferenceManager.saveString(LoginActivity.this, "user", mStrUser);
        SharedPreferenceManager.saveString(LoginActivity.this, "passwd", mStrPassW);
        if (!TextUtils.isEmpty(mStrUser)) {
            if (!TextUtils.isEmpty(mStrPassW)) {
                loginHttp(mStrUser, mStrPassW);
                mFlag = true;
                if (mytask == null) {
                    mytask = new SntAsyn();
                    mytask.execute("");
                }

            } else {
                Toast.makeText(getApplicationContext(), "密码不能为空，请输入密码", 0).show();
                mEdPassW.requestFocus();
            }
        } else {
            Toast.makeText(getApplicationContext(), "用户名不能为空，请输入用户名", 0).show();
            mEdUser.requestFocus();
        }
    }

    /* 登录的请求 */
    private void loginHttp(final String mStrUser, final String mStrPassW) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", mStrUser);
            jsonObject.put("passwd", XTHttpJSON.string2MD5(mStrPassW));
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    POST_LOGIN_ADDRESS, jsonObject,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Toast.makeText(LoginActivity.this, response.toString(), 0).show();
                            try {
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(response.toString());
                                mCode = jsonObject.optString("code");
                                mToken = jsonObject.optString("sessionToken", null);
                                if (mCode.equals("0")) {
                                    initAccountData(mStrUser, mStrPassW, mToken);
                                    Toast.makeText(getApplicationContext(), "登录成功", 0).show();
                                    Intent intent = new Intent(getApplicationContext(), TrunkActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (mCode.equals("1")) {
                                    Toast.makeText(getApplicationContext(), "用户不存在", 0).show();
                                    mLoginButton.setEnabled(true);
                                    mFlag = false;
                                } else if (mCode.equals("2")) {
                                    Toast.makeText(getApplicationContext(), "用户名或密码错误", 0).show();
                                    mLoginButton.setEnabled(true);
                                    mFlag = false;
                                } else {
                                    mFlag = false;
                                    Toast.makeText(getApplicationContext(), "登录失败", 0).show();
                                    mLoginButton.setEnabled(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                    Toast.makeText(LoginActivity.this, "连接网元服务器失败", Toast.LENGTH_SHORT).show();
                    mFlag = false;
                    mLoginButton.setEnabled(true);
                   /*Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);*/
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 忘记密码
    public void forget_pwd(View view) {

        // 跳转到ForgetPasswordActivity中去
        Intent intent = new Intent(LoginActivity.this,
                ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFlag = false;
    }

    private void initAccountData(String mStrUser, String mStrPassW, String mToken) {
        AccountModel accountModel = new AccountModel();
        if (mToken == null) {
            mToken = SharedPreferenceManager.getString(mContext, "mToken");
        } else {
            SharedPreferenceManager.saveString(mContext, "mToken", mToken);
        }
        accountModel.setSessionToken(mToken);
        accountModel.setUser(mStrUser);
        accountModel.setPasswd(mStrPassW);
        AppData.accountModel = accountModel;
        reSetCurrentSession(mToken);
        upDateDataBaseSession(mToken);
    }
    private void upDateDataBaseSession(String mToken) {
        List<StarCodeModel> all = DataSupport.findAll(StarCodeModel.class);
        for(StarCodeModel codeModel:all){
            if(codeModel.getSessionToken()!=null){
                codeModel.setSessionToken(mToken);
                codeModel.update(codeModel.getId());
            }
        }
    }
    private void reSetCurrentSession(String mToken) {
        String savedStar = SharedPreferenceManager.getString(mContext,"currentStar");
        if(savedStar!=null){
            StarCodeModel currentStar = GsonUtils.fromJson(savedStar, StarCodeModel.class);
            currentStar.setSessionToken(mToken);
            SharedPreferenceManager.saveString(mContext,"currentStar",GsonUtils.toJson(currentStar));
        }
    }

    public class SntAsyn extends AsyncTask<String, String, String> {

        int i = 1;

        @Override
        protected String doInBackground(String... params) {
            while (mFlag) {
                if (i == 5) {
                    i = 1;

                }
                publishProgress("" + i);
                i++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i("LYJ", "values[0]" + values[0]);
            if (values[0].equals("1")) {
                mLoginButton.setBackgroundResource(R.drawable.login_btn1);
                mLoginButton.setEnabled(false);
            }
            if (values[0].equals("2")) {
                mLoginButton.setBackgroundResource(R.drawable.login_btn2);
                mLoginButton.setEnabled(false);

            }
            if (values[0].equals("3")) {
                mLoginButton.setBackgroundResource(R.drawable.login_btn3);
                mLoginButton.setEnabled(false);

            }
            if (values[0].equals("4")) {
                mLoginButton.setBackgroundResource(R.drawable.login_btn2);
                mLoginButton.setEnabled(false);

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
