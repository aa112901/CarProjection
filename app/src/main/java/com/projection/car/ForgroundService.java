package com.projection.car;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.accessibility.AccessibilityEvent;

import static com.projection.car.Utils.log;

public class ForgroundService extends AccessibilityService {


    public static ForgroundService mService;

    AccessibilityServiceInfo info;

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        log("gesture start");
        mService = this;
    }

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


    }

    @Override
    public void onInterrupt() {
        log("gesture onInterrupt");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("gesture onDestroy");
        mService = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("car_projection start");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            log("action is " + intent.getAction());
            if ("service_start".equals(intent.getAction())) {
                NotificationChannel channel = null;
                final String channelId = "projection_car";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    channel = new NotificationChannel(channelId, "System", NotificationManager.IMPORTANCE_LOW);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.createNotificationChannel(channel);
                    }
                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
                final Notification xiriNotification = notificationBuilder.setOngoing(true)
                        .setContentText("projection car")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build();
                startForeground(10010, xiriNotification);
            } else {
                stopForeground(true);
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
