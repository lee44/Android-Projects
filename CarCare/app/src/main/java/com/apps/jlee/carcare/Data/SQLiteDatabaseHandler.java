package com.apps.jlee.carcare.Data;

import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.apps.jlee.carcare.Objects.Gas;

//In order to use the SQLite class, you need to create a helper class that is a child(extends) SQLiteOpenHelper
//With the class below, we are defining database operations like delete, update, select, etc
public class SQLiteDatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CarCareDB";
    private static final String CREATION_TABLE_GAS = "CREATE TABLE IF NOT EXISTS Gas ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "cost DOUBLE, " + "amount DOUBLE, " + "miles DOUBLE, " + "date INTEGER )";
    private static final String TABLE_NAME = "Gas", KEY_ID = "id", KEY_COST = "cost", KEY_AMOUNT = "amount", KEY_MILES = "miles", KEY_DATE = "date";
    private static final String[] COLUMNS = {KEY_ID, KEY_COST, KEY_AMOUNT, KEY_MILES, KEY_DATE};
    private Context c;
    private static SQLiteDatabaseHandler sInstance;

    public SQLiteDatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = context;
    }

    public static synchronized SQLiteDatabaseHandler getInstance(Context context)
    {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null)
        {
            sInstance = new SQLiteDatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATION_TABLE_GAS);
    }

    public void addEntry(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_COST, ((Gas) o).getCost());
        values.put(KEY_AMOUNT, ((Gas) o).getAmount());
        values.put(KEY_MILES, ((Gas) o).getMiles());
        values.put(KEY_DATE, ((Gas) o).getDateRefilled());
        db.insert(TABLE_NAME, null, values);
    }

    public List<Object> getAllEntries()
    {
        List<Object> list = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do
            {
                Gas gas = new Gas();
                gas.setID(Integer.parseInt(cursor.getString(0)));
                gas.setCost(Double.parseDouble(cursor.getString(1)));
                gas.setAmount(Double.parseDouble(cursor.getString(2)));
                gas.setMiles(Double.parseDouble(cursor.getString(3)));
                gas.setDateRefilled(Long.parseLong(cursor.getString(4)));
                list.add(gas);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int updateEntry(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int i;

        values.put(KEY_COST, ((Gas)o).getCost());
        values.put(KEY_AMOUNT, ((Gas)o).getAmount());
        values.put(KEY_MILES, ((Gas)o).getMiles());
        values.put(KEY_DATE, ((Gas)o).getDateRefilled());

        i = db.update(
            TABLE_NAME, // table
            values, // column/value
            "id = ?", // selections
            new String[]{String.valueOf(((Gas)o).getID())});

        return i;
    }
    public void deleteEntry(Object o)
    {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(((Gas)o).getID())});
    }
    public long getProfilesCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public Gas getEntry(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor is a pointer to a data set
        // e.g [1,55.00,15,495.00,'2018-10-15'],[2,52.00,16,475.00,'2018-11-15']
        Cursor cursor = db.query(
                TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args(Where condition)
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Gas gas = new Gas();
        gas.setID(Integer.parseInt(cursor.getString(0)));
        gas.setCost(Double.parseDouble(cursor.getString(1)));
        gas.setAmount(Double.parseDouble(cursor.getString(2)));
        gas.setMiles(Double.parseDouble(cursor.getString(3)));
        gas.setDateRefilled(Integer.parseInt(cursor.getString(4)));

        return gas;
    }

    public void deleteTable(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        this.onCreate(db);
    }

    public List<Gas> sortEntries(String condition)
    {
        List<Gas> gasList = new LinkedList<Gas>();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + condition + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Gas gas = null;

        if (cursor.moveToFirst())
        {
            do
            {
                gas = new Gas();
                gas.setID(Integer.parseInt(cursor.getString(0)));
                gas.setCost(Double.parseDouble(cursor.getString(1)));
                gas.setAmount(Double.parseDouble(cursor.getString(2)));
                gas.setMiles(Double.parseDouble(cursor.getString(3)));
                gas.setDateRefilled(Integer.parseInt(cursor.getString(4)));
                gasList.add(gas);
            } while (cursor.moveToNext());
        }
        return gasList;
    }
}
