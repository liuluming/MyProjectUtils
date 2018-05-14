package com.my51c.see51;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.LoginActivity;
import com.my51c.see51.app.bean.DevStatusBean;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.ScreenUtil;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.SharedPreferenceManager;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    protected HttpUtils http;
    private WarnDialogFragment warnDialogFragment;
    private FragmentManager supportFragmentManager;
    private boolean isVisible;
    private UpdateUIBroadcastReceiver broadcastReceiver;
    private ProgressDialog pd;
    private static ArrayList<String> lastWarnList;
    protected AppData application;
    private RequestQueue mRequestQueue;
    private Unbinder unbinder;

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        LayoutInflater.from(this).setFactory2(new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                if("EditText".equals(name)){
                    EditText editText=new EditText(context,attrs);
                    editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    return  editText;
                }
                return null;
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if("EditText".equals(name)){
                    EditText editText=new EditText(context,attrs);
                    editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    return  editText;
                }
                return null;
            }
        });
        super.onCreate(arg0);
        mContext = this;
        application = (AppData) getApplication();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        application.addActivity(new WeakReference<Activity>(this));
        http = new HttpUtils();
        http.configTimeout(30 * 1000);
        http.configSoTimeout(30 * 1000);
        http.configCurrentHttpCacheExpiry(500);
        supportFragmentManager = getSupportFragmentManager();
        warnDialogFragment = new WarnDialogFragment();
        pd = new ProgressDialog(mContext);
        pd.setCanceledOnTouchOutside(false);
        isVisible = true;
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
         unbinder = ButterKnife.bind(this);
    }
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        unbinder =ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }

    protected void showMutualDialog() {
        /*if(isFinishing()||!isVisible){
			return;
		}*/
        final Dialog noticeDialog = new Dialog(mContext);
        noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_tip_layout, null);
        noticeDialog.setContentView(view);
        Button mInto = (Button) view.findViewById(R.id.jinru);
        mInto.setText("确认");
        mInto.setTypeface(AppData.fontXiti);
        Button mQuxiao = (Button) view.findViewById(R.id.quxiao);
        mQuxiao.setVisibility(View.GONE);
        mQuxiao.setTypeface(AppData.fontXiti);
        TextView tView = (TextView) view.findViewById(R.id.import_tishiword);
        tView.setText("ACU占用，请稍后操作!");
        tView.setTypeface(AppData.fontXiti);
        TextView tView2 = (TextView) view.findViewById(R.id.import_tip);
        tView2.setTypeface(AppData.fontXiti);
        mInto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
            }
        });
        mQuxiao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
            }
        });
        Window window = noticeDialog.getWindow();
        android.view.WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (ScreenUtil.getWidth(mContext) * 0.8);
        noticeDialog.show();
    }
    protected void showLoginDialog() {
        final Dialog noticeDialog = new Dialog(mContext);
        noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_tip_layout, null);
        noticeDialog.setCancelable(false);
        noticeDialog.setContentView(view);
        Button mInto = (Button) view.findViewById(R.id.jinru);
        mInto.setText("确认");
        mInto.setTypeface(AppData.fontXiti);
        Button mQuxiao = (Button) view.findViewById(R.id.quxiao);
        mQuxiao.setVisibility(View.GONE);
        mQuxiao.setTypeface(AppData.fontXiti);
        TextView tView = (TextView) view.findViewById(R.id.import_tishiword);
        tView.setText("该账号已在其它设备登录，请重新登录！");
        tView.setTypeface(AppData.fontXiti);
        TextView tView2 = (TextView) view.findViewById(R.id.import_tip);
        tView2.setTypeface(AppData.fontXiti);
        mInto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
                application.exit();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);

            }
        });
        mQuxiao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
            }
        });
        Window window = noticeDialog.getWindow();
        android.view.WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (ScreenUtil.getWidth(mContext) * 0.8);
        noticeDialog.show();
    }
    protected void showDia(String... msg) {
        if (msg != null && msg.length > 0) {
            pd.setMessage("\"" + msg[0] + "....." + "\"");
        } else {
            pd.setMessage("正在加载数据.....");
        }
        if(isVisible){
            pd.show();
        }

    }

    protected void dismissDia() {
        if (pd != null&&isVisible) {
            pd.dismiss();
        }
    }
   protected boolean isShowingDia(){
       if(pd!=null&&isVisible){
           return  pd.isShowing();
       }else{
           return  false;
       }

   }
    private void showWarnDialogFragment(ArrayList<String> warnList) {
        if (!isFinishing() && isVisible) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("value", warnList);
            if (!warnDialogFragment.isAdded()) {
                warnDialogFragment.setArguments(bundle);
            }
            if (!warnDialogFragment.isVisible()) {
                warnDialogFragment.show(supportFragmentManager, "");
            } else {
                warnDialogFragment.reFreshData(bundle);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("ServiceUpdateUI");
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }
    private void doDevstatu() {
        try {
            RequestParams params = new RequestParams("UTF-8");
            JSONObject jsonObjet = new JSONObject();
            jsonObjet.put("sessionToken", AppData.accountModel.getSessionToken());
            params.setBodyEntity(new StringEntity(jsonObjet.toString(), "UTF-8"));
            params.setContentType("application/json");
            http.send(HttpMethod.POST,
                    XTHttpUtil.devstatu, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            //Toast.makeText(mContext, responseInfo.result, 1).show();
                            DevStatusBean devStatusBean = GsonUtils.fromJson(responseInfo.result, DevStatusBean.class);
                            if (devStatusBean != null) {
                                String code = devStatusBean.getCode();
                                if ("0".equals(code)) {
                                    ArrayList<String> warnList = new ArrayList<>();
                                    initWarnList(devStatusBean, warnList);
                                    if (warnList.size() > 0) {
                                        if (!isFinishing() && isVisible) {
                                            if(lastWarnList!=null&&lastWarnList.equals(warnList)){
                                                boolean isNoRead = SharedPreferenceManager.getBoolean(mContext, "isNoRead");
                                                  if(isNoRead){
                                                      showWarnDialogFragment(warnList);
                                                  }
                                            }else{
                                                SharedPreferenceManager.saveBoolean(mContext,"isNoRead",true);
                                                showWarnDialogFragment(warnList);
                                            }
                                            lastWarnList=warnList;
                                        }
                                    }
                                } else if("-2".equals(code)){
                                    if (!isFinishing() && isVisible) {
                                            //showLoginDialog();
                                    }
                                    //if (!isFinishing() && isVisible) {
                                        //Toast.makeText(mContext, devStatusBean.getMsg(), Toast.LENGTH_LONG).show();

                                }
                            }


                        }

                        @Override
                        public void onFailure(HttpException arg0, String arg1) { //
                            //TODO Auto-generated method stub

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化 错误信息
     *
     * @param devStatusBean
     * @param warnList
     */
    private void initWarnList(DevStatusBean devStatusBean, List<String> warnList) {
        String gl = devStatusBean.getGeneral();
        if ("-1".equals(gl)) {
            warnList.add(getString(R.string.warn_gl));
        }
        String current = devStatusBean.getCurrent();
        if ("-1".equals(current)) {
            warnList.add(getString(R.string.wran_current));
        }
        String hot = devStatusBean.getHot();
        if ("-1".equals(hot)) {
            warnList.add(getString(R.string.warn_hot));
        }
        String voltage = devStatusBean.getVoltage();
        if ("-1".equals(voltage)) {
            warnList.add(getString(R.string.warn_voltage));
        }
        String search = devStatusBean.getSearch();
        if ("-1".equals(search)) {
            warnList.add(getString(R.string.warn_search));
        }
        String oriemotor = devStatusBean.getOrimotor();
        if ("-1".equals(oriemotor)) {
            warnList.add(getString(R.string.warn_oriemotor));
        }
        String sendzero = devStatusBean.getSendzero();
        if ("-1".equals(sendzero)) {
            warnList.add(getString(R.string.warn_send_zero));
        }
        String pitchmotor = devStatusBean.getPitchmotor();
        if ("-1".equals(pitchmotor)) {
            warnList.add(getString(R.string.warn_pitchmotor));
        }
        String rollzero = devStatusBean.getRollzero();
        if ("-1".equals(rollzero)) {
            warnList.add(getString(R.string.warn_rollzero));
        }
        String oriezero = devStatusBean.getOrizero();
        if ("-1".equals(oriezero)) {
            warnList.add(getString(R.string.wran_oriezero));
        }
        String gps = devStatusBean.getGps();
        if ("-1".equals(gps)) {
            warnList.add(getString(R.string.warn_gps));
        }
        String rf = devStatusBean.getRf();
        if ("-1".equals(rf)) {
            warnList.add(getString(R.string.warn_rf));
        }
        String pitchzero = devStatusBean.getPitchzero();
        if ("-1".equals(pitchzero)) {
            warnList.add(getString(R.string.warn_pitchzero));
        }
        String sensor = devStatusBean.getSensor();
        if ("-1".equals(sensor)) {
            String odType = devStatusBean.getOdutype();
            if (odType.contains("7") || odType.equals("8")) {
                warnList.add(getString(R.string.warn_dzlp));
            } else {
                warnList.add(getString(R.string.warn_tly));
            }
        }
    }

    class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(mContext, "onReceive", 0).show();
            if (AppData.accountModel != null) {
                doDevstatu();
            }

        }


    }

    public void querySessionStatus(final OnSessionStatusListener onSessionStatusListener) {
        final String mToken = SharedPreferenceManager.getString(mContext, "mToken");
        String queryStatusUrl = XTHttpUtil.QUERY_STATUS;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionToken", mToken);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    queryStatusUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                String code = jsonObject.getString("code");
                                if ("0".equals(code)) {
                                    //Session未失效
                                    onSessionStatusListener.sessionSuccess();
                                } else if ("-1".equals(code)) {
                                    //Session失效
                                   showLoginDialog();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    onSessionStatusListener.sessionErrorResponse();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public abstract class OnSessionStatusListener{
        public abstract void sessionSuccess();
        public void sessionErrorResponse(){

        }
    }
}

@SuppressLint("ValidFragment")
class WarnDialogFragment extends DialogFragment {

    private WarnDetailDialogFragment warnDetailDialogFragment;
    private ArrayList<String> warnList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.dialog_warn_layout, container,
                false);
        return view;
    }

    public void reFreshData(Bundle bundle) {
        warnList = bundle.getStringArrayList("value");
        TextView tv_warn_count = (TextView) getView().findViewById(
                R.id.tv_warn_count);
        tv_warn_count.setText(warnList.size() + "");
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onActivityCreated(arg0);
        Bundle bundle = getArguments();
        warnList = bundle.getStringArrayList("value");
        TextView tv_warn_count = (TextView) getView().findViewById(
                R.id.tv_warn_count);
        tv_warn_count.setText(warnList.size() + "");
        if (warnDetailDialogFragment != null && warnDetailDialogFragment.isVisible()) {
            warnDetailDialogFragment.dismiss();
        }
        getView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                showWarnDetailsDialog(warnList);
                SharedPreferenceManager.saveBoolean(getActivity(),"isNoRead",false);


            }
        });

    }

    protected void showWarnDetailsDialog(ArrayList<String> warnList) {
        warnDetailDialogFragment = new WarnDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("value", warnList);
        warnDetailDialogFragment.setArguments(bundle);
        warnDetailDialogFragment.show(getActivity().getSupportFragmentManager(), "");

    }

    @Override
    public void onResume() {
        super.onResume();
        // 必须放在onresume 才能填充屏幕
        setDialogLayout(getDialog());
    }

    private void setDialogLayout(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 注意此处
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (ScreenUtil.getWidth(getActivity()) * 0.65);
        window.setAttributes(params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_guide);
    }

}

