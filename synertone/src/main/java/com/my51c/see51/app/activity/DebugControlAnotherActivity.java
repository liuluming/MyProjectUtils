package com.my51c.see51.app.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.came.viewbguilib.ButtonBgUi;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.Logger.LoggerSave;
import com.my51c.see51.app.http.SntAsyncHttpGet;
import com.my51c.see51.app.http.SntAsyncHttpGet.HpOverListener;
import com.my51c.see51.app.http.SntAsyncPost;
import com.my51c.see51.app.http.SntAsyncPost.PostOverHandle;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.ChechIpMask;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static com.my51c.see51.app.http.XTHttpUtil.GET_DEBUG_QUERY;
import static com.my51c.see51.app.http.XTHttpUtil.GET_RESTART;
import static com.my51c.see51.app.http.XTHttpUtil.GET_STOP;
import static com.my51c.see51.app.http.XTHttpUtil.POST_DEBUG_ELEVSTART;
import static com.my51c.see51.app.http.XTHttpUtil.POST_DEBUG_ZAISTART;
import static com.my51c.see51.app.http.XTHttpUtil.QUERY_HENG_GUN;
import static com.my51c.see51.app.http.XTHttpUtil.SET_HENG_GUN;

/*调试控制
 * */
public class DebugControlAnotherActivity extends BaseActivity implements OnClickListener {
    private final static String TAG = "DebugControlAnotherActivity";
    private TextView mCurrentEAngle, mCurrentHAngle, mCurrentGAngle;
    private EditText mEPoint, mHPoint, mGpoint;
    private ButtonBgUi mEbtn;
    private ButtonBgUi mHbtn;
    private ButtonBgUi mGbtn;
    private String azi = "";// 水平角文本
    private String elev = "";// 仰角文本
    private String mHeng = "";// 横滚角文本
    private String mRoll;
    private boolean Mflag, Gflog;
    private SntAsyncHttpGet qrytask;
    private SntAsyncPost dbgtask;
    private JSONObject elevjs, azijs, hengjs;
    private SateDbgQry myuptask;
    private SateDbgQryRoll myuptaskroll;
    private boolean isMbjd = false, ismbyj = false, ismfyj = false;//目标角度    目标仰角度。
    private TextView debug_henggun_name, debug_ed_hengun, zhuandonghenggun, danbuhenggun;
    private ButtonBgUi mStopButton;
    private ButtonBgUi mResumeButton;
    private ButtonBgUi mLuyouButton;
    private RequestQueue mRequestQueue;
    private HashMap<String, Toast> toaHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_control_another_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        initView();
        initToasts();
    }

    private void initView() {
        TextView danbu_tv = (TextView) findViewById(R.id.danbu_tv);
        danbu_tv.setTypeface(AppData.fontXiti);
        TextView danbu = (TextView) findViewById(R.id.danbu);
        danbu.setTypeface(AppData.fontXiti);
        TextView zhuandongfangshi = (TextView) findViewById(R.id.zhuandongfangshi);
        zhuandongfangshi.setTypeface(AppData.fontXiti);
        TextView mubiaojiaodu = (TextView) findViewById(R.id.mubiaojiaodu);
        mubiaojiaodu.setTypeface(AppData.fontXiti);
        TextView dangqianshuiping = (TextView) findViewById(R.id.dangqianshuiping);
        dangqianshuiping.setTypeface(AppData.fontXiti);
        TextView zhuandongjiaodu_tv = (TextView) findViewById(R.id.zhuandongjiaodu_tv);
        zhuandongjiaodu_tv.setTypeface(AppData.fontXiti);
        TextView mubiaojiaodu_tv = (TextView) findViewById(R.id.mubiaojiaodu_tv);
        mubiaojiaodu_tv.setTypeface(AppData.fontXiti);
        mStopButton = (ButtonBgUi) findViewById(R.id.stop_bt);
        mStopButton.setTypeface(AppData.fontXiti);
        mStopButton.setOnClickListener(this);
        mResumeButton = (ButtonBgUi) findViewById(R.id.resume_bt);
        mResumeButton.setTypeface(AppData.fontXiti);
        mResumeButton.setOnClickListener(this);
        mLuyouButton = (ButtonBgUi) findViewById(R.id.luyouban_bt);
        mLuyouButton.setTypeface(AppData.fontXiti);
        mLuyouButton.setOnClickListener(this);
        mCurrentGAngle = (TextView) findViewById(R.id.debug_current_henggun);
        mCurrentGAngle.setTypeface(AppData.fontXiti);
        debug_henggun_name = (TextView) findViewById(R.id.debug_henggun_name);
        debug_henggun_name.setTypeface(AppData.fontXiti);
        debug_ed_hengun = (TextView) findViewById(R.id.debug_ed_hengun);
        debug_ed_hengun.setTypeface(AppData.fontXiti);
        mGpoint = (EditText) findViewById(R.id.debug_ed_gpoint);
        mGpoint.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(DebugControlAnotherActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
        mGpoint.setTypeface(AppData.fontXiti);
        zhuandonghenggun = (TextView) findViewById(R.id.zhuandonghenggun);
        zhuandonghenggun.setTypeface(AppData.fontXiti);
        danbuhenggun = (TextView) findViewById(R.id.danbuhenggun);
        danbuhenggun.setTypeface(AppData.fontXiti);
        mGbtn = (ButtonBgUi) findViewById(R.id.debug_Gbtn);
        mGbtn.setTypeface(AppData.fontXiti);
        TextView tiaoshimoshi_tv = (TextView) findViewById(R.id.tiaoshimoshi_tv);
        tiaoshimoshi_tv.setTypeface(AppData.fontXiti);
        TextView dangqianyangjiao_tv = (TextView) findViewById(R.id.dangqianyangjiao_tv);
        dangqianyangjiao_tv.setTypeface(AppData.fontXiti);
        mCurrentEAngle = (TextView) findViewById(R.id.debug_current_eangle);// 当前俯仰角
        mCurrentEAngle.setTypeface(AppData.fontXiti);
        mCurrentHAngle = (TextView) findViewById(R.id.debug_current_hangle);// 当前方位角
        mCurrentHAngle.setTypeface(AppData.fontXiti);
        mEPoint = (EditText) findViewById(R.id.debug_ed_epoint);// 俯仰目标角度  仰角
        mEPoint.setTypeface(AppData.fontXiti);
        mEPoint.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(DebugControlAnotherActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
        mHPoint = (EditText) findViewById(R.id.debug_ed_hpoint);// 方位目标角度
        mHPoint.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(DebugControlAnotherActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
        mHPoint.setTypeface(AppData.fontXiti);
        mEbtn = (ButtonBgUi) findViewById(R.id.debug_ebtn);// 启动当前仰角
        mEbtn.setTypeface(AppData.fontXiti);
        mHbtn = (ButtonBgUi) findViewById(R.id.debug_hbtn);// 启动当前水平角
        mHbtn.setTypeface(AppData.fontXiti);
        mEbtn.setOnClickListener(this);
        mHbtn.setOnClickListener(this);
        mGbtn.setOnClickListener(this);
    }

    @SuppressLint("ShowToast")
    @SuppressWarnings("unused")
    private void initToasts() {
        Toast toast1 = Toast.makeText(DebugControlAnotherActivity.this, "接收到的俯仰角数据不合法！！", 0);
        Toast toast2 = Toast.makeText(DebugControlAnotherActivity.this, "接收到方位角数据不合法！！", 0);
        Toast toast3 = Toast.makeText(DebugControlAnotherActivity.this, "查询俯仰角方位角故障", 0);
        Toast toast4 = Toast.makeText(DebugControlAnotherActivity.this, "连接网元服务器失败", 0);
        Toast toast5 = Toast.makeText(DebugControlAnotherActivity.this, "输入俯仰目标角不合法！！", 0);
        Toast toast6 = Toast.makeText(DebugControlAnotherActivity.this, "输入方位目标角不合法！！", 0);
        Toast toast7 = Toast.makeText(DebugControlAnotherActivity.this, "查询仰角不合法！！", 0);
        Toast toast8 = Toast.makeText(DebugControlAnotherActivity.this, "查询方位角不合法！！", 0);
        Toast toast9 = Toast.makeText(DebugControlAnotherActivity.this, "查询方位、俯仰错误", 0);
        Toast toast10 = Toast.makeText(DebugControlAnotherActivity.this, "输入目标横滚角不合法！！", 0);
        Toast toast11 = Toast.makeText(DebugControlAnotherActivity.this, "横滚角查询成功", 0);
        Toast toast12 = Toast.makeText(DebugControlAnotherActivity.this, "横滚角查询失败", 0);
        Toast toast13 = Toast.makeText(DebugControlAnotherActivity.this, "横滚角不合法！！", 0);
        Toast toast14 = Toast.makeText(DebugControlAnotherActivity.this, "查询横滚角错误", 0);
        Toast toast15 = Toast.makeText(DebugControlAnotherActivity.this, "查询横滚角不合法！！", 0);
        Toast toast16 = Toast.makeText(DebugControlAnotherActivity.this, "调整俯仰角失败", 0);
        Toast toast17 = Toast.makeText(DebugControlAnotherActivity.this, "调整方位角失败", 0);
        Toast toast18 = Toast.makeText(DebugControlAnotherActivity.this, "调整横滚角失败", 0);
        toaHashMap.put("接收到的俯仰角数据不合法！！", toast1);
        toaHashMap.put("接收到方位角数据不合法！！", toast2);
        toaHashMap.put("查询俯仰角方位角故障", toast3);
        toaHashMap.put("连接网元服务器失败", toast4);
        toaHashMap.put("输入俯仰目标角不合法！！", toast5);
        toaHashMap.put("输入方位目标角不合法！！", toast6);
        toaHashMap.put("查询仰角不合法！！", toast7);
        toaHashMap.put("查询方位角不合法！！", toast8);
        toaHashMap.put("查询方位、俯仰错误", toast9);
        toaHashMap.put("输入目标横滚角不合法！！", toast10);
        toaHashMap.put("横滚角查询成功", toast11);
        toaHashMap.put("横滚角查询失败", toast12);
        toaHashMap.put("横滚角不合法！！", toast13);
        toaHashMap.put("查询横滚角错误", toast14);
        toaHashMap.put("查询横滚角不合法！！", toast15);
        toaHashMap.put("调整俯仰角失败", toast16);
        toaHashMap.put("调整方位角失败", toast17);
        toaHashMap.put("调整横滚角失败", toast18);

    }

    private void myquery() {
        qrytask = new SntAsyncHttpGet();
        qrytask.setFinishListener(new HpOverListener() {
            public void HpRecvOk(JSONObject data) {
                String receive_elev_data;
                //double receive_elev;
                String receive_elev;

                String receive_azi_data;
                //double receive_azi;
                String receive_azi;
                /*try {
					Toast.makeText(DebbugControlActivity.this, "elev-仰角--->"+data.getString("elev").toString()+"---------azi--俯角---->"+data.getString("azi").toString(), 0).show();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
                try {
                    receive_elev_data = data.getString("elev");//、、、、、仰角
                    receive_azi_data = data.getString("azi");//、、、、、俯角
                    //if(ChechIpMask.isDigite(receive_elev_data,0,90,2)){//判断目标角度 是不是数字
                    if (ChechIpMask.a2b(receive_elev_data, 0, 105)) {
                        //if(ChechIpMask.a2d(receive_elev_data, -10, 120)){
                        Log.e(TAG, "接受到的仰角合法！！！");
                        // receive_elev=Double.valueOf(receive_elev_data);
                        receive_elev = ChechIpMask.numDigite(receive_elev_data, 2);
                        // receive_elev=((int)(receive_elev*100))/100;
                    } else {
                        if (toaHashMap.get("接收到的俯仰角数据不合法！！") != null) {
                            toaHashMap.get("接收到的俯仰角数据不合法！！").show();
                        }
                        receive_elev = "--";//-1
                    }
                    //当前水平角   0-180
                    //if(ChechIpMask.isDigite(receive_azi_data,0,180,2)){//判断水平角都是不是数字
                    if (ChechIpMask.abs(receive_azi_data, 360)) {
                        Log.e(TAG, "接受到的水平角合法！！！");
                        //receive_azi=Double.valueOf(receive_azi_data);
                        receive_azi = ChechIpMask.numDigite(receive_azi_data, 2);
                        //receive_azi=((int)(receive_elev*100))/100;
                    } else {
                        if (toaHashMap.get("接收到方位角数据不合法！！") != null) {
                            toaHashMap.get("接收到方位角数据不合法！！").show();
                        }
                        receive_azi = "--";//-1
                    }
                    if (data.getString("code").equals("0")) {
                        //	Toast.makeText(DebbugControlActivity.this, "receive_elev-仰角--->"+receive_elev+"---------receive_azi--俯角---->"+receive_azi, 0).show();
                        mCurrentEAngle.setText(String.valueOf(receive_elev));//当前仰角
                        mCurrentHAngle.setText(String.valueOf(receive_azi));//当前水平角度
                    } else if (data.getString("code").equals("-1")) {
                        if (data.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("查询俯仰角方位角故障") != null) {
                                toaHashMap.get("查询俯仰角方位角故障").show();
                            }
                        }
                    } else {
                        if (toaHashMap.get("查询俯仰角方位角故障") != null) {
                            toaHashMap.get("查询俯仰角方位角故障").show();
                        }
                    }
                } catch (JSONException e) {
                    if (toaHashMap.get("查询俯仰角方位角故障") != null) {
                        toaHashMap.get("查询俯仰角方位角故障").show();
                    }
                    e.printStackTrace();
                }
            }
        });
        qrytask.execute(GET_DEBUG_QUERY);
        LoggerSave.requestLog(GET_DEBUG_QUERY,GET_DEBUG_QUERY);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        querySessionStatus(new OnSessionStatusListener() {
            @Override
            public void sessionSuccess() {
                myquery();
                queryHengGet();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mflag = false;
        Gflog = false;
        try {
            Iterator<Entry<String, Toast>> iter = toaHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Toast> entry = iter.next();
                Toast toast = entry.getValue();
                Field field = toast.getClass().getDeclaredField("mTN");
                field.setAccessible(true);
                Object obj = field.get(toast);
                java.lang.reflect.Method m=obj.getClass().getDeclaredMethod("hide");
                m.invoke(obj);
                iter.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void elevdbg() {//点击   目标角度    启动按钮
        elev = mEPoint.getText().toString();
        if (elev.equals("")) {
            Toast.makeText(DebugControlAnotherActivity.this, "输入俯仰角为空", 0).show();
            isMbjd = false;
            return;

        } else if (ChechIpMask.a2b(elev, 0, 105)) { //if(ChechIpMask.isDigite(elev,0,90,2)){//如果是数字
            //}else if(ChechIpMask.a2d(elev,-10,120)){
            String _elev = ChechIpMask.numDigite(elev, 2);
            elev = _elev;
            Log.e(TAG, "输入的IP为数字！！！");
            elevjs = new JSONObject();
            try {
                elevjs.put("deselev", elev);
                isMbjd = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //Toast.makeText(DebbugControlActivity.this, "对不起，您输入俯仰角不合法！", 0).show();
            //	mEPoint.setText(--);//-1
            isMbjd = false;
        }

        dbgtask = new SntAsyncPost();
        dbgtask.SetListener(new PostOverHandle() {
            public void HandleData(JSONObject data) {
                try {
                    if (data.getString("code").equals("0")) {
                        getstatus();
                    } else if (data.getString("code").equals("-100")) {
						/*Toast.makeText(DebbugControlActivity.this,
								"俯仰角调试失败,错误码："+data.getString("code"), 0).show();*///edit by hyw 20161205 
                        if (toaHashMap.get("连接网元服务器失败") != null) {
                            toaHashMap.get("连接网元服务器失败").show();
                        }
                    } else if (data.getString("code").equals("-1")) {
                        if (data.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("调整俯仰角失败") != null) {
                                toaHashMap.get("调整俯仰角失败").show();
                            }
                        }
                    } else {
                        if (toaHashMap.get("调整俯仰角失败") != null) {
                            toaHashMap.get("调整俯仰角失败").show();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        if (isMbjd) {
            dbgtask.execute(POST_DEBUG_ELEVSTART, elevjs.toString());
            LoggerSave.requestLog(POST_DEBUG_ELEVSTART,elevjs.toString());

        } else {
            if (toaHashMap.get("输入俯仰目标角不合法！！") != null) {
                toaHashMap.get("输入俯仰目标角不合法！！").show();
            }
            return;
        }
        isMbjd = false;
    }

    private void azidbg() {//点击目标水平角
        //目标水平角
        azi = mHPoint.getText().toString();
        if (azi.equals("")) {
            Toast.makeText(DebugControlAnotherActivity.this, "输入方位角为空", 0).show();
            ismbyj = false;
            return;

        } else if (ChechIpMask.abs(azi, 360)) {//if(ChechIpMask.isDigite(azi,0,180,2)){
            String _azi = ChechIpMask.numDigite(azi, 2);
            azi = _azi;
            azijs = new JSONObject();
            try {
                azijs.put("desazi", azi);
                ismbyj = true;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            mHPoint.setText("--");//-1
            ismbyj = false;
            if (toaHashMap.get("输入方位目标角不合法！！") != null) {
                toaHashMap.get("输入方位目标角不合法！！").show();
            }
        }
        dbgtask = new SntAsyncPost();
        dbgtask.SetListener(new PostOverHandle() {
            public void HandleData(JSONObject data) {
                try {
                    if (data.getString("code").equals("0")) {
                        getstatus();
                    } else if (data.getString("code").equals("-100")) {
						/*Toast.makeText(DebbugControlActivity.this,
						"方位角调试失败,错误码："+data.getString("code"), 0).show();*///edit by hyw 20161205
                        if (toaHashMap.get("连接网元服务器失败") != null) {
                            toaHashMap.get("连接网元服务器失败").show();
                        }
                    } else if (data.getString("code").equals("-1")) {
                        if (data.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("调整方位角失败") != null) {
                                toaHashMap.get("调整方位角失败").show();
                            }
                        }
                    } else {
                        if (toaHashMap.get("调整方位角失败") != null) {
                            toaHashMap.get("调整方位角失败").show();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        if (ismbyj) {
            dbgtask.execute(POST_DEBUG_ZAISTART, azijs.toString());
            LoggerSave.requestLog(POST_DEBUG_ZAISTART,azijs.toString());
        } else {
            if (toaHashMap.get("输入方位目标角不合法！！") != null) {
                toaHashMap.get("输入方位目标角不合法！！").show();
            }
            return;
        }
    }

    private void startHeng() {//点击目标横滚角
        //目标横滚角
        mHeng = mGpoint.getText().toString();
        if (mHeng.equals("")) {
            Toast.makeText(DebugControlAnotherActivity.this, "输入横滚角为空", 0).show();
            ismfyj = false;
            return;
        } else if (ChechIpMask.abs(mHeng, 35)) {//if(ChechIpMask.isDigite(azi,0,180,2)){
            String _mHeng = ChechIpMask.numDigite(mHeng, 2);
            mHeng = _mHeng;
            hengjs = new JSONObject();
            try {
                hengjs.put("dstroll", mHeng);
                ismfyj = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mGpoint.setText("--");//-1
            ismfyj = false;
            if (toaHashMap.get("输入目标横滚角不合法！！") != null) {
                toaHashMap.get("输入目标横滚角不合法！！").show();
            }
        }
        dbgtask = new SntAsyncPost();
        dbgtask.SetListener(new PostOverHandle() {
            public void HandleData(JSONObject data) {
                try {
                    if (data.getString("code").equals("0")) {
                        getStatusRoll();
                    } else if (data.getString("code").equals("-100")) {
                        if (toaHashMap.get("连接网元服务器失败") != null) {
                            toaHashMap.get("连接网元服务器失败").show();
                        }
                    } else if (data.getString("code").equals("-1")) {
                        if (data.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("调整横滚角失败") != null) {
                                toaHashMap.get("调整横滚角失败").show();
                            }
                        }
                    } else {
                        if (toaHashMap.get("调整横滚角失败") != null) {
                            toaHashMap.get("调整横滚角失败").show();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        if (ismfyj) {
            dbgtask.execute(SET_HENG_GUN, hengjs.toString());
            LoggerSave.requestLog(SET_HENG_GUN,hengjs.toString());
        } else {
            if (toaHashMap.get("输入目标横滚角不合法！！") != null) {
                toaHashMap.get("输入目标横滚角不合法！！").show();
            }
            return;
        }
    }

    private void UpUip() {
        myuptask = new SateDbgQry();
        myuptask.execute(GET_DEBUG_QUERY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.debug_ebtn:
                Gflog = false;
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        elevdbg();
                    }
                });
                break;
            case R.id.debug_hbtn:
                Gflog = false;
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        azidbg();
                    }
                });
                break;
            case R.id.debug_Gbtn:
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        startHeng();
                    }
                });
                break;
            case R.id.stop_bt:
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        stopGet();
                    }
                });
                break;
            case R.id.resume_bt:
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        resumeGet();
                    }
                });
                break;
            case R.id.luyouban_bt:
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        rebootGet();
                    }
                });
        }
    }

    public void getstatus() {
        Mflag = true;
        myuptask = new SateDbgQry();
        myuptask.execute(GET_DEBUG_QUERY);
        LoggerSave.requestLog(GET_DEBUG_QUERY,GET_DEBUG_QUERY);
    }

    public void getStatusRoll() {
        Gflog = true;
        myuptaskroll = new SateDbgQryRoll();
        myuptaskroll.execute(QUERY_HENG_GUN);
        LoggerSave.requestLog(QUERY_HENG_GUN,QUERY_HENG_GUN);
    }

    // 点击退出当前页面
    public void debugControlOnFinish(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void stopGet() {
        String stopUrl = GET_STOP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, stopUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(GET_STOP,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("0")) {
                        Toast.makeText(DebugControlAnotherActivity.this, "天线停止命令发送成功", 0).show();
                    } else if (code.equals("-1")) {
                        if (msg.equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            Toast.makeText(DebugControlAnotherActivity.this, "天线停止命令发送失败", 0).show();
                        }
                    } else {
                        Toast.makeText(DebugControlAnotherActivity.this, "天线停止命令发送失败", 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(GET_STOP,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
        mRequestQueue.add(stringRequest);
    }

    private void resumeGet() {
        String restartUrl = GET_RESTART;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restartUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(GET_RESTART,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("0")) {
                        Toast.makeText(DebugControlAnotherActivity.this, "天线重启命令发送成功", 0).show();
                    } else if (code.equals("-1")) {
                        if (msg.equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            Toast.makeText(DebugControlAnotherActivity.this, "天线重启命令发送失败", 0).show();
                        }
                    } else {
                        Toast.makeText(DebugControlAnotherActivity.this, "天线重启命令发送失败", 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(GET_RESTART,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
        mRequestQueue.add(stringRequest);
    }

    private void rebootGet() {
        String rebootUrl = XTHttpUtil.GET_REBOOT;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, rebootUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        Toast.makeText(DebugControlAnotherActivity.this, "智能网关重启命令发送成功", 0).show();
                    } else {
                        Toast.makeText(DebugControlAnotherActivity.this, "智能网关重启命令发送失败", 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
        mRequestQueue.add(stringRequest);
    }

    private void queryHengGet() {
        String queryHengUrl = QUERY_HENG_GUN;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, queryHengUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(QUERY_HENG_GUN,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("0")) {
                        if (toaHashMap.get("横滚角查询成功") != null) {
                            toaHashMap.get("横滚角查询成功").show();
                        }
                    } else if (code.equals("-1")) {
                        if (msg.equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("横滚角查询失败") != null) {
                                toaHashMap.get("横滚角查询失败").show();
                            }
                        }
                    } else {
                        if (toaHashMap.get("横滚角查询失败") != null) {
                            toaHashMap.get("横滚角查询失败").show();
                        }
                    }
                    mRoll = jsonObject.getString("roll");
                    if (ChechIpMask.abs(mRoll, 35)) {
                        Log.e(TAG, "横滚角度合法！！");
                        String _heng = ChechIpMask.numDigite(mRoll, 2);
                        mCurrentGAngle.setText(_heng);
                    } else {
                        mCurrentGAngle.setText("--");//-1
                        if (toaHashMap.get("横滚角不合法！！") != null) {
                            toaHashMap.get("横滚角不合法！！").show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (toaHashMap.get("连接网元服务器失败") != null) {
                    toaHashMap.get("连接网元服务器失败").show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(QUERY_HENG_GUN,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
        mRequestQueue.add(stringRequest);
    }

    public class SateDbgQry extends AsyncTask<String, String, String> {
        private String recv = "";
        private JSONObject myjs;
        private int i = 0;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpGet hget = new HttpGet(params[0]);
            HttpClient hclient = new DefaultHttpClient();
            HttpResponse respone;
            while (Mflag && (i < 6)) {
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
                i++;
            }
            return recv;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            Log.i("LYJ", "onprogressupdate---->数据是：" + values[0]);
            if (values[0].equals("")) {
                Mflag = false;
                Toast.makeText(DebugControlAnotherActivity.this, "通信错误", 0).show();
            } else {
                mEPoint = (EditText) findViewById(R.id.debug_ed_epoint);// 俯仰目标角度  仰角
                mHPoint = (EditText) findViewById(R.id.debug_ed_hpoint);// 目标方位角度
                try {
                    myjs = new JSONObject(values[0]);
                    if (myjs.getString("code").equals("0")) {
                        if (ChechIpMask.a2b(myjs.getString("elev"), 0, 105)) {
                            //if(ChechIpMask.a2d(myjs.getString("elev"),-10,120)){
                            Log.e(TAG, "仰度合法！！");
                            String _elev = ChechIpMask.numDigite(myjs.getString("elev"), 2);
                            mCurrentEAngle.setText(_elev);
                        } else {
                            mCurrentEAngle.setText("--");//-1
                            if (i == 5) {
                                if (toaHashMap.get("查询仰角不合法！！") != null) {
                                    toaHashMap.get("查询仰角不合法！！").show();
                                }
                            }
                        }
                        if (ChechIpMask.abs(myjs.getString("azi"), 360)) {
                            Log.e(TAG, "方位角度合法！！");
                            String _azi = ChechIpMask.numDigite(myjs.getString("azi"), 2);
                            mCurrentHAngle.setText(_azi);
                        } else {
                            mCurrentHAngle.setText("--");//-1
                            if (i == 5) {
                                if (toaHashMap.get("查询方位角不合法！！") != null) {
                                    toaHashMap.get("查询方位角不合法！！").show();
                                }
                            }
                        }
                        if ((myjs.getString("elev").equals(mEPoint.getText().toString()))
                                && (myjs.getString("azi").equals(mHPoint.getText().toString()))) {
                            Mflag = false;
                        }
                    } else if (myjs.getString("code").equals("-1")) {
                        if (myjs.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (i == 5) {
                                if (toaHashMap.get("查询横滚角错误") != null) {
                                    toaHashMap.get("查询横滚角错误").show();
                                }
                            }
                        }
                    } else {
                        if (i == 5) {
                            if (toaHashMap.get("查询方位、俯仰错误") != null) {
                                toaHashMap.get("查询方位、俯仰错误").show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("LYJ", "实时更新查看" + result.toString());
        }
    }

    public class SateDbgQryRoll extends AsyncTask<String, String, String> {
        private String recv = "";
        private JSONObject myjs;
        private int i = 0;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpGet hget = new HttpGet(params[0]);
            HttpClient hclient = new DefaultHttpClient();
            HttpResponse respone;
            while (Gflog && (i < 6)) {
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
                i++;
            }
            return recv;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            Log.i("LYJ", "onprogressupdate---->数据是：" + values[0]);
            if (values[0].equals("")) {
                Gflog = false;
                if (toaHashMap.get("连接网元服务器失败") != null) {
                    toaHashMap.get("连接网元服务器失败").show();
                }
            } else {
                mHeng = mGpoint.getText().toString();
                try {
                    myjs = new JSONObject(values[0]);
                    if (myjs.getString("code").equals("0")) {
                        if (ChechIpMask.abs(myjs.getString("roll"), 35)) {
                            Log.e(TAG, "横滚角度合法！！");
                            String _mHeng = ChechIpMask.numDigite(myjs.getString("roll"), 2);
                            mCurrentGAngle.setText(_mHeng);
                        } else {
                            mCurrentGAngle.setText("--");//-1
                            if (toaHashMap.get("查询横滚角不合法！！") != null) {
                                toaHashMap.get("查询横滚角不合法！！").show();
                            }
                        }
                        if (myjs.getString("roll").equals(mHeng)) {
                            Gflog = false;
                        }
                    } else if (myjs.getString("code").equals("-1")) {
                        if (myjs.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (i == 5) {
                                if (toaHashMap.get("查询横滚角错误") != null) {
                                    toaHashMap.get("查询横滚角错误").show();
                                }
                            }
                        }

                    } else {
                        if (toaHashMap.get("查询横滚角错误") != null) {
                            toaHashMap.get("查询横滚角错误").show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("LYJ", "实时更新查看" + result.toString());
        }
    }
}
