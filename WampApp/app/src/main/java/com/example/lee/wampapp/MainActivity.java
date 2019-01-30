package com.example.lee.wampapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lee.wampapp.helper.CheckNetworkStatus;

public class MainActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_my_sql);
        Button viewAllBtn = (Button) findViewById(R.id.viewAllBtn);
        Button addNewBtn = (Button) findViewById(R.id.addNewBtn);
        viewAllBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    Intent i = new Intent(getApplicationContext(), PatientListingActivity.class);
                    startActivity(i);
                } else
                {
                    //Display error message if not connected to internet
                    Toast.makeText(MainActivity.this, "Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        addNewBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    Intent i = new Intent(getApplicationContext(),AddPatientActivity.class);
                    startActivity(i);
                } else
                {
                    //Display error message if not connected to internet
                    Toast.makeText(MainActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
