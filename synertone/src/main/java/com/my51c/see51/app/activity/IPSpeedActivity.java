package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
import com.my51c.see51.app.adapter.IpspeedAdapter;
import com.my51c.see51.app.adapter.IpspeedAdapter.OnListRemovedListener;
import com.my51c.see51.app.domian.IpSpeed;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*IP限速*/
public class IPSpeedActivity extends BaseActivity implements OnListRemovedListener {

    protected static final String TAG = "IPSpeedActivity";
    public static int number;
    //	private IpSpeed mIpSpeed1;
//	private IpSpeed mIpSpeed2;
//	private IpSpeed mIpSpeed3;
    public static List mCurrUsers = new ArrayList<>();
    Handler mHandler = new Handler();
    private PullToRefreshListView mListView;
    private IpspeedAdapter mAdapter;
    private List<IpSpeed> mIpSpeeds;
    private List<String> mStrings;
    private List<String> mAddString;
    private RequestQueue mRequestQueue;
    private IpSpeed mIpSpeed;
    private int a;
    private TextView zhiding_ipspeed;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_speed_activity);
        zhiding_ipspeed = (TextView) findViewById(R.id.zhiding_ipspeed);
        zhiding_ipspeed.setTypeface(AppData.fontXiti);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        ipSpeedQuery();
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.ipspeed_list);
        if (mIpSpeeds == null) {
            mIpSpeeds = new ArrayList<IpSpeed>();
        }
        if (mStrings == null) {
            mStrings = new ArrayList<String>();
        }
        if (mAddString == null) {
            mAddString = new ArrayList<String>();
        }

        //这里是测试数据，以后要删掉----------------------------------------------------------
        /*mIpSpeed1=new IpSpeed();
		mIpSpeed1.setUser("bbb" + a);
		mIpSpeed1.setIpSpeedTag(false);
		mIpSpeed1.setIpSpeedDelTag(1);
		mIpSpeeds.add(mIpSpeed1);
		mIpSpeed2=new IpSpeed();
		mIpSpeed2.setUser("aaa" + a);
		mIpSpeed2.setIpSpeedTag(false);
		mIpSpeed2.setIpSpeedDelTag(1);
		mIpSpeeds.add(mIpSpeed2);
		mIpSpeed3=new IpSpeed();
		mIpSpeed3.setUser("ccc" + a);
		mIpSpeed3.setIpSpeedTag(false);
		mIpSpeed3.setIpSpeedDelTag(1);
		mIpSpeeds.add(mIpSpeed3);		*/
        //---------------------------------------------------------------------------------

        //mAdapter = new IpspeedAdapter(mIpSpeeds, this,mRequestQueue,
        mAdapter = new IpspeedAdapter(mIpSpeeds, this, a, mRequestQueue,
                mStrings, mAddString);

        mListView.setAdapter(mAdapter);
        mAdapter.setOnListRemovedListener(this);
        //	mAdapter.notifyDataSetChanged();
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                mAdapter.notifyDataSetChanged();

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 3000);
            }
        });

    }

    // 初始化   请求服务  有限制的ip列表
    private void ipSpeedQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_IPLIMIT_QUERY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据=ooo==》" + response.toString());

                loadData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(IPSpeedActivity.this, "网络错误",
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
            try {
                //有限制的  ip 的数量
                number = response.getInt("num");
                Log.e(TAG, "请求服务器加载有限制的数据的条目>>>>>>>>>number==" + number);
                for (int i = 1; i <= number; i++) {
                    JSONObject limitlObject = response.getJSONObject("limit"
                            + i);

                    Log.e(TAG, "请求服务器加载有限制的数据的条目>>>循环>>>>>>number==" + number);
                    String enable = limitlObject.getString("enable");
                    String ip = limitlObject.getString("ip");
                    String downmax = limitlObject.getString("downmax");
                    String upmax = limitlObject.getString("upmax");
                    int prior = limitlObject.getInt("prior");
                    String user = limitlObject.getString("user");
                    mCurrUsers.add(user);

                    mIpSpeed = new IpSpeed();
                    mIpSpeed.setIpSpeedTag(true);
                    mIpSpeed.setEnable(enable);
                    mIpSpeed.setIp(ip);
                    mStrings.add(ip);
                    mIpSpeed.setDownmax(downmax);
                    mIpSpeed.setUpmax(upmax);
                    mIpSpeed.setPrior(prior);
                    mIpSpeed.setIpSpeedDelTag(0);
                    //			mIpSpeed.setIpSpeedTag(true);
                    mIpSpeed.setUser(user);
                    mIpSpeeds.add(mIpSpeed);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(IPSpeedActivity.this, "访问失败", 0).show();
        }
    }

    //新增  ip  加载所有的ip列表
    private void ipSpeedAdd() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_AllIP_QUERY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());

                allIpData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(IPSpeedActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    //修改ip限制     20160714  by  hyw
    private void ipSpeedModify() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_AllIP_QUERY, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());

                allIpData(response);// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(IPSpeedActivity.this, "网络错误",
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
            int num;
            try {
                num = response.getInt("num");
                for (int i = 1; i <= num; i++) {
                    JSONObject ipJSONObject02 = response
                            .getJSONObject("ip" + i);
                    String superIp = ipJSONObject02.getString("ip");
                    String superMac = ipJSONObject02.getString("mac");
                    String superUser = ipJSONObject02.getString("user");
                    mAddString.add(superIp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(IPSpeedActivity.this, "访问失败", 0).show();
        }
    }

    // 点击添加条目
    public void addIpItemOnClick(View view) {
        a++;
        mIpSpeed = new IpSpeed();
        mIpSpeed.setUser("aaa" + a);
        mIpSpeed.setIpSpeedTag(false);
        //	mIpSpeed.setIpSpeedDelTag(1);
        mIpSpeed.setIpSpeedDelTag(a);
        // mIpSpeeds.add(mIpSpeed);
        // ipSpeedAdd();
        ipSpeedAdd();
        mIpSpeeds.add(mIpSpeed);
        mAdapter.notifyDataSetChanged();
    }

    public void ipSpeedOnFinish(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        a = 0;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onRemoved() {
        mAdapter.notifyDataSetChanged();
    }
}
