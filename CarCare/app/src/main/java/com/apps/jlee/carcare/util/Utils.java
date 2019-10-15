package com.apps.jlee.carcare.util;

import android.app.Activity;
import android.content.Intent;
import com.apps.jlee.carcare.R;

public class Utils
{
    public static int sTheme = 1;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_MATERIAL_LIGHT = 1;
    public final static int THEME_MATERIAL_DARK = 2;

    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.Material_Light);
                break;
            case THEME_MATERIAL_DARK:
                activity.setTheme(R.style.Material_Dark);
                break;
        }
    }
}
