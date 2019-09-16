package com.apps.jlee.carcare.Adapters;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.carcare.Objects.Gas;
import com.apps.jlee.carcare.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GasAdapter extends RecyclerView.Adapter<GasAdapter.ViewHolder>
{
    private List<Object> gasList;

    public GasAdapter(List<Object> gasList)
    {
        this.gasList = gasList;
    }

    //Inflates the layout of the item and returns the ViewHolder
    @Override
    public GasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gas_listview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    // Populates data into the views of each item through the ViewHolder
    @Override
    public void onBindViewHolder(GasAdapter.ViewHolder holder, int position)
    {
        Gas entry = (Gas)gasList.get(position);
        DecimalFormat number = new DecimalFormat("0.00");

        TextView date = holder.date;
        date.setText(new SimpleDateFormat("M/dd/yy").format(new Date(entry.getDateRefilled())));

        TextView cost = holder.cost;
        cost.setText(number.format(entry.getCost()));

        TextView miles = holder.miles;
        miles.setText(number.format(entry.getMiles()));

        TextView gallons = holder.gallons;
        gallons.setText(number.format(entry.getAmount()));

        TextView mpg = holder.mpg;
        mpg.setText(String.format("%.2f", entry.getMiles() / entry.getAmount())+" MPG");
    }

    @Override
    public int getItemCount()
    {
        return gasList.size();
    }

    //Provides direct link to all the views inside each item
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView date,cost,miles,gallons,mpg;
        ImageView cash_icon,miles_icon,gallons_icon;
        CardView cv;

        //Provides a direct reference to each of the views within a data item
       // Used to cache the views within the item layout for fast access
        public ViewHolder(View itemView)
        {
            super(itemView);

            date = itemView.findViewById(R.id.Date);
            cost = itemView.findViewById(R.id.cost);
            miles = itemView.findViewById(R.id.miles);
            gallons = itemView.findViewById(R.id.gallons);
            mpg = itemView.findViewById(R.id.MPG);
            cv = itemView.findViewById(R.id.cardView);

            cash_icon = itemView.findViewById(R.id.cash_icon);
            miles_icon = itemView.findViewById(R.id.miles_icon);
            gallons_icon = itemView.findViewById(R.id.gallons_icon);
        }
    }
}
