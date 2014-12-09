package com.example.DailySelfie;

import android.content.Context;
import android.content.SharedPreferences;

public class DailySelfiePreferences {

    private static final String PREFERENCES = "SelfyPreferences";
    private static final String DIRECTORY = "SelfiesDirectory";

    private static final String NOTIFICATION_ENABLED = "NotificationEnabled";
    private static final String PERIOD_UNIT = "PeriodUnit";
    private static final String PERIOD_VALUE = "PeriodValue";
    public static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    public static final int MILLISECONDS_PER_HOUR = 60 * 60 * 1000;
    public static final int MILLISECONDS_PER_DAY = 60 * 60 * 24 * 1000;
    public static final int MILLISECONDS_PER_SECOND = 1000;
    private Context context;


    public DailySelfiePreferences(Context context) {
        this.context = context;
    }

    public boolean isNotificationEnabled() {
        return getSharedPreferences().getBoolean(NOTIFICATION_ENABLED, false);
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
    }

    public TimeUnit getNotificationPeriodUnit() {
        return TimeUnit.fromString(getSharedPreferences().getString(PERIOD_UNIT, TimeUnit.Day.getShortcut()));
    }

    public int getNotificationPeriodValue() {
        return getSharedPreferences().getInt(PERIOD_VALUE, 1);
    }

    public void saveNotificationEnabled(boolean notificationEnabled) {
        getSharedPreferences().edit().putBoolean(NOTIFICATION_ENABLED, notificationEnabled).commit();
    }

    public void saveNotificationPeriod(int value, TimeUnit unit) {
        getSharedPreferences().edit().putInt(PERIOD_VALUE, value).putString(PERIOD_UNIT, unit.getShortcut()).commit();
    }

    public String getSelfiesDirectory() {
        return getSharedPreferences().getString(DIRECTORY, null);
    }

    public void saveDirectory(String directory) {
        getSharedPreferences().edit().putString(DIRECTORY, directory).commit();
    }

    public int evaluateNotificationPeriod(int value, TimeUnit unit) {
        switch (unit) {
            case Minute:
                return value * MILLISECONDS_PER_MINUTE;
            case Hour:
                return value * MILLISECONDS_PER_HOUR;
            case Day:
                return value * MILLISECONDS_PER_DAY;
            case Second:
            default:
                return value * MILLISECONDS_PER_SECOND;
        }
    }
}
