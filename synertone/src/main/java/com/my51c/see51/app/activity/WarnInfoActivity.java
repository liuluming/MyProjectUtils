package com.my51c.see51.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.bean.WarnBean;
import com.my51c.see51.app.bean.WarnInfoBean;
import com.my51c.see51.app.bean.WarnInfoBean.AlarmListBean;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.TimeUtils;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.PinnedSectionListView;
import com.my51c.see51.widget.PinnedSectionListView.PinnedSectionListAdapter;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarnInfoActivity extends BaseActivity {
    private PinnedSectionListView pslv_content;
    private HashMap<String, String> arrayMap;
    private Spinner sp_device_type;
    private String mDeviceType;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_warn_info_layout);
        arrayMap = new HashMap<>();
        initAlarmData();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        sp_device_type.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                switch (position) {
                    case 0:
                        sp_device_type.setSelection(0);
                        mDeviceType = "0";
                        doDevstatuAlarm();
                        break;
                    case 1:
                        sp_device_type.setSelection(1);
                        mDeviceType = "10";
                        doDevstatuAlarm();
                        break;
                    case 2:
                        sp_device_type.setSelection(2);
                        mDeviceType = "11";
                        doDevstatuAlarm();
                        break;
                    case 3:
                        sp_device_type.setSelection(3);
                        mDeviceType = "12";
                        doDevstatuAlarm();
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void initAlarmData() {
        arrayMap.put("100000", "路由板重启检测");
        arrayMap.put("100001", "手机板（4G模块）接入检测");
        arrayMap.put("100002", "4G通信链路检测");
        arrayMap.put("100003", "卫星调制解调器接入检测");
        arrayMap.put("100004", "卫星通信链路检测");
        arrayMap.put("100005", "路由板与ODU通信链路检测");
        arrayMap.put("100006", "WIFI模块异常检测");
        arrayMap.put("100007", "WIFI链路检测");
        arrayMap.put("100008", "内存存储满检测");
        arrayMap.put("100009", "内存存储满检测");
        arrayMap.put("100010", "SATA硬盘存储满检测");

        arrayMap.put("110000", "一般错误");
        arrayMap.put("110001", "过流");
        arrayMap.put("110002", "过温");
        arrayMap.put("110003", "低电压");
        arrayMap.put("110004", "卫星未找到");
        arrayMap.put("110005", "方位电机过载");
        arrayMap.put("110006", "发射归零故障");
        arrayMap.put("110007", "俯仰电机过载");
        arrayMap.put("110008", "射频信号故障");
        arrayMap.put("110009", "横滚归零错误");
        arrayMap.put("110010", "方位归零故障");
        arrayMap.put("110011", "GPS异常告警");
        arrayMap.put("110012", "俯仰归零故障");
        arrayMap.put("110013", "电子罗盘/陀螺仪故障");

        arrayMap.put("120000", "增值业务摄像头未找到");

    }

    private void initData() {
        sp_device_type.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.deviceType)));
        doDevstatuAlarm();
    }

    private void doDevstatuAlarm() {

        try {
            showDia();
            RequestParams params = new RequestParams("UTF-8");
            JSONObject jsonObjet = new JSONObject();
            jsonObjet.put("sessionToken",
                    AppData.accountModel.getSessionToken());
            jsonObjet.put("dev_type", mDeviceType);
            String currnetTime = TimeUtils.getCurrentTime();
            jsonObjet.put("startTime", TimeUtils.addDay(currnetTime, -7));
            jsonObjet.put("endTime", currnetTime);
            params.setBodyEntity(new StringEntity(jsonObjet.toString(), "UTF-8"));
            params.setContentType("applicatin/json");
            http.send(HttpMethod.POST, XTHttpUtil.devstatuAlarm, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            //Toast.makeText(getApplicationContext(), responseInfo.result.toString(), 0).show();
                            dismissDia();
                            WarnInfoBean warnInfoBean = GsonUtils.fromJson(
                                    responseInfo.result, WarnInfoBean.class);
                            if (warnInfoBean != null) {
                                String code = warnInfoBean.getCode();
                                String msg = warnInfoBean.getMsg();
                                if ("0".equals(code)) {
                                    List<AlarmListBean> alarmList = warnInfoBean
                                            .getAlarmList();
                                    if (alarmList != null) {
                                        for (AlarmListBean alarmListBean : alarmList) {
                                            if (alarmListBean.getTrigger() == 1) {
                                                alarmListBean
                                                        .setAlarmType(" （解除）");
                                            } else {
                                                alarmListBean
                                                        .setAlarmType(" （触发）");
                                            }
                                            String alarmCode = alarmListBean
                                                    .getAlarmCode();
                                            alarmListBean.setAlarmContent(arrayMap
                                                    .get(alarmCode)
                                                    + alarmListBean
                                                    .getAlarmType());
                                        }
                                        initWarnAdapter(alarmList);
                                    }
                                } else if("-2".equals(code)) {
                                        showLoginDialog();
                                    /*ToastShow.showCustomDialog(
                                            warnInfoBean.getMsg(), mContext);*/
                                }
                            }
                        }

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            dismissDia();

                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
		 * params.setContentType("applicatin/json");
		 * params.addBodyParameter("sessionToken"
		 * ,AppData.accountModel.getSessionToken());
		 * params.addBodyParameter("dev_type","0"); String currnetTime
		 * =TimeUtils.getCurrentTime();
		 * params.addBodyParameter("startTime","2017-08-09 10:00:00");
		 * params.addBodyParameter("endTime","2017-08-09 12:00:00");
		 * params.addBodyParameter("startTime",currnetTime);
		 * params.addBodyParameter("endTime",TimeUtils.addDay(currnetTime, -6));
		 */
    }

    private void initView() {
        pslv_content = (PinnedSectionListView) findViewById(R.id.pslv_content);
        sp_device_type = (Spinner) findViewById(R.id.sp_device_type);
        TextView tv_warn = (TextView) findViewById(R.id.tv_warn);
        tv_warn.setTypeface(AppData.fontXiti);
    }

    private void initWarnAdapter(List<AlarmListBean> alarmList) {
        String lastTime = "";
        int sectionPosition = 0, listPosition = 0;
        List<AlarmListBean> dataSource = new ArrayList<>();
        for (AlarmListBean alarmListBean : alarmList) {
            String alarmTime = alarmListBean.getAlarmTime();// 2016-07-05
            // 14:30:12
            if (TimeUtils.isSameDate(alarmTime, lastTime)) {
                alarmListBean.setType(AlarmListBean.ITEM);
                alarmListBean.sectionPosition = sectionPosition;
                alarmListBean.listPosition = listPosition++;
            } else {
                alarmListBean.setType(AlarmListBean.SECTION);
                alarmListBean.sectionPosition = sectionPosition;
                alarmListBean.listPosition = listPosition++;
                sectionPosition++;
            }
            dataSource.add(alarmListBean);
            if (alarmListBean.getType() == AlarmListBean.SECTION) {
                AlarmListBean bean = new AlarmListBean();
                bean.setType(AlarmListBean.ITEM);
                bean.sectionPosition = sectionPosition;
                bean.listPosition = listPosition++;
                bean.setAlarmCode(alarmListBean.getAlarmCode());
                bean.setAlarmContent(alarmListBean.getAlarmContent());
                bean.setAlarmTime(alarmListBean.getAlarmTime());
                bean.setTrigger(alarmListBean.getTrigger());
                dataSource.add(bean);
            }
            lastTime = alarmTime;
        }
        SimpleAdapter adapter = new SimpleAdapter(mContext,
                R.layout.item_warn_info_layout, dataSource) {

            @Override
            protected void fillItemData(CommonViewHolder viewHolder,
                                        int position, AlarmListBean item) {
                if (item.type == WarnBean.SECTION) {
                    viewHolder.setTextForTextView(R.id.tv_section, item
                            .getAlarmTime().split(" ")[0]);
                    viewHolder.setBackgroundForView(R.id.ll_content,
                            getResources().getColor(R.color.green_light));
                    viewHolder.setVisibility(R.id.tv_section, View.VISIBLE);
                } else if (item.type == WarnBean.ITEM) {
                    viewHolder.setTextForTextView(R.id.tv_content,
                            item.getAlarmContent());
                    viewHolder.setTextForTextView(R.id.tv_time,
                            item.getAlarmTime());
                    viewHolder.setVisibility(R.id.tv_section, View.GONE);
                }

            }

        };
        pslv_content.setAdapter(adapter);
    }

    public void onPersonFinish(View v) {
        finish();

    }

    abstract class SimpleAdapter extends CommonAdapter<AlarmListBean> implements
            PinnedSectionListAdapter {

        public SimpleAdapter(Context context, int itemLayoutResId,
                             List<AlarmListBean> dataSource) {
            super(context, itemLayoutResId, dataSource);

        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == WarnBean.SECTION;
        }

    }
}
