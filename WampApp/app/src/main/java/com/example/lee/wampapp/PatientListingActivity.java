package com.example.lee.wampapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.wampapp.helper.CheckNetworkStatus;
import com.example.lee.wampapp.helper.HttpJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientListingActivity extends AppCompatActivity
{
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String BASE_URL = "http://192.168.1.136:5559/";
    private ArrayList<HashMap<String, String>> patientList;
    private ListView patientListView;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_listing);
        patientListView = (ListView) findViewById(R.id.patientList);
        new FetchPatientAsyncTask().execute();
    }
    // Fetches the list of movies from the server
    private class FetchPatientAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(PatientListingActivity.this);
            pDialog.setMessage("Loading patients. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... params)
        {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "fetch_all_patient.php", "GET", null);
            try
            {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray patients;
                if (success == 1)
                {
                    patientList = new ArrayList<>();
                    //getJSONArray grabs the value associated to KEY_DATA in the JSON object returned by the database.
                    //In this case, the value consist of JSON objects with patients name and email.
                    patients = jsonObject.getJSONArray(KEY_DATA);

                    //Iterate through the response and populate movies list
                    for (int i = 0; i < patients.length(); i++)
                    {
                        //Grabs a single JSON object
                        JSONObject patient = patients.getJSONObject(i);
                        //Gets the value associated to KEY_EMAIL,KEY_FIRST_NAME, KEY_LAST_NAME which is the patients email,first and last name.
                        String email = patient.getString(KEY_EMAIL);
                        String first_name = patient.getString(KEY_FIRST_NAME);
                        String last_name = patient.getString(KEY_LAST_NAME);
                        //HashMap just stores email and name like key/value pairs
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_EMAIL, email);
                        map.put(KEY_FIRST_NAME, first_name + ' ' + last_name + '\n' + email);
                        patientList.add(map);
                    }
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            //Runs populateMovieList method on the UI thread so that its run instantly
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    populatePatientList();
                }
            });
        }
    }
    /* Updating parsed JSON data into ListView */
    private void populatePatientList()
    {
        ListAdapter adapter = new SimpleAdapter(PatientListingActivity.this, patientList,R.layout.list_item, new String[]{KEY_EMAIL,KEY_FIRST_NAME},new int[]{R.id.email, R.id.Name});
        // updating listview
        patientListView.setAdapter(adapter);
        //Call PatientUpdateDeleteActivity when a patient name is clicked
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    String email = ((TextView) view.findViewById(R.id.email)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(),PatientUpdateDeleteActivity.class);
                    intent.putExtra(KEY_EMAIL, email);

                    startActivityForResult(intent, 20);
                } else
                {
                    Toast.makeText(PatientListingActivity.this,"Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20)
        {
            // If the result code is 20 that means that
            // the user has deleted/updated the movie.
            // So refresh the movie listing
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
