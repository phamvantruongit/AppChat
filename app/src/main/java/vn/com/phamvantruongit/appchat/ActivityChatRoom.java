package vn.com.phamvantruongit.appchat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.com.phamvantruongit.appchat.gcm.GCMRegistrationIntentService;
import vn.com.phamvantruongit.appchat.model.Message;

public class ActivityChatRoom extends AppCompatActivity implements View.OnClickListener {

    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog dialog;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private ArrayList<Message> messages;
    private Button btnSend;
    private EditText edMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(AppController.getInstance().getUserName());
        setSupportActionBar(toolbar);

        dialog=new ProgressDialog(this);
        dialog.setMessage("Opening chat room");
        dialog.show();
        edMessage=findViewById(R.id.editTextMessage);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        messages=new ArrayList<>();
        fetchMessage();

        btnSend=findViewById(R.id.buttonSend);
        btnSend.setOnClickListener(this);
        
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    
                }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT)){
                    Toast.makeText(getApplicationContext(), "Chatroom Ready...", Toast.LENGTH_SHORT).show();
                }else if(intent.getAction().equals(Constants.PUSH_NOTIFICATION)){
                    String name=intent.getStringExtra("name");
                    String message=intent.getStringExtra("message");
                    String id=intent.getStringExtra("id");
                    procesMessage(name,message,id);
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

    }

    private void procesMessage(String name, String message, String id) {
        Message m = new Message(Integer.parseInt(id), message, getTimeStamp(), name);
        messages.add(m);
        scrollToBottom();
    }

    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);

    }

    private String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    private void fetchMessage() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_FETCH_MESSAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("messages");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int userId = obj.getInt("userid");
                                String message = obj.getString("message");
                                String name = obj.getString("name");
                                String sentAt = obj.getString("sentat");
                                Message messagObject = new Message(userId, message, sentAt, name);
                                messages.add(messagObject);
                            }

                            adapter = new ThreadAdapter(ActivityChatRoom.this, messages, AppController.getInstance().getUserId());
                            recyclerView.setAdapter(adapter);
                            scrollToBottom();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        sendMessage();
    }

    private void sendMessage() {

        final String message = edMessage.getText().toString().trim();
        if (message.equalsIgnoreCase(""))
            return;
        int userId = AppController.getInstance().getUserId();
        String name = AppController.getInstance().getUserName();
        String sentAt = getTimeStamp();

        Message m = new Message(userId, message, sentAt, name);
        messages.add(m);
        adapter.notifyDataSetChanged();

        scrollToBottom();

        edMessage.setText("");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(AppController.getInstance().getUserId()));
                params.put("message", message);
                params.put("name", AppController.getInstance().getUserName());
                return params;
            }
        };

        //Disabling retry to prevent duplicate messages
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
    }


    //Unregistering receivers
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
