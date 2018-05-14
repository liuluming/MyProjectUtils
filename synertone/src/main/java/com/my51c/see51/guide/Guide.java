package com.my51c.see51.guide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;

public class Guide extends SherlockFragmentActivity {
    private Button btnOldUser, btnNewUser;
    private OnClickListener btnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(Guide.this, Login.class);
            boolean isRigister = false;
            switch (v.getId()) {
                case R.id.btnNewUser:
                    isRigister = true;
                    break;
                case R.id.btnOldUser:
                    isRigister = false;
                    break;
                default:
                    break;
            }
            intent.putExtra("isRigister", isRigister);
            startActivity(intent);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable title_bg = getResources().getDrawable(R.drawable.title_bg);
        actionBar.setBackgroundDrawable(title_bg);

        ((AppData) getApplication()).addActivity(new WeakReference<Activity>(this));
        //setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
        btnOldUser = (Button) findViewById(R.id.btnOldUser);
        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnOldUser.setOnClickListener(btnListener);
        btnNewUser.setOnClickListener(btnListener);
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
}
