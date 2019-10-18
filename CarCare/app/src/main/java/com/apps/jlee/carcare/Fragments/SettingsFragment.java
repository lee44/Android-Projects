package com.apps.jlee.carcare.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.apps.jlee.carcare.Adapters.SpinnerCustomAdapter;
import com.apps.jlee.carcare.R;
import com.apps.jlee.carcare.util.Utils;

public class SettingsFragment extends Fragment
{
    private Spinner spThemes, spDefaultTab;
    private String[] themes = {"Material Light","Material Dark"};
    private String[] tabs = {"Home","Gas"};

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings, container, false);
        spThemes = view.findViewById(R.id.spThemes);
        spDefaultTab = view.findViewById(R.id.spdefaulttab);

        SpinnerCustomAdapter theme_adapter = new SpinnerCustomAdapter(getContext(), themes);
        spThemes.setAdapter(theme_adapter);

        SpinnerCustomAdapter tab_adapter = new SpinnerCustomAdapter(getContext(), tabs);
        spDefaultTab.setAdapter(tab_adapter);

        spThemes.setSelection(getContext().getSharedPreferences("Preferences",0).getInt("Theme",0),false);
        spDefaultTab.setSelection(getContext().getSharedPreferences("Preferences",0).getInt("Tab",0),false);

        spThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Utils.changeToTheme(getActivity(),adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spDefaultTab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                SharedPreferences pref = getContext().getSharedPreferences("Preferences", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("Tab",adapterView.getSelectedItemPosition());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return view;
    }
}
