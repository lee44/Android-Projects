package com.apps.jlee.carcare.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.apps.jlee.carcare.R;


public class SpinnerCustomAdapter extends BaseAdapter
{
    Context context;
    String[] filters;
    LayoutInflater inflater;

    public SpinnerCustomAdapter(Context applicationContext, String[] filters)
    {
        this.context = applicationContext;
        this.filters = filters;
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount()
    {
        return filters.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = inflater.inflate(R.layout.spinner_item, null);
        view.setMinimumWidth((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.39));
        TextView tv = view.findViewById(R.id.Filters);
        tv.setText(filters[i]);

        return view;
    }
}
