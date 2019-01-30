package jlee.app.cal.wampfirebasemessaging;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class HomeActivity extends AppCompatActivity
{
    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragement;
    private SettingsFragment settingFragment;
    public static Context contextOfApplication;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        mainNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mainFrame = (FrameLayout) findViewById(R.id.main_frame);

        homeFragment = new HomeFragment();
        profileFragement = new ProfileFragment();
        settingFragment = new SettingsFragment();

        setFragment(homeFragment);

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_profile:
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(profileFragement);
                        return true;

                    case R.id.nav_settings:
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(settingFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}
