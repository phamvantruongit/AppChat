package vn.com.phamvantruongit.appchat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
    public static final String TAG=AppController.class.getSimpleName();
    private RequestQueue requestQueue;
    private static AppController mInstance;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }
    public static synchronized AppController getInstance(){
        return  mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        //Setting a tag to the request
        req.setTag(TAG);

        //calling the method to get the request queue and adding the requeust the the queuue
        getRequestQueue().add(req);
    }

    //method to cancle the pending requests
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    //Method to get sharedpreferences
    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    //This method will clear the sharedpreference
    //It will be called on logout
    public void logout() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

    //This method will store the user data on sharedpreferences
    //It will be called on login
    public void loginUser(int id, String name, String email) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constants.USER_ID, id);
        editor.putString(Constants.USER_EMAIL, email);
        editor.putString(Constants.USER_NAME, name);
        editor.putBoolean(Constants.IS_LOGGED_IN, true);
        editor.apply();
    }

    //This method will check whether the user is logged in or not
    public boolean isLoggedIn() {
        return getSharedPreferences().getBoolean(Constants.IS_LOGGED_IN, false);
    }

    //This method will return the user id of logged in user
    public int getUserId() {
        return getSharedPreferences().getInt(Constants.USER_ID, -1);
    }

    //This method will return the username of logged in user
    public String getUserName() {
        return getSharedPreferences().getString(Constants.USER_NAME, null);
    }
}


