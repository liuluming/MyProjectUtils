package com.my51c.see51.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceUpdateUI extends Service {

    private Timer timer;
    private TimerTask task;

    @Override
    public void onCreate() {
        super.onCreate();

        final Intent intent = new Intent();
        intent.setAction("ServiceUpdateUI");

        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                sendBroadcast(intent);
            }
        };
        timer.schedule(task, 0, 1000 * 4);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
