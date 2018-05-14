// Generated code from Butter Knife. Do not modify!
package com.my51c.see51.app.activity.aggregation;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.synertone.netAssistant.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NetAdapterSettingActivity_ViewBinding implements Unbinder {
  private NetAdapterSettingActivity target;

  @UiThread
  public NetAdapterSettingActivity_ViewBinding(NetAdapterSettingActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NetAdapterSettingActivity_ViewBinding(NetAdapterSettingActivity target, View source) {
    this.target = target;

    target.moreFinish = Utils.findRequiredViewAsType(source, R.id.more_finish, "field 'moreFinish'", ImageButton.class);
    target.tvMiddleTitle = Utils.findRequiredViewAsType(source, R.id.tv_middle_title, "field 'tvMiddleTitle'", TextView.class);
    target.ivRight = Utils.findRequiredViewAsType(source, R.id.iv_right, "field 'ivRight'", ImageView.class);
    target.plvContent = Utils.findRequiredViewAsType(source, R.id.plv_content, "field 'plvContent'", PullToRefreshListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NetAdapterSettingActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.moreFinish = null;
    target.tvMiddleTitle = null;
    target.ivRight = null;
    target.plvContent = null;
  }
}
