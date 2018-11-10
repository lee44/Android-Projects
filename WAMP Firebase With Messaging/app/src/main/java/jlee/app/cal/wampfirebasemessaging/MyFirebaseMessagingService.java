package jlee.app.cal.wampfirebasemessaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseMsgService";
    private LocalBroadcastManager broadcaster;

    public void onCreate()
    {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        if (remoteMessage.getData().size() > 0)
        {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try
            {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Intent intent = new Intent("MessageReceived");
                intent.putExtra("Message", remoteMessage.getData().toString());
                broadcaster.sendBroadcast(intent);
            } catch (Exception e)
            {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
}
