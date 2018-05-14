package com.my51c.see51.app.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.my51c.see51.BaseActivity;
import com.my51c.see51.Logger.LoggerSave;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.bean.StarCodeModel;
import com.my51c.see51.app.http.SntAsyncHttpGet;
import com.my51c.see51.app.http.SntAsyncHttpGet.HpOverListener;
import com.my51c.see51.app.http.SntAsyncPost;
import com.my51c.see51.app.http.SntAsyncPost.PostOverHandle;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.ChechIpMask;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.ScreenUtil;
import com.my51c.see51.app.utils.SntSpUtils;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.SharedPreferenceManager;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;
import com.yzx.tools.DensityUtil;

import org.afinal.simplecache.ACache;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.my51c.see51.app.http.XTHttpUtil.GET_COMPASS;
import static com.my51c.see51.app.http.XTHttpUtil.GET_SATEADV_ARG;
import static com.my51c.see51.app.http.XTHttpUtil.GET_STOP;
import static com.my51c.see51.app.http.XTHttpUtil.POST_ARGSET_ADV;
import static com.my51c.see51.app.http.XTHttpUtil.QUERY_COMPASS_RESULT;


/*参数设置
 * */
public class SuperSetRefActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {
    private final static String TAG = "SuperSetRefActivity---->";
    private Spinner mSatelliteNum, mBandwidth, mStatMode, mPoraization,mModulator, mLocatemode,
            mWXJD,mDQJD,mDQJD1,mDQWD;
    private String strXinRate,strZaiRate,strCenter,strSignRate;
    private EditText// mEdItude, //经度
            //	 mEdRate, //频率
            mCurrentLongItude,//当前纬度
            mCurrentLatitude,//当前经度
            mCurrentLatitude1,//经度
            mEdItude,
            mThreshold;//rssi门限
    //mEdRecvpol, mSendpol;  lyj-del 0815 删除收发极化角输入
    private ButtonBgUi mBtnSave;
    private ButtonBgUi mBtnSaveGeneral;
    private ButtonBgUi mBtdefault;
    private ButtonBgUi mBtnStop;
    private Button mBtCompass;
    private TextView mStartNum;
    private RequestQueue mRequestQueue;
    private ACache mACache;
    @SuppressWarnings("unused")
    private String SET_REF_TAG = "setreftag";
    private LinearLayout mDebug_Control;
    private LinearLayout  ll_xinbiao_rate,ll_zaibo_rate,ll_ref_bandwidth,ll_pinbao,ll_compass;
    private LinearLayout ll_center_rate;
    private LinearLayout ll_sign_rate;
    private TextView tv_center_rate;
    private TextView tv_sign_rate;
    private EditText et_center_rate;
    private EditText et_sign_rate;
    private EditText super_xinbiao_rate, super_zaibo_rate,super_ref_fuyang;
    private TextView fuyang_tv,zaibo_tv,xinbiao_tv,tv_compass;
    private View tv_ref_bandwidth;
    private String intStrStatMode, intPoraization, intLocatemode,intBandwidth,mCode;
    private int mPosition=0;
    private SntAsyncHttpGet advgettask;
    private SntAsyncPost agrtask,sategettask;
    private JSONObject genjs,advjs,tmpjs,numjs;
    private String mSate = "";
    private Spinner super_ref_lub;
    private EditText super_ref_buc;
    private String starGetMode,starMode,mPing;
    private ToggleButton mToggleButton;
    private ScrollView scrollview_compass;
    private Gson gson;
    private boolean isAddStar=false;
    private boolean is_longitude=false,
            is_rate=false,
            is_rssiLimit=false,
            is_currLongitude=false,
            is_currLatitude=false,
            is_currentElevoff=false,
            is_currLnb=false;

