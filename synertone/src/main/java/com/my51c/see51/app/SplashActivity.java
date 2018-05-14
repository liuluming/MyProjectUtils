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
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.bean.AccountModel;
import com.my51c.see51.app.bean.StarCodeModel;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.service.GpsService;
import com.my51c.see51.common.AppData;
import com.my51c.see51.widget.SharedPreferenceManager;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class SplashActivity extends BaseActivity {
    private RequestQueue mRequestQueue;

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = false;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        Intent service = new Intent(this, GpsService.class);
        this.startService(service);
        screenOritention();
        initStarList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                boolean firstLoad = SharedPreferenceManager.getBoolean(mContext, "firstLoad");
                if(firstLoad){
                    SharedPreferenceManager.saveBoolean(mContext, "firstLoad", false);
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                   String mToken = SharedPreferenceManager.getString(mContext, "mToken");
                    if (mToken == null) {
                        goToLoginActivity();
                    } else {
                        queryStatus(mToken);
                    }
                }

            }
        }, 500);

    }
    private void goToLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
    private void queryStatus(final String mToken) {
        String queryStatusUrl = XTHttpUtil.QUERY_STATUS;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionToken", mToken);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    queryStatusUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                String code = jsonObject.getString("code");
                                if ("0".equals(code)) {
                                    goToHomeActivity(mToken);
                                } else if ("-1".equals(code)) {
                                    goToLoginActivity();
                                } else {
                                    goToLoginActivity();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    goToLoginActivity();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 0f));
            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void goToHomeActivity(final String mToken) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AccountModel accountModel = new AccountModel();
                accountModel.setSessionToken(mToken);
                accountModel.setUser(SharedPreferenceManager.getString(mContext, "user"));
                accountModel.setPasswd(SharedPreferenceManager.getString(mContext, "passwd"));
                AppData.accountModel = accountModel;
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);


    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    private void screenOritention() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("info", "landscape");
            ImageView splash_img = (ImageView) findViewById(R.id.spalsh_img);
            splash_img.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap = readBitMap(this, R.drawable.luancher_land);
            Drawable drawable = new BitmapDrawable(bgBitmap);
            splash_img.setBackgroundDrawable(drawable);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("info", "portrait");
            ImageView splash_img = (ImageView) findViewById(R.id.spalsh_img);
            splash_img.setScaleType(ScaleType.FIT_XY);
            Bitmap bgBitmap = readBitMap(this, R.drawable.qidong);
            Drawable drawable = new BitmapDrawable(bgBitmap);
            splash_img.setBackgroundDrawable(drawable);

        }

    }
    private void initStarList() {
        String initData = SharedPreferenceManager.getString(getApplicationContext(), "initData");
        if (initData == null) {
            StarCodeModel s1 = new StarCodeModel();
            s1.setSatename("协同一号");
            s1.setSatelon("119.5");
            s1.setMode("1");
            s1.setFreq("960.00");
            s1.setZfreq("12236");
            s1.setCenterFreq("12369");
            s1.setSignRate("60000");
            s1.setBw("0");
            s1.setType("0");
            s1.save();
            StarCodeModel s2 = new StarCodeModel();
            s2.setSatename("亚洲4号");
            s2.setSatelon("122.1");
            s2.save();
            StarCodeModel s3 = new StarCodeModel();
            s3.setSatename("中星10号");
            s3.setSatelon("110.5");
            s3.save();
            StarCodeModel s4 = new StarCodeModel();
            s4.setSatename("中星11号");
            s4.setSatelon("98.0");
            s4.setMode("0");
            s4.setFreq("960.00");
            s4.setZfreq("12236");
            s4.setBw("0");
            s4.setType("0");
            s4.setCenterFreq("12500");
            s4.setSignRate("43200");
            s4.save();
            StarCodeModel s5 = new StarCodeModel();
            s5.setSatename("中星12号");
            s5.setSatelon("87.5");
            s5.setMode("0");
            s5.setFreq("960.00");
            s5.setZfreq("12236");
            s5.setBw("0");
            s5.setType("0");
            s5.setCenterFreq("12514");
            s5.setSignRate("05000");
            s5.save();
            StarCodeModel s6 = new StarCodeModel();
            s6.setSatename("中星9号");
            s6.setSatelon("92.2");
            s6.save();
            StarCodeModel s7 = new StarCodeModel();
            s7.setSatename("中星20号");
            s7.setSatelon("103.2");
            s7.save();
            StarCodeModel s8 = new StarCodeModel();
            s8.setSatename("中星2A");
            s8.setSatelon("98.3");
            s8.save();
            StarCodeModel s9 = new StarCodeModel();
            s9.setSatename("Asiasat-3S");
            s9.setSatelon("146.0");
            s9.save();
            StarCodeModel s10 = new StarCodeModel();
            s10.setSatename("Asiasat-5");
            s10.setSatelon("100.4");
            s10.save();
            StarCodeModel s11 = new StarCodeModel();
            s11.setSatename("Asiasat-7");
            s11.setSatelon("105.5");
            s11.save();
            StarCodeModel s12 = new StarCodeModel();
            s12.setSatename("Apstar-5");
            s12.setSatelon("138.0");
            s12.save();
            StarCodeModel s13 = new StarCodeModel();
            s13.setSatename("Apstar-6");
            s13.setSatelon("134.0");
            s13.save();
            StarCodeModel s14 = new StarCodeModel();
            s14.setSatename("Apstar-7");
            s14.setSatelon("76.5");
            s14.save();
            StarCodeModel s15 = new StarCodeModel();
            s15.setSatename("INSAT-4A");
            s15.setSatelon("83.0");
            s15.save();
            StarCodeModel s16 = new StarCodeModel();
            s16.setSatename("Gsat-10");
            s16.setSatelon("83.0");
            s16.save();
            StarCodeModel s17 = new StarCodeModel();
            s17.setSatename("Palapa-D");
            s17.setSatelon("112.9");
            s17.save();
            StarCodeModel s18 = new StarCodeModel();
            s18.setSatename("Measat-3");
            s18.setSatelon("91.5");
            s18.save();
            StarCodeModel s19 = new StarCodeModel();
            s19.setSatename("Measat-3A");
            s19.setSatelon("91.5");
            s19.save();
            StarCodeModel s20 = new StarCodeModel();
            s20.setSatename("ST-2");
            s20.setSatelon("88.0");
            s20.save();
            StarCodeModel s21 = new StarCodeModel();
            s21.setSatename("Thaicom-5");
            s21.setSatelon("78.5");
            s21.save();
            StarCodeModel s22 = new StarCodeModel();
            s22.setSatename("VinaSat-2");
            s22.setSatelon("131.8");
            s22.save();
            StarCodeModel s23 = new StarCodeModel();
            s23.setSatename("Gsat-8");
            s23.setSatelon("55.1");
            s23.save();
            StarCodeModel s24 = new StarCodeModel();
            s24.setSatename("JCSAT-1B");
            s24.setSatelon("150.0");
            s24.save();
            StarCodeModel s25 = new StarCodeModel();
            s25.setSatename("Superbird-C2");
            s25.setSatelon("143.9");
            s25.save();
            StarCodeModel s26 = new StarCodeModel();
            s26.setSatename("BSAT-3C");
            s26.setSatelon("110.0");
            s26.save();
            StarCodeModel s27 = new StarCodeModel();
            s27.setSatename("BSAT-3B");
            s27.setSatelon("109.8");
            s27.save();
            StarCodeModel s28 = new StarCodeModel();
            s28.setSatename("BSAT-3A");
            s28.setSatelon("109.8");
            s28.save();
            StarCodeModel s29 = new StarCodeModel();
            s29.setSatename("N-SAT110");
            s29.setSatelon("110.0");
            s29.save();
            StarCodeModel s30 = new StarCodeModel();
            s30.setSatename("JCSAT-13");
            s30.setSatelon("124.0");
            s30.save();
            StarCodeModel s31 = new StarCodeModel();
            s31.setSatename("Koreasat-6");
            s31.setSatelon("116.0");
            s31.save();
            StarCodeModel s32 = new StarCodeModel();
            s32.setSatename("INSAT-4CR(Ku)");
            s32.setSatelon("47.8");
            s32.save();
            StarCodeModel s33 = new StarCodeModel();
            s33.setSatename("OPTUS-D1");
            s33.setSatelon("160.0");
            s33.save();
            StarCodeModel s34 = new StarCodeModel();
            s34.setSatename("OPTUS-D3");
            s34.setSatelon("156.0");
            s34.save();
            StarCodeModel s35 = new StarCodeModel();
            s35.setSatename("OPTUS-C1");
            s35.setSatelon("156.0");
            s35.save();
            StarCodeModel s36 = new StarCodeModel();
            s36.setSatename("OPTUS-D2");
            s36.setSatelon("152.0");
            s36.save();
            StarCodeModel s37 = new StarCodeModel();
            s37.setSatename("TURKSAT-3A");
            s37.setSatelon("42.0");
            s37.save();
            StarCodeModel s38 = new StarCodeModel();
            s38.setSatename("Kazsat-2");
            s38.setSatelon("86.5");
            s38.save();
            StarCodeModel s39 = new StarCodeModel();
            s39.setSatename("Express-AM22");
            s39.setSatelon("80.1");
            s39.save();
            StarCodeModel s40 = new StarCodeModel();
            s40.setSatename("Yamal-402");
            s40.setSatelon("54.9");
            s40.save();
            StarCodeModel s41 = new StarCodeModel();
            s41.setSatename("Intelsat-12");
            s41.setSatelon("45.0");
            s41.save();
            StarCodeModel s42 = new StarCodeModel();
            s42.setSatename("Intelsat-15");
            s42.setSatelon("85.2");
            s42.save();
            StarCodeModel s43 = new StarCodeModel();
            s43.setSatename("Astra-1F");
            s43.setSatelon("44.3");
            s43.save();
            StarCodeModel s44 = new StarCodeModel();
            s44.setSatename("NSS-11");
            s44.setSatelon("108.2");
            s44.save();
            SharedPreferenceManager.saveString(this, "initData", "done");

        }
    }

}
