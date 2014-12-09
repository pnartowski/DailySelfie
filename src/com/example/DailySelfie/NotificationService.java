package com.example.DailySelfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;

public class NotificationService extends Service {

    public static final String CONTENT_TITLE = "Daily Selfie";
    public static final String CONTENT_TEXT = "It's time to take selfie";
    public static final String TICKER_TEXT = "Take Selfie";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, createNotification());
        return START_STICKY;
    }

    private Notification createNotification() {
        PendingIntent openDailySelfie = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        return new Notification.Builder(this)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(CONTENT_TEXT)
                .setSmallIcon(R.drawable.ic_action_camera)
                .setTicker(TICKER_TEXT)
                .setContentIntent(openDailySelfie)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
