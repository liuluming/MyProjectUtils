package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class SystemInfoActivity extends BaseActivity {
    protected static final String TAG = "FirewallInfoActivity";
    public boolean progresshow;
    //public TextView firewall;
//	public ImageButton back_home;
    private ListView sysInfoListView;
    private RequestQueue mRequestQueue;
    private TextView sys_daily;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_linfo_layout);
        sys_daily = (TextView) findViewById(R.id.sys_daily);
        sys_daily.setTypeface(AppData.fontXiti);
        //firewall = (TextView) this.findViewById(R.id.systeminfo_edi);
//		back_home = (ImageButton) this.findViewById(R.id.back_btn_finish);
        //firewall.setVisibility(View.VISIBLE);
        sysInfoListView = (ListView) findViewById(R.id.sysinfo_lv);
        sysInfoListView.setDivider(null);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        devstatuFireWall();

    }

    // 查看防火墙信息
    private void devstatuFireWall() {
        RequestQueue mRequestQueue = Volley
                .newRequestQueue(SystemInfoActivity.this);
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_DEVSTATU_CORESTATUS_SYSDAILY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());

                        loadDataSysDaily(response);// 加载数据
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

    private void loadDataSysDaily(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                int firNum = response.getInt("num");
                List<String> sysDailyList = new ArrayList<String>();
                for (int i = 1; i <= firNum; i++) {
                    String firMsg1 = response.getString("msg" + i);
                    sysDailyList.add(firMsg1);
                }
                // String firMsg2 = response.getString("msg2");
                //Toast.makeText(this, fireList.toString() + "成功了", 0).show();
                //firewall.setText(sysDailyList.toString());
                sysInfoListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.coremodule_simple_list, sysDailyList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(this, "失败了", 0).show();
        }
    }


    public void backSystemStatuOnFinish(View v) {
        finish();
    }

    public void showDia() {
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
