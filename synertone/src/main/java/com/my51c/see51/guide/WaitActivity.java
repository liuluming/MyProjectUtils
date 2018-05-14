package com.my51c.see51.guide;

import android.content.Intent;
import android.os.Bundle;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

import java.util.Timer;
import java.util.TimerTask;

public class WaitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitactivity);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent(WaitActivity.this, DeviceIdActivity.class);
                intent.putExtra("BindStyle", "addByVoice");
                startActivity(intent);
                finish();
            }
        }, 4500);
    }

}
