package com.apps.jlee.carcare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Generate a random ID for the nofication. With this unique id, we can cancel the notification.
        int notificationId = new Random().nextInt();

        Intent yesIntent = new Intent(context,YesBroadcastReceiver.class);
        yesIntent.putExtra("notification_id",notificationId);
        Intent noIntent = new Intent(context,NoBroadCastReceiver.class);
        noIntent.putExtra("notification_id",notificationId);

        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context,0,yesIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(context,0,noIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"1")
                .setSmallIcon(R.drawable.warning_icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("message"))
                .addAction(0,"Yes",yesPendingIntent)
                .addAction(0,"No",noPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notificationBuilder.build());
    }
}
