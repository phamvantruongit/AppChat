package vn.com.phamvantruongit.appchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class    MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edEmail,edName;
    private Button btnEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edEmail=findViewById(R.id.editTextEmail);
        edName=findViewById(R.id.editTextName);
        btnEnter=findViewById(R.id.buttonEnter);
        btnEnter.setOnClickListener(this);
        if(AppController.getInstance().isLoggedIn()){
            finish();
            startActivity(new Intent(this,ActivityChatRoom.class));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(AppController.getInstance().isLoggedIn()){
            finish();
            startActivity(new Intent(this,ActivityChatRoom.class));
        }
    }

    @Override
    public void onClick(View v) {
        registerUser();
    }

    private void registerUser() {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Enter chat room");
        dialog.show();

        final String name=edName.getText().toString().trim();
        final String email=edEmail.getText().toString().trim();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();
                try {
                    JSONObject obj=new JSONObject(response);
                    int id=obj.getInt("id");
                    String name=obj.getString("name");
                    String email=obj.getString("email");

                    AppController.getInstance().loginUser(id,name,email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
