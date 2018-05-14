package com.my51c.see51.app.routbw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
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

/*带宽叠加   高级*/
public class BwSuperActivity extends BaseActivity implements OnClickListener {
    private LinearLayout mLayoutInterface, mLayoutLeaguer, mLayoutPlan,
            mLayoutRule;
    private RequestQueue requestQueue;
    private Intent mIntent;
    private ProgressDialog pd;
    private boolean progresshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bwsuper_activity);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
    }

    private void initView() {
        TextView bwsuper_gaoji = (TextView) findViewById(R.id.bwsuper_gaoji);
        bwsuper_gaoji.setTypeface(AppData.fontXiti);
        TextView bwsuper_jiekou = (TextView) findViewById(R.id.bwsuper_jiekou);
        bwsuper_jiekou.setTypeface(AppData.fontXiti);
        TextView bwsuper_member = (TextView) findViewById(R.id.bwsuper_member);
        bwsuper_member.setTypeface(AppData.fontXiti);
        TextView bwsuper_celv = (TextView) findViewById(R.id.bwsuper_celv);
        bwsuper_celv.setTypeface(AppData.fontXiti);
        TextView bwsuper_guize = (TextView) findViewById(R.id.bwsuper_guize);
        bwsuper_guize.setTypeface(AppData.fontXiti);


        mLayoutInterface = (LinearLayout) findViewById(R.id.bwsuper_interface);// 接口
        mLayoutLeaguer = (LinearLayout) findViewById(R.id.bwsuper_leaguer);// 成员
        mLayoutPlan = (LinearLayout) findViewById(R.id.bwsuper_plan);// 策划
        mLayoutRule = (LinearLayout) findViewById(R.id.bwsuper_rule);// 规矩

        mLayoutInterface.setOnClickListener(this);
        mLayoutLeaguer.setOnClickListener(this);
        mLayoutPlan.setOnClickListener(this);
        mLayoutRule.setOnClickListener(this);
    }

    // public void Save_All_Info(View v) {
    // loginHttp();
    // }

    public void bwSuperOnFinish(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bwsuper_interface:
                mIntent = new Intent(this, BwSuperInteferActivity.class);
                startActivity(mIntent);
                break;
            case R.id.bwsuper_leaguer:
                Toast.makeText(this, "点击了成员", 0).show();
                mIntent = new Intent(this, BwMenberActivity.class);
                startActivity(mIntent);
                break;
            case R.id.bwsuper_plan:
                mIntent = new Intent(this, BwPlanActivity.class);
                startActivity(mIntent);
                break;
            case R.id.bwsuper_rule:
                mIntent = new Intent(this, BwProjectActivity.class);
                startActivity(mIntent);
                break;
            case R.id.save_all_info_btn:
                loginHttp();
                break;

        }
    }

    private void loginHttp() {
        progresshow = true;
        showDia();
        JsonObjectRequest Request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.BWSET_ADV_START, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // 加载数据
                Toast.makeText(BwSuperActivity.this,
                        response.toString() + "12312313", 5000).show();
                // Log.i(TAG, "接收回来的数据===》" + response.toString());
                pdDismiss(response);
                loadData(response);

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.i(TAG, error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        requestQueue.add(Request);

    }

    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(BwSuperActivity.this, "访问成功", Toast.LENGTH_SHORT)
                    .show();
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BwSuperActivity.this, "访问失败", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
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

    }

}