    private String  strSatelliteNum01,
            strSatelliteNum,
            strEdItude,
            strEdRate,
            strRefLub,
            strStatMode,
            strBandwidth,
            strPoraization,
            strModulator,
            strThreshold,
            strRecvpol,
            strSendpol,
            strLocatemode,
            strCurrentLatitude,
            strCurrentLongItude,
            strCurrentLongItude1,
            strCurrentElevoff,
            mSateName,
            sateName,
            startGetName,
            mSatelon,
            strCurrentLnb;
    private HashMap<String, Toast> toaHashMap=new HashMap<>();
    private int messageDelay = 5000;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 2){
                queryCompassResult();
            }
        }
    };
    private PopupWindow pWindow;
    private ListView starListView;
    private List<StarCodeModel> starCodeModels=new ArrayList<>();
    private List<StarCodeModel> searchStarModels=new ArrayList<>();
    private CommonAdapter<StarCodeModel> starAdapter;
    private MyTextWatcher myTextWatcher;
    private EditText super_ref_numner;
    protected StarCodeModel currentStar;
    private String mType,mToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superset_ref_activity);
        if(AppData.accountModel!=null) {
            mToken = AppData.accountModel.getSessionToken();
        }
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        gson=new Gson();
        mType = getIntent().getStringExtra("compass");
        initView();
        initData();
        String savedStar = SharedPreferenceManager.getString(mContext,
                "currentStar");
        if (savedStar != null) {
            currentStar = GsonUtils.fromJson(savedStar, StarCodeModel.class);
            if (currentStar != null) {
                try {
                    initDataView();
                    initStarList();
                } catch (Exception e) {
                    initStarList();
                    e.printStackTrace();
                }
            }
        }else{
            initStarList();
        }
        initEvent();
    }
    private void initData() {
        if("C系列".equals(mType)){
            ll_compass.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        initToasts();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int[] location = new int[2];
        super_ref_numner.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if(event.getX() < x || event.getX() > (x + super_ref_numner.getWidth()) || event.getY() < y || event.getY() > (y + super_ref_numner.getHeight())){
            pWindow.dismiss();
        }

        return super.dispatchTouchEvent(event);
    }

    private void initEvent() {
        super_xinbiao_rate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(super_xinbiao_rate.getText().toString())) {
                        super_xinbiao_rate.setText("");
                    }
                }

            }
        });

        super_zaibo_rate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(super_zaibo_rate.getText().toString())) {
                        super_zaibo_rate.setText("");
                    }
                }

            }
        });
        et_center_rate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(et_center_rate.getText().toString())) {
                        et_center_rate.setText("");
                    }
                }

            }
        });
        et_sign_rate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(et_sign_rate.getText().toString())) {
                        et_sign_rate.setText("");
                    }
                }

            }
        });
        super_ref_numner.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if("--".equals(super_ref_numner.getText().toString())){
                            super_ref_numner.setText("");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        pWindow.showAsDropDown(super_ref_numner);

                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        myTextWatcher = new MyTextWatcher();
        super_ref_numner.addTextChangedListener(myTextWatcher);

    }
    protected void showDelDialog(final StarCodeModel starCodeModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定删除卫星");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DataSupport.delete(StarCodeModel.class, mPosition);
                starCodeModel.delete();
                searchStarModels.remove(starCodeModel);
                starCodeModels.remove(starCodeModel);
                starAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

    }
    class MyTextWatcher implements  TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            searchStarModels.clear();
            if("".equals(s.toString())){
                searchStarModels.addAll(starCodeModels);
            }else{
                for(StarCodeModel temp:starCodeModels){
                    if(temp.getSatename().contains(s.toString())){
                        searchStarModels.add(temp);
                    }
                }
            }
            getRemoveText();
            setListViewHeight();
            starAdapter.notifyDataSetChanged();
            if(pWindow!=null&&!pWindow.isShowing()){
                pWindow.showAsDropDown(super_ref_numner);
            }

        }

        private void getRemoveText() {
            if(searchStarModels.size()==0){
                super_ref_numner.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
    private void initSatelliteData() {
        setSateLon();
        String mode=mStatMode.getSelectedItem().toString();
        switch (mode){
            case "信标":
                super_xinbiao_rate.setText(currentStar.getFreq());
                break;
            case "载波":
                if(!StringUtils.isEmpty(currentStar.getBw())){
                    mBandwidth.setSelection(Integer.parseInt(currentStar.getBw()));
                }
                super_zaibo_rate.setText(currentStar.getZfreq());
                break;
            case "DVB":
                setDVBViewData();
                break;

        }
    }
    private void setSateLon() {
        String _satelon=ChechIpMask.numDigite(currentStar.getSatelon(), 1);//保留一位有效数字。
        //SharedPreferenceManager.saveString(mContext, "satelon", _satelon);
        //如果带负号
        if(_satelon.substring(0,1).equals("-")||_satelon.substring(0,1).equals("﹣")||_satelon.substring(0,1).equals("－")||_satelon.substring(0,1).equals("﹣")){

            _satelon=_satelon.substring(1);
            mWXJD.setSelection(1);
        }else{
            mWXJD.setSelection(0);
        }
        mEdItude.setText(_satelon);
    }
    private void setDVBViewData() {
        if(currentStar!=null){
            et_center_rate.setText(currentStar.getCenterFreq());
            et_sign_rate.setText(currentStar.getSignRate());
        }
    }
    protected void showAddDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_rename_device, null);
        final EditText et_star_name = (EditText) v.findViewById(R.id.et_star_name);
        et_star_name.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (arg1) {
                    if ("--".equals(et_star_name.getText().toString())) {
                        et_star_name.setText("");
                    }
                }

            }
        });
        final EditText mCurrentLatitude1 = (EditText) v.findViewById(R.id.et_star_lat_lng);
        mCurrentLatitude1.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(mCurrentLatitude1.getText().toString())) {
                        mCurrentLatitude1.setText("");
                    }
                }

            }
        });
        mDQJD1=(Spinner) v.findViewById(R.id.super_ref_mdqjd1);
        mDQJD1.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.e_w)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setTitle("添加卫星");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sateName=et_star_name.getText().toString();
                if ((!"".equals(sateName))&&(!"--".equals(sateName))) {
                    if(ChechIpMask.a2b(mCurrentLatitude1.getText().toString(), 0, 180)){
                        String _strCurrentLongItude1=ChechIpMask.numDigite(mCurrentLatitude1.getText().toString(), 2);
        			/*if(mDQJD1.getSelectedItemPosition()==0){
        				Toast.makeText(SuperSetRefActivity.this, "对不起，请选择东经还是西经！！",0).show();
        			}else*/
                        if(mDQJD1.getSelectedItemPosition()==1){
                            _strCurrentLongItude1="-"+_strCurrentLongItude1;
                            strCurrentLongItude1 =_strCurrentLongItude1;
                        }else if(mDQJD1.getSelectedItemPosition()==0){
                            strCurrentLongItude1 =_strCurrentLongItude1;
                        }
                        StarCodeModel s1=new StarCodeModel();
                        s1.setSatename(sateName);
                        s1.setSatelon(strCurrentLongItude1);
                        s1.setAdd(true);
                        s1.save();
                        searchStarModels.add(searchStarModels.size(), s1);
                        starCodeModels.add(starCodeModels.size(), s1);
                        starAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的经度不合法！！",0).show();
                    }
                }else{
                    Toast.makeText(SuperSetRefActivity.this, "卫星编号不能为空",0).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

    }
    private void initStarList() {
        pWindow=new PopupWindow();
        pWindow.setWidth(LayoutParams.WRAP_CONTENT);
        pWindow.setHeight(LayoutParams.WRAP_CONTENT);
        pWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        pWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
        starListView =new ListView(this);
            List<StarCodeModel> dbData = DataSupport.findAll(StarCodeModel.class);
            starCodeModels.clear();
            starCodeModels.addAll(dbData);
            searchStarModels.addAll(starCodeModels);
        starAdapter=new CommonAdapter<StarCodeModel>(this,R.layout.item_star_info,searchStarModels) {

            @Override
            protected void fillItemData(CommonViewHolder viewHolder,
                                        int position, StarCodeModel item) {
                viewHolder.setTextForTextView(R.id.tv_star_name,item.getSatename());
				/*if(searchStarModels.size()>12){
					isAddStar=true;
				}*/

            }
        };
        starListView.setAdapter(starAdapter);
        pWindow.setBackgroundDrawable(new ColorDrawable());
        //pWindow.setOutsideTouchable(true);
        final LinearLayout ll=new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.drawable.shape_bottom_corner__gray_bg);
        ll.addView(starListView);
        View adView = getLayoutInflater().inflate(R.layout.item_star_info, null);
        adView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        TextView tv_star_name= (TextView) adView.findViewById(R.id.tv_star_name);
        tv_star_name.setText("添加更多");
		/*View lineView=new View(mContext);
		lineView.setBackgroundColor(getResources().getColor(R.color.white));
		ll.addView(lineView);
		LinearLayout.LayoutParams lineParms=(android.widget.LinearLayout.LayoutParams) lineView.getLayoutParams();
		lineParms.height=DensityUtil.dip2px(mContext, 1);
		lineParms.width=LinearLayout.LayoutParams.MATCH_PARENT;*/
        ll.addView(adView);
        pWindow.setContentView(ll);
        super_ref_numner.post(new Runnable() {
            @Override
            public void run() {
                setListViewHeight();
                pWindow.setWidth(super_ref_numner.getWidth());
            }
        });

        starListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                CommonAdapter<StarCodeModel> adapter = (CommonAdapter) parent.getAdapter();
                StarCodeModel item = adapter.getItem(position);
                if (item.isAdd() && (position > 43)) {
                    showDelDialog(adapter.getItem(position));
                }
                return false;
            }
        });
        starListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                strSatelliteNum=String.valueOf(position+1);
                super_ref_numner.removeTextChangedListener(myTextWatcher);
                CommonAdapter<StarCodeModel> adapter=(CommonAdapter) parent.getAdapter();
                currentStar=adapter.getItem(position);
                //SharedPreferenceManager.saveString(mContext, "starname", currentStar.getSatename());
                initSatelliteData();
                pWindow.dismiss();
                super_ref_numner.setText(currentStar.getSatename());
                super_ref_numner.setSelection(currentStar.getSatename().length());
                super_ref_numner.addTextChangedListener(myTextWatcher);
                try{
                    mPoraization.setSelection(Integer.parseInt(currentStar.getType()));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }
    @SuppressWarnings("unused")
    @SuppressLint("ShowToast")
    private void initToasts() {
        Toast toast=Toast.makeText(SuperSetRefActivity.this, "获取纬度成功！", 0);
        Toast toast1=Toast.makeText(getApplicationContext(),"获取天线参数失败", 0);
        Toast toast2=Toast.makeText(getApplicationContext(), "获取定位方式失败", 0);
        Toast toast3=Toast.makeText(getApplicationContext(), "获取定位方式成功", 0);
        Toast toast4=Toast.makeText(SuperSetRefActivity.this, "RSSI门限值查询成功！", 0);
        Toast toast5=Toast.makeText(SuperSetRefActivity.this, "RSSI门限值查询失败！", 0);
        Toast toast6=Toast.makeText(SuperSetRefActivity.this, "获取经度成功！", 0);
        Toast toast7=Toast.makeText(SuperSetRefActivity.this, "获取俯仰补偿角成功！", 0);
        Toast toast8=Toast.makeText(SuperSetRefActivity.this, "获取俯仰补偿角失败！", 0);
        Toast toast9=Toast.makeText(SuperSetRefActivity.this, "获取LNB成功！", 0);
        Toast toast10=Toast.makeText(SuperSetRefActivity.this, "获取LNB失败！", 0);
        Toast toast11=Toast.makeText(SuperSetRefActivity.this, "获取经度失败！", 0);
        Toast toast12=Toast.makeText(SuperSetRefActivity.this, "获取纬度失败！", 0);
        Toast toast13=Toast.makeText(SuperSetRefActivity.this, "ping包参数查询失败", 0);
        Toast toast14=Toast.makeText(SuperSetRefActivity.this, "ping包参数查询成功", 0);
        Toast toast15=Toast.makeText(SuperSetRefActivity.this, "ping包参数设置成功", 0);
        Toast toast16=Toast.makeText(SuperSetRefActivity.this, "ping包参数设置失败", 0);
        Toast toast17=Toast.makeText(SuperSetRefActivity.this, "俯仰补偿角设置成功", 0);
        Toast toast18=Toast.makeText(SuperSetRefActivity.this, "俯仰补偿角设置失败", 0);
        Toast toast19=Toast.makeText(SuperSetRefActivity.this, "经纬度设置失败", 0);
        Toast toast20=Toast.makeText(SuperSetRefActivity.this, "经纬度设置成功", 0);
        Toast toast21=Toast.makeText(SuperSetRefActivity.this, "定位方式设置成功", 0);
        Toast toast22=Toast.makeText(SuperSetRefActivity.this, "定位方式设置失败", 0);
        Toast toast23=Toast.makeText(SuperSetRefActivity.this, "RSSI保存成功！", 0);
        Toast toast24=Toast.makeText(SuperSetRefActivity.this, "RSSI保存失败", 0);
        Toast toast25=Toast.makeText(SuperSetRefActivity.this, "LNB设置成功", 0);
        Toast toast26=Toast.makeText(SuperSetRefActivity.this, "LNB设置失败", 0);
        Toast toast27=Toast.makeText(getApplicationContext(),"连接网元服务器失败", 0);
        Toast toast28=Toast.makeText(getApplicationContext(),"保存失败,请检查设备！", 0);
        Toast toast29=Toast.makeText(getApplicationContext(),"星位参数保存成功", 0);
        Toast toast30=Toast.makeText(getApplicationContext(),"对不起，您输入的经度不合法！！", 0);
        Toast toast31=Toast.makeText(getApplicationContext(),"对不起，您输入的纬度不合法！！", 0);
        Toast toast32=Toast.makeText(getApplicationContext(),"对不起，您输入的俯仰补偿角不合法！！", 0);
        Toast toast33=Toast.makeText(getApplicationContext(),"抱歉，接收到的RSSI格式不正确！！", 0);
        Toast toast34=Toast.makeText(getApplicationContext(),"抱歉，接收到的当前经度格式不正确！！", 0);
        Toast toast35=Toast.makeText(getApplicationContext(),"抱歉，接收到的当前纬度格式不正确！！", 0);
        Toast toast36=Toast.makeText(getApplicationContext(),"抱歉，接收到的俯仰补偿角格式不正确！！", 0);
        Toast toast37=Toast.makeText(getApplicationContext(),"抱歉，接收到的经度格式不正确！！", 0);
        Toast toast38=Toast.makeText(getApplicationContext(),"抱歉，接收到的频率格式不正确！！", 0);
        Toast toast39=Toast.makeText(getApplicationContext(),"对不起，您输入的RSSI不合法！！", 0);
        Toast toast40=Toast.makeText(getApplicationContext(),"执行了保存！！", 0);
        Toast toast41=Toast.makeText(getApplicationContext(),"电子罗盘初始化命令发送成功", 0);
        toast41.setGravity(Gravity.CENTER, 0, 0);
        Toast toast42=Toast.makeText(getApplicationContext(),"电子罗盘初始化命令发送失败", 0);
        toast42.setGravity(Gravity.CENTER, 0, 0);
        Toast toast43=Toast.makeText(getApplicationContext(),"初始化设置成功", 0);
        toast43.setGravity(Gravity.CENTER, 0, 0);
        Toast toast44=Toast.makeText(getApplicationContext(),"正在执行电子罗盘初始化,请耐心等候！", Toast.LENGTH_LONG);
        toast44.setGravity(Gravity.CENTER, 0, 0);
        Toast toast45=Toast.makeText(getApplicationContext(),"任务未执行", 0);
        toast45.setGravity(Gravity.CENTER, 0, 0);
        Toast toast46=Toast.makeText(getApplicationContext(),"初始化设置失败", 0);
        toast46.setGravity(Gravity.CENTER, 0, 0);
        Toast toast47=Toast.makeText(getApplicationContext(),"电子罗盘校准命令发送成功", 0);
        toast47.setGravity(Gravity.CENTER, 0, 0);
        Toast toast48=Toast.makeText(getApplicationContext(),"电子罗盘校准命令发送失败", 0);
        toast48.setGravity(Gravity.CENTER, 0, 0);
        Toast toast49=Toast.makeText(getApplicationContext(),"电子罗盘校准成功", 0);
        toast49.setGravity(Gravity.CENTER, 0, 0);
        Toast toast50=Toast.makeText(getApplicationContext(),"正在执行电子罗盘校准,请耐心等候！", Toast.LENGTH_LONG);
        toast50.setGravity(Gravity.CENTER, 0, 0);
        Toast toast51=Toast.makeText(getApplicationContext(),"电子罗盘校准失败", 0);
        toast51.setGravity(Gravity.CENTER, 0, 0);
        Toast toast52=Toast.makeText(getApplicationContext(),"收到非正常回复", 0);
        toast52.setGravity(Gravity.CENTER, 0, 0);
        Toast toast53=Toast.makeText(getApplicationContext(),"超时未收到回复", 0);
        toast53.setGravity(Gravity.CENTER, 0, 0);
        Toast toast54=Toast.makeText(getApplicationContext(),"天线类型不支持", 0);
        Toast toast55=Toast.makeText(getApplicationContext(),"此天线只支持手动定位方式", 0);
        toaHashMap.put("获取纬度成功！", toast);
        toaHashMap.put("获取天线参数失败", toast1);
        toaHashMap.put("获取定位方式失败", toast2);
        toaHashMap.put("获取定位方式成功", toast3);
        toaHashMap.put("RSSI门限值查询成功！", toast4);
        toaHashMap.put("RSSI门限值查询失败！", toast5);
        toaHashMap.put("获取经度成功！", toast6);
        toaHashMap.put("获取俯仰补偿角成功！", toast7);
        toaHashMap.put("获取俯仰补偿角失败！", toast8);
        toaHashMap.put("获取LNB成功！", toast9);
        toaHashMap.put("获取LNB失败！", toast10);
        toaHashMap.put("获取经度失败！", toast11);
        toaHashMap.put("获取纬度失败！", toast12);
        toaHashMap.put("ping包参数查询失败", toast13);
        toaHashMap.put("ping包参数查询成功", toast14);
        toaHashMap.put("ping包参数设置成功", toast15);
        toaHashMap.put("ping包参数设置失败", toast16);
        toaHashMap.put("俯仰补偿角设置成功", toast17);
        toaHashMap.put("俯仰补偿角设置失败", toast18);
        toaHashMap.put("经纬度设置失败", toast19);
        toaHashMap.put("经纬度设置成功", toast20);
        toaHashMap.put("定位方式设置成功", toast21);
        toaHashMap.put("定位方式设置失败", toast22);
        toaHashMap.put("RSSI保存成功！", toast23);
        toaHashMap.put("RSSI保存失败", toast24);
        toaHashMap.put("LNB设置成功", toast25);
        toaHashMap.put("LNB设置失败", toast26);
        toaHashMap.put("连接网元服务器失败", toast27);
        toaHashMap.put("保存失败,请检查设备！", toast28);
        toaHashMap.put("星位参数保存成功", toast29);
        toaHashMap.put("对不起，您输入的经度不合法！！", toast30);
        toaHashMap.put("对不起，您输入的纬度不合法！！", toast31);
        toaHashMap.put("对不起，您输入的俯仰补偿角不合法！！", toast32);
        toaHashMap.put("抱歉，接收到的RSSI格式不正确！！", toast33);
        toaHashMap.put("抱歉，接收到的当前经度格式不正确！！", toast34);
        toaHashMap.put("抱歉，接收到的当前纬度格式不正确！！", toast35);
        toaHashMap.put("抱歉，接收到的俯仰补偿角格式不正确！！", toast36);
        toaHashMap.put("抱歉，接收到的经度格式不正确！！", toast37);
        toaHashMap.put("抱歉，接收到的频率格式不正确！！", toast38);
        toaHashMap.put("对不起，您输入的RSSI不合法！！", toast39);
        toaHashMap.put("执行了保存！！", toast40);
        toaHashMap.put("电子罗盘初始化命令发送成功", toast41);
        toaHashMap.put("电子罗盘初始化命令发送失败", toast42);
        toaHashMap.put("初始化设置成功", toast43);
        toaHashMap.put("正在执行电子罗盘初始化,请耐心等候！", toast44);
        toaHashMap.put("任务未执行", toast45);
        toaHashMap.put("初始化设置失败", toast46);
        toaHashMap.put("电子罗盘校准命令发送成功", toast47);
        toaHashMap.put("电子罗盘校准命令发送失败", toast48);
        toaHashMap.put("电子罗盘校准成功", toast49);
        toaHashMap.put("正在执行电子罗盘校准,请耐心等候！", toast50);
        toaHashMap.put("电子罗盘校准失败", toast51);
        toaHashMap.put("收到非正常回复", toast52);
        toaHashMap.put("超时未收到回复", toast53);
        toaHashMap.put("天线类型不支持", toast54);
        toaHashMap.put("此天线只支持手动定位方式", toast55);


    }
    @SuppressLint("ResourceAsColor")
    private void initView() {
        et_center_rate= (EditText) findViewById(R.id.et_center_rate);
        et_sign_rate= (EditText) findViewById(R.id.et_sign_rate);
        tv_center_rate= (TextView) findViewById(R.id.tv_center_rate);
        tv_sign_rate= (TextView) findViewById(R.id.tv_sign_rate);
        tv_center_rate.setTypeface(AppData.fontXiti);
        tv_sign_rate.setTypeface(AppData.fontXiti);
        ll_center_rate=(LinearLayout)findViewById(R.id.ll_center_rate);
        ll_sign_rate=(LinearLayout)findViewById(R.id.ll_sign_rate);
        scrollview_compass=(ScrollView) findViewById(R.id.scrollview_compass);
        TextView bianhao_weixing=(TextView)findViewById(R.id.bianhao_weixing);
        bianhao_weixing.setTypeface(AppData.fontXiti);
        ll_xinbiao_rate=(LinearLayout) findViewById(R.id.ll_xinbiao_rate);
        ll_zaibo_rate=(LinearLayout) findViewById(R.id.ll_zaibo_rate);
        ll_ref_bandwidth=(LinearLayout) findViewById(R.id.ll_ref_bandwidth);
        ll_pinbao=(LinearLayout) findViewById(R.id.ll_pinbao);
        TextView pinbao_tv=(TextView) findViewById(R.id.pinbao_tv);
        pinbao_tv.setTypeface(AppData.fontXiti);
        TextView duixing_way=(TextView)findViewById(R.id.duixing_way);
        duixing_way.setTypeface(AppData.fontXiti);
        TextView dingweifangshi=(TextView)findViewById(R.id.dingweifangshi);
        dingweifangshi.setTypeface(AppData.fontXiti);
        TextView daikuan_tv=(TextView)findViewById(R.id.daikuan_tv);
        daikuan_tv.setTypeface(AppData.fontXiti);
        TextView jihuaway_tv=(TextView)findViewById(R.id.jihuaway_tv);
        jihuaway_tv.setTypeface(AppData.fontXiti);
        TextView tiaozhijeitiao_tv=(TextView)findViewById(R.id.tiaozhijeitiao_tv);
        tiaozhijeitiao_tv.setTypeface(AppData.fontXiti);
        ll_compass=(LinearLayout) findViewById(R.id.ll_compass);
        tv_compass=(TextView) findViewById(R.id.tv_compass);
        tv_compass.setTypeface(AppData.fontXiti);
        mBtCompass=(Button) findViewById(R.id.bt_compass);
        fuyang_tv=(TextView) findViewById(R.id.fuyang_tv);
        fuyang_tv.setTypeface(AppData.fontXiti);
        zaibo_tv=(TextView) findViewById(R.id.zaibo_tv);
        zaibo_tv.setTypeface(AppData.fontXiti);
        xinbiao_tv=(TextView) findViewById(R.id.xinbiao_tv);
        xinbiao_tv.setTypeface(AppData.fontXiti);
        mToggleButton=(ToggleButton) findViewById(R.id.pinbao_onoff);
        mToggleButton.setOnCheckedChangeListener(this);
        super_xinbiao_rate=(EditText)findViewById(R.id.super_xinbiao_rate);//信标频率
        super_xinbiao_rate.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(SuperSetRefActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        super_zaibo_rate=(EditText)findViewById(R.id.super_zaibo_rate);//载波频率
        super_zaibo_rate.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(SuperSetRefActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        super_ref_fuyang=(EditText) findViewById(R.id.super_ref_fuyang);
        super_ref_fuyang.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (arg1) {
                    if ("--".equals(super_ref_fuyang.getText().toString())) {
                        super_ref_fuyang.setText("");
                    }
                }

            }
        });
        super_ref_fuyang.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(SuperSetRefActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });

        TextView rssi_tv=(TextView)findViewById(R.id.rssi_tv);
        rssi_tv.setTypeface(AppData.fontXiti);
        TextView dangqiaojinwei_tv=(TextView)findViewById(R.id.dangqiaojinwei_tv);
        dangqiaojinwei_tv.setTypeface(AppData.fontXiti);
        TextView dangqianweidu_tv=(TextView)findViewById(R.id.dangqianweidu_tv);
        dangqianweidu_tv.setTypeface(AppData.fontXiti);
        TextView lnb_tv=(TextView)findViewById(R.id.lnb_tv);
        lnb_tv.setTypeface(AppData.fontXiti);
        TextView buc_tv=(TextView)findViewById(R.id.buc_tv);
        buc_tv.setTypeface(AppData.fontXiti);
        //tv_ref_bandwidth=(View)findViewById(R.id.tv_ref_bandwidth);
        TextView super_reftv=(TextView)findViewById(R.id.super_reftv);
        super_reftv.setTypeface(AppData.fontXiti);
        TextView super_reftiaoshi=(TextView)findViewById(R.id.super_reftiaoshi);
        super_reftiaoshi.setTypeface(AppData.fontXiti);
        mDebug_Control=(LinearLayout) findViewById(R.id.superset_debug_control);//调试模式
        mStartNum = (TextView) findViewById(R.id.super_ref_statrtnum);// 卫星编号文本
        mStartNum.setTypeface(AppData.fontXiti);
        super_ref_numner = (EditText) findViewById(R.id.super_ref_numner);// 卫星编号
	/*	startGetName=SharedPreferenceManager.getString(mContext, "starname");
		if(startGetName!=null){
			super_ref_numner.setText(startGetName);
		}else{
			super_ref_numner.setText("--");
		}*/
        //卫星经度增加下拉框  20170105 hyw added
        mWXJD=(Spinner) findViewById(R.id.super_ref_mwxjd);
        //mWXJD.setEnabled(false);
        mWXJD.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.e_w)));
        mEdItude = (EditText) findViewById(R.id.super_ref_itude);// 经纬度
	/*	mSatelon=SharedPreferenceManager.getString(mContext, "satelon");
		if(mSatelon!=null){
			mEdItude.setText(mSatelon);
			if(mSatelon.substring(0,1).equals("-")||mSatelon.substring(0,1).equals("﹣")||mSatelon.substring(0,1).equals("－")||mSatelon.substring(0,1).equals("﹣")){
				mSatelon=mSatelon.substring(1);
				mWXJD.setSelection(2);
			}else{
				mWXJD.setSelection(1);
			}
		}else{
			mEdItude.setText("--");
		}*/
        //mEdRate = (EditText) findViewById(R.id.super_ref_rate);// 频率
        mStatMode = (Spinner) findViewById(R.id.super_ref_statmode);// 对星方式
        mStatMode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.vsat_mode)));
        //edit by llm 20161208
        mStatMode.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //	mStatMode.getSelectedItem().toString();
                //Toast.makeText(SuperSetRefActivity.this,"mStatMode.getSelectedItem().toString()------->"+ mStatMode.getSelectedItem().toString(), 0).show();
                if(mStatMode.getSelectedItem().toString().equals("信标")){
                    initXinBiaoView();

                }else if(mStatMode.getSelectedItem().toString().equals("载波")){
                    initZaiBoView();
                } else if(mStatMode.getSelectedItem().toString().equals("DVB")){
                    initDVBView();
                }
                starMode=mStatMode.getSelectedItem().toString();
                SharedPreferenceManager.saveString(getApplicationContext(), "starmode", starMode);
                starGetMode=SharedPreferenceManager.getString(getApplicationContext(), "starmode");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBandwidth = (Spinner) findViewById(R.id.super_ref_bandwidth);// 带宽
        mBandwidth.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ref_bandwidth)));

        mPoraization = (Spinner) findViewById(R.id.super_ref_polarization);// 接收极化
        mPoraization.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.receive_polarization)));

        mModulator = (Spinner) findViewById(R.id.super_ref_modulator);// 调制解调
        mModulator.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ref_modulator)));

        mThreshold = (EditText) findViewById(R.id.super_ref_threshold);// 门限
        mThreshold.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(mThreshold.getText().toString())) {
                        mThreshold.setText("");
                    }
                }

            }
        });
