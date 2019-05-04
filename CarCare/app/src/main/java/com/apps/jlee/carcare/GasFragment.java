package com.apps.jlee.carcare;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.internal.ParcelableSparseArray;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GasFragment extends Fragment
{
    GasDialogFragment d;
    private ListView listView;
    private ArrayList<HashMap<String,String>> arrayList;
    private SimpleAdapter adapter;
    private SQLiteDatabaseHandler db;
    private int updateFlag = 0, editPosition;

    public GasFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
       View view = inflater.inflate(R.layout.fragment_gas, container, false);
       FloatingActionButton fab = view.findViewById(R.id.fab);
       d = new GasDialogFragment();
       db = new SQLiteDatabaseHandler(getContext());
       listView = view.findViewById(R.id.gasListView);
       arrayList = new ArrayList<>();
       adapter = new SimpleAdapter(getContext(),arrayList,R.layout.gas_listview_items, new String[]{"Date","Cost","Miles","Gallons","MPG"},new int[]{R.id.Date,R.id.cost,R.id.miles,R.id.gallons,R.id.MPG});
       listView.setAdapter(adapter);

        new AsyncDBTask(db,new Gas(),"Fetch").execute();

       //registers the context menu for to be shown for our listview
       registerForContextMenu(listView);

       fab.setOnClickListener(new View.OnClickListener()
       {
            public void onClick(View view)
            {
                updateFlag = 0;
                Bundle b = new Bundle();
                b.putString("Cost","");
                b.putString("Miles","");
                b.putString("Gallons","");
                b.putString("Date","");
                d.setArguments(b);
                d.show(getFragmentManager(), "fragment_gas");
            }
       });

       d.setListener(new GasDialogFragment.GasInterface()
       {
           @Override
           public void onClick(String milesValue, String gallonsValue, String cost, Date date)
           {
               int dbCount = (int)db.getProfilesCount(new Gas())+1;

               //Insert new Gas Entry
               if(updateFlag == 0)
               {
                   Gas g = new Gas();
                   g.setID(dbCount);
                   g.setMiles(Double.parseDouble(milesValue));
                   g.setAmount(Double.parseDouble(gallonsValue));
                   g.setCost(Double.parseDouble(cost));
                   g.setDateRefilled(date.getTime());
                   new AsyncDBTask(db,g,"Add").execute();

                   HashMap<String, String> hashMap = new HashMap<>();
                   hashMap.put("ID", dbCount + "");
                   hashMap.put("Cost", cost);
                   hashMap.put("Miles", milesValue+" mi");
                   hashMap.put("Gallons", gallonsValue+" gal");
                   hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                   hashMap.put("Date Long",Long.toString(date.getTime()));
                   hashMap.put("MPG", String.format("%.2f", (Double.parseDouble(milesValue) / Double.parseDouble(gallonsValue)))+ " MPG");
                   arrayList.add(0,hashMap);
               }
               //Update existing Gas Entry
               else
               {
                   HashMap<String, String> hashMap = (HashMap<String,String>) adapter.getItem(editPosition);
                   hashMap.put("Cost", cost);
                   hashMap.put("Miles", milesValue+" mi");
                   hashMap.put("Gallons", gallonsValue+" gal");
                   hashMap.put("Date",new SimpleDateFormat("MM/dd/yyyy").format(date));
                   hashMap.put("Date Long",Long.toString(date.getTime()));
                   hashMap.put("MPG", String.format("%.2f", (Double.parseDouble(milesValue) / Double.parseDouble(gallonsValue)))+ " MPG");
                   new AsyncDBTask(db,new Gas(Integer.valueOf(hashMap.get("ID")),Double.parseDouble(cost),Double.parseDouble(gallonsValue),Double.parseDouble(milesValue),date.getTime()),"Update").execute();
               }
               updateProgressBar();
               Collections.sort(arrayList, new MapComparator("Date"));
               adapter.notifyDataSetChanged();
           }
       });
       return view;
    }

    //Generates the view objects of the menu and inflate(render) them into the screen
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    //Called when an item inside context menu is selected
    public boolean onContextItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.Delete)
        {
            //getMenuInfo gets a reference to the item in the listview that was long pressed when opening the context menu
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            //info.position is the position of the listview item on the visible screen. It does not reflect the actual position
            //inside the arraylist. For example, lets say a screen can only fit 10 items so the position will range from 1-10.
            int index = info.position;
            HashMap<String,String> hashMap=new HashMap<>();
            //getItem will get a reference to data stored in the ArrayList. This data is a HashMap. We cast it below because
            //getItem is returning a plain object. By casting, we are telling the compiler that the object is a HashMap object.
            hashMap = (HashMap<String,String>) adapter.getItem(index);
            //db.deleteEntry(new Gas(Integer.valueOf(hashMap.get("ID")),0,0,0,0));
            new AsyncDBTask(db,new Gas(Integer.valueOf(hashMap.get("ID")),0,0,0,0),"Delete");
            updateProgressBar();
            arrayList.remove(adapter.getItem(index));
            adapter.notifyDataSetChanged();
        }
        else if(item.getItemId() == R.id.Edit)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            HashMap<String,String> hashMap = (HashMap<String,String>) adapter.getItem(index);

            updateFlag = 1;
            editPosition = index;

            Bundle b = new Bundle();
            b.putString("ID",hashMap.get("ID"));
            b.putString("Cost",hashMap.get("Cost"));
            b.putString("Miles",hashMap.get("Miles"));
            b.putString("Gallons",hashMap.get("Gallons"));
            b.putString("Date",hashMap.get("Date"));
            d.setArguments(b);
            d.show(getFragmentManager(), "fragment_gas");
        }
        else
            return false;

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar.
        inflater.inflate(R.menu.actionbar_gas_fragment, menu);
    }

    //Called when an item inside action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.SortByCost)
        {
            Collections.sort(arrayList, new MapComparator("Cost"));
            adapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.SortByMiles)
        {
            Collections.sort(arrayList, new MapComparator("Miles"));
            adapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.SortByGallons)
        {
            Collections.sort(arrayList, new MapComparator("Gallons"));
            adapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.SortByMPG)
        {
            Collections.sort(arrayList, new MapComparator("MPG"));
            adapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.SortByDate)
        {
            Collections.sort(arrayList, new MapComparator("Date"));
            adapter.notifyDataSetChanged();
            return true;
        }
        else
        {
            generateGasEntries();
        }

        return super.onOptionsItemSelected(item);
    }

    public void generateGasEntries()
    {
        HashMap<String,String> hashMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        Double cost,gallons,miles;
        int id;

        for(int i = 1; i < 14; i++)
        {
            id = (int)db.getProfilesCount(new Gas())+1;
            cal.set(Calendar.MONTH,6); cal.set(Calendar.DAY_OF_MONTH, i*2); cal.set(Calendar.YEAR, 2019);

            cost = Double.parseDouble(String.format("%.2f",(new Random().nextInt(10)+45) + new Random().nextDouble()));
            gallons = Double.parseDouble(String.format("%.2f",(new Random().nextInt(6)+10) + new Random().nextDouble()));
            miles = Double.parseDouble(String.format("%.2f",(new Random().nextInt(115)+400) + new Random().nextDouble()));

            db.addEntry(new Gas(id,cost,gallons,miles,cal.getTime().getTime()));
        }
    }

    public void updateProgressBar()
    {
        int previous_oil_total = 0, previous_brakes_total = 0, previous_wheels_total = 0, previous_battery_total = 0, previous_timingbelt_total = 0;
        List<Object> list = db.getAllEntries(new Gas());
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if(list.size() == 1)
        {
            editor.putLong("oil_checkpoint",((Gas) (list.get(0))).getDateRefilled());
            editor.putLong("brakes_checkpoint",((Gas) (list.get(0))).getDateRefilled());
            editor.putLong("wheels_checkpoint",((Gas) (list.get(0))).getDateRefilled());
            editor.putLong("battery_checkpoint",((Gas) (list.get(0))).getDateRefilled());
            editor.putLong("timingbelt_checkpoint",((Gas) (list.get(0))).getDateRefilled());
        }

        for (int i = 0; i < list.size(); i++)
        {
            double miles = ((Gas) (list.get(i))).getMiles();

            if(((Gas) (list.get(i))).getDateRefilled() >= sharedpreferences.getLong("oil_checkpoint",0))
                previous_oil_total += miles;
            if(((Gas) (list.get(i))).getDateRefilled() >= sharedpreferences.getLong("brakes_checkpoint",0))
                previous_brakes_total += miles;
            if(((Gas) (list.get(i))).getDateRefilled() >= sharedpreferences.getLong("wheels_checkpoint",0))
                previous_wheels_total += miles;
            if(((Gas) (list.get(i))).getDateRefilled() >= sharedpreferences.getLong("battery_checkpoint",0))
                previous_battery_total += miles;
            if(((Gas) (list.get(i))).getDateRefilled() >= sharedpreferences.getLong("timingbelt_checkpoint",0))
                previous_timingbelt_total += miles;
        }
        if(list.size() > 0)
        {
            if (previous_oil_total < 3000)
                editor.putFloat("oil", previous_oil_total);
            else
            {
                editor.putFloat("oil", 3000);
                scheduleNotification("Oil Replacement", "You have driven more than 3000 miles and your oil needs to be replaced. Have you replaced your oil?",1);
            }

            if (previous_brakes_total < 50000)
                editor.putFloat("brakes", previous_brakes_total);
            else
            {
                editor.putFloat("brakes", 50000);
                scheduleNotification("Brake Replacement", "You have driven more than 50000 miles and your brakes needs to be replaced. Have you replaced your brakes?",2);
            }

            if (previous_wheels_total < 15000)
                editor.putFloat("wheels", previous_wheels_total);
            else
            {
                editor.putFloat("wheels", 15000);
                scheduleNotification("Tire Replacement", "You have driven more than 15000 miles and your tires needs to be replaced. Have you replaced your tires?",3);
            }

            if (previous_battery_total < 30000)
                editor.putFloat("battery", previous_battery_total);
            else
            {
                editor.putFloat("battery", 30000);
                scheduleNotification("Battery Replacement", "You have driven more than 30000 miles and your battery needs to be replaced. Have you replaced your battery?",4);
            }

            if (previous_timingbelt_total < 100000)
                editor.putFloat("timingbelt", previous_timingbelt_total);
            else
            {
                editor.putFloat("timingbelt", 100000);
                scheduleNotification("Timing Belt Replacement", "You have driven more than 100000 miles and your timing belt needs to be replaced. Have you replaced your timing belt?",5);
            }
        }
        else
        {
            editor.putFloat("oil", previous_oil_total);
            editor.putFloat("brakes", previous_brakes_total);
            editor.putFloat("wheels", previous_wheels_total);
            editor.putFloat("battery", previous_battery_total);
            editor.putFloat("timingbelt", previous_timingbelt_total);
        }
        editor.apply();
    }

    public void scheduleNotification(String title, String message, int id)
    {
        AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("title",title);
        intent.putExtra("message",message);
        intent.putExtra("id",id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);

        //Schedule a repeating alarm that runs every 24 hours
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000 * 60 * 60 * 24, alarmIntent);
        // Schedule a repeating alarm that runs every minute
        // alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000 * 60 * 1, alarmIntent);
        // Schedule alarm that runs once at the given time
        //alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);
    }

    private class AsyncDBTask extends AsyncTask<Void,Void,List<Object>>
    {
        private SQLiteDatabaseHandler handler;
        private Gas g;
        private String operation;

        public AsyncDBTask(SQLiteDatabaseHandler handler,Gas g,String operation)
        {
            this.handler = handler;
            this.g = g;
            this.operation = operation;
        }
        @Override
        protected List<Object> doInBackground(Void... voids)
        {
            if(operation.equals("Add"))
            {
                handler.addEntry(g);
            }
            else if(operation.equals("Update"))
            {
                handler.updateEntry(g);
            }
            else if(operation.equals("Delete"))
            {
                handler.deleteEntry(g);
            }
            else if(operation.equals("Fetch"))
            {
                return handler.getAllEntries(new Gas());
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Object> list)
        {
            super.onPostExecute(list);
            Date date = null;

            if(list != null)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    DecimalFormat number = new DecimalFormat("0.00");
                    Double cost = Double.valueOf((((Gas)(list.get(i))).getCost()));
                    Double miles = Double.valueOf((((Gas)(list.get(i))).getMiles()));
                    Double gallons = Double.valueOf((((Gas)(list.get(i))).getAmount()));

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("ID",String.valueOf(((Gas)(list.get(i))).getID()));
                    hashMap.put("Cost", number.format(cost));
                    hashMap.put("Miles",number.format(miles)+" mi");
                    hashMap.put("Gallons",number.format(gallons)+" gal");
                    date = new Date(((Gas)(list.get(i))).getDateRefilled());
                    hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                    hashMap.put("Date Long",Long.toString(date.getTime()));
                    hashMap.put("MPG",String.format("%.2f", (((Gas)(list.get(i))).getMiles()) / (((Gas)(list.get(i))).getAmount()))+" MPG");
                    arrayList.add(0,hashMap);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //Defines the rules for comparisons that is used in Collection.sort method
    class MapComparator implements Comparator<Map<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, String> first, Map<String, String> second)
        {
            String firstValue = "", secondValue = "";

            firstValue = String.valueOf(first.get(key));
            secondValue = String.valueOf(second.get(key));

            return secondValue.compareTo(firstValue);
        }
    }
}
