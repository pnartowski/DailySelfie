package com.example.DailySelfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleNotificationAfterReboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            DailySelfiePreferences preferences = new DailySelfiePreferences(context);
            SelfieAlarmManager alarmManager = new SelfieAlarmManager(context);
            alarmManager.scheduleAlarm(
                    preferences.evaluateNotificationPeriod(
                            preferences.getNotificationPeriodValue(),
                            preferences.getNotificationPeriodUnit()
                    )
            );
        }
    }
}
