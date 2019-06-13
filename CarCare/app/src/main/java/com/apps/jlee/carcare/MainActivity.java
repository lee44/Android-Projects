package com.apps.jlee.carcare;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
{
    private HomeFragment home;
    private EntryFragment entry;
    private OilFragment oil;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        home = new HomeFragment();
        entry = new EntryFragment();
        oil = new OilFragment();

        setFragment(home);
        bottomNavigationView.setSelectedItemId(R.id.Home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.Home:
                        setFragment(home);
                        break;

                    case R.id.Gas:
                        setFragment(entry);
                        break;

                    case R.id.Oil:
                        setFragment(oil);
                        break;
                }
                return true;
            }
        });

    }
    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }
}
