package com.my51c.see51.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.my51c.see51.app.activity.SuperSetActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

public class GaoJiAccountActivity extends BaseActivity {
    private EditText gaojiaccount_ed_user, gaojiaccount_ed_password;//用户名和密码
    private Button btn_resiger;//点击开始体验按钮
    private RequestQueue mRequestQueue;
    private String mStrUser, mStrPwd;
    private boolean progresshow;
    // 登录加载数据的 ProgressDialog
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaojiaccount);
        initView();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void initView() {
        gaojiaccount_ed_user = (EditText) findViewById(R.id.gaojiaccount_ed_user);
        gaojiaccount_ed_password = (EditText) findViewById(R.id.gaojiaccount_ed_password);
        // 用户名改变的时候清空密码输入框
        gaojiaccount_ed_user.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                gaojiaccount_ed_password.setText(null);
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

    public void onGaojiaccount(View view) {
        mStrUser = gaojiaccount_ed_user.getText().toString();
        mStrPwd = gaojiaccount_ed_password.getText().toString();
        if (!TextUtils.isEmpty(mStrUser)) {
            if (!TextUtils.isEmpty(mStrPwd)) {
                gaojiaccountHttp(mStrPwd);

                //登录操作


            } else {
                Toast.makeText(GaoJiAccountActivity.this, "密码不能为空，请输入密码",
                        Toast.LENGTH_SHORT).show();
                gaojiaccount_ed_password.requestFocus();
            }
        } else {
            Toast.makeText(GaoJiAccountActivity.this, "用户名不能为空，请输入用户名",
                    Toast.LENGTH_SHORT).show();
            gaojiaccount_ed_user.requestFocus();
        }
    }

    private void gaojiaccountHttp(String mStrPwd) {
        progresshow = true;
        showDia();
        JSONObject list = XTHttpJSON.gaojiAccount(mStrPwd);
        JSONObject object;
        try {
            object = new JSONObject(list.toString());
            JsonObjectRequest request = new JsonObjectRequest(Method.POST, XTHttpUtil.POST_GAOJIACCOUNT_ADDRESS, object,
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //gaojiaccountData(response.toString());
//							try {
//								if(response.getString("code").equals("0")){
                            Intent intent = new Intent(GaoJiAccountActivity.this, SuperSetActivity.class);
                            startActivity(intent);
                            finish();
//								}else{
//									Toast.makeText(GaoJiAccountActivity.this, "密码错误",
//											Toast.LENGTH_SHORT).show();
//								}
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
                        }


                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("ResigerActivity", error.toString());
                    Toast.makeText(GaoJiAccountActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
//							if (pd.isShowing()) {
//								pd.dismiss();
//							}

//							if(gaojiaccount_ed_password.getText().toString().equals("snt123")){
                    Intent intent = new Intent(GaoJiAccountActivity.this, SuperSetActivity.class);
                    startActivity(intent);
                    finish();
//							}

                }
            });
            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void gaojiaccountData(String string) {
        if (XTHttpJSON.getJSONString(string).equals("0")) {
            try {
                JSONObject object = new JSONObject(string);
                JSONObject data = object.getJSONObject("data");
                String msg = data.getString("msg");
                /*
				 * //跳转大HomeActivity里面 Intent intent = new
				 * Intent(LoginActivity.this, HomeActivity.class);
				 * startActivity(intent);
				 */
                Toast.makeText(getApplicationContext(), "登录成功", 0).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(string).equals("-1")) {
            Toast.makeText(GaoJiAccountActivity.this, "登录失败", Toast.LENGTH_SHORT)
                    .show();
            return;
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
        pd.setMessage("正在登录。。。。。");
        pd.show();
    }
}
