package com.my51c.see51.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
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
import com.my51c.see51.app.db.XTsateenOpenHelper;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.ResWifiActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

/*网络设置*/
public class NetWorkSetActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {
    protected static final String TAG = "NetWorkSetActivity";
    private ToggleButton network_Satellite_OnOff, network_Sim_OnOff,
            network_Wifi_OnOff, network_Bw_OnOff, network_Tariff_OnOff;
    private RequestQueue mRequestQueue;// 消息队列
    private LinearLayout mNetwork_satellite_LL, mNetwork_34g_LL,
            mNetwork_wifi_LL, mNetwork_first_LL, mNetwork_tariff_LL;
    private XTsateenOpenHelper mNetOpenHelper;
    private Cursor mCursor;
    private boolean tagNet;
    private boolean resWifiTag;
    private String policyEnStr, policyVal;
    private int START_NETWORk_TAG = 3;// 一键对星TAG
    private int THREE_FOUR_G_TAG = 3;// 3G4G公网标记
    private int BW_FIRST = 3;// 带宽优先
    private int TARIFF_FIRST = 3;// 资费优先
    private int RES_WIFI_TAG = 3;// wifi开启的标记
    private String satelliteEnable, satelliteNum, satelliteName, satellink;
    private String simEnable, simNum, simType, wifiLink, simLink;
    private String wifiEnable, wifiSSID;
    private int mPolicy;
    private Intent mIntent;
    private String sim1type, sim2type, simEnadlePost;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;
    private String startEnableStr, satenumStr, satelonStr, modeStr, freqStr,
            bwStr, typeStr, modemStr, rssiStr, recvpolStr, sendpolStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_set_activity);
        mNetOpenHelper = new XTsateenOpenHelper(this);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        netWorkSetLogin();
    }

    private void initView() {
        network_Satellite_OnOff = (ToggleButton) findViewById(R.id.network_satellite_onoff);// 卫星网络
        network_Sim_OnOff = (ToggleButton) findViewById(R.id.network_sim_onoff);// 3G4G公网
        network_Wifi_OnOff = (ToggleButton) findViewById(R.id.network_wifi_onoff);// wifi
        network_Bw_OnOff = (ToggleButton) findViewById(R.id.network_bw_onoff);// 宽带优先
        network_Tariff_OnOff = (ToggleButton) findViewById(R.id.network_tariff_onoff);// 资费优先

        mNetwork_satellite_LL = (LinearLayout) findViewById(R.id.network_satellite);
        mNetwork_34g_LL = (LinearLayout) findViewById(R.id.network_set_34g);
        mNetwork_wifi_LL = (LinearLayout) findViewById(R.id.network_set_wifi);
        mNetwork_first_LL = (LinearLayout) findViewById(R.id.network_bw_first);
        mNetwork_tariff_LL = (LinearLayout) findViewById(R.id.network_tariff);

        mNetwork_satellite_LL.setOnClickListener(this);
        mNetwork_34g_LL.setOnClickListener(this);
        mNetwork_wifi_LL.setOnClickListener(this);
        mNetwork_first_LL.setOnClickListener(this);
        mNetwork_tariff_LL.setOnClickListener(this);

        network_Satellite_OnOff.setOnCheckedChangeListener(this);

        // 34G 公网开启的时候get 请求数据
        network_Sim_OnOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            tagNet = true;
                            if (THREE_FOUR_G_TAG == 2) {
                                tagNet = true;
                                simEnadlePost = "0";
                                simQuery();
                                // showListDia();

                            }
                            /*
							 * else { // THREE_FOUR_G_TAG = 2; // }
							 */
                        } else {
                            tagNet = false;
                            simEnadlePost = "-1";
                            THREE_FOUR_G_TAG = 2;
                            try {
                                postSimEnable(new JSONObject().put("enable",
                                        simEnadlePost));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        network_Wifi_OnOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            if (RES_WIFI_TAG == 2) {
                                resWifiTag = true;
                                mIntent = new Intent(NetWorkSetActivity.this,
                                        ResWifiActivity.class);
                                startActivity(mIntent);
                            }
                        } else {
                            resWifiTag = false;
                            RES_WIFI_TAG = 2;
                            try {
                                httpPostRequest(
                                        XTHttpUtil.POST_NETSET_WIFICONNECT,
                                        new JSONObject().put("enable", "-1")
                                                .toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        network_Bw_OnOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            if (BW_FIRST == 3) {
                                network_Tariff_OnOff.setChecked(false);
                                policyEnStr = "0";
                                policyVal = "1";
                                BW_FIRST = 1;
                                JSONObject object = XTHttpJSON.postPolicyslc(
                                        policyEnStr, policyVal);
                                postPolicyEnable(object);
                            } else {
                                BW_FIRST = 3;
                            }
                        } else {
                            network_Tariff_OnOff.setChecked(true);
                            policyEnStr = "-1";
                            BW_FIRST = 3;
                        }
                    }
                });
        network_Tariff_OnOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            if (TARIFF_FIRST == 3) {
                                network_Bw_OnOff.setChecked(false);
                                policyEnStr = "0";
                                policyVal = "2";
                                TARIFF_FIRST = 1;
                                JSONObject object = XTHttpJSON.postPolicyslc(
                                        policyEnStr, policyVal);
                                postPolicyEnable(object);
                            } else {
                                TARIFF_FIRST = 3;
                            }
                        } else {
                            network_Bw_OnOff.setChecked(true);
                            policyEnStr = "-1";
                            TARIFF_FIRST = 3;
                        }
                    }
                });
    }

    // 一进页面查询
    private void netWorkSetLogin() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_NETSET_STATUS, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "接收到数据-->" + response.toString());
                // Toast.makeText(NetWorkSetActivity.this,
                // response.toString()+"44444444444444444444444444",
                // 0).show();
						/*
						 * START_NETWORk_TAG = 1; THREE_FOUR_G_TAG = 1;
						 */
                BW_FIRST = 1;
                TARIFF_FIRST = 1;
                RES_WIFI_TAG = 1;
                pdDismiss(response);
                loginDataQuery(response);
                Toast.makeText(
                        NetWorkSetActivity.this,
                        "卫星网络选择:" + satelliteName + "\n" + "3/4G公网的运行："
                                + simType + "\n" + "wifi选择:" + wifiSSID,
                        Toast.LENGTH_LONG).show();

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                START_NETWORk_TAG = 2;
                THREE_FOUR_G_TAG = 2;
                RES_WIFI_TAG = 2;
            }
        });
        mRequestQueue.add(request);
    }

    // 查找加载数据
    private void loginDataQuery(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                JSONObject satelliteJObject = response
                        .getJSONObject("satellite");
                satelliteEnable = satelliteJObject.getString("enable");
                satellink = satelliteJObject.getString("satelink");
                if (satelliteEnable.equals("0")) {
                    network_Satellite_OnOff.setChecked(true);
                    satelliteNum = satelliteJObject.getString("num");
                    //satelliteName = satelliteJObject.getString("satename");
                    START_NETWORk_TAG = 1;
                } else if (satelliteEnable.equals("-1")) {
                    network_Satellite_OnOff.setChecked(false);
                    START_NETWORk_TAG = 2;
                }
                if (satellink.equals("0")) {
                    Toast.makeText(NetWorkSetActivity.this, "卫星链路连接成功", 0).show();
                } else {
                    Toast.makeText(NetWorkSetActivity.this, "卫星链路连接失败", 0).show();
                }
                JSONObject simObject = response.getJSONObject("sim");
                simEnable = simObject.getString("enable");
                simLink = simObject.getString("simlink");
                if (simEnable.equals("0")) {
                    network_Sim_OnOff.setChecked(true);
                    tagNet = true;
                    simEnadlePost = "0";
                    THREE_FOUR_G_TAG = 1;
                } else if (simEnable.equals("-1")) {
                    network_Sim_OnOff.setChecked(false);
                    tagNet = false;
                    simEnadlePost = "-1";
                    THREE_FOUR_G_TAG = 2;
                }
                Toast.makeText(NetWorkSetActivity.this, "simLink------>" + simLink.toString(), 0).show();
                if (simLink.equals("0")) {
                    Toast.makeText(NetWorkSetActivity.this, "3G/4G公网连接成功", 0).show();
                    simNum = simObject.getString("simnum");
                    simType = simObject.getString("simtype");
                } else if (simLink.equals("-1")) {
                    Toast.makeText(NetWorkSetActivity.this, "3G/4G公网连接失败", 0).show();
                }
                JSONObject wifiObject = response.getJSONObject("wifi");
                wifiEnable = wifiObject.getString("enable");
                wifiLink = wifiObject.getString("wifilink");
                if (wifiEnable.equals("0")) {
                    network_Wifi_OnOff.setChecked(true);
                    resWifiTag = true;
                    RES_WIFI_TAG = 1;
                } else if (wifiEnable.equals("-1")) {
                    network_Wifi_OnOff.setChecked(false);
                    resWifiTag = false;
                    RES_WIFI_TAG = 2;
                }
                if (wifiLink.equals("0")) {
                    wifiSSID = wifiObject.getString("SSID");
                    Toast.makeText(NetWorkSetActivity.this, "wifi连接成功", 0).show();
                } else if (wifiLink.equals("-1")) {
                    Toast.makeText(NetWorkSetActivity.this, "wifi连接失败", 0).show();
                } else if (wifiLink.equals("1")) {
                    Toast.makeText(NetWorkSetActivity.this, "正在连接wifi", 0).show();
                }

                mPolicy = response.getInt("policy");
                if (mPolicy == 1) {
                    network_Bw_OnOff.setChecked(true);
                    network_Tariff_OnOff.setChecked(false);
                    BW_FIRST = 1;
                    TARIFF_FIRST = 3;
                } else if (mPolicy == 2) {
                    network_Bw_OnOff.setChecked(false);
                    network_Tariff_OnOff.setChecked(true);
                    BW_FIRST = 3;
                    TARIFF_FIRST = 1;
                }
                // upDataUi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 更新UI的方法
    private void upDataUi() {
		/*
		 * switch (satelliteEnable) { case 0:
		 * network_Satellite_OnOff.setChecked(true); START_NETWORk_TAG = 1;
		 * break; case -1: network_Satellite_OnOff.setChecked(false);
		 * START_NETWORk_TAG = 2; break; }
		 *
		 * switch (simEnable) { case 0: network_Sim_OnOff.setChecked(true);
		 * tagNet = true; simEnadlePost = "0"; THREE_FOUR_G_TAG = 1; break; case
		 * -1: network_Sim_OnOff.setChecked(false); tagNet = false;
		 * simEnadlePost = "-1"; THREE_FOUR_G_TAG = 2; break; }
		 *
		 * switch (wifiEnable) { case 0: network_Wifi_OnOff.setChecked(true);
		 * resWifiTag = true; break; case -1:
		 * network_Wifi_OnOff.setChecked(false); resWifiTag = false; break; }
		 *
		 * switch (mPolicy) { case 1: network_Bw_OnOff.setChecked(true);
		 * network_Tariff_OnOff.setChecked(false); BW_FIRST = 1; break; case 2:
		 * network_Bw_OnOff.setChecked(false);
		 * network_Tariff_OnOff.setChecked(true); TARIFF_FIRST = 1; break; }
		 */
        // if (satelliteEnable.equals("0")) {
        // network_Satellite_OnOff.setChecked(true);
        // START_NETWORk_TAG = 1;
        // }
        // if (satelliteEnable.equals("-1")) {
        // network_Satellite_OnOff.setChecked(false);
        // START_NETWORk_TAG = 2;
        // }
        // // if (satelliteEnable.equals("-1"))
        // if (simEnable.equals("0")) {
        // network_Sim_OnOff.setChecked(true);
        // tagNet = true;
        // simEnadlePost = "0";
        // THREE_FOUR_G_TAG = 1;
        // }
        // if (simEnable.equals("-1")) {
        // network_Sim_OnOff.setChecked(false);
        // tagNet = false;
        // simEnadlePost = "-1";
        // THREE_FOUR_G_TAG = 2;
        // }
        //
        // if (wifiEnable.equals("0")) {
        // network_Wifi_OnOff.setChecked(true);
        // resWifiTag = true;
        // }
        // if (wifiEnable.equals("-1")) {
        // network_Wifi_OnOff.setChecked(false);
        // resWifiTag = false;
        // }
        //
        // if (mPolicy == 1) {
        // network_Bw_OnOff.setChecked(true);
        // network_Tariff_OnOff.setChecked(false);
        // BW_FIRST = 1;
        // } else if (mPolicy == 2) {
        // network_Bw_OnOff.setChecked(false);
        // network_Tariff_OnOff.setChecked(true);
        // TARIFF_FIRST = 1;
        // }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.network_set_34g:
                if (tagNet) {
                    simQuery();
                }
                break;
            case R.id.network_set_wifi:
                if (resWifiTag) {
                    mIntent = new Intent(NetWorkSetActivity.this,
                            ResWifiActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.network_bw_first:

                break;
            case R.id.network_tariff:

                break;

        }

    }

    // 34G打开之后的查询
    private void simQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_NETSET_SIM_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);

                        loginGetSim(response);
                        showListDia();
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                THREE_FOUR_G_TAG = 2;
            }
        });
        mRequestQueue.add(request);
    }

    // 34G点击开始查询
    private void loginGetSim(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(NetWorkSetActivity.this, "访问成功", Toast.LENGTH_SHORT)
                    .show();
            try {
                sim1type = response.getString("sim1type");
                sim2type = response.getString("sim2type");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("1")) {
            Toast.makeText(NetWorkSetActivity.this, "正在开启", Toast.LENGTH_SHORT)
                    .show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(NetWorkSetActivity.this, "访问失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /* 列表对话框 */
    private void showListDia() {
        if (sim1type != null || sim2type != null) {
            final String[] mList = {sim1type, sim2type};
            AlertDialog.Builder listDia = new AlertDialog.Builder(this);
            listDia.setItems(mList, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
					/* 下标是从0开始的 */
                    showClickMessage(mList[which]);

					/*
					 * if (mList[which].equals("CDMA")) { simNum = "1"; } if
					 * (mList[which].equals("TD-SCDMA")) { simNum = "2"; }
					 * if(mList[which].equals("WCDMA")){ simNum="3"; }
					 */

                    JSONObject object = XTHttpJSON.postSimEnable(simEnadlePost,
                            which + 1 + "");
                    // postSimEnable(object);
                    httpPostRequest(XTHttpUtil.POST_NETSET_SIMENABLE,
                            object.toString());
                }
            });
            listDia.create().show();
        } else {
            showClickMessage("加载的数据为空");
            // return;
        }
    }

    // simPost
    private void postSimEnable(JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_NETSET_SIMENABLE, object,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);
                        if (XTHttpJSON.getJSONString(response.toString())
                                .equals("0")) {
                            Toast.makeText(NetWorkSetActivity.this, "访问成功", 0)
                                    .show();
                        } else if (XTHttpJSON
                                .getJSONString(response.toString())
                                .equals("-1")) {
                            Toast.makeText(NetWorkSetActivity.this, "访问失败", 0)
                                    .show();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(request);
    }

    // Post Policyslc 带宽资费选择
    private void postPolicyEnable(JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_NETSET_POLICYSLC, object,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);
                        if (XTHttpJSON.getJSONString(response.toString())
                                .equals("0")) {
                            Toast.makeText(NetWorkSetActivity.this, "访问成功", 0)
                                    .show();
                        } else if (XTHttpJSON
                                .getJSONString(response.toString())
                                .equals("-1")) {
                            Toast.makeText(NetWorkSetActivity.this, "访问失败", 0)
                                    .show();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
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

    /* 显示点击的内容 */
    private void showClickMessage(String message) {
        Toast.makeText(this, message + "aaaaaaaaaaaaaaaaaaaaaaaa",
                Toast.LENGTH_SHORT).show();
    }

    // ////////////////////卫星网络是否开启///////////////////////////
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (START_NETWORk_TAG == 2) {
				/*
				 * mCursor = mNetOpenHelper.select(1); while
				 * (mCursor.moveToPosition(0)) { int id =
				 * mCursor.getInt(mCursor.getColumnIndex("_id")); satenumStr =
				 * mCursor.getString(mCursor .getColumnIndex("satenum"));
				 * satelonStr = mCursor.getString(mCursor
				 * .getColumnIndex("satelon")); modeStr =
				 * mCursor.getString(mCursor.getColumnIndex("mode")); freqStr =
				 * mCursor.getString(mCursor.getColumnIndex("freq")); bwStr =
				 * mCursor.getString(mCursor.getColumnIndex("bw")); typeStr =
				 * mCursor.getString(mCursor.getColumnIndex("type")); modemStr =
				 * mCursor.getString(mCursor.getColumnIndex("modem")); //
				 * Log.i("===========",satenumStr+ //
				 * satelonStr+modeStr+freqStr+bwStr+typeStr+modemStr);
				 */
                startEnableStr = "0";
                httpPostRequest(XTHttpUtil.POST_NETSET_SATEENABLE,
                        startNetWork());
            } else {
                START_NETWORk_TAG = 2;
            }
        } else {
            START_NETWORk_TAG = 2;
            startEnableStr = "-1";
            httpPostRequest(XTHttpUtil.POST_NETSET_SATEENABLE, startNetWork());
        }
    }

    private String startNetWork() {
        SharedPreferences read = getSharedPreferences("onestart", MODE_PRIVATE);
        satenumStr = read.getString("satenum", "");
        satelonStr = read.getString("satelon", "");
        modeStr = read.getString("mode", "");
        freqStr = read.getString("freq", "");
        bwStr = read.getString("bw", "");
        typeStr = read.getString("type", "");
        modemStr = read.getString("modem", "");
        rssiStr = read.getString("rssi", "");
        recvpolStr = read.getString("recvpol", "");
        sendpolStr = read.getString("sendpol", "");
        Toast.makeText(
                this,
                satenumStr + satelonStr + modeStr + freqStr + bwStr + typeStr
                        + modemStr + rssiStr + recvpolStr + sendpolStr
                        + "333333333333333333333333", 0).show();
        JSONObject object = XTHttpJSON.postSateableSet(startEnableStr,
                satenumStr, satelonStr, modeStr, freqStr, bwStr, typeStr,
                modemStr, rssiStr, recvpolStr, sendpolStr);
        return object.toString();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 点击退出页面
    public void netWorkOnFinish(View v) {
        finish();
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
                        Toast.makeText(getApplicationContext(),
                                arg0.result + "11111111111111111", 0).show();
                        pdDismiss(arg0);
                        // loginDataQuery(arg0);
                    }
                });
    }

    protected void loginDataQuery(String string) {

    }

}

