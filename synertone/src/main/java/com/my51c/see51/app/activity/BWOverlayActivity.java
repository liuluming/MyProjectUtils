package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
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
import com.my51c.see51.app.routbw.BwSuperActivity;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*带宽叠加*/
public class BWOverlayActivity extends BaseActivity {
    protected static final String TAG = "BWOverlayActivity";
    private Intent mIntent;
    private RequestQueue mRequestQueue;
    //private ToggleButton ;
    private CheckBox mBw_wan01, mBw_wan02, mBw_wan03, mBw_wan04, mBw_wan05,
            mBw_wan06;
    private Spinner mBw_plan;
    private Button mBw_PostBtn;
    private boolean tagOnOff;
    private List<String> bwWanSelect;
    private int index01, index02, index03, index04, index05, index06;
    private String mbwWenable;
    private int bwWanSnum, mPolicy, bwOnoffSrt;
    private String enableStr, wan01Str, wan02Str, wan03Str, wan04Str, wan05Str,
            wan06Str, policyStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    // 宽带叠加 Post
    public static JSONObject postBwSet(String enable, int wansnum,
                                       String wanenable01, String wanenable02, String wanenable03,
                                       String wanenable04, String wanenable05, String wanenable06,
                                       String policy) {
        JSONObject bwSetJson = new JSONObject();
        try {
            bwSetJson.put("enable", enable);
            bwSetJson.put("wansnum", wansnum);
            bwSetJson.put("wan0", wanenable01);
            bwSetJson.put("wan1", wanenable02);
            bwSetJson.put("wan2", wanenable03);
            bwSetJson.put("wan3", wanenable04);
            bwSetJson.put("wan4", wanenable05);
            bwSetJson.put("wan5", wanenable06);
            bwSetJson.put("policy", policy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bwSetJson;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_bwoverlay_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        bWOverlayQurty();
        bwOverlyPost(mBw_PostBtn);
    }

    private void initView() {
        TextView textview = (TextView) findViewById(R.id.bw_textview);
        textview.setTypeface(AppData.fontXiti);
        TextView super_bw_tv1 = (TextView) findViewById(R.id.super_bw_tv1);
        super_bw_tv1.setTypeface(AppData.fontXiti);
        TextView bw_celue = (TextView) findViewById(R.id.bw_celue);
        bw_celue.setTypeface(AppData.fontXiti);
        TextView bw_gaoji = (TextView) findViewById(R.id.bw_gaoji);
        bw_gaoji.setTypeface(AppData.fontPutu);

        //mbwoverly_onff = (ToggleButton) findViewById(R.id.bwoverly_onff);
        mBw_wan01 = (CheckBox) findViewById(R.id.bw_check_wan01);
        mBw_wan01.setTypeface(AppData.fontXiti);
        mBw_wan02 = (CheckBox) findViewById(R.id.bw_check_wan02);
        mBw_wan02.setTypeface(AppData.fontXiti);
        mBw_wan03 = (CheckBox) findViewById(R.id.bw_check_wan03);
        mBw_wan03.setTypeface(AppData.fontXiti);
        mBw_wan04 = (CheckBox) findViewById(R.id.bw_check_wan04);
        mBw_wan04.setTypeface(AppData.fontXiti);
        mBw_wan05 = (CheckBox) findViewById(R.id.bw_check_wan05);
        mBw_wan05.setTypeface(AppData.fontXiti);
        mBw_wan06 = (CheckBox) findViewById(R.id.bw_check_wan06);
        mBw_wan06.setTypeface(AppData.fontXiti);

        mBw_plan = (Spinner) findViewById(R.id.super_bw_plan);
        mBw_plan.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.super_bw_plan)));
        mBw_PostBtn = (Button) findViewById(R.id.bw_postbtn);

//		mBw_wan01.setEnabled(false);
//		mBw_wan02.setEnabled(false);
//		mBw_wan03.setEnabled(false);
//		mBw_wan04.setEnabled(false);
//		mBw_wan05.setEnabled(false);
//		mBw_plan.setEnabled(false);
        //	mbwoverly_onff.setOnCheckedChangeListener(this);
    }

    private void getTextStr() {
//		mbwoverly_onff
//				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						if (isChecked) {
//							mbwWenable = "0";
//							mBw_wan01.setEnabled(true);
//							mBw_wan02.setEnabled(true);
//							mBw_wan03.setEnabled(true);
//							mBw_wan04.setEnabled(true);
//							mBw_wan05.setEnabled(true);
//							mBw_plan.setEnabled(true);
//						} else {
//							mbwWenable = "-1";
//							mBw_wan01.setEnabled(false);
//							mBw_wan02.setEnabled(false);
//							mBw_wan03.setEnabled(false);
//							mBw_wan04.setEnabled(false);
//							mBw_wan05.setEnabled(false);
//							mBw_plan.setEnabled(false);
//						}
//					}
//				});

        mBw_wan01.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan01Str = "0";
                    index01 = 1;
                } else {
                    wan01Str = "-1";
                    index01 = 0;
                }
            }
        });
        mBw_wan02.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan02Str = "0";
                    index02 = 1;
                } else {
                    wan02Str = "-1";
                    index02 = 0;
                }
            }
        });
        mBw_wan03.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan03Str = "0";
                    index03 = 1;
                } else {
                    wan03Str = "-1";
                    index03 = 0;
                }
            }
        });
        mBw_wan04.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan04Str = "0";
                    index04 = 1;
                } else {
                    wan04Str = "-1";
                    index04 = 0;
                }
            }
        });
        mBw_wan05.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan05Str = "0";
                    index05 = 1;
                } else {
                    wan05Str = "-1";
                    index05 = 0;
                }
            }
        });
        mBw_wan06.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    wan06Str = "0";
                    index06 = 1;
                } else {
                    wan06Str = "-1";
                    index06 = 0;
                }
            }
        });

        mBw_plan.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (mBw_plan.getSelectedItem().toString().equals("均衡")) {
                    mPolicy = 0;
                } else if (mBw_plan.getSelectedItem().toString().equals("不可到达")) {
                    mPolicy = 1;
                } else if (mBw_plan.getSelectedItem().toString().equals("丢弃")) {
                    mPolicy = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // 一进页面加载数据
    private void bWOverlayQurty() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_BWSET_EASY_QUERY, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        /*Toast.makeText(
								BWOverlayActivity.this,
								response.toString()
										+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
								0).show();*/
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                mBw_plan.setEnabled(false);
                Toast.makeText(BWOverlayActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(request);
    }

    // 访问解析数据
    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {

            try {
                bwWanSelect = new ArrayList<String>();
                bwOnoffSrt = response.getInt("enable");
                if (bwOnoffSrt == 0) {
                    tagOnOff = true;
                    bwWanSnum = response.getInt("wansnum");
                    for (int i = 0; i < bwWanSnum; i++) {
                        mbwWenable = response.getString("wan" + i);
                        bwWanSelect.add(mbwWenable);
                    }
                    mPolicy = response.getInt("policy");
                    updataUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(BWOverlayActivity.this, "访问失败", Toast.LENGTH_SHORT)
                    .show();
            tagOnOff = false;
        }
    }

    // 更新UI
    private void updataUi() {
        BWOverlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bwOnoffSrt == 0) {
                    //mbwoverly_onff.setChecked(true);  //lxx-del 开关 布局文件删除了
                    for (int i = 0; i < bwWanSnum; i++) {
                        if (i == 0 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan01.setChecked(true);
                            index01 = 1;
                        } else {
                            index01 = 0;
                        }
                        if (i == 1 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan02.setChecked(true);
                            index02 = 1;
                        } else {
                            index02 = 0;
                        }
                        if (i == 2 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan03.setChecked(true);
                            index03 = 1;
                        } else {
                            index03 = 0;
                        }
                        if (i == 3 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan04.setChecked(true);
                            index04 = 1;
                        } else {
                            index04 = 0;
                        }
                        if (i == 4 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan05.setChecked(true);
                            index05 = 1;
                        } else {
                            index05 = 0;
                        }
                        if (i == 5 && bwWanSelect.get(i).equals("0")) {
                            mBw_wan06.setChecked(true);
                            index06 = 1;
                        } else {
                            index06 = 0;
                        }
                    }
                    mBw_plan.setSelection(mPolicy);
                } else if (bwOnoffSrt == 1) {
//					mBw_wan01.setEnabled(false);
//					mBw_wan02.setEnabled(false);
//					mBw_wan03.setEnabled(false);
//					mBw_wan04.setEnabled(false);
//					mBw_wan05.setEnabled(false);
//					mBw_plan.setEnabled(false);
                }
            }
        });
    }

    // 带宽叠加 Post提交数据
    public void bwOverlyPost(View v) {
        getTextStr();
        if (tagOnOff) {
            progresshow = true;
            showDia();
            JSONObject object = postBwSet(mbwWenable, index01 + index02
                            + index03 + index04 + index05 + index06, wan01Str,
                    wan02Str, wan03Str, wan04Str, wan05Str, wan06Str, mPolicy
                            + "");
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.GET_BWSET_EASY_QUERY, object,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "接收回来的数据===》" + response.toString());
							/*Toast.makeText(
									BWOverlayActivity.this,
									response.toString() + "AAAAAAAAAAAAAAAAAAA",
									0).show();*/
                            loadData(response);// 加载数据
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                    Toast.makeText(BWOverlayActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
            mRequestQueue.add(request);
        }
    }

    // 点击高级跳到带宽叠加高级页面
    public void bwOverlyOnClick(View v) {
        mIntent = new Intent(this, BwSuperActivity.class);
        startActivity(mIntent);
    }

    // 点击退出当前页面
    public void bwOverlyOnFinish(View v) {
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

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        pd.dismiss();
        super.onDestroy();
    }

//	@Override
//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		if (isChecked) {
//			mBw_wan01.setEnabled(true);
//			mBw_wan02.setEnabled(true);
//			mBw_wan03.setEnabled(true);
//			mBw_wan04.setEnabled(true);
//			mBw_wan05.setEnabled(true);
//			mBw_plan.setEnabled(true);
//			tagOnOff = true;
//		} else {
//			mBw_wan01.setEnabled(false);
//			mBw_wan02.setEnabled(false);
//			mBw_wan03.setEnabled(false);
//			mBw_wan04.setEnabled(false);
//			mBw_wan05.setEnabled(false);
//			mBw_plan.setEnabled(false);
//			tagOnOff = false;
//		}
//	}

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }
}
