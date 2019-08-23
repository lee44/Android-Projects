package com.apps.jlee.carcare.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.apps.jlee.carcare.Objects.Oil;
import com.apps.jlee.carcare.Dialog_Fragments.OilDialogFragment;
import com.apps.jlee.carcare.R;
import com.apps.jlee.carcare.Data.SQLiteDatabaseHandler;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
        adapter = new SimpleAdapter(getContext(),arrayList,R.layout.oil_listview_items, new String[]{"Date","Oil_Name","Oil_Amount","Mileage"},new int[]{R.id.Date,R.id.oil_name,R.id.oil_amount,R.id.Mileage});
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
            public void onClick(String oil_name, String oil_amount, String mileage, Date date)
            {
                int dbCount = (int)db.getProfilesCount(new Oil())+1;

                if(updateFlag == 0)
                {
                    Oil o = new Oil();
                    o.setID(dbCount);
                    o.setOilName(oil_name);
                    o.setOilAmount(Double.parseDouble(oil_amount));
                    o.setMileage(Double.parseDouble(mileage));
                    o.setDate(new SimpleDateFormat(dbDateFormat).format(date));
                    db.addEntry(o);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", String.valueOf(dbCount));
                    hashMap.put("Oil_Name", oil_name);
                    hashMap.put("Oil_Amount", String.valueOf(oil_amount));
                    hashMap.put("Miles",mileage+"");
                    DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                    hashMap.put("Mileage", formatter.format(Double.valueOf(mileage)) + " MI");
                    hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                    arrayList.add(0, hashMap);
                }
                else
                {
                    HashMap<String, String> hashMap = (HashMap<String,String>) adapter.getItem(editPosition);
                    hashMap.put("Oil_Name", oil_name);
                    hashMap.put("Oil_Amount", String.valueOf(oil_amount));
                    hashMap.put("Miles",mileage+"");
                    DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                    hashMap.put("Mileage", formatter.format(Double.valueOf(mileage)) + " MI");
                    hashMap.put("Date",new SimpleDateFormat("MM/dd/yyyy").format(date));
                    db.updateEntry(new Oil(Integer.valueOf(hashMap.get("ID")),oil_name,Double.parseDouble(oil_amount),Double.parseDouble(mileage),new SimpleDateFormat(dbDateFormat).format(date)));
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
            b.putString("oil_name",hashMap.get("Oil_Name"));
            b.putString("oil_amount",hashMap.get("Oil_Amount"));
            b.putString("Mileage",hashMap.get("Miles"));
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

        if(list.size() != 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                DecimalFormat number = new DecimalFormat("0.00");
                Double oil_Amount = Double.valueOf(((Oil)(list.get(i))).getOilAmount());

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ID",String.valueOf(((Oil)(list.get(i))).getID()));
                hashMap.put("Oil_Name", String.valueOf(((Oil)(list.get(i))).getOilName()));
                hashMap.put("Oil_Amount", number.format(oil_Amount));
                DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                Double mileage = Double.valueOf(((Oil)(list.get(i))).getMileage());
                hashMap.put("Miles",mileage+"");
                hashMap.put("Mileage",formatter.format(mileage)+" MI");

                try{ date = new SimpleDateFormat(dbDateFormat).parse(((Oil)(list.get(i))).getDate());
                } catch (ParseException e) { e.printStackTrace();}

                hashMap.put("Date", new SimpleDateFormat("MM/dd/yyyy").format(date));
                //hashMap.put("Details"," " + ((Oil)(list.get(i))).getOilName()+"\n "+((Oil)(list.get(i))).getOilAmount() + " quarts");
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
