package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class StaticrouteInfoActivity extends BaseActivity {
    //	public ImageButton back_home;
    protected static final String TAG = "FirewallInfoActivity";
    public TextView staticroute;
    public boolean progresshow;
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staticroute_linfo_layout);
        staticroute = (TextView) this.findViewById(R.id.staticroute_edi);
//		back_home = (ImageButton) this.findViewById(R.id.back_btn_finish);
        staticroute.setVisibility(View.VISIBLE);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        devstatuRout();

    }

    // 查看路由列表
    private void devstatuRout() {
        RequestQueue mRequestQueue = Volley
                .newRequestQueue(StaticrouteInfoActivity.this);
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_DEVSTATU_CORESTATUS_ROUTLIST, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        loadDataRout(response);// 加载数据
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

    private void loadDataRout(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                int firNum = response.getInt("num");
                List<String> list = new ArrayList<String>();
                for (int i = 1; i <= firNum; i++) {
                    String firMsg = response.getString("msg" + i);
                    list.add("msg" + i + ":" + firMsg + "\n");
                }
                // String firMsg2 = response.getString("msg2");
                //String routStrList=null;
                //Char[] routStr=list.toString().split(",");
//				    for (String string : routStr) {
//				    	routStrList=string;
//					}
                staticroute.setText(list.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
        }
    }

    public void staticroute_back(View v) {
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
