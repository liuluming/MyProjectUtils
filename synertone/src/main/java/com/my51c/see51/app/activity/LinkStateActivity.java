package com.my51c.see51.app.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.my51c.see51.app.domian.WansData;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;

/*链路状态*/
public class LinkStateActivity extends BaseActivity {
    private final static String TAG = "LinkStateActivity";
    private TextView mLinkstat_weixin_onoff, mLinkstat_weixin_num, mLinkstat_weixin_upnum, mLinkstat_weixin_downnum,
            mLinkstat_weixin_num_tv, mLinkstat_weixin_up_tv, mLinkstat_weixin_down_tv, mLinkstat_weixin_up_mps,
            mLinkstat_weixin_down_mps, mLinkstat_weixin_num_unit;
    private TextView mLinkStat34gOnOff, mLinkStatSim, mLinkSimkaSys, mPublic34gUpNum, mPublic34gDownNum, mLinkStatSimka,
            mLinkStatSimsysTv, mLinkStatSim3gMPSUp, mLinkStatSim3gMPSDown, mLinkStatSim3gDown, mLinkStatSim3gUp;
    private TextView mLinkStat_WiFi_OnOff, mLinkStat_Wifi_Name, mLinkStat_Wifi_Up, mLinkStat_Wifi_Down,
            mLinkStat_WiFi_Name_Tv, mLinkStat_WiFi_Up_MPS, mLinkStat_WiFi_Down_MPS, mLinkStat_WiFi_Down_Tv,
            mLinkStat_WiFi_Up_Tv;
    // private Gson mLinkGson;// Gson
    private RequestQueue mRequestQueue;// Volly的消息队列
    private ArrayList<WansData> wansData;
    private WansData mWan;
    private TextView mWanTv01, mWanTv02, mWanTv03, mWanTv04, mWanTv05;
    private Button mWanBtn01, mWanBtn02, mWanBtn03, mWanBtn04, mWanBtn05;
    private ToggleButton mLink_satellite_onoff, mLink_sim_onoff, mLink_wifi_onoff;
    private LinearLayout mLink_weixing_ll, mLink_sim_ll, mLink_wifi_ll;
    // 卫星网络的Text
    private String satestatusEnable, satestatusUpspeed, satenum, satestatusDownspeed;
    // 3G/4g公网
    private String simstatusEnable, simstatusNum, simstatusMode, simUpspeed, simDowns;
    // WIFI状态
    private String wifistatusEnable, wifistatussid, wifistatusUpspeed, wifistatusDowns;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    /*
     * public ArrayList<Satestatus> satestatus;// 卫星网络 public
     * ArrayList<Simstatus> simstatus;// 3G4G public ArrayList<Wifistatus>
     * wifistatus;// wifi状态 public ArrayList<Wans> wans;// WAN口在线状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_state_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        mLink_weixing_ll = (LinearLayout) findViewById(R.id.link_weixing_ll);
        mLink_sim_ll = (LinearLayout) findViewById(R.id.link_sim_ll);
        mLink_wifi_ll = (LinearLayout) findViewById(R.id.link_wifi_ll);
        mLink_satellite_onoff = (ToggleButton) findViewById(R.id.link_satellite_onoff);// 卫星网络是否开启按钮
        // mLinkstat_weixin_onoff = (TextView)
        // findViewById(R.id.linkstate_weixin_onoff);// 卫星网络是否开启

        TextView link_34g_xiambs = (TextView) findViewById(R.id.link_34g_xiambs);
        TextView lianluzhuangtai = (TextView) findViewById(R.id.lianluzhuangtai);
        lianluzhuangtai.setTypeface(AppData.fontXiti);
        TextView weixin_net = (TextView) findViewById(R.id.weixin_net);
        weixin_net.setTypeface(AppData.fontXiti);

        TextView link_textView5 = (TextView) findViewById(R.id.link_textView5);
        link_textView5.setTypeface(AppData.fontXiti);
        TextView link_textView10 = (TextView) findViewById(R.id.link_textView10);
        link_textView10.setTypeface(AppData.fontXiti);
        TextView weixin_wlan_status = (TextView) findViewById(R.id.weixin_wlan_status);
        weixin_wlan_status.setTypeface(AppData.fontXiti);

        mLinkstat_weixin_num = (TextView) findViewById(R.id.linkstat_weixin_num);// 卫星网络编号
        mLinkstat_weixin_upnum = (TextView) findViewById(R.id.weixin_up_number);// 卫星网洛上行
        mLinkstat_weixin_downnum = (TextView) findViewById(R.id.weixin_down_number);// 卫星网络下行
        mLinkstat_weixin_num_tv = (TextView) findViewById(R.id.linkstat_weixin_number);// 卫星编号
        mLinkstat_weixin_num_tv.setTypeface(AppData.fontXiti);                                                                                // 文字
        mLinkstat_weixin_up_tv = (TextView) findViewById(R.id.link_textView3);// 上行
        mLinkstat_weixin_up_tv.setTypeface(AppData.fontXiti);                                                                        // 文字
        mLinkstat_weixin_down_tv = (TextView) findViewById(R.id.link_textView1);// 下行
        mLinkstat_weixin_down_tv.setTypeface(AppData.fontXiti);                                                            // 文字
        mLinkstat_weixin_up_mps = (TextView) findViewById(R.id.weixin_mps_up);// 上行
        mLinkstat_weixin_down_mps = (TextView) findViewById(R.id.weinxin_unit);// 下行
        mLinkstat_weixin_num_unit = (TextView) findViewById(R.id.weixin_weixin_num_unit);// 卫星编号
        // 号
        // 文字

        // two

        mLink_sim_onoff = (ToggleButton) findViewById(R.id.link_sim_onoff);// 34G是否开启按钮
        // mLinkStat34gOnOff = (TextView)
        // findViewById(R.id.linkstate_34g_onff);// 34G是否开启
        mLinkStatSim = (TextView) findViewById(R.id.linkstat_sim);// 34G卡槽
        mLinkSimkaSys = (TextView) findViewById(R.id.linkstat_simka_sys);// 34G制式
        mPublic34gUpNum = (TextView) findViewById(R.id.public_34g_up_number);// 卫星网络上行
        mPublic34gDownNum = (TextView) findViewById(R.id.public_34g_down_number);// 卫星网络下行
        mLinkStatSimka = (TextView) findViewById(R.id.linkstat_simka);// 卡槽 文字
        mLinkStatSimka.setTypeface(AppData.fontXiti);
        mLinkStatSimsysTv = (TextView) findViewById(R.id.linkstat_simsys_text);// 卡槽卡制式
        mLinkStatSimsysTv.setTypeface(AppData.fontXiti);                                                        // 文字
        mLinkStatSim3gMPSUp = (TextView) findViewById(R.id.weixin_mps_34g_mps_up);// 上行
        // mLinkStatSim3gMPSDown= (TextView)
        // findViewById(R.id.weixin_mps_34g_mps_down);//下行 MPS
        mLinkStatSim3gDown = (TextView) findViewById(R.id.weixin_mps_34g_down);// 下行
        mLinkStatSim3gDown.setTypeface(AppData.fontXiti);                                                                    // 文字
        mLinkStatSim3gUp = (TextView) findViewById(R.id.link_textView6);// 上行 文字
        mLinkStatSim3gUp.setTypeface(AppData.fontXiti);
        // three

        mLink_wifi_onoff = (ToggleButton) findViewById(R.id.link_wifi_onoff);// wifi网络是否开启按钮
        // mLinkStat_WiFi_OnOff = (TextView)
        // findViewById(R.id.linkstat_wifi_onoff);// wifi网络是否开启
        mLinkStat_Wifi_Name = (TextView) findViewById(R.id.linkstat_wifi_name);// wifi网络名字
        mLinkStat_Wifi_Up = (TextView) findViewById(R.id.linkstat_wifi_up);// wifi网络上行
        mLinkStat_Wifi_Down = (TextView) findViewById(R.id.linkstat_wifi_down);// wifi网络下行
        mLinkStat_WiFi_Name_Tv = (TextView) findViewById(R.id.link_wifi_text);// WiFi
        mLinkStat_WiFi_Name_Tv.setTypeface(AppData.fontXiti);                                                            // 名称
        // 文字
        mLinkStat_WiFi_Up_MPS = (TextView) findViewById(R.id.weixin_mps_wifi_mps_up);// 上行
        mLinkStat_WiFi_Down_MPS = (TextView) findViewById(R.id.weixin_mps_eifi_mps_down);// 下行
        mLinkStat_WiFi_Down_Tv = (TextView) findViewById(R.id.link_textView13);// 下行
        mLinkStat_WiFi_Down_Tv.setTypeface(AppData.fontXiti);                                                                // 文字
        mLinkStat_WiFi_Up_Tv = (TextView) findViewById(R.id.link_textView12);// 上行
        mLinkStat_WiFi_Up_Tv.setTypeface(AppData.fontXiti);                                                                // 文字

        mWanBtn01 = (Button) findViewById(R.id.linkstat_wan01);
        mWanBtn02 = (Button) findViewById(R.id.linkstat_wan02);
        mWanBtn03 = (Button) findViewById(R.id.linkstat_wan03);
        mWanBtn04 = (Button) findViewById(R.id.linkstat_wan04);
        mWanBtn05 = (Button) findViewById(R.id.linkstat_wan05);
        linkStatLoginData();
    }

    private void linkStatLoginData() {
        progresshow = true;
        showDia();
        JsonObjectRequest linkstatusRequest = new JsonObjectRequest(Method.GET, XTHttpUtil.GET_LINKSTATUS_STATE, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收return回来的数据===》" + response.toString());
                        pdDismiss(response);
                        //Toast.makeText(LinkStateActivity.this, response.toString(), 0).show();
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(LinkStateActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                // mLinkstat_weixin_onoff.setText("未开启");
                // mLinkStat34gOnOff.setText("未开启");
                // mLinkStat_WiFi_OnOff.setText("未连接");

                mLink_satellite_onoff.setChecked(false);
                mLink_sim_onoff.setChecked(false);
                mLink_wifi_onoff.setChecked(false);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(linkstatusRequest);
    }

    // 加载获取数据的 方法
    private void loadData(JSONObject json) {
        if (XTHttpJSON.getJSONString(json.toString()).equals("0")) {
            //Toast.makeText(LinkStateActivity.this, "查询成功 可以获取数据", 0).show();
            try {
                JSONObject satestatus = json.getJSONObject("satestatus");
                satestatusEnable = satestatus.getString("enable");// 0或者1
                satestatusUpspeed = satestatus.getString("upspeed");// 卫星设置的上行编号
                satestatusDownspeed = satestatus.getString("downspeed");// 卫星设置下行编号
                satenum = satestatus.getString("satenum");// 卫星编号

                JSONObject simstatus = json.getJSONObject("simstatus");
                simstatusEnable = simstatus.getString("enable");
                simstatusNum = simstatus.getString("simnum");
                simstatusMode = simstatus.getString("simmode");
                simUpspeed = simstatus.getString("upspeed");
                simDowns = simstatus.getString("downspeed");

                JSONObject wifistatus = json.getJSONObject("wifistatus");
                wifistatusEnable = wifistatus.getString("enable");
                wifistatussid = wifistatus.getString("ssid");
                wifistatusUpspeed = wifistatus.getString("upspeed");
                wifistatusDowns = wifistatus.getString("downspeed");

                int wansum = json.getInt("wansnum");

                wansData = new ArrayList<WansData>();
                for (int i = 0; i < wansum; i++) {
                    mWan = new WansData();
                    JSONObject object2 = json.getJSONObject("wan" + i);
                    mWan.setNum(object2.getString("num"));
                    mWan.setEnable(object2.getString("enable"));
                    mWan.setUpspeed(object2.getString("upspeed"));
                    mWan.setDownspeed(object2.getString("downspeed"));
                    wansData.add(mWan);
                }
                for (WansData wan : wansData) {
                    Log.i("adasdasdasda", wan.toString());
                }
                updateUi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 在主线程更新Ui
    private void updateUi() {
        LinkStateActivity.this.runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                if (satestatusEnable.equals("0")) {
                    // mLinkstat_weixin_onoff.setText("开启");
                    mLink_satellite_onoff.setChecked(true);
                    mLink_weixing_ll.setVisibility(View.VISIBLE);
                    mLinkstat_weixin_num.setText(satenum);
                    mLinkstat_weixin_upnum.setText(satestatusUpspeed);
                    mLinkstat_weixin_downnum.setText(satestatusDownspeed);
                } else if (satestatusEnable.equals("-1")) {
                    Toast.makeText(LinkStateActivity.this, "未开启", 0).show();
                    mLink_satellite_onoff.setChecked(false);
                    mLink_weixing_ll.setVisibility(View.GONE);

                }

                if (simstatusEnable.equals("0")) {
                    mLink_sim_onoff.setChecked(true);
                    mLink_sim_ll.setVisibility(View.VISIBLE);
                    mLinkStatSim.setText(simstatusNum);
                    mLinkSimkaSys.setText(simstatusMode);
                    mPublic34gUpNum.setText(simUpspeed);
                    mPublic34gDownNum.setText(simDowns);
                } else if (simstatusEnable.equals("-1")) {
                    mLink_sim_ll.setVisibility(View.GONE);
                    mLink_sim_onoff.setChecked(false);

                }

                if (wifistatusEnable.equals("0")) {
                    mLink_wifi_onoff.setChecked(true);
                    mLink_wifi_ll.setVisibility(View.VISIBLE);
                    mLinkStat_Wifi_Name.setText(wifistatussid);
                    mLinkStat_Wifi_Up.setText(wifistatusUpspeed);
                    mLinkStat_Wifi_Down.setText(wifistatusDowns);
                } else if (wifistatusEnable.equals("-1")) {
                    mLink_wifi_onoff.setChecked(false);
                    mLink_wifi_ll.setVisibility(View.GONE);

                }
                for (WansData wan : wansData) {
                    System.out.println(wan.toString());
                    if (wan.getNum().equals("0")) {
                        if (wan.getEnable().equals("0")) {
                            // mWanTv01.setBackgroundResource(R.color.greenyellow);
                            mWanBtn01.setBackgroundResource(R.drawable.wan1lv);
                            mWanTv01.setText(mWanTv01.getText().subSequence(0, 4)
                                    + getResources().getString(R.string.wan_status_online));
                        } else if (wan.getEnable().equals("-1")) {
                            // mWanTv01.setBackgroundResource(R.color.antiquewhite);
                            mWanBtn01.setBackgroundResource(R.drawable.wan1lan);
                        }
                    }
                    if (wan.getNum().equals("1")) {
                        if (wan.getEnable().equals("0")) {
                            // mWanTv02.setBackgroundResource(R.color.greenyellow);
                            mWanBtn02.setBackgroundResource(R.drawable.wan2lv);
                            mWanTv02.setText(mWanTv02.getText().subSequence(0, 4)
                                    + getResources().getString(R.string.wan_status_online));
                        } else if (wan.getEnable().equals("-1")) {
                            // mWanTv02.setBackgroundResource(R.color.antiquewhite);
                            mWanBtn02.setBackgroundResource(R.drawable.wan2lan);
                        }
                    }
                    if (wan.getNum().equals("2")) {
                        if (wan.getEnable().equals("0")) {
                            // mWanTv03.setBackgroundResource(R.color.greenyellow);
                            mWanBtn03.setBackgroundResource(R.drawable.wan3lv);
                            mWanTv03.setText(mWanTv03.getText().subSequence(0, 4)
                                    + getResources().getString(R.string.wan_status_online));
                        } else if (wan.getEnable().equals("-1")) {
                            mWanBtn03.setBackgroundResource(R.drawable.wan3lan);
                            // mWanTv03.setBackgroundResource(R.color.antiquewhite);
                        }
                    }
                    if (wan.getNum().equals("3")) {
                        if (wan.getEnable().equals("0")) {
                            mWanBtn04.setBackgroundResource(R.drawable.wan4lv);
                            // mWanTv04.setBackgroundResource(R.color.greenyellow);
                            mWanTv04.setText(mWanTv04.getText().subSequence(0, 4)
                                    + getResources().getString(R.string.wan_status_online));
                        } else if (wan.getEnable().equals("-1")) {
                            mWanBtn04.setBackgroundResource(R.drawable.wan4lan);
                            // mWanTv04.setBackgroundResource(R.color.antiquewhite);
                        }

                    }
                    if (wan.getNum().equals("4")) {
                        if (wan.getEnable().equals("0")) {
                            // mWanTv05.setBackgroundResource(R.color.greenyellow);
                            mWanBtn05.setBackgroundResource(R.drawable.wan5lv);
                            mWanTv05.setText(mWanTv05.getText().subSequence(0, 4)
                                    + getResources().getString(R.string.wan_status_online));
                        } else if (wan.getEnable().equals("-1")) {
                            mWanBtn05.setBackgroundResource(R.drawable.wan5lan);
                            // mWanTv05.setBackgroundResource(R.color.antiquewhite);
                        }
                    }

                }
            }
        });
    }

    // 点击退出
    public void linkStateOnFinish(View v) {
        finish();
    }

    // 退出Activity 调用的方法
    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.cancel();
        super.onDestroy();
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
}
