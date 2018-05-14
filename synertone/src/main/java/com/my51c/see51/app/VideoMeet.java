package com.my51c.see51.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

public class VideoMeet extends BaseActivity {

    private TextView putongshiping, weixingshiping, yidongshiping, shipinhuiyi;//字体显示文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_meeting_activity);
        initView();
    }

    private void initView() {
        shipinhuiyi = (TextView) findViewById(R.id.shipinhuiyi);
        shipinhuiyi.setTypeface(AppData.fontXiti);
        putongshiping = (TextView) findViewById(R.id.putongshiping);
        putongshiping.setTypeface(AppData.fontXiti);
        weixingshiping = (TextView) findViewById(R.id.weixingshiping);
        weixingshiping.setTypeface(AppData.fontXiti);
        yidongshiping = (TextView) findViewById(R.id.yidongshiping);
        yidongshiping.setTypeface(AppData.fontXiti);
    }

    //点击退出视频会议界面
    public void onMeetFinish(View v) {
        finish();
    }

}
