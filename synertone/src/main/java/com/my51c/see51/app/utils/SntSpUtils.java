package com.my51c.see51.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

public class SntSpUtils {

    //保存并设置
    public static void GenJStoSp(Context context, String file, JSONObject js) {
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        Editor gened = sp.edit();
        try {
            gened.putString("satenum", js.getString("satenum"));
            gened.putString("satelon", js.getString("satelon"));
            gened.putString("mode", js.getString("mode"));
            gened.putString("freq", js.getString("freq"));
            gened.putString("bw", js.getString("bw"));
            gened.putString("type", js.getString("type"));
            gened.putString("modem", js.getString("modem"));
            gened.putString("recvpol", js.getString("recvpol"));
            gened.putString("sendpol", js.getString("sendpol"));
            gened.commit();
            //gened.putString("rssi", js.getString("rssi"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //
    public static void GenSptoJS(Context context, String file, JSONObject js) {
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        try {
            if (file.equals("sate1")) {
                js.put("satenum", sp.getString("satenum", "1"));
            } else if (file.equals("sate2")) {
                js.put("satenum", sp.getString("satenum", "2"));
            } else if (file.equals("sate3")) {
                js.put("satenum", sp.getString("satenum", "3"));
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            js.put("satelon", sp.getString("satelon", "119.5"));
            js.put("mode", sp.getString("mode", "1"));
            js.put("freq", sp.getString("freq", "1715.00"));
            js.put("bw", sp.getString("bw", "4"));
            js.put("type", sp.getString("type", "0"));
            js.put("modem", sp.getString("modem", "Gilat"));
            js.put("recvpol", sp.getString("recvpol", "90"));
            js.put("sendpol", sp.getString("sendpol", "0"));
//			js.put("rssi",sp.getString("rssi", "2500"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void AdvJStoSp(Context context, JSONObject js) {
        SharedPreferences sp = context.getSharedPreferences("sateadv", Context.MODE_PRIVATE);
        Editor adved = sp.edit();
        try {
            adved.putString("rssi", js.getString("rssi"));
            adved.putString("locatype", js.getString("locatype"));
            adved.putString("curlon", js.getString("curlon"));
            adved.putString("currlat", js.getString("currlat"));
            adved.commit();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void AdvSptoJS(Context context, JSONObject js) {
        SharedPreferences sp = context.getSharedPreferences("sateadv", Context.MODE_PRIVATE);
        try {
            js.put("rssi", sp.getString("rssi", "2000"));
            js.put("locatype", sp.getString("locatype", "1"));
            js.put("curlon", sp.getString("curlon", "114.3"));
            js.put("currlat", sp.getString("currlat", "22.5"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
