package com.my51c.see51.app.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FontManager {

    public static void changeFonts(View root, Activity act) {

        Typeface tf = Typeface.createFromAsset(act.getAssets(),
                "fonts/gongfang.ttf");

        for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
            View v = ((ViewGroup) root).getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeFonts(v, act);
            }
        }

    }
}  