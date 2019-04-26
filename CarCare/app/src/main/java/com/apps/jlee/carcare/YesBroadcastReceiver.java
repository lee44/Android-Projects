package com.apps.jlee.carcare;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class YesBroadcastReceiver extends BroadcastReceiver
{
    Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        int notificationId = intent.getIntExtra("notification_id", 0);

        SharedPreferences sharedpreferences = context.getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat("oil",0);
        editor.putLong("CarCareCheckpoint",myCalendar.getTime().getTime());
        editor.apply();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title",intent.getStringExtra("title"));
        intent.putExtra("message",intent.getStringExtra("message"));
        PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent2, 0);

        try { alarmManager.cancel(pintent);} catch (Exception e) { Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());}
    }
}
