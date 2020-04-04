package com.algaita.activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.Welcome;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
//
                    if (sessionHandlerUser.isLoggedIn()){
                        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        CheckSession(android_id);
                    }else{

                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
//                        if(Build.VERSION.SDK_INT>20){
//                            ActivityOptions options =
//                                    ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this);
//                            startActivity(i,options.toBundle());
//                        }else {
//                            startActivity(i);
//                        }

                    startActivity(i);
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

                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(i);

                            } else if(response.getInt("status") == 1) {

                                AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("It seems your account has been logged in another Device!");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Remove my Account", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                                Settings.Secure.ANDROID_ID);

                                        String userid = String.valueOf(sessionHandlerUser.getUserDetail().getUserid());
                                        UpdateDevice(userid, android_id);


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
                        alertDialog.setMessage("Network Connection Error!");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OFFLINE MODE!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                alertDialog.dismiss();
                                Intent intent = new Intent(SplashActivity.this, BaseActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrayRequest.setRetryPolicy(policy);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
        return true;
    }

    private void UpdateDevice(String userid, String android_id) {
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
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("userid", userid);
                data.put("device_id", android_id);
                String result = rh.sendPostRequest(Config.url + "device_update.php",data);

                return result;
            }
        }

        regdevice ui = new regdevice();
        ui.execute();
    }
}

