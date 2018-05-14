package com.my51c.see51.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
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
import com.my51c.see51.app.activity.IPSpeedActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BlackListActivity;
import com.my51c.see51.app.routbw.QosBaseSetActivity;
import com.my51c.see51.app.routbw.WhileListActivity;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

/*带宽管理*/
public class NetWorkOptActivity extends BaseActivity implements OnClickListener,
        OnItemSelectedListener {
    protected static final String TAG = "NetWorkOptActivity";
    private Spinner mNetOptSpinner;
    private RequestQueue mRequestQueue;
    private LinearLayout mNetwork_opt_qosset, mNetwork_opt_ipspeed,
            mNetwork_opt_whilelist, mNetwork_opt_blacklist;
    private String tag = "2";
    private TextView rout_tv1, qos_set, ipspeed_set, white_set, black_set, moshi_choose;
    private Intent mIntent;
    private String getBwMangerStr, getBwMangerNum;
    /* 查询带宽管理默认是哪个选项 */
    private String bwqueryStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_optiminzation_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        tag = "1";
        initView();
        bwMangerQuery();
    }

    private void initView() {
        rout_tv1 = (TextView) findViewById(R.id.rout_tv1);
        rout_tv1.setTypeface(AppData.fontXiti);
        qos_set = (TextView) findViewById(R.id.qos_set);
        qos_set.setTypeface(AppData.fontXiti);
        ipspeed_set = (TextView) findViewById(R.id.ipspeed_set);
        ipspeed_set.setTypeface(AppData.fontXiti);
        white_set = (TextView) findViewById(R.id.white_set);
        white_set.setTypeface(AppData.fontXiti);
        black_set = (TextView) findViewById(R.id.black_set);
        black_set.setTypeface(AppData.fontXiti);
        moshi_choose = (TextView) findViewById(R.id.moshi_choose);
        moshi_choose.setTypeface(AppData.fontPutu);

        mNetOptSpinner = (Spinner) findViewById(R.id.netwodrk_opt_spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.networkopt_bwmanger));
        mNetOptSpinner.setAdapter(adapter);
        mNetwork_opt_qosset = (LinearLayout) findViewById(R.id.network_opt_qosset);
        mNetwork_opt_ipspeed = (LinearLayout) findViewById(R.id.network_opt_ipspeed);
        mNetwork_opt_whilelist = (LinearLayout) findViewById(R.id.network_opt_whilelist);
        mNetwork_opt_blacklist = (LinearLayout) findViewById(R.id.network_opt_blacklist);

        mNetOptSpinner.setOnItemSelectedListener(this);
        mNetwork_opt_qosset.setOnClickListener(this);
        mNetwork_opt_ipspeed.setOnClickListener(this);
        mNetwork_opt_whilelist.setOnClickListener(this);
        mNetwork_opt_blacklist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.network_opt_qosset:
                mIntent = new Intent(NetWorkOptActivity.this,
                        QosBaseSetActivity.class);
                startActivity(mIntent);
                break;
            case R.id.network_opt_ipspeed:
                mIntent = new Intent(NetWorkOptActivity.this,
                        IPSpeedActivity.class);
                startActivity(mIntent);
                break;
            case R.id.network_opt_whilelist:
                mIntent = new Intent(NetWorkOptActivity.this,
                        WhileListActivity.class);
                startActivity(mIntent);
                break;
            case R.id.network_opt_blacklist:
                mIntent = new Intent(NetWorkOptActivity.this,
                        BlackListActivity.class);
                startActivity(mIntent);
                //Toast.makeText(this,"你点击了黑名单",0).show();
                break;
        }

    }

    // ///////////////////////mNetWorkSprinner改变的方法////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        getBwMangerStr = mNetOptSpinner.getSelectedItem().toString();

        if (tag.equals("2")) {
            if (getBwMangerStr.equals("带宽平均")) {
                getBwMangerNum = "1";
            } else if (getBwMangerStr.equals("网页优先")) {
                getBwMangerNum = "2";
            } else if (getBwMangerStr.equals("下载优先")) {
                getBwMangerNum = "3";
            }
            JSONObject object = XTHttpJSON.postBwMangerSet(getBwMangerNum);
            bwMangerPost(object);
        } else {
            tag = "2";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Post带宽管理
    private void bwMangerPost(JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_NETOPT_BWMANGER_SET, object,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        if (XTHttpJSON.getJSONString(response.toString())
                                .equals("0")) {
                            Toast.makeText(NetWorkOptActivity.this, "访问成功",
                                    Toast.LENGTH_SHORT).show();
                        } else if (XTHttpJSON.getJSONString(
                                response.toString()).equals("-1")) {
                            Toast.makeText(NetWorkOptActivity.this, "访问失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkOptActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    private void bwMangerQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_NETOPT_BWMANGER_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                    /*	Toast.makeText(NetWorkOptActivity.this,
								response.toString(), 0).show();*/
                        loadData(response);// 加载数据
                        tag = "1";
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(NetWorkOptActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(NetWorkOptActivity.this, "访问成功", Toast.LENGTH_SHORT).show();
            try {
                bwqueryStr = response.getString("bwquery");
                updataUi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals(
                "-1")) {
        }
    }

    // 查询 获取数据更新UI
    private void updataUi() {
        NetWorkOptActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bwqueryStr.equals("1")) {
                    mNetOptSpinner.setSelection(0);
                } else if (bwqueryStr.equals("2")) {
                    mNetOptSpinner.setSelection(1);
                } else if (bwqueryStr.equals("3")) {
                    mNetOptSpinner.setSelection(2);
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.dismiss();
        super.onDestroy();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 点击退出页面
    public void netWorkoptOnFinish(View v) {
        finish();
    }

}
