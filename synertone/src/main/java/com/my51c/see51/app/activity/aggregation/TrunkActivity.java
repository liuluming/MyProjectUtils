package com.my51c.see51.app.activity.aggregation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.ChannelModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

/**
 * Created by snt1206 on 2018/1/10.
 */
public class TrunkActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout mTruckNet,mTrunkChannel,mTrunkLink,mTrunkService;
    private Intent mIntent;
    private Button app_configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trunk_activity);
        initView();
    }

    private void initView() {
        mTruckNet=(LinearLayout) findViewById(R.id.ll_trunk_net);
        mTrunkChannel=(LinearLayout) findViewById(R.id.ll_trunk_channel);
        mTrunkLink=(LinearLayout) findViewById(R.id.ll_trunk_link);
        mTrunkService=(LinearLayout) findViewById(R.id.ll_trunk_service);
        app_configuration=(Button) findViewById(R.id.app_configuration);
        mTruckNet.setOnClickListener(this);
        mTrunkChannel.setOnClickListener(this);
        mTrunkLink.setOnClickListener(this);
        mTrunkService.setOnClickListener(this);
        app_configuration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_trunk_net:
                Intent intent=new Intent(mContext,NetAdapterSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_trunk_channel:
                mIntent = new Intent(TrunkActivity.this, TrunkChannelActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ll_trunk_link:
                Intent intent1=new Intent(mContext,AggregatedLinksSettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_trunk_service:
                mIntent = new Intent(TrunkActivity.this, TrunkServiceActivity.class);
                startActivity(mIntent);
                break;
            case R.id.app_configuration:
                appConfig();
                break;

        }
    }
    /**
     * 应用配置
     */
    private void appConfig() {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.AppConfig, new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    ChannelModel configChannelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                    if (configChannelModel != null) {
                        if ("0".equals(configChannelModel.getCode())) {
                            Toast.makeText(mContext, R.string.app_config_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, R.string.oprator_fail,Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    public void refTrunkOnFinish(View v) {
        finish();
    }
}
