package com.my51c.see51.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synertone.netAssistant.R;

@SuppressLint("ResourceAsColor")
public class ToastCommom {


    private Toast toast;

    /**
     * ��ʾToast
     *
     * @param context
     * @param root
     * @param tvString
     */

    public void ToastShow(Context context, String tvString, int time) {
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView text = (TextView) layout.findViewById(R.id.toast_tx);
        ImageView mImageView = (ImageView) layout.findViewById(R.id.toast_img);
        mImageView.setBackgroundResource(R.drawable.toast_img);
        text.setText(tvString);
        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.setDuration(time);
        toast.setView(layout);
        toast.show();
    }

} 