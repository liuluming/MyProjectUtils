package com.my51c.see51.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.my51c.see51.common.TextUtils;
import com.my51c.see51.yzxvoip.AudioConverseActivity;
import com.my51c.see51.yzxvoip.IMMessageActivity;
import com.synertone.netAssistant.R;

public class CallPhonePopupWindow extends PopupWindow implements
        OnClickListener {
    private Context mContext;
    private View view;
    private EditText et_direct_phone;
    private ImageView ib_digit0;
    private ImageView ib_digit1;
    private ImageView ib_digit2;
    private ImageView ib_digit3;
    private ImageView ib_digit4;
    private ImageView ib_digit5;
    private ImageView ib_digit6;
    private ImageView ib_digit7;
    private ImageView ib_digit8;
    private ImageView ib_digit9;
    private ImageView iv_delete;
    private ImageView iv_xinhao;
    private ImageView iv_jinhao;
    private Button ib_direct_call;


    public CallPhonePopupWindow(Context _mContext) {
        this.mContext = _mContext;
        this.view = LayoutInflater.from(mContext).inflate(
                R.layout.call_number_view, null);
        et_direct_phone = (EditText) view.findViewById(R.id.et_direct_phone);
        ib_direct_call = (Button) view.findViewById(R.id.ib_direct_call);
        ib_digit0 = (ImageView) view.findViewById(R.id.digit0);
        ib_digit1 = (ImageView) view.findViewById(R.id.digit1);
        ib_digit2 = (ImageView) view.findViewById(R.id.digit2);
        ib_digit3 = (ImageView) view.findViewById(R.id.digit3);
        ib_digit4 = (ImageView) view.findViewById(R.id.digit4);
        ib_digit5 = (ImageView) view.findViewById(R.id.digit5);
        ib_digit6 = (ImageView) view.findViewById(R.id.digit6);
        ib_digit7 = (ImageView) view.findViewById(R.id.digit7);
        ib_digit8 = (ImageView) view.findViewById(R.id.digit8);
        ib_digit9 = (ImageView) view.findViewById(R.id.digit9);
        iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        // iv_xinhao=(ImageView) view.findViewById(R.id.iv_xinhao);
        iv_jinhao = (ImageView) view.findViewById(R.id.iv_jinhao);
        ib_digit0.setOnClickListener(this); // DTMF ����
        ib_digit1.setOnClickListener(this);
        ib_digit2.setOnClickListener(this);
        ib_digit3.setOnClickListener(this);
        ib_digit4.setOnClickListener(this);
        ib_digit5.setOnClickListener(this);
        ib_digit6.setOnClickListener(this);
        ib_digit7.setOnClickListener(this);
        ib_digit8.setOnClickListener(this);
        ib_digit9.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        // iv_xinhao.setOnClickListener(this);
        iv_jinhao.setOnClickListener(this);
        ib_direct_call.setOnClickListener(this);

        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        iv_delete.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                et_direct_phone.setText("");
                return false;
            }
        });
          /* 设置弹出窗口特征 */
        // 设置视图  
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高  
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击  
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景  
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出  
        //this.setAnimationStyle(R.style.take_photo_anim);  

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_direct_call:    // 拨打电话
                if (IMMessageActivity.checkNetwork(mContext, false) == false) {
                    return;
                }
                if (TextUtils.isEmpty(et_direct_phone.getText().toString())) {
                    Toast.makeText(mContext, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = et_direct_phone.getText().toString();
                    Intent intentVoice = new Intent(mContext, AudioConverseActivity.class);
                    intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intentVoice.putExtra("phoneNumber", phone);
                    intentVoice.putExtra("call_phone", phone);
                    intentVoice.putExtra("call_type", 2);//直拨电话
                    mContext.startActivity(intentVoice);
                    dismiss();
                }
                break;
            case R.id.digit0:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "0");
                break;
            case R.id.digit1:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "1");
                break;
            case R.id.digit2:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "2");
                break;
            case R.id.digit3:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "3");
                break;
            case R.id.digit4:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "4");
                break;
            case R.id.digit5:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "5");
                break;
            case R.id.digit6:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "6");
                break;
            case R.id.digit7:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "7");
                break;
            case R.id.digit8:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "8");
                break;
            case R.id.digit9:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "9");
                break;
            case R.id.iv_delete:
                if (et_direct_phone.getEditableText().toString().length() != 0
                        || !"".equals(et_direct_phone.getEditableText().toString())) {

                    et_direct_phone.setText(et_direct_phone.getEditableText().toString().substring(0,
                            et_direct_phone.getEditableText().toString().length() - 1));
                } else {
                    et_direct_phone.setText("");
                }
                break;
		/*
		 * case R.id.iv_xinhao:
		 * et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "*");
		 * break;
		 */
            case R.id.iv_jinhao:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "#");
                break;
            default:
                break;
        }
    }
}
