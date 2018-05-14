package com.my51c.see51.app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.activity.SuperSetActivity;
import com.my51c.see51.app.activity.WarnInfoActivity;
import com.my51c.see51.app.activity.aggregation.TrunkActivity;
import com.my51c.see51.app.service.GpsService;
import com.my51c.see51.app.service.ServiceUpdateUI;
import com.my51c.see51.common.AppData;
import com.my51c.see51.ui.MainActivity;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends BaseActivity {
    String tip = "您已经进入工程模式，该项操作有可能损伤您的设备，请确认是否进入？";
    private Button mHRunBtn, mHVideoMeet, mHMap, mHSet;
    private Button mHVMonitor, mHVoiphone, mHPerson, mHMore;
    private GridView mGrid;
    private ListView mListView;
    private List<ImageView> imageViews;// 滑动的图片集合
    private ViewPager adViewPager;
    private int currentItem = 0; // 当前图片的索引号
    private ScheduledExecutorService scheduledExecutorService;
    private int[] res = {R.drawable.phone_banner_image,
            R.drawable.phone_banner_image, R.drawable.phone_banner_image,
            R.drawable.phone_banner_image};
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adViewPager.setCurrentItem(currentItem);
        }
    };
    private View dot0;
    private View dot1;
    private View dot2;
    private View dot3;
    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;
    private TextView yuyindianhua, shipinghuiyi, shipingjiankong, wangluoshezhi, daikuanguanli, yunxingzhuangtai;
    private TextView dingwei, gerenzhongxin, gaojishezhi, vod, tuozhan1, tuozhan2;
    private Dialog noticeDialog;
    private Context mContext;
    private AlertDialog.Builder builder;
    private TextView tView, tView2;
    private Button mInto;
    private Button mQuxiao;

    private Intent mIntent;
    private TextView mOduAlarm;
    private TextView mTruck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = HomeActivity.this;
        setContentView(R.layout.home_activity);
        Intent serIntent = new Intent(this, ServiceUpdateUI.class);
        startService(serIntent);
        initView();
        initData();
        //startAd();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        try {
            setContentView(R.layout.home_activity);
        } catch (Exception ex) {
        }
    }

    //设置字体的控件
    private void initView() {
        /*yuyindianhua=(TextView) findViewById(R.id.yuyindianhua);
		yuyindianhua.setTypeface(AppData.fontXiti);
		shipinghuiyi=(TextView) findViewById(R.id.shipinghuiyi);
		shipinghuiyi.setTypeface(AppData.fontXiti);*/
        shipingjiankong = (TextView) findViewById(R.id.shipingjiankong);
        shipingjiankong.setTypeface(AppData.fontXiti);
        wangluoshezhi = (TextView) findViewById(R.id.wangluoshezhi);
        wangluoshezhi.setTypeface(AppData.fontXiti);
        //daikuanguanli=(TextView) findViewById(R.id.daikuanguanli);
        //daikuanguanli.setTypeface(AppData.fontXiti);
        yunxingzhuangtai = (TextView) findViewById(R.id.yunxingzhuangtai);
        yunxingzhuangtai.setTypeface(AppData.fontXiti);
        dingwei = (TextView) findViewById(R.id.dingwei);
        dingwei.setTypeface(AppData.fontXiti);
        gerenzhongxin = (TextView) findViewById(R.id.gerenzhongxin);
        gerenzhongxin.setTypeface(AppData.fontXiti);
        gaojishezhi = (TextView) findViewById(R.id.gaojishezhi);
        gaojishezhi.setTypeface(AppData.fontXiti);
        mOduAlarm = (TextView) findViewById(R.id.tv_odu_alarm);
        mOduAlarm.setTypeface(AppData.fontXiti);
        mTruck = (TextView) findViewById(R.id.tv_home_trunk);
        mTruck.setTypeface(AppData.fontXiti);
    }

    private void initData() {
        imageViews = new ArrayList<ImageView>();
        // 点
        dots = new ArrayList<View>();
        dotList = new ArrayList<View>();
        dot0 = findViewById(R.id.home_banner_dot1);
        dot1 = findViewById(R.id.home_banner_dot2);
        dot2 = findViewById(R.id.home_banner_dot3);
        dot3 = findViewById(R.id.home_banner_dot4);

        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        adViewPager = (ViewPager) findViewById(R.id.vp);
        adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        adViewPager.setOnPageChangeListener(new MyPageChangeListener());
        addDynamicView();
    }

    private void addDynamicView() {
        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        for (int i = 0; i < res.length; i++) {
            ImageView imageView = new ImageView(this);

            imageView.setBackgroundResource(res[i]);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            dots.get(i).setVisibility(View.VISIBLE);
            dotList.add(dots.get(i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAd();
    }

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
                TimeUnit.SECONDS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
    }

    /**
     * 点击banner选择前一张或后一张的点击事件 还没有实现
     *
     * @param v
     */
    public void check_click(View v) {
        switch (v.getId()) {
            case R.id.home_banner_before:
                if (currentItem - 1 >= 0) {
                    adViewPager.setCurrentItem(currentItem - 1);
                }
                break;
            case R.id.home_banner_after:
                if (currentItem + 1 <= 4) {
                    adViewPager.setCurrentItem(currentItem + 1);
                }
                break;
            case R.id.home_banner_dot1:
                adViewPager.setCurrentItem(currentItem);
                break;
            case R.id.home_banner_dot2:
                adViewPager.setCurrentItem(currentItem);
                break;
            case R.id.home_banner_dot3:
                adViewPager.setCurrentItem(currentItem);
                break;
            case R.id.home_banner_dot4:
                adViewPager.setCurrentItem(currentItem);
                break;
            default:
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.home_device_status:
                mIntent = new Intent(HomeActivity.this, DeviceStatusActivity.class);
                startActivity(mIntent);
                //	Toast.makeText(HomeActivity.this, "点击了运行状态状态", 0).show();
                break;

		/*case R.id.home_video_meeting:
			mIntent = new Intent(HomeActivity.this, VideoMeet.class);
			startActivity(mIntent);
		//	Toast.makeText(HomeActivity.this, "点击视频会议", 0).show();
			break;*/
            case R.id.home_map_position:
//			//新增定位功能
                mIntent = new Intent(HomeActivity.this, LocationActivity.class);
                //mIntent = new Intent(HomeActivity.this, locationHUO.class);
                startActivity(mIntent);
                //		Toast.makeText(HomeActivity.this, "点击导航地图", 0).show();
                break;

            case R.id.home_network_set:
                //mIntent = new Intent(HomeActivity.this, NetWorkSetActivity.class);
                mIntent = new Intent(HomeActivity.this, NetSetActivity.class);
                startActivity(mIntent);
//			Toast.makeText(HomeActivity.this, "点击网络设置", 0).show();
                break;

            case R.id.home_video_monitoring:
                mIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(mIntent);
//			Toast.makeText(HomeActivity.this, "点击视频监控", 0).show();
                break;
		/*case R.id.home_voip_phone:
			mIntent = new Intent(HomeActivity.this, MKLoginActivity.class);
			startActivity(mIntent);
			mIntent = new Intent(HomeActivity.this, YzxLogin2Activity.class);
			startActivity(mIntent);
			break;*/
            case R.id.home_personal_center:
//			Toast.makeText(HomeActivity.this, "点击了个人中心", 0).show();
                mIntent = new Intent(HomeActivity.this, PersonalActivity.class);
                startActivity(mIntent);
                break;
            case R.id.home_more:
//			Toast.makeText(HomeActivity.this, "高级设置", 0).show();
//			mIntent = new Intent(HomeActivity.this, GaoJiAccountActivity.class);
//			startActivity(mIntent);
                DiaLogTip();
                break;
            case R.id.home_odu_alarm:
                mIntent = new Intent(HomeActivity.this, WarnInfoActivity.class);
                startActivity(mIntent);
                break;
		    case R.id.home_trunk:
                mIntent = new Intent(HomeActivity.this, TrunkActivity.class);
                startActivity(mIntent);
			    break;
        }
    }

    private void DiaLogTip1() {
        builder = new Builder(HomeActivity.this);
        builder.setTitle("重要提示");
        builder.setMessage(tip);
        builder.setPositiveButton("进入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Intent intent = new Intent(HomeActivity.this, SuperSetActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void DiaLogTip() {
        noticeDialog = new Dialog(HomeActivity.this);
        noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.home_tip_layout, null);
        noticeDialog.setContentView(view);
        mInto = (Button) view.findViewById(R.id.jinru);
        mInto.setTypeface(AppData.fontXiti);
        mQuxiao = (Button) view.findViewById(R.id.quxiao);
        mQuxiao.setTypeface(AppData.fontXiti);
        tView = (TextView) view.findViewById(R.id.import_tishiword);
        tView.setTypeface(AppData.fontXiti);
        tView2 = (TextView) view.findViewById(R.id.import_tip);
        tView2.setTypeface(AppData.fontXiti);
        mInto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuperSetActivity.class);
                startActivity(intent);
                noticeDialog.dismiss();
            }
        });
        mQuxiao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
            }
        });
        noticeDialog.show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent service = new Intent(this, GpsService.class);
            stopService(service);
            Intent serIntent = new Intent(this, ServiceUpdateUI.class);
            stopService(serIntent);
        }

        return super.onKeyDown(keyCode, event);
    }

    private class ScrollTask implements Runnable {
        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % res.length;
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    private class MyPageChangeListener implements OnPageChangeListener {
        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;

            dots.get(oldPosition).setBackgroundResource(R.drawable.dot1);
            dots.get(position).setBackgroundResource(R.drawable.dot2);
            oldPosition = position;
        }
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return res.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = imageViews.get(position);
            container.addView(iv);
            // final AdDomain adDomain = adList.get(position);
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理跳转逻辑
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }


}
