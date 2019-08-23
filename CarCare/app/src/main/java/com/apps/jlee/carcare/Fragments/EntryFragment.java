package com.apps.jlee.carcare.Fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.jlee.carcare.R;

public class EntryFragment extends Fragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        MyTabPagerAdapter tabPager = new MyTabPagerAdapter(getChildFragmentManager());

        ViewPager viewPager = getView().findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(tabPager);

        // Display a tab for each Fragment displayed in ViewPager.
        TabLayout tabLayout = getView().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    class MyTabPagerAdapter extends FragmentPagerAdapter
    {
        private String tabTitles[] = new String[] { "Gas", "Oil"};

        MyTabPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return 2; //Set the number of tabs you want here
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new GasFragment();
                case 1:
                    return new OilFragment();
                default:
                    return new GasFragment();
            }
        }
        public CharSequence getPageTitle(int position)
        {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}

