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
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends BaseActivity {

    private EditText forgetpassword_ed_user, forgetpassword_ed_yzm;//用户名和密码
    private Button btn_forgetpassword;//点击开始体验按钮
    private RequestQueue mRequestQueue;
    private String mStrUser, mStrYzm;
    private boolean progresshow;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        initView();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void initView() {
        forgetpassword_ed_user = (EditText) findViewById(R.id.forgetpassword_ed_user);
        forgetpassword_ed_yzm = (EditText) findViewById(R.id.forgetpassword_ed_yzm);
        // 用户名改变的时候清空密码输入框
        forgetpassword_ed_user.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                forgetpassword_ed_yzm.setText(null);
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

    public void onForgetpassword(View view) {
        mStrUser = forgetpassword_ed_user.getText().toString();
        mStrYzm = forgetpassword_ed_yzm.getText().toString();
        if (!TextUtils.isEmpty(mStrUser)) {
            if (!TextUtils.isEmpty(mStrYzm)) {
                forgetPasswordHttp(mStrUser, mStrYzm);

                //注册操作
                Intent intent = new Intent(ForgetPasswordActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(ForgetPasswordActivity.this, "验证码不能为空，请输入验证码",
                        Toast.LENGTH_SHORT).show();
                forgetpassword_ed_yzm.requestFocus();
            }
        } else {
            Toast.makeText(ForgetPasswordActivity.this, "用户名不能为空，请输入用户名",
                    Toast.LENGTH_SHORT).show();
            forgetpassword_ed_user.requestFocus();
        }
    }

    private void forgetPasswordHttp(String mStrUser, String mStrYzm) {
        progresshow = true;
        showDia();
        JSONObject list = XTHttpJSON.forgetpassword(mStrUser, mStrYzm);
        JSONObject object;
        try {
            object = new JSONObject(list.toString());
            JsonObjectRequest request = new JsonObjectRequest(Method.POST, XTHttpUtil.POST_FORGETPASS_ADDRESS, object,
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            forgetData(response.toString());
                        }


                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("F", error.toString());
                    Toast.makeText(ForgetPasswordActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
//							if (pd.isShowing()) {
//								pd.dismiss();
//							}
                }
            });
            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void forgetData(String string) {
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
                Toast.makeText(getApplicationContext(), "验证成功！", 0).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Toast.makeText(getApplicationContext(), "验证码码有误！", 0).show();
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
        pd.setMessage("正在验证。。。。。");
        pd.show();
    }
}
