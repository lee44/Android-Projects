package com.apps.jlee.carcare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class OilFragment extends Fragment
{
    private ListView listView;
    private ArrayList<HashMap<String,String>> arrayList;
    private SimpleAdapter adapter;
    private OilDialogFragment d;
    private SQLiteDatabaseHandler db;
    private int updateFlag = 0, editPosition;
    private String dbDateFormat = "yyyy-MM-dd HH:mm:ss";

    public OilFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_oil, container, false);
        FloatingActionButton fab = view.findViewById(R.id.oilFab);

        d = new OilDialogFragment();
        db = new SQLiteDatabaseHandler(getContext());
        listView = view.findViewById(R.id.oilListView);
        arrayList = new ArrayList<>();
        adapter = new SimpleAdapter(getContext(),arrayList,R.layout.oil_listview_items, new String[]{"Date","Details","Mileage"},new int[]{R.id.Date,R.id.Details,R.id.Mileage});
        listView.setAdapter(adapter);

        loadOilEntries();
        registerForContextMenu(listView);

        fab.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                updateFlag = 0;

                Bundle b = new Bundle();
                b.putString("oil_name","");
                b.putString("oil_amount","");
                b.putString("Mileage","");
                b.putString("Date","");
                d.setArguments(b);
                d.show(getFragmentManager(), "fragment_oil");
            }
        });

        d.setListener(new OilDialogFragment.OilInterface()
        {
            @Override
            public void onClick(String oil_name, double oil_amount, double mileage, Date date)
            {
                int dbCount = (int)db.getProfilesCount(new Oil())+1;

                if(updateFlag == 0)
                {
                    Oil o = new Oil();
                    o.setID(dbCount);
                    o.setOilName(oil_name);
                    o.setOilAmount(oil_amount);
                    o.setMileage(mileage);
                    o.setDate(new SimpleDateFormat(dbDateFormat).format(date));
                    db.addEntry(o);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", String.valueOf(dbCount));
                    hashMap.put("oil_name", oil_name);
                    hashMap.put("oil_amount", String.valueOf(oil_amount));
                    DecimalFormat formatter = new DecimalFormat("#,###,###.##");
                    hashMap.put("Mileage", formatter.format(mileage) + " mi");
                    hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                    hashMap.put("Details"," " + oil_name + "\n " + oil_amount + " quarts");
                    arrayList.add(0, hashMap);
                }
                else
                {
                    HashMap<String, String> hashMap = (HashMap<String,String>) adapter.getItem(editPosition);
                    hashMap.put("oil_name", oil_name);
                    hashMap.put("oil_amount", String.valueOf(oil_amount));
                    DecimalFormat formatter = new DecimalFormat("#,###,###.##");
                    hashMap.put("Mileage", formatter.format(mileage) + " mi");
                    hashMap.put("Date",new SimpleDateFormat("MM/dd/yyyy").format(date));
                    hashMap.put("Details"," " + oil_name + "\n " + oil_amount + " quarts");

                    db.updateEntry(new Oil(Integer.valueOf(hashMap.get("ID")),oil_name,oil_amount,mileage,new SimpleDateFormat(dbDateFormat).format(date)));
                }
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            HashMap<String,String> hashMap = (HashMap<String,String>) adapter.getItem(index);
            db.deleteEntry(new Oil(Integer.valueOf(hashMap.get("ID")),"",0,0,""));
            arrayList.remove(adapter.getItem(index));
            adapter.notifyDataSetChanged();
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
            b.putString("oil_name",hashMap.get("oil_name"));
            b.putString("oil_amount",hashMap.get("oil_amount"));
            StringTokenizer tokenizer = new StringTokenizer(hashMap.get("Mileage"),", mil");

            String s = "";
            while(tokenizer.hasMoreTokens())
            {
                s = s+tokenizer.nextToken();
            }
            b.putString("Mileage",s);
            b.putString("Date",hashMap.get("Date"));
            d.setArguments(b);
            d.show(getFragmentManager(), "fragment_oil");
        }
        else
            return false;

        return true;
    }

    public void loadOilEntries()
    {
        List<Object> list = db.getAllEntries(new Oil());
        Date date = null;

        if(list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ID",String.valueOf(((Oil)(list.get(i))).getID()));
                hashMap.put("oil_name", String.valueOf(((Oil)(list.get(i))).getOilName()));
                hashMap.put("oil_amount",String.valueOf(((Oil)(list.get(i))).getOilAmount()));
                DecimalFormat formatter = new DecimalFormat("#,###,###.##");
                hashMap.put("Mileage",formatter.format(((Oil)(list.get(i))).getMileage())+" mi");

                try{ date = new SimpleDateFormat(dbDateFormat).parse(((Oil)(list.get(i))).getDate());
                } catch (ParseException e) { e.printStackTrace();}

                hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                hashMap.put("Details"," " + ((Oil)(list.get(i))).getOilName()+"\n "+((Oil)(list.get(i))).getOilAmount() + " quarts");
                arrayList.add(0,hashMap);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void generateRandomOilEntries()
    {
        String[] brands = {"Mobil 1 Extended Performance","Castrol GTX MAGNATEC","Royal Purple HMX","Valvoline Premium"};
        db.addEntry(new Oil((int)db.getProfilesCount(new Oil())+1,brands[new Random().nextInt(4)],new Random().nextInt(5)+1,new Random().nextInt(1000000),new SimpleDateFormat("MM / dd / yyyy").format(Calendar.getInstance().getTime())));
    }
}
