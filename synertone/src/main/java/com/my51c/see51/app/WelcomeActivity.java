package com.my51c.see51.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.RequestQueue;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.activity.aggregation.TrunkActivity;
import com.synertone.netAssistant.R;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {
    private ViewPager vp_pics;
    private List<ImageView> mDatas = new ArrayList<ImageView>();
    private List<ImageView> mDatasland = new ArrayList<ImageView>();
    private List<ImageView> mDatasproit = new ArrayList<ImageView>();
    private boolean flags = false;
    private boolean firstLoad;
    private String mToken;
    private RequestQueue mRequestQueue;
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = false;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenOritention();
        initView();
        initData();
        initEvent();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

    }

    private void initData() {
        MyAdapter mAdapter = new MyAdapter();
        vp_pics.setAdapter(mAdapter);
    }

    private boolean screenOritention() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("info", "landscape");
            flags = false;//横屏

            ImageView iv3 = new ImageView(this);
            iv3.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap1 = readBitMap(this, R.drawable.xuandao_land1);
            Drawable drawable1 = new BitmapDrawable(bgBitmap1);
            //iv3.setImageResource(R.drawable.xuandao_land1);
            iv3.setBackground(drawable1);
            mDatasland.add(iv3);

            ImageView iv4 = new ImageView(this);
            iv4.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap2 = readBitMap(this, R.drawable.xuandao_land2);
            Drawable drawable2 = new BitmapDrawable(bgBitmap2);
            //iv3.setImageResource(R.drawable.xuandao_land1);
            iv4.setBackground(drawable2);
            mDatasland.add(iv4);

		/*ImageView iv4=new ImageView(this);
        iv4.setScaleType(ScaleType.FIT_XY);
		iv4.setImageResource(R.drawable.xuandao_land2);
		mDatasland.add(iv4);*/


            mDatas = mDatasland;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("info", "portrait");
            flags = true;//竖屏

            ImageView iv1 = new ImageView(this);
            iv1.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap1 = readBitMap(this, R.drawable.welcome1);
            Drawable drawable1 = new BitmapDrawable(bgBitmap1);
            iv1.setBackground(drawable1);
            mDatasproit.add(iv1);

            ImageView iv2 = new ImageView(this);
            iv2.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap2 = readBitMap(this, R.drawable.xuandao6);
            Drawable drawable2 = new BitmapDrawable(bgBitmap2);
            iv2.setBackground(drawable2);

            mDatasproit.add(iv2);
            mDatas = mDatasproit;
        }
        return flags;
    }

    private void initEvent() {
	/*	bt_exp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent home = new Intent(WelcomeActivity.this, LoginActivity.class);
				startActivity(home);
				finish();
			}
		});*/
        vp_pics.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
				if (position == mDatas.size() - 1) {
                    goToLoginActivity();
				}

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //edit  by hyw 20161219
				/*if (position == mDatas.size() - 1) {
				bt_exp.setVisibility(View.VISIBLE);
			} else {
				bt_exp.setVisibility(View.GONE);
			}*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (rightEdge != null && !rightEdge.isFinished()) {//到了最后一张并且还继续拖动，出现蓝色限制边条了
                    //waite2s();
                }
            }
        });
    }



    private void initView() {
        setContentView(R.layout.activity_welcome);
        vp_pics = (ViewPager) findViewById(R.id.vp_guid_pics);
        //edit by hyw 20161219
        try {
            Field leftEdgeField = vp_pics.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = vp_pics.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(vp_pics);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(vp_pics);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, TrunkActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mDatas.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
