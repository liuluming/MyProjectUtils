package com.my51c.see51.app.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.my51c.see51.app.view.CustomDialog;

public class ToastShow {

    public static void showCustomDialog(String warmInfo, final Context context) {
        CustomDialog.Builder customBuilder = new
                CustomDialog.Builder(context);
        customBuilder.setMessage(warmInfo);
        final CustomDialog customDialog = customBuilder.create();
        customDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try{
                    Activity ac=(Activity)context;
                    if(!ac.isFinishing()){
                        customDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }, 3500);
    }

} 
