package com.my51c.see51.yzxvoip;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

/**
 * @author
 * @Title DirectEntryActivity
 * @Description 直拨入口
 * @Company
 * @date
 */
public class DirectEntryActivity extends BaseActivity implements OnClickListener {
    private static String input_tel;
    private TextView tv_direct_back;
    private EditText et_direct_phone;
    private ImageButton ib_delete_tel_num;
    private ImageButton ib_digit0;
    private ImageButton ib_digit1;
    private ImageButton ib_digit2;
    private ImageButton ib_digit3;
    private ImageButton ib_digit4;
    private ImageButton ib_digit5;
    private ImageButton ib_digit6;
    private ImageButton ib_digit7;
    private ImageButton ib_digit8;
    private ImageButton ib_digit9;
    private ImageButton ib_digit_star;
    private ImageButton ib_digit_husa;
    private ImageButton ib_direct_call;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_entry);
        initView();
        initListener();
        /*// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        try {
            super.onConfigurationChanged(newConfig);

            setContentView(R.layout.activity_direct_entry);
            et_direct_phone.setText(input_tel);
        } catch (Exception ex) {
        }
    }

    /**
     * @return void    返回类型
     * @Description 初始化view
     * @date 2016-5-18 上午11:03:38
     * @author xhb
     */
    private void initView() {
        et_direct_phone = (EditText) findViewById(R.id.et_direct_phone);
        ib_delete_tel_num = (ImageButton) findViewById(R.id.ib_delete_tel_num);
        tv_direct_back = (TextView) findViewById(R.id.tv_direct_back);
        ib_direct_call = (ImageButton) findViewById(R.id.ib_direct_call);
        ib_digit0 = (ImageButton) findViewById(R.id.digit0);
        ib_digit1 = (ImageButton) findViewById(R.id.digit1);
        ib_digit2 = (ImageButton) findViewById(R.id.digit2);
        ib_digit3 = (ImageButton) findViewById(R.id.digit3);
        ib_digit4 = (ImageButton) findViewById(R.id.digit4);
        ib_digit5 = (ImageButton) findViewById(R.id.digit5);
        ib_digit6 = (ImageButton) findViewById(R.id.digit6);
        ib_digit7 = (ImageButton) findViewById(R.id.digit7);
        ib_digit8 = (ImageButton) findViewById(R.id.digit8);
        ib_digit9 = (ImageButton) findViewById(R.id.digit9);
        ib_digit_star = (ImageButton) findViewById(R.id.digit_star);
        ib_digit_husa = (ImageButton) findViewById(R.id.digit_husa);
    }

    /**
     * @return void    返回类型
     * @Description 初始化监听器
     * @date 2016-5-18 上午11:03:52
     * @author xhb
     */
    private void initListener() {
        tv_direct_back.setOnClickListener(this);

        ib_delete_tel_num.setOnClickListener(this);

        ib_direct_call.setOnClickListener(this);
        ib_digit0.setOnClickListener(this);                // DTMF 按键
        ib_digit1.setOnClickListener(this);
        ib_digit2.setOnClickListener(this);
        ib_digit3.setOnClickListener(this);
        ib_digit4.setOnClickListener(this);
        ib_digit5.setOnClickListener(this);
        ib_digit6.setOnClickListener(this);
        ib_digit7.setOnClickListener(this);
        ib_digit8.setOnClickListener(this);
        ib_digit9.setOnClickListener(this);
        ib_digit_star.setOnClickListener(this);
        ib_digit_husa.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_delete_tel_num:
				/*//动作按下
				int action = KeyEvent.ACTION_DOWN;
				//code:删除，其他code也可以，例如 code = 0
				int code = KeyEvent.KEYCODE_DEL;
				KeyEvent event = new KeyEvent(action, code);
				et_direct_phone.onKeyDown(KeyEvent.KEYCODE_DEL, event); //抛给系统处理了*/
                et_direct_phone.setText("");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.tv_direct_back:    // 返回
                finish();
                break;
            case R.id.ib_direct_call:    // 拨打电话
                if (IMMessageActivity.checkNetwork(DirectEntryActivity.this, false) == false) {
                    return;
                }
                if (TextUtils.isEmpty(et_direct_phone.getText().toString())) {
                    Toast.makeText(DirectEntryActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = et_direct_phone.getText().toString();
                    Intent intentVoice = new Intent(DirectEntryActivity.this, AudioConverseActivity.class);
                    intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intentVoice.putExtra("phoneNumber", phone);
                    intentVoice.putExtra("call_phone", phone);
                    intentVoice.putExtra("call_type", 2);//直拨电话
                    startActivity(intentVoice);
                }
                break;
            case R.id.digit0:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "0");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit1:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "1");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit2:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "2");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit3:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "3");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit4:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "4");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit5:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "5");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit6:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "6");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit7:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "7");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit8:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "8");
                input_tel = et_direct_phone.getEditableText().toString();

                break;
            case R.id.digit9:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "9");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit_star:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "*");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            case R.id.digit_husa:
                et_direct_phone.getEditableText().insert(et_direct_phone.getText().length(), "#");
                input_tel = et_direct_phone.getEditableText().toString();
                break;
            default:
                break;
        }
    }

}
  
