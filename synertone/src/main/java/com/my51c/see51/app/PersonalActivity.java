package com.my51c.see51.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
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
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.activity.ReviseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.UpdateManager;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {
    protected static final String TAG = "PersonalActivity";
    public static String AppVerSion;
    private static String VERSION_1;
    private static String UPDATE_SERVERAPK;
    private static String uri_app;
    UpdateManager manager = new UpdateManager(PersonalActivity.this);
    private LinearLayout mRevise_password;
    private TextView mVersionNum, personal, mimaxiugai, app_shengji, banbenhao;
    private ToggleButton mVersionOnOff;
    private RequestQueue mRequestQueue;
    private Intent mIntent;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    /**
     * //得到当前程序版本名
     *
     * @param context
     * @return
     * @author snt1179
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            Log.e(TAG, "当前程序版本号---versionName----》" + versionName);
            versionCode = pi.versionCode;
            Log.e(TAG, "当前版本号----versionCode---》" + versionCode);
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();

        if (!(TextUtils.isEmpty(VERSION_1))) {
            mVersionNum.setText(VERSION_1);
        }

        try {
            mVersionNum.setText(getVersionName());
            /*getVersionName();
			getAppVersionName(this);*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void initView() {
        personal = (TextView) findViewById(R.id.personal);
        personal.setTypeface(AppData.fontXiti);
        mimaxiugai = (TextView) findViewById(R.id.mimaxiugai);
        mimaxiugai.setTypeface(AppData.fontXiti);
        app_shengji = (TextView) findViewById(R.id.app_shengji);
        app_shengji.setTypeface(AppData.fontXiti);
        banbenhao = (TextView) findViewById(R.id.banbenhao);
        banbenhao.setTypeface(AppData.fontXiti);

        mRevise_password = (LinearLayout) findViewById(R.id.personal_revise_password);
        mVersionNum = (TextView) findViewById(R.id.personal_version_number);
        mVersionOnOff = (ToggleButton) findViewById(R.id.personla_query_version);
        mRevise_password.setOnClickListener(this);
        mVersionOnOff.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_revise_password:
                mIntent = new Intent(this, ReviseActivity.class);
                startActivity(mIntent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // 查询最新版本号
            postVerSionQuery();
            // 版本升级请求
            // postVerSionStart();

        } else {
        }
    }

    // 版本开始升级请求
    private void postVerSionStart() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_PEOPLE_UPSYS_START, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        verSionStart(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    protected void verSionStart(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            // Toast.makeText(this, "成功111111", 0).show();
            try {
                final String url = response.getString("uri");
                //Toast.makeText(this, "成功111111", 0).show();
//				 manager.getApkUrl(url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "设置失败", 0).show();
        }

    }

    // 查看最新版本接口
    private void postVerSionQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_PEOPLE_UPSYS_CHECK, null,
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
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    protected void loadData(JSONObject response) {

        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(this, "设置成功", 0).show();
            try {
                AppVerSion = response.getString("ver");
                //Toast.makeText(this, AppVerSion, 0).show();
                // manager.checkUpdate();
                if (!(AppVerSion.equals(mVersionNum.getText()))) {
                    // 这里来检测版本是否需要更新
                    manager = new UpdateManager(this);
                    postVerSionStart();
                    manager.checkUpdateInfo();

                    //这个version号暂时取消赋值
                    //VERSION_1 = verSion;
                } else {
                    Toast.makeText(PersonalActivity.this, "已经是最新版本",
                            Toast.LENGTH_SHORT).show();
                    // mVersionNum.setText(verSion);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "设置失败", 0).show();
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

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 点击退出个人中心页面
    public void onPersonFinish(View v) {
        finish();

    }

    /**
     * @return
     * @throws Exception
     * @author snt1179
     * 获取当前应用版本号
     */
    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        Log.e(TAG, "当前应用版本号----version---》" + version);
	        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.e(TAG, "当前系统版本号-----currentapiVersion--》" + currentapiVersion);
        return version;
    }


}
