package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.adapter.BwInterferAdapter;
import com.my51c.see51.app.adapter.BwInterferAdapter.OnListRemovedListener;
import com.my51c.see51.app.domian.InteferNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*接口列表*/
public class BwSuperInteferActivity extends BaseActivity implements OnListRemovedListener {

    protected static final String TAG = "BwSuperInteferActivity";
    public static List<String> nameList = new ArrayList<String>();
    Handler mHandler = new Handler();
    private PullToRefreshListView mListView;
    private BwInterferAdapter mAdapter;
    private List<InteferNum> mInterList;
    private RequestQueue mRequestQueue;
    private InteferNum mInterNum;
    private int i;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw_interface_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        bwInterfaceQuery();
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.bwinterfer_listview);
        if (mInterList == null) {
            mInterList = new ArrayList<InteferNum>();
        }
        mAdapter = new BwInterferAdapter(mInterList, this, i, mRequestQueue);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnListRemovedListener(this);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                mAdapter.notifyDataSetChanged();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 5000);
            }
        });
    }

    private void bwInterfaceQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_BWSET_ADV_INTFER_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        Toast.makeText(BwSuperInteferActivity.this,
                                response.toString(), 0).show();
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(BwSuperInteferActivity.this, "网络错误",
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
            Toast.makeText(BwSuperInteferActivity.this, "访问成功", 0).show();
            try {
                int number = response.getInt("intfersum");
                for (int i = 1; i <= number; i++) {
                    mInterNum = new InteferNum();
                    JSONObject limitlObject = response.getJSONObject("intfers"
                            + i);
                    String name = limitlObject.getString("name");
                    int ipssum = limitlObject.getInt("ipssum");
                    for (int j = 1; j <= ipssum; j++) {
                        if (j == 1) {
                            String ip1 = limitlObject.getString("ip" + 1);
                            mInterNum.setIp1(ip1);
                        }
                        if (j == 2) {
                            String ip2 = limitlObject.getString("ip" + 2);
                            mInterNum.setIp2(ip2);
                        }
                        if (j == 3) {
                            String ip3 = limitlObject.getString("ip" + 3);
                            mInterNum.setIp3(ip3);
                        }
                        if (j == 4) {
                            String ip4 = limitlObject.getString("ip" + 4);
                            mInterNum.setIp4(ip4);
                        }
                        if (j == 5) {
                            String ip5 = limitlObject.getString("ip" + 5);
                            mInterNum.setIp5(ip5);
                        }
                    }
                    String ipsnum = limitlObject.getString("ipsnum");
                    String count = limitlObject.getString("count");
                    String timeout = limitlObject.getString("timeout");
                    String interval = limitlObject.getString("interval");
                    String lost = limitlObject.getString("lost");
                    String connect = limitlObject.getString("connect");
                    mInterNum.setName(name);
                    mInterNum.setIpssum(ipssum);
                    mInterNum.setIpsnum(ipsnum);
                    mInterNum.setCount(count);
                    mInterNum.setTimeout(timeout);
                    mInterNum.setInterval(interval);
                    mInterNum.setLost(lost);
                    mInterNum.setConnect(connect);
                    mInterNum.setInterferTag(true);
                    mInterList.add(mInterNum);

                    nameList.add(name);
                }
                for (String nm : nameList) {
                    Log.e(TAG, "现在存在的名字列表为----------->" + nm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BwSuperInteferActivity.this, "访问失败", 0).show();
        }
    }

    //点击添加条目(加号)监听
    public void bwAddRow(View view) {
        i++;
        mInterNum = new InteferNum();
        mInterNum.setName("name0" + i);
        mInterNum.setInterferTag(false);
        mInterNum.setIpssum(5);//20160801  by  hyw
        mInterList.add(mInterNum);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        i = 0;
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

    public void bwInterferOnFinish(View v) {
        finish();
    }

    @Override
    public void onRemoved() {
        mAdapter.notifyDataSetChanged();
    }

}
