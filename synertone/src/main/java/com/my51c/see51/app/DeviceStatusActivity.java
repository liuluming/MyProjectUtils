package com.my51c.see51.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.Logger.LoggerSave;
import com.my51c.see51.app.adapter.MyFragmentDeviceAdapter;
import com.my51c.see51.app.fragment.CoreModuleFragment;
import com.my51c.see51.app.fragment.StartStauFragment;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.my51c.see51.app.http.XTHttpUtil.GET_DEVSTATU_SATESATUS;

/*运行状态*/
public class DeviceStatusActivity extends BaseActivity implements
        OnCheckedChangeListener, OnPageChangeListener {
    protected static final String TAG = "DeviceStatusActivity";
    protected List<Toast> toasts = new ArrayList<>();
    private MyFragmentDeviceAdapter adapter;
    private List<Fragment> fragments;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private String url;
    private RequestQueue mRequestQueue;
    private int DEVICE_STATU_TAG = 2;// 标记
    private JSONObject mObject;
    private StartStauFragment mFragment;
    private CoreModuleFragment mCoreModuleFragment;
    private boolean mStusFlag = false;
    private SntSateStusTask stustask;
    private JSONObject stusjs;
    private int i = 0;
    private String mToken;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_statu_activity);
        if (AppData.accountModel != null) {
            mToken = AppData.accountModel.getSessionToken();
        }
        //	mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//		if (DEVICE_STATU_TAG == 2) {
//			url = XTHttpUtil.GET_DEVSTATU_SATESATUS;
//			devStatuQuery();
//		} else {
//			DEVICE_STATU_TAG = 1;
//		}
        initView();
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        // 找到ViewPager
        mViewPager = (ViewPager) findViewById(R.id.device_view_pager);
        mRadioGroup = (RadioGroup) findViewById(R.id.device_statu_radiogroup);
        ((RadioButton) mRadioGroup.getChildAt(0)).setChecked(true);//显示第一个fragment

        fragments = new ArrayList<Fragment>();
        mFragment = new StartStauFragment(url, mObject);//天线状体入口
        fragments.add(mFragment);
        fragments.add(new CoreModuleFragment(url));//核心模块入口
        /*
		 * mViewPager.setOnPageChangeListener(new
		 * ViewPager.SimpleOnPageChangeListener() {
		 *
		 * @Override public void onPageSelected(int position) {
		 *
		 * } });
		 */
        mViewPager.setOnPageChangeListener(this);
        adapter = new MyFragmentDeviceAdapter(getSupportFragmentManager(),
                fragments);
        mViewPager.setAdapter(adapter);
        mRadioGroup.setOnCheckedChangeListener(this);
        if(!isFinishing()){
            showDia();
        }
        querySessionStatus(new OnSessionStatusListener() {
            @Override
            public void sessionSuccess() {
                SntStusUp();
            }
        });

    }

    //RadioGroup选择事件
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.device_rb_01:
                mViewPager.setCurrentItem(0);
                if(!isFinishing()&&isVisible()){
                    showDia();
                }
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        SntStusUp();
                    }
                });
                break;
            case R.id.device_rb_02:
                mStusFlag = false;
                mViewPager.setCurrentItem(1);
                //url = XTHttpUtil.GET_DEVSTATU_CORESTATUS_ONE;
                //devstatuCore();
                DEVICE_STATU_TAG = 1;
                break;
        }
    }

    // ///////////////////ViewPager改变的方法////////////////////////////////
    @Override
    public void onPageScrollStateChanged(int position) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        // 设置当前的RadioButton 为checked 状态
        ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
        DEVICE_STATU_TAG = 1;
    }

    private void SntStusUp() {
        mStusFlag = true;
        int i = 0;
        stustask = new SntSateStusTask();
        stustask.execute(GET_DEVSTATU_SATESATUS);
        LoggerSave.requestLog(GET_DEVSTATU_SATESATUS,GET_DEVSTATU_SATESATUS);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mStusFlag = false;
        for (Toast temp : toasts) {
            Field field;
            try {
                field = temp.getClass().getDeclaredField("mTN");
                field.setAccessible(true);
                Object obj = field.get(temp);
                java.lang.reflect.Method m=obj.getClass().getDeclaredMethod("hide");
                m.invoke(obj);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //mStusFlag = true;
    }

    // 点击退出页面
    public void deviceStatuOnFinish(View v) {
        finish();
    }

    private void showDia() {
        pd = new ProgressDialog(DeviceStatusActivity.this);
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

    //实时查询天线状态
    private class SntSateStusTask extends AsyncTask<String, String, String> {
        String recv = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progresshow = true;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpGet hget = new HttpGet(params[0]);
            HttpClient hclient = new DefaultHttpClient();
            hclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            HttpResponse respone;
            while (mStusFlag) {

                try {
                    respone = hclient.execute(hget);
                    recv = EntityUtils.toString(respone.getEntity());
                    LoggerSave.responseLog(params[0],recv);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                publishProgress(recv);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (recv.equals("")) {
                if(pd!=null&&isVisible()){
                    pd.dismiss();
                }
                Toast toast = Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0);
                toast.show();
                toasts.add(toast);
                mStusFlag = false;
                if (i == 1)
                    mStusFlag = false;
                i++;
            } else {
                try {
                    stusjs = new JSONObject(recv);
                    if (stusjs.getString("code").equals("0")) {
                        //Toast.makeText(getApplicationContext(), stusjs.toString(), 0).show();
                        if(pd!=null&&isVisible()){
                            pd.dismiss();
                        }
                        mFragment.loadData(stusjs);
                    } else if (stusjs.getString("code").equals("-100")) {
                        if(pd!=null&&isVisible()){
                            pd.dismiss();
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0);
                        toast.show();
                        toasts.add(toast);
                        mStusFlag = false;
                    } else if (stusjs.getString("code").equals("-1")) {
                        if(pd!=null&&isVisible()){
                            pd.dismiss();
                        }
                        if (stusjs.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                            mStusFlag = false;
                        } else {
                            Toast toast = Toast.makeText(DeviceStatusActivity.this, "查询天线状态失败", 0);
                            toast.show();
                            toasts.add(toast);
                            mStusFlag = false;
                        }
                    } else {
                        if(pd!=null&&isVisible()){
                            pd.dismiss();
                        }
                        Toast toast = Toast.makeText(DeviceStatusActivity.this, "查询天线状态失败", 0);
                        toast.show();
                        toasts.add(toast);
                        mStusFlag = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
