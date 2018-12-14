package com.ytwd.midiengine.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by yanwei on 6/6/16.
 */
public class AlarmUtils {
    private final static String BROADCAST_ACT_AWAKE = "com.mobiledev.lib.pendingalarm.awake";
    private final static int requestCode = 100;

    public static void startScheduleAfterDays(Context context, int delayDays){
        Context app = context.getApplicationContext();
        Intent intent = new Intent(app, AlarmBroadcastReceiver.class);
        intent.setAction(BROADCAST_ACT_AWAKE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60*60*24*delayDays);
        // TODO: for test only.
        //calendar.add(Calendar.SECOND, delayDays);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(app, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager)app.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void stopScheduleAfterDays(Context context){
        Context app = context.getApplicationContext();
        Intent intent = new Intent(app, AlarmBroadcastReceiver.class);
        intent.setAction(BROADCAST_ACT_AWAKE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(app, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager)app.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static long getLastAccessTime(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(null,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getLong("last_time", -1);
    }

    public static void setLastAccessTimeNow(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(null,
                Activity.MODE_PRIVATE).edit();
        editor.putLong("last_time", System.currentTimeMillis());
        editor.commit();
    }
}
