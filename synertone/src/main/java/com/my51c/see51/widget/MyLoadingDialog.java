package com.my51c.see51.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synertone.netAssistant.R;

public class MyLoadingDialog extends Dialog {
    private TextView tv;

    public MyLoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    private MyLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) this.findViewById(R.id.textviewwaitcontent);
        setTextViewVisible(false);
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }

    public String getContent() {
        return tv.getText().toString();
    }

    public void setContent(String str) {
        tv.setText(str);
    }

    public void setTextViewVisible(boolean bshow) {
        if (bshow) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
        }
    }
}

