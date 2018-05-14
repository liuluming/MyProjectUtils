package com.my51c.see51.app.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.BaseModel;
import com.my51c.see51.app.utils.GsonUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.synertone.netAssistant.R;
import com.yzx.tools.DensityUtil;

/**
 * Created by snt1231 on 2018/1/15.
 */

public abstract  class MyRequestCallBack extends RequestCallBack {
    private  ProgressDialog pd;
    private Context mContext;
    private String msg;

    public MyRequestCallBack(Context mContext, boolean isShow,String...msg) {
        this.mContext=mContext;
        if(msg!=null&&msg.length>0){
            this.msg=msg[0];
        }else{
            this.msg=null;
        }
        if(isShow){
            pd = new ProgressDialog(mContext);
            pd.setCanceledOnTouchOutside(false);
            if(mContext instanceof Activity){
                Activity ac= (Activity) mContext;
                if(!ac.isFinishing()){
                    showDia();
                }
            }
        }

    }
    @Override
    public void onFailure(HttpException e, String s) {
        dismissDia();
        Toast.makeText(mContext,s, Toast.LENGTH_LONG).show();
        //Toast.makeText(mContext, R.string.net_error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(ResponseInfo responseInfo) {
        BaseActivity activity= (BaseActivity) mContext;
        if(!activity.isVisible()){
            return;
        }
        BaseModel baseModel = GsonUtils.fromJson(responseInfo.result.toString(), BaseModel.class);
        if(baseModel!=null){
            String code = baseModel.getCode();
            if(!"0".equals(code)){
                showErrorDialog(activity,baseModel.getMsg());
            }
        }
        dismissDia();
    }

    private void showErrorDialog(BaseActivity activity, final String msg) {
        NiceDialog.init()
                .setLayoutId(R.layout.one_button_confirm_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setText(R.id.title, mContext.getString(R.string.tip));
                        holder.setText(R.id.message, msg);
                        holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setOutCancel(false)
                .setMargin(DensityUtil.dip2px(mContext,10))
                .show(activity.getSupportFragmentManager())
        ;
    }

    protected void showDia() {
        if (msg != null ) {
            pd.setMessage("\"" + msg + "....." + "\"");
        } else {
            pd.setMessage("正在加载数据.....");
        }
        pd.show();
    }
    protected void dismissDia() {
        if (pd != null) {
            pd.dismiss();
        }
    }
}
