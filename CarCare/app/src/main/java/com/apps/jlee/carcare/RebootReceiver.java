package com.apps.jlee.carcare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class RebootReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            SharedPreferences sharedpreferences = context.getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context, AlarmReceiver.class);
            intent2.putExtra("title",sharedpreferences.getString("notification_title",""));
            intent2.putExtra("message",sharedpreferences.getString("notification_description",""));
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 00);

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000 * 60 * 60 * 24, alarmIntent);
        }
    }
}
