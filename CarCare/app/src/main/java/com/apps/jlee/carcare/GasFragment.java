package com.apps.jlee.carcare;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private String dbDateFormat = "yyyy-MM-dd HH:mm:ss";

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
       adapter = new SimpleAdapter(getContext(),arrayList,R.layout.gas_listview_items, new String[]{"Date","Details","MPG"},new int[]{R.id.Date,R.id.Details,R.id.MPG});
       listView.setAdapter(adapter);

       loadGasEntries();

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
                   g.setDateRefilled(new SimpleDateFormat(dbDateFormat).format(date));
                   db.addEntry(g);

                   HashMap<String, String> hashMap = new HashMap<>();
                   hashMap.put("ID", dbCount + "");
                   hashMap.put("Cost", cost);
                   hashMap.put("Miles", milesValue);
                   hashMap.put("Gallons", gallonsValue);
                   hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                   hashMap.put("MPG", String.format("%.2f", (Double.parseDouble(milesValue) / Double.parseDouble(gallonsValue)))+ " MPG");
                   //hashMap.put("Details", "Cost: $" + cost + "\nMiles: " + milesValue + "\nGallons: " + gallonsValue);
                   hashMap.put("Details"," " + cost + "\n " + milesValue + " mi\n " + gallonsValue + " gal");
                   arrayList.add(0,hashMap);
               }
               //Update existing Gas Entry
               else
               {
                   HashMap<String, String> hashMap = new HashMap<>();
                   hashMap = (HashMap<String,String>) adapter.getItem(editPosition);
                   hashMap.put("Cost", cost);
                   hashMap.put("Miles", milesValue);
                   hashMap.put("Gallons", gallonsValue);
                   hashMap.put("Date",new SimpleDateFormat("MM/dd/yyyy").format(date));
                   hashMap.put("MPG", String.format("%.2f", (Double.parseDouble(milesValue) / Double.parseDouble(gallonsValue)))+ " MPG");
                   //hashMap.put("Details", "Cost: $" + cost + "\nMiles: " + milesValue + "\nGallons: " + gallonsValue);
                   hashMap.put("Details"," " + cost + "\n " + milesValue + " mi\n " + gallonsValue + " gal");

                   db.updateEntry(new Gas(Integer.valueOf(hashMap.get("ID")),Double.valueOf(cost),Double.valueOf(gallonsValue),Double.valueOf(milesValue),new SimpleDateFormat(dbDateFormat).format(date)));
               }
               Collections.sort(arrayList, new MapComparator("Date"));
               adapter.notifyDataSetChanged();
               updateProgressBar();
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
            db.deleteEntry(new Gas(Integer.valueOf(hashMap.get("ID")),0,0,0,""));
            arrayList.remove(adapter.getItem(index));
            adapter.notifyDataSetChanged();
            updateProgressBar();
        }
        else if(item.getItemId() == R.id.Edit)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap = (HashMap<String,String>) adapter.getItem(index);

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

        return super.onOptionsItemSelected(item);
    }

    //Load saved Gas entries
    public void loadGasEntries()
    {
        List<Object> list = db.getAllEntries(new Gas());
        Date date = null;

        if(list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ID",String.valueOf(((Gas)(list.get(i))).getID()));
                hashMap.put("Cost", String.valueOf(((Gas)(list.get(i))).getCost()));
                hashMap.put("Miles",String.valueOf(((Gas)(list.get(i))).getMiles()));
                hashMap.put("Gallons",String.valueOf(((Gas)(list.get(i))).getAmount()));

                try {
                    date = new SimpleDateFormat(dbDateFormat).parse(((Gas)(list.get(i))).getDateRefilled());
                } catch (ParseException e) { e.printStackTrace();}

                hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                hashMap.put("MPG",String.format("%.2f", (((Gas)(list.get(i))).getMiles()) / (((Gas)(list.get(i))).getAmount()))+" MPG");
                //hashMap.put("Details", "Cost: $" + (((Gas)(list.get(i))).getCost() + "\nMiles: " + (((Gas)(list.get(i))).getMiles() + "\nGallons: " + (((Gas)(list.get(i))).getAmount()))));
                hashMap.put("Details"," " + (((Gas)(list.get(i))).getCost() + "\n " + (((Gas)(list.get(i))).getMiles() + " mi\n " + (((Gas)(list.get(i))).getAmount()))) + " gal");
                arrayList.add(0,hashMap);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void generateGasEntries()
    {
        db.addEntry(new Gas((int)db.getProfilesCount(new Gas())+1,(double)(new Random().nextInt(20)+45),(double)(new Random().nextInt(6)+10),(double)(new Random().nextInt(100)+400),"Mon Nov 26 15:24:54 PST 2018"));
        db.addEntry(new Gas((int)db.getProfilesCount(new Gas())+1,(double)(new Random().nextInt(20)+45),(double)(new Random().nextInt(6)+10),(double)(new Random().nextInt(100)+400),"Fri Nov 30 15:24:54 PST 2018"));
    }

    public void updateProgressBar()
    {
        int total = 0;
        List<Object> list = db.getAllEntries(new Gas());
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        for (int i = 0; i < list.size(); i++)
            total += ((Gas) (list.get(i))).getMiles();

        if ((total % 3001) - sharedpreferences.getInt("oil",0) + sharedpreferences.getInt("oil",0) <= 3000)
            editor.putInt("oil", total % 3001);
        else
            editor.putInt("oil", 3000);

        if ((total % 50001) - sharedpreferences.getInt("brakes",0) + sharedpreferences.getInt("brakes",0) <= 50000)
            editor.putInt("brakes", total % 50001);
        else
            editor.putInt("brakes", 50000);

        if ((total % 15001) - sharedpreferences.getInt("wheels",0) + sharedpreferences.getInt("wheels",0) <= 15000)
            editor.putInt("wheels", total % 15001);
        else
            editor.putInt("wheels", 15000);

        if ((total % 30001) - sharedpreferences.getInt("battery",0) + sharedpreferences.getInt("battery",0) <= 30000)
            editor.putInt("battery", total % 30001);
        else
            editor.putInt("battery", 30000);

        if ((total % 100001) - sharedpreferences.getInt("timingbelt",0) + sharedpreferences.getInt("timingbelt",0) <= 100000)
            editor.putInt("timingbelt", total % 100001);
        else
            editor.putInt("timingbelt", 100000);

        editor.apply();
    }

    public void scheduleNotification(String title, String message)
    {
        AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("title",title);
        intent.putExtra("message",message);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 30);

        alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);
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

            if(key == "Date")
            {
                try
                {
                    Date d1 = new SimpleDateFormat("MM/dd/yyyy").parse(first.get(key));
                    Date d2 = new SimpleDateFormat("MM/dd/yyyy").parse(second.get(key));
                    return d2.compareTo(d1);
                }catch(ParseException e){}
            }
            else
            {
                firstValue = String.valueOf(first.get(key));
                secondValue = String.valueOf(second.get(key));
            }

            return secondValue.compareTo(firstValue);
        }
    }
}
