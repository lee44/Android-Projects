package jlee.app.cal.wampfirebasemessaging;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity
{
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<UserMessage> messageList;
    private EditText msgInputText;
    private Button msgSend;
    private ProgressDialog progressDialog;
    private UserMessage message;

    protected void onStart()
    {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),new IntentFilter("MessageReceived"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
                JSONObject json = new JSONObject(intent.getExtras().getString("Message"));
                JSONObject data = json.getJSONObject("data");
                UserMessage u = new UserMessage(UserMessage.MSG_TYPE_RECEIVED,data.getString("message"));
                Log.v("MyServerMessage",data.getString("message"));
                messageList.add(u);
                updateRecyclerView();
            }
            catch(Exception e)
            {

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        msgInputText = (EditText)findViewById(R.id.edittext_chatbox);
        msgSend = (Button)findViewById(R.id.button_chatbox_send);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messageList = new ArrayList<UserMessage>();
        mMessageAdapter = new MessageListAdapter(this, messageList);
        //setLayoutManager is assigning the LayoutManager for our messages to be displayed.
        //LayoutManager is responsible for measuring and positioning item views within a RecyclerView.
        //LinearLayoutManager shows data in a simple list vertically by default.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);

        Log.v("MyServerEmail",getIntent().getExtras().getString("email"));

        msgSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String msgContent = msgInputText.getText().toString();
                if(!TextUtils.isEmpty(msgContent))
                {
                    message = new UserMessage(UserMessage.MSG_TYPE_SENT,msgContent);
                    messageList.add(message);
                    updateRecyclerView();
                    // Empty the input edit text box.
                    msgInputText.setText("");

                    sendMessage();
                }
            }
        });
    }
    private void sendMessage()
    {
        //POST method which will call the sendSinglePush.php on the WAMP server. The file will send the message to the firebase cloud server.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, jlee.app.cal.wampfirebasemessaging.IPAddress.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.v("MyServerResponse",response);
                        Toast.makeText(MessageActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("message", message.getMessage());
                params.put("email", getIntent().getExtras().getString("email"));
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void updateRecyclerView()
    {
        int newMsgPosition = messageList.size() - 1;
        // Notify recycler view insert one new data.
        mMessageAdapter.notifyItemInserted(newMsgPosition);
        // Scroll RecyclerView to the last message.
        mMessageRecycler.scrollToPosition(newMsgPosition);
    }
}
