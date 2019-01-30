package jlee.app.cal.wampfirebasemessaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment
{
    private ListView listView;
    private ProgressDialog progressDialog;
    private List<String> devices;
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private ArrayList<HashMap<String, String>> friendsList;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        //inflate method will read the given xml and generate View objects
        //so the return statement is just returning View objects generated from the XML. Notice public View onCreateView
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        listView = (ListView) view.findViewById(R.id.friendsList);
        devices = new ArrayList<>();
        loadRegisteredDevices();
    }

    private void loadRegisteredDevices()
    {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Devices...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, IPAddress.URL_FETCH_DEVICES,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.dismiss();
                        JSONObject obj = null;
                        try
                        {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error"))
                            {
                                friendsList = new ArrayList<>();
                                JSONArray jsonDevices = obj.getJSONArray("devices");

                                for (int i = 0; i < jsonDevices.length(); i++)
                                {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(KEY_EMAIL, d.getString("email"));
                                    friendsList.add(map);
                                }
                                ListAdapter adapter = new SimpleAdapter(getContext(),friendsList ,R.layout.list_item, new String[]{KEY_EMAIL},new int[]{R.id.email});
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                    {
                                        String email = ((TextView) view.findViewById(R.id.email)).getText().toString();
                                        Intent intent = new Intent(getContext(),MessageActivity.class);
                                        intent.putExtra(KEY_EMAIL, email);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                })
        {

        };
        MyVolley.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

}
