package com.my51c.see51.app.activity.aggregation;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.NetAdapterModel;
import com.my51c.see51.app.bean.NetAdapterStateModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.TransformUtils;
import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import butterknife.BindView;

public class NetAdapterStateActivity extends BaseActivity {
    @BindView(R.id.more_finish)
    ImageButton moreFinish;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_net_adapter_name)
    TextView tvNetAdapterName;
    @BindView(R.id.tv_link_state)
    TextView tvLinkState;
    @BindView(R.id.tv_ip_address)
    TextView tvIpAddress;
    @BindView(R.id.tv_gateway)
    TextView tvGateway;
    @BindView(R.id.tv_mask)
    TextView tvMask;
    @BindView(R.id.tv_dns1)
    TextView tvDns1;
    @BindView(R.id.tv_dns2)
    TextView tvDns2;
    @BindView(R.id.tv_mac_address)
    TextView tvMacAddress;
    @BindView(R.id.tv_receive_byte)
    TextView tvReceiveByte;
    @BindView(R.id.tv_send_byte)
    TextView tvSendByte;
    @BindView(R.id.tv_sim_state)
    TextView tvSimState;
    @BindView(R.id.tv_signal_trad)
    TextView tvSignalTrad;
    @BindView(R.id.tv_mobile_operator)
    TextView tvMobileOperator;
    @BindView(R.id.tv_mobile_standard)
    TextView tvMobileStandard;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.ll_sim_content)
    LinearLayout llSimContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_adapter_state);
        NetAdapterModel.AdapterListBean interfaceListBean = getIntent().getParcelableExtra("item");
        if (interfaceListBean != null) {
            tvMiddleTitle.setText(R.string.title_state);
            tvNetAdapterName.setText(interfaceListBean.getAdapterName());
            doNetworkAdapterStatus(interfaceListBean);
        }

    }

    private void doNetworkAdapterStatus(final NetAdapterModel.AdapterListBean interfaceListBean) {
        try {
            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oId", interfaceListBean.getOId());
            params.setBodyEntity(new StringEntity(jsonObject.toString()));
            http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkAdapterStatus, params, new MyRequestCallBack(mContext, true) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if (!isVisible()) {
                        return;
                    }
                    String result = responseInfo.result.toString();
                    if (!StringUtils.isEmpty(result)) {
                        NetAdapterStateModel netAdapterStateModel = GsonUtils.fromJson(result, NetAdapterStateModel.class);
                        if (netAdapterStateModel != null) {
                            String code = netAdapterStateModel.getCode();
                            if ("0".equals(code)) {
                                NetAdapterStateModel.StatusInfoBean statusInfo = netAdapterStateModel.getStatusInfo();
                                if (statusInfo != null) {
                                    initStatusInfo(statusInfo);
                                }
                                if (statusInfo != null) {
                                    initIpInfo(statusInfo);
                                }
                                NetAdapterStateModel.StatusInfoBean.SimInfoBean simInfo = statusInfo.getSimInfo();
                                if (simInfo != null) {
                                    llSimContent.setVisibility(View.VISIBLE);
                                    initSimInfo(simInfo);
                                } else {
                                    llSimContent.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSimInfo(NetAdapterStateModel.StatusInfoBean.SimInfoBean simInfoBean) {
        tvSimState.setText(TransformUtils.getSimState(simInfoBean.getSim(), mContext));
        tvMobileOperator.setText(TransformUtils.getMobileOperator(simInfoBean.getMno(), mContext));
        tvSignalTrad.setText(simInfoBean.getSignal());
        tvMobileStandard.setText(TransformUtils.getMobileStandard(simInfoBean.getStandard(), mContext));
        tvPhoneNumber.setText(simInfoBean.getMsisdn());
    }

    private void initIpInfo(NetAdapterStateModel.StatusInfoBean ipInfoBean) {
        tvIpAddress.setText(ipInfoBean.getIp());
        tvGateway.setText(ipInfoBean.getGateway());
        tvMask.setText(ipInfoBean.getMask());
        tvDns1.setText(ipInfoBean.getDns1());
        tvDns2.setText(ipInfoBean.getDns2());
        tvMacAddress.setText(ipInfoBean.getMac());
    }

    private void initStatusInfo(NetAdapterStateModel.StatusInfoBean statusInfoBean) {
        tvLinkState.setText(TransformUtils.getConnectionState(statusInfoBean.getLinkStatus(), mContext));
        tvReceiveByte.setText(statusInfoBean.getRecv() + getString(R.string.byte1));
        tvSendByte.setText(statusInfoBean.getTran() + getString(R.string.byte1));
    }

    public void onPersonFinish(View view) {
        finish();
    }
}
