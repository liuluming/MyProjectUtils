package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

public class SystemUpdateActivity extends BaseActivity implements
        OnCheckedChangeListener {
    protected static final String TAG = "SystemUpdateActivity";
    public Button save_upload_info2;
    private ToggleButton mSystemStart;
    private Spinner mMode, mSerNum;
    private RequestQueue mRequestQueue;
    private String modeStr, sernumStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_system_update_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        getSystemUpdataQuery();
    }

    private void initView() {
        TextView sys_update = (TextView) findViewById(R.id.sys_update);
        sys_update.setTypeface(AppData.fontXiti);
        TextView sys_shengji = (TextView) findViewById(R.id.sys_shengji);
        sys_shengji.setTypeface(AppData.fontXiti);
        TextView sys_mode = (TextView) findViewById(R.id.sys_mode);
        sys_mode.setTypeface(AppData.fontXiti);
        TextView sys_fuwuqi = (TextView) findViewById(R.id.sys_fuwuqi);
        sys_fuwuqi.setTypeface(AppData.fontXiti);

        mSystemStart = (ToggleButton) findViewById(R.id.system_updata_toggle);
        mMode = (Spinner) findViewById(R.id.system_updata_motion);
        mMode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.system_update_motion)));

        mSerNum = (Spinner) findViewById(R.id.system_updata_sernum);
        mSerNum.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.system_update_server)));

        save_upload_info2 = (Button) findViewById(R.id.save_upload_info222);
        mSystemStart.setOnCheckedChangeListener(this);
        mMode.setEnabled(false);
        mSerNum.setEnabled(false);
    }

    private void getTextStr() {
        if (mMode.getSelectedItem().toString().equals("本地")) {
            modeStr = "0";
        } else if (mMode.getSelectedItem().toString().equals("远程")) {
            modeStr = "1";
        }

        if (mSerNum.getSelectedItem().toString().equals("服务器1")) {
            sernumStr = "1";
        } else if (mSerNum.getSelectedItem().toString().equals("服务器2")) {
            sernumStr = "2";
        } else if (mSerNum.getSelectedItem().toString().equals("服务器3")) {
            sernumStr = "3";
        } else if (mSerNum.getSelectedItem().toString().equals("服务器4")) {
            sernumStr = "4";
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mMode.setEnabled(true);
            mSerNum.setEnabled(true);
//			postSystemUpdata();
        } else {
            mMode.setEnabled(false);
            mSerNum.setEnabled(false);
        }
    }

    // 查询是否
    private void getSystemUpdataQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_MOREAD_SYSUP_STATUS, null,
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

    // 查询是否
    private void postSystemUpdata() {
        progresshow = true;
        showDia();
        getTextStr();
        JSONObject object = new JSONObject();
        try {
            object.put("mode", modeStr);
            object.put("sernum", sernumStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.GET_MOREAD_SYSUP_STATUS, object,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        loadData2(response);// 加载数据
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
            Toast.makeText(this, "升级成功", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "升级失败", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("1")) {
            Toast.makeText(this, "正在升级", 0).show();
        }
    }

    protected void loadData2(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(this, "升级成功", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "升级失败", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("1")) {
            Toast.makeText(this, "正在升级", 0).show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("2")) {
            Toast.makeText(this, "已经是最新版本", 0).show();
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

    public void sysUpdataOnFinish(View v) {
        finish();
    }

    public void SaveUpload_info(View v) {
        //Toast.makeText(SystemUpdateActivity.this, "点击保存", Toast.LENGTH_LONG).show();
        postSystemUpdata();
    }

}
