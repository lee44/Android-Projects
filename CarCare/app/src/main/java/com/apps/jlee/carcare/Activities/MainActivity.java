package com.apps.jlee.carcare.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.jlee.carcare.Fragments.GasFragment;
import com.apps.jlee.carcare.Fragments.HomeFragment;
import com.apps.jlee.carcare.R;

public class MainActivity extends AppCompatActivity
{
    private HomeFragment home;
    private GasFragment gas;
    private BottomNavigationView bottomNavBar;
    private CheckBox cb;
    private TextView toolbar_title,checkbox_text;
    private Toolbar toolbar;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavBar = findViewById(R.id.bottom_navigation);
        cb = findViewById(R.id.toolbar_checkbox);
        toolbar_title = findViewById(R.id.toolbar_title);
        checkbox_text = findViewById(R.id.checkbox_text);

        home = new HomeFragment();
        gas = new GasFragment();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavBar.setSelectedItemId(R.id.Home);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.Home:
                        setToolbarCheckBoxVisibility();
                        setFragment(home);
                        break;

                    case R.id.Gas:
                        setFragment(gas);
                        break;
                }
                return true;
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                    gas.selectAllCheckBoxes();
                else
                    gas.deselectAllCheckBoxes();
            }
        });

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST);
        }
        else
        {
            setFragment(home);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setFragment(home);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    public void setBottomNavBarVisibility(boolean visible)
    {
        if (bottomNavBar.isShown() && !visible)
        {
            bottomNavBar.setVisibility(View.GONE);
        }
        else if (!bottomNavBar.isShown() && visible)
        {
            bottomNavBar.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarCheckBoxVisibility()
    {
        if(cb.getVisibility() != View.VISIBLE)
        {
            checkbox_text.setVisibility(View.VISIBLE);
            cb.setVisibility(View.VISIBLE);
            toolbar_title.setVisibility(View.GONE);
        }
        else
        {
            checkbox_text.setVisibility(View.GONE);
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
            toolbar_title.setVisibility(View.VISIBLE);
        }
    }
}
