package com.my51c.see51.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synertone.netAssistant.R;

public class DevImgItem extends LinearLayout implements android.content.DialogInterface.OnClickListener {
    private Context mContext;
    private ImageView devImg = null;
    private ImageView statusImg = null;
    private TextView devInfoTx;
    private boolean isDevOnline = false;

    public DevImgItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DevImgItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public DevImgItem(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.dev_img_item, this);
        devImg = (ImageView) findViewById(R.id.devImg);
        statusImg = (ImageView) findViewById(R.id.statusImg);
        devInfoTx = (TextView) findViewById(R.id.devInfoTx);
    }

    public void setDevImg(Object snapImg) {
        if (snapImg instanceof Bitmap) {
            devImg.setImageBitmap((Bitmap) snapImg);
        } else {
            devImg.setImageResource((Integer) snapImg);
        }
    }

    public void setStatusImg(int status) {
        if (status == 2) {
            statusImg.setBackgroundResource(R.drawable.dev_online);
            isDevOnline = true;
        } else {
            statusImg.setBackgroundResource(R.drawable.dev_offline);
            isDevOnline = false;
        }
    }

    public void setDevInfoTx(String tx) {
        devInfoTx.setText(tx);
    }

    public boolean getStatus() {
        return isDevOnline;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

    }


}
