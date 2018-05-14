package com.my51c.see51.app.service;

import com.my51c.see51.app.http.XTHttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

import io.vov.vitamio.utils.Log;


public class Work extends TimerTask {

    //通过路径获取到字节数组的方法
    public static byte[] getBytes(String path) {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            if (200 == connection.getResponseCode()) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                byte array[] = baos.toByteArray();
                Log.i("================", array.toString());
                return array;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        getBytes(XTHttpUtil.GET_XT);
    }

}
