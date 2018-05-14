package com.my51c.see51.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.DataModel;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.PackageUtil;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualControlActivity extends BaseActivity implements OnClickListener {
    private ImageView imageButton1;
    private Spinner mSatelliteNum;
    private TextView dangqiaojindu_tv, dangqiaoweidu_tv, mubiaojihua_tv, mubiaofangwei_tv, mubiaofuyang_tv;
    private LinearLayout mLinearLayoutCompass;
    private AppData application;
    private Button debug_btn;
    private String mModemIp;
    private double mSatelliteLongitude;
    private RequestQueue mRequestQueue;
    private double mLongitude, mLatitude;
    private Gson gson;
    private TextView super_reftv, dangqiaojin, dangqianweidu, mubiaojihua,
            mubiaofangwei, mubiaofuyang, capability, receice_level, mReceiveLevel, mCapability;
    private String mRx, maxRx, mTx, maxTx, curlon, currlat, mLongitudeS, mLatitudeS;
    private ProgressBar pb_progressbar, pb_progressbar1;
    private Spinner mDQJD, mDQWD, sp_mbjh, sp_fwmb, sp_mbfy;
    private int messageDelay = 900;
    private HttpUtils http;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getCatParameter();
            }
        }
    };
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initHttpData();
        initView();
        initEvent();
        List<PackageInfo> insatalledPackages = PackageUtil.getInsatalledPackages(this);
        for (PackageInfo po : insatalledPackages) {
            String pn = po.packageName;
            if (pn.endsWith(".compass")) {
                packageName = pn;
                break;
            }
        }
    }

    private void initEvent() {
        imageButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        application = (AppData) getApplication();
        mDQJD = (Spinner) findViewById(R.id.super_ref_mdqjd);
        mDQJD.setEnabled(false);
        mDQJD.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.e_w)));//当前经度
        mDQWD = (Spinner) findViewById(R.id.super_ref_mdqwd);
        mDQWD.setEnabled(false);
        mDQWD.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.n_s)));//当前纬度
        sp_mbjh = (Spinner) findViewById(R.id.sp_mbjh);
        sp_mbjh.setEnabled(false);
        sp_mbjh.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.n_jh)));//目标极化角
        sp_fwmb = (Spinner) findViewById(R.id.sp_fwmb);
        sp_fwmb.setEnabled(false);
        sp_fwmb.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.n_fw)));//目标方位角
        sp_mbfy = (Spinner) findViewById(R.id.sp_mbfy);
        sp_mbfy.setEnabled(false);
        sp_mbfy.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.n_fy)));//目标俯仰角
        mLongitude = application.mLatLng.longitude;
        curlon = (String.valueOf(mLongitude));
        if (curlon.substring(0, 1).equals("-") || curlon.substring(0, 1).equals("﹣") || curlon.substring(0, 1).equals("－") || curlon.substring(0, 1).equals("﹣")) {
            mDQJD.setSelection(1);
        } else {
            mDQJD.setSelection(0);

        }
        mLatitude = application.mLatLng.latitude;
        currlat = (String.valueOf(mLatitude));
        if (currlat.substring(0, 1).equals("-") || currlat.substring(0, 1).equals("﹣") || currlat.substring(0, 1).equals("－") || currlat.substring(0, 1).equals("﹣")) {
            mDQWD.setSelection(1);
        } else {
            mDQWD.setSelection(0);

        }
        imageButton1 = (ImageView) findViewById(R.id.imageButton1);
        dangqiaojindu_tv = (TextView) findViewById(R.id.dangqiaojindu_tv);
        mLongitudeS = String.valueOf(XTHttpJSON.decimalFormat.format(mLongitude));
        if ("0.00".equals(mLongitudeS)) {
            dangqiaojindu_tv.setText("--");
        } else {
            dangqiaojindu_tv.setText(String.valueOf(XTHttpJSON.decimalFormat.format(mLongitude)));
        }
        dangqiaoweidu_tv = (TextView) findViewById(R.id.dangqianweidu_tv);
        mLatitudeS = String.valueOf(XTHttpJSON.decimalFormat.format(mLatitude));
        if ("0.00".equals(mLatitudeS)) {
            dangqiaoweidu_tv.setText("--");
        } else {
            dangqiaoweidu_tv.setText(String.valueOf(XTHttpJSON.decimalFormat.format(mLatitude)));
        }
        mubiaojihua_tv = (TextView) findViewById(R.id.mubiaojihua_tv);
        mubiaofangwei_tv = (TextView) findViewById(R.id.mubiaofangwei_tv);
        mubiaofuyang_tv = (TextView) findViewById(R.id.mubiaofuyang_tv);
        super_reftv = (TextView) findViewById(R.id.super_reftv);
        super_reftv.setTypeface(AppData.fontXiti);
        dangqiaojin = (TextView) findViewById(R.id.dangqiaojin);
        dangqiaojin.setTypeface(AppData.fontXiti);
        dangqianweidu = (TextView) findViewById(R.id.dangqianweidu);
        dangqianweidu.setTypeface(AppData.fontXiti);
        mubiaojihua = (TextView) findViewById(R.id.mubiaojihua);
        mubiaojihua.setTypeface(AppData.fontXiti);
        mubiaofangwei = (TextView) findViewById(R.id.mubiaofangwei);
        mubiaofangwei.setTypeface(AppData.fontXiti);
        mubiaofuyang = (TextView) findViewById(R.id.mubiaofuyang);
        mubiaofuyang.setTypeface(AppData.fontXiti);
        capability = (TextView) findViewById(R.id.capability);
        receice_level = (TextView) findViewById(R.id.receice_level);
        debug_btn = (Button) findViewById(R.id.debug_btn);
        debug_btn.setTypeface(AppData.fontXiti);
        mCapability = (TextView) findViewById(R.id.tv_capability);
        mReceiveLevel = (TextView) findViewById(R.id.tv_receive_level);
        pb_progressbar = (ProgressBar) findViewById(R.id.pb_progressbar);
        pb_progressbar1 = (ProgressBar) findViewById(R.id.pb_progressbar1);
        mSatelliteNum = (Spinner) findViewById(R.id.super_ref_numner);// 卫星编号
        mSatelliteNum.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.onestart_spinner)));
        mSatelliteNum.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (mSatelliteNum.getSelectedItem().toString().equals("协同一号")) {
                    mSatelliteLongitude = 119.5;

                } else if (mSatelliteNum.getSelectedItem().toString().equals("亚洲七号")) {
                    mSatelliteLongitude = 105.5;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mLinearLayoutCompass = (LinearLayout) findViewById(R.id.ll_compass);
        mLinearLayoutCompass.setOnClickListener(this);
        debug_btn.setOnClickListener(this);
        getModemIp();
    }

    private void initHttpData() {
        gson = new Gson();
        http = new HttpUtils();
        http.configTimeout(5 * 1000);
        http.configSoTimeout(5 * 1000);
        http.configCurrentHttpCacheExpiry(500);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_compass:
                if (!PackageUtil.startAppByPackageName(ManualControlActivity.this, packageName)) {
                    Intent mIntent = new Intent(ManualControlActivity.this, CompassActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.debug_btn:
                calculatedValue();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getModemIp() {
        String getModemUrl = XTHttpUtil.GET_MODEM_IP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getModemUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        //Toast.makeText(ManualControlActivity.this, "获取ip地址成功", 0).show();
                    } else {
                        //Toast.makeText(ManualControlActivity.this, "获取ip地址失败", 0).show();
                    }
                    mModemIp = jsonObject.getString("ip");
                    if (mModemIp != null) {
                        getCatParameter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
        mRequestQueue.add(stringRequest);
    }

    private void calculatedValue() {
        //注意反正切函数返回的是弧度制的值, 这里重新转成了角度值,加Math.toDegrees.
        double a = mLatitude;
        double b = mSatelliteLongitude - mLongitude;
        double A = Math.toDegrees(Math.atan(Math.tan(b * Math.PI / 180) / Math.sin(a * Math.PI / 180)));
        String mubiaofangwei_value = XTHttpJSON.decimalFormat.format(A);
        if (mubiaofangwei_value.substring(0, 1).equals("-") || mubiaofangwei_value.substring(0, 1).equals("﹣") || mubiaofangwei_value.substring(0, 1).equals("－") || mubiaofangwei_value.substring(0, 1).equals("﹣")) {
            sp_fwmb.setSelection(2);
        } else {
            sp_fwmb.setSelection(1);

        }
        if ("0.00".equals(mLongitudeS) && "0.00".equals(mLatitudeS)) {
            mubiaofangwei_tv.setText("--");
        } else {
            mubiaofangwei_tv.setText(String.valueOf(Math.abs(Double.parseDouble(mubiaofangwei_value))));
        }
        double E = Math.toDegrees(Math.atan((Math.cos(a * Math.PI / 180) * Math.cos(b * Math.PI / 180) - 0.151)
                / Math.sqrt(1 - Math.cos(a * Math.PI / 180) * Math.cos(b * Math.PI / 180) * Math.cos(a * Math.PI / 180) * Math.cos(b * Math.PI / 180))));
        String mubiaofuyang_value = XTHttpJSON.decimalFormat.format(E);
        if (mubiaofuyang_value.substring(0, 1).equals("-") || mubiaofuyang_value.substring(0, 1).equals("﹣") || mubiaofuyang_value.substring(0, 1).equals("－") || mubiaofuyang_value.substring(0, 1).equals("﹣")) {
            sp_mbfy.setSelection(2);
        } else {
            sp_mbfy.setSelection(1);

        }
        if ("0.00".equals(mLongitudeS) && "0.00".equals(mLatitudeS)) {
            mubiaofuyang_tv.setText("--");
        } else {
            mubiaofuyang_tv.setText(String.valueOf(Math.abs(Double.parseDouble(mubiaofuyang_value))));
        }
        double P = Math.toDegrees(Math.atan(Math.sin((b) * Math.PI / 180) / Math.tan(a * Math.PI / 180)));
        String mubiaojihua_value = XTHttpJSON.decimalFormat.format(P);
        if (mubiaojihua_value.substring(0, 1).equals("-") || mubiaojihua_value.substring(0, 1).equals("﹣") || mubiaojihua_value.substring(0, 1).equals("－") || mubiaojihua_value.substring(0, 1).equals("﹣")) {
            sp_mbjh.setSelection(2);
        } else {
            sp_mbjh.setSelection(1);

        }
        if ("0.00".equals(mLongitudeS) && "0.00".equals(mLatitudeS)) {
            mubiaojihua_tv.setText("--");
        } else {
            mubiaojihua_tv.setText(String.valueOf(Math.abs(Double.parseDouble(mubiaojihua_value))));
        }
    }

    private void getCatParameter() {
        //String sendUrl = "http://10.192.0.177/cgi-bin/modemstatus/";
        String getCatParameterUrl = "http://" + mModemIp + "/cgi-bin/modemstatus/";
        http.send(HttpRequest.HttpMethod.GET, getCatParameterUrl, null,
                new RequestCallBack<JSONObject>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessageDelayed(1, messageDelay);
                    }

                    @SuppressWarnings("rawtypes")
                    @Override
                    public void onSuccess(ResponseInfo responseInfo) {
                        try {
                            String resInfoStr = responseInfo.result.toString();
                            DataModel dataModel = gson.fromJson(resInfoStr, DataModel.class);
                            mRx = dataModel.getRx();
                            maxRx = dataModel.getMaxrx();
                            mTx = dataModel.getTx();
                            maxTx = dataModel.getMaxtx();
                            mReceiveLevel.setText(mRx + "/" + maxRx + " dB");
                            mCapability.setText(mTx + "/" + maxTx + " dB");
                            if (mRx.substring(0, 1).equals("-") || mRx.substring(0, 1).equals("﹣") || mRx.substring(0, 1).equals("－") || mRx.substring(0, 1).equals("﹣")) {
                                pb_progressbar.setProgress(0);
                            } else {
                                pb_progressbar.setProgress((int) (((Double.parseDouble(mRx) / Double.parseDouble(maxRx)) * 100)));

                            }
                            if (mTx.substring(0, 1).equals("-") || mTx.substring(0, 1).equals("﹣") || mTx.substring(0, 1).equals("－") || mTx.substring(0, 1).equals("﹣")) {
                                pb_progressbar1.setProgress(0);
                            } else {
                                pb_progressbar1.setProgress((int) (((Double.parseDouble(mTx) / Double.parseDouble(maxTx)) * 100)));

                            }
                            handler.sendEmptyMessageDelayed(1, messageDelay);
                        } catch (Exception e) {
                            handler.sendEmptyMessageDelayed(1, messageDelay);
                            e.printStackTrace();
                        }
                    }
                });

    }
}
