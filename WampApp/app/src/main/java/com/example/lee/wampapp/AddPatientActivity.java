package com.example.lee.wampapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lee.wampapp.helper.CheckNetworkStatus;
import com.example.lee.wampapp.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPatientActivity extends AppCompatActivity
{
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String BASE_URL = "http://192.168.1.136:5559/";
    private static String STRING_EMPTY = "";
    private EditText first_name;
    private EditText last_name;
    private EditText email;

    private String patient_first_name;
    private String patient_last_name;
    private String patient_email;

    private Button addButton;
    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        first_name = (EditText) findViewById(R.id.txtFirstNameAdd);
        last_name = (EditText) findViewById(R.id.txtLastNameAdd);
        email = (EditText) findViewById(R.id.txtEmailAdd);

        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    addMovie();
                } else
                {
                    Toast.makeText(AddPatientActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Checks whether all files are filled. If so then calls AddMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     */
    private void addMovie()
    {
        if (!STRING_EMPTY.equals(first_name.getText().toString()) && !STRING_EMPTY.equals(last_name.getText().toString()) && !STRING_EMPTY.equals(email.getText().toString()) )
        {
            patient_first_name = first_name.getText().toString();
            patient_last_name = last_name.getText().toString();
            patient_email = email.getText().toString();
            new AddMovieAsyncTask().execute();
        } else
        {
            Toast.makeText(AddPatientActivity.this,"One or more fields left empty!",Toast.LENGTH_LONG).show();
        }
    }

    /* AsyncTask for adding a movie */
    private class AddMovieAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Display proggress bar
            pDialog = new ProgressDialog(AddPatientActivity.this);
            pDialog.setMessage("Adding Patient. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_FIRST_NAME, patient_first_name);
            httpParams.put(KEY_LAST_NAME, patient_last_name);
            httpParams.put(KEY_EMAIL, patient_email);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "add_patient.php", "POST", httpParams);
            try
            {
                success = jsonObject.getInt(KEY_SUCCESS);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    if (success == 1)
                    {
                        //Display success message
                        Toast.makeText(AddPatientActivity.this, "Patient Added", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        //Finish ths activity and go back to listing activity
                        finish();

                    } else
                    {
                        Toast.makeText(AddPatientActivity.this,"Some error occurred while adding patient",  Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
