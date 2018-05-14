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
public class FirewallInfoActivity extends BaseActivity {
    //	public ImageButton back_home;
    protected static final String TAG = "FirewallInfoActivity";
    public boolean progresshow;
    //public TextView firewall;
    private ListView fireListView;
    private RequestQueue mRequestQueue;
    private TextView firewall;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firewallinfo_layout);
        //firewall = (TextView) this.findViewById(R.id.firewall_edi);
//		back_home = (ImageButton) this.findViewById(R.id.back_btn_finish);
        //firewall.setVisibility(View.VISIBLE);
        firewall = (TextView) findViewById(R.id.firewall);
        firewall.setTypeface(AppData.fontXiti);
        fireListView = (ListView) findViewById(R.id.firewall_lv);
        fireListView.setDivider(null);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        devstatuSysDaily();

    }

    private void devstatuSysDaily() {
        RequestQueue mRequestQueue = Volley
                .newRequestQueue(FirewallInfoActivity.this);
        progresshow = true;
        showDia();

        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_DEVSTATU_CORESTATUS_FIRE, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        loadDataFire(response);// 加载数据
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

    private void loadDataFire(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                List<String> fireList = new ArrayList<String>();
                int firNum = response.getInt("num");
                for (int i = 1; i <= firNum; i++) {
                    String firMsg1 = response.getString("msg" + i);
                    fireList.add(firMsg1);
                }
                //firewall.setText(fireList.toString());
                fireListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.coremodule_simple_list, fireList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
        }
    }

    public void backStatuOnFinish(View v) {
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