//		mEdRecvpol = (EditText) findViewById(R.id.super_ref_recvpol);// 接收极化角   //lyj-del 0815  删除收发极化角输入框
//		mSendpol = (EditText) findViewById(R.id.super_ref_sendpol);// 发射极化角

        mLocatemode = (Spinner) findViewById(R.id.super_ref_locatemode);// 定位方式
        mLocatemode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.advpar_locatemode)));

        super_ref_lub = (Spinner) findViewById(R.id.super_ref_lub);
        super_ref_lub.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.lub_mode)));
        super_ref_buc = (EditText) findViewById(R.id.super_ref_buc);
        super_ref_buc.setFocusable(false);
        super_ref_buc.setEnabled(false);
        super_ref_buc.setBackgroundColor(R.color.gray);
        //当前经度  20170105hyw added
        mDQJD=(Spinner) findViewById(R.id.super_ref_mdqjd);
        mDQJD.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.e_w)));
        mCurrentLongItude = (EditText) findViewById(R.id.super_ref_longitude);// 当前经度
        mCurrentLongItude.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(mCurrentLongItude.getText().toString())) {
                        mCurrentLongItude.setText("");
                    }
                }

            }
        });
        mCurrentLongItude.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(SuperSetRefActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        //当前维度  20170105hyw added
        mDQWD=(Spinner) findViewById(R.id.super_ref_mdqwd);
        mDQWD.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.n_s)));
        mCurrentLatitude = (EditText) findViewById(R.id.super_ref_latitude);// 当前纬度
        mCurrentLatitude.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    if ("--".equals(mCurrentLatitude.getText().toString())) {
                        mCurrentLatitude.setText("");
                    }
                }

            }
        });
        mCurrentLatitude.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                    Toast.makeText(SuperSetRefActivity.this, "对不起，您只能输入两位小数！！", 0).show();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });

        mBtnSave = (ButtonBgUi) findViewById(R.id.super_ref_btnsave);
        mBtnSaveGeneral = (ButtonBgUi) findViewById(R.id.super_ref_btnsave01);
        mBtdefault = (ButtonBgUi)findViewById(R.id.super_ref_btnsetbefore);
        mBtnStop=(ButtonBgUi)findViewById(R.id.super_ref_btnstop);
        mBtnSave.setOnClickListener(this);
        mBtnSaveGeneral.setOnClickListener(this);
        mBtdefault.setOnClickListener(this);
        mDebug_Control.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtCompass.setOnClickListener(this);
        querySessionStatus(new OnSessionStatusListener() {
            @Override
            public void sessionSuccess() {
                GetAdvArg();//从服务器获取下面参数
                checkPing();
            }
        });
    }

    // 从服务器获取卫星高级参数1
    private void GetAdvArg(){//--------------------------------------------查看下面 rssi门限
        Log.e(TAG, "下面开始从服务器获取卫星经度极频率参数并判断格式是否正确------》");
        advgettask = new SntAsyncHttpGet();
        advgettask.execute(GET_SATEADV_ARG);//	// 查看卫星参数--高级
        LoggerSave.requestLog(GET_SATEADV_ARG,GET_SATEADV_ARG);
        advgettask.setFinishListener(new HpOverListener() {
            @Override
            public void HpRecvOk(JSONObject data) {
                // TODO Auto-generated method stub
                // lyj-modi 0729
                try {
                    if(data.getString("code").equals("-100")){
                        if (toaHashMap.get("连接网元服务器失败")!=null){
                            toaHashMap.get("连接网元服务器失败").show();
                        }
                    }else if(data.getString("code").equals("-1")){
                        if(data.optString("msg").equals("acu_occupy")){
                            showMutualDialog();
                        }else {
                            if (toaHashMap.get("获取天线参数失败")!=null){
                                toaHashMap.get("获取天线参数失败").show();
                            }
                        }
                    }else{
                        ///Toast.makeText(getApplicationContext(), "高级参数获取成功", 0).toString();//，返回码为："+data.getString("code"),
                        Log.e(TAG, "高级参数获取，返回码为："+data.getString("code"));
                        AdvUpUI(data);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        Log.e(TAG, "---------》从服务器获取卫星经度极频率参数并判断格式是否正确完成");
    }
    //从服务器获取参数  RSSI 当前经度  当前纬度
    private void AdvUpUI(JSONObject js){//设置参数
        Log.e(TAG, "从服务器获取参数------>"+js.toString());
        Log.e(TAG, "下面开始从服务器获取RSSI参数并判断格式是否正确------》");
        try {
            int locint = 0;

            if(js.getString("locatypecode")!=""&&js.getString("locatypecode")!=null){
			/*	locint = Integer.parseInt(js.getString("locatype"));
				if(locint>4){
					Toast.makeText(getApplicationContext(), "获取定位方式失败", 0).show();
					mLocatemode.setSelection(2);*/
                if(js.getString("locatypecode").equals("-1")){
                    if (toaHashMap.get("获取定位方式失败")!=null){
                        toaHashMap.get("获取定位方式失败").show();
                    }
                    toaHashMap.get("获取定位方式失败").show();
                }else if(js.getString("locatypecode").equals("0")){
                    if(!StringUtils.isEmpty(js.getString("locatype"))){
                        mLocatemode.setSelection(Integer.parseInt(js.getString("locatype")));
                    }
                    Log.e(TAG, "选择的定位方式是：------》"+js.getString("locatype"));
                    if (toaHashMap.get("获取定位方式成功")!=null){
                        toaHashMap.get("获取定位方式成功").show();
                    }
                }
            }else{
                return;
            }

            //判断接受的数据是否符合格式
            if(js.getString("rssicode").equals("0")){
                if(ChechIpMask.a2b(js.getString("rssi"), 100, 10000)){
                    //String _rssi=ChechIpMask.numDigite(js.getString("rssi"), 2);
                    mThreshold.setText(js.getString("rssi"));//从服务器获取RSSI门限
                    if (toaHashMap.get("RSSI门限值查询成功！")!=null){
                        toaHashMap.get("RSSI门限值查询成功！").show();
                    }
                }else if(!ChechIpMask.a2b(js.getString("rssi"), 100,10000)){
                    mThreshold.setText("--");//-1
                    if (toaHashMap.get("抱歉，接收到的RSSI格式不正确！！")!=null){
                        toaHashMap.get("抱歉，接收到的RSSI格式不正确！！").show();
                    }
                }
            }else if(js.getString("rssicode").equals("-1")){
                if (toaHashMap.get("RSSI门限值查询失败！")!=null){
                    toaHashMap.get("RSSI门限值查询失败！").show();
                }
            }
            //判断接受的数据是否符合格式
            if(js.getString("lonlatcode").equals("0")){
                if(ChechIpMask.a2b(js.getString("curlon"), 0, 180)){//当前经度
                    String _curlon=ChechIpMask.numDigite(js.getString("curlon"), 2);
                    //Toast.makeText(SuperSetRefActivity.this, "_curlon当前经度0--->"+_curlon, 0).show();
                    //如果带负号
                    if(_curlon.substring(0,1).equals("-")||_curlon.substring(0,1).equals("﹣")||_curlon.substring(0,1).equals("－")||_curlon.substring(0,1).equals("﹣")){
                        _curlon=_curlon.substring(1);
                        mDQJD.setSelection(1);
                    }else{
                        mDQJD.setSelection(0);

                    }
                    mCurrentLongItude.setText(_curlon);//从服务器获取经度
                    if (toaHashMap.get("获取经度成功！")!=null){
                        toaHashMap.get("获取经度成功！").show();
                    }
                }else if(!ChechIpMask.a2b(js.getString("curlon"), 0, 180)){
                    mCurrentLongItude.setText("--");//-1
                    if (toaHashMap.get("抱歉，接收到的当前经度格式不正确！！")!=null){
                        toaHashMap.get("抱歉，接收到的当前经度格式不正确！！").show();
                    }
                }
            }else if(js.getString("lonlatcode").equals("-1")){
                if (toaHashMap.get("获取经度失败！")!=null){
                    toaHashMap.get("获取经度失败！").show();
                }
            }
            //判断接受的数据是否符合格式
            if(js.getString("lonlatcode").equals("0")){
                if(ChechIpMask.a2b(js.getString("currlat"), 0, 90)){//当前维度
                    String _currlat=ChechIpMask.numDigite(js.getString("currlat"), 2);
                    //mCurrentLatitude.setText(js.getString("currlat"));//从服务器获取纬度
                    //如果带负号
                    if(_currlat.substring(0,1).equals("-")||_currlat.substring(0,1).equals("﹣")||_currlat.substring(0,1).equals("－")||_currlat.substring(0,1).equals("﹣")){

                        _currlat=_currlat.substring(1);
                        //Toast.makeText(SuperSetRefActivity.this, "获取mDQWD当前经度0--->"+mDQWD.getSelectedItem(), 0).show();
                        mDQWD.setSelection(1);
                        //Toast.makeText(SuperSetRefActivity.this, "获取mDQWD当前经度1--->"+mDQWD.getSelectedItem(), 0).show();
                    }else{
                        mDQWD.setSelection(0);
                    }
                    mCurrentLatitude.setText(_currlat);//从服务器获取纬度
                    if (toaHashMap.get("获取纬度成功！")!=null){
                        toaHashMap.get("获取纬度成功！").show();
                    }
                }else if(!ChechIpMask.a2b(js.getString("currlat"), 0, 90)){
                    mCurrentLatitude.setText("--");//-1
                    if (toaHashMap.get("抱歉，接收到的当前纬度格式不正确！！")!=null){
                        toaHashMap.get("抱歉，接收到的当前纬度格式不正确！！").show();
                    }
                }
            }else if(js.getString("lonlatcode").equals("-1")){
                if (toaHashMap.get("获取纬度失败！")!=null){
                    toaHashMap.get("获取纬度失败！").show();
                }
            }
            if(js.getString("elevoffsetcode").equals("0")){
                if(ChechIpMask.abs(js.getString("elevoffset"), 15)){//当前俯仰补偿角
                    String _elevoffset=ChechIpMask.numDigite(js.getString("elevoffset"), 2);
                    super_ref_fuyang.setText(_elevoffset);//从服务器获取俯仰补偿角
                    if (toaHashMap.get("获取俯仰补偿角成功！")!=null){
                        toaHashMap.get("获取俯仰补偿角成功！").show();
                    }
                }else if(!ChechIpMask.abs(js.getString("elevoffset"), 15)){
                    super_ref_fuyang.setText("--");//-1
                    if (toaHashMap.get("抱歉，接收到的俯仰补偿角格式不正确！！")!=null){
                        toaHashMap.get("抱歉，接收到的俯仰补偿角格式不正确！！").show();
                    }

                }
            }else if(js.getString("elevoffsetcode").equals("-1")){
                if (toaHashMap.get("获取俯仰补偿角失败！")!=null){
                    toaHashMap.get("获取俯仰补偿角失败！").show();
                }
            }
            if(js.getString("lnbcode").equals("0")){
                if(js.getString("lnb").equals("9750")){
                    super_ref_lub.setSelection(0);
                }else if(js.getString("lnb").equals("10600")){
                    super_ref_lub.setSelection(1);
                }else if(js.getString("lnb").equals("10750")){
                    super_ref_lub.setSelection(2);
                }else if(js.getString("lnb").equals("11300")){
                    super_ref_lub.setSelection(3);
                }
                SharedPreferenceManager.saveString(this,"lnb",js.getString("lnb"));
                if (toaHashMap.get("获取LNB成功！")!=null){
                    toaHashMap.get("获取LNB成功！").show();
                }
                toaHashMap.get("").show();
            }else if(js.getString("lnb").equals("-1")){
                if (toaHashMap.get("获取LNB失败！")!=null){
                    toaHashMap.get("获取LNB失败！").show();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "获取天线参数故障", 0).show();
        }
        Log.e(TAG, "---------》从服务器获取RSSI参数并判断格式完成");
    }

    private StarCodeModel isDataSaved() {
        List<StarCodeModel> stars = DataSupport.findAll(StarCodeModel.class);
        if (stars != null) {
            for (StarCodeModel s : stars) {
                if (currentStar.getId() == s.getId()) {
                    return s;
                }
            }
        }
        return null;
    }
    private void getRefText_up(){
        //strSatelliteNum01 = mSatelliteNum.getSelectedItem().toString();// 卫星编号
        //if(ChechIpMask.isDigite(mEdItude.getText().toString(), -180, 180, 2)){//判断经纬度
        if(ChechIpMask.a2b(mEdItude.getText().toString(), 0 , 180)){
            String _mEdItude=ChechIpMask.numDigite(mEdItude.getText().toString(), 2);
            if(mWXJD.getSelectedItemPosition()==1){
                _mEdItude="-"+_mEdItude;
                strEdItude =_mEdItude;
                is_longitude=true;
            }else if(mWXJD.getSelectedItemPosition()==0){
                strEdItude =_mEdItude;// mCurrentLongItude.getText().toString();// 卫星经度
                is_longitude=true;
            }

        }else{
            strEdItude=null;
            is_longitude=false;
            Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的经度不合法！！",0).show();
            return;
        }

        //	Toast.makeText(SuperSetRefActivity.this, "super_xinbiao_rate---------"+super_xinbiao_rate.getText().toString(),0).show();
        //	ChechIpMask.Toast_alert(SuperSetRefActivity.this, "super_xinbiao_rate", super_xinbiao_rate.getText().toString());

        if(starGetMode.equals("信标")){
            if(ChechIpMask.a2b(super_xinbiao_rate.getText().toString(), 300, 3000)){//判断频率

               /* String _XstrEdRate=ChechIpMask.numDigite(super_xinbiao_rate.getText().toString(), 2);
                strXinRate = _XstrEdRate;
                //super_xinbiao_rate.getText().toString();// 频率文本框
                super_xinbiao_rate.setText(strXinRate);*/
                super_xinbiao_rate.setText(ChechIpMask.numDigite(super_xinbiao_rate.getText().toString(), 2));
                is_rate=true;
            }else if(!ChechIpMask.a2b(super_xinbiao_rate.getText().toString(), 300, 3000)){
                //strXinRate=null;
                is_rate=false;
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的频率不合法！！",0).show();
                return;
            }
        }else if(starGetMode.equals("载波")){
            if(ChechIpMask.a2b(super_zaibo_rate.getText().toString(),10700, 12750)){//判断频率
                /*String _strEdRate=ChechIpMask.numDigite(super_zaibo_rate.getText().toString(), 2);;
                strZaiRate =_strEdRate ;//super_zaibo_rate.getText().toString();// 频率文本框
                //super_xinbiao_rate.setText(super_zaibo_rate.getText().toString());
                super_zaibo_rate.setText(strZaiRate);*/
                super_zaibo_rate.setText(ChechIpMask.numDigite(super_zaibo_rate.getText().toString(), 2));
                is_rate=true;
            }else if(!ChechIpMask.a2b(super_zaibo_rate.getText().toString(),10700, 12750)){
                //strZaiRate=null;
                is_rate=false;
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的频率不合法！！",0).show();
                return;
            }
        } else if(starGetMode.equals("DVB")){
            if(ChechIpMask.a2b(et_center_rate.getText().toString(),11550,12750)){
                is_rate=true;
            }else{
                is_rate=false;
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的中心频率不合法！！",0).show();
                return;
            }
            if(ChechIpMask.a2b(et_sign_rate.getText().toString(),0,99999)){
                is_rate=true;
            }else{
                is_rate=false;
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的符号率不合法！！",0).show();
                return;
            }
        }
        strStatMode =starGetMode; //mStatMode.getSelectedItem().toString();// 对星方式
        strBandwidth = mBandwidth.getSelectedItem().toString();// 带宽
        strPoraization = mPoraization.getSelectedItem().toString();// 接收极化
        strModulator = mModulator.getSelectedItem().toString();// 调制解调

        if (strStatMode.equals("信标")) {
            intStrStatMode = "0";
        } else if (strStatMode.equals("载波")) {
            intStrStatMode = "1";
        } else if (strStatMode.equals("DVB")) {
            intStrStatMode = "2";
    }

        if (strBandwidth.equals("25K")) {
            intBandwidth = "0";
        } else if (strBandwidth.equals("50K")) {
            intBandwidth = "1";
        } else if (strBandwidth.equals("100K")) {
            intBandwidth = "2";
        } else if (strBandwidth.equals("200K")) {
            intBandwidth = "3";
        } else if (strBandwidth.equals("400K")) {
            intBandwidth = "4";
        } else if (strBandwidth.equals("800K")) {
            intBandwidth = "5";
        } else if (strBandwidth.equals("1.6M")) {
            intBandwidth = "6";
        } else if (strBandwidth.equals("2M")) {
            intBandwidth = "7";
        } else if (strBandwidth.equals("4M")) {
            intBandwidth = "8";
        } else if (strBandwidth.equals("5M")) {
            intBandwidth = "9";
        } else if (strBandwidth.equals("6M")) {
            intBandwidth = "10";
        } else if (strBandwidth.equals("7M")) {
            intBandwidth = "11";
        } else if (strBandwidth.equals("8M")) {
            intBandwidth = "12";
        } else if (strBandwidth.equals("9M")) {
            intBandwidth = "13";
        } else if (strBandwidth.equals("10M")) {
            intBandwidth = "14";
        }
        if (strPoraization.equals("垂直")) {
            intPoraization = "0";
        } else if (strPoraization.equals("水平")) {
            intPoraization = "1";
        }
        if (currentStar != null) {
            StarCodeModel dataSaved = isDataSaved();
            if (dataSaved == null) {
                currentStar.setMode(intStrStatMode);
                if(!"--".equals(super_xinbiao_rate.getText().toString())){
                    currentStar.setFreq(ChechIpMask.numDigite(super_xinbiao_rate.getText().toString(), 2));
                }
                if(!"--".equals(super_zaibo_rate.getText().toString())){
                    currentStar.setZfreq(ChechIpMask.numDigite(super_zaibo_rate.getText().toString(), 2));
                }
                if(!"--".equals(et_center_rate.getText().toString())){
                    currentStar.setCenterFreq(et_center_rate.getText().toString());
                }
                if(!"--".equals(et_sign_rate.getText().toString())){
                    currentStar.setSignRate(et_sign_rate.getText().toString());
                }
                if(!"--".equals(mEdItude.getText().toString())){
                    currentStar.setSatelon(strEdItude);
                }
                currentStar.setType(intPoraization);
                currentStar.setSessionToken(mToken);
                currentStar.setBw(intBandwidth);
                currentStar.save();
            } else {
                dataSaved.setMode(intStrStatMode);
                if(!"--".equals(super_xinbiao_rate.getText().toString())){
                    dataSaved.setFreq(ChechIpMask.numDigite(super_xinbiao_rate.getText().toString(), 2));
                }
                if(!"--".equals(super_zaibo_rate.getText().toString())){
                    dataSaved.setZfreq(ChechIpMask.numDigite(super_zaibo_rate.getText().toString(), 2));
                }
                if(!"--".equals(et_center_rate.getText().toString())){
                    dataSaved.setCenterFreq(et_center_rate.getText().toString());
                }
                if(!"--".equals(et_sign_rate.getText().toString())){
                    dataSaved.setSignRate(et_sign_rate.getText().toString());
                }
                if(!"--".equals(mEdItude.getText().toString())){
                    dataSaved.setSatelon(strEdItude);
                }
                dataSaved.setType(intPoraization);
                dataSaved.setSessionToken(mToken);
                dataSaved.setBw(intBandwidth);
                dataSaved.update(dataSaved.getId());
            }
            if (is_rate) {
                if (toaHashMap.get("星位参数保存成功") != null) {
                    toaHashMap.get("星位参数保存成功").show();
                }
                SharedPreferenceManager.saveString(mContext, "currentStar",
                        GsonUtils.toJson(dataSaved == null ? currentStar
                                : dataSaved));
            } else {
                if (toaHashMap.get("星位参数保存失败") != null) {
                    toaHashMap.get("星位参数保存失败").show();
                }
            }

        }
    }
    private void getRefText_down(){
        strLocatemode = mLocatemode.getSelectedItem().toString();// 定位方式
        if (strLocatemode.equals("手动")) {
            intLocatemode = "0";
        } else if (strLocatemode.equals("北斗")) {
            intLocatemode = "2";
        } else if (strLocatemode.equals("GPS")) {
            intLocatemode = "1";
        }
        //if(ChechIpMask.isDigite(mThreshold.getText().toString(), 100, 10000, 0)){//判断RSSI门限
        if(ChechIpMask.a2b(mThreshold.getText().toString(), 100, 10000)){
            String _strThreshold=ChechIpMask.numDigite(mThreshold.getText().toString(), 0);
            strThreshold =_strThreshold;// mThreshold.getText().toString();// RSSI门限文本框
            is_rssiLimit=true;
        }else if(!ChechIpMask.a2b(mThreshold.getText().toString(), 100, 10000)){
            strThreshold=null;
            is_rssiLimit=false;
            if (toaHashMap.get("对不起，您输入的RSSI不合法！！")!=null){
                toaHashMap.get("对不起，您输入的RSSI不合法！！").show();
            }
        }
        if (strLocatemode.equals("手动")){
            if(ChechIpMask.a2b(mCurrentLongItude.getText().toString(), 0, 180)){
                String _strCurrentLongItude=ChechIpMask.numDigite(mCurrentLongItude.getText().toString(), 2);
                //Toast.makeText(SuperSetRefActivity.this, "_strCurrentLongItude当前经度0--->"+_strCurrentLongItude, 0).show();

						/*if(mDQJD.getSelectedItemPosition()==0){
							strCurrentLongItude=null;
							is_currLongitude=false;
							Toast.makeText(SuperSetRefActivity.this, "对不起，请选择东经还是西经！！",0).show();
						}else*/
                if(mDQJD.getSelectedItemPosition()==1){
                    _strCurrentLongItude="-"+_strCurrentLongItude;
                    strCurrentLongItude =_strCurrentLongItude;
                    is_currLongitude=true;
                    //Toast.makeText(SuperSetRefActivity.this, "_strCurrentLongItude当前经度01--->"+_strCurrentLongItude, 0).show();
                }else if(mDQJD.getSelectedItemPosition()==0){
                    strCurrentLongItude =_strCurrentLongItude;// mCurrentLongItude.getText().toString();// 当前经度
                    is_currLongitude=true;
                    //Toast.makeText(SuperSetRefActivity.this, "_strCurrentLongItude当前经度02--->"+_strCurrentLongItude, 0).show();
                }

            }else if(!ChechIpMask.a2b(mCurrentLongItude.getText().toString(), 0, 180)){
                strCurrentLongItude=null;
                is_currLongitude=false;
                if (toaHashMap.get("对不起，您输入的经度不合法！！")!=null){
                    toaHashMap.get("对不起，您输入的经度不合法！！").show();
                }
            }else if(strCurrentLongItude==null){
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的经度不能为空！！",0).show();
            }

            if(ChechIpMask.a2b(mCurrentLatitude.getText().toString(), 0, 90)){
                String _strCurrentLatitude=ChechIpMask.numDigite(mCurrentLatitude.getText().toString(), 2);
                //Toast.makeText(SuperSetRefActivity.this, "下发前_strCurrentLatitude当前经度0--->"+_strCurrentLatitude, 0).show();
						/*if(mDQWD.getSelectedItemPosition()==0){
							_strCurrentLatitude=null;
							is_currLatitude=false;
							Toast.makeText(SuperSetRefActivity.this, "对不起，请选择南纬还是北纬！！",0).show();
						}else*/
                if(mDQWD.getSelectedItemPosition()==1){//南纬
                    _strCurrentLatitude="-"+_strCurrentLatitude;
                    strCurrentLatitude = _strCurrentLatitude;
                    is_currLatitude=true;
                    //Toast.makeText(SuperSetRefActivity.this, "下发_strCurrentLatitude当前经度01--->"+_strCurrentLatitude, 0).show();
                }else if(mDQWD.getSelectedItemPosition()==0){//北纬
                    strCurrentLatitude = _strCurrentLatitude;//mCurrentLatitude.getText().toString();// 当前纬度
                    is_currLatitude=true;
                    //	Toast.makeText(SuperSetRefActivity.this, "下发_strCurrentLatitude当前经度02--->"+_strCurrentLatitude, 0).show();
                }

            }else if(!ChechIpMask.a2b(mCurrentLatitude.getText().toString(), 0, 90)){
                strCurrentLatitude=null;
                is_currLatitude=false;
                if (toaHashMap.get("对不起，您输入的纬度不合法！！")!=null){
                    toaHashMap.get("对不起，您输入的纬度不合法！！").show();
                }
            }else if(strCurrentLatitude==null){
                Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的纬度不能为空！！",0).show();
            }
        }else {
				/*if(ChechIpMask.a2b(mCurrentLongItude.getText().toString(), 0, 180)||"".equals(mCurrentLongItude.getText().toString())
						||mCurrentLongItude.getText().toString()==null){
					String _strCurrentLongItude=ChechIpMask.numDigite2(mCurrentLongItude.getText().toString(), 2);
				    strCurrentLongItude =_strCurrentLongItude;// mCurrentLongItude.getText().toString();// 当前经度
				    is_currLongitude=true;
				}else if(!ChechIpMask.a2b(mCurrentLongItude.getText().toString(), 0, 180)){
					strCurrentLongItude=null;
					is_currLongitude=false;
					//Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的经度不合法！！",0).show();
				}
				 if(ChechIpMask.a2b(mCurrentLatitude.getText().toString(), 0, 90)||"".equals(mCurrentLatitude.getText().toString())
						 ||mCurrentLatitude.getText().toString()==null){
					String _strCurrentLatitude=ChechIpMask.numDigite2(mCurrentLatitude.getText().toString(), 2);
					strCurrentLatitude = _strCurrentLatitude;//mCurrentLatitude.getText().toString();// 当前纬度
					is_currLatitude=true;
				}else if(!ChechIpMask.a2b(mCurrentLatitude.getText().toString(), 0, 90)){
					strCurrentLatitude=null;
					is_currLatitude=false;
					//Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的纬度不合法！！",0).show();
				}*/
        }

        if(ChechIpMask.abs(super_ref_fuyang.getText().toString(), 15)){
            String _strCurrentElevoff=ChechIpMask.numDigite(super_ref_fuyang.getText().toString(), 2);
            strCurrentElevoff = _strCurrentElevoff;//mCurrentLatitude.getText().toString();// 当前纬度
            is_currentElevoff=true;
        }else if(!ChechIpMask.abs(super_ref_fuyang.getText().toString(), 15)){
            strCurrentElevoff=null;
            is_currentElevoff=false;
            if (toaHashMap.get("对不起，您输入的俯仰补偿角不合法！！")!=null){
                toaHashMap.get("对不起，您输入的俯仰补偿角不合法！！").show();
            }
        }else if(strCurrentElevoff==null){
            Toast.makeText(SuperSetRefActivity.this, "对不起，您输入的俯仰补偿角不能为空！！",0).show();
        }
		/*
		 * lyj-del 0815 删除收发简化角输入框
		 */
//		strRecvpol = mEdRecvpol.getText().toString();// 接收极化角
//		strSendpol = mSendpol.getText().toString();// 发射极化角
        strRefLub = super_ref_lub.getSelectedItem().toString();
        if (strRefLub.equals("9750")) {
            strCurrentLnb = "9750";
        } else if (strRefLub.equals("10600")) {
            strCurrentLnb = "10600";
        } else if (strRefLub.equals("10750")) {
            strCurrentLnb = "10750";
        }else if (strRefLub.equals("11300")) {
            strCurrentLnb = "11300";
        }
        is_currLnb=true;
    }
    /* 获取控件的文本信息 */
	/*private void getRefText() {
	}*/
    private JSONObject objectPuTong = null;
    private JSONObject objectAdv;
    // 点击保存的响应事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.super_ref_btnsave01://点击上面保存
                saveStarPara();
                break;
            case R.id.super_ref_btnsetbefore:  //设为默认卫星
                Log.i("LYJ", "点击设为默认");
                genjs = new JSONObject();
                GenUitoJS();
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        Gendefsave();
                    }
                });
                break;
            case R.id.super_ref_btnsave://buc 下面的保存
                advjs = new JSONObject();
                AdvUitoJS();
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        AdvStore();
                    }
                });
                break;
            case R.id.superset_debug_control://点击了调试模式
                stopGet();
                if("S(三轴)".equals(mType)){
                    Intent mIntent = new Intent(SuperSetRefActivity.this,DebugControlAnotherActivity.class);
                    startActivity(mIntent);
                }else{
                    Intent mIntent = new Intent(SuperSetRefActivity.this,DebbugControlActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.super_ref_btnstop://点击停止保存天线参数
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        stopGet();
                    }
                });
                break;
            case R.id.bt_compass://点击校准罗盘
                querySessionStatus(new OnSessionStatusListener() {
                    @Override
                    public void sessionSuccess() {
                        getCompass();
                    }
                });
                break;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            mPing="1";
            PingPost(isChecked);
        }else{
            mPing="0";
            PingPost(isChecked);
        }

    }
    //点击保存上面 ，把UI获得的数据转到Jason,上面的    保存和默认设置都要调用.
    private void GenUitoJS(){//提交数据
        getRefText_up();
        if(!"--".equals(super_xinbiao_rate.getText().toString())){
            strXinRate= ChechIpMask.numDigite(super_xinbiao_rate.getText().toString(), 2);
            if (!StringUtils.isEmpty(strXinRate)) {
                strXinRate=new BigDecimal(strXinRate).multiply(new BigDecimal(100)).toString();
            }
        }
        if(!"--".equals(super_zaibo_rate.getText().toString())){
            strZaiRate=ChechIpMask.numDigite(super_zaibo_rate.getText().toString(), 2);
        }
        if(!"--".equals(et_center_rate.getText().toString())){
            strCenter=et_center_rate.getText().toString();
            String mLnb = SharedPreferenceManager.getString(this, "lnb");
            if (!StringUtils.isEmpty(strCenter) && !StringUtils.isEmpty(mLnb)) {
                BigDecimal lCenterFreq = new BigDecimal(strCenter);
                strCenter=lCenterFreq.subtract(new BigDecimal(mLnb)).multiply(new BigDecimal(100)).toString();
            }
        }
        if(!"--".equals(et_sign_rate.getText().toString())){
            strSignRate=et_sign_rate.getText().toString();
        }
        //	getRefText();
        try {
            genjs.put("satenum", strSatelliteNum);
            genjs.put("satelon", strEdItude);
            genjs.put("mode", intStrStatMode);
            genjs.put("freq", strXinRate);
            genjs.put("zfreq", strZaiRate);
            genjs.put("centerFreq", strCenter);
            genjs.put("signRate", strSignRate);
            genjs.put("bw", intBandwidth);
            genjs.put("type", intPoraization);
            genjs.put("modem", strModulator);

            //	1、仅保存，
            //Toast.makeText(getApplicationContext(), "tmp-输入框的---->"+tmp.toString(), 0).show();
//				tmp.put("recvpol", strRecvpol);
//				tmp.put("sendpol", strSendpol);
            //	tmp.put("rssi", strThreshold);//edit by hyw 20161114
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //设置默认，上面
    private void Gendefsave(){
        agrtask = new SntAsyncPost();
        if(is_longitude&&is_rate){
            agrtask.execute(XTHttpUtil.POST_ONESTAR_DEFAULT_ADDRESS,genjs.toString());
            //  1、仅保存，
            //Toast.makeText(getApplicationContext(), "data设置并默认----->"+genjs.toString(), 0).show();
            Log.e(TAG, "设置并默认------>"+genjs.toString());
        }else {
            //Toast.makeText(SuperSetRefActivity.this, "输入参数有误，请检查！！", 0).show();。。
        }
        agrtask.SetListener(new PostOverHandle() {
            @Override
            public void HandleData(JSONObject data) {
                try {
                    if(data.getString("code").equals("0")){
                        SntSpUtils.GenJStoSp(SuperSetRefActivity.this, mSate, genjs);
                        Toast.makeText(SuperSetRefActivity.this, "保存默认对星成功", 0).show();
                    }else if(data.getString("code").equals("-100")){
                        Toast.makeText(SuperSetRefActivity.this,"连接网元服务器失败,参数未保存", 0).show();
                    }
                    else if(data.getString("code").equals("-1")){
                        Toast.makeText(SuperSetRefActivity.this,"保存对星失败！", 0).show();
                    }else if(data.getString("code").equals("-2")){
                        Toast _toast =	Toast.makeText(SuperSetRefActivity.this, "保存失败，天线类型不支持！",Toast.LENGTH_LONG);
                        _toast.setGravity(Gravity.CENTER, 0, 0);
                        _toast.show();//,错误码："+data.getString("code")
                    }else{
                        Toast.makeText(SuperSetRefActivity.this, "设为默认保存失败", 0).show();//,错误码："+data.getString("code")
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        is_longitude=false;
        is_rate=false;
    }
    //buc下面的保村
    private void AdvUitoJS(){
        getRefText_down();
        //	getRefText();
        try {
            advjs.put("rssi", strThreshold);
            advjs.put("locatype", intLocatemode);
            if (strLocatemode.equals("手动")){
                advjs.put("curlon", strCurrentLongItude);
                advjs.put("currlat", strCurrentLatitude);
            }
            advjs.put("elevoffset",strCurrentElevoff);
            advjs.put("lnb",strCurrentLnb);
            Log.e(TAG, "strThreshold------>"+strThreshold);
            Log.e(TAG, "intLocatemode------>"+intLocatemode);
            Log.e(TAG, "strCurrentLongItude------>"+strCurrentLongItude);
            Log.e(TAG, "strCurrentLatitude------>"+strCurrentLatitude);
            Log.e(TAG, "strCurrentLnb------>"+strCurrentLnb);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //buc下面的保存
    private void AdvStore(){
        agrtask = new SntAsyncPost();
        if (strLocatemode.equals("手动")){
            if(is_currLatitude&&is_currLongitude&&is_rssiLimit&&is_currentElevoff&&is_currLnb){
                Log.e(TAG, "buc下面的保存----卫星参数设置-高级---"+advjs.toString());
                agrtask.execute(POST_ARGSET_ADV,advjs.toString());// 卫星参数设置-高级
                LoggerSave.requestLog(POST_ARGSET_ADV,advjs.toString());
                //Toast.makeText(SuperSetRefActivity.this, advjs.toString(), 0).show();
            }
        }else{
            if(is_rssiLimit&&is_currentElevoff&&is_currLnb){
                Log.e(TAG, "buc下面的保存----卫星参数设置-高级---"+advjs.toString());
                agrtask.execute(POST_ARGSET_ADV,advjs.toString());// 卫星参数设置-高级
                LoggerSave.requestLog(POST_ARGSET_ADV,advjs.toString());
                //Toast.makeText(SuperSetRefActivity.this, advjs.toString(), 0).show();
            }
        }
        agrtask.SetListener(new PostOverHandle() {
            @Override
            public void HandleData(JSONObject data) {
                //Toast.makeText(getApplicationContext(), data.toString(), 0).show();
                try {
                    Log.e(TAG, "buc下面保存时的值为----------->"+data.getString("code"));
                    if(data.getString("code").equals("0")){
                        SntSpUtils.AdvJStoSp(SuperSetRefActivity.this,advjs );//edit by hyw 20161114
                    }else if(data.getString("code").equals("-100")){
                        if (toaHashMap.get("连接网元服务器失败")!=null){
                            toaHashMap.get("连接网元服务器失败").show();
                        }
                    }else if(data.getString("code").equals("-1")){
                        if(data.getString("msg").equals("acu_occupy")){
                            showMutualDialog();
                        }else{
                            if (toaHashMap.get("保存失败,请检查设备！")!=null){
                                toaHashMap.get("保存失败,请检查设备！").show();
                            }
                        }
                    }else{
                        if (toaHashMap.get("保存失败,请检查设备！")!=null){
                            toaHashMap.get("保存失败,请检查设备！").show();
                        }
                    }

                    if(data.getString("rssicode").equals("0")){//-1
                        if (toaHashMap.get("RSSI保存成功！")!=null){
                            toaHashMap.get("RSSI保存成功！").show();
                        }
                    }else if(data.getString("rssicode").equals("-1")){
                        if (toaHashMap.get("RSSI保存失败")!=null){
                            toaHashMap.get("RSSI保存失败").show();
                        }
                    }
                    if(data.getString("locatypecode").equals("0")){
                        if (toaHashMap.get("定位方式设置成功")!=null){
                            toaHashMap.get("定位方式设置成功").show();
                        }
                    }else if(data.getString("locatypecode").equals("-1")){
                        if (toaHashMap.get("定位方式设置失败")!=null){
                            toaHashMap.get("定位方式设置失败").show();
                        }
                    }else if(data.getString("locatypecode").equals("1")){
                        mLocatemode.setSelection(0);
                        if (toaHashMap.get("此天线只支持手动定位方式")!=null){
                            toaHashMap.get("此天线只支持手动定位方式").show();
                        }
						/*if (toaHashMap.get("天线类型不支持")!=null){
							 toaHashMap.get("天线类型不支持").show();
							}*/
                    }else{
                        if (toaHashMap.get("定位方式设置失败")!=null){
                            toaHashMap.get("定位方式设置失败").show();
                        }
                    }

                    if(data.getString("lonlatcode").equals("0")){//(-1)
                        if(strLocatemode.equals("手动")){
                            if (toaHashMap.get("经纬度设置成功")!=null){
                                toaHashMap.get("经纬度设置成功").show();
                            }
                        }else{

                        }
                    }else if(data.getString("lonlatcode").equals("-1")){
                        if(strLocatemode.equals("手动")){
                            if (toaHashMap.get("经纬度设置失败")!=null){
                                toaHashMap.get("经纬度设置失败").show();
                            }
                        }else{

                        }
                    }else if(data.getString("lonlatcode").equals("1")){
                        if(strLocatemode.equals("手动")){
                            if (toaHashMap.get("天线类型不支持")!=null){
                                toaHashMap.get("天线类型不支持").show();
                            }
                        }else{

                        }
                    }else{
                        if(strLocatemode.equals("手动")){
                            if (toaHashMap.get("经纬度设置失败")!=null){
                                toaHashMap.get("经纬度设置失败").show();
                            }
                        }else{

                        }
                    }
                    if(data.getString("elevoffsetcode").equals("0")){//(-1)
                        if (toaHashMap.get("俯仰补偿角设置成功")!=null){
                            toaHashMap.get("俯仰补偿角设置成功").show();
                        }
                    }else if(data.getString("elevoffsetcode").equals("-1")){
                        if(data.optString("msg").equals("acu_occupy")){
                            showMutualDialog();
                        }else{
                            if (toaHashMap.get("俯仰补偿角设置失败")!=null){
                                toaHashMap.get("俯仰补偿角设置失败").show();
                            }
                        }
                    }
                    if(data.getString("lnbcode").equals("0")){//(-1)
                        if (toaHashMap.get("LNB设置成功")!=null){
                            toaHashMap.get("LNB设置成功").show();
                        }
                        SharedPreferenceManager.saveString(mContext,"lnb",strCurrentLnb);
                    }else if(data.getString("lnbcode").equals("-1")){
                        if (toaHashMap.get("LNB设置失败")!=null){
                            toaHashMap.get("LNB设置失败").show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        is_currLatitude=false;
        is_currLongitude=false;
        is_rssiLimit=false;
        is_currentElevoff=false;
        is_currLnb=false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    // 点击退出
    public void refSetOnFinish(View v) {
        finish();
    }
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;
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

    private void stopGet(){
        String stopUrl = GET_STOP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,stopUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(GET_STOP,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if(code.equals("0")){
                        Toast.makeText(SuperSetRefActivity.this, "天线停止命令发送成功", 0).show();
                    }else if(code.equals("-1")){
                        if(msg.equals("acu_occupy")){
                            showMutualDialog();
                        }else{
                            Toast.makeText(SuperSetRefActivity.this, "天线停止命令发送失败", 0).show();
                        }
                    }else{
                        Toast.makeText(SuperSetRefActivity.this, "天线停止命令发送失败", 0).show();
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(GET_STOP,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,0f));
        mRequestQueue.add(stringRequest);
    }
    private void getCompass(){
        String getCompassUrl = GET_COMPASS;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,getCompassUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(GET_COMPASS,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if(code.equals("0")){
                        if (toaHashMap.get("电子罗盘校准命令发送成功")!=null){
                            toaHashMap.get("电子罗盘校准命令发送成功").show();
                        }
                        queryCompassResult();
                    }else if(code.equals("-1")){
                        if(msg.equals("acu_occupy")){
                            showMutualDialog();
                        }else{
                            if (toaHashMap.get("电子罗盘校准命令发送失败")!=null){
                                toaHashMap.get("电子罗盘校准命令发送失败").show();
                            }
                        }
                    }else{
                        if (toaHashMap.get("电子罗盘校准命令发送失败")!=null){
                            toaHashMap.get("电子罗盘校准命令发送失败").show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (toaHashMap.get("连接网元服务器失败")!=null){
                    toaHashMap.get("连接网元服务器失败").show();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(GET_COMPASS,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,0f));
        mRequestQueue.add(stringRequest);
    }
    private void queryCompassResult(){
        String queryCompassResultUrl = QUERY_COMPASS_RESULT;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,queryCompassResultUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoggerSave.responseLog(QUERY_COMPASS_RESULT,response.toString());
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.optString("msg");
                    if(code.equals("0")){
                        if (toaHashMap.get("电子罗盘校准成功")!=null){
                            toaHashMap.get("电子罗盘校准成功").show();
                        }
                    } else if(code.equals("1")){
                        if (toaHashMap.get("正在执行电子罗盘校准,请耐心等候！")!=null){
                            toaHashMap.get("正在执行电子罗盘校准,请耐心等候！").show();
                        }
                        handler.sendEmptyMessageDelayed(2, messageDelay);
                    } else if(code.equals("-1")){
                        if(msg.equals("acu_occupy")){
                            showMutualDialog();
                        }else{
                            if (toaHashMap.get("任务未执行")!=null){
                                toaHashMap.get("任务未执行").show();
                            }
                        }
                    } else if(code.equals("-2")){
                        if (toaHashMap.get("收到非正常回复")!=null){
                            toaHashMap.get("收到非正常回复").show();
                        }
                    } else if(code.equals("-3")){
                        if (toaHashMap.get("超时未收到回复")!=null){
                            toaHashMap.get("超时未收到回复").show();
                        }
                    }else{
                        if (toaHashMap.get("电子罗盘校准失败")!=null){
                            toaHashMap.get("电子罗盘校准失败").show();
                        }
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                return map;
            }
        };
        LoggerSave.requestLog(QUERY_COMPASS_RESULT,stringRequest.toString());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,0f));
        mRequestQueue.add(stringRequest);
    }
    // 查看ping参数
    private void checkPing(){
        String getCheckPingUrl="http://192.168.80.1:9991/api/ping/get";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,getCheckPingUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if(code.equals("0")){
                        String ping  = jsonObject.getString("ping");
                        if("1".equals(ping)){
                            setToggleState(true);
                        }else{
                            setToggleState(false);
                        }
                    }else if(code.equals("-1")){
                        if (toaHashMap.get("ping包参数查询失败")!=null){
                            toaHashMap.get("ping包参数查询失败").show();
                        }
                    }else{
                        if (toaHashMap.get("ping包参数查询失败")!=null){
                            toaHashMap.get("ping包参数查询失败").show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,0f));
        mRequestQueue.add(stringRequest);
    }

    private void setToggleState(boolean bl) {
        mToggleButton.setOnCheckedChangeListener(null);
        mToggleButton.setChecked(bl);
        mToggleButton.setOnCheckedChangeListener(SuperSetRefActivity.this);
    }

    //设置ping参数并生效
    private void PingPost(final boolean isChecked) {
        String setPingUrl="http://192.168.80.1:9991/api/ping/set";
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("ping", mPing);
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    setPingUrl, jsonObject,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //JSONObject jsonObject = new JSONObject(response.toString());
                                String code = response.getString("code");
                                if(code.equals("0")){
                                    if (toaHashMap.get("ping包参数设置成功")!=null){
                                        toaHashMap.get("ping包参数设置成功").show();
                                    }
                                }else if(code.equals("-1")){
                                    if (toaHashMap.get("ping包参数设置失败")!=null){
                                        toaHashMap.get("ping包参数设置失败").show();
                                        backToggleState(isChecked);
                                    }
                                }else{
                                    if (toaHashMap.get("ping包参数设置失败")!=null){
                                        toaHashMap.get("ping包参数设置失败").show();
                                        backToggleState(isChecked);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                backToggleState(isChecked);
                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "连接网元服务器失败", 0).show();
                    backToggleState(isChecked);
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(10*1000,0,0f));
            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当ping包设置失败时，回退Toggle切换按钮的状态
     * @param isChecked
     */
    private void backToggleState(boolean isChecked) {
        mToggleButton.setOnCheckedChangeListener(null);
        mToggleButton.setChecked(!isChecked);
        mToggleButton.setOnCheckedChangeListener(SuperSetRefActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Iterator<Entry<String, Toast>> iter = toaHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Toast> entry = iter.next();
                Toast toast=entry.getValue();
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
		/*for(Toast temp:toasts){
			Field field;
			try {
				field = temp.getClass().getDeclaredField("mTN");
				 field.setAccessible(true);
	                Object obj = field.get(temp);
	                 java.lang.reflect.Method m=obj.getClass().getDeclaredMethod("hide", null);
	                  m.invoke(obj, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
    }
    private void saveStarPara(){
        getRefText_up();
    }
    private void setListViewHeight() {
        int height = DensityUtil.dip2px(mContext, 42)*starAdapter.getCount();
        int scrrenH=(int) (ScreenUtil.getHight(mContext)*0.55);
        int realH=height>=scrrenH?scrrenH:height;
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) starListView.getLayoutParams();
        layoutParams.height=realH;
    }
    private void initDataView() {
        super_ref_numner.setText(currentStar.getSatename());
        super_ref_numner.setSelection(currentStar.getSatename().length());
        String _satelon=ChechIpMask.numDigite(currentStar.getSatelon(), 1);//保留一位有效数字。
        //SharedPreferenceManager.saveString(mContext, "satelon", _satelon);
        //如果带负号
        if(_satelon.substring(0,1).equals("-")||_satelon.substring(0,1).equals("﹣")||_satelon.substring(0,1).equals("－")||_satelon.substring(0,1).equals("﹣")){

            _satelon=_satelon.substring(1);
            mWXJD.setSelection(1);
        }else{
            mWXJD.setSelection(0);
        }
        mEdItude.setText(_satelon);//卫星经度
        if(currentStar.getMode()==null){//信标
            mStatMode.setSelection(0);
            super_xinbiao_rate.setText("--");
        }else if(currentStar.getMode().equals("1")){
            mStatMode.setSelection(1);
            if(!StringUtils.isEmpty(currentStar.getBw())){
                mBandwidth.setSelection(Integer.parseInt(currentStar.getBw()));
            }
            super_zaibo_rate.setText(currentStar.getZfreq());
        }else if(currentStar.getMode().equals("0")){
            mStatMode.setSelection(0);
            super_xinbiao_rate.setText(currentStar.getFreq());
        }
        if(currentStar.getType()==null){
            mPoraization.setSelection(0);
        }else{
            if(!StringUtils.isEmpty(currentStar.getType())){

                mPoraization.setSelection(Integer.parseInt(currentStar.getType()));
            }
        }
        if(currentStar.getMode()==null){
            mStatMode.setSelection(0);
        }else{
            if(!StringUtils.isEmpty(currentStar.getMode())){

                mStatMode.setSelection(Integer.parseInt(currentStar.getMode()));
            }
        }
        if(currentStar.getMode()==null){
            initXinBiaoView();
        }else{
            int _mode=0;
            if(!StringUtils.isEmpty(currentStar.getMode())){
                _mode=Integer.parseInt(currentStar.getMode());
            }

            if(_mode==0){
                initXinBiaoView();

            }else if(_mode==1){
                initZaiBoView();
            } else if(_mode==2){
                initDVBView();
            }
        }
    }
    private void initXinBiaoView() {
        ll_xinbiao_rate.setVisibility(View.VISIBLE);
        ll_zaibo_rate.setVisibility(View.GONE);
        ll_ref_bandwidth.setVisibility(View.GONE);
        ll_center_rate.setVisibility(View.GONE);
        ll_sign_rate.setVisibility(View.GONE);
        if(currentStar!=null){
            super_xinbiao_rate.setText(currentStar.getFreq());
        }
    }

    private void initZaiBoView() {
        ll_xinbiao_rate.setVisibility(View.GONE);
        ll_zaibo_rate.setVisibility(View.VISIBLE);
        ll_ref_bandwidth.setVisibility(View.VISIBLE);
        ll_center_rate.setVisibility(View.GONE);
        ll_sign_rate.setVisibility(View.GONE);
        if(currentStar!=null){
            super_zaibo_rate.setText(currentStar.getZfreq());
        }
    }
    private void initDVBView() {
        ll_center_rate.setVisibility(View.VISIBLE);
        ll_sign_rate.setVisibility(View.VISIBLE);
        ll_xinbiao_rate.setVisibility(View.GONE);
        ll_zaibo_rate.setVisibility(View.GONE);
        ll_ref_bandwidth.setVisibility(View.GONE);
        setDVBViewData();
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
                    pWindow.showAsDropDown(super_ref_numner);
                }
            }, 200);

        }


    }
}
