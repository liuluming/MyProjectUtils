package com.my51c.see51.app.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import org.json.JSONObject;

//加载数据的 ProgressDialog
public class ProgressDialogUtil {

    private ProgressDialog pd;
    private boolean progresshow;
    private Context context;

    public ProgressDialogUtil(ProgressDialog pd,
                              Context context) {
        super();
        this.pd = pd;
        this.progresshow = progresshow;
        this.context = context;
    }

    public void showDia() {
        pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //progresshow = false;
            }
        });
        pd.setMessage("正在加载数据。。。。。");
        pd.show();
    }

    // 有数据的时候ProgressDialog消失
    public void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }
}
