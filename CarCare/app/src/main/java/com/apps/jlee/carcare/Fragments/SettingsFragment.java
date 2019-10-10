package com.apps.jlee.carcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.apps.jlee.carcare.R;
import com.apps.jlee.carcare.util.Utils;

public class SettingsFragment extends Fragment
{
    private Spinner spThemes;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings, container, false);
        spThemes = view.findViewById(R.id.spThemes);

        spThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Utils.changeToTheme(getActivity(),adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        return view;
    }
}
