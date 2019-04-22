package com.apps.jlee.carcare;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

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
    }
}
