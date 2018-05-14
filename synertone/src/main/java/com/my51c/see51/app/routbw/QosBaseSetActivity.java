package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

/*QosBase基本信息设置页面*/
public class QosBaseSetActivity extends BaseActivity {
    private static final String TAG = "QosBaseSetActivity";
    private EditText mDownload_Speed, mUpload_Speed;// 下载上传宽带总速度
    private Spinner mDownload_first, mUpload_first;// 下载上传优先
    private EditText mDownload_oneip, mUpload_oneip;// 单ip下载上传
    private Spinner mPDownload_Oneip, mPUpload_oneip;// 惩罚单下载上传优先级
    private EditText mPDownload_bwip, mPUpload_bwip;// 惩罚单单ip下载上传宽带
    private ToggleButton mBTOnOff;// BT限速
    private EditText mDownload_maxspeed, mUpload_maxspeed;// 上传下载的最大速度
    private RequestQueue mRequestQueue;// 消息队列
    private boolean btenableTag;// 标记BT功能

    private TextView qos_tv1, download_all, up_all, download_youxian, up_youxian, danip_download, danip_up,
            xianzhidanip_download, xianzhidanip_up, xianzhidanip_downyouxian, xianzhidanip_upyouxian, bt_xiansu, down_max, up_max;


    // private String QOS_BTSPEED_TAG = "2";
    private String downtotal, uptotal, ipdownlimit, ipuplimit, pudown, puup,
            btenable, btdown, btup;
    private int downprior, upprior, pudownpri, puuppri;
    private String downtotalStr, uptotalStr, downpriorStr, uppriorStr,
            ipdownlimitStr, ipuplimitStr, pudownpriStr, puuppriStr, pudownStr,
            puupStr, btenableStr, btdownStr, btupStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qos_baseset_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        qosBaseQuery();
    }

    private void initView() {
        qos_tv1 = (TextView) findViewById(R.id.qos_tv1);
        qos_tv1.setTypeface(AppData.fontXiti);
        download_all = (TextView) findViewById(R.id.download_all);
        download_all.setTypeface(AppData.fontXiti);
        up_all = (TextView) findViewById(R.id.up_all);
        up_all.setTypeface(AppData.fontXiti);
        download_youxian = (TextView) findViewById(R.id.download_youxian);
        download_youxian.setTypeface(AppData.fontXiti);
        up_youxian = (TextView) findViewById(R.id.up_youxian);
        up_youxian.setTypeface(AppData.fontXiti);
        danip_download = (TextView) findViewById(R.id.danip_download);
        danip_download.setTypeface(AppData.fontXiti);
        danip_up = (TextView) findViewById(R.id.danip_up);
        danip_up.setTypeface(AppData.fontXiti);
        xianzhidanip_download = (TextView) findViewById(R.id.xianzhidanip_download);
        xianzhidanip_download.setTypeface(AppData.fontXiti);
        xianzhidanip_up = (TextView) findViewById(R.id.xianzhidanip_up);
        xianzhidanip_up.setTypeface(AppData.fontXiti);
        xianzhidanip_downyouxian = (TextView) findViewById(R.id.xianzhidanip_downyouxian);
        xianzhidanip_downyouxian.setTypeface(AppData.fontXiti);
        xianzhidanip_upyouxian = (TextView) findViewById(R.id.xianzhidanip_upyouxian);
        xianzhidanip_upyouxian.setTypeface(AppData.fontXiti);
        bt_xiansu = (TextView) findViewById(R.id.bt_xiansu);
        bt_xiansu.setTypeface(AppData.fontXiti);
        down_max = (TextView) findViewById(R.id.down_max);
        down_max.setTypeface(AppData.fontXiti);
        up_max = (TextView) findViewById(R.id.up_max);
        up_max.setTypeface(AppData.fontXiti);

        mDownload_Speed = (EditText) findViewById(R.id.qosbase_download_speed);
        mUpload_Speed = (EditText) findViewById(R.id.qosbase_upload_speed);

        mDownload_first = (Spinner) findViewById(R.id.qosbase_download_first);
        SpinnerAdapter mDownload_first_adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.opt_ji));
        mDownload_first.setAdapter(mDownload_first_adapter);

        mUpload_first = (Spinner) findViewById(R.id.qosbase_upload_first);
        SpinnerAdapter mUpload_first_adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.opt_ji));
        mUpload_first.setAdapter(mUpload_first_adapter);


        mDownload_oneip = (EditText) findViewById(R.id.qosbase_download_oneip);
        mUpload_oneip = (EditText) findViewById(R.id.qosbase_upload_oneip);

        mPDownload_Oneip = (Spinner) findViewById(R.id.qosbase_punish_download_oneip);
        SpinnerAdapter mPDownload_Oneip_adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.opt_ji));
        mPDownload_Oneip.setAdapter(mPDownload_Oneip_adapter);

        mPUpload_oneip = (Spinner) findViewById(R.id.qosbase_punish_upload_oneip);
        SpinnerAdapter mPUpload_oneip_adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.opt_ji));
        mPUpload_oneip.setAdapter(mPUpload_oneip_adapter);


        mPDownload_bwip = (EditText) findViewById(R.id.qosbase_punish_download_bwip);
        mPUpload_bwip = (EditText) findViewById(R.id.qosbase_punish_upload_bwip);

        mBTOnOff = (ToggleButton) findViewById(R.id.qosbase_bt_onoff);
        mDownload_maxspeed = (EditText) findViewById(R.id.qosbase_download_maxspeed);
        mUpload_maxspeed = (EditText) findViewById(R.id.qosbase_upload_maxspeed);

		/*
         * mDownload_maxspeed.setEnabled(false);
		 * mUpload_maxspeed.setEnabled(false);
		 */
        mBTOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    btenableStr = "0";
                    mDownload_maxspeed.setEnabled(true);
                    mUpload_maxspeed.setEnabled(true);
                    btenableTag = true;
                } else {
                    mDownload_maxspeed.setText("");
                    mUpload_maxspeed.setText("");
                    btenableStr = "-1";
                    btdownStr = "";
                    btupStr = "";
                    mDownload_maxspeed.setEnabled(false);
                    mUpload_maxspeed.setEnabled(false);
                    btenableTag = false;
                }
            }
        });
    }

    // 查询
    private void qosBaseQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_BWDIY_BASIC_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());

                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(QosBaseSetActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    // 查询加载数据
    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                downtotal = response.getString("downtotal");// 下载带宽总速度
                uptotal = response.getString("uptotal");// 上传带宽总速度

                downprior = response.getInt("downprior");// 下载优先级 1~9 1 优先级最高
                upprior = response.getInt("upprior");// 上传优先级 1~9 1 优先级最高

                ipdownlimit = response.getString("ipdownlimit");// 单ip最大下载速度
                ipuplimit = response.getString("ipuplimit");// 单ip最大上传速度

                pudownpri = response.getInt("pudownpri");// 惩罚单ip下载优先级 1~5
                puuppri = response.getInt("puuppri");// 惩罚单ip上传优先级 1~5

                pudown = response.getString("pudown");// 惩罚单ip下载带宽
                puup = response.getString("puup");// 惩罚单ip上传带宽

                btenable = response.getString("btenable");// BT使能 0 开启 1 关闭
                btdown = response.getString("btdown");// BT使能 0 开启 1 关闭
                btup = response.getString("btup");// BT使能 0 开启 1 关闭

                upDataUi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(QosBaseSetActivity.this, "连接失败", 0).show();
        }
    }

    // 查询数据更新UI的方法
    private void upDataUi() {
        QosBaseSetActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mDownload_Speed.setText(downtotal);
                mUpload_Speed.setText(uptotal);

                mDownload_first.setSelection(downprior - 1);
                mUpload_first.setSelection(upprior - 1);

                mDownload_oneip.setText(ipdownlimit);
                mUpload_oneip.setText(ipuplimit);

                mPDownload_Oneip.setSelection(pudownpri - 1);
                mPUpload_oneip.setSelection(puuppri - 1);

                mPDownload_bwip.setText(pudown);
                mPUpload_bwip.setText(puup);

                mDownload_maxspeed.setText(btdown);
                mUpload_maxspeed.setText(btup);

                if (btenable.equals("0")) {
                    mBTOnOff.setChecked(true);
                    mDownload_maxspeed.setText(btdown);
                    mUpload_maxspeed.setText(btup);
                    mDownload_maxspeed.setEnabled(true);
                    mUpload_maxspeed.setEnabled(true);
                    btenableStr = "0";
                    btenableTag = true;
                } else if (btenable.equals("-1")) {
                    mBTOnOff.setChecked(false);
                    mDownload_maxspeed.setText("");
                    mUpload_maxspeed.setText("");
                    mDownload_maxspeed.setEnabled(false);
                    mUpload_maxspeed.setEnabled(false);
                    btenableStr = "-1";
                    btenableTag = false;
                }
            }
        });
    }

    // 获取文本
    private void qosGetText() {
        downtotalStr = mDownload_Speed.getText().toString();
        uptotalStr = mUpload_Speed.getText().toString();

        downpriorStr = mDownload_first.getSelectedItem().toString();
        uppriorStr = mUpload_first.getSelectedItem().toString();

        ipdownlimitStr = mDownload_oneip.getText().toString();
        ipuplimitStr = mUpload_oneip.getText().toString();

        pudownpriStr = mPDownload_Oneip.getSelectedItem().toString();
        puuppriStr = mPUpload_oneip.getSelectedItem().toString();

        pudownStr = mPDownload_bwip.getText().toString();
        puupStr = mPUpload_bwip.getText().toString();

        if (btenableTag) {
            btdownStr = mDownload_maxspeed.getText().toString();
            btupStr = mUpload_maxspeed.getText().toString();
        } else {
            btdownStr = "";
            btupStr = "";
        }

    }

    // QosBaseSet 点击保存Post上去
    public void qosBasePostOnClick(View v) {
        try {
            qosBaseRequest();
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.dismiss();
        super.onDestroy();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 点击退出页面
    public void netWorkBasetOnFinish(View v) {
        finish();
    }

    private void qosBaseRequest() throws Exception {
        qosGetText();
        JSONObject object = XTHttpJSON.postQosBasicSet(downtotalStr,
                uptotalStr, downpriorStr, uppriorStr, ipdownlimitStr,
                ipuplimitStr, pudownpriStr, puuppriStr, pudownStr, puupStr,
                btenableStr, btdownStr, btupStr);
        RequestParams params = new RequestParams("UTF-8");
        params.setBodyEntity(new StringEntity(object.toString(), "UTF-8"));
        params.setContentType("applicatin/json");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.POST_BWDIY_BASIC_SET,
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
                        //	Log.i(TAG, "接收到数据-->" + arg0.reasonPhrase);
                        Log.i(TAG, "接收到数据-->" + arg0.result);
						/*Toast.makeText(getApplicationContext(),
								arg0.result + "", 0).show();*/
                        pdDismiss(arg0);
                        // loginDataQuery(arg0);
                    }
                });
    }

    protected void loginDataQuery(String string) {

    }

}