@SuppressLint("ValidFragment")
class WarnDetailDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.dialog_warn_detail_layout,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onActivityCreated(arg0);
        Bundle bundle = getArguments();
        final ArrayList<String> warnList = bundle.getStringArrayList("value");
        final ListView lv_content = (ListView) getView().findViewById(
                R.id.lv_content);
        CommonAdapter<String> adapter = new CommonAdapter<String>(
                getActivity(), R.layout.item_warn_layout, warnList) {

            @Override
            protected void fillItemData(CommonViewHolder viewHolder,
                                        int position, String item) {
                viewHolder.setTextForTextView(R.id.tv_warn_content,
                        item);
				/*viewHolder.setTextForTextView(R.id.tv_warn_time,
						item.getWarnTime());*/
            }
        };
        lv_content.setAdapter(adapter);
        lv_content.post(new Runnable() {

            @Override
            public void run() {
                int height = lv_content.getHeight();
                int Screenhight = (int) (ScreenUtil.getHight(getActivity()) * 0.8);
                LayoutParams layoutParams = lv_content.getLayoutParams();
                layoutParams.height = height >= Screenhight ? Screenhight
                        : height;
                lv_content.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 必须放在onresume 才能填充屏幕
        setDialogLayout(getDialog());
    }

    private void setDialogLayout(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 注意此处
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (ScreenUtil.getWidth(getActivity()) * 0.85);
        window.setAttributes(params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_guide);
    }


}