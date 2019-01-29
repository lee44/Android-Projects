package com.apps.jlee.carcare;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/*One way of dismissing notification programatically*/
public class YesNotificationActivity extends Activity
{
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences = getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("oil",10);
        editor.apply();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }
    //Returns a pendingIntent that will call the class itself to cancel the notification which is implemented above
    public static PendingIntent getDismissIntent(String action, int notificationId, Context context)
    {
        Intent intent = new Intent(context, YesNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("Action",action);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }
}