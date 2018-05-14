// Generated code from Butter Knife. Do not modify!
package com.my51c.see51.app.activity.aggregation;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.came.viewbguilib.ButtonBgUi;
import com.synertone.netAssistant.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NetAdapterAmendActivity_ViewBinding implements Unbinder {
  private NetAdapterAmendActivity target;

  @UiThread
  public NetAdapterAmendActivity_ViewBinding(NetAdapterAmendActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NetAdapterAmendActivity_ViewBinding(NetAdapterAmendActivity target, View source) {
    this.target = target;

    target.moreFinish = Utils.findRequiredViewAsType(source, R.id.more_finish, "field 'moreFinish'", ImageButton.class);
    target.tvMiddleTitle = Utils.findRequiredViewAsType(source, R.id.tv_middle_title, "field 'tvMiddleTitle'", TextView.class);
    target.ivRight = Utils.findRequiredViewAsType(source, R.id.iv_right, "field 'ivRight'", ImageView.class);
    target.tvInterfaceMode = Utils.findRequiredViewAsType(source, R.id.tv_interface_mode, "field 'tvInterfaceMode'", TextView.class);
    target.spInterfaceMode = Utils.findRequiredViewAsType(source, R.id.sp_interface_mode, "field 'spInterfaceMode'", Spinner.class);
    target.tvProtocolMode = Utils.findRequiredViewAsType(source, R.id.tv_protocol_mode, "field 'tvProtocolMode'", TextView.class);
    target.spProtocolMode = Utils.findRequiredViewAsType(source, R.id.sp_protocol_mode, "field 'spProtocolMode'", Spinner.class);
    target.tvSourIp = Utils.findRequiredViewAsType(source, R.id.tv_sour_ip, "field 'tvSourIp'", TextView.class);
    target.etSourIp = Utils.findRequiredViewAsType(source, R.id.et_sour_ip, "field 'etSourIp'", EditText.class);
    target.tvGateway = Utils.findRequiredViewAsType(source, R.id.tv_gateway, "field 'tvGateway'", TextView.class);
    target.etGateway = Utils.findRequiredViewAsType(source, R.id.et_gateway, "field 'etGateway'", EditText.class);
    target.tvMask = Utils.findRequiredViewAsType(source, R.id.tv_mask, "field 'tvMask'", TextView.class);
    target.etMask = Utils.findRequiredViewAsType(source, R.id.et_mask, "field 'etMask'", EditText.class);
    target.tvDns1 = Utils.findRequiredViewAsType(source, R.id.tv_dns1, "field 'tvDns1'", TextView.class);
    target.etDns1 = Utils.findRequiredViewAsType(source, R.id.et_dns1, "field 'etDns1'", EditText.class);
    target.tvDns2 = Utils.findRequiredViewAsType(source, R.id.tv_dns2, "field 'tvDns2'", TextView.class);
    target.etDns2 = Utils.findRequiredViewAsType(source, R.id.et_dns2, "field 'etDns2'", EditText.class);
    target.llStaticIpConfig = Utils.findRequiredViewAsType(source, R.id.ll_static_ip_config, "field 'llStaticIpConfig'", LinearLayout.class);
    target.tvUsername = Utils.findRequiredViewAsType(source, R.id.tv_username, "field 'tvUsername'", TextView.class);
    target.etUsername = Utils.findRequiredViewAsType(source, R.id.et_username, "field 'etUsername'", EditText.class);
    target.tvPassword = Utils.findRequiredViewAsType(source, R.id.tv_password, "field 'tvPassword'", TextView.class);
    target.etPassword = Utils.findRequiredViewAsType(source, R.id.et_password, "field 'etPassword'", EditText.class);
    target.llPPPoEConfig = Utils.findRequiredViewAsType(source, R.id.ll_PPPoE_config, "field 'llPPPoEConfig'", LinearLayout.class);
    target.tvApn = Utils.findRequiredViewAsType(source, R.id.tv_apn, "field 'tvApn'", TextView.class);
    target.etApn = Utils.findRequiredViewAsType(source, R.id.et_apn, "field 'etApn'", EditText.class);
    target.tvApnUsername = Utils.findRequiredViewAsType(source, R.id.tv_apn_username, "field 'tvApnUsername'", TextView.class);
    target.etApnUsername = Utils.findRequiredViewAsType(source, R.id.et_apn_username, "field 'etApnUsername'", EditText.class);
    target.tvAnpPassword = Utils.findRequiredViewAsType(source, R.id.tv_anp_password, "field 'tvAnpPassword'", TextView.class);
    target.etApnPassword = Utils.findRequiredViewAsType(source, R.id.et_apn_password, "field 'etApnPassword'", EditText.class);
    target.llAnpConfig = Utils.findRequiredViewAsType(source, R.id.ll_anp_config, "field 'llAnpConfig'", LinearLayout.class);
    target.btSave = Utils.findRequiredViewAsType(source, R.id.bt_save, "field 'btSave'", ButtonBgUi.class);
    target.tvNetAdapterName = Utils.findRequiredViewAsType(source, R.id.tv_net_adapter_name, "field 'tvNetAdapterName'", TextView.class);
    target.etNetAdapterName = Utils.findRequiredViewAsType(source, R.id.et_net_adapter_name, "field 'etNetAdapterName'", EditText.class);
    target.etConfirmPassword = Utils.findRequiredViewAsType(source, R.id.et_confirm_password, "field 'etConfirmPassword'", EditText.class);
    target.etConfirmApnPassword = Utils.findRequiredViewAsType(source, R.id.et_confirm_apn_password, "field 'etConfirmApnPassword'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NetAdapterAmendActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.moreFinish = null;
    target.tvMiddleTitle = null;
    target.ivRight = null;
    target.tvInterfaceMode = null;
    target.spInterfaceMode = null;
    target.tvProtocolMode = null;
    target.spProtocolMode = null;
    target.tvSourIp = null;
    target.etSourIp = null;
    target.tvGateway = null;
    target.etGateway = null;
    target.tvMask = null;
    target.etMask = null;
    target.tvDns1 = null;
    target.etDns1 = null;
    target.tvDns2 = null;
    target.etDns2 = null;
    target.llStaticIpConfig = null;
    target.tvUsername = null;
    target.etUsername = null;
    target.tvPassword = null;
    target.etPassword = null;
    target.llPPPoEConfig = null;
    target.tvApn = null;
    target.etApn = null;
    target.tvApnUsername = null;
    target.etApnUsername = null;
    target.tvAnpPassword = null;
    target.etApnPassword = null;
    target.llAnpConfig = null;
    target.btSave = null;
    target.tvNetAdapterName = null;
    target.etNetAdapterName = null;
    target.etConfirmPassword = null;
    target.etConfirmApnPassword = null;
  }
}
