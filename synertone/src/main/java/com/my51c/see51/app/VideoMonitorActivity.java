package com.my51c.see51.app;

import android.os.Bundle;
import android.view.View;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

public class VideoMonitorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_monitor_activity);
    }


    //点击退出视频监控页面
    public void onMonitotFinish(View v) {
        finish();
    }
}
