package com.my51c.see51.app.activity.aggregation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.ChannelInfoModel;
import com.my51c.see51.app.bean.ChannelModel;
import com.my51c.see51.app.bean.NetAdapterModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.SpinnerAdapter;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddConfigChannelActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener{

    private Spinner mChannelMode;
    private EditText et_service_ip,et_service_port,et_authentication_account, et_authentication_cmd,et_channel_name;
    private ToggleButton tog_channel_encryption,tog_channel_compass;
    private String mEncrypt,mCompress,mAdapter;
    private List<String> adapterList=new ArrayList<>();
    private String[] integers;
    private Button bt_channel_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_config_channel);
        initView();
        doNetworkAdapterList(true);
    }
   
    private void initView() {
        String name = getIntent().getStringExtra("name");
        TextView tv_middle_title=(TextView) findViewById(R.id.tv_middle_title);
        ImageView iv_right=(ImageView)findViewById(R.id.iv_right);
        tv_middle_title.setText(name);
        iv_right.setVisibility(View.GONE);
        bt_channel_save=(Button) findViewById(R.id.bt_channel_save);
        bt_channel_save.setOnClickListener(this);
        et_channel_name= (EditText) findViewById(R.id.et_channel_name);
        et_service_ip= (EditText) findViewById(R.id.et_service_ip);
        et_service_port = (EditText) findViewById(R.id.et_service_port);
        et_authentication_account = (EditText) findViewById(R.id.et_authentication_account);
        tog_channel_encryption = (ToggleButton) findViewById(R.id.tog_channel_encryption);
        tog_channel_compass = (ToggleButton) findViewById(R.id.tog_channel_compass);
        et_authentication_cmd  = (EditText) findViewById(R.id.et_authentication_cmd );
        tog_channel_encryption.setOnCheckedChangeListener(this);
        tog_channel_compass.setOnCheckedChangeListener(this);
        mEncrypt = "0";
        mCompress = "0";
    }
    private void initSpinnerData(final List<NetAdapterModel.AdapterListBean> interfaceList) {
        mChannelMode = (Spinner) findViewById(R.id.tv_adapter_mode);
        mChannelMode.setAdapter(new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, integers));
        mChannelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(mChannelMode.getSelectedItem().toString().equals(adapterList.get(position))) {
                    mAdapter = interfaceList.get(position).getOId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    /**
     * 添加网络通道
     */
    private void addNetChannel() {
        try {
            RequestParams params = new RequestParams("UTF-8");
            JSONObject jsonObject = new JSONObject();
            ChannelInfoModel.ChannelInfoBean channelInfoBean = new ChannelInfoModel.ChannelInfoBean();
            channelInfoBean.setChannel(et_channel_name.getText().toString());
            channelInfoBean.setIp(et_service_ip.getText().toString());
            channelInfoBean.setPort(et_service_port.getText().toString());
            channelInfoBean.setUsername(et_authentication_account.getText().toString());
            channelInfoBean.setPassword(et_authentication_cmd.getText().toString());
            channelInfoBean.setEncrypt(mEncrypt);
            channelInfoBean.setCompress(mCompress);
            channelInfoBean.setAdapter(mAdapter);
            jsonObject.put("operation", "0");
            String json= GsonUtils.toJson(channelInfoBean);
            jsonObject.put("channelInfo", new JSONObject(json));
            params.setBodyEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
            params.setContentType("application/json");
            http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.SetChannelConfig, params, new MyRequestCallBack(mContext, true) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                        ChannelModel configChannelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                        if (configChannelModel != null) {
                            if ("0".equals(configChannelModel.getCode())) {
                                setResult(RESULT_OK);
                                finish();
                                Toast.makeText(mContext, R.string.add_net_channel_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, R.string.oprator_fail,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId())  {
            case R.id.tog_channel_encryption:
                if(isChecked) {
                    mEncrypt = "1";
                } else {
                    mEncrypt = "0";
                }
                break;
            case R.id.tog_channel_compass:
                if(isChecked) {
                    mCompress = "1";
                } else {
                    mCompress = "0";
                }
                break;
        }

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
                            for(NetAdapterModel.AdapterListBean channelListBean:interfaceList) {
                                adapterList.add(channelListBean.getAdapterName()) ;
                            }
                            integers = adapterList.toArray(new String[0]);
                            initSpinnerData(interfaceList);
                        } else {
                            Toast.makeText(mContext, R.string.query_network_adapter_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    public void onPersonFinish(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_channel_save:
                if (StringUtils.isEmpty(et_channel_name.getText())) {
                    Toast.makeText(mContext, R.string.et_channel_name, Toast.LENGTH_LONG).show();
                    return;
                }else if (StringUtils.isEmpty(et_service_ip.getText())) {
                    Toast.makeText(mContext, R.string.et_service_ip, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_service_port.getText())) {
                    Toast.makeText(mContext, R.string.et_service_port, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_authentication_account.getText())) {
                    Toast.makeText(mContext, R.string.et_authentication_account, Toast.LENGTH_LONG).show();
                    return;
                }else if(StringUtils.isEmpty(et_authentication_cmd.getText())) {
                    Toast.makeText(mContext, R.string.et_authentication_cmd, Toast.LENGTH_LONG).show();
                    return;
                }
                addNetChannel();
                break;
        }
    }
}