package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

/*Wifi设置界面*/
public class WifiSetActivity extends BaseActivity {
    private final static String TAG = "WifiSetActivity";
    public boolean resWifiTag;
    public boolean resWifiTag2;
    private ToggleButton mWifiSet_onoff24, mWifiSet_hidenet24;
    private ToggleButton mWifiSet_onoff5, mWifiSet_hidenet5,
            mWifiSet_bridged_net5;
    private EditText mWifiSet_ed24Name, mWifiSet_ed24PW, mWifiSet_ed5Name,
            mWifiSet_ed5PW;
    private Spinner mWifiSet_PW_Way24, mWfiSet_Singal24, mWifiSet_PW_Way5,
            mWfiSet_Singal5, mWifiSet24Channel, mWifiSet5Channel;
    private RequestQueue mRequestQueue;
    private int RES_WIFI_TAG = 3;// wifi开启的标记
    private Intent mIntent;
    private String enable2g, type2G, ssid2g, passset2g, hide2g;
    private String enable5g, type5G, ssid5g, passset5g, hide5g;

    // public void bridged_net5_click(View v) {
    // if (resWifiTag) {
    // Toast.makeText(WifiSetActivity.this, "点击了关闭", Toast.LENGTH_LONG)
    // .show();
    // resWifiTag=false;
    // }else {
    // Toast.makeText(WifiSetActivity.this, String.valueOf(resWifiTag),
    // Toast.LENGTH_LONG)
    // .show();
    // mIntent = new Intent(WifiSetActivity.this, ResCommActivity.class);
    // startActivity(mIntent);
    // resWifiTag=true;
    // }

    // }
    private int passtype2g, level2g, passtype5g, level5g, channel2g, channel5g;
    private ArrayAdapter<String> mPassType2g, mPassType5g;
    private ArrayAdapter<String> mLevelList2g, mLevelList5g;
    private String enable2gStr, time2gStr, type2gStr, ssid2gStr, passtype2gStr,
            passset2gStr, level2gStr, hide2gStr, channel2gStr;
    private String enable5gStr, time5gStr, type5gStr, ssid5gStr, passtype5gStr,
            passset5gStr, level5gStr, hide5gStr, channel5gStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifiset_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();

