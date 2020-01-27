package com.algaita.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMPTY = "";

    EditText etphone, etpassword;
    String phone, password;
    TextView txtregister, txtforget;
    Button btn_login;



//    FireBase

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAB1DrYQk:APA91bGyd4Wx_J8bXjN5ZUEi2u28lJnBSjaRhV9HQbhxscMz6IVU8MMBTrBRiJ6AUEXVhnLfXzHIKWN3X0pjBCwPtc5gCSDgnIR1fSCrXgmSp_niHk6xHsaJSfiJWjI7xQ3kpOFzkuAO";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        viewDialog = new ViewDialog(this);

        if (sessionHandlerUser.isLoggedIn()){
//            loadDashboard();
//            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
//
//            CheckSession(android_id);

            loadDashboard();
        }
        etphone = findViewById(R.id.phone);
        etpassword = findViewById(R.id.password);
        txtregister = findViewById(R.id.create_account);
        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        txtforget = findViewById(R.id.forget_password);
        txtforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPay();
            }
        });
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etphone.getText().toString();
                String password = etpassword.getText().toString();
                if (phone.isEmpty()) {
                    etphone.setError("Enter Phone Number");
                    etphone.requestFocus();
                }else if(password.isEmpty()){
                    etpassword.setError("Enter Password");
                    etpassword.requestFocus();
                }else {
                    MakeLogin(phone, password);
                }

            }
        });




    }


    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), BaseActivity.class);
        startActivity(i);
        finish();
    }

    private void MakeLogin(String phone, String password) {

        viewDialog.showDialog();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("phone", phone);
            request.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, Config.user_login, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                sessionHandlerUser.loginUser(response.getString("email"), response.getString("fullname"), response.getString("phone"), response.getString("userid"));
                                subscribeToPushService();
//                                loadDashboard();


                                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                CheckSession(android_id);

                            } else {

                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
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




    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("login");
        FirebaseMessaging.getInstance().subscribeToTopic("announcement");


        Log.d("AndroidBash", "Subscribed");
//        Toast.makeText(LoginActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("AndroidBash", token);

        RegFirebase(token);
    }




    public void RegFirebase(final String token){
        class firebase extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();
            Intent i = getIntent();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("token", token);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                String result = rh.sendPostRequest(Config.url + "firebase_user.php",data);

                return result;
            }
        }

        firebase ui = new firebase();
        ui.execute();
    }

    private void showDialogPay() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_forget_password);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText phone;
        phone = dialog.findViewById(R.id.phone);



        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPass(phone.getText().toString());
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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

                            loadDashboard();

                            }  else if(response.getInt("status") == 1) {

                                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("It seems your account has been logged in another Device!");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Remove my Account", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                        startActivity(getIntent());

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

                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Network Connection Error");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "GO IT!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
        return true;
    }

    public void ForgetPass(final String phone){
        class forgetpass extends AsyncTask<Bitmap,Void,String> {

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
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("phone", phone);
                String result = rh.sendPostRequest(Config.url + "forget_password.php",data);

                return result;
            }
        }

        forgetpass ui = new forgetpass();
        ui.execute();
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
//                viewDialog.hideDialog();
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
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
