package com.apps.jlee.carcare.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.apps.jlee.carcare.R;

public class Utils
{
    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int THEME_MATERIAL_DARK = 1;

    public static void changeToTheme(Activity activity, int theme)
    {
        SharedPreferences pref = activity.getSharedPreferences("Preferences", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("Theme",theme);
        editor.commit();

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity)
    {
        SharedPreferences pref = activity.getSharedPreferences("Preferences", 0);

        switch (pref.getInt("Theme",1))
        {
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.Material_Light);
                break;
            case THEME_MATERIAL_DARK:
                activity.setTheme(R.style.Material_Dark);
                break;
        }
    }
}
