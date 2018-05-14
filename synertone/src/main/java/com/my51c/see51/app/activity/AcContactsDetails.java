package com.my51c.see51.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

public class AcContactsDetails extends BaseActivity {
    TextView tv_phone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_contacts_details);
        TextView tv_name = ((TextView) findViewById(R.id.tv_name));
        tv_phone = ((TextView) findViewById(R.id.tv_phone));
        ImageButton putong_phone = ((ImageButton) findViewById(R.id.putong_call));
        ImageButton wangluo_phone = ((ImageButton) findViewById(R.id.wangluo_call));
        ImageButton finish_more = ((ImageButton) findViewById(R.id.more_finish));
        tv_name.setText(getIntent().getStringExtra("infodate"));
        tv_name.setText(getIntent().getStringExtra("infodate"));
        putong_phone.setOnClickListener(new MyClick());
        finish_more.setOnClickListener(new MyClick());
    }

    public void call() {
        /**
         * 打电话需要获取系统权限，需要到AndroidManifest.xml里面配置权限 <uses-permission
         * android:name="android.permission.CALL_PHONE"/>
         */
        Intent intent = new Intent();
        // 设置意图要做的事，这里是打电话
        intent.setAction(Intent.ACTION_CALL);
        // 设置参数 Uri请求资源表示符
        intent.setData(Uri.parse("tel:" + tv_phone.getText()));
        startActivity(intent);
    }

    public class MyClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.putong_call:
                    call();
                    break;
                // case R.id.wangluo_call:
                // break;
                case R.id.more_finish:
                    finish();
                    break;

            }

        }
    }

}
