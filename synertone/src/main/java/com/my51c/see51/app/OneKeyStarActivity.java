package com.my51c.see51.app;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.came.viewbguilib.ButtonBgUi;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.Logger.LoggerSave;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.bean.DataModel;
import com.my51c.see51.app.bean.StarCodeModel;
import com.my51c.see51.app.http.SntAsyncHttpGet;
import com.my51c.see51.app.http.SntAsyncPost;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.ScreenUtil;
import com.my51c.see51.app.utils.SntSpUtils;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.SharedPreferenceManager;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;
import com.yzx.tools.DensityUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.my51c.see51.app.http.XTHttpUtil.GET_ONESTAR_STATE_ADDRESS;
import static com.my51c.see51.app.http.XTHttpUtil.POST_SATE_CATCH;

public class OneKeyStarActivity extends BaseActivity implements OnClickListener,
        OnItemSelectedListener {
    /**
     * 开始点击对星，onekeystar_progerss出现，然后对星完成后消失（），onekeystar_finish出现
     * 按钮onekeystar_btn_duixing_progress消失
     * ，onekeystar_btn_duixing_finish出现，在下方出现关于卫星的相关信息
     */
    protected static final String TAG = "OneKeyStarActivity";
    private LinearLayout onekeystar_progerss, onekeystar_finish, ll_level, ll_capability;
    private Spinner onekeystar_spinner_choose;
    private TextView onekeystar_tvleft_value, onekeystar_tvright_version,
            onekeystar_tv_getvalue, onekeystar_tv_getversion;// 得到Eb/No值与版本
    private TextView onekeystar_tv_getstarname;// 得到选择的卫星名
    private ImageView onekeystar_iv_getstarimage;// 对应的卫星图标
    private ImageView onekeystar_iv_duixingimage, iv_refresh;// 対星过程中改变的图片
    private ButtonBgUi onekeystar_btn_duixing_progress;
    private Button onekeystar_btn_duixing_finish;// 対星结束前和结束后的按钮
    private ListView onekeystar_lv;// 对星结束后得到卫星的相关信息
    private List<String> list;
    private AnimationDrawable rocketAnimation;

    private String msate = "";
    private JSONObject satejs;
    private SntAsyncPost mpostask;
    private SntAsyncHttpGet statustask;
    private boolean mSateFlag = false;
    private SntSateStatusQuery satequerytask;
    private int count = 0;
    private String starName, startGetName;
    private boolean mStateStatus = false;
    private int messageDelay = 900;
    private String mStarCatIp;
    private Gson gson;
    private RequestQueue mRequestQueue;
    private String mRx, maxRx, mTx, maxTx;
    private TextView mReceiveLevel, mCapability, mReceiveLevel1, mCapability1;
    private ProgressBar pb_progressbar, pb_progressbar1, p_progressbar, p_progressbar1;
    private HashMap<String, Toast> toaHashMap = new HashMap<>();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getCatParameter();
            }
        }

    };
    private HttpUtils http;
    private PopupWindow pWindow;
    private ListView starListView;
    private List<StarCodeModel> starCodeModels = new ArrayList<>();
    private List<StarCodeModel> searchStarModels = new ArrayList<>();
    private CommonAdapter<StarCodeModel> starAdapter;
    private EditText onekeystar_num_choose;
    protected StarCodeModel currentStar;
    private MyTextWatcher myTextWatcher;
    private String centerFreq;
    private String freq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_star);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String savedStar = SharedPreferenceManager.getString(mContext,
                "currentStar");
        if (savedStar != null) {
            currentStar = GsonUtils.fromJson(savedStar, StarCodeModel.class);
        }
        initview();
        initHttpData();
        initToasts();
        initStarList();
        initEvent();
        getModemIp();
    }

    private void initview() {
        TextView star_shoudong = (TextView) findViewById(R.id.star_shoudong);
        star_shoudong.setTypeface(AppData.fontXiti);
        TextView star_choosestar = (TextView) findViewById(R.id.star_choosestar);
        star_choosestar.setTypeface(AppData.fontXiti);
        ll_level = (LinearLayout) findViewById(R.id.ll_level);
        ll_capability = (LinearLayout) findViewById(R.id.ll_capability);
        mCapability = (TextView) findViewById(R.id.tv_capability);
        mReceiveLevel = (TextView) findViewById(R.id.tv_receive_level);
        mCapability1 = (TextView) findViewById(R.id.tv_capability1);
        mReceiveLevel1 = (TextView) findViewById(R.id.tv_receive_level1);
        pb_progressbar = (ProgressBar) findViewById(R.id.pb_progressbar);
        pb_progressbar1 = (ProgressBar) findViewById(R.id.pb_progressbar1);
        p_progressbar = (ProgressBar) findViewById(R.id.p_progressbar);
        p_progressbar1 = (ProgressBar) findViewById(R.id.p_progressbar1);
        onekeystar_progerss = (LinearLayout) findViewById(R.id.onekeystar_progerss);
        onekeystar_finish = (LinearLayout) findViewById(R.id.onekeystar_finish);
        /*onekeystar_tvleft_value = (TextView) findViewById(R.id.onekeystar_tvleft_value);
		onekeystar_tvright_version = (TextView) findViewById(R.id.onekeystar_tvright_version);*/
        //onekeystar_tv_getvalue = (TextView) findViewById(R.id.onekeystar_tv_getvalue);
        //onekeystar_tv_getversion = (TextView) findViewById(R.id.onekeystar_tv_getversion);
        onekeystar_tv_getstarname = (TextView) findViewById(R.id.onekeystar_tv_getstarname);
        onekeystar_tv_getstarname.setTypeface(AppData.fontXiti);
        onekeystar_iv_getstarimage = (ImageView) findViewById(R.id.onekeystar_iv_getstarimage);
        onekeystar_iv_duixingimage = (ImageView) findViewById(R.id.onekeystar_iv_duixing);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(this);
		/*String mCurrentStar = SharedPreferenceManager.getString(mContext, "currentStar");
		currentStar = GsonUtils.fromJson(mCurrentStar, StarCodeModel.class);*/
        //onekeystar_lv = (ListView) findViewById(R.id.onekeystar_lv);
        onekeystar_num_choose = (EditText) findViewById(R.id.onekeystar_num_choose);// 卫星编号
        if (currentStar != null) {
            onekeystar_num_choose.setText(currentStar.getSatename());
            onekeystar_num_choose.setSelection(currentStar.getSatename().length());
        } else {
            onekeystar_num_choose.setText("--");
        }
        onekeystar_btn_duixing_progress = (ButtonBgUi) findViewById(R.id.onekeystar_btn_duixing_progress);
        onekeystar_btn_duixing_finish = (Button) findViewById(R.id.onekeystar_btn_duixing_finish);

        list = new ArrayList<>();
        // 点击监听
        onekeystar_btn_duixing_progress.setOnClickListener(this);
        onekeystar_progerss.setOnClickListener(this);
        onekeystar_btn_duixing_finish.setOnClickListener(this);

    }

    @SuppressLint("ShowToast")
    @SuppressWarnings("unused")
    private void initToasts() {
        Toast toast1 = Toast.makeText(OneKeyStarActivity.this, "正在对星！", 0);
        Toast toast2 = Toast.makeText(OneKeyStarActivity.this, "对星命令发送成功", 0);
        Toast toast3 = Toast.makeText(OneKeyStarActivity.this, "连接网元服务器失败", 0);
        Toast toast4 = Toast.makeText(OneKeyStarActivity.this, "对星命令发送失败", 0);
        Toast toast5 = Toast.makeText(OneKeyStarActivity.this, "对星故障", 0);
        Toast toast6 = Toast.makeText(OneKeyStarActivity.this, "対星失败，请重新尝试！", 0);
        Toast toast7 = Toast.makeText(OneKeyStarActivity.this, "查询对星状态通信故障", 0);
        Toast toast8 = Toast.makeText(OneKeyStarActivity.this, "正在确认", 0);
        Toast toast9 = Toast.makeText(OneKeyStarActivity.this, "对星成功", 0);
        Toast toast10 = Toast.makeText(OneKeyStarActivity.this, "正在捕获", 0);
        Toast toast11 = Toast.makeText(OneKeyStarActivity.this, "正在跟踪", 0);
        Toast toast12 = Toast.makeText(OneKeyStarActivity.this, "查询对星 状态故障", 0);
        Toast toast13 = Toast.makeText(OneKeyStarActivity.this, "请先设置相关星位参数！", Toast.LENGTH_LONG);
        toast13.setGravity(Gravity.CENTER, 0, 0);
        Toast toast14 = Toast.makeText(OneKeyStarActivity.this, "DVB捕获确认中", Toast.LENGTH_SHORT);
        toaHashMap.put("正在对星！", toast1);
        toaHashMap.put("对星命令发送成功", toast2);
        toaHashMap.put("连接网元服务器失败", toast3);
        toaHashMap.put("对星命令发送失败", toast4);
        toaHashMap.put("对星故障", toast5);
        toaHashMap.put("対星失败，请重新尝试！", toast6);
        toaHashMap.put("查询对星状态通信故障", toast7);
        toaHashMap.put("正在确认", toast8);
        toaHashMap.put("对星成功", toast9);
        toaHashMap.put("正在捕获", toast10);
        toaHashMap.put("正在跟踪", toast11);
        toaHashMap.put("查询对星 状态故障", toast12);
        toaHashMap.put("请先设置相关星位参数！", toast13);
        toaHashMap.put("DVB捕获确认中", toast14);

    }

    private boolean isData = false;// 表示是否有数据,默认没有

    private void initHttpData() {
        gson = new Gson();
        http = new HttpUtils();
        http.configTimeout(5 * 1000);
        http.configSoTimeout(5 * 1000);
        http.configCurrentHttpCacheExpiry(500);

    }

    private void initEvent() {
        onekeystar_num_choose.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if ("--".equals(onekeystar_num_choose.getText().toString())) {
                            onekeystar_num_choose.setText("");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        pWindow.showAsDropDown(onekeystar_num_choose);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        myTextWatcher = new MyTextWatcher();
        onekeystar_num_choose.addTextChangedListener(myTextWatcher);
    }

    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            searchStarModels.clear();
            if ("".equals(s.toString())) {
                searchStarModels.addAll(starCodeModels);
            } else {
                for (StarCodeModel temp : starCodeModels) {
                    if (temp.getSatename().contains(s.toString())) {
                        searchStarModels.add(temp);
                    }
                }
            }
            getRemoveText();
            starAdapter.notifyDataSetChanged();
            setListViewHeight();
            if (pWindow != null && !pWindow.isShowing()) {
                pWindow.showAsDropDown(onekeystar_num_choose);
            }

        }

        private void getRemoveText() {
            if (searchStarModels.size() == 0) {
                onekeystar_num_choose.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    private void initStarList() {
        pWindow = new PopupWindow();
        pWindow.setWidth(LayoutParams.WRAP_CONTENT);
        pWindow.setHeight(LayoutParams.WRAP_CONTENT);
        pWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        pWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
        starListView = new ListView(this);
        starListView.setBackgroundResource(R.drawable.shape_bottom_corner__gray_bg);
        List<StarCodeModel> dbData = DataSupport.findAll(StarCodeModel.class);
        starCodeModels.clear();
        starCodeModels.addAll(dbData);
        searchStarModels.addAll(starCodeModels);
        starAdapter = new CommonAdapter<StarCodeModel>(this, R.layout.item_star_info, searchStarModels) {

            @Override
            protected void fillItemData(CommonViewHolder viewHolder,
                                        int position, StarCodeModel item) {
                viewHolder.setTextForTextView(R.id.tv_star_name, item.getSatename());
                viewHolder.setTextWidth(R.id.tv_star_name, onekeystar_num_choose.getWidth());

            }
        };
        starListView.setAdapter(starAdapter);
        pWindow.setBackgroundDrawable(new ColorDrawable());
        LinearLayout ll = new LinearLayout(mContext);
        ll.addView(starListView);
        pWindow.setContentView(ll);
        onekeystar_num_choose.post(new Runnable() {

            @Override
            public void run() {
                setListViewHeight();
                pWindow.setWidth(onekeystar_num_choose.getWidth());
            }
        });

        starListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onekeystar_num_choose.removeTextChangedListener(myTextWatcher);
                CommonAdapter<StarCodeModel> adapter = (CommonAdapter) parent.getAdapter();
                currentStar = adapter.getItem(position);
                SharedPreferenceManager.saveString(mContext, "starname", currentStar.getSatename());
                onekeystar_num_choose.setText(currentStar.getSatename());
                onekeystar_num_choose.setSelection(currentStar.getSatename().length());
                pWindow.dismiss();
                onekeystar_num_choose.addTextChangedListener(myTextWatcher);
                SharedPreferenceManager.saveString(mContext, "currentStar", GsonUtils.toJson(currentStar));

            }

        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int[] location = new int[2];
        onekeystar_num_choose.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (event.getX() < x || event.getX() > (x + onekeystar_num_choose.getWidth()) || event.getY() < y || event.getY() > (y + onekeystar_num_choose.getHeight())) {
            pWindow.dismiss();
        }

        return super.dispatchTouchEvent(event);
    }

    private void setListViewHeight() {
        int height = DensityUtil.dip2px(mContext, 42) * starAdapter.getCount();
        int scrrenH = (int) (ScreenUtil.getHight(mContext) * 0.55);
        int realH = height >= scrrenH ? scrrenH : height;
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) starListView.getLayoutParams();
        layoutParams.height = realH;
    }

    // 点击対星按钮，开始対星
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.onekeystar_btn_duixing_progress:
                animationShow();
                if (currentStar == null || currentStar.getSessionToken() == null) {
                    onekeystar_btn_duixing_progress.setClickable(true);
                    rocketAnimation.stop();
                    if (toaHashMap.get("请先设置相关星位参数！") != null) {
                        toaHashMap.get("请先设置相关星位参数！").show();
                    }
                } else {
                    //正在对星，设置对星图标不可点击，
                    onekeystar_btn_duixing_progress.setClickable(false);
                    //onekeystar_spinner_choose.setClickable(false);//spinner 不能点
                    //onekeystar_spinner_choose.setEnabled(false);
                    mStateStatus = true;
                    //showDia();
                    //sntCatch();
                    sateCatchPost();
                    if (toaHashMap.get("正在对星！") != null) {
                        toaHashMap.get("正在对星！").show();
                    }
                }
                break;
            case R.id.onekeystar_btn_duixing_finish:
                onekeystar_iv_duixingimage
                        .setBackgroundResource(R.drawable.onekeystar_iv_beforeduixing);
                onekeystar_finish.setVisibility(View.GONE);
                onekeystar_btn_duixing_progress.setVisibility(View.VISIBLE);
                onekeystar_btn_duixing_progress.setClickable(true);//对星按钮可以点
                //onekeystar_spinner_choose.setClickable(true);//spinner 能点
                //onekeystar_spinner_choose.setEnabled(true);
                //sntCatch();
                sateCatchPost();
                break;
            case R.id.iv_refresh:
                if(handler!=null){
                    handler.removeMessages(1);
                    getModemIp();
                }
                onekeystar_progerss.setVisibility(View.VISIBLE);
                onekeystar_btn_duixing_progress.setClickable(true);
                showDia();
                break;
            default:
                break;
        }
    }

    private void sntSateStatus() {

        mSateFlag = true;
        satequerytask = new SntSateStatusQuery();
        satequerytask.execute(GET_ONESTAR_STATE_ADDRESS);
        LoggerSave.requestLog(GET_ONESTAR_STATE_ADDRESS, GET_ONESTAR_STATE_ADDRESS);
    }


    /**
     * 対星过程中的动画
     */
    private void animationShow() {

        onekeystar_progerss.setVisibility(View.VISIBLE);
//				Toast.makeText(getApplicationContext(), "正在对星....", 1).show();

        // 开始対星
        onekeystar_iv_duixingimage
                .setBackgroundResource(R.drawable.rocket_thrust);
        rocketAnimation = (AnimationDrawable) onekeystar_iv_duixingimage
                .getBackground();
        rocketAnimation.start();
    }

    /**
     * 表示対星失败
     */
    private void failDuixing() {
        rocketAnimation.stop();
        onekeystar_iv_duixingimage
                .setBackgroundResource(R.drawable.onekeystar_iv_beforeduixing);
        if (toaHashMap.get("対星失败，请重新尝试！") != null) {
            toaHashMap.get("対星失败，请重新尝试！").show();
        }
        onekeystar_btn_duixing_progress.setClickable(true);//可以点击
        //onekeystar_spinner_choose.setClickable(true);//spinner 能点
        //onekeystar_spinner_choose.setEnabled(true);
    }

    /**
     * 表示対星成功动画停止，得到相关数据
     */
    private void successDuixing() {
        // 这里表示対星状态变化
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                rocketAnimation.stop();
                onekeystar_progerss.setVisibility(View.GONE);
                onekeystar_btn_duixing_progress.setVisibility(View.GONE);
                // 更换过程图
                onekeystar_iv_duixingimage
                        .setBackgroundResource(R.drawable.onekeystar_iv_duixing);
                //onekeystar_finish.setVisibility(View.VISIBLE);
                onekeystar_btn_duixing_progress.setClickable(true);
                //onekeystar_spinner_choose.setEnabled(true);
                //onekeystar_spinner_choose.setClickable(true);//spinner 能点
                onekeystar_tv_getstarname.setText(currentStar.getSatename());
                iv_refresh.setOnClickListener(null);
                if ("2".equals(currentStar.getMode())) {
                    onekeystar_finish.setVisibility(View.VISIBLE);
                    ll_level.setVisibility(View.GONE);
                    ll_capability.setVisibility(View.GONE);
                } else {
                    onekeystar_finish.setVisibility(View.VISIBLE);
                }

            }
        }, 3000);

        // 模拟数据
		/*for (int i = 0; i < 10; i++) {

			list.add("卫星。。。" + i);
		}
		ListAdapter adapter = new ArrayAdapter<>(getApplicationContext(),
				android.R.layout.simple_list_item_1, list);
		onekeystar_lv.setAdapter(adapter);*/
    }

    // sp选择监听
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        satejs = new JSONObject();
        if (onekeystar_spinner_choose.getSelectedItem().toString().equals("协同一号")) {
            msate = "sate1";
        } else if (onekeystar_spinner_choose.getSelectedItem().toString().equals("亚洲七号")) {
            msate = "sate2";
        }/*else if(onekeystar_spinner_choose.getSelectedItem().toString().equals("鑫诺五号")){
			msate = "sate3";
		}*/

        SntSpUtils.GenSptoJS(OneKeyStarActivity.this, msate, satejs);
        //starName=onekeystar_spinner_choose.getSelectedItem().toString();
        //SharedPreferenceManager.saveString(this, "starname", starName);
        //onekeystar_tv_getstarname.setText(SharedPreferenceManager.getString(this, "starname"));
        //startGetName=SharedPreferenceManager.getString(this, "starname");
        //onekeystar_tv_getversion.setText(onekeystar_tvright_version.getText().toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 返回
    public void oneKeyStarOnFinish(View view) {
        mSateFlag = false;
        mStateStatus = false;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSateFlag = false;
        mStateStatus = false;
        try {
            Iterator<Entry<String, Toast>> iter = toaHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Toast> entry = iter.next();
                Toast toast = entry.getValue();
                Field field = toast.getClass().getDeclaredField("mTN");
                field.setAccessible(true);
                Object obj = field.get(toast);
                java.lang.reflect.Method m = obj.getClass().getDeclaredMethod("hide");
                m.invoke(obj);
                iter.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SntSateStatusQuery extends AsyncTask<String, String, String> {
        private String recv = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            count = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpGet hget = new HttpGet(params[0]);
            HttpClient hclient = new DefaultHttpClient();
            HttpResponse respone;
            while (mSateFlag) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    respone = hclient.execute(hget);
                    recv = EntityUtils.toString(respone.getEntity());
                    LoggerSave.responseLog(params[0], recv);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                publishProgress(recv);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (recv.equals("")) {
                if (toaHashMap.get("查询对星状态通信故障") != null) {
                    toaHashMap.get("查询对星状态通信故障").show();
                }
                mSateFlag = false;
            } else {
                try {
                    JSONObject satetusjs = new JSONObject(recv);
                    //LoggerSave.responseLog("TT",satetusjs.toString()+"count="+count);
                    if (satetusjs.getString("code").equals("0")) {
                        if ("2".equals(currentStar.getMode()) && mSateFlag) {
                            if (toaHashMap.get("DVB捕获确认中") != null) {
                                toaHashMap.get("DVB捕获确认中").show();
                            }
                        } else {
                            //onekeystar_spinner_choose.setClickable(false);//spinner 不能点
                            if (toaHashMap.get("正在确认") != null && mSateFlag) {
                                toaHashMap.get("正在确认").show();
                            }
                        }
                        //onekeystar_spinner_choose.setEnabled(false);
                        onekeystar_btn_duixing_progress.setClickable(false);
                        if (count > 3) {
                            mSateFlag = false;
                            successDuixing();
                            if (toaHashMap.get("对星成功") != null) {
                                toaHashMap.get("对星成功").show();
                            }
                            //onekeystar_spinner_choose.setClickable(true);//spinner 能点
                            //onekeystar_spinner_choose.setEnabled(true);
                            onekeystar_btn_duixing_progress.setClickable(true);
                        } else {
                            count++;
                        }
                    } else if (satetusjs.getString("code").equals("1")) {
                        if ("2".equals(currentStar.getMode())) {
                            if (toaHashMap.get("DVB捕获确认中") != null) {
                                toaHashMap.get("DVB捕获确认中").show();
                            }
                        } else {
                            if (toaHashMap.get("正在捕获") != null) {
                                toaHashMap.get("正在捕获").show();
                            }
                        }
                        //onekeystar_spinner_choose.setEnabled(false);
                        onekeystar_btn_duixing_progress.setClickable(false);
                        //onekeystar_spinner_choose.setClickable(false);//spinner 能点
                        count = 0;
                    } else if (satetusjs.getString("code").equals("2")) {
                        Toast.makeText(getApplicationContext(), "卫星未找到", 0).show();
                        mSateFlag = false;//对星失败 不需要继续查询了
                        failDuixing();
                    } else if (satetusjs.getString("code").equals("3")) {
                        if (toaHashMap.get("正在跟踪") != null) {
                            toaHashMap.get("正在跟踪").show();
                        }
                        //onekeystar_spinner_choose.setEnabled(false);
                        onekeystar_btn_duixing_progress.setClickable(false);
                        //onekeystar_spinner_choose.setClickable(false);//spinner 不能点
                    } else if (satetusjs.getString("code").equals("-1")) {
                        if (satetusjs.optString("msg").equals("acu_occupy")) {
                            showMutualDialog();
                        } else {
                            if (toaHashMap.get("查询对星 状态故障") != null) {
                                toaHashMap.get("查询对星 状态故障").show();
                            }
                            mSateFlag = false;//查询状态失败 不需要继续查询了
                            failDuixing();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mSateFlag = false;
        mStateStatus = false;
        return super.onKeyDown(keyCode, event);
    }

    private void getModemIp() {
        String getModemUrl = XTHttpUtil.GET_MODEM_IP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getModemUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        //Toast.makeText(ManualControlActivity.this, "获取ip地址成功", 0).show();
                    } else {
                        //Toast.makeText(ManualControlActivity.this, "获取ip地址失败", 0).show();
                    }
                    mStarCatIp = jsonObject.getString("ip");
                    if (mStarCatIp != null) {
                        getCatParameter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    private void getCatParameter() {
        String getCatParameterUrl = "http://" + mStarCatIp + "/cgi-bin/modemstatus/";
        http.send(HttpRequest.HttpMethod.GET, getCatParameterUrl, null,
                new RequestCallBack<JSONObject>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessageDelayed(1, messageDelay);
                    }

                    @SuppressWarnings("rawtypes")
                    @Override
                    public void onSuccess(ResponseInfo responseInfo) {
                        try {
                            dismissDia();
                            String resInfoStr = responseInfo.result.toString();
                            DataModel dataModel = gson.fromJson(resInfoStr, DataModel.class);
                            mRx = dataModel.getRx();
                            maxRx = dataModel.getMaxrx();
                            mTx = dataModel.getTx();
                            maxTx = dataModel.getMaxtx();
                            mReceiveLevel.setText(mRx + "/" + maxRx + " dB");
                            mCapability.setText(mTx + "/" + maxTx + " dB");
                            mReceiveLevel1.setText(mRx + "/" + maxRx + " dB");
                            mCapability1.setText(mTx + "/" + maxTx + " dB");
                            if (mRx.substring(0, 1).equals("-") || mRx.substring(0, 1).equals("﹣") || mRx.substring(0, 1).equals("－") || mRx.substring(0, 1).equals("﹣")) {
                                pb_progressbar.setProgress(0);
                                p_progressbar.setProgress(0);
                            } else {
                                pb_progressbar.setProgress((int) (((Double.parseDouble(mRx) / Double.parseDouble(maxRx)) * 100)));
                                p_progressbar.setProgress((int) (((Double.parseDouble(mRx) / Double.parseDouble(maxRx)) * 100)));

                            }
                            if (mTx.substring(0, 1).equals("-") || mTx.substring(0, 1).equals("﹣") || mTx.substring(0, 1).equals("－") || mTx.substring(0, 1).equals("﹣")) {
                                pb_progressbar1.setProgress(0);
                                p_progressbar1.setProgress(0);
                            } else {
                                pb_progressbar1.setProgress((int) (((Double.parseDouble(mTx) / Double.parseDouble(maxTx)) * 100)));
                                p_progressbar1.setProgress((int) (((Double.parseDouble(mTx) / Double.parseDouble(maxTx)) * 100)));
                            }
                            handler.sendEmptyMessageDelayed(1, messageDelay);
                        } catch (Exception e) {
                            handler.sendEmptyMessageDelayed(1, messageDelay);
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void sateCatchPost() {
        animationShow();
        //dismissDia();
        String sateCatchUrl = POST_SATE_CATCH;
        if(StringUtils.isEmpty(centerFreq)&&StringUtils.isEmpty(freq)){
            centerFreq = currentStar.getCenterFreq();
            freq = currentStar.getFreq();
        }
        String mLnb = SharedPreferenceManager.getString(this, "lnb");
        if (!StringUtils.isEmpty(centerFreq) && !StringUtils.isEmpty(mLnb)) {
            BigDecimal lCenterFreq = new BigDecimal(centerFreq);
            currentStar.setCenterFreq(lCenterFreq.subtract(new BigDecimal(mLnb)).multiply(new BigDecimal(100)).toString());
        }
        if (!StringUtils.isEmpty(freq)) {
            currentStar.setFreq(new BigDecimal(freq).multiply(new BigDecimal(100)).toString());
        }
        String mCurrentStar = gson.toJson(currentStar);
        try {
            JSONObject jsonObject = new JSONObject(mCurrentStar);
            LoggerSave.requestLog(POST_SATE_CATCH, jsonObject.toString());
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    sateCatchUrl, jsonObject,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                LoggerSave.responseLog(POST_SATE_CATCH, response.toString());
                                String code = response.getString("code");
                                String msg = response.optString("msg");
                                if (code.equals("0")) {
                                    if (mStateStatus) {
                                        sntSateStatus();
                                    }
                                    if (toaHashMap.get("对星命令发送成功") != null) {
                                        toaHashMap.get("对星命令发送成功").show();
                                    }
                                } else if (code.equals("-1")) {
                                    if (msg.equals("acu_occupy")) {
                                        showMutualDialog();
                                    } else {
                                        if (toaHashMap.get("对星命令发送失败") != null) {
                                            toaHashMap.get("对星命令发送失败").show();
                                        }
                                        failDuixing();
                                    }
                                } else if (code.equals("-2")) {
                                    showLoginDialog();
                                    failDuixing();
                                } else {
                                    if (toaHashMap.get("对星故障") != null) {
                                        toaHashMap.get("对星故障").show();
                                    }
                                    failDuixing();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (toaHashMap.get("连接网元服务器失败") != null) {
                        toaHashMap.get("连接网元服务器失败").show();
                    }
                    failDuixing();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initStarList();
            }
        }, 50);
        if (pWindow != null && pWindow.isShowing()) {
            pWindow.dismiss();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pWindow.showAsDropDown(onekeystar_num_choose);
                }
            }, 200);

        }


    }
}