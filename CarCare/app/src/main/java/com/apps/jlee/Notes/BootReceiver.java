package com.apps.jlee.Notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

//This receiver is registered for the ACTION_BOOT_COMPLETED system event which is fired once the Android system has completed the boot process
public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //getAction() gets the action defined in the android manifest under the action tag
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, WakefulReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, i, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Date date = calendar.getTime();

            //When alarm goes off, a broadcast receiver is called which is the WakefuleReceiver class
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
        }
    }
}
