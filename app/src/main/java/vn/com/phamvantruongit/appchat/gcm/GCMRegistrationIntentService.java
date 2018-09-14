package vn.com.phamvantruongit.appchat.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vn.com.phamvantruongit.appchat.AppController;
import vn.com.phamvantruongit.appchat.R;


public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String REGISTRATION_TOKEN_SENT = "RegistrationTokenSent";

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
         registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete=null;
        String token=null;
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.w("GCMRegIntentService", "token:" + token);


            sendRegistrationTokenToServer(token);
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);


        } catch (IOException e) {
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void sendRegistrationTokenToServer(final String token) {
        int id= AppController.getInstance().getUserId();
        StringRequest stringRequest=new StringRequest(Request.Method.PUT, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent registrationComplete = new Intent(REGISTRATION_TOKEN_SENT);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("token",token);
                return super.getParams();
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
