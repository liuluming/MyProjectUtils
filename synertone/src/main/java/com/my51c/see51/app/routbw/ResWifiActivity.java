package com.my51c.see51.app.routbw;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.adapter.ResWifiAdapter;
import com.my51c.see51.app.domian.ResWifi;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*wifi 查询的类*/
public class ResWifiActivity extends BaseActivity implements OnItemClickListener {
    protected static final String TAG = "ResWifiActivity";
    String ssidpass;
    private PullToRefreshListView mWifiLV;
    private RequestQueue mRequestQueue;// 消息队列
    private List<ResWifi> mResWifiList;
    private ResWifi mResWifi;
    private ResWifiAdapter mResWifiAdapter;
    private Handler mHandler = new Handler();
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reswifi_activity);
        mRequestQueue = Volley.newRequestQueue(this);
        initView();
        wifiDataList();
        mResWifiAdapter = new ResWifiAdapter(mResWifiList, this);
        mWifiLV.setAdapter(mResWifiAdapter);

        mWifiLV.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mResWifiAdapter.notifyDataSetChanged();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mWifiLV.onRefreshComplete();
                    }
                }, 5000);

            }
        });
    }

    private void initView() {
        mResWifiList = new ArrayList<ResWifi>();
        mWifiLV = (PullToRefreshListView) findViewById(R.id.wifiListView);
        mWifiLV.setOnItemClickListener(this);
    }

    private void wifiDataList() {
        /*progresshow = true;
		showDia();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				XTHttpUtil.GET_NETSET_WIFIQUERY, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i(TAG, "接收到数据-->" + response.toString());
						pdDismiss(response);
						Toast.makeText(ResWifiActivity.this,
								response.toString(), 0).show();
						loginQuery(response);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i(TAG, error.toString());
						Toast.makeText(ResWifiActivity.this, "网络错误",
								Toast.LENGTH_SHORT).show();
						if (pd.isShowing()) {
							pd.dismiss();
						}
					}
				});
		mRequestQueue.add(request);*/
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.GET_NETSET_WIFIQUERY,
                null, new RequestCallBack<JSONObject>() {

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
                    public void onFailure(HttpException arg0, String arg1) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        Log.i(TAG, "接收到数据-->" + arg0.reasonPhrase);
                        Toast.makeText(getApplicationContext(), arg0.result + "", 0).show();
                        //	loginDataQuery(arg0);
                        pdDismiss(arg0.result + "");
                        JSONObject object;
                        try {
                            object = new JSONObject((String) arg0.result);
                            loginQuery(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void loginQuery(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(ResWifiActivity.this, "访问成功", Toast.LENGTH_SHORT)
                    .show();
            try {
                int num = response.getInt("num");
                for (int i = 1; i <= num; i++) {
                    JSONObject wifiObject = response.getJSONObject("wifi" + i);
                    String name = wifiObject.getString("name");
                    String encryption = wifiObject.getString("encryption");
                    String strength = wifiObject.getString("strength");

                    mResWifi = new ResWifi();
                    mResWifi.setName(name);
                    mResWifi.setEncryption(encryption);
                    mResWifi.setStrength(strength);

                    mResWifiList.add(mResWifi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(ResWifiActivity.this, "访问失败", Toast.LENGTH_SHORT)
                    .show();
        }
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

    // 点击退出页面
    public void retwifiOnFinish(View v) {
        finish();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(String object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            final int position, long id) {
        //Toast.makeText(getApplicationContext(), "点击了第"+position+"条", 0).show();
        AlertDialog.Builder customDia = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View viewDia = layoutInflater.inflate(
                R.layout.base_custom_dialog, null);

        final ResWifi wifi = mResWifiList.get(position);
        customDia.setTitle(wifi.getName());
        customDia.setView(viewDia);
        customDia.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText diaInput = (EditText) viewDia
                                .findViewById(R.id.txt_cusDiaInput);
                        if (!TextUtils.isEmpty(diaInput.getText().toString())) {
                            ssidpass = diaInput.getText().toString().trim();
                        }
                        final JSONObject jsonObject = XTHttpJSON.postWifiConnect(
                                wifi.getName(), ssidpass);
                        wifiDataListPost(jsonObject);
                    }
                });
        customDia.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        customDia.create().show();
    }

    private void wifiDataListPost(JSONObject object) {
	/*	progresshow = true;
		showDia();
		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				XTHttpUtil.POST_NETSET_WIFICONNECT, object,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i(TAG, "接收到数据-->" + response.toString());
						pdDismiss(response.toString());
						Toast.makeText(ResWifiActivity.this,
								response.toString(), 0).show();
						//loginQuery(response);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i(TAG, error.toString());
						Toast.makeText(ResWifiActivity.this, "网络错误",
								Toast.LENGTH_SHORT).show();
						if (pd.isShowing()) {
							pd.dismiss();
						}
					}
				});
		mRequestQueue.add(request);*/
        RequestParams params = new RequestParams("UTF-8");
        try {
            params.setBodyEntity(new StringEntity(object.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.setContentType("applicatin/json");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.POST_NETSET_WIFICONNECT,
                params, new RequestCallBack<JSONObject>() {

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
                    public void onFailure(HttpException arg0, String arg1) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        Log.i(TAG, "接收到数据-->" + arg0.reasonPhrase);
                        Toast.makeText(getApplicationContext(), arg0.result + "", 0).show();
                        //	loginDataQuery(arg0);
                        pdDismiss(arg0.result + "");
                       /* JSONObject object;
						try {
							object = new JSONObject((String)arg0.result);
							loginQuery(object);
						} catch (JSONException e) {
							e.printStackTrace();
						}*/
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mResWifi != null) {
            mResWifi = null;
        }
    }
}
