package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
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
import com.my51c.see51.app.utils.ChechIpMask;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

/*WAN设置页面*/
public class WanSetActivity extends BaseActivity implements OnItemSelectedListener {
    protected static final String TAG = null;
    private Spinner mWanSet_protocol, mWanSet_wanNum;
    private View mUMTS_GPRS_EVDO, mWanSet_pppoe, mStaticIp, mDhcp;
    private EditText mEdStaticIp, mEdStaticMaskEd, mEdStaticGw, mEdStaticDns1,
            mEdStaticDns2;
    private EditText mEdDhcpHome;
    private EditText mEdPPPoeUser, mEdPPPoePass, mEdPPPoeAcc, mEdPPPoeServer;
    private EditText mUmtsModp, mUmtsSerType, mUmtsApn, mUmtsPin, mUmtsPapUser,
            mUmtsPapPass;
    private RequestQueue mRequestQueue;// 消息队列
    private String wanNumStr;
    private JSONObject object;
    private int POST_WANSET_TAG = 1;
    private int wanNum;
    private String typeStr;
    private String staticMask, staticIp, staticGw, staticDns1, staticDns2;
    private String dhcpHostNmae;
    private String pppoeUser, pppoePass, pppoeAccess, pppoeServer;
    private String umtsModp, umtsSertype, umtsApn, umtsPin, umtsPapuser,
            umtsPappass;
    private boolean is_staticIp = false, is_staticMask = false, is_staticGw = false;
    private String staticIp_ip;
    private String staticMask_mask;
    private String staticGw_gw;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wanset_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        TextView wan_set = (TextView) findViewById(R.id.wan_set);
        wan_set.setTypeface(AppData.fontXiti);

        TextView wan_tiaozheng = (TextView) findViewById(R.id.wan_tiaozheng);
        wan_tiaozheng.setTypeface(AppData.fontXiti);
        TextView wan_fuwuqi = (TextView) findViewById(R.id.wan_fuwuqi);
        wan_fuwuqi.setTypeface(AppData.fontXiti);
        TextView wan_apn = (TextView) findViewById(R.id.wan_apn);
        wan_apn.setTypeface(AppData.fontXiti);
        TextView wan_pin = (TextView) findViewById(R.id.wan_pin);
        wan_pin.setTypeface(AppData.fontXiti);
        TextView wan_username = (TextView) findViewById(R.id.wan_username);
        wan_username.setTypeface(AppData.fontXiti);
        TextView wan_pwd = (TextView) findViewById(R.id.wan_pwd);
        wan_pwd.setTypeface(AppData.fontXiti);
        TextView wan_pap = (TextView) findViewById(R.id.wan_pap);
        wan_pap.setTypeface(AppData.fontXiti);
        TextView wan_chap = (TextView) findViewById(R.id.wan_chap);
        wan_chap.setTypeface(AppData.fontXiti);
        TextView wan_jizhongqi = (TextView) findViewById(R.id.wan_jizhongqi);
        wan_jizhongqi.setTypeface(AppData.fontXiti);
        TextView wan_fuwuming = (TextView) findViewById(R.id.wan_fuwuming);
        wan_fuwuming.setTypeface(AppData.fontXiti);
        TextView wan_ip = (TextView) findViewById(R.id.wan_ip);//ip
        wan_ip.setTypeface(AppData.fontXiti);
        TextView wan_yanma = (TextView) findViewById(R.id.wan_yanma);//掩码
        wan_yanma.setTypeface(AppData.fontXiti);
        TextView wan_line = (TextView) findViewById(R.id.wan_line);//网关
        wan_line.setTypeface(AppData.fontXiti);
        TextView fuwuqi1 = (TextView) findViewById(R.id.fuwuqi1);//服务器1
        fuwuqi1.setTypeface(AppData.fontXiti);
        TextView fuwuqi2 = (TextView) findViewById(R.id.fuwuqi2);//服务器2
        fuwuqi2.setTypeface(AppData.fontXiti);
        TextView wan_zhujiname = (TextView) findViewById(R.id.wan_zhujiname);
        wan_zhujiname.setTypeface(AppData.fontXiti);

