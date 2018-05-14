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
import com.my51c.see51.app.adapter.BwMenberAdapter;
import com.my51c.see51.app.adapter.BwMenberAdapter.OnListRemovedListener;
import com.my51c.see51.app.domian.BwMenberNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*成员*/
public class BwMenberActivity extends BaseActivity implements OnListRemovedListener {
    protected static final String TAG = "BwMenberActivity";
    public static List<String> nameList = new ArrayList<String>();//存储
    Handler mHandler = new Handler();
    private PullToRefreshListView mListView;
    private BwMenberAdapter mAdapter;
    private List<BwMenberNum> mBwMenberNums;
    private List<String> mStrings;
    private RequestQueue mRequestQueue;
    private BwMenberNum mBwMenberNum;
    private int i;
    private int number;
    private boolean statuTag;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw_member_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        bwMenberQuery();
        statuTag = false;
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.bwmember_listview);
        if (mBwMenberNums == null) {
            mBwMenberNums = new ArrayList<BwMenberNum>();
        }
        if (mStrings == null) {
            mStrings = new ArrayList<String>();
        }
        mAdapter = new BwMenberAdapter(mBwMenberNums, this, i, mRequestQueue,
                number, mStrings, statuTag);
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

    private void bwMenberQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_BWSET_AVD_MEMBER_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        Toast.makeText(BwMenberActivity.this,
                                response.toString(), 0).show();
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(BwMenberActivity.this, "网络错误",
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
            Toast.makeText(BwMenberActivity.this, "访问成功", 0).show();
            try {
                number = response.getInt("memsum");
                for (int i = 1; i <= number; i++) {
                    JSONObject memberObject = response.getJSONObject("member"
                            + i);
                    String name = memberObject.getString("name");
                    String intfer = memberObject.getString("intfer");
                    String metric = memberObject.getString("metric");
                    String weight = memberObject.getString("weight");
                    mBwMenberNum = new BwMenberNum();
                    mBwMenberNum.setName(name);
                    mBwMenberNum.setIntfer(intfer);
                    mBwMenberNum.setMetric(metric);
                    mBwMenberNum.setWeight(weight);
                    mBwMenberNum.setMemberTag(false);
                    mStrings.add(mBwMenberNum.getIntfer());
                    mBwMenberNums.add(mBwMenberNum);

                    nameList.add(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BwMenberActivity.this, "访问失败", 0).show();
        }
    }

    // 点击添加条目(加号监听)
    public void bwAddMember(View view) {
        i++;
        mBwMenberNum = new BwMenberNum();
        mBwMenberNum.setName("aaa" + i);
        mBwMenberNum.setIntfer("wan0");
        statuTag = true;
        mBwMenberNum.setMemberTag(true);
        mBwMenberNums.add(mBwMenberNum);
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

    // 点击退出页面
    public void bwMemberOnFinish(View v) {
        finish();
    }

    @Override
    public void onRemoved() {
        mAdapter.notifyDataSetChanged();
    }

}
