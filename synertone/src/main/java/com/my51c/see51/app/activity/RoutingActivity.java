package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.LanSetActivity;
import com.my51c.see51.app.routbw.WanSetActivity;
import com.my51c.see51.app.routbw.WebAutSetActivity;
import com.my51c.see51.app.routbw.WifiSetActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

public class RoutingActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {
    private LinearLayout mRoutset_Lanset, mRoutset_wanset, mRoutset_wifiset, mWebaut_set;
    private RequestQueue mRequestQueue;
    private String enableStr;
    private ToggleButton mRouting_set_onoff;
    private Intent mIntent;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routing_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        TextView routing_tv1 = (TextView) findViewById(R.id.routing_tv1);
        routing_tv1.setTypeface(AppData.fontXiti);
        TextView rout_wangluoshezhi = (TextView) findViewById(R.id.rout_wangluoshezhi);
        rout_wangluoshezhi.setTypeface(AppData.fontXiti);
        TextView rout_lan = (TextView) findViewById(R.id.rout_lan);
        rout_lan.setTypeface(AppData.fontXiti);
        TextView rout_wan = (TextView) findViewById(R.id.rout_wan);
        rout_wan.setTypeface(AppData.fontXiti);
        TextView rout_wifi = (TextView) findViewById(R.id.rout_wifi);
        rout_wifi.setTypeface(AppData.fontXiti);
        TextView rout_web = (TextView) findViewById(R.id.rout_web);
        rout_web.setTypeface(AppData.fontXiti);
        mRoutset_Lanset = (LinearLayout) findViewById(R.id.routset_lanset);//Lan口设置
        mRoutset_wanset = (LinearLayout) findViewById(R.id.routset_wanset);//WAN口设置
        mRoutset_wifiset = (LinearLayout) findViewById(R.id.routset_wifiset);//WIFI设置
        mWebaut_set = (LinearLayout) findViewById(R.id.webaut_set);//WEB认证设置
        mRouting_set_onoff = (ToggleButton) findViewById(R.id.routing_set_onoff);//WEB认证开关


        mRoutset_Lanset.setOnClickListener(this);
        mRoutset_wanset.setOnClickListener(this);
        mRoutset_wifiset.setOnClickListener(this);
        mWebaut_set.setOnClickListener(this);
        mRouting_set_onoff.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.routset_lanset:
                mIntent = new Intent(RoutingActivity.this, LanSetActivity.class);
                startActivity(mIntent);
                break;
            case R.id.routset_wanset:
                mIntent = new Intent(RoutingActivity.this, WanSetActivity.class);
                startActivity(mIntent);
                break;
            case R.id.routset_wifiset:
                mIntent = new Intent(RoutingActivity.this, WifiSetActivity.class);
                startActivity(mIntent);
                break;
            case R.id.webaut_set:
                mIntent = new Intent(RoutingActivity.this, WebAutSetActivity.class);
                startActivity(mIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        webAutQuery();
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
                        //Log.i(TAG, "接收到数据-->" + response.toString());
                        /*	Toast.makeText(RoutingActivity.this,
									response.toString(), 0).show();*/
                        loginDataQuery(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i(TAG, error.toString());
                Toast.makeText(RoutingActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RoutingActivity.this, "连接成功", 0).show();
            try {
                enableStr = response.getString("enable");
                RoutingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (enableStr.equals("0")) {
                            mRouting_set_onoff.setChecked(true);
                            mWebaut_set.setClickable(true);
                            //mWebaut_set.setEnabled(true);
                        } else if (enableStr.equals("1")) {
                            mRouting_set_onoff.setChecked(false);
                            mWebaut_set.setClickable(false);
                            //mWebaut_set.setEnabled(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(RoutingActivity.this, "连接失败", 0).show();//edit by hyw 20161205
        }
    }

    //点击退出页面
    public void routSetOnFinish(View v) {
        finish();
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

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.cancel();
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
//			mIntent=new Intent(RoutingActivity.this, WebAutSetActivity.class);
//			startActivity(mIntent);
            mWebaut_set.setClickable(true);
            //mWebaut_set.setEnabled(true);
        } else {
            mWebaut_set.setClickable(false);
            //mWebaut_set.setEnabled(false);
        }
    }


}
