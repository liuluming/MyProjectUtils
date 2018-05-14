package com.my51c.see51.guide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;

public class GuidePrepareActivity extends SherlockFragmentActivity implements OnCompletionListener, OnClickListener {
    private MediaPlayer mediaPlayer;
    private Button mNext;
    private String devId = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_prepare);
        devId = getIntent().getStringExtra("DeviceId");
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.voice_backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        ((AppData) getApplication()).addActivity(new WeakReference<Activity>(this));
        mNext = (Button) findViewById(R.id.btnNext);
        mNext.setOnClickListener(this);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.greed);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(GuidePrepareActivity.this, GuideSmartWifi.class);
        intent.putExtra("DeviceId", devId);
        startActivity(intent);
        finish();
    }
}
