package com.my51c.see51.app.routbw;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
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
import com.my51c.see51.app.utils.ChechIpMask;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

/*路由设置---》     网络设置---》  Lan口设置*/
@SuppressLint("ShowToast")
public class LanSetActivity extends BaseActivity implements OnCheckedChangeListener {
    private final static String TAG = "LanSetActivity";
    //	private Spinner mLan_protocol;
    private LinearLayout mLanset_DHCP_LL;
    private EditText mLanset_ipaddress, mLanset_subnet_code, mlanset_gateway01,
            mLanset_gateway02;
    private ToggleButton mLanset_dhcp_onff;
    private EditText mLanset_dhcp_start, mLanset_dhcp_pend,
            mLanset_dhcp_renttime;
    private Button mLanSetSave;// 点击保存POST数据
    private RequestQueue mRequestQueue;

    private int mProtocolInt;
    private String mProtocol, mProtocolStr, mIPStr, mMaskStr, mGwStr, mBroadcast, mDhcp,
            mDhcpstart, mDhcpend, mRenttime, enableStr;

    private boolean mDhcpTag = false;
    private String mIPString;          //ip给更新用
    private String mMaskString;        //mask给更新用
    private boolean isIP = false, isMask = false;
    // 加载数据的 ProgressDialogO
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lanset_set);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        queryLanSet();
    }

    private void initView() {
        TextView lan_set = (TextView) findViewById(R.id.lan_set);
        lan_set.setTypeface(AppData.fontXiti);
        //	TextView lan_setxieyi=(TextView)findViewById(R.id.lan_setxieyi);
        //	lan_setxieyi.setTypeface(AppData.fontXiti);
        TextView lan_ipadress = (TextView) findViewById(R.id.lan_ipadress);
        lan_ipadress.setTypeface(AppData.fontXiti);
        TextView lan_ziwangyanma = (TextView) findViewById(R.id.lan_ziwangyanma);
        lan_ziwangyanma.setTypeface(AppData.fontXiti);
        //	TextView lan_wangguan=(TextView)findViewById(R.id.lan_wangguan);
        //	lan_wangguan.setTypeface(AppData.fontXiti);
        //	TextView lan_broadcast=(TextView)findViewById(R.id.lan_broadcast);
        //	lan_broadcast.setTypeface(AppData.fontXiti);
        TextView lan_dchp = (TextView) findViewById(R.id.lan_dchp);
        lan_dchp.setTypeface(AppData.fontXiti);
        TextView lan_spoolstart = (TextView) findViewById(R.id.lan_spoolstart);
        lan_spoolstart.setTypeface(AppData.fontXiti);
        TextView lan_adressend = (TextView) findViewById(R.id.lan_adressend);
        lan_adressend.setTypeface(AppData.fontXiti);
        TextView lan_zuyongtime = (TextView) findViewById(R.id.lan_zuyongtime);
        lan_zuyongtime.setTypeface(AppData.fontXiti);

        mLanSetSave = (Button) findViewById(R.id.lan_set_btnsave);// 保存 提交POST
        //	mLan_protocol = (Spinner) findViewById(R.id.lan_protocol);// 协议
        //mLan_protocol.setAdapter(new SpinnerAdapter(this,
        //		android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lan_protocol)));
        mLanset_ipaddress = (EditText) findViewById(R.id.lanset_ipaddress);// ip地址
        mLanset_subnet_code = (EditText) findViewById(R.id.lanset_subnet_code);// 子网掩码
        //mlanset_gateway01 = (EditText) findViewById(R.id.lanset_gateway01);// 网关1
        //	mLanset_gateway02 = (EditText) findViewById(R.id.lanset_gateway02);// 网关2

        mLanset_dhcp_onff = (ToggleButton) findViewById(R.id.lanset_dhcp_onff);// dhcp开关

        mLanset_dhcp_start = (EditText) findViewById(R.id.lanset_dhcp_start);// dhcp开始
        mLanset_dhcp_pend = (EditText) findViewById(R.id.lanset_dhcp_pend);// dhcp结束
        mLanset_dhcp_renttime = (EditText) findViewById(R.id.lanset_dhcp_renttime);// dhcp租时间


        mLanset_dhcp_onff.setChecked(false);
        mLanset_dhcp_start.setEnabled(false);
        mLanset_dhcp_pend.setEnabled(false);
        mLanset_dhcp_renttime.setEnabled(false);

        mLanset_DHCP_LL = (LinearLayout) findViewById(R.id.lanset_dhcp_layout);
        mLanset_dhcp_onff.setOnCheckedChangeListener(this);
    }

    @SuppressLint("ShowToast")
    private void getLenSetText() {
        //校验输入的IP地址,调用此方法发送给服务器。
        if (ChechIpMask.judgeIpIsLegal(mLanset_ipaddress.getText().toString())) {
            mIPStr = mLanset_ipaddress.getText().toString();
            Log.e(TAG, "是一个IP--------》》》》》");
            isIP = true;
        } else if (!ChechIpMask.judgeIpIsLegal(mLanset_ipaddress.getText().toString())) {
            Log.e(TAG, "不是一个IP--------》》》》》");
            isIP = false;
            mIPStr = null;
            Toast.makeText(LanSetActivity.this, "输入的IP的格式不准确！！", 0).show();
        }
        //校验输入的子网掩码,调用此方法发送给服务器。
        if (ChechIpMask.judgeSubnetMask(mLanset_subnet_code.getText().toString())) {
            mMaskStr = mLanset_subnet_code.getText().toString();
            Log.e(TAG, "是一个子网掩码-------->>>>>>>");
            Toast.makeText(LanSetActivity.this, "输入的子网掩码的格式准确！！", 0).show();
            isMask = true;
        } else if (!ChechIpMask.judgeSubnetMask(mLanset_subnet_code.getText().toString())) {
            Log.e(TAG, "不是一个子网掩码-------->>>>>>>");
            mMaskStr = null;
            isMask = false;
            Toast.makeText(LanSetActivity.this, "输入的子网掩码的格式不准确！！", 0).show();
        }
        if (mDhcpTag) {
            mDhcp = "0";
            mDhcpstart = mLanset_dhcp_start.getText().toString();
            mDhcpend = mLanset_dhcp_pend.getText().toString();
            mRenttime = mLanset_dhcp_renttime.getText().toString();
        } else {
            mDhcp = "1";
            mDhcpstart = "";
            mDhcpend = "";
            mRenttime = "";
        }
    }

    public void onLanSetSave(View v) {
        enableStr = "0";
        postLanSet();
    }

    public void onLanSetSaveApp(View v) {
        enableStr = "1";
        postLanSet();
    }

    private void postLanSet() {
        progresshow = true;
        showDia();
        getLenSetText();

        if (isIP && isMask) {//是，才能提交。
            JSONObject object = XTHttpJSON.postLanSet(enableStr, mProtocolStr, mIPStr,
                    mMaskStr, mGwStr, mBroadcast, mDhcp, mDhcpstart, mDhcpend,
                    mRenttime, this);
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_ROUTESET_LAN_SET, object,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "提交的数据----->" + response.toString());
                            /*Toast.makeText(LanSetActivity.this,
									response.toString(), 0).show();*/
                            //loginDataQuery(response);
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                    Toast.makeText(LanSetActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
            mRequestQueue.add(request);
        } else if (isIP == false || isMask == false) {
            Toast.makeText(LanSetActivity.this, "抱歉，IP 或者  MASK 有错！！", 0).show();
        }
    }

    // 一进页面开始加载 GET数据
    private void queryLanSet() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_ROUTESET_LAN_QUERT, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收到数据-->" + response.toString());
					/*	Toast.makeText(LanSetActivity.this,
								response.toString(), 0).show();*/
                        loginDataQuery(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(LanSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    // 获取LAN口状态
    private void loginDataQuery(JSONObject response) {
        Log.e(TAG, "code===0?===>" + XTHttpJSON.getJSONString(response.toString()));
        String code = XTHttpJSON.getJSONString(response.toString());
        if ("0".equals(code)) {
            try {
                //JSONObject object = response.getJSONObject("data");
                mProtocolInt = response.getInt("type");
                mIPStr = response.getString("ip");

                //校验接收到的IP
                if (ChechIpMask.judgeIpIsLegal(mIPStr)) {
                    mIPString = mIPStr;
                } else {
                    mIPString = null;
                    Toast.makeText(LanSetActivity.this, "接受的IP的格式不准确！！", 0).show();
                }

                mMaskStr = response.getString("mask");
                //校验接收到的子网掩码
                if (ChechIpMask.judgeIpIsLegal(mMaskStr)) {
                    mMaskString = mMaskStr;
                } else {
                    mMaskString = null;
                    Toast.makeText(LanSetActivity.this, "接受的IP的格式不准确！！", 0).show();
                }

                mGwStr = response.getString("gw");
                mBroadcast = response.getString("broadcast");

                mDhcp = response.getString("dhcp");
                Log.e(TAG, "mDhcp===0?===>" + mDhcp);

                if (mDhcp.equals("0")) {//  0就是 显示
                    mDhcpstart = response.getString("dhcpstart");
                    mDhcpend = response.getString("dhcpend");
                    mRenttime = response.getString("renttime");

                    mLanset_dhcp_onff.setChecked(true);
                    mLanset_dhcp_start.setText(mDhcpstart);
                    mLanset_dhcp_pend.setText(mDhcpend);
                    mLanset_dhcp_renttime.setText(mRenttime);
                } else {
                    //Toast.makeText(LanSetActivity.this, "mDhcp.equals(1)", 0).show();
                }
                upDataUi();// 更新Ui
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals(
                "-1")) {
            Toast.makeText(LanSetActivity.this, "访问失败", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals(
                "1")) {
            Toast.makeText(LanSetActivity.this, "从未设置过", 0).show();
        }
        Log.e(TAG, "loginDataQuery结束！！");
    }

    // 更新UI
    private void upDataUi() {
        LanSetActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //	mLan_protocol.setSelection(mProtocolInt);
                mLanset_ipaddress.setText(mIPString);
                mLanset_subnet_code.setText(mMaskString);
                //	mlanset_gateway01.setText(mGwStr);
                mLanset_gateway02.setText(mBroadcast);
                if (mDhcp.equals("0")) {
                    mLanset_dhcp_onff.setChecked(true);
                    mDhcpTag = true;
                } else if (mDhcp.equals("1")) {
                    mLanset_dhcp_onff.setChecked(false);
                    mLanset_dhcp_start.setEnabled(false);
                    mLanset_dhcp_pend.setEnabled(false);
                    mLanset_dhcp_renttime.setEnabled(false);
                    mDhcpTag = false;
                }
            }
        });
    }

    private void showDia() {
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(true);
        pd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progresshow = false;
            }
        });
        pd.setMessage("正在加载数据。。。。。");
        pd.show();
    }

    // ///////////////////Swith////////////////////////////////////
    @SuppressLint("ResourceAsColor")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mLanset_dhcp_onff.setChecked(true);
            mLanset_dhcp_start.setEnabled(true);
            mLanset_dhcp_pend.setEnabled(true);
            mLanset_dhcp_renttime.setEnabled(true);
            mDhcpTag = true;
            mDhcp = "0";

        } else {
            mLanset_dhcp_onff.setChecked(false);
            mLanset_dhcp_start.setEnabled(false);
            mLanset_dhcp_pend.setEnabled(false);
            mLanset_dhcp_renttime.setEnabled(false);

            mDhcpTag = false;
            mDhcp = "1";
            mDhcpstart = "";
            mDhcpend = "";
            mRenttime = "";
        }
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 退出页面
    public void lanSetOnFinish(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.cancel();
        super.onDestroy();
    }
}
