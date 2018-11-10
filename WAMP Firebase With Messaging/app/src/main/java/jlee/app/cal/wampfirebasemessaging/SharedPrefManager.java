package jlee.app.cal.wampfirebasemessaging;

import android.content.Context;
import android.content.SharedPreferences;

//This class just stores device token in SharedPreferences
public class SharedPrefManager
{
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context)
    {
        mCtx = context;
    }

    //This returns an instance of the class to access the methods saveDeviceToken or getDeviceToken i.e SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    public static synchronized SharedPrefManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token)
    {
        //Since no device token has been saved, getSharedPreferences will just give you access to the SharedPreferences.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, null);
    }
}