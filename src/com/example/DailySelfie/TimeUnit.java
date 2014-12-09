package com.example.DailySelfie;

/**
 * Created by pol on 11/27/14.
 */
public enum TimeUnit {
    Second("sec") ,
    Minute("min"),
    Hour("hour"),
    Day("day");

    private String shortcut;
    TimeUnit(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }

    public static TimeUnit fromString(String value) {
        for (TimeUnit one : TimeUnit.values()) {
            if (one.getShortcut().equals(value)) {
                return one;
            }
        }
        return Second;
    }
}
