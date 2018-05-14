package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
import com.my51c.see51.app.adapter.BlackListAdapter;
import com.my51c.see51.app.domian.BlackList;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlackListActivity extends BaseActivity {
    protected static final String TAG = "BlackListActivity";
    public static List mCurrBlackUsers = new ArrayList();
    Handler mHandler = new Handler();
    private PullToRefreshListView mBlackList;
    private BlackListAdapter mAdapter;
    private List<BlackList> mBlackLists;
    private List<String> mBlcakIpStr;
    private List<String> mBlcakMacStr;
    private List<String> mBlackIpStrAdd;
    private List<String> mBlackMacStrAdd;
    private RequestQueue mRequestQueue;
    private BlackList mBList;
    private int i;


	/*private BlackList mBList1;
    private BlackList mBList2;
	private BlackList mBList3;
	private BlackList mBList4;
	private BlackList mBList5;*/
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netops_blacklist_activity);
        TextView black_shezhi = (TextView) findViewById(R.id.black_shezhi);
        black_shezhi.setTypeface(AppData.fontXiti);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        mBlackList = (PullToRefreshListView) findViewById(R.id.blacklist_listview);
        if (mBlackLists == null) {
            mBlackLists = new ArrayList<BlackList>();
        }
        if (mBlcakIpStr == null) {
            mBlcakIpStr = new ArrayList<String>();
        }
        if (mBlcakMacStr == null) {
            mBlcakMacStr = new ArrayList<String>();
        }
        if (mBlackIpStrAdd == null) {
            mBlackIpStrAdd = new ArrayList<String>();
        }
        if (mBlackMacStrAdd == null) {
            mBlackMacStrAdd = new ArrayList<String>();
        }
        blackListQuery();

        mAdapter = new BlackListAdapter(mBlackLists, this, i, mRequestQueue,
                mBlcakIpStr, mBlcakMacStr, mBlackIpStrAdd, mBlackMacStrAdd);
        mBlackList.setAdapter(mAdapter);
        mBlackList.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mAdapter.notifyDataSetChanged();

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mBlackList.onRefreshComplete();
                    }
                }, 5000);
            }
        });
    }

    private void blackListQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_BLACK_LIST, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());
						/*Toast.makeText(BlackListActivity.this,
								response.toString(), 0).show();*/
                pdDismiss(response);
                loadData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(BlackListActivity.this, "网络错误",
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
            Toast.makeText(BlackListActivity.this, "访问成功", 0).show();
            try {
                int blackIpNum = response.getInt("num");
                for (int i = 1; i <= blackIpNum; i++) {
                    JSONObject blcakObject = response
                            .getJSONObject("black" + i);
                    String blcakIpStr = blcakObject.getString("ip");
                    String blcakMacStr = blcakObject.getString("mac");
                    String blcakUserStr = blcakObject.getString("user");
                    mCurrBlackUsers.add(blcakUserStr);//
                    mBList = new BlackList();
                    mBList.setUser(blcakUserStr);
                    mBList.setBlackListTag(true);
                    mBList.setBlackDelTag(0);
                    mBlcakIpStr.add(blcakIpStr);
                    mBlcakMacStr.add(blcakMacStr);
                    mBlackLists.add(mBList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BlackListActivity.this, "访问失败", 0).show();
        }
    }

    private void blackListAdd() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_AllIP_QUERY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());
						/*Toast.makeText(BlackListActivity.this,
								response.toString(), 0).show();*/
                allIpData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(BlackListActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    protected void allIpData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(BlackListActivity.this, "访问成功", 0).show();
            int num;
            try {
                num = response.getInt("num");
                for (int i = 1; i <= num; i++) {
                    JSONObject ipJSONObject02 = response
                            .getJSONObject("ip" + i);
                    String superIp = ipJSONObject02.getString("ip");
                    String superMac = ipJSONObject02.getString("mac");
                    String superUser = ipJSONObject02.getString("user");
                    // mBList.setIp(superIp);
                    // mBList.setMac(superIp);
                    // mBList.setUser(superUser);
                    mBlackIpStrAdd.add(superIp);
                    mBlackMacStrAdd.add(superMac);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BlackListActivity.this, "访问失败", 0).show();
        }
    }

    public void addBlackItemOnClick(View v) {
        i++;
        mBList = new BlackList();
        mBList.setUser("bbbb" + i);
        mBList.setBlackListTag(false);
        mBList.setBlackDelTag(1);
        mBlackLists.add(mBList);
        blackListAdd();
        mAdapter.notifyDataSetChanged();
    }

    public void blackListOnFinish(View v) {
        finish();
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
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

}
