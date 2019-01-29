package com.apps.jlee.Notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
/*
When the alarm fires from BootReceiver class, this WakefulBroadcastReceiver receives the broadcast Intent and then posts the notification.

WakefulBroadcastReceiver is a special type of broadcast receiver that takes care of creating and managing a PARTIAL_WAKE_LOCK for you app. A WakefulBroadcastReceiver passes off
the work to a Service (in our case Alarm Service), while ensuring that the device does not go back to sleep in the transition.
If you don't hold a wake lock while transitioning the work to a service, you are effectively allowing the device to go back to sleep before the work completes.

PARTIAL_WAKE_LOCK ensures that the CPU is running.

This receiver is registered in the manifest so it doesnt rely on Bootreceiver to send a broadcast to this receiver.
*/
public class WakefulReceiver extends WakefulBroadcastReceiver
{
    private AlarmManager mAlarmManager;

    public void onReceive(Context context, Intent intent)
    {

        //release the wake lock so CPU can go back to sleep
        WakefulReceiver.completeWakefulIntent(intent);
    }

    /*
     * Sets the next alarm to run. When the alarm fires,
     * the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity.
     */
    public void setAlarm(Context context)
    {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //ALWAYS recompute the calendar after using add, set, roll
        Date date = calendar.getTime();

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);

        //In the manifest, the BootReceiver has android:enabled = false which means that the receiver will not be called unless the application explicitly enables it.
        //This prevents the boot receiver from being called unnecessarily. The following enables the receiver permanently even through reboots and overrides the manifest setting.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity
     */
    public void cancelAlarm(Context context)
    {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        mAlarmManager.cancel(alarmIntent);

        // Disable {@code BootReceiver} so that it doesn't automatically restart when the device is rebooted.
        //// TODO: you may need to reference the context by ApplicationActivity.class
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }
}
