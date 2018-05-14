// Generated code from Butter Knife. Do not modify!
package com.my51c.see51.app.activity.aggregation;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.synertone.netAssistant.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NetAdapterStateActivity_ViewBinding implements Unbinder {
  private NetAdapterStateActivity target;

  @UiThread
  public NetAdapterStateActivity_ViewBinding(NetAdapterStateActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NetAdapterStateActivity_ViewBinding(NetAdapterStateActivity target, View source) {
    this.target = target;

    target.moreFinish = Utils.findRequiredViewAsType(source, R.id.more_finish, "field 'moreFinish'", ImageButton.class);
    target.tvMiddleTitle = Utils.findRequiredViewAsType(source, R.id.tv_middle_title, "field 'tvMiddleTitle'", TextView.class);
    target.ivRight = Utils.findRequiredViewAsType(source, R.id.iv_right, "field 'ivRight'", ImageView.class);
    target.tvNetAdapterName = Utils.findRequiredViewAsType(source, R.id.tv_net_adapter_name, "field 'tvNetAdapterName'", TextView.class);
    target.tvLinkState = Utils.findRequiredViewAsType(source, R.id.tv_link_state, "field 'tvLinkState'", TextView.class);
    target.tvIpAddress = Utils.findRequiredViewAsType(source, R.id.tv_ip_address, "field 'tvIpAddress'", TextView.class);
    target.tvGateway = Utils.findRequiredViewAsType(source, R.id.tv_gateway, "field 'tvGateway'", TextView.class);
    target.tvMask = Utils.findRequiredViewAsType(source, R.id.tv_mask, "field 'tvMask'", TextView.class);
    target.tvDns1 = Utils.findRequiredViewAsType(source, R.id.tv_dns1, "field 'tvDns1'", TextView.class);
    target.tvDns2 = Utils.findRequiredViewAsType(source, R.id.tv_dns2, "field 'tvDns2'", TextView.class);
    target.tvMacAddress = Utils.findRequiredViewAsType(source, R.id.tv_mac_address, "field 'tvMacAddress'", TextView.class);
    target.tvReceiveByte = Utils.findRequiredViewAsType(source, R.id.tv_receive_byte, "field 'tvReceiveByte'", TextView.class);
    target.tvSendByte = Utils.findRequiredViewAsType(source, R.id.tv_send_byte, "field 'tvSendByte'", TextView.class);
    target.tvSimState = Utils.findRequiredViewAsType(source, R.id.tv_sim_state, "field 'tvSimState'", TextView.class);
    target.tvSignalTrad = Utils.findRequiredViewAsType(source, R.id.tv_signal_trad, "field 'tvSignalTrad'", TextView.class);
    target.tvMobileOperator = Utils.findRequiredViewAsType(source, R.id.tv_mobile_operator, "field 'tvMobileOperator'", TextView.class);
    target.tvMobileStandard = Utils.findRequiredViewAsType(source, R.id.tv_mobile_standard, "field 'tvMobileStandard'", TextView.class);
    target.tvPhoneNumber = Utils.findRequiredViewAsType(source, R.id.tv_phone_number, "field 'tvPhoneNumber'", TextView.class);
    target.llSimContent = Utils.findRequiredViewAsType(source, R.id.ll_sim_content, "field 'llSimContent'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NetAdapterStateActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.moreFinish = null;
    target.tvMiddleTitle = null;
    target.ivRight = null;
    target.tvNetAdapterName = null;
    target.tvLinkState = null;
    target.tvIpAddress = null;
    target.tvGateway = null;
    target.tvMask = null;
    target.tvDns1 = null;
    target.tvDns2 = null;
    target.tvMacAddress = null;
    target.tvReceiveByte = null;
    target.tvSendByte = null;
    target.tvSimState = null;
    target.tvSignalTrad = null;
    target.tvMobileOperator = null;
    target.tvMobileStandard = null;
    target.tvPhoneNumber = null;
    target.llSimContent = null;
  }
}
