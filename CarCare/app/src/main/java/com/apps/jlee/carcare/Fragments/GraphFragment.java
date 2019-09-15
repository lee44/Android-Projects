package com.apps.jlee.carcare.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.jlee.carcare.Objects.Gas;
import com.apps.jlee.carcare.R;
import com.apps.jlee.carcare.Data.SQLiteDatabaseHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphFragment extends Fragment
{
    private SQLiteDatabaseHandler db;
    private List<Entry> entries;
    private List<Object> list;
    private LineChart chart;
    private String[] values;
    private ProgressBar oilPB, brakesPB, wheelsPB, batteryPB, timingBeltPB;
    private String dbDateFormat = "yyyy-MM-dd HH:mm:ss";
    private TextView t;

    public GraphFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        db = new SQLiteDatabaseHandler(getContext());
        entries = new ArrayList<Entry>();
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        chart = v.findViewById(R.id.chart);
        oilPB = v.findViewById(R.id.oilPB);
        brakesPB = v.findViewById(R.id.brakesPB);
        wheelsPB = v.findViewById(R.id.wheelsPB);
        batteryPB = v.findViewById(R.id.batteryPB);
        timingBeltPB = v.findViewById(R.id.timingBeltPB);
        t = v.findViewById(R.id.title);

        oilPB.setMax(3000);
        brakesPB.setMax(50000);
        wheelsPB.setMax(15000);
        batteryPB.setMax(30000);
        timingBeltPB.setMax(100000);

        new AsyncDBTask(db).execute();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar.
        inflater.inflate(R.menu.actionbar_home_fragment, menu);
    }

    //Called when an item inside action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        entries.clear();
        chart.clear();

        switch(item.getItemId())
        {
            case R.id.GraphByCost: t.setText("Cost Graph");loadGraphData(1);break;
            case R.id.GraphByGallons: t.setText("Gallons Graph");loadGraphData(2);break;
            case R.id.GraphByMiles: t.setText("Miles Graph");loadGraphData(3);break;
            case R.id.GraphByMPG: t.setText("MPG Graph");loadGraphData(4);break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadGraphData(int graphType)
    {
        SimpleDateFormat format = new SimpleDateFormat(dbDateFormat);
        values = new String[list.size()];

        switch (graphType)
        {
            case 1:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i,(float)((Gas)(list.get(i))).getCost()));
                    values[i] = (String) DateFormat.format("M",date) + "/" + (String) DateFormat.format("dd",date);
                }
                break;

            case 2:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getAmount()));
                    values[i] = (String) DateFormat.format("M", date) + "/" + (String) DateFormat.format("dd", date);
                }
                break;
            case 3:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getMiles()));
                    values[i] = (String) DateFormat.format("M", date) + "/" + (String) DateFormat.format("dd", date);
                }
                break;
            case 4:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getMiles() / (float) ((Gas) (list.get(i))).getAmount()));
                    values[i] = (String) DateFormat.format("M", date) + "/" + (String) DateFormat.format("dd", date);
                }
                break;
        }
        drawGraph();
    }

    public void drawGraph()
    {
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setLineWidth(3);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.rgb(0,255,47));
        dataSet.setCircleHoleColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleHoleRadius(5f);
        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter()
        {
            // "value" represents the position of the label on the axis (x or y) which is why we need to store the values into a string array.
            public String getFormattedValue(float value, AxisBase axis)
            {
                if (value >= 0)
                {
                    if (values.length > (int) value)//This prevents IndexOutofArray Exception
                        return values[(int)value];
                    else
                        return "";
                }
                else
                    return "";
            }
        });

        xAxis.setLabelRotationAngle(45);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(15f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        /*Similar to setting margins around chart*/
        chart.setExtraOffsets(0, 0, 0, 10);

        chart.setData(lineData);
        chart.invalidate();

        chart.setVisibleXRangeMaximum(8);/*Set Maximum x values being displayed*/
        chart.moveViewToX(list.size());
    }

    public void loadProgressBar()
    {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);

        oilPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                oilPB.setProgress((int)sharedpreferences.getFloat("oil",0));
            }
        });

        brakesPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                brakesPB.setProgress((int)sharedpreferences.getFloat("brakes",0));
            }
        });

        wheelsPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                wheelsPB.setProgress((int)sharedpreferences.getFloat("wheels",0));
            }
        });

        batteryPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                batteryPB.setProgress((int)sharedpreferences.getFloat("battery",0));
            }
        });

        timingBeltPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                timingBeltPB.setProgress((int)sharedpreferences.getFloat("timingbelt",0));
            }
        });
    }

    private class AsyncDBTask extends AsyncTask<Void,Void,List<Object>>
    {
        private SQLiteDatabaseHandler handler;

        public AsyncDBTask(SQLiteDatabaseHandler handler)
        {
            this.handler = handler;
        }
        @Override
        protected List<Object> doInBackground(Void... voids)
        {
            return handler.getAllEntries(new Gas());
        }
        @Override
        protected void onPostExecute(List<Object> DBlist)
        {
            super.onPostExecute(DBlist);

            if(DBlist.size() != 0)
            {
                list = DBlist;
                loadGraphData(1);
                loadProgressBar();

            }
        }
    }
}

