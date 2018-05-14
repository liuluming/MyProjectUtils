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
import com.my51c.see51.app.adapter.WhileListAdapter;
import com.my51c.see51.app.domian.WhileList;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*白名单设置*/
public class WhileListActivity extends BaseActivity {
    protected static final String TAG = "IPSpeedActivity";
    public static List mCurrWhiteUsers = new ArrayList();
    Handler mHandler = new Handler();
    private PullToRefreshListView mWhileList;
    private WhileListAdapter mAdapter;
    private List<WhileList> mWhileLists;
    private List<String> mIpStrings;
    private List<String> mMacStrings;
    private List<String> mIpStringAdd;
    private List<String> mMacStringAdd;
    private RequestQueue mRequestQueue;
    private WhileList mWList;
    private int i;
    private boolean whileListTag;
    private WhileList mWList1;
    private WhileList mWList2;
    private WhileList mWList3;
    private WhileList mWList4;
    private WhileList mWList5;
    private TextView tv_baimingdan;//设置字体，勿删
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netops_whilelist_activity);
        tv_baimingdan = (TextView) findViewById(R.id.tv_baimingdan);
        tv_baimingdan.setTypeface(AppData.fontXiti);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        whileListTag = true;
        initView();
    }

    private void initView() {
        mWhileList = (PullToRefreshListView) findViewById(R.id.whilelist_listview);
        whileListQuery();
        if (mWhileLists == null) {
            mWhileLists = new ArrayList<WhileList>();
        }
        if (mIpStrings == null) {
            mIpStrings = new ArrayList<String>();
        }
        if (mMacStrings == null) {
            mMacStrings = new ArrayList<String>();

        }
        if (mIpStringAdd == null) {
            mIpStringAdd = new ArrayList<String>();

        }
        if (mMacStringAdd == null) {
            mMacStringAdd = new ArrayList<String>();
        }

        //这里是模拟数据--------------------------------------------------
    /*	mWList1 = new WhileList();
		mWList1.setUser("wwww" + i);
		mWList1.setWhileListTag(false);
		mWList1.setWhileTag(1);
		mWhileLists.add(mWList1);
		mWList2 = new WhileList();
		mWList2.setUser("ssss" + i);
		mWList2.setWhileListTag(false);
		mWList2.setWhileTag(1);
		mWhileLists.add(mWList2);
		mWList3 = new WhileList();
		mWList3.setUser("aaaa" + i);
		mWList3.setWhileListTag(false);
		mWList3.setWhileTag(1);
		mWhileLists.add(mWList3);
		mWList4 = new WhileList();
		mWList4.setUser("oooo" + i);
		mWList4.setWhileListTag(false);
		mWList4.setWhileTag(1);
		mWhileLists.add(mWList4);
		mWList5 = new WhileList();
		mWList5.setUser("kkkk" + i);
		mWList5.setWhileListTag(false);
		mWList5.setWhileTag(1);
		mWhileLists.add(mWList5);*/
        //--------------------------------------------------------------------------------


        mAdapter = new WhileListAdapter(mWhileLists, this, i, mRequestQueue,
                mIpStrings, mMacStrings, mIpStringAdd, mMacStringAdd);
        mWhileList.setAdapter(mAdapter);

        mWhileList.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mAdapter.notifyDataSetChanged();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mWhileList.onRefreshComplete();
                    }
                }, 5000);
            }
        });
    }

    private void whileListQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_WHILE_LIST, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());
						/*Toast.makeText(WhileListActivity.this,
								response.toString(), 0).show();*/
                loadData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WhileListActivity.this, "网络错误",
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
            Toast.makeText(WhileListActivity.this, "访问成功", 0).show();
            try {
                int num = response.getInt("num");
                for (int i = 1; i <= num; i++) {
                    mWList = new WhileList();
                    JSONObject ipJSONObject01 = response.getJSONObject("super"
                            + i);
                    String superIp = ipJSONObject01.getString("ip");
                    String superMac = ipJSONObject01.getString("mac");
                    String superUser = ipJSONObject01.getString("user");

                    mCurrWhiteUsers.add(superUser);

                    mWList.setIp(superIp);
                    mWList.setMac(superIp);
                    mWList.setUser(superUser);
                    mWList.setWhileListTag(true);
                    mWList.setWhileTag(0);
                    mWhileLists.add(mWList);
                    mIpStrings.add(superIp);
                    mMacStrings.add(superMac);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(WhileListActivity.this, "访问失败", 0).show();
        }
    }

    private void whileListAdd() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_AllIP_QUERY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());
						/*Toast.makeText(WhileListActivity.this,
								response.toString(), 0).show();*/
                allIpData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(WhileListActivity.this, "网络错误",
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
            Toast.makeText(WhileListActivity.this, "访问成功", 0).show();
            int num;
            try {
                num = response.getInt("num");
                for (int i = 1; i <= num; i++) {
                    mWList = new WhileList();
                    JSONObject ipJSONObject02 = response.getJSONObject("ip" + i);
                    String superIp = ipJSONObject02.getString("ip");
                    String superMac = ipJSONObject02.getString("mac");
                    String superUser = ipJSONObject02.getString("user");
//					mWList.setIp(superIp);
//					mWList.setMac(superIp);
//					mWList.setUser(superUser);
                    //	mWList.setWhileListTag(true);
                    //	mWhileLists.add(mWList);
                    mIpStringAdd.add(superIp);
                    mMacStringAdd.add(superMac);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(WhileListActivity.this, "访问失败", 0).show();
        }
    }

    public void addWhileItemOnClick(View v) {
        i++;
        mWList = new WhileList();
        mWList.setUser("wwww" + i);
        mWList.setWhileListTag(false);
        mWList.setWhileTag(1);
        whileListAdd();
        mWhileLists.add(mWList);
        mAdapter.notifyDataSetChanged();
    }

    public void whileListOnFinish(View v) {
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
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }
}
