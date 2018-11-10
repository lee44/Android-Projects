package com.example.lee.wampapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.lee.wampapp.helper.CheckNetworkStatus;
import com.example.lee.wampapp.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PatientUpdateDeleteActivity extends AppCompatActivity
{
    private static String STRING_EMPTY = "";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String BASE_URL = "http://192.168.1.136:5559/";
    private String emailParam;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;

    private String first_name;
    private String last_name;
    private String email;

    private Button deleteButton;
    private Button updateButton;
    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_update_delete);
        Intent intent = getIntent();
        firstNameEditText = (EditText) findViewById(R.id.txtFirstNameUpdate);
        lastNameEditText = (EditText) findViewById(R.id.txtLastNameUpdate);
        emailEditText = (EditText) findViewById(R.id.txtEmailUpdate);

        emailParam = intent.getStringExtra(KEY_EMAIL);

        new FetchPatientDetailsAsyncTask().execute();
        deleteButton = (Button) findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                confirmDelete();
            }
        });
        updateButton = (Button) findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    updatePatient();

                } else
                {
                    Toast.makeText(PatientUpdateDeleteActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * Fetches single movie details from the server
     */
    private class FetchPatientDetailsAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(PatientUpdateDeleteActivity.this);
            pDialog.setMessage("Loading Patient Details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_EMAIL, emailParam);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "get_patient_detail.php", "GET", httpParams);
            try
            {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONObject patient;
                if (success == 1)
                {
                    //Parse the JSON response
                    patient = jsonObject.getJSONObject(KEY_DATA);
                    first_name = patient.getString(KEY_FIRST_NAME);
                    last_name = patient.getString(KEY_LAST_NAME);
                    email = patient.getString(KEY_EMAIL);
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
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    //Populate the Edit Texts once the network activity is finished executing
                    firstNameEditText.setText(first_name);
                    lastNameEditText.setText(last_name);
                    emailEditText.setText(email);
                }
            });
        }
    }

    /**
     * Displays an alert dialogue to confirm the deletion
     */
    private void confirmDelete()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientUpdateDeleteActivity.this);
        alertDialogBuilder.setMessage("Are you sure, you want to delete this patient?");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                        {
                            //If the user confirms deletion, execute DeleteMovieAsyncTask
                            new DeletePatientAsyncTask().execute();
                        } else
                        {
                            Toast.makeText(PatientUpdateDeleteActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * AsyncTask to delete a patient
     */
    private class DeletePatientAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(PatientUpdateDeleteActivity.this);
            pDialog.setMessage("Deleting Patient. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_EMAIL, emailParam);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "delete_patient.php", "POST", httpParams);
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
                        Toast.makeText(PatientUpdateDeleteActivity.this,"Patient Deleted", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie deletion
                        setResult(20, i);
                        finish();

                    } else
                    {
                        Toast.makeText(PatientUpdateDeleteActivity.this,"Some error occurred while deleting patient",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Checks whether all files are filled. If so then calls UpdateMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     */
    private void updatePatient()
    {
        if (!STRING_EMPTY.equals(firstNameEditText.getText().toString()) && !STRING_EMPTY.equals(lastNameEditText.getText().toString()) && !STRING_EMPTY.equals(emailEditText.getText().toString()))
        {
            first_name = firstNameEditText.getText().toString();
            last_name = lastNameEditText.getText().toString();
            email = emailEditText.getText().toString();
            new UpdatePatientAsyncTask().execute();
        } else
        {
            Toast.makeText(PatientUpdateDeleteActivity.this,"One or more fields left empty!",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * AsyncTask for updating a movie details
     */
    private class UpdatePatientAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(PatientUpdateDeleteActivity.this);
            pDialog.setMessage("Updating Movie. Please wait...");
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
            httpParams.put(KEY_FIRST_NAME, first_name);
            httpParams.put(KEY_LAST_NAME, last_name);
            httpParams.put(KEY_EMAIL, emailParam);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest( BASE_URL + "update_patient.php", "POST", httpParams);
            try
            {
                success = jsonObject.getInt(KEY_SUCCESS);
                first_name = jsonObject.getString(KEY_FIRST_NAME);
                last_name = jsonObject.getString(KEY_LAST_NAME);
                email = jsonObject.getString(KEY_EMAIL);

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
                        Toast.makeText(PatientUpdateDeleteActivity.this,"Patient Updated", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        finish();

                    } else
                    {
                        Toast.makeText(PatientUpdateDeleteActivity.this,"Some error occurred while updating Patient",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}