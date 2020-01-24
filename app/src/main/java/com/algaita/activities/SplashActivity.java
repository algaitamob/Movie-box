package com.algaita.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    SessionHandlerUser sessionHandlerUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        sessionHandlerUser = new SessionHandlerUser(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Thread thread = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(6*500);

                    if (sessionHandlerUser.isLoggedIn()){
                        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        CheckSession(android_id);
                    }else{
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();

                    }

                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private boolean CheckSession(String android_id) {
        String url_ = Config.url+"check_session.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&device_id=" + android_id;
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 0) {

                                Intent intent = new Intent(SplashActivity.this, BaseActivity.class);
                                startActivity(intent);
                                finish();

                            } else if(response.getInt("status") == 1) {

                                AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("It seems you account has been logged in another Device!");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Remove my Account", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                        startActivity(getIntent());
                                    }
                                });
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("It Seem your Device is not Registered!");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "GO IT!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
        return true;
    }
}

