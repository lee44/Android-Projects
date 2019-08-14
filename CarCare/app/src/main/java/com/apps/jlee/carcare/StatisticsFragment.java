package com.apps.jlee.carcare;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private class AsyncStatTask extends AsyncTask<Void,Void,double[]>
    {
        private SQLiteDatabaseHandler handler;

        public AsyncStatTask(SQLiteDatabaseHandler handler)
        {
            this.handler = handler;
        }

        @Override
        protected double[] doInBackground(Void... voids)
        {
            double[] v = new double[9];
            List<Object> list = handler.getAllEntries(new Gas());
            Double cost = 0.0, miles = 0.0, gallons = 0.0;
            long timeInMilliseconds = 0;

            if(list.size() > 2)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    cost += Double.valueOf((((Gas) (list.get(i))).getCost()));
                    miles += Double.valueOf((((Gas) (list.get(i))).getMiles()));
                    gallons += Double.valueOf((((Gas) (list.get(i))).getAmount()));
                }
                v[0] = cost; v[1] = miles; v[2] = gallons;
                v[3] = cost/list.size(); v[4] = miles/list.size(); v[5] = gallons/list.size();

                //LinearRegression
                //Log.v("Dodgers",""+(int)(System.currentTimeMillis()/(1000*60*60*24)));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    Date mDate = sdf.parse(Calendar.getInstance().get(Calendar.YEAR)+"-12-31");
                    timeInMilliseconds = mDate.getTime();
                }
                catch (ParseException e){e.printStackTrace();}

                v[6] = new LinearRegression("cost",list).predictForValue((int)(timeInMilliseconds/(1000*60*60*24)));
                v[7] = new LinearRegression("miles",list).predictForValue((int)(timeInMilliseconds/(1000*60*60*24)));
                v[8] = new LinearRegression("gallons",list).predictForValue((int)(timeInMilliseconds/(1000*60*60*24)));

                //LinearRegression2
                //v[6] = new LinearRegression2(new double[]{18025,18037,18051},new double[]{59,61,25}).predict(18060);
            }
            return v;
        }

        @Override
        protected void onPostExecute(double[] v)
        {
            super.onPostExecute(v);

            DecimalFormat number = new DecimalFormat("0.00");

            totalCost.setText("$"+number.format(v[0]));
            totalGallons.setText(number.format(v[1]));
            totalMiles.setText(number.format(v[2]));

            averageCost.setText("$"+number.format(v[3]));
            averageMiles.setText(number.format(v[4]));
            averageGallons.setText(number.format(v[5]));

            forecastCost.setText("$"+number.format(v[6]));
            forecastMiles.setText(number.format(v[7]));
            forecastGallons.setText(number.format(v[8]));
        }
    }
}
