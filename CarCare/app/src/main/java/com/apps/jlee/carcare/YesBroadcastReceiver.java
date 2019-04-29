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

        if(intent.getStringExtra("title").equals("Oil Replacement"))
        {
            editor.putFloat("oil",0);
            editor.putLong("oil_checkpoint",myCalendar.getTime().getTime());
        }
        else if(intent.getStringExtra("title").equals("Brake Replacement"))
        {
            editor.putFloat("brakes",0);
            editor.putLong("brakes_checkpoint",myCalendar.getTime().getTime());
        }
        else if(intent.getStringExtra("title").equals("Tire Replacement"))
        {
            editor.putFloat("wheels",0);
            editor.putLong("wheels_checkpoint",myCalendar.getTime().getTime());
        }
        else if(intent.getStringExtra("title").equals("Battery Replacement"))
        {
            editor.putFloat("battery",0);
            editor.putLong("battery_checkpoint",myCalendar.getTime().getTime());
        }
        else if(intent.getStringExtra("title").equals("Timing Belt Replacement"))
        {
            editor.putFloat("timingbelt",0);
            editor.putLong("timingbelt_checkpoint",myCalendar.getTime().getTime());
        }

        editor.apply();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        intent2.putExtra("title",intent.getStringExtra("title"));
        intent2.putExtra("message",intent.getStringExtra("message"));
        intent2.putExtra("id",intent.getIntExtra("id",0));
        PendingIntent pintent = PendingIntent.getBroadcast(context, intent.getIntExtra("id",0), intent2, 0);

        try { alarmManager.cancel(pintent);} catch (Exception e) { Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());}
    }
}
