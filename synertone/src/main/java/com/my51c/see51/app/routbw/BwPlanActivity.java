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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.adapter.PlanArrayAdapter;
import com.my51c.see51.app.adapter.PlanArrayAdapter.OnListRemovedListener;
import com.my51c.see51.app.domian.BwPlanNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*策略*/
public class BwPlanActivity extends BaseActivity implements OnListRemovedListener {

    protected static final String TAG = "BwPlanActivity";
    public static List<String> nameList = new ArrayList<String>();//存储
    Handler mHandler = new Handler();
    private PlanArrayAdapter mAdapter;
    private PullToRefreshListView mListView;
    private List<BwPlanNum> mBwPlanNums;
    private List<String> mStrings;
    private RequestQueue mRequestQueue;
    private BwPlanNum mBwPlanNum;
    private int i;
    private int mPag;
    private int number;
    private boolean bwPlanTag;
    private String code, name, memsum, mem;
    private int policysel;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw_plan_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        bwPlanTag = false;
        bwQuery();
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.bwplan_listview);
        if (mBwPlanNums == null) {
            mBwPlanNums = new ArrayList<BwPlanNum>();
        }
        if (mStrings == null) {
            mStrings = new ArrayList<String>();
        }
        mAdapter = new PlanArrayAdapter(mBwPlanNums, this, i, mRequestQueue, mStrings, mPag);
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

    private void bwQuery() {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                XTHttpUtil.GET_BWSET_AVD_POLICY_QUERY,
                new RequestCallBack<String>() {

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
                    public void onSuccess(ResponseInfo<String> response) {
                        Log.i(TAG, "接收到数据-->" + response.result);
                        pdDismiss(response);
                        loginDataQuery(response.result);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
    }

    protected void loginDataQuery(String string) {
        if (XTHttpJSON.getJSONString(string).equals("0")) {
            Toast.makeText(this, "请求成功", 0).show();
            try {
                JSONObject object = new JSONObject(string);
                int sum = object.getInt("sum");
                if (sum == 0 || sum < 0) {
                    return;
                }
                for (int i = 1; i <= sum; i++) {
                    JSONObject policysObject = object.getJSONObject("policys"
                            + i);
                    name = policysObject.getString("name");
                    mBwPlanNum = new BwPlanNum();
                    mBwPlanNum.setName(name);
                    mBwPlanNum.setBwPlanTag(bwPlanTag);
                    int memsum = policysObject.getInt("memsum");
                    mBwPlanNum.setMemsum(memsum);
                    for (int j = 1; j <= memsum; j++) {
                        if (j == 1) {
                            mem = policysObject.getString("mem" + j);
                            mBwPlanNum.setMem1(mem);
                        }
                        if (j == 2) {
                            mem = policysObject.getString("mem" + j);
                            mBwPlanNum.setMem2(mem);
                        }
                        if (j == 3) {
                            mem = policysObject.getString("mem" + j);
                            mBwPlanNum.setMem3(mem);
                        }
                        if (j == 4) {
                            mem = policysObject.getString("mem" + j);
                            mBwPlanNum.setMem4(mem);
                        }
                        if (j == 5) {
                            mem = policysObject.getString("mem" + j);
                            mBwPlanNum.setMem5(mem);
                        }
                        mStrings.add(mem);
                    }
                    policysel = policysObject.getInt("policysel");
                    mBwPlanNum.setPolicysel(policysel);
                    mBwPlanNum.setmPag(0);
                    mBwPlanNums.add(mBwPlanNum);

                    nameList.add(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(string).equals("-1")) {
            Toast.makeText(this, "请求失败", 0).show();
        }
    }

    // 点击添加条目
    public void bwAddPlan(View view) {
        i++;
        mBwPlanNum = new BwPlanNum();
        mBwPlanNum.setName("name" + i);
        bwPlanTag = true;
        mBwPlanNum.setmPag(0);
        mBwPlanNum.setBwPlanTag(bwPlanTag);
        mBwPlanNums.add(mBwPlanNum);
        mAdapter.notifyDataSetChanged();
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

    // 点击离开页面
    public void bwPlanOnFinish(View v) {
        finish();
    }

    @Override
    public void onRemoved() {
        mAdapter.notifyDataSetChanged();
    }

}
