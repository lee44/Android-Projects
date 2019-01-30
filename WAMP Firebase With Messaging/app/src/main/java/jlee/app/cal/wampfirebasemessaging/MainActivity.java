package jlee.app.cal.wampfirebasemessaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private Button buttonDisplayToken;
    private Button buttonSendPush;
    private TextView textViewToken;
    private Button buttonRegister;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSendPush = (Button) findViewById(R.id.buttonSendNotification);

        buttonRegister.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view)
            {
                if (view == buttonRegister)
                    sendTokenToServer();
            }
        });
        buttonSendPush.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view)
            {
                if(view == buttonSendPush)
                    startActivity(new Intent(getBaseContext(), HomeActivity.class));
            }
        });
    }
    //storing token to mysql server
    private void sendTokenToServer()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        //Grab device token if it exist from SharedPreferences
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = editTextEmail.getText().toString();

        if (token == null)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }
        //From the volley library. Uses the POST method to send the data to the server.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, IPAddress.URL_REGISTER_DEVICE,
                new Response.Listener<String>()
                {
                    //Called when server sends a response in the form of JSON.
                    public void onResponse(String response)
                    {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            //Returns a map containing the user's email and device token for a POST and PUT request.
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
