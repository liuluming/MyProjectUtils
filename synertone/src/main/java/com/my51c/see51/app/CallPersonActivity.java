package com.my51c.see51.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.PagerAdapter;
import com.synertone.netAssistant.R;

public class CallPersonActivity extends BaseActivity {
    private Button btn_tian, btn_zou;
    private ImageButton btn_back;
    private ViewPager pager;
    View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_btn_1:
                    setCurrentPage(0);
                    break;
                case R.id.main_btn_2:
                    setCurrentPage(1);
                    break;
                case R.id.tonghuajilu_btn_finish:
                    finish();
                    break;
                default:
                    break;
            }
        }

    };
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_person);
        initView();
        initDate();
    }

    private void initDate() {
        adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                setCurrentPage(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        setCurrentPage(0);
    }

    private void setCurrentPage(int arg0) {
        switch (arg0) {
            case 0:
                btn_tian.setBackgroundColor(getResources()
                        .getColor(R.color.pressed));
                btn_zou.setBackgroundColor(getResources().getColor(
                        R.color.noPressed));
                pager.setCurrentItem(0);
                break;
            case 1:
                btn_tian.setBackgroundColor(getResources().getColor(
                        R.color.noPressed));
                btn_zou.setBackgroundColor(getResources().getColor(R.color.pressed));
                pager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private void initView() {
        btn_tian = (Button) findViewById(R.id.main_btn_1);
        btn_zou = (Button) findViewById(R.id.main_btn_2);
        btn_back = (ImageButton) findViewById(R.id.tonghuajilu_btn_finish);
        pager = (ViewPager) findViewById(R.id.vp_content);
        btn_tian.setOnClickListener(click);
        btn_zou.setOnClickListener(click);
        btn_back.setOnClickListener(click);
    }
}
