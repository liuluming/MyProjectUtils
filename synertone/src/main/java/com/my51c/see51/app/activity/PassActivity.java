package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

public class PassActivity extends BaseActivity {
    public EditText mStrUser;
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_custom_dialog);
        mStrUser = (EditText) this.findViewById(R.id.txt_cusDiaInput);
    }

    // 点击登ת录
    public void Login_ok_btn(View v) {
        String mStrPassW = mStrUser.getText().toString();
        if (!TextUtils.isEmpty(mStrPassW)) {
            loginHttp(mStrPassW);
            Intent intent = new Intent(PassActivity.this,
                    SuperSetActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(PassActivity.this, "密码不能为空，请输入密码",
                    Toast.LENGTH_SHORT).show();
            mStrUser.requestFocus();
        }
    }

    /* 登录的请求 */
    private void loginHttp(String passWork) {
        progresshow = true;
        showDia();
        JSONObject list = XTHttpJSON.loginMuch(passWork);
        JSONObject object;
        try {
            object = new JSONObject(list.toString());
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.MUST_LOGIN_ADDRESS, object,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//							Log.i(TAG,
//									"接收登录return回来的data-->"
//											+ response.toString());
                            Toast.makeText(PassActivity.this,
                                    "接收登录return回来的data-->", 5000).show();
                            loginData(response.toString());
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//							Log.i(TAG, error.toString());
                    Toast.makeText(PassActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
            mRequestQueue.add(request);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // 解析登录 return 回来的数据
    private void loginData(String string) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(string).equals("0")) {
            Toast.makeText(PassActivity.this, "成功", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (XTHttpJSON.getJSONString(string).equals("-1")) {
            Toast.makeText(PassActivity.this, "失败", Toast.LENGTH_SHORT)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // LoginActivity.this.onDestroy();
        pd.dismiss();

    }

}
