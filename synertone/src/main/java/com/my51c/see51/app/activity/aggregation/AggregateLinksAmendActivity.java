package com.my51c.see51.app.activity.aggregation;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.came.viewbguilib.ButtonBgUi;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.bean.AggregatedLinkInfoModel;
import com.my51c.see51.app.bean.ChannelModel;
import com.my51c.see51.app.bean.LinkListState;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AggregateLinksAmendActivity extends BaseActivity {

    private Spinner sp_aggregate_mode;
    private GridView gv_channels;
    private EditText et_aggregated_name;
    private ButtonBgUi bt_save;
    private TextView tv_middle_title;
    private List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> channelListBeans;
    private CommonAdapter<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> channelAdapter;
    private AggregatedLinkInfoModel.LinkinfoBean currentLinkinfo;//初始聚合链路，添加时为null
    private String oId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate_links_amend);
        oId =  getIntent().getStringExtra("oId");
        initView();
        initData();
        initEvent();
        try {
            if(!StringUtils.isEmpty(oId)){
                //修改
                doAggregatedLinkInfo();
            }else{
                //添加
                tv_middle_title.setText(R.string.add_aggreated_link);
                sp_aggregate_mode.setSelection(0);
                doNetworkChannelList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAggregatedLinkInfo() throws JSONException, UnsupportedEncodingException {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.AggregatedLinkInfo, params, new MyRequestCallBack(mContext,true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if(!isVisible()){
                    return;
                }
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    AggregatedLinkInfoModel aggregatedLinkInfoModel = GsonUtils.fromJson(responseInfo.result.toString(), AggregatedLinkInfoModel.class);
                    if(aggregatedLinkInfoModel!=null){
                        String code = aggregatedLinkInfoModel.getCode();
                        if("0".equals(code)){
                            currentLinkinfo = aggregatedLinkInfoModel.getLinkinfo();
                            initDefaultChannels();
                            initDefaultView();
                            try {
                                doNetworkChannelList();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initDefaultView() {
        if(currentLinkinfo==null){
            return;
        }
        int position = Integer.parseInt(currentLinkinfo.getMode());
        sp_aggregate_mode.setSelection(position);
        tv_middle_title.setText(currentLinkinfo.getName() + getString(R.string.link_setting));
        et_aggregated_name.setText(currentLinkinfo.getName());
        et_aggregated_name.setSelection(currentLinkinfo.getName().length());
    }

    private void initDefaultChannels() {
        if(currentLinkinfo==null){
            return;
        }
        List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> currentChannelList = currentLinkinfo.getChannelList();
        if(currentChannelList!=null&&currentChannelList.size()>0){
            for(AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean cl:currentChannelList){
                cl.setChoose(true);
            }
        }
    }

    /**
     * 查询网络通道信息
     */
    private void doNetworkChannelList() throws UnsupportedEncodingException, JSONException {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("used","0");
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkChannelList,params, new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if(!isVisible()){
                    return;
                }
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    ChannelModel channelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                    if (channelModel != null) {
                        if ("0".equals(channelModel.getCode())) {
                            List<ChannelModel.ChannelListBean> channelList = channelModel.getChannelList();
                            if (channelList != null) {
                                channelListBeans.clear();
                                addDefaultChannels();
                                addUnOccupyChannels(channelList);
                                channelAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
            }
        });
    }

    private void addUnOccupyChannels(List<ChannelModel.ChannelListBean> channelList) {
        for (ChannelModel.ChannelListBean channelListBean : channelList) {
            AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean channelBean=new AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean();
            channelBean.setChannel(channelListBean.getChannel());
            channelBean.setChannelId(channelListBean.getOId());
            channelBean.setChoose(false);
            channelListBeans.add(channelBean);
        }
    }

    private void addDefaultChannels() {
        if(currentLinkinfo!=null){
            List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> currentChannelList = currentLinkinfo.getChannelList();
            if(currentChannelList!=null&&currentChannelList.size()>0){
                channelListBeans.addAll(currentChannelList);
            }
        }
    }

    private void initEvent() {
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAggregatedLinkConfig();
            }
        });
    }

    /**
     * 聚合链路设置
     */
    private void doAggregatedLinkConfig() {
        try {
            List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> choosedChannels = getCheesedChannels();
            if (verifyInput(choosedChannels)) return;
            RequestParams params=new RequestParams();
            JSONObject jsonObject = new JSONObject();
            if (currentLinkinfo == null) {
                //增加
                AggregatedLinkInfoModel.LinkinfoBean linkBeanAdd = new AggregatedLinkInfoModel.LinkinfoBean();
                linkBeanAdd.setChannelList(choosedChannels);
                linkBeanAdd.setMode(sp_aggregate_mode.getSelectedItemPosition() + "");
                linkBeanAdd.setName(et_aggregated_name.getText().toString());
                linkBeanAdd.setOId("");
                jsonObject.put("operation", "0");
                String json = GsonUtils.toJson(linkBeanAdd);
                jsonObject.put("linkinfo", new JSONObject(json));
            }else{
                //修改
                currentLinkinfo.setChannelList(choosedChannels);
                currentLinkinfo.setMode(sp_aggregate_mode.getSelectedItemPosition() + "");
                currentLinkinfo.setName(et_aggregated_name.getText().toString());
                jsonObject.put("operation", "1");
                String json = GsonUtils.toJson(currentLinkinfo);
                jsonObject.put("linkinfo", new JSONObject(json));
            }

            params.setBodyEntity(new StringEntity(jsonObject.toString()));
            http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.AggregatedLinkConfig, params, new MyRequestCallBack(mContext, true,getString(R.string.sending_request)) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if(!isVisible()){
                        return;
                    }
                    if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                        LinkListState linkListState = GsonUtils.fromJson(responseInfo.result.toString(), LinkListState.class);
                        if (linkListState != null) {
                            if ("0".equals(linkListState.getCode())) {
                                    setResult(RESULT_OK);
                                    finish();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取选中的通道集合
     */
    private List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> getCheesedChannels() {
        Iterator<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> iterator = channelListBeans.iterator();
        List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> channelBeans = new ArrayList<>();
        while (iterator.hasNext()) {
            AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean next = iterator.next();
            if (next.isChoose()) {
                channelBeans.add(next);
            }
        }
        return channelBeans;
    }

    private boolean verifyInput(List<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean> choosedChannels) {
        String s = et_aggregated_name.getText().toString();
        if (StringUtils.isEmpty(s)) {
            Toast.makeText(mContext, R.string.pelase_input_aggregate_name, Toast.LENGTH_LONG).show();
            return true;
        }
        if (choosedChannels.size() == 0) {
            Toast.makeText(mContext, R.string.please_choose_channel, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void initData() {
        sp_aggregate_mode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.aggregate_mode)));
        channelListBeans = new ArrayList<>();
        channelAdapter = new CommonAdapter<AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean>(mContext, R.layout.item_channel_check, channelListBeans) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final AggregatedLinkInfoModel.LinkinfoBean.ChannelListBean item) {
                viewHolder.setTextForCheckBox(R.id.cb_channel, item.getChannel());
                viewHolder.setCheckForCheckBox(R.id.cb_channel, item.isChoose());
                viewHolder.setOnCheckedChangeListener(R.id.cb_channel, new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setChoose(isChecked);
                    }
                });
            }
        };
        gv_channels.setAdapter(channelAdapter);
    }

    private void initView() {
        tv_middle_title = (TextView) findViewById(R.id.tv_middle_title);
        sp_aggregate_mode = (Spinner) findViewById(R.id.sp_aggregate_mode);
        gv_channels = (GridView) findViewById(R.id.gv_channels);
        et_aggregated_name = (EditText) findViewById(R.id.et_aggregated_name);
        bt_save = (ButtonBgUi) findViewById(R.id.bt_save);

    }

    public void onPersonFinish(View view) {
        finish();
    }
}
