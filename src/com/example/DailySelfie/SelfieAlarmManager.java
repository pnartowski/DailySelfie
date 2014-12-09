package com.example.DailySelfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SelfieAlarmManager {

    private Context context;

    public SelfieAlarmManager(Context context) {
        this.context = context;
    }

    public void scheduleAlarm(int period) {
        getAlarmManager().setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, period, period, getAlarmIntent());
    }

    private PendingIntent getAlarmIntent() {
        Intent notification = new Intent(context, NotificationService.class);
        return PendingIntent.getService(context, 0, notification, 0);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    }

    public void removeAlarm() {
        getAlarmManager().cancel(getAlarmIntent());
    }
}
