package com.apps.jlee.carcare;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment
{
    private TextView totalCost, totalGallons, totalMiles, averageCost, averageGallons, averageMiles, forecastCost, forecastGallons, forecastMiles;
    private SQLiteDatabaseHandler db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        db = new SQLiteDatabaseHandler(getContext());

        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        totalCost = view.findViewById(R.id.totalCost);
        totalGallons = view.findViewById(R.id.totalGallons);
        totalMiles = view.findViewById(R.id.totalMiles);
        averageCost = view.findViewById(R.id.averageCost);
        averageGallons = view.findViewById(R.id.averageGallons);
        averageMiles = view.findViewById(R.id.averageMiles);
        forecastCost = view.findViewById(R.id.forcastCost);
        forecastGallons = view.findViewById(R.id.forcastGallons);
        forecastMiles = view.findViewById(R.id.forcastMiles);

        new AsyncStatTask(db).execute();

        return view;
    }

    private class AsyncStatTask extends AsyncTask<Void,Void,List<Object>>
    {
        private SQLiteDatabaseHandler handler;

        public AsyncStatTask(SQLiteDatabaseHandler handler)
        {
            this.handler = handler;
        }
        @Override
        protected List<Object> doInBackground(Void... voids)
        {
            return handler.getAllEntries(new Gas());
        }
        @Override
        protected void onPostExecute(List<Object> list)
        {
            super.onPostExecute(list);
            Date date = null;
            Double cost = 0.0, miles = 0.0, gallons = 0.0;
            DecimalFormat number = new DecimalFormat("0.00");

            if(list != null)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    cost += Double.valueOf((((Gas)(list.get(i))).getCost()));
                    miles += Double.valueOf((((Gas)(list.get(i))).getMiles()));
                    gallons += Double.valueOf((((Gas)(list.get(i))).getAmount()));
                }
                totalCost.setText("$"+number.format(cost));
                totalGallons.setText(number.format(gallons));
                totalMiles.setText(number.format(miles));

                averageCost.setText("$"+number.format(cost/list.size()));
                averageGallons.setText(number.format(gallons/list.size()));
                averageMiles.setText(number.format(miles/list.size()));

                forecastCost.setText("$"+number.format(new LinearRegression().predictForValue(8)));
            }
        }
    }
}
