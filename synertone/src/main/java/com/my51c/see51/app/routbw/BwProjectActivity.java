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
import com.my51c.see51.app.adapter.BwProjectArrayAdapter;
import com.my51c.see51.app.adapter.BwProjectArrayAdapter.OnListRemovedListener;
import com.my51c.see51.app.domian.BwProjectNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*宽带叠加  高级  里面的规则*/
public class BwProjectActivity extends BaseActivity implements
        OnListRemovedListener {
    protected static final String TAG = "BwProjectActivity";
    public static List<String> nameList = new ArrayList<String>();//存储
    Handler mHandler = new Handler();
    private PullToRefreshListView mListView;
    private BwProjectArrayAdapter mAdapter;
    private List<BwProjectNum> mBwProjectNums;
    private RequestQueue mRequestQueue;
    private BwProjectNum mBwProNum;
    private int i;
    private List<String> mSprinner;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw_project_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        bwProjectQuery();
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.bw_project_listview);
        if (mBwProjectNums == null) {
            mBwProjectNums = new ArrayList<BwProjectNum>();
        }
        if (mSprinner == null) {
            mSprinner = new ArrayList<String>();
        }

        mAdapter = new BwProjectArrayAdapter(mBwProjectNums, this, i,
                mRequestQueue, mSprinner);
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

    // 规划查询数据
    private void bwProjectQuery() {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                XTHttpUtil.GET_BWSET_AVD_RULE_QUREY,
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
                        //
                        if (response != null) {
                            pd.dismiss();
                            loginDataQuery(response.result);
                        }
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
                int rulesum = object.getInt("rulesum");
                for (int i = 1; i <= rulesum; i++) {
                    JSONObject rulesObject = object.getJSONObject("rule" + i);
                    String name = rulesObject.getString("name");
                    String srcip = rulesObject.getString("srcip");
                    String srcport = rulesObject.getString("srcport");
                    String desip = rulesObject.getString("desip");
                    String desport = rulesObject.getString("desport");
                    int pro = rulesObject.getInt("pro");
                    String policy = rulesObject.getString("policy");
                    mBwProNum = new BwProjectNum();
                    mBwProNum.setName(name);
                    mBwProNum.setSrcip(srcip);
                    mBwProNum.setSrcport(srcport);
                    mBwProNum.setDesip(desip);
                    mBwProNum.setDesport(desport);
                    mBwProNum.setPro(pro);
                    mBwProNum.setPolicy(policy);
                    mBwProNum.setBwProjectTag(false);
                    mSprinner.add(policy);
                    for (int j = 0; j < mSprinner.size(); j++) {
                        for (int k = j + 1; k < mSprinner.size(); k++) {
                            if (mSprinner.get(j).toString().equals(mSprinner.get(k).toString())) {
                                mSprinner.remove(k);
                            }
                        }
                    }
                    mBwProjectNums.add(mBwProNum);

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
    public void bwAddProject(View view) {
        i++;
        mBwProNum = new BwProjectNum();
        mBwProNum.setName("name" + i);
        mBwProNum.setBwProjectTag(true);
        mBwProjectNums.add(mBwProNum);
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

    // 点击离开页面
    public void bwProjectOnFinish(View v) {
        finish();
    }

    @Override
    public void onRemoved() {
        mAdapter.notifyDataSetChanged();
    }
}
