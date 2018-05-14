package com.my51c.see51.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ScrollView;
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
import com.android.volley.toolbox.StringRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.db.XTsateenOpenHelper;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.NetWorkUtils;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.afinal.simplecache.ACache;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/*一键对星页面*/
public class OneStartActivity extends BaseActivity implements OnClickListener,
        OnItemSelectedListener {
    private final static String TAG = "OneStartActivity";
    JSONObject aaaaaobject;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            new OneStartNetWork().execute();
        }
    };
    private Button oneStartbtn, defaultbtn;
    private Spinner satelliteNnumber;
    private TextView one_Start_Satelon, one_Start_Freq, one_Start_Bw,
            one_Start_Mode, one_Start_Type, one_Start_Modem, one_Start_Ebn0,
            one_Start_Rssi, one_Start_RePol, one_Start_SendPol, oneText;
    private ToggleButton towText;
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueue01;
    private String ebn0, msg;
    private int staenum;
    // private SQLiteDatabase mDB;
    private XTsateenOpenHelper mOpenHelper;
    private String url;
    private String urlOneStart;
    private Thread thread01, thread02, thread03, thread04;
    private boolean threadTag;
    private Intent iService;
    private ScrollView mScrollView;
    private SntCatch catch1;
    private ACache mOneStartCache;
    private String SET_REF_TAG = "setreftag";
    private boolean threadOneStartTag = true;
    private int THREAH_ONE_TAG = 1;
    private int THREAH_TOW_TAG = 1;
    private int THREAH_THREE_TAG = 1;
    private int THREAH_FOUR_TAG = 1;
    private String satenumStr, satelonStr, modeStr, freqStr, bwStr, typeStr,
            modemStr, rssiStr, rePolStr, sendPolStr, satenum;
    private String typeInt, modeInt;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            while (threadOneStartTag) {
                try {
                    Thread.sleep(5 * 1000);
                    mHandler.sendMessage(mHandler.obtainMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_onestart_activity);

        initView();


        //oneStartQuery();  后续完善 已经在对星状态下  天线正常对星的状态显示
    }

    private void oneStartQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest catchRequest001 = new JsonObjectRequest(Method.GET,
                "http://192.168.80.1:8005/api/moreadv/sate/onestar/query",
                null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "接收return回来的数据===》" + response.toString());
                pdDismiss(response);
                // Toast.makeText(OneStartActivity.this,
                // response.toString(), 0).show();
                loadData(response.toString());// 加载数据
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(OneStartActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(catchRequest001);
    }

    // 控件的查找
    private void initView() {
        // oneStartbtn = (Button) findViewById(R.id.one_start_btn);// 对星按钮
        satelliteNnumber = (Spinner) findViewById(R.id.one_statrt_satellite_number);// 卫星编号Spinner
        one_Start_Satelon = (TextView) findViewById(R.id.one_start_satelon);// 卫星经度
        one_Start_Freq = (TextView) findViewById(R.id.one_start_freq);// 频率
        one_Start_Bw = (TextView) findViewById(R.id.one_start_bw);// 带宽
        one_Start_Mode = (TextView) findViewById(R.id.one_start_mode);// 对星方式
        one_Start_Type = (TextView) findViewById(R.id.one_start_type);// 接收极化
        one_Start_Modem = (TextView) findViewById(R.id.one_start_modem);// 调制解调
        one_Start_Ebn0 = (TextView) findViewById(R.id.one_start_ebn0);// Ed/NO值

        oneText = (TextView) findViewById(R.id.text01);
        // towText = (ToggleButton) findViewById(R.id.textView2);

        one_Start_Rssi = (TextView) findViewById(R.id.one_start_rssi);// Rssi门限
        one_Start_RePol = (TextView) findViewById(R.id.one_start_receiving_pol);// 接收极化角
        one_Start_SendPol = (TextView) findViewById(R.id.one_start_send_pol);// 发送极化角

        defaultbtn = (Button) findViewById(R.id.onestart_default_btn);// 设置为默认值

        defaultbtn.setOnClickListener(this);
        // oneStartbtn.setOnClickListener(this);
        satelliteNnumber.setOnItemSelectedListener(this);

        getOneStatrtString();
        // mOpenHelper.insert(satenum, satelonStr, modeStr, freqStr, bwStr,
        // typeStr, modemStr, rssiStr, rePolStr, sendPolStr);
        //

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mScrollView.setOnTouchListener(new TouchListenerImpl());
    }

    private void saveSharedPreferences() {
        // 步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
        SharedPreferences.Editor editor = getSharedPreferences("onestart",
                MODE_PRIVATE).edit();
        // 步骤2-2：将获取过来的值放入文件
        editor.putString("satenum", satenum);
        editor.putString("satelon", satelonStr);
        editor.putString("mode", modeInt);
        editor.putString("freq", freqStr);
        editor.putString("bw", bwStr);
        editor.putString("type", typeInt);
        editor.putString("modem", modemStr);
        editor.putString("rssi", rssiStr);
        editor.putString("recvpol", rePolStr);
        editor.putString("sendpol", sendPolStr);
        // 步骤3：提交
        editor.commit();
    }

    private void getOneStatrtString() {
        satenumStr = satelliteNnumber.getSelectedItem().toString();
        satelonStr = one_Start_Satelon.getText().toString();// 获取Sprinner
        modeStr = one_Start_Mode.getText().toString();// 对星方式
        freqStr = one_Start_Freq.getText().toString();// 频率
        bwStr = one_Start_Bw.getText().toString();// 带宽
        typeStr = one_Start_Type.getText().toString();// 极化方式
        modemStr = one_Start_Modem.getText().toString();// 调制解调

        rssiStr = one_Start_Rssi.getText().toString();// Rssi门限制
        rePolStr = one_Start_RePol.getText().toString();// 发送极化角
        sendPolStr = one_Start_SendPol.getText().toString();// 设置为默认值

        if (typeStr.equals("垂直")) {
            typeInt = "0";
        } else if (typeStr.equals("水平")) {
            typeInt = "1";
        }

        if (modeStr.equals("信标")) {
            modeInt = "0";
        } else if (modeStr.equals("载波")) {
            modeInt = "1";
        }

    }

    private JSONObject getOneStrartText() {

        getOneStatrtString();
        if (bwStr.equals("25K")) {
            bwStr = "0";
        } else if (bwStr.equals("50K")) {
            bwStr = "1";
        } else if (bwStr.equals("100K")) {
            bwStr = "2";
        } else if (bwStr.equals("200K")) {
            bwStr = "3";
        } else if (bwStr.equals("400K")) {
            bwStr = "4";
        } else if (bwStr.equals("800K")) {
            bwStr = "5";
        } else if (bwStr.equals("1.6M")) {
            bwStr = "6";
        } else if (bwStr.equals("2M")) {
            bwStr = "7";
        } else if (bwStr.equals("4M")) {
            bwStr = "8";
        } else if (bwStr.equals("5M")) {
            bwStr = "9";
        } else if (bwStr.equals("6M")) {
            bwStr = "10";
        } else if (bwStr.equals("7M")) {
            bwStr = "11";
        } else if (bwStr.equals("8M")) {
            bwStr = "12";
        } else if (bwStr.equals("9M")) {
            bwStr = "13";
        } else if (bwStr.equals("10M")) {
            bwStr = "14";
        }
        JSONObject object = XTHttpJSON.postOneStart(satenum, satelonStr,
                modeInt, freqStr, bwStr, typeInt, modemStr, rssiStr, rePolStr,
                sendPolStr, this);
        return object;
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.onestart_default_btn:// 点击设为默认值
                progresshow = true;
                showDia();
                getOneStatrtString();
                // mOpenHelper.update(1, satenum, satelonStr, modeStr, freqStr,
                // bwStr, typeStr, modemStr, rssiStr, rePolStr, sendPolStr);
                saveSharedPreferences();
                JsonObjectRequest defaultRequest = new JsonObjectRequest(
                        Method.POST,
                        "http://192.168.80.1:8005/api/moreadv/sate/onestar/default",
                        getOneStrartText(), new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG,
                                "接收return回来的数据===》" + response.toString());
                        pdDismiss(response);
                            /*
							 * Toast.makeText(OneStartActivity.this,
							 * response.toString(), 0).show();
							 */
                        if (XTHttpJSON.getJSONString(response.toString())
                                .equals("0")) {
                            Toast.makeText(OneStartActivity.this, "设置成功", 0)
                                    .show();
                        } else if (XTHttpJSON.getJSONString(
                                response.toString()).equals("-1")) {
                            Toast.makeText(OneStartActivity.this, "设置失败", 0)
                                    .show();
                        }
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
							/*
							 * Toast.makeText(OneStartActivity.this, "网络错误",
							 * Toast.LENGTH_SHORT).show();
							 */
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
                mRequestQueue.add(defaultRequest);
                break;

            case R.id.onestart_01:// 配置下发
                // configurationDown();
                break;
            case R.id.onestart_02:
                catchOneStart(getOneStrartText());
                oneStar();// 一键对星
                catch1 = new SntCatch();
                catch1.execute();
                break;
            case R.id.onestart_03:
                // oneStarStop();// 收星
                break;
            case R.id.onestart_04:
                // oneStarStow();// 停止
                lyjOneStart();
                break;
        }
    }

    // 加载json数据 GET请求 把东西放到控件
    protected void loadData(String string) {
        if (XTHttpJSON.getJSONString(string).equals("0")) {// 对星成功
            JSONObject jsonObject;
            try {
                threadOneStartTag = false;
                jsonObject = new JSONObject(string);
                upDataUI(jsonObject);
                ebn0 = jsonObject.getString("ebn0");
                one_Start_Ebn0.setText(ebn0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(string).equals("-1")) {// 天线故障
            Toast.makeText(OneStartActivity.this, "天线故障或者修改参数", 0).show();
        } else if (XTHttpJSON.getJSONString(string).equals("1")) {// 正在捕星
            Toast.makeText(OneStartActivity.this, "正在捕星", 0).show();
            threadOneStartTag = true;
            url = "http://192.168.80.1:8005/api/moreadv/sate/onestar/query";
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (THREAH_TOW_TAG == 1) {
                        thread02.start();
                    }
                    THREAH_TOW_TAG = 2;

                }
            }, 5000);

        } else if (XTHttpJSON.getJSONString(string).equals("2")) {// 未捕获到卫星（换星或重试）
            Toast.makeText(OneStartActivity.this, "未捕获到卫星（换星或重试）", 0).show();
            threadOneStartTag = true;

            url = "http://192.168.80.1:8005/api/moreadv/sate/onestar/query";
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (THREAH_THREE_TAG == 1) {
                        thread03.start();
                    }
                    THREAH_THREE_TAG = 2;

                }
            }, 5000);
        } else if (XTHttpJSON.getJSONString(string).equals("3")) {// 正在跟踪
            Toast.makeText(OneStartActivity.this, "正在跟踪", 0).show();
            threadOneStartTag = true;

            url = "http://192.168.80.1:8005/api/moreadv/sate/onestar/query";
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (THREAH_FOUR_TAG == 1) {
                        thread04.start();
                    }
                    THREAH_FOUR_TAG = 2;
                }
            }, 5000);
        }
    }

    private void upDataUI(JSONObject jsonObject) {
        try {
            staenum = jsonObject.getInt("satenum");
            String satelon = jsonObject.getString("satelon");
            String mode = jsonObject.getString("mode");
            String freq = jsonObject.getString("freq");
            String bw = jsonObject.getString("bw");
            String type = jsonObject.getString("type");
            String modem = jsonObject.getString("modem");

            String rssi = jsonObject.getString("rssi");
            String recvpol = jsonObject.getString("recvpol");
            String sendpol = jsonObject.getString("sendpol");

            // satelliteNnumber.setSelection(staenum);
            one_Start_Satelon.setText(satelon);
            if (mode.equals("0")) {
                one_Start_Mode.setText("信标");
            } else if (mode.equals("1")) {
                one_Start_Mode.setText("载波");
            }
            one_Start_Freq.setText(freq);
            if (bw.equals("0")) {
                one_Start_Bw.setText("25K");
            } else if (bw.equals("1")) {
                one_Start_Bw.setText("50K");
            } else if (bw.equals("2")) {
                one_Start_Bw.setText("100K");
            } else if (bw.equals("3")) {
                one_Start_Bw.setText("200K");
            } else if (bw.equals("4")) {
                one_Start_Bw.setText("400K");
            } else if (bw.equals("5")) {
                one_Start_Bw.setText("800K");
            } else if (bw.equals("6")) {
                one_Start_Bw.setText("1.6M");
            } else if (bw.equals("7")) {
                one_Start_Bw.setText("2M");
            } else if (bw.equals("8")) {
                one_Start_Bw.setText("4M");
            } else if (bw.equals("9")) {
                one_Start_Bw.setText("5M");
            } else if (bw.equals("10")) {
                one_Start_Bw.setText("6M");
            } else if (bw.equals("11")) {
                one_Start_Bw.setText("7M");
            } else if (bw.equals("12")) {
                one_Start_Bw.setText("8M");
            } else if (bw.equals("13")) {
                one_Start_Bw.setText("9M");
            } else if (bw.equals("14")) {
                one_Start_Bw.setText("10M");
            }

            if (type.equals("0")) {
                one_Start_Type.setText("信标模式");
            } else if (type.equals("1")) {
                one_Start_Type.setText("水平极化");
            }
            one_Start_Modem.setText(modem);
            one_Start_Rssi.setText(rssi);
            one_Start_RePol.setText(recvpol);
            one_Start_SendPol.setText(sendpol);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // //////////////////////satelliteNnumber//onItemSelected///////////////////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        if (satelliteNnumber.getSelectedItem().toString().equals("协同一号")) {
            url = XTHttpUtil.GET_ONESTART_SETDOWN_ONE;
            urlOneStart = XTHttpUtil.GET_ONESTART_ONE;
            getOneStatrtString();
            satenum = "7";
            JSONObject getSetRefObject = mOneStartCache
                    .getAsJSONObject("saveGeneral" + satenum);

            if (getSetRefObject != null) {
                upDataUI(getSetRefObject);
            }

        } else if (satelliteNnumber.getSelectedItem().toString().equals("亚洲三号")) {
            url = XTHttpUtil.GET_ONESTART_SETDOWN_THREE;
            urlOneStart = XTHttpUtil.GET_ONESTART_THREE;
            getOneStatrtString();
            satenum = "6";
            JSONObject getSetRefObject = mOneStartCache
                    .getAsJSONObject("saveGeneral" + satenum);
            if (getSetRefObject != null) {
                upDataUI(getSetRefObject);
            }

        } else if (satelliteNnumber.getSelectedItem().toString().equals("鑫诺五号")) {
            url = XTHttpUtil.GET_ONESTART_SETDOWN_FIVE;
            urlOneStart = XTHttpUtil.GET_ONESTART_FIVE;
            getOneStatrtString();
            satenum = "3";
            JSONObject getSetRefObject = mOneStartCache
                    .getAsJSONObject("saveGeneral" + satenum);
            if (getSetRefObject != null) {
                upDataUI(getSetRefObject);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 点击Sprinner查找
    private void catchOneStart(JSONObject jsonObject) {
        progresshow = true;
        showDia();
        JsonObjectRequest catchRequest = new JsonObjectRequest(Method.POST,
                "http://192.168.80.1:8005/api/moreadv/sate/onestar/catch",
                getOneStrartText(), new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "接收return回来的数据===》" + response.toString());
                pdDismiss(response);
                if (XTHttpJSON.getJSONString(response.toString())
                        .equals("0")) {
                    Toast.makeText(OneStartActivity.this, "对星成功", 0)
                            .show();
                    // loadData(response.toString());// 加载数据
                } else if (XTHttpJSON
                        .getJSONString(response.toString())
                        .equals("-1")) {
                    Toast.makeText(OneStartActivity.this, "设置失败", 0)
                            .show();
                }
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
        mRequestQueue01.add(catchRequest);
    }

    // 配置下发
    private void configurationDown() {
        progresshow = true;
        showDia();
        StringRequest set01 = new StringRequest(Method.GET, url,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // oneText.setText(response);
                        pdDismiss(response);
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OneStartActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(set01);
    }

    // 一键对星
    private void oneStar() {
        progresshow = true;
        showDia();
        StringRequest set02 = new StringRequest(Method.GET, urlOneStart,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "接收return回来的数据===》" + response);
                        // oneText.setText(response);
                        pdDismiss(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(OneStartActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(set02);
    }

    // 停止
    private void oneStarStop() {
        progresshow = true;
        showDia();
        StringRequest set03 = new StringRequest(Method.GET,
                XTHttpUtil.GET_ONESTART_STOP, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "接收return回来的数据===》" + response.toString());
                // oneText.setText(response);
                pdDismiss(response);

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(OneStartActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(set03);
    }

    // 收星
    private void oneStarStow() {
        progresshow = true;
        showDia();
        StringRequest set04 = new StringRequest(Method.GET,
                XTHttpUtil.GET_ONESTART_STOW, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "接收return回来的数据===》" + response.toString());
                // oneText.setText(response);
                pdDismiss(response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(OneStartActivity.this, "网络错误",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mRequestQueue.add(set04);
    }

    // 停止
    private void lyjOneStart() {
        progresshow = true;
        showDia();
        JsonObjectRequest set03 = new JsonObjectRequest(Method.GET,
                "http://192.168.80.1:8005/api/moreadv/sate/debug/stop", null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "接收return回来的数据===》" + response.toString());
                        pdDismiss(response);
                        if (XTHttpJSON.getJSONString(response.toString())
                                .equals("0")) {
                            Toast.makeText(OneStartActivity.this, "停止成功", 0)
                                    .show();
                            // loadData(response.toString());// 加载数据
                        } else if (XTHttpJSON
                                .getJSONString(response.toString())
                                .equals("-1")) {
                            Toast.makeText(OneStartActivity.this, "停止失败", 0)
                                    .show();
                        }
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
        mRequestQueue.add(set03);
    }

    // 点击退出
    public void oneStartOnFinish(View v) {
        finish();
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
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        threadOneStartTag = false;
        mRequestQueue01.stop();
        pd.dismiss();
        super.onDestroy();
    }

    private class TouchListenerImpl implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;

                case MotionEvent.ACTION_MOVE:
                    int scrollY = view.getScrollY();
                    int height = view.getHeight();
                    int scrollViewMeasuredHeight = mScrollView.getChildAt(0)
                            .getMeasuredHeight();
                    if (scrollY == 0) {
                        System.out.println("滑动到了顶端 view.getScrollY()=" + scrollY);
                    }
                    if ((scrollY + height) == scrollViewMeasuredHeight) {
                        System.out.println("滑动到了底部 scrollY=" + scrollY);
                        System.out.println("滑动到了底部 height=" + height);
                        System.out.println("滑动到了底部 scrollViewMeasuredHeight="
                                + scrollViewMeasuredHeight);
                    }
                    break;
            }
            return false;
        }
    }

    class SntCatch extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            aaaaaobject = getOneStrartText();
        }

        @Override
        protected String doInBackground(Void... params) {
            String posturl = "http://192.168.80.1:8005/api/moreadv/sate/onestar/catch";
            try {
                StringEntity jsnentity = new StringEntity(
                        aaaaaobject.toString(), "UTF-8");
                // HttpEntity reHttpEntity = new UrlEncodedFormEntity(posturl);

                HttpPost httpPost = new HttpPost(posturl);
                httpPost.setEntity(jsnentity);

                HttpClient client = new DefaultHttpClient();
                HttpResponse mhttpResponse = client.execute(httpPost);

                HttpEntity showEntity = mhttpResponse.getEntity();

                InputStream inputStream = showEntity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line = null;
                while (null != (line = reader.readLine())) {
                    // recvstr += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class OneStartNetWork extends AsyncTask<Void, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                return NetWorkUtils.getJson(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                loadData(result.toString());
            } else {
                return;
            }
        }
    }
}
