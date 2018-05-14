package com.my51c.see51.app.activity.aggregation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.my51c.see51.app.bean.ChannelModel;
import com.my51c.see51.app.bean.LinkListState;
import com.my51c.see51.app.bean.NetAdapterModel;
import com.my51c.see51.app.bean.ServiceInfoModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.LvHeightUtil;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AddServiceActivity extends BaseActivity implements View.OnClickListener{
    private Spinner sp_service_mode,spinner_protocol_mode;
    private GridView  gv_service_channels,gv_net_channels;
    private LinearLayout ll_service_channels,ll_service_net;
    private String mServiceMode,mName,mId,mProtocolMode,mLinkType;
    private EditText et_sour_ip,et_sour_port,et_dest_ip,et_service_priority,et_service_name,et_dest_port;
    private List<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean> transferPolicyListBeans=new ArrayList<>();
    private CommonAdapter<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean> transferPolicyListAdapter ;
    private ButtonBgUi bt_service_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        initView();
        initData();
        doAggregatedLinkList(true);

    }

    private void initView() {
        mName = getIntent().getStringExtra("name");
        mId = getIntent().getStringExtra("id");
        TextView tv_middle_title=(TextView) findViewById(R.id.tv_middle_title);
        ImageView iv_right=(ImageView)findViewById(R.id.iv_right);
        tv_middle_title.setText(mName);
        iv_right.setVisibility(View.GONE);
        sp_service_mode = (Spinner) findViewById(R.id.sp_service_mode);
        gv_service_channels=(GridView)findViewById(R.id.gv_service_channels);
        gv_net_channels=(GridView)findViewById(R.id.gv_net_channels);
        ll_service_channels= (LinearLayout) findViewById(R.id.ll_service_channels);
        ll_service_net= (LinearLayout) findViewById(R.id.ll_service_net);
        spinner_protocol_mode = (Spinner) findViewById(R.id.spinner_protocol_mode);
        et_service_name= (EditText) findViewById(R.id.et_service_name);
        et_sour_ip= (EditText) findViewById(R.id.et_sour_ip);
        et_sour_port= (EditText) findViewById(R.id.et_sour_port);
        et_dest_ip= (EditText) findViewById(R.id.et_dest_ip);
        et_dest_port= (EditText) findViewById(R.id.et_dest_port);
        et_service_priority= (EditText) findViewById(R.id.et_service_priority);
        bt_service_save= (ButtonBgUi) findViewById(R.id.bt_service_save);
        bt_service_save.setOnClickListener(this);
    }
    private void initData() {
        sp_service_mode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.service_mode)));
        sp_service_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mServiceMode=sp_service_mode.getSelectedItem().toString();
                if(mServiceMode.equals("聚合链路")){
                   ll_service_channels.setVisibility(View.VISIBLE);
                    ll_service_net.setVisibility(View.GONE);
                    mLinkType="0" ;
                    doAggregatedLinkList(true);
                }else {
                    ll_service_channels.setVisibility(View.GONE);
                    ll_service_net.setVisibility(View.VISIBLE);
                    mLinkType="1" ;
                    doNetworkAdapterList(true);
                }
                initSpinnerData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_protocol_mode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.protocol_mode)));
        spinner_protocol_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(spinner_protocol_mode.getSelectedItem().toString().equals("TCP")){
                    mProtocolMode="0";
                }else {
                    mProtocolMode="1";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initSpinnerData() {
        transferPolicyListAdapter=new CommonAdapter<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean>(mContext,R.layout.item_service_check,transferPolicyListBeans) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final  ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean item) {
                viewHolder.setTextForCheckBox(R.id.cb_service_channel,item.getLinkName());
                viewHolder.setCheckForCheckBox(R.id.cb_service_channel, item.isChoose());
                viewHolder.setTextForEditText(R.id.et_service_weight,item.getWeight());
                viewHolder.setItemWeightEditText(mContext,R.id.et_service_weight,item);
                viewHolder.setOnCheckedChangeListener(R.id.cb_service_channel,new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setChoose(isChecked);
                    }
                });
            }
        };

        gv_service_channels.setAdapter(transferPolicyListAdapter);

        transferPolicyListAdapter=new CommonAdapter<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean>(mContext,R.layout.item_service_check,transferPolicyListBeans) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final  ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean item) {
                viewHolder.setTextForCheckBox(R.id.cb_service_channel,item.getLinkName());
                viewHolder.setCheckForCheckBox(R.id.cb_service_channel, item.isChoose());
                viewHolder.setTextForEditText(R.id.et_service_weight,item.getWeight());
                viewHolder.setItemWeightEditText(mContext,R.id.et_service_weight,item);
                viewHolder.setOnCheckedChangeListener(R.id.cb_service_channel,new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setChoose(isChecked);


                    }
                });
            }
        };
        gv_net_channels.setAdapter(transferPolicyListAdapter);
    }
    /**
     * 添加业务
     */
    private void addService() {
        try {
            RequestParams params = new RequestParams("UTF-8");
            JSONObject jsonObject = new JSONObject();
            transferPolicyListBeans = getChoosedServices();
            ServiceInfoModel.ServiceInfoBean serviceInfoBean=new ServiceInfoModel.ServiceInfoBean();
            serviceInfoBean.setOId("");
            serviceInfoBean.setName(et_service_name.getText().toString());
            serviceInfoBean.setSourIP(et_sour_ip.getText().toString());
            serviceInfoBean.setSourPort(et_sour_port.getText().toString());
            serviceInfoBean.setDestIP(et_dest_ip.getText().toString());
            serviceInfoBean.setDestPort(et_dest_port.getText().toString());
            serviceInfoBean.setProtocol(mProtocolMode);
            serviceInfoBean.setLinkType(mLinkType);
            serviceInfoBean.setPriority(et_service_priority.getText().toString());
            serviceInfoBean.setTransferPolicyList(transferPolicyListBeans);
            jsonObject.put("operation", "0");
            String json= GsonUtils.toJson(serviceInfoBean);
            jsonObject.put("serviceInfo", new JSONObject(json));
            params.setBodyEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
            params.setContentType("application/json");
            http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.SetServiceConfig, params, new MyRequestCallBack(mContext, true) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                        ChannelModel configChannelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                        if (configChannelModel != null) {
                            if ("0".equals(configChannelModel.getCode())) {
                                setResult(RESULT_OK);
                                finish();
                                Toast.makeText(mContext, R.string.add_service_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, configChannelModel.getMsg(), Toast.LENGTH_SHORT).show();
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
     * 获取选中的聚合链路和权重
     */
    private List<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean> getChoosedServices() {
        Iterator<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean> iterator = transferPolicyListBeans.iterator();
        List<ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean> serviceBeans = new ArrayList<>();
        while (iterator.hasNext()) {
            ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean next = iterator.next();
            if (next.isChoose()) {
                serviceBeans.add(next);
            }
        }
        return serviceBeans;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.bt_service_save:
                if (StringUtils.isEmpty(et_service_name.getText())) {
                    Toast.makeText(mContext, R.string.et_service_name, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_sour_ip.getText())) {
                    Toast.makeText(mContext,R.string.et_sour_ip, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_sour_port.getText())) {
                    Toast.makeText(mContext, R.string.et_sour_port, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_dest_ip.getText())) {
                    Toast.makeText(mContext, R.string.et_dest_ip, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_dest_port.getText())) {
                    Toast.makeText(mContext, R.string.et_dest_port, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_service_priority.getText())) {
                    Toast.makeText(mContext, R.string.et_service_priority, Toast.LENGTH_LONG).show();
                    return;
                }
                addService();
                break;
        }
    }
    public void onPersonFinish(View view) {
        finish();
    }

    private void doNetworkAdapterList(boolean b) {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkAdapterList, new MyRequestCallBack(mContext, b) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    NetAdapterModel netAdapterModel = GsonUtils.fromJson(result, NetAdapterModel.class);
                    if (netAdapterModel != null) {
                        String code = netAdapterModel.getCode();
                        if ("0".equals(code)) {
                            List<NetAdapterModel.AdapterListBean> interfaceList = netAdapterModel.getAdapterList();
                            transferPolicyListBeans.clear();
                            addUnOccupyAdapers(interfaceList);
                        } else {
                            Toast.makeText(mContext, R.string.query_network_adapter_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    private void doAggregatedLinkList(boolean isShow) {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.AggregatedLinkList, new MyRequestCallBack(mContext,isShow) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if(!isVisible()){
                    return;
                }
                if(!StringUtils.isEmpty(responseInfo.result.toString())){
                    LinkListState linkListState = GsonUtils.fromJson(responseInfo.result.toString(), LinkListState.class);
                    if(linkListState!=null){
                        if("0".equals(linkListState.getCode())){
                            List<LinkListState.LinkListBean> linkList = linkListState.getLinkList();
                            if(transferPolicyListBeans.size()!=0) {
                                transferPolicyListBeans.clear();
                            }
                            addUnOccupyLinks(linkList);
                        }else{
                            Toast.makeText(mContext, R.string.query_aggregate_link_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        });
    }
    private void addUnOccupyAdapers(List<NetAdapterModel.AdapterListBean> interfaceList) {
        for (NetAdapterModel.AdapterListBean adapterListBean : interfaceList) {
            ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean  bean=new  ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean();
            bean.setLinkId(adapterListBean.getOId());
            bean.setLinkName(adapterListBean.getAdapterName());
            bean.setChoose(false);
            transferPolicyListBeans.add(bean);

        }
        transferPolicyListAdapter.notifyDataSetChanged();
        LvHeightUtil.setGridViewHeightBasedOnChildren(gv_service_channels);
        LvHeightUtil.setGridViewHeightBasedOnChildren(gv_net_channels);
    }
    private void addUnOccupyLinks(List<LinkListState.LinkListBean> linkList) {
        for (LinkListState.LinkListBean linkListBean : linkList) {
            ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean  bean=new  ServiceInfoModel.ServiceInfoBean.TransferPolicyListBean();
            bean.setLinkId(linkListBean.getOId());
            bean.setLinkName(linkListBean.getName());
            bean.setChoose(false);
            transferPolicyListBeans.add(bean);

        }
        initSpinnerData();
        transferPolicyListAdapter.notifyDataSetChanged();
        LvHeightUtil.setGridViewHeightBasedOnChildren(gv_service_channels);
        LvHeightUtil.setGridViewHeightBasedOnChildren(gv_net_channels);
    }
}
