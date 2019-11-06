package com.apps.jlee.carcare.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.carcare.Activities.MainActivity;
import com.apps.jlee.carcare.Fragments.GasFragment;
import com.apps.jlee.carcare.Objects.Gas;
import com.apps.jlee.carcare.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GasAdapter extends RecyclerView.Adapter<GasAdapter.ViewHolder>
{
    private List<Object> gasList;
    private Context c;
    private GasFragment g;

    public GasAdapter(List<Object> gasList, Context c, GasFragment g)
    {
        this.gasList = gasList;
        this.c = c;
        this.g = g;
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

        holder.date.setText(new SimpleDateFormat("M/dd/yy").format(new Date(entry.getDateRefilled())));
        holder.cost.setText(number.format(entry.getCost()));
        holder.miles.setText(number.format(entry.getMiles()));
        holder.gallons.setText(number.format(entry.getAmount()));
        holder.mpg.setText(String.format("%.2f", entry.getMiles() / entry.getAmount())+" MPG");

        if(entry.showCheckbox)
            holder.cb.setVisibility(View.VISIBLE);
        else
            holder.cb.setVisibility(View.GONE);

        if(entry.showChecked)
            holder.cb.setChecked(true);
        else
            holder.cb.setChecked(false);
    }

    @Override
    public int getItemCount()
    {
        return gasList.size();
    }

    /**Reveals All checkboxes*/
    private void revealAllCheckBoxes()
    {
        for(int i = 0; i < gasList.size(); i++)
        {
            ((Gas)(gasList.get(i))).showCheckbox = true;
            notifyItemChanged(i);
        }
        ((MainActivity)c).toggleToolbarSelectedDisplay();
    }

    private void hideAllCheckBoxes()
    {
        for (int i = 0; i < gasList.size(); i++)
        {
            ((Gas)gasList.get(i)).showCheckbox = false;
            ((Gas)gasList.get(i)).showChecked = false;
            notifyItemChanged(i);
        }
        ((MainActivity)c).toggleToolbarSelectedDisplay();
    }

    //Provides a direct reference to each of the views within a data item. Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView date,cost,miles,gallons,mpg;
        ImageView cash_icon,miles_icon,gallons_icon;
        CardView cv;
        CheckBox cb;

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
            cb = itemView.findViewById(R.id.gas_listview_checkbox);

            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    int i = getAdapterPosition();
                    if(!((Gas)(gasList.get(i))).showCheckbox)
                    {
                        ((Gas)(gasList.get(i))).showChecked = true;
                        notifyItemChanged(i);
                        revealAllCheckBoxes();
                        g.toggleFloatingActionButton();
                        ((MainActivity)c).toggleBottomNavBarButtons();
                        ((MainActivity)c).setSelected(1);
                        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(500);
                        }
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int i = getAdapterPosition();
                    if(((Gas)(gasList.get(i))).showCheckbox)
                    {
                        if(((Gas)(gasList.get(i))).showChecked)
                        {
                            ((Gas)(gasList.get(i))).showChecked = false;
                            ((MainActivity)c).increaseSelected(-1);
                        }
                        else
                        {
                            ((Gas)(gasList.get(i))).showChecked = true;
                            ((MainActivity)c).increaseSelected(1);
                        }
                        notifyItemChanged(i);
                    }
                }
            });
        }
    }
}
