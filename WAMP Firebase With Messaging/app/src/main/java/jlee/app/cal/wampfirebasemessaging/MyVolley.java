package jlee.app.cal.wampfirebasemessaging;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//Since we will be making many http request, this singleton pattern for volley will handle network requests.
//The singleton pattern restricts the instantiation of a class to one object(only one can be created). For example, lets say we have a
//single DB connection shared by multiple objects instead of creating a separate DB connection for every object which may be costly.
public class MyVolley
{
    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyVolley(Context context)
    {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context)
    {
        //This if statement ensures only one instantiation of the class is created.
        if (mInstance == null)
            mInstance = new MyVolley(context);

        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }

}