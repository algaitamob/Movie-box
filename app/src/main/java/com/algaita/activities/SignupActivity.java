package com.algaita.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.ViewDialog;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SignupActivity extends AppCompatActivity {

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMPTY = "";

    EditText etphone, etpassword, etemail, etfullname;
    String phone, password, email, fullname;
    TextView txtlogin;
    Button btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        viewDialog = new ViewDialog(this);

//        if (sessionHandlerUser.isLoggedIn()){
//            loadDashboard();
//        }

        etphone = findViewById(R.id.phone);
        etpassword = findViewById(R.id.password);
        etfullname = findViewById(R.id.fullname);
        etemail = findViewById(R.id.email);

        txtlogin = findViewById(R.id.back_to_login);

        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etphone.getText().toString();
                String password = etpassword.getText().toString();
                String fullname = etfullname.getText().toString();
                String email = etemail.getText().toString();

                if (phone.isEmpty()) {
                    etphone.setError("Enter Phone Number");
                    etphone.requestFocus();
                }else if(password.isEmpty()){
                    etpassword.setError("Enter Password");
                    etpassword.requestFocus();
                }else if(fullname.isEmpty()){
                    etfullname.setError("Enter Fullname");
                    etfullname.requestFocus();
                }else if(email.isEmpty()){
                    etemail.setError("Enter Email Address");
                    etemail.requestFocus();
                }else {
                    MakeLogin(phone, password, email, fullname);
                }



            }
        });
    }



    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void MakeLogin(String phone, String password, String email, String fullname) {
        viewDialog.showDialog();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("password", password);
            request.put("fullname", fullname);
            request.put("email", email);
            request.put("phone", phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, Config.user_register, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            viewDialog.hideDialog();
                        try {
                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                //Set the user sessio

                                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                String userid = response.getString("userid");

                                RegisterDevice(userid, android_id);

                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText("Account Created Successfully");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();


                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.putExtra("phone", response.getString("phone"));
                                startActivity(i);
                                finish();
                            }else if(response.getInt(KEY_STATUS) == 1){

                                etphone.setError("Phone Number already taken!");
                                etphone.requestFocus();

                            }else{

                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text =  layout.findViewById(R.id.text);
                                text.setText(response.getString(KEY_MESSAGE));
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog();
                        //Display error message whenever an error occurs
//                        Toast.makeText(getApplicationContext(),
//                                error.getMessage(), Toast.LENGTH_SHORT).show();


                        View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Network Connection Error");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);

    }



    public void RegisterDevice(String userid, String android_id){
        class regdevice extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                viewDialog.hideDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                viewDialog.hideDialog();
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("userid", userid);
                data.put("device_id", android_id);
                String result = rh.sendPostRequest(Config.url + "device_register.php",data);

                return result;
            }
        }

        regdevice ui = new regdevice();
        ui.execute();
    }


    /**
     * Validates inputs and shows error if any
     *
     * @return
     */
    private boolean validateInputs() {
        if (KEY_EMPTY.equals(phone)) {
            etphone.setError("Phone Number cannot be empty");
            etphone.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            etpassword.setError("Password cannot be empty");
            etpassword.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(email)) {
            etemail.setError("Email cannot be empty");
            etemail.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(fullname)) {
            etfullname.setError("FullName cannot be empty");
            etfullname.requestFocus();
            return false;
        }
        return true;
    }

}
