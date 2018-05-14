package com.my51c.see51.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewDebug.ExportedProperty;
import android.widget.Checkable;
import android.widget.CompoundButton;

import com.synertone.netAssistant.R;

public class NewSwitch extends CompoundButton implements Checkable {

    private boolean isChecked = false;
    private boolean noTouch = false; //bm-add-1101

    public NewSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public NewSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewSwitch(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //bm-modi-1101
        if (!noTouch) {
            setChecked(!isChecked);
        }
        return super.onTouchEvent(ev);
    }

    public void toggle() {
        isChecked = !isChecked;
    }

    @Override
    @ExportedProperty
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub
        isChecked = checked;
        if (checked) {
            this.setText(R.string.newswitch_on);
            this.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            this.setBackgroundResource(R.drawable.phone_my_setting_switch_selected_new);
        } else {
            this.setText(R.string.newswitch_off);
            this.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            this.setBackgroundResource(R.drawable.phone_my_setting_switch_new);
        }
        super.setChecked(checked);
    }

    //bm-add-1101 begain
    public void toggleUI() {
        isChecked = !isChecked;
        if (isChecked) {
            this.setText(R.string.newswitch_on);
            this.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            this.setBackgroundResource(R.drawable.phone_my_setting_switch_selected_new);
        } else {
            this.setText(R.string.newswitch_off);
            this.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            this.setBackgroundResource(R.drawable.phone_my_setting_switch_new);
        }
    }

    public void setNoTouch(boolean noTouch) {
        this.noTouch = noTouch;
    }

    //bm-add-1101 end

}
