package com.apps.jlee.carcare.Broadcast_Receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.apps.jlee.carcare.R;

import java.util.Random;

/*Broadcast receiver is a component of the app that listens for an event made by the system i.e an alarm set off by the system. Once this alarm sets off, the broadcast receiver will build
  a notification.*/
public class AlarmReceiver extends BroadcastReceiver
{
    final String CHANNEL_ID = "1";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Generate a random ID for the nofication. With this unique id, we can cancel the notification.
        int notificationId = new Random().nextInt();

        //getStringExtra "title" and "message" are being passed to another Intent because in the YesBrodacastReceiver, we need this info to cancel the notification
        Intent yesIntent = new Intent(context,YesBroadcastReceiver.class);
        yesIntent.putExtra("notification_id",notificationId);
        yesIntent.putExtra("title",intent.getStringExtra("title"));
        yesIntent.putExtra("message",intent.getStringExtra("message"));
        yesIntent.putExtra("id",intent.getIntExtra("id",0));

        Intent noIntent = new Intent(context,NoBroadCastReceiver.class);
        noIntent.putExtra("notification_id",notificationId);
        noIntent.putExtra("id",intent.getIntExtra("id",0));

        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context,intent.getIntExtra("id",0),yesIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(context,intent.getIntExtra("id",0),noIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"1")
                .setSmallIcon(R.drawable.ic_stat_drive_eta)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("message"))
                .addAction(0,"Yes",yesPendingIntent)
                .addAction(0,"No",noPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
}
