package com.my51c.see51.app.activity.aggregation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.my51c.see51.app.bean.APNModel;
import com.my51c.see51.app.bean.AdapterInfoModel;
import com.my51c.see51.app.bean.AdapterModel;
import com.my51c.see51.app.bean.BaseModel;
import com.my51c.see51.app.bean.InterfaceModel;
import com.my51c.see51.app.bean.PPPoEModel;
import com.my51c.see51.app.bean.StaticIPModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.app.utils.TransformUtils;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;

public class NetAdapterAmendActivity extends BaseActivity {
    @BindView(R.id.more_finish)
    ImageButton moreFinish;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_interface_mode)
    TextView tvInterfaceMode;
    @BindView(R.id.sp_interface_mode)
    Spinner spInterfaceMode;
    @BindView(R.id.tv_protocol_mode)
    TextView tvProtocolMode;
    @BindView(R.id.sp_protocol_mode)
    Spinner spProtocolMode;
    @BindView(R.id.tv_sour_ip)
    TextView tvSourIp;
    @BindView(R.id.et_sour_ip)
    EditText etSourIp;
    @BindView(R.id.tv_gateway)
    TextView tvGateway;
    @BindView(R.id.et_gateway)
    EditText etGateway;
    @BindView(R.id.tv_mask)
    TextView tvMask;
    @BindView(R.id.et_mask)
    EditText etMask;
    @BindView(R.id.tv_dns1)
    TextView tvDns1;
    @BindView(R.id.et_dns1)
    EditText etDns1;
    @BindView(R.id.tv_dns2)
    TextView tvDns2;
    @BindView(R.id.et_dns2)
    EditText etDns2;
    @BindView(R.id.ll_static_ip_config)
    LinearLayout llStaticIpConfig;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.ll_PPPoE_config)
    LinearLayout llPPPoEConfig;
    @BindView(R.id.tv_apn)
    TextView tvApn;
    @BindView(R.id.et_apn)
    EditText etApn;
    @BindView(R.id.tv_apn_username)
    TextView tvApnUsername;
    @BindView(R.id.et_apn_username)
    EditText etApnUsername;
    @BindView(R.id.tv_anp_password)
    TextView tvAnpPassword;
    @BindView(R.id.et_apn_password)
    EditText etApnPassword;
    @BindView(R.id.ll_anp_config)
    LinearLayout llAnpConfig;
    @BindView(R.id.bt_save)
    ButtonBgUi btSave;
    @BindView(R.id.tv_net_adapter_name)
    TextView tvNetAdapterName;
    @BindView(R.id.et_net_adapter_name)
    EditText etNetAdapterName;
    private static final String NET_GAPE = "0";
    private static final String MOBILE_NETWORK = "1";
    private static String DHCP;
    private static String STATIC_IP;
    private static String PPPOE;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.et_confirm_apn_password)
    EditText etConfirmApnPassword;
    private List<String> interfaces;
    private SpinnerAdapter interfaceAdapter;
    private List<InterfaceModel.InterfaceListBean> interfaceList;
    private APNModel apnModel;
    private StaticIPModel staticIPModel;
    private PPPoEModel ppPoEModel;
    private AdapterModel noConfigModel;
    private List<String> currentProtocolList;
    private String[] protocols;
    private SpinnerAdapter protocolAdapter;
    private String oId;//适配器id
    private AdapterInfoModel.AdapterInfoBean currentAdapterInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_adapter_amend);
        oId=getIntent().getStringExtra("oId");
        initProtocol();
        initData();
        initEvent();
        try {
            if(!StringUtils.isEmpty(oId)){
                //修改
                doNetworkAdapterInfo();
            }else{
                //添加
                tvMiddleTitle.setText(R.string.add_adapter);
                doInterfaceList();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doNetworkAdapterInfo() throws JSONException, UnsupportedEncodingException {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkAdapterInfo, params, new MyRequestCallBack(mContext,true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if (!isVisible()) {
                    return;
                }
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    AdapterInfoModel adapterInfoModel = GsonUtils.fromJson(result, AdapterInfoModel.class);
                    if(adapterInfoModel!=null){
                        String code = adapterInfoModel.getCode();
                        if("0".equals(code)){
                            currentAdapterInfoModel = adapterInfoModel.getAdapterInfo();
                            if (currentAdapterInfoModel != null) {
                                initDefaultView();
                                try {
                                    doInterfaceList();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void initDefaultView() {
        tvMiddleTitle.setText(currentAdapterInfoModel.getAdapterName() + getString(R.string.setting_args));
        if (!StringUtils.isEmpty(currentAdapterInfoModel.getAdapterName())) {
            etNetAdapterName.setText(currentAdapterInfoModel.getAdapterName());
            etNetAdapterName.setSelection(currentAdapterInfoModel.getAdapterName().length());
        }
    }

    private void initProtocol() {
        DHCP = getString(R.string.DHCP);
        STATIC_IP = getString(R.string.STATIC_IP);
        PPPOE = getString(R.string.PPPOE);
    }

    private void initEvent() {
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(etNetAdapterName.getText())) {
                    Toast.makeText(mContext, "请输入适配器名称", Toast.LENGTH_LONG).show();
                    return;
                }
                AdapterModel currentModel = getCurrentModel();
                if (verifyInput(currentModel)) return;
                try {
                    doNetworkAdapterConfig(currentModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        spInterfaceMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (interfaceList != null) {
                    InterfaceModel.InterfaceListBean bean = interfaceList.get(position);
                    String type = bean.getType();
                    filterProtocolByType(type);
                    String protocolSelectedItem;
                    try {
                        protocolSelectedItem = (String) spProtocolMode.getSelectedItem();
                    } catch (Exception e) {
                        e.printStackTrace();
                        protocolSelectedItem = getString(R.string.DHCP);
                    }

                    if (NET_GAPE.equals(type) && protocolSelectedItem.equals(STATIC_IP)) {
                        //静态IP
                        initStaticIP();
                    } else if (NET_GAPE.equals(type) && protocolSelectedItem.equals(PPPOE)) {
                        //PPPOE
                        initPPPOE();
                    } else if (NET_GAPE.equals(type) && protocolSelectedItem.equals(DHCP)) {
                        initNoConfig();
                    } else if (MOBILE_NETWORK.equals(type) && protocolSelectedItem.equals(DHCP)) {
                        //APN
                        initAPN();
                    } else {
                        //Toast.makeText(mContext,"this is impossible",Toast.LENGTH_LONG).show();
                    }
                    if(currentAdapterInfoModel!=null){
                        if(currentAdapterInfoModel.getInterfaceX().equals(bean.getInterfaceX())){
                            initConfigView(currentAdapterInfoModel);
                        }else{
                            initConfigView(null);
                        }
                    }else{
                        initConfigView(null);
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spProtocolMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItemPosition = spInterfaceMode.getSelectedItemPosition();
                String itemAtPosition = (String) parent.getItemAtPosition(position);
                String type = null;
                InterfaceModel.InterfaceListBean bean = null;
                if (interfaceList != null&&selectedItemPosition>=0) {
                    bean = interfaceList.get(selectedItemPosition);
                    type = bean.getType();
                }
                if (NET_GAPE.equals(type) && itemAtPosition.equals(STATIC_IP)) {
                    //静态IP
                    initStaticIP();
                } else if (NET_GAPE.equals(type) && itemAtPosition.equals(PPPOE)) {
                    //PPPOE
                    initPPPOE();
                } else if (NET_GAPE.equals(type) && itemAtPosition.equals(DHCP)) {
                    initNoConfig();
                } else if (MOBILE_NETWORK.equals(type) && itemAtPosition.equals(DHCP)) {
                    //APN
                    initAPN();
                } else {
                    // Toast.makeText(mContext,"this is impossible",Toast.LENGTH_LONG).show();
                }
                if(bean!=null){
                    if(currentAdapterInfoModel!=null){
                        if(currentAdapterInfoModel.getInterfaceX().equals(bean.getInterfaceX())){
                            initConfigView(currentAdapterInfoModel);
                        }else{
                            initConfigView(null);
                        }
                    }else{
                        initConfigView(null);
                    }

                }else{
                    initConfigView(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean verifyInput(AdapterModel currentModel) {
        if (currentModel instanceof PPPoEModel) {
            PPPoEModel ppPoEModel = (PPPoEModel) currentModel;
            if (StringUtils.isEmpty(ppPoEModel.getUsername())) {
                Toast.makeText(mContext, "请输入账号", Toast.LENGTH_LONG).show();
                return true;
            }
            if (StringUtils.isEmpty(ppPoEModel.getPassword())) {
                Toast.makeText(mContext, "请输入口令", Toast.LENGTH_LONG).show();
                return true;
            }
            String confirmPsw = etConfirmPassword.getText().toString();
            if(!confirmPsw.equals(ppPoEModel.getPassword())){
                Toast.makeText(mContext, "二次口令输入不一致", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        if (currentModel instanceof APNModel) {
            APNModel apnModel = (APNModel) currentModel;
            if (StringUtils.isEmpty(apnModel.getApn())) {
                Toast.makeText(mContext, "请输入APN名称", Toast.LENGTH_LONG).show();
                return true;
            }
            if (StringUtils.isEmpty(apnModel.getUsername())) {
                Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_LONG).show();
                return true;
            }
            if (StringUtils.isEmpty(apnModel.getPassword())) {
                Toast.makeText(mContext, "请输入密码", Toast.LENGTH_LONG).show();
                return true;
            }
            String confirmPsw = etConfirmApnPassword.getText().toString();
            if(!confirmPsw.equals(apnModel.getPassword())){
                Toast.makeText(mContext, "二次密码输入不一致", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        if (currentModel instanceof StaticIPModel) {
            StaticIPModel staticIPModel = (StaticIPModel) currentModel;
           String regex= "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
            if (!Pattern.matches(regex,staticIPModel.getIp())) {
                Toast.makeText(mContext, "IP输入不合法！", Toast.LENGTH_LONG).show();
                return true;
            }
            if (!Pattern.matches(regex,staticIPModel.getGateway())) {
                Toast.makeText(mContext, "网关输入不合法！", Toast.LENGTH_LONG).show();
                return true;
            }
            if (!Pattern.matches(regex,staticIPModel.getMask())) {
                Toast.makeText(mContext, "子网掩码输入不合法！", Toast.LENGTH_LONG).show();
                return true;
            }
            if (!Pattern.matches(regex,staticIPModel.getDns1())) {
                Toast.makeText(mContext, "DNS1输入不合法！", Toast.LENGTH_LONG).show();
                return true;
            }
            if (!Pattern.matches(regex,staticIPModel.getDns2())) {
                Toast.makeText(mContext, "DNS2输入不合法！", Toast.LENGTH_LONG).show();
                return true;
            }


        }
        return false;
    }

    private void initNoConfig() {
        llStaticIpConfig.setVisibility(View.GONE);
        llAnpConfig.setVisibility(View.GONE);
        llPPPoEConfig.setVisibility(View.GONE);
        apnModel = null;
        ppPoEModel = null;
        staticIPModel = null;
        noConfigModel = new AdapterModel();
    }

    private void initAPN() {
        llStaticIpConfig.setVisibility(View.GONE);
        llAnpConfig.setVisibility(View.VISIBLE);
        llPPPoEConfig.setVisibility(View.GONE);
        apnModel = new APNModel();
        ppPoEModel = null;
        staticIPModel = null;
        noConfigModel = null;
    }

    private void initPPPOE() {
        llStaticIpConfig.setVisibility(View.GONE);
        llAnpConfig.setVisibility(View.GONE);
        llPPPoEConfig.setVisibility(View.VISIBLE);
        ppPoEModel = new PPPoEModel();
        apnModel = null;
        staticIPModel = null;
        noConfigModel = null;
    }

    private void initStaticIP() {
        llStaticIpConfig.setVisibility(View.VISIBLE);
        llAnpConfig.setVisibility(View.GONE);
        llPPPoEConfig.setVisibility(View.GONE);
        staticIPModel = new StaticIPModel();
        apnModel = null;
        ppPoEModel = null;
        noConfigModel = null;
    }

    private void doNetworkAdapterConfig(AdapterModel currentModel) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        if (currentAdapterInfoModel!=null) {
            //修改
            jsonObject.put("operation", "1");
        } else {
            //添加
            jsonObject.put("operation", "0");
        }
        if (currentModel != null) {
            String json = GsonUtils.toJson(currentModel);
            jsonObject.put("adapterInfo", new JSONObject(json));
        }
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.NetworkAdapterConfig, params, new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if (!isVisible()) {
                    return;
                }
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    BaseModel baseModel = GsonUtils.fromJson(result, BaseModel.class);
                    if (baseModel != null) {
                        String code = baseModel.getCode();
                        if ("0".equals(code)) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
            }
        });
    }

    private AdapterModel getCurrentModel() {
            if (apnModel != null) {
                setCurrentModelCommon(apnModel);
                apnModel.setApn(etApn.getText().toString());
                apnModel.setUsername(etApnUsername.getText().toString());
                apnModel.setPassword(etApnPassword.getText().toString());
                return apnModel;
            }
            if (staticIPModel != null) {
                setCurrentModelCommon(staticIPModel);
                staticIPModel.setIp(etSourIp.getText().toString());
                staticIPModel.setGateway(etGateway.getText().toString());
                staticIPModel.setMask(etMask.getText().toString());
                staticIPModel.setDns1(etDns1.getText().toString());
                staticIPModel.setDns2(etDns2.getText().toString());
                return staticIPModel;
            }
            if (ppPoEModel != null) {
                setCurrentModelCommon(ppPoEModel);
                ppPoEModel.setUsername(etUsername.getText().toString());
                ppPoEModel.setPassword(etPassword.getText().toString());
                return ppPoEModel;
            }
            if (noConfigModel != null) {
                setCurrentModelCommon(noConfigModel);
                return noConfigModel;
            }
        return null;
    }

    private void setCurrentModelCommon(AdapterModel adapterModel) {
        adapterModel.setOId(currentAdapterInfoModel != null?currentAdapterInfoModel.getOId():"");
        adapterModel.setAdapterName(etNetAdapterName.getText().toString());
        adapterModel.setProtocol((spProtocolMode.getSelectedItemPosition() + 1) + "");
        adapterModel.setType(interfaceList.get(spInterfaceMode.getSelectedItemPosition()).getType());
        adapterModel.setInterfaceX((String) spInterfaceMode.getSelectedItem());
    }

    private void doInterfaceList() throws JSONException, UnsupportedEncodingException {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.InterfaceList,new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if (!isVisible()) {
                    return;
                }
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    InterfaceModel interfaceModel = GsonUtils.fromJson(result, InterfaceModel.class);
                    if (interfaceModel != null) {
                        String code = interfaceModel.getCode();
                        if ("0".equals(code)) {
                            interfaceList = interfaceModel.getInterfaceList();
                            if (interfaceList != null && interfaceList.size() > 0) {
                                interfaces.clear();
                                initDefaultInterface();
                                initUnOccupyInterfaces();
                                interfaceAdapter.notifyDataSetChanged();
                                initInterfaceSelect();
                                initProtocolSelect();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initUnOccupyInterfaces() {
        for (InterfaceModel.InterfaceListBean bean : interfaceList) {
            interfaces.add(bean.getInterfaceX());
        }
    }

    private void initDefaultInterface() {
        InterfaceModel.InterfaceListBean currentInterface=new InterfaceModel.InterfaceListBean();
        if(currentAdapterInfoModel!=null){
            currentInterface.setInterfaceX(currentAdapterInfoModel.getInterfaceX());
            currentInterface.setType(currentAdapterInfoModel.getType());
            interfaceList.add(0,currentInterface);
        }
    }

    private void initConfigView(AdapterInfoModel.AdapterInfoBean interfaceListBean) {
            if (llAnpConfig.getVisibility() == View.VISIBLE) {
                etApn.setText(interfaceListBean==null?"":interfaceListBean.getApn());
                etApnUsername.setText(interfaceListBean==null?"":interfaceListBean.getUsername());
                etApnPassword.setText(interfaceListBean==null?"":interfaceListBean.getPassword());
                etConfirmApnPassword.setText(interfaceListBean==null?"":interfaceListBean.getPassword());
            }
            if (llPPPoEConfig.getVisibility() == View.VISIBLE) {
                etUsername.setText(interfaceListBean==null?"":interfaceListBean.getUsername());
                etPassword.setText(interfaceListBean==null?"":interfaceListBean.getPassword());
                etConfirmPassword.setText(interfaceListBean==null?"":interfaceListBean.getPassword());
            }
            if (llStaticIpConfig.getVisibility() == View.VISIBLE) {
                etSourIp.setText(interfaceListBean==null?"":interfaceListBean.getIp());
                etGateway.setText(interfaceListBean==null?"":interfaceListBean.getGateway());
                etMask.setText(interfaceListBean==null?"":interfaceListBean.getMask());
                etDns1.setText(interfaceListBean==null?"":interfaceListBean.getDns1());
                etDns2.setText(interfaceListBean==null?"":interfaceListBean.getDns2());
            }
    }

    private void initProtocolSelect() {
        if (currentAdapterInfoModel != null) {
            String protocol = currentAdapterInfoModel.getProtocol();
            String protocolName= TransformUtils.getProtocol(protocol,mContext);
            for (int i = 0; i < currentProtocolList.size(); i++) {
                if (protocolName.equals(currentProtocolList.get(i))) {
                    spProtocolMode.setSelection(i);
                    break;
                }
            }
        }
    }

    private void initInterfaceSelect() {
        if (currentAdapterInfoModel != null) {
            String interfaceX = currentAdapterInfoModel.getInterfaceX();
            if (!StringUtils.isEmpty(interfaceX)) {
                for (int i = 0; i < interfaces.size(); i++) {
                    if (interfaceX.equals(interfaces.get(i))) {
                        spInterfaceMode.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void initData() {
        interfaces = new ArrayList<>();
        interfaceAdapter = new SpinnerAdapter(mContext, android.R.layout.simple_spinner_item, interfaces);
        spInterfaceMode.setAdapter(interfaceAdapter);
        currentProtocolList = new ArrayList<>();
        protocols = getResources().getStringArray(R.array.net_protocol_mode);
        for (int i = 0; i < protocols.length; i++) {
            currentProtocolList.add(protocols[i]);
        }
        protocolAdapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, currentProtocolList);
        spProtocolMode.setAdapter(protocolAdapter);
    }

    private void filterProtocolByType(String type) {
        if (NET_GAPE.equals(type)) {
            //以太网口
            currentProtocolList.clear();
            for (int i = 0; i < protocols.length; i++) {
                currentProtocolList.add(protocols[i]);
            }
            protocolAdapter.notifyDataSetChanged();
        } else if (MOBILE_NETWORK.equals(type)) {
            //移动电话网络
            currentProtocolList.clear();
            currentProtocolList.add(protocols[0]);
            protocolAdapter.notifyDataSetChanged();
        }
    }

    public void onPersonFinish(View view) {
        finish();
    }
}
