package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

/*web认证设置*/
public class WebAutSetActivity extends BaseActivity implements
        OnClickListener {
    private static final String TAG = "WebAutSetActivity";
    //private ToggleButton mWebaut_OnOff;
    private Spinner mWebaut_oldtime, mWebaut_Server;
    private RequestQueue mRequestQueue;
    private String enableStr;
    private int timeoutInt, sernumInt;
    private Button mWebRefsave, mWebAppSavebtn;
    private boolean tagWebAut;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webautset_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        webAutQuery();
    }

    // 查找控件
    private void initView() {
        TextView web_set = (TextView) findViewById(R.id.web_set);
        web_set.setTypeface(AppData.fontXiti);
        TextView web_renzheng = (TextView) findViewById(R.id.web_renzheng);
        web_renzheng.setTypeface(AppData.fontXiti);
        TextView web_fuwuqi = (TextView) findViewById(R.id.web_fuwuqi);
        web_fuwuqi.setTypeface(AppData.fontXiti);

        //mWebaut_OnOff = (ToggleButton) findViewById(R.id.webaut_onoff);
        mWebaut_oldtime = (Spinner) findViewById(R.id.webaut_oldtime);
        mWebaut_oldtime.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.webaut_old_time)));

        mWebaut_Server = (Spinner) findViewById(R.id.webaut_server);
        mWebaut_Server.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.webaut_servicer)));

        mWebRefsave = (Button) findViewById(R.id.web_ref_btnsave);
        mWebAppSavebtn = (Button) findViewById(R.id.web_ref_appsave);
        //mWebaut_oldtime.setEnabled(false);
        //mWebaut_Server.setEnabled(false);
        //mWebaut_OnOff.setOnCheckedChangeListener(this);
        mWebRefsave.setOnClickListener(this);
        mWebAppSavebtn.setOnClickListener(this);
    }

    // 查看WEB设置
    private void webAutQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_ROUTESET_WEB_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        /*Toast.makeText(WebAutSetActivity.this,
								response.toString(), 0).show();*/
                        loginDataQuery(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WebAutSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                mWebaut_oldtime.setEnabled(false);
                mWebaut_Server.setEnabled(false);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    // get 查看数据
    private void loginDataQuery(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(WebAutSetActivity.this, "连接成功", 0).show();
            try {
                enableStr = response.getString("enable");
                timeoutInt = response.getInt("timeout");
                sernumInt = response.getInt("sernum");
                WebAutSetActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (enableStr.equals("0")) {
                            tagWebAut = true;
                            //mWebaut_OnOff.setChecked(true);
                            mWebaut_oldtime.setSelection(timeoutInt - 1);
                            mWebaut_Server.setSelection(sernumInt - 1);
                        } else if (enableStr.equals("1")) {
                            //mWebaut_OnOff.setChecked(false);
                            //mWebaut_oldtime.setEnabled(false);
                            //mWebaut_Server.setEnabled(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(WebAutSetActivity.this, "连接成功", 0).show();
        }
    }

//	// 按钮的改变
//	@Override
//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		if (isChecked) {
//			mWebaut_oldtime.setEnabled(true);
//			mWebaut_Server.setEnabled(true);
//			//enableStr = "0";
//			tagWebAut = true;
//		} else {
//			mWebaut_oldtime.setEnabled(false);
//			mWebaut_Server.setEnabled(false);
//			//enableStr = "1";
//			tagWebAut = false;
//		}
//	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_ref_btnsave:
                if (tagWebAut) {
                    enableStr = "0";
                    webLoginPost();
                } else {
                    enableStr = "-1";
                    webLoginPost();
                }
                break;
            case R.id.web_ref_appsave:
                if (tagWebAut) {
                    enableStr = "1";
                    webLoginPost();
                } else {
                    enableStr = "-2";
                    webLoginPost();
                }
                break;
        }
    }

    // 保存提交上去的josn 数据
    private void webLoginPost() {
        if (mWebaut_Server.getSelectedItem().toString().equals("服务器1")) {
            sernumInt = 1;
        } else if (mWebaut_Server.getSelectedItem().toString().equals("服务器2")) {
            sernumInt = 2;
        } else if (mWebaut_Server.getSelectedItem().toString().equals("服务器3")) {
            sernumInt = 3;
        }

        if (mWebaut_oldtime.getSelectedItem().toString().equals("0.25h")) {
            timeoutInt = 1;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("0.5h")) {
            timeoutInt = 2;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("1.5h")) {
            timeoutInt = 3;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("2h")) {
            timeoutInt = 4;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("5h")) {
            timeoutInt = 5;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("24h")) {
            timeoutInt = 6;
        } else if (mWebaut_oldtime.getSelectedItem().toString().equals("48h")) {
            timeoutInt = 7;
        }

        progresshow = true;
        showDia();
        JSONObject postJsonObject = XTHttpJSON.postWebAut(enableStr,
                timeoutInt, sernumInt, this);
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_ROUTESET_WEB_SET, postJsonObject,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收到数据-->" + response.toString());
					/*	Toast.makeText(WebAutSetActivity.this,
								response.toString(), 0).show();*/
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WebAutSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
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

    // 点击退出页面
    public void webAutOnFinish(View v) {
        finish();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.cancel();
        super.onDestroy();
    }

}