        TextView wan_wan = (TextView) findViewById(R.id.wan_wan);
        wan_wan.setTypeface(AppData.fontXiti);
        TextView wan_xieyi2 = (TextView) findViewById(R.id.wan_xieyi2);
        wan_xieyi2.setTypeface(AppData.fontXiti);

        //数量
        mWanSet_wanNum = (Spinner) findViewById(R.id.wanset_wan_num);
        mWanSet_wanNum.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wanset_wannum)));

        //协议
        mWanSet_protocol = (Spinner) findViewById(R.id.wanset_protocol);
        mWanSet_protocol.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.wan_protocol)));

        mUMTS_GPRS_EVDO = findViewById(R.id.wanset_umtsgprs_layout);
        mWanSet_pppoe = findViewById(R.id.wanset_pppope_layout);

        mStaticIp = findViewById(R.id.wanset_static_layout);//布局
        mDhcp = findViewById(R.id.wanset_dhcp_layout);

        mEdStaticIp = (EditText) mStaticIp.findViewById(R.id.wanset_static_ip);//IP
        mEdStaticMaskEd = (EditText) mStaticIp
                .findViewById(R.id.wanset_static_mask);//掩码
        mEdStaticGw = (EditText) mStaticIp.findViewById(R.id.wanset_static_gw);//网关
        mEdStaticDns1 = (EditText) mStaticIp
                .findViewById(R.id.wanset_static_dns1);//服务器1
        mEdStaticDns2 = (EditText) mStaticIp
                .findViewById(R.id.wanset_static_dns2);//服务器2

        mEdDhcpHome = (EditText) mDhcp.findViewById(R.id.wanset_dhcp_hostname);

        mEdPPPoeUser = (EditText) mWanSet_pppoe
                .findViewById(R.id.wanset_pppoe_papname);
        mEdPPPoePass = (EditText) mWanSet_pppoe
                .findViewById(R.id.Wanset_pppoe_pappassword);
        mEdPPPoeAcc = (EditText) mWanSet_pppoe
                .findViewById(R.id.wanset_pppoe_concentrator);
        mEdPPPoeServer = (EditText) mWanSet_pppoe
                .findViewById(R.id.wanset_pppoe_servername);

        mUmtsModp = (EditText) mUMTS_GPRS_EVDO.findViewById(R.id.wan_umts_modp);
        mUmtsSerType = (EditText) mUMTS_GPRS_EVDO
                .findViewById(R.id.wan_umts_sertype);
        mUmtsApn = (EditText) mUMTS_GPRS_EVDO
                .findViewById(R.id.wanset_umts_apn);
        mUmtsPin = (EditText) mUMTS_GPRS_EVDO
                .findViewById(R.id.Wanset_umts_pin);
        mUmtsPapUser = (EditText) mUMTS_GPRS_EVDO
                .findViewById(R.id.wanset_umts_papname);
        mUmtsPapPass = (EditText) mUMTS_GPRS_EVDO
                .findViewById(R.id.wanset_umts_pappasswodrk);

        mWanSet_protocol.setOnItemSelectedListener(this);

        object = new JSONObject();

        mWanSet_wanNum.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String wanStr = mWanSet_wanNum.getSelectedItem().toString();
                if (wanStr.equals("WAN1")) {
                    wanNumStr = "0";
                    try {
                        object.put("wannum", wanNumStr);
                        wanQuery(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (wanStr.equals("WAN2")) {
                    wanNumStr = "1";
                    try {
                        object.put("wannum", wanNumStr);
                        wanQuery(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (wanStr.equals("WAN3")) {
                    wanNumStr = "2";
                    try {
                        object.put("wannum", wanNumStr);
                        wanQuery(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (wanStr.equals("WAN4")) {
                    wanNumStr = "3";
                    try {
                        object.put("wannum", wanNumStr);
                        wanQuery(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (wanStr.equals("WAN5")) {
                    wanNumStr = "4";
                    try {
                        object.put("wannum", wanNumStr);
                        wanQuery(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // /////////////////////////////////////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        String itemStr = mWanSet_protocol.getSelectedItem().toString();
        if ("UMTS/GPRS/EV-DO".equals(itemStr)) {
            mUMTS_GPRS_EVDO.setVisibility(View.VISIBLE);
            mWanSet_pppoe.setVisibility(View.GONE);
            mStaticIp.setVisibility(View.GONE);
            mDhcp.setVisibility(View.GONE);
            POST_WANSET_TAG = 5;
        }
        if ("pppoe".equals(itemStr)) {
            mUMTS_GPRS_EVDO.setVisibility(View.GONE);
            mWanSet_pppoe.setVisibility(View.VISIBLE);
            mStaticIp.setVisibility(View.GONE);
            mDhcp.setVisibility(View.GONE);
            POST_WANSET_TAG = 4;
        }
        if ("静态地址".equals(itemStr)) {
            mUMTS_GPRS_EVDO.setVisibility(View.GONE);
            mWanSet_pppoe.setVisibility(View.GONE);
            mDhcp.setVisibility(View.GONE);
            mStaticIp.setVisibility(View.VISIBLE);
            POST_WANSET_TAG = 2;
        }
        if ("DHCP".equals(itemStr)) {
            mDhcp.setVisibility(View.VISIBLE);
            mUMTS_GPRS_EVDO.setVisibility(View.GONE);
            mWanSet_pppoe.setVisibility(View.GONE);
            mStaticIp.setVisibility(View.GONE);
            POST_WANSET_TAG = 3;
        } else {
            return;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void wanQuery(JSONObject jsonObject) {
        // 一进页面查询
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.PSOT_ROUTESET_WAN_QUERY, jsonObject,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);
                    /*	Toast.makeText(WanSetActivity.this,
								response.toString(), 0).show();*/
                        loginDataQuery(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WanSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }

            }
        });
        mRequestQueue.add(request);

    }

    private void getTextString() {////判断输入的IP是否合法！
        if (ChechIpMask.judgeIpIsLegal(mEdStaticIp.getText().toString())) {//判断输入的IP是否合法
            staticIp = mEdStaticIp.getText().toString();
            is_staticIp = true;
        } else if (!ChechIpMask.judgeIpIsLegal(mEdStaticIp.getText().toString())) {
            staticIp = null;
            is_staticIp = false;
            Toast.makeText(WanSetActivity.this, "抱歉，输入的IP格式不对！！", 0).show();
        }

        if (ChechIpMask.judgeSubnetMask(mEdStaticMaskEd.getText().toString())) {//判断掩码是否有效
            staticMask = mEdStaticMaskEd.getText().toString();
            is_staticMask = true;
        } else if (!ChechIpMask.judgeSubnetMask(mEdStaticMaskEd.getText().toString())) {
            staticMask = null;
            is_staticMask = false;
            Toast.makeText(WanSetActivity.this, "抱歉，输入的子网掩码格式不对！！", 0).show();
        }
        if (is_staticIp && is_staticMask) {
            if (ChechIpMask.judgeGatewayResult(staticIp, staticMask, mEdStaticGw.getText().toString())) {//判断网关是否有效
                staticGw = mEdStaticGw.getText().toString();
                is_staticGw = true;
            } else if (!ChechIpMask.judgeGatewayResult(staticIp, staticMask, mEdStaticGw.getText().toString())) {
                staticGw = null;
                is_staticGw = false;
                Toast.makeText(WanSetActivity.this, "抱歉，输入的网关格式不对！！", 0).show();
            }

        }


        staticDns1 = mEdStaticDns1.getText().toString();
        staticDns2 = mEdStaticDns2.getText().toString();

        dhcpHostNmae = mEdDhcpHome.getText().toString();

        pppoeUser = mEdPPPoeUser.getText().toString();
        pppoePass = mEdPPPoePass.getText().toString();
        pppoeAccess = mEdPPPoeAcc.getText().toString();
        pppoeServer = mEdPPPoeServer.getText().toString();

        umtsModp = mUmtsModp.getText().toString();
        umtsSertype = mUmtsSerType.getText().toString();
        umtsApn = mUmtsApn.getText().toString();
        umtsPin = mUmtsPin.getText().toString();
        umtsPapuser = mUmtsPapUser.getText().toString();
        umtsPappass = mUmtsPapPass.getText().toString();
    }

    private void loginDataQuery(JSONObject response) {//加载的时候做判断
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(WanSetActivity.this, "访问成功", 0).show();
            try {
                wanNum = response.getInt("wannum");
                typeStr = response.getString("type");

                mWanSet_wanNum.setSelection(wanNum);

                if (typeStr.equals("static")) {
                    JSONObject typeJsonObject = response
                            .getJSONObject("static");
                    POST_WANSET_TAG = 2;


                    if (ChechIpMask.judgeIpIsLegal(typeJsonObject.getString("ip"))) {
                        staticIp = typeJsonObject.getString("ip");
                        Log.e(TAG, "接收到的IP为正确的！！");

                    } else if (!ChechIpMask.judgeIpIsLegal(typeJsonObject.getString("ip"))) {
                        staticIp = null;
                        Log.e(TAG, "抱歉，接收到的IP为错误的！");
                        Toast.makeText(WanSetActivity.this, "抱歉，接收的IP格式不对！！", 0).show();
                    }

                    if (ChechIpMask.judgeSubnetMask(typeJsonObject.getString("mask"))) {
                        staticMask = typeJsonObject.getString("mask");
                        Log.e(TAG, "接收到的子网掩码为正确的！！");
                    } else if (!ChechIpMask.judgeSubnetMask(typeJsonObject.getString("mask"))) {
                        staticMask = null;
                        Log.e(TAG, "接收到的子网掩码为错误的！！");
                        Toast.makeText(WanSetActivity.this, "抱歉，输入的子网掩码格式不对！！", 0).show();
                    }

                    if (ChechIpMask.judgeGatewayResult(staticIp, staticMask, typeJsonObject.getString("gw"))) {
                        staticGw = typeJsonObject.getString("gw");
                        Log.e(TAG, "接收到的网关为正确的！！");
                    } else if (!ChechIpMask.judgeGatewayResult(staticIp, staticMask, typeJsonObject.getString("gw"))) {
                        staticGw = null;
                        Log.e(TAG, "接收到的网关为错误的！！");
                        Toast.makeText(WanSetActivity.this, "抱歉，输入的网关格式不对！！", 0).show();
                    }


                    staticDns1 = typeJsonObject.getString("dns1");
                    staticDns2 = typeJsonObject.getString("dns2");

                    mWanSet_protocol.setSelection(0);
                    mEdStaticIp.setText(staticIp);
                    mEdStaticMaskEd.setText(staticMask);
                    mEdStaticGw.setText(staticGw);
                    mEdStaticDns1.setText(staticDns1);
                    mEdStaticDns2.setText(staticDns2);

                } else if (typeStr.equals("dhcp")) {
                    dhcpHostNmae = response.getString("hostname");
                    POST_WANSET_TAG = 3;
                    mWanSet_protocol.setSelection(1);
                    mEdDhcpHome.setText(dhcpHostNmae);
                } else if (typeStr.equals("pppoe")) {
                    JSONObject pppoeObject = response.getJSONObject("pppoe");
                    POST_WANSET_TAG = 4;
                    pppoeUser = pppoeObject.getString("papuser");
                    pppoePass = pppoeObject.getString("pappass");
                    pppoeAccess = pppoeObject.getString("access");
                    pppoeServer = pppoeObject.getString("server");

                    mWanSet_protocol.setSelection(2);
                    mEdPPPoeUser.setText(pppoeUser);
                    mEdPPPoePass.setText(pppoePass);
                    mEdPPPoeAcc.setText(pppoeAccess);
                    mEdPPPoeServer.setText(pppoeServer);

                } else if (typeStr.equals("umts")) {
                    JSONObject umtsObject = response.getJSONObject("umts");
                    POST_WANSET_TAG = 5;
                    umtsModp = umtsObject.getString("modp");
                    umtsSertype = umtsObject.getString("sertype");
                    umtsApn = umtsObject.getString("apn");
                    umtsPin = umtsObject.getString("pin");
                    umtsPapuser = umtsObject.getString("papuser");
                    umtsPappass = umtsObject.getString("pappass");

                    mWanSet_protocol.setSelection(3);
                    mUmtsModp.setText(umtsModp);
                    mUmtsSerType.setText(umtsSertype);
                    mUmtsApn.setText(umtsApn);
                    mUmtsPin.setText(umtsPin);
                    mUmtsPapUser.setText(umtsPapuser);
                    mUmtsPapPass.setText(umtsPappass);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(WanSetActivity.this, "访问失败", 0).show();
        }

    }

    // 普通保存
    public void saveWanset(View v) {
        getTextString();
        if (POST_WANSET_TAG == 2) {
            if (is_staticGw && is_staticIp && is_staticMask) {
                object = XTHttpJSON.postWanSetStatic(wanNumStr, "static", "0",
                        staticIp, staticMask, staticGw, staticDns1, staticDns2);
                postWanSet(object);
            } else {
                Toast.makeText(WanSetActivity.this, "抱歉，IP 子网掩码  网关有错误输入！！", 0).show();
            }

        } else if (POST_WANSET_TAG == 3) {
            object = XTHttpJSON.postWanSetDhcp(wanNumStr, "dhcp", "0", dhcpHostNmae);
            postWanSet(object);
        } else if (POST_WANSET_TAG == 4) {
            object = XTHttpJSON.postWanSetPPPoe(wanNumStr, "pppoe", "0", pppoeUser,
                    pppoePass, pppoeAccess, pppoeServer);
            postWanSet(object);
        } else if (POST_WANSET_TAG == 5) {
            object = XTHttpJSON.postWanSetUmts(wanNumStr, "umts", "0", umtsModp, umtsSertype,
                    umtsApn, umtsPin, umtsPapuser, umtsPappass);
            postWanSet(object);
        }
    }

    //保存并应用
    public void saveAppWanSet(View v) {
        getTextString();
        if (POST_WANSET_TAG == 2) {
            if (is_staticGw && is_staticIp && is_staticMask) {
                object = XTHttpJSON.postWanSetStatic(wanNumStr, "static", "1",
                        staticIp, staticMask, staticGw, staticDns1, staticDns2);
                postWanSet(object);
            } else {
                Toast.makeText(WanSetActivity.this, "抱歉，IP 子网掩码  网关有错误输入！！", 0).show();
            }

        } else if (POST_WANSET_TAG == 3) {
            object = XTHttpJSON.postWanSetDhcp(wanNumStr, "dhcp", "1", dhcpHostNmae);
            postWanSet(object);
        } else if (POST_WANSET_TAG == 4) {
            object = XTHttpJSON.postWanSetPPPoe(wanNumStr, "pppoe", "1", pppoeUser,
                    pppoePass, pppoeAccess, pppoeServer);
            postWanSet(object);
        } else if (POST_WANSET_TAG == 5) {
            object = XTHttpJSON.postWanSetUmts(wanNumStr, "umts", "1", umtsModp, umtsSertype,
                    umtsApn, umtsPin, umtsPapuser, umtsPappass);
            postWanSet(object);
        }
    }

    private void postWanSet(JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_ROUTESET_WAN_SET, object,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);
						/*Toast.makeText(WanSetActivity.this,
								response.toString(), 0).show();*/
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WanSetActivity.this, "网络错误",
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

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 点击退出页面
    public void wanSetOnFinish(View v) {
        finish();
    }
}
