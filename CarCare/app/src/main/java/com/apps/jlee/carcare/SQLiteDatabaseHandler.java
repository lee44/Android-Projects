package com.apps.jlee.carcare;

import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//In order to use the SQLite class, you need to create a helper class that is a child(extends) SQLiteOpenHelper
//With the class below, we are defining database operations like delete, update, select, etc
public class SQLiteDatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CarCareDB";
    private static final String CREATION_TABLE_GAS = "CREATE TABLE IF NOT EXISTS Gas ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "cost DOUBLE, " + "amount DOUBLE, " + "miles DOUBLE, " + "date DATE )";
    private static final String CREATION_TABLE_OIL = "CREATE TABLE IF NOT EXISTS Oil ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "amount DOUBLE, " + "mileage DOUBLE, " + "date DATE )";
    private static final String TABLE_NAME = "Gas", KEY_ID = "id", KEY_COST = "cost", KEY_AMOUNT = "amount", KEY_MILES = "miles", KEY_DATE = "date";
    private static final String[] COLUMNS = {KEY_ID, KEY_COST, KEY_AMOUNT, KEY_MILES, KEY_DATE};
    private static final String OIL_TABLE_NAME = "Oil", OIL_KEY_ID = "id", OIL_KEY_NAME = "name", OIL_KEY_AMOUNT = "amount", OIL_KEY_MILEAGE = "mileage", OIL_KEY_DATE = "date";
    private Context c;

    public SQLiteDatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATION_TABLE_GAS);
        db.execSQL(CREATION_TABLE_OIL);
    }
    public void addEntry(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(o instanceof Gas)
        {
            values.put(KEY_COST, ((Gas) o).getCost());
            values.put(KEY_AMOUNT, ((Gas) o).getAmount());
            values.put(KEY_MILES, ((Gas) o).getMiles());
            values.put(KEY_DATE, ((Gas) o).getDateRefilled());
            db.insert(TABLE_NAME, null, values);
        }
        else
        {
            values.put(OIL_KEY_NAME, ((Oil) o).getOilName());
            values.put(OIL_KEY_AMOUNT, ((Oil) o).getOilAmount());
            values.put(OIL_KEY_MILEAGE, ((Oil) o).getMileage());
            values.put(OIL_KEY_DATE, ((Oil) o).getDate());
            db.insert(OIL_TABLE_NAME, null, values);
        }
        db.close();
    }
    public List<Object> getAllEntries(Object o)
    {
        List<Object> list = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        if(o instanceof Gas)
        {
            String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_DATE;
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
                    gas.setDateRefilled(cursor.getString(4));
                    list.add(gas);
                } while (cursor.moveToNext());
            }
        }
        else
        {
            String query = "SELECT * FROM " + OIL_TABLE_NAME + " ORDER BY " + OIL_KEY_DATE;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    Oil oil = new Oil();
                    oil.setID(Integer.parseInt(cursor.getString(0)));
                    oil.setOilName(cursor.getString(1));
                    oil.setOilAmount(Double.parseDouble(cursor.getString(2)));
                    oil.setMileage(Double.parseDouble(cursor.getString(3)));
                    oil.setDate(cursor.getString(4));
                    list.add(oil);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }
    public int updateEntry(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int i;

        if(o instanceof Gas)
        {
            values.put(KEY_COST, ((Gas)o).getCost());
            values.put(KEY_AMOUNT, ((Gas)o).getAmount());
            values.put(KEY_MILES, ((Gas)o).getMiles());
            values.put(KEY_DATE, ((Gas)o).getDateRefilled().toString());

            i = db.update(
                TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[]{String.valueOf(((Gas)o).getID())});
        }
        else
        {
            values.put(OIL_KEY_NAME,((Oil)o).getOilName());
            values.put(OIL_KEY_AMOUNT,((Oil)o).getOilAmount());
            values.put(OIL_KEY_MILEAGE,((Oil)o).getMileage());
            values.put(OIL_KEY_DATE,((Oil)o).getDate());

            i = db.update(
                OIL_TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[]{String.valueOf(((Oil)o).getID())});
        }
        db.close();

        return i;
    }
    public void deleteEntry(Object o)
    {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        if(o instanceof Gas)
            db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(((Gas)o).getID())});
        else
            db.delete(OIL_TABLE_NAME, "id = ?", new String[]{String.valueOf(((Oil)o).getID())});

        db.close();
    }
    public long getProfilesCount(Object o)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        long count;

        if(o instanceof Gas)
            count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        else
            count = DatabaseUtils.queryNumEntries(db, OIL_TABLE_NAME);

        db.close();
        return count;
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
        gas.setDateRefilled(cursor.getString(4));

        return gas;
    }
    public void deleteTable(Object o)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        if(o instanceof Gas)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        else
            db.execSQL("DROP TABLE IF EXISTS " + OIL_TABLE_NAME);

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
                gas.setDateRefilled(cursor.getString(4));
                gasList.add(gas);
            } while (cursor.moveToNext());
        }
        return gasList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }
}
