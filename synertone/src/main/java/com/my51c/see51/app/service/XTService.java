package com.my51c.see51.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.my51c.see51.app.http.XTHttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.vov.vitamio.utils.Log;

public class XTService extends Service {

    // 通过路径获取到字节数组的方法
    public static byte[] getBytes(String path) {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 读者可以修改此处的Minutes从而改变提醒间隔时间
        // 此处是设置每隔90分钟启动一次
        // 这是90分钟的毫秒数
        int Minutes = 6000;
        // SystemClock.elapsedRealtime()表示1970年1月1日0点至今所经历的时间
        long triggerAtTime = SystemClock.elapsedRealtime() + Minutes;
        // 此处设置开启AlarmReceiver这个Service
        Intent i = new Intent(this, AlarmManager.class);
        String by = getBytes(XTHttpUtil.GET_XT).toString();
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        // ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        // 在Service结束后关闭AlarmManager
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        super.onDestroy();
    }

}
