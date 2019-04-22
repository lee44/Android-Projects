package com.apps.jlee.carcare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment
{
    private SQLiteDatabaseHandler db;
    private List<Entry> entries;
    private List<Object> list;
    private LineChart chart;
    private String[] values;
    private ProgressBar oilPB, brakesPB, wheelsPB, batteryPB, timingBeltPB;
    private String dbDateFormat = "yyyy-MM-dd HH:mm:ss";
    private TextView t;

    public HomeFragment(){}

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
        list = db.getAllEntries(new Gas());
        View v = inflater.inflate(R.layout.fragment_home, container, false);
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

        if(list != null)
        {
            loadGraphData(1);
            loadProgressBar();
            //Log.v("Dodgers",list.toString());
        }
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
                    values[i] = (String) DateFormat.format("MM",date) + "/" + (String) DateFormat.format("dd",date);
                }
                break;

            case 2:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getAmount()));
                    values[i] = (String) DateFormat.format("MM", date) + "/" + (String) DateFormat.format("dd", date);
                }
                break;
            case 3:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getMiles()));
                    values[i] = (String) DateFormat.format("MM", date) + "/" + (String) DateFormat.format("dd", date);
                }
                break;
            case 4:
                for (int i = 0; i < list.size(); i++)
                {
                    Date date = new Date(((Gas)(list.get(i))).getDateRefilled());

                    entries.add(new Entry(i, (float) ((Gas) (list.get(i))).getMiles() / (float) ((Gas) (list.get(i))).getAmount()));
                    values[i] = (String) DateFormat.format("MM", date) + "/" + (String) DateFormat.format("dd", date);
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
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleHoleColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleHoleRadius(5f);
        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(values));
        xAxis.setLabelRotationAngle(45);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(15f);
        leftAxis.setTextColor(Color.BLACK);
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
        oilPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
                oilPB.setProgress((int)sharedpreferences.getFloat("oil",0));
            }
        });

        brakesPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
                brakesPB.setProgress((int)sharedpreferences.getFloat("brakes",0));
            }
        });

        wheelsPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
                wheelsPB.setProgress((int)sharedpreferences.getFloat("wheels",0));
            }
        });

        batteryPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
                batteryPB.setProgress((int)sharedpreferences.getFloat("battery",0));
            }
        });

        timingBeltPB.post(new Runnable()
        {
            @Override
            public void run()
            {
                SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
                timingBeltPB.setProgress((int)sharedpreferences.getFloat("timingbelt",0));
            }
        });
    }

    //This uses an activity to either reset a value for progress bar or cancel the notification
    private void sendNotification(String message, String title)
    {
        //Generate a random ID for the nofication. With this unique id, we can cancel the notification.
        int notificationId = new Random().nextInt();
        PendingIntent yesPendingIntent = YesNotificationActivity.getDismissIntent("Yes",notificationId,getContext());

        PendingIntent noPendingIntent = NoNotificationActivity.getDismissIntent("No",notificationId,getContext());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(),"1")
        .setSmallIcon(R.drawable.oil)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(Notification.PRIORITY_MAX)
        .setWhen(0)
        .addAction(0,"Yes",yesPendingIntent)
        .addAction(0,"No",noPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notificationBuilder.build());
    }

    //This uses broadcast receiver to either reset a value for progress bar or cancel notification
    private void sendNotificationUsingBroadcastReceiver(String message, String title)
    {
        //Generate a random ID for the nofication. With this unique id, we can cancel the notification.
        int notificationId = new Random().nextInt();

        Intent yesIntent = new Intent(getContext(),YesBroadcastReceiver.class);
        yesIntent.putExtra("notification_id",notificationId);
        Intent noIntent = new Intent(getContext(),NoBroadCastReceiver.class);
        noIntent.putExtra("notification_id",notificationId);

        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(getContext(),0,yesIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(getContext(),0,noIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(),"1")
        .setSmallIcon(R.drawable.oil)
        .setContentTitle(title)
        .setContentText(message)
        .addAction(0,"Yes",yesPendingIntent)
        .addAction(0,"No",noPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notificationBuilder.build());
    }
}
