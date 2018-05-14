package com.my51c.see51.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synertone.netAssistant.R;

public class ListViewCheckableItem extends LinearLayout implements Checkable {

    private Context mContext;
    private boolean mChecked;
    private ImageView mPreView = null;
    private ImageView mSelectView = null;
    private TextView nameTx, timeTx, sizeTx;
    private ImageView videoLogo = null;

    public ListViewCheckableItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.local_video_item, this);
        mPreView = (ImageView) findViewById(R.id.local_filepreview);
        mSelectView = (ImageView) findViewById(R.id.local_choicce);
        nameTx = (TextView) findViewById(R.id.local_filename);
        sizeTx = (TextView) findViewById(R.id.local_filesize);
        timeTx = (TextView) findViewById(R.id.local_filetime);
        videoLogo = (ImageView) findViewById(R.id.local_video_logo);

    }

    public ListViewCheckableItem(Context context, AttributeSet attrs) {
//		super(context, attrs);
        this(context, attrs, 0);
    }

    public ListViewCheckableItem(Context context) {
//		super(context);
        this(context, null, 0);
    }

    @Override
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub
        mChecked = checked;
        mSelectView.setBackgroundResource(checked ? R.drawable.icon_choice : R.drawable.listview_unchecked);
//		mSelectView.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @Override
    public void toggle() {
        // TODO Auto-generated method stub
        setChecked(!mChecked);
    }

    public void setSelectImg(boolean visible) {
        if (visible) {
            mSelectView.setVisibility(View.VISIBLE);
        } else {
            mSelectView.setVisibility(View.GONE);
        }
    }

    public void setPreViewId(int resId) {
        if (mPreView != null) {
            mPreView.setBackgroundResource(resId);
        }
    }

    public void setPreViewBitmap(Bitmap bitmap) {
        if (mPreView != null) {
            mPreView.setImageBitmap(bitmap);
        }
    }

    public void setFileTime(String time) {
        timeTx.setText(time);
    }

    public void setFileSize(String size) {
        sizeTx.setText(size);
    }

    public void setFileName(String name) {
        nameTx.setText(name);
    }

    public void setVideoLogo(boolean isVideo) {
        if (!isVideo) {
            videoLogo.setVisibility(View.GONE);
        }
    }
}
