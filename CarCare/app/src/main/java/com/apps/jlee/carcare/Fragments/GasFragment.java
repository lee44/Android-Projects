package com.apps.jlee.carcare.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apps.jlee.carcare.Activities.MainActivity;
import com.apps.jlee.carcare.Adapters.GasAdapter;
import com.apps.jlee.carcare.Dialog_Fragments.FilterDialogFragment;
import com.apps.jlee.carcare.Objects.Gas;
import com.apps.jlee.carcare.Dialog_Fragments.GasDialogFragment;
import com.apps.jlee.carcare.R;
import com.apps.jlee.carcare.Data.SQLiteDatabaseHandler;
import com.apps.jlee.carcare.UI.SwipeCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class GasFragment extends Fragment
{
    private GasDialogFragment d;
    private FilterDialogFragment f;
    private ArrayList<HashMap<String,String>> arrayList;
    private List<Object> gasList;
    private Gas mRecentlyDeletedItem;
    private RecyclerView rv;
    private GasAdapter adapter;
    private SQLiteDatabaseHandler db;
    private FloatingActionButton fab;
    private boolean updateFlag = false;
    private String DateFormat = "M/dd/yy";
    private int mRecentlyDeletedItemPosition;
    private long oil, brake, wheels, battery, timmingBelt;

    public GasFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        arrayList = new ArrayList<>();
        gasList = new LinkedList<>();
        d = new GasDialogFragment();
        f = new FilterDialogFragment();
        db = SQLiteDatabaseHandler.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
       View view = inflater.inflate(R.layout.fragment_gas, container, false);
       fab = view.findViewById(R.id.fab2);

       rv = view.findViewById(R.id.gas_Entries2);
       rv.setLayoutManager(new LinearLayoutManager(getContext()));
       adapter = new GasAdapter(gasList,getActivity(),this);
       rv.setAdapter(adapter);
       ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCallback(this));
       itemTouchHelper.attachToRecyclerView(rv);

       //new AsyncDBTask(db).execute();
       gasList.addAll(db.getAllEntries());
       adapter.notifyDataSetChanged();

       fab.setOnClickListener(new View.OnClickListener()
       {
            public void onClick(View view)
            {
                updateFlag = false;
                Bundle b = new Bundle();
                b.clear();
                d.setArguments(b);
                d.show(getFragmentManager(), "fragment_gas");
            }
       });

       d.setListener(new GasDialogFragment.GasInterface()
       {
           @Override
           public void onClick(int position, String milesValue, String gallonsValue, String cost, Date date)
           {
               //Insert new Gas Entry
               if(!updateFlag)
               {
                   Gas g = new Gas();
                   g.setMiles(Double.parseDouble(milesValue));
                   g.setAmount(Double.parseDouble(gallonsValue));
                   g.setCost(Double.parseDouble(cost));
                   g.setDateRefilled(date.getTime());
                   db.addEntry(g);
                   gasList.add(g);
                   Collections.sort(gasList, new MapComparator("Date"));
                   adapter.notifyDataSetChanged();
               }
               //Update existing Gas Entry
               else
               {
                    Gas g = (Gas)gasList.get(position);
                    g.setCost(Double.valueOf(cost));
                    g.setMiles(Double.valueOf(milesValue));
                    g.setAmount(Double.valueOf(gallonsValue));
                    g.setDateRefilled(date.getTime());
                    db.updateEntry(g);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemInserted(position);
               }
           }
       });

       f.setListener(new FilterDialogFragment.FilterInterface()
       {
           @Override
           public void onClick(Date starting_date, Date ending_date, String sortBy)
           {
               List<Object> list = db.getAllEntries();

               if(list.size() != 0)
               {
                   if(!sortBy.equals("Date"))
                   {
                       gasList.clear();
                       for (int i = 0; i < list.size(); i++)
                           if (starting_date.getTime() <= ((Gas) (list.get(i))).getDateRefilled() && ending_date.getTime() >= ((Gas) (list.get(i))).getDateRefilled())
                               gasList.add(list.get(i));

                       Collections.sort(gasList, new MapComparator(sortBy));
                   }
                   else
                   {
                       gasList.clear();
                       gasList.addAll(list);
                   }

                   adapter.notifyDataSetChanged();
               }
           }
       });

       rv.addOnScrollListener(new RecyclerView.OnScrollListener()
       {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && gasList.size() != 5)
                {
                    ((MainActivity)getActivity()).toggleBottomNavBarVisibility(false);
                }
                else if (dy < 0 )
                {
                    ((MainActivity)getActivity()).toggleBottomNavBarVisibility(true);

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

       return view;
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

        if(id == R.id.Filter)
        {
            f.show(getFragmentManager(), "fragment_filter");
        }
        else if (id == R.id.Email)
        {
            new AsyncExcel(db).execute();
            //email();

            //For s9, we are saving excel files on the interal storage but this storage has internal and external partitions. Internal partitions are invisible while external are not.

            //Internal Storage:
            // Files saved to the internal storage are private to your application and other applications cannot access them. When the user uninstalls your application,
            // these files are removed/deleted. Your app user also can't access them using file manager; even after enabling "show hidden files" option in file manager. To access files in
            // Internal Storage, you have to root your Android phone.

            //External Storage:
            //This can be a removable storage media (such as an SD card) or an internal (non-removable) storage

            //The following methods give paths to the external paritition of the internal storage
            //Log.v("Dodgers",Environment.getExternalStorageDirectory().getAbsolutePath());
            //Log.v("Dodgers",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());

            //Following two will give you the directory paths under the package name: Android/data/com.apps.jlee.carcare
            //Log.v("Dodgers",getContext().getExternalCacheDir().getAbsolutePath());
            //Log.v("Dodgers",getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        }
        else
        {
            generateGasEntries();
        }

        return super.onOptionsItemSelected(item);
    }

    public void generateGasEntries()
    {
        Calendar cal = Calendar.getInstance();
        Double cost,gallons,miles;

        for(int j = 11; j < 12; j++)
        {
            cal.set(Calendar.MONTH, j);
            for (int i = 0; i < 5; i++)
            {
                cal.set(Calendar.DAY_OF_MONTH, i * 2);
                cal.set(Calendar.YEAR, 2019);

                cost = Double.parseDouble(String.format("%.2f", (new Random().nextInt(10) + 45) + new Random().nextDouble()));
                gallons = Double.parseDouble(String.format("%.2f", (new Random().nextInt(6) + 10) + new Random().nextDouble()));
                miles = Double.parseDouble(String.format("%.2f", (new Random().nextInt(115) + 400) + new Random().nextDouble()));

                db.addEntry(new Gas(i + 999, cost, gallons, miles, cal.getTime().getTime()));
                gasList.add(new Gas(i + 999, cost, gallons, miles, cal.getTime().getTime()));
                //new AsyncAddDeleteTask(db,new Gas(i+999,cost,gallons,miles,cal.getTime().getTime()),"add").execute();
            }
        }
        //new AsyncDBTask(db).execute();
        //gasList.addAll(db.getAllEntries());
        Collections.sort(gasList, new MapComparator("Date"));
        adapter.notifyDataSetChanged();
    }

    /**Load Gas Entries*/
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
            return handler.getAllEntries();
        }
        @Override
        protected void onPostExecute(List<Object> list)
        {
            super.onPostExecute(list);

            if(list.size() != 0)
            {
                gasList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class AsyncAddDeleteTask extends AsyncTask<Void,Void,Void>
    {
        private SQLiteDatabaseHandler handler;
        private Object o;
        private String flag;

        public AsyncAddDeleteTask(SQLiteDatabaseHandler handler, Object o, String flag)
        {
            this.handler = handler;
            this.o = o;
            this.flag = flag;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if(flag.equals("add"))
                handler.addEntry(o);
            else if(flag.equals("delete"))
                handler.deleteEntry(o);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    /**Sort Gas Entries*/
    private class AsyncDBFilterTask extends AsyncTask<Void,Void,List<Object>>
    {
        private SQLiteDatabaseHandler handler;
        private Date starting_date, ending_date;
        private String sortBy;

        public AsyncDBFilterTask(SQLiteDatabaseHandler handler, Date starting_date, Date ending_date, String sortBy)
        {
            this.handler = handler;
            this.starting_date = starting_date;
            this.ending_date = ending_date;
            this.sortBy = sortBy;
        }

        @Override
        protected List<Object> doInBackground(Void... voids)
        {
            return handler.getAllEntries();
        }

        @Override
        protected void onPostExecute(List<Object> list)
        {
            super.onPostExecute(list);
            Date date = null;

            if(list.size() != 0)
            {
                gasList.clear();
                for (int i = 0; i < list.size(); i++)
                    if (starting_date.getTime() <= ((Gas) (list.get(i))).getDateRefilled() && ending_date.getTime() >= ((Gas) (list.get(i))).getDateRefilled())
                        gasList.add(list.get(i));

                Collections.sort(gasList, new MapComparator(sortBy));
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**Load Gas Entries and Generate Excel File*/
    private class AsyncExcel extends AsyncTask<Void,Void,List<Object>>
    {
        private SQLiteDatabaseHandler handler;

        public AsyncExcel(SQLiteDatabaseHandler handler)
        {
            this.handler = handler;
        }

        @Override
        protected List<Object> doInBackground(Void... voids)
        {
            return handler.getAllEntries();
        }

        @Override
        protected void onPostExecute(List<Object> list)
        {
            super.onPostExecute(list);

            if(list.size() != 0)
            {
                long yourmilliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_HH_mm_ss");
                Date resultdate = new Date(yourmilliseconds);

                String Fnamexls = "excelSheet"+sdf.format(resultdate)+ ".xls";
                File sdCard = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File directory = new File(sdCard.getAbsolutePath() + "/Gas_Entries");
                if (!directory.mkdirs()) ;
                    //Log.v("Dodgers", "Directory not created");

                File file = new File(directory, Fnamexls);

                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));

                WritableWorkbook workbook;
                try
                {
                    workbook = Workbook.createWorkbook(file, wbSettings);
                    WritableSheet sheet = workbook.createSheet("First Sheet", 0);

                    Label label = new Label(0, 0, "Date");
                    Label label1 = new Label(1,0,"Cost");
                    Label label2 = new Label(2,0,"Miles");
                    Label label3 = new Label(3,0,"Gallons");
                    Label label4 = new Label(4,0,"MPG");

                    try
                    {
                        sheet.addCell(label);
                        sheet.addCell(label1);
                        sheet.addCell(label2);
                        sheet.addCell(label3);
                        sheet.addCell(label4);
                    }
                    catch (RowsExceededException e) {e.printStackTrace();}
                    catch (WriteException e) {e.printStackTrace();}

                    for (int i = 0; i < list.size(); i++)
                    {
                        try
                        {
                            Date date = new Date(((Gas) (list.get(i))).getDateRefilled());
                            Label l = new Label(0, i + 1, new SimpleDateFormat(DateFormat).format(date));
                            Label l1 = new Label(1, i + 1, String.valueOf(((Gas) (list.get(i))).getCost()));
                            Label l2 = new Label(2, i + 1, String.valueOf(((Gas) (list.get(i))).getMiles()));
                            Label l3 = new Label(3, i + 1, String.valueOf(((Gas) (list.get(i))).getAmount()));
                            Label l4 = new Label(4, i + 1, String.format("%.2f", (((Gas) (list.get(i))).getMiles()) / (((Gas) (list.get(i))).getAmount())));

                            sheet.addCell(l);
                            sheet.addCell(l1);
                            sheet.addCell(l2);
                            sheet.addCell(l3);
                            sheet.addCell(l4);
                        } catch (RowsExceededException e)
                        {
                            e.printStackTrace();
                        } catch (WriteException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    workbook.write();
                    try
                    {
                        workbook.close();
                    } catch (WriteException e) {e.printStackTrace();}

                } catch (IOException e) {e.printStackTrace();}

                //Initate the BroadcastReceiver
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
                EmailBroadCastReceiver emailBroadCastReceiver = new EmailBroadCastReceiver();
                //Intentfilter specify the kind of intents that the component can receive. In this case, the emailBroadCastReceiver(component) will only receive intents with the
                // action "com.action.email"
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.action.email");
                manager.registerReceiver(emailBroadCastReceiver,filter);

                //Broadcast the intent
                Intent i = new Intent("com.action.email");
                i.putExtra("file_path",directory.toString());
                i.putExtra("file_name",Fnamexls);
                manager.sendBroadcast(i);
            }
        }
    }

    //This broadcastreceiver will be used locally instead of globally across the system and called by the AsyncExcel
    class EmailBroadCastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            //Log.v("Dodgers",intent.getStringExtra("file_path") + "/" + intent.getStringExtra("file_name"));
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/vnd.ms-excel");
            //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jon@example.com"}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gas Entries");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Here are all the gas entries recorded");
            emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(),"com.mydomain.fileprovider",new File(intent.getStringExtra("file_path"),intent.getStringExtra("file_name"))));
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    //Defines the rules for comparisons that is used in Collection.sort method
    class MapComparator implements Comparator<Object>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        @Override
        public int compare(Object first, Object second)
        {
            int v = 0;

            switch (key)
            {
                case "MPG": v = (((Gas)(second)).getMiles()/((Gas)(second)).getAmount()+"").compareTo(((Gas)(first)).getMiles()/((Gas)(first)).getAmount()+""); break;
                case "Cost": v = (((Gas)(second)).getCost()+"").compareTo((((Gas)(first)).getCost()+"")); break;
                case "Miles": v = (((Gas)(second)).getMiles()+"").compareTo((((Gas)(first)).getMiles()+"")); break;
                case "Gallons": v = (((Gas)(second)).getAmount()+"").compareTo((((Gas)(first)).getAmount()+"")); break;
                case "Date": v = (((Gas)(second)).getDateRefilled()+"").compareTo((((Gas)(first)).getDateRefilled()+""));
            }
            return v;
        }
    }

    /**Deletes items on left swipe*/
    public void deleteItem(int position)
    {
        mRecentlyDeletedItem = (Gas)gasList.get(position);
        mRecentlyDeletedItemPosition = position;

        //new AsyncAddDeleteTask(db,mRecentlyDeletedItem,"delete").execute();
        db.deleteEntry(mRecentlyDeletedItem);
        gasList.remove(position);
        adapter.notifyItemRemoved(position);
        ((MainActivity)getActivity()).toggleBottomNavBarVisibility(true);
        showUndoSnackbar();

        if(db.getProfilesCount() == 0)
        {
            SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
            oil = sharedpreferences.getLong("oil_checkpoint",0);
            brake = sharedpreferences.getLong("brakes_checkpoint",0);
            wheels = sharedpreferences.getLong("wheels_checkpoint",0);
            battery = sharedpreferences.getLong("battery_checkpoint",0);
            timmingBelt = sharedpreferences.getLong("timingbelt_checkpoint",0);
            sharedpreferences.edit().clear().apply();
        }
    }

    /**Deletes selected items*/
    public void deleteSelectedItems()
    {
        for(int i = 0; i < gasList.size(); i++)
        {
            if(((Gas) (gasList.get(i))).showChecked)
            {
                db.deleteEntry(gasList.get(i));
                gasList.remove(i);
                adapter.notifyItemRemoved(i);
                i--;
            }
        }
    }

    private void undoDelete()
    {
        gasList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        db.addEntry(mRecentlyDeletedItem);
        //new AsyncAddDeleteTask(db,mRecentlyDeletedItem,"add").execute();
        adapter.notifyItemInserted(mRecentlyDeletedItemPosition);

        if(db.getProfilesCount() == 0)
        {
            SharedPreferences sharedpreferences = getContext().getSharedPreferences("Replacement Values", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong("oil_checkpoint",oil);
            editor.putLong("brakes_checkpoint",brake);
            editor.putLong("wheels_checkpoint",wheels);
            editor.putLong("battery_checkpoint",battery);
            editor.putLong("timingbelt_checkpoint",timmingBelt);
            editor.apply();
        }
    }

    /**Cancels the selection mode*/
    public void cancel()
    {
        for(int i = 0; i < gasList.size(); i++)
        {
            ((Gas)(gasList.get(i))).showCheckbox = false;
            ((Gas)(gasList.get(i))).showChecked = false;
            adapter.notifyItemChanged(i);
        }
    }

    private void showUndoSnackbar()
    {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.root), R.string.snack_bar_text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Undo",v -> undoDelete());
        snackbar.show();
    }

    /**Edit item on right swipe*/
    public void edit(int position)
    {
        updateFlag = true;

        Bundle b = new Bundle();
        b.putString("Position",position+"");
        b.putString("ID",((Gas)gasList.get(position)).getID()+"");
        b.putString("Cost",((Gas)gasList.get(position)).getCost()+"");
        b.putString("Miles",((Gas)gasList.get(position)).getMiles()+"");
        b.putString("Gallons",((Gas)gasList.get(position)).getAmount()+"");
        b.putString("Date",((Gas)gasList.get(position)).getDateRefilled()+"");
        d.setArguments(b);
        d.show(getFragmentManager(), "fragment_gas");
    }

    /**Selects or Deselects all CheckBoxes*/
    public void toggleSelectAllCheckBoxes()
    {
        int size = gasList.size();
        boolean select_all = false;

        for(int i = 0; i < size; i++)
        {
            if(!((Gas)(gasList.get(i))).showChecked)
            {
                select_all = true;
                break;
            }
        }

        if(select_all)
        {
            for(int i = 0; i < size; i++)
            {
                ((Gas)(gasList.get(i))).showChecked = true;
                adapter.notifyItemChanged(i);
            }
            ((MainActivity)getActivity()).setSelected(size);
        }
        else
        {
            for(int i = 0; i < size; i++)
            {
                ((Gas)(gasList.get(i))).showChecked = false;
                adapter.notifyItemChanged(i);
            }
            ((MainActivity)getActivity()).setSelected(0);
        }
    }

    /**Cancels the selection mode*/
    public void resetCheckBoxes()
    {
        for(int i = 0; i < gasList.size(); i++)
        {
            ((Gas)(gasList.get(i))).showCheckbox = false;
            ((Gas)(gasList.get(i))).showChecked = false;
            adapter.notifyItemChanged(i);
        }
    }

    /**
     * Reveals or Hides Floating Action Button
     */
    public void toggleFloatingActionButton()
    {
        if(fab.getVisibility() == View.GONE)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
    }
}