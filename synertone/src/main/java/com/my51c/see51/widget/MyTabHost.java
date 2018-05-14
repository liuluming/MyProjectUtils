package com.my51c.see51.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

import java.util.Map;

public class MyTabHost extends TabHost {

    public MyTabHost(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTabHost(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        try {
            super.dispatchWindowFocusChanged(hasFocus);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