        wifiSetLogin();
    }

    private void initView() {
        TextView wifi_set = (TextView) findViewById(R.id.wifi_set);
        wifi_set.setTypeface(AppData.fontXiti);
        TextView wifi_wifi2 = (TextView) findViewById(R.id.wifi_wifi2);
        wifi_wifi2.setTypeface(AppData.fontXiti);
        TextView wifi_name = (TextView) findViewById(R.id.wifi_name);
        wifi_name.setTypeface(AppData.fontXiti);
        TextView wifi_jiamiway = (TextView) findViewById(R.id.wifi_jiamiway);
        wifi_jiamiway.setTypeface(AppData.fontXiti);
        TextView wifi_xinhao = (TextView) findViewById(R.id.wifi_xinhao);
        wifi_xinhao.setTypeface(AppData.fontXiti);
        TextView wifi_pwd = (TextView) findViewById(R.id.wifi_pwd);
        wifi_pwd.setTypeface(AppData.fontXiti);
        TextView wifi_yincangwnagluo = (TextView) findViewById(R.id.wifi_yincangwnagluo);
        wifi_yincangwnagluo.setTypeface(AppData.fontXiti);
        TextView wifi_xindao = (TextView) findViewById(R.id.wifi_xindao);
        wifi_xindao.setTypeface(AppData.fontXiti);
        TextView wifi_wifi5 = (TextView) findViewById(R.id.wifi_wifi5);
        wifi_wifi5.setTypeface(AppData.fontXiti);
        TextView wifi_kaiqi = (TextView) findViewById(R.id.wifi_kaiqi);
        wifi_kaiqi.setTypeface(AppData.fontXiti);
        TextView wifi_mingcheng = (TextView) findViewById(R.id.wifi_mingcheng);
        wifi_mingcheng.setTypeface(AppData.fontXiti);
        TextView wifi_jiami = (TextView) findViewById(R.id.wifi_jiami);
        wifi_jiami.setTypeface(AppData.fontXiti);
        TextView wifi_qiangdu = (TextView) findViewById(R.id.wifi_qiangdu);
        wifi_qiangdu.setTypeface(AppData.fontXiti);
        TextView wifi_mima = (TextView) findViewById(R.id.wifi_mima);
        wifi_mima.setTypeface(AppData.fontXiti);
        TextView wifi_yincang = (TextView) findViewById(R.id.wifi_yincang);
        wifi_yincang.setTypeface(AppData.fontXiti);
        TextView wifi_xind = (TextView) findViewById(R.id.wifi_xind);
        wifi_xind.setTypeface(AppData.fontXiti);


        mWifiSet_onoff24 = (ToggleButton) findViewById(R.id.wifiset_onff24);
        mWifiSet_hidenet24 = (ToggleButton) findViewById(R.id.wifiset_hidenet_onoff);
        mWifiSet_ed24Name = (EditText) findViewById(R.id.wifiset_edname02);
        mWifiSet_ed24PW = (EditText) findViewById(R.id.wifiset_ed_24password);

        mWifiSet_PW_Way24 = (Spinner) findViewById(R.id.wifiset_password_way);
        mWifiSet_PW_Way24.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifisert_password_way)));

        mWfiSet_Singal24 = (Spinner) findViewById(R.id.wifiset_singal);
        mWfiSet_Singal24.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifiset_signal_strength)));

        mWifiSet_onoff5 = (ToggleButton) findViewById(R.id.wifiset_onff5);
        mWifiSet_hidenet5 = (ToggleButton) findViewById(R.id.wifiset_hidenet_onoff5);
        mWifiSet_ed5Name = (EditText) findViewById(R.id.wifiset_ed5name);
        mWifiSet_ed5PW = (EditText) findViewById(R.id.wifiset_ed_password5);

        mWifiSet_PW_Way5 = (Spinner) findViewById(R.id.wifiset_password_way5);
        mWifiSet_PW_Way5.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifisert_password_way)));

        // 5G的信号强度
        mWfiSet_Singal5 = (Spinner) findViewById(R.id.wifiset_singal5);
        mWfiSet_Singal5.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifiset_signal_strength)));

        mWifiSet_bridged_net5 = (ToggleButton) findViewById(R.id.wifiset_bridged_net5);

        mWifiSet24Channel = (Spinner) findViewById(R.id.wifiset_24g_channel);
        mWifiSet24Channel.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifiset_24_channel)));

        mWifiSet5Channel = (Spinner) findViewById(R.id.wifiset_5g_channel);
        mWifiSet5Channel.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wifiset_24_channel)));

        mWifiSet_hidenet24.setEnabled(false);
        mWifiSet_ed24Name.setEnabled(false);
        mWifiSet_ed24PW.setEnabled(false);
        mWifiSet_PW_Way24.setEnabled(false);
        mWfiSet_Singal24.setEnabled(false);

        mWifiSet_bridged_net5.setEnabled(false);
        mWifiSet_hidenet5.setEnabled(false);
        mWifiSet_PW_Way5.setEnabled(false);
        mWifiSet_ed5Name.setEnabled(false);
        mWifiSet_ed5PW.setEnabled(false);
        mWfiSet_Singal5.setEnabled(false);

        mWifiSet24Channel.setEnabled(false);
        mWifiSet5Channel.setEnabled(false);

        switchOnCheckedChangeListener();
    }

    // GET查看服务器里面的数据
    private void wifiSetLogin() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_ROUTESET_WF_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        /*Toast.makeText(WifiSetActivity.this,
								response.toString(), 0).show();*/
                        RES_WIFI_TAG = 1;
                        loginDataQuery(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WifiSetActivity.this, "网络错误,与服务器断开",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                RES_WIFI_TAG = 2;
            }
        });
        mRequestQueue.add(request);
    }

    // GET解析服务器里面的数据
    protected void loginDataQuery(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                JSONObject object2g = response.getJSONObject("2g");
                enable2g = object2g.getString("enable");
                type2G = object2g.getString("type");
                ssid2g = object2g.getString("ssid");
                passtype2g = object2g.getInt("passtype");
                passset2g = object2g.getString("passset");
                level2g = object2g.getInt("level");
                hide2g = object2g.getString("hide");
                channel2g = object2g.getInt("channel");

                if (enable2g.equals("0")) {
                    mWifiSet_onoff24.setChecked(true);
                    mWifiSet_hidenet24.setEnabled(true);
                    mWifiSet_ed24Name.setEnabled(true);
                    mWifiSet_ed24PW.setEnabled(true);
                    mWifiSet_PW_Way24.setEnabled(true);
                    mWfiSet_Singal24.setEnabled(true);
                    mWifiSet24Channel.setEnabled(true);

                    mWifiSet_ed24Name.setText(ssid2g);
                    mWifiSet_ed24PW.setText(passset2g);

                    mWifiSet_PW_Way24.setSelection(passtype2g);
                    mWfiSet_Singal24.setSelection(level2g - 1);

                    if (hide2g.equals("0")) {
                        mWifiSet_hidenet24.setChecked(true);
                        hide2gStr = "0";
                    } else if (hide2g.equals("1")) {
                        mWifiSet_hidenet24.setChecked(false);
                        hide2gStr = "1";
                    }
                    mWifiSet24Channel.setSelection(channel2g - 1);
                }


                JSONObject object5g = response.getJSONObject("5g");
                enable5g = object5g.getString("enable");
                type5G = object5g.getString("type");
                ssid5g = object5g.getString("ssid");
                passtype5g = object5g.getInt("passtype");
                passset5g = object5g.getString("passset");
                level5g = object5g.getInt("level");
                hide5g = object5g.getString("hide");
                channel5g = object5g.getInt("channel");

                if (enable5g.equals("0")) {//可用
                    mWifiSet_bridged_net5.setEnabled(true);
                    mWifiSet_onoff5.setChecked(true);
                    mWifiSet_hidenet5.setEnabled(true);
                    mWifiSet_PW_Way5.setEnabled(true);
                    mWifiSet_ed5Name.setEnabled(true);
                    mWifiSet_ed5PW.setEnabled(true);
                    mWfiSet_Singal5.setEnabled(true);
                    mWifiSet5Channel.setEnabled(true);

                    if (type5G.equals("0")) {
                        mWifiSet_bridged_net5.setChecked(true);
                        // wifiSetLogin();
                        resWifiTag = true;
                        RES_WIFI_TAG = 1;
                    } else if (type5G.equals("-1")) {
                        mWifiSet_bridged_net5.setChecked(false);
                        resWifiTag = false;
                        RES_WIFI_TAG = 2;
                    }

                    mWifiSet_ed5Name.setText(ssid5g);
                    mWifiSet_ed5PW.setText(passset5g);
                    mWifiSet_PW_Way5.setSelection(passtype5g);
                    mWfiSet_Singal5.setSelection(level5g - 1);
                    if (hide5g.equals("0")) {
                        mWifiSet_hidenet5.setChecked(true);
                        hide5gStr = "0";
                    } else if (hide5g.equals("1")) {
                        mWifiSet_hidenet5.setChecked(false);
                        hide5gStr = "1";
                    }
                    mWifiSet5Channel.setSelection(channel5g - 1);
                } else if (!enable5g.equals("0")) {//可用

                    mWifiSet_bridged_net5.setEnabled(false);
                    mWifiSet_onoff5.setChecked(false);
                    mWifiSet_hidenet5.setEnabled(false);
                    mWifiSet_ed5Name.setEnabled(false);
                    mWifiSet_ed5PW.setEnabled(false);

                    mWifiSet_PW_Way5.setVisibility(View.INVISIBLE);
                    mWifiSet_PW_Way5.setEnabled(false);
                    mWfiSet_Singal5.setVisibility(View.INVISIBLE);
                    mWfiSet_Singal5.setEnabled(false);
                    mWifiSet5Channel.setVisibility(View.INVISIBLE);
                    mWifiSet5Channel.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "访问失败", 0).show();
        }
    }

    // Switch点击事件其他控件不能用
    private void switchOnCheckedChangeListener() {
        mWifiSet_onoff24
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            mWifiSet_hidenet24.setEnabled(true);
                            mWifiSet_ed24Name.setEnabled(true);
                            mWifiSet_ed24PW.setEnabled(true);
                            mWifiSet_PW_Way24.setEnabled(true);
                            mWfiSet_Singal24.setEnabled(true);
                            mWifiSet24Channel.setEnabled(true);
                            enable2gStr = "0";
                        } else {
                            mWifiSet_hidenet24.setEnabled(false);
                            mWifiSet_ed24Name.setEnabled(false);
                            mWifiSet_ed24PW.setEnabled(false);
                            mWifiSet_PW_Way24.setEnabled(false);
                            mWfiSet_Singal24.setEnabled(false);
                            mWifiSet24Channel.setEnabled(false);
                            enable2gStr = "1";
                        }
                    }
                });
        mWifiSet_bridged_net5
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            mWifiSet_hidenet5.setEnabled(true);
                            mWifiSet_PW_Way5.setEnabled(true);
                            mWifiSet_PW_Way5.setVisibility(View.VISIBLE);
                            mWifiSet_ed5Name.setEnabled(true);
                            mWifiSet_ed5PW.setEnabled(true);
                            mWfiSet_Singal5.setEnabled(true);
                            mWfiSet_Singal5.setVisibility(View.VISIBLE);
                            mWifiSet5Channel.setEnabled(true);
                            mWifiSet5Channel.setVisibility(View.VISIBLE);

                            if (RES_WIFI_TAG == 2) {
                                resWifiTag = true;
                                mIntent = new Intent(WifiSetActivity.this,
                                        ResCommActivity.class);
                                startActivity(mIntent);
                            }
                        } else {
                            mWifiSet_hidenet5.setEnabled(false);
                            mWifiSet_PW_Way5.setEnabled(false);
                            mWifiSet_PW_Way5.setVisibility(View.INVISIBLE);
                            mWifiSet_ed5Name.setEnabled(false);
                            mWifiSet_ed5PW.setEnabled(false);
                            mWfiSet_Singal5.setEnabled(false);
                            mWfiSet_Singal5.setVisibility(View.INVISIBLE);
                            mWifiSet5Channel.setEnabled(false);
                            mWifiSet5Channel.setVisibility(View.INVISIBLE);

                            resWifiTag = false;
                            RES_WIFI_TAG = 2;
                            try {
                                httpPostRequest(
                                        XTHttpUtil.POST_ROUTESET_Bridging_Connect,
                                        new JSONObject().put("enable", "-1")
                                                .toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        mWifiSet_onoff5
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            mWifiSet_bridged_net5.setEnabled(true);

                            enable5gStr = "0";
                        } else {
                            mWifiSet_bridged_net5.setEnabled(false);
                            mWifiSet_bridged_net5.setChecked(false);
                            enable5gStr = "1";

                        }
                    }
                });

        mWifiSet_hidenet24
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            hide2gStr = "0";
                        } else {
                            hide2gStr = "1";
                        }
                    }
                });
        mWifiSet_hidenet5
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            hide5gStr = "0";
                        } else {
                            hide5gStr = "1";
                        }
                    }
                });
    }

    private void getTextWifiSet() {
        ssid2gStr = mWifiSet_ed24Name.getText().toString();
        passset2gStr = mWifiSet_ed24PW.getText().toString();
        if (mWifiSet_PW_Way24.getSelectedItem().toString().equals("无加密")) {
            passtype2gStr = "1";
        } else if (mWifiSet_PW_Way24.getSelectedItem().toString()
                .equals("WAP2")) {
            passtype2gStr = "2";
        } else if (mWifiSet_PW_Way24.getSelectedItem().toString()
                .equals("混合加密")) {
            passtype2gStr = "3";
        }

        if (mWfiSet_Singal24.getSelectedItem().toString().equals("弱")) {
            level2gStr = "1";
        } else if (mWfiSet_Singal24.getSelectedItem().toString().equals("一般")) {
            level2gStr = "2";
        } else if (mWfiSet_Singal24.getSelectedItem().toString().equals("穿墙")) {
            level2gStr = "3";
        }
        channel2gStr = mWifiSet24Channel.getSelectedItem().toString();

        ssid5gStr = mWifiSet_ed5Name.getText().toString();
        passset5gStr = mWifiSet_ed5PW.getText().toString();
        if (mWifiSet_PW_Way5.getSelectedItem().toString().equals("无加密")) {
            passtype5gStr = "1";
        } else if (mWifiSet_PW_Way5.getSelectedItem().toString().equals("WAP2")) {
            passtype5gStr = "2";
        } else if (mWifiSet_PW_Way5.getSelectedItem().toString().equals("混合加密")) {
            passtype5gStr = "3";
        }
        if (mWfiSet_Singal5.getSelectedItem().toString().equals("弱")) {
            level5gStr = "1";
        } else if (mWfiSet_Singal5.getSelectedItem().toString().equals("一般")) {
            level5gStr = "2";
        } else if (mWfiSet_Singal5.getSelectedItem().toString().equals("穿墙")) {
            level5gStr = "3";
        }
        channel5gStr = mWifiSet5Channel.getSelectedItem().toString();

    }

    // 点击保存的按钮 Post数据到服务器
    public void onWifiSetSave(View v) {
        type2gStr = "0";
        type5gStr = "0";
        time2gStr = "0";
        time5gStr = "0";
        getTextWifiSet();
        JSONObject jsonObject = XTHttpJSON.postWifiSet(enable2gStr, time2gStr,
                type2gStr, ssid2gStr, passtype2gStr, passset2gStr, level2gStr,
                hide2gStr, channel2gStr, enable5gStr, time5gStr, type5gStr,
                ssid5gStr, passtype5gStr, passset5gStr, level5gStr, hide5gStr,
                channel5gStr);
        httpPostRequest(XTHttpUtil.POST_ROUTESET_WIFI_SET,
                jsonObject.toString());
        Log.e(TAG, "保存-----请求发送的数据。。。。。。》》》" + jsonObject.toString());
    }

    // 点击保存及应用的按钮 Post数据到服务器
    public void onWifiSetAppSave(View v) {
        type2gStr = "0";
        type5gStr = "0";
        time2gStr = "1";
        time5gStr = "1";
        getTextWifiSet();
        JSONObject jsonObject = XTHttpJSON.postWifiSet(enable2gStr, time2gStr,
                type2gStr, ssid2gStr, passtype2gStr, passset2gStr, level2gStr,
                hide2gStr, channel2gStr, enable5gStr, time5gStr, type5gStr,
                ssid5gStr, passtype5gStr, passset5gStr, level5gStr, hide5gStr,
                channel5gStr);
        httpPostRequest(XTHttpUtil.POST_ROUTESET_WIFI_SET,
                jsonObject.toString());
        Log.e(TAG, "保存及应用-----请求发送的 数据。。。。。。》》》" + jsonObject.toString());
    }

    private void httpPostRequest(String url, String jsonObjet) {
        RequestParams params = new RequestParams("UTF-8");
        try {
            params.setBodyEntity(new StringEntity(jsonObjet, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.setContentType("applicatin/json");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<JSONObject>() {

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        progresshow = true;
                        showDia();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        Log.i(TAG, "接收到数据-->" + arg0.reasonPhrase);
					/*	Toast.makeText(getApplicationContext(),
								arg0.result + "", 0).show();*/
                        pdDismiss(arg0);
                        // wifiSetLogin();
                    }
                });
    }

    // 点击退出页面
    public void wifiSetOnFinish(View v) {
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
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // /**
    // * 桥接查询服务器
    // */
    // // GET查看服务器里面的数据
    // private void Bridging_Select() {
    // progresshow = true;
    // showDia();
    // JsonObjectRequest request = new JsonObjectRequest(Method.GET,
    // XTHttpUtil.GET_ROUTESET_Bridging_Select, null,
    // new Listener<JSONObject>() {
    // @Override
    // public void onResponse(JSONObject response) {
    // pdDismiss(response);
    // Log.i(TAG, "接收到数据-->" + response.toString());
    // Toast.makeText(WifiSetActivity.this,
    // response.toString(), 0).show();
    // loginDataQuery2(response);
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // Log.i(TAG, error.toString());
    // Toast.makeText(WifiSetActivity.this, "网络错误,与服务器断开",
    // Toast.LENGTH_SHORT).show();
    // if (pd.isShowing()) {
    // pd.dismiss();
    // }
    // }
    // });
    // mRequestQueue.add(request);
    // }
    //
    // /**
    // * 解析桥接查询get
    // */
    // // GET解析服务器里面的数据
    //
    //
    // protected void loginDataQuery2(JSONObject response) {
    // if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
    // try {
    // JSONObject object5g = response.getJSONObject("5g");
    // enable5g = object5g.getString("enable");
    // type5G = object5g.getString("type");
    // ssid5g = object5g.getString("ssid");
    // if (enable5g.equals("0")) {
    // mWifiSet_bridged_net5.setEnabled(true);
    // mWifiSet_onoff5.setChecked(true);
    // mWifiSet_hidenet5.setEnabled(true);
    // mWifiSet_PW_Way5.setEnabled(true);
    // mWifiSet_ed5Name.setEnabled(true);
    // mWifiSet_ed5PW.setEnabled(true);
    // mWfiSet_Singal5.setEnabled(true);
    // mWifiSet5Channel.setEnabled(true);
    // if (type5G.equals("0")) {
    // mWifiSet_bridged_net5.setChecked(true);
    //
    // } else if (type5G.equals("1")) {
    // mWifiSet_bridged_net5.setChecked(false);
    // }
    // mWifiSet_ed5Name.setText(ssid5g);
    // mWifiSet_ed5PW.setText(passset5g);
    // mWifiSet_PW_Way5.setSelection(passtype5g);
    // mWfiSet_Singal24.setSelection(level5g - 1);
    // if (hide5g.equals("0")) {
    // mWifiSet_hidenet5.setChecked(true);
    // hide5gStr = "0";
    // } else if (hide5g.equals("1")) {
    // mWifiSet_hidenet5.setChecked(false);
    // hide5gStr = "1";
    // }
    // mWifiSet5Channel.setSelection(channel5g - 1);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
    // Toast.makeText(this, "访问失败", 0).show();
    // }
    // }
}
