package com.apps.jlee.carcare.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private TextView toolbar_title;
    private Toolbar toolbar;
    private static final int REQUEST = 112;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavBar = findViewById(R.id.bottom_navigation);
        toolbar_title = findViewById(R.id.toolbar_title);

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
                        setFragment(home);
                        bottomNavBar.findViewById(R.id.Home).setSelected(true);
                        bottomNavBar.findViewById(R.id.Gas).setSelected(false);
                        break;

                    case R.id.Gas:
                        setFragment(gas);
                        bottomNavBar.findViewById(R.id.Home).setSelected(false);
                        bottomNavBar.findViewById(R.id.Gas).setSelected(true);
                        break;

                    case R.id.cancel:
                        countSelected(-count);
                        toggleToolbarSelection();
                        toggleBottomNavBarButtons();
                        gas.cancel();
                        break;

                    case R.id.delete:
                        countSelected(-count);
                        gas.deleteSelectedItems();
                        break;

                    case R.id.selectall:
                        gas.toggleSelectAllCheckBoxes();
                }
                return true;
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

    /**
     * Hides Bottom Nav Bar when scrolling Recyclerview
     */
    public void toggleBottomNavBarVisibility(boolean visible)
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

    /**
     * Reveals or hides checkbox inside the Toolbar
     */
    public void toggleToolbarSelection()
    {
        if(toolbar_title.getText().equals("CarCare"))
        {
            toolbar_title.setText("0 selected");
            toolbar_title.setTextSize(19);
        }
        else
        {
            toolbar_title.setText("CarCare");
            toolbar_title.setTextSize(24);
        }
    }

    /**
     * Switches menus for the Bottom Nav Bar
     */
    public void toggleBottomNavBarButtons()
    {
        if(bottomNavBar.getMenu().getItem(0).getItemId() == R.id.Home)
        {
           bottomNavBar.getMenu().clear();
           bottomNavBar.inflateMenu(R.menu.bot_nav_selectall_delete_cancel);
           bottomNavBar.setItemIconTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.checked}},new int[]{Color.WHITE}));
           bottomNavBar.setItemTextColor(new ColorStateList(new int[][]{new int[]{-android.R.attr.checked}},new int[]{Color.WHITE}));
        }
        else
        {
            int[][] states = new int[][]
            {
                new int[] {android.R.attr.state_selected},
                new int[] {-android.R.attr.state_selected}
            };

            int[] colors = new int[]
            {
                Color.WHITE,
                Color.parseColor("#32000000")
            };

            bottomNavBar.getMenu().clear();
            bottomNavBar.inflateMenu(R.menu.bot_nav_home_gas);
            bottomNavBar.setItemIconTintList(new ColorStateList(states,colors));
            bottomNavBar.setItemTextColor(new ColorStateList(states,colors));

            bottomNavBar.findViewById(R.id.Gas).setSelected(true);
        }
    }

    public void countSelected(int n)
    {
        count += n;
        toolbar_title.setText(count+" selected");
    }

    public void setSelected(int x)
    {
        count = x;
        toolbar_title.setText(count+" selected");
    }
}
