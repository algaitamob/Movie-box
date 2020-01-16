package com.algaita.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapter.Cast_RecycleviewAdapter;

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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import modalclass.CastModalClass;

public class MovieInfoActivity extends AppCompatActivity {


    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String urlString;
    Dialog downloadDialog;
    int WalletBalance;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})

    private Handler handler;
    private Runnable runnable;
    ProgressDialog progressDialog;
    RecyclerView cast_recycleview;
    Cast_RecycleviewAdapter cast_recycleviewAdapter;
    private ArrayList<CastModalClass> castArrayList;
    TextView txttitle, txtrelease_date, txtprice, txtdescription, btn_trailer, btn_buy, btn_download;
    ImageView poster, poster_bg;
    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        sessionHandlerUser = new SessionHandlerUser(this);
        viewDialog = new ViewDialog(this);
        final Intent intent = getIntent();
        txttitle = findViewById(R.id.title);
        txtdescription = findViewById(R.id.description);
        txtprice = findViewById(R.id.price);
        txtrelease_date = findViewById(R.id.release_date);
        poster = findViewById(R.id.poster);
        poster_bg = findViewById(R.id.poster_bg);


        CheckBalance();
        final VideoView videoView =(VideoView)findViewById(R.id.vdVw);
        //Set MediaController  to enable play, pause, forward, etc options.

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        btn_trailer = findViewById(R.id.trailer);
        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                poster_bg.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);

               try {
                   Uri uri = Uri.parse(intent.getStringExtra("trailer_url"));
                   videoView.setVideoURI(uri);
                   videoView.requestFocus();
                   videoView.start();
               }catch (Exception e){

                   e.printStackTrace();
               }

            }
        });
        btn_buy = findViewById(R.id.buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();

            }
        });

        btn_download = findViewById(R.id.download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();


            }
        });

        CheckVideoStatus();

        Glide.with(this)
                .load(intent.getStringExtra("poster"))
                .placeholder(R.drawable.imgloader)
                .error(R.drawable.icon)
                .into(poster);

        Glide.with(this)
                .load(intent.getStringExtra("poster"))
                .placeholder(R.drawable.imgloader)
                .error(R.drawable.icon)
                .into(poster_bg);
//

        String status = intent.getStringExtra("status");
        if (status.contains("coming")){
            countDownStart();
            btn_buy.setText("Coming Soon!");
            btn_buy.setEnabled(false);
        }else{
            txtrelease_date.setText(intent.getStringExtra("release_date"));
        }

        txttitle.setText(intent.getStringExtra("title"));
        txtdescription.setText(intent.getStringExtra("description"));
        txtprice.setText("₦" + intent.getStringExtra("price"));
        txttitle.setText(intent.getStringExtra("title"));


        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }



//    ButtomSheetPayment
    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_select_payment_method, null);

        LinearLayout card_airtime, card_credit, card_wallet;
        card_credit = view.findViewById(R.id.card_credit);
        card_airtime = view.findViewById(R.id.card_airtime);
        card_wallet = view.findViewById(R.id.card_wallet);

        card_airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(MovieInfoActivity.this, PaymetWeb.class);
                intent.putExtra("type", "airtime");
                intent.putExtra("amount", i.getStringExtra("price"));
                intent.putExtra("videoid", i.getStringExtra("id"));
                startActivity(intent);
            }
        });

        card_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(MovieInfoActivity.this, PaymetWeb.class);
                intent.putExtra("type", "paystack");
                intent.putExtra("amount", i.getStringExtra("price"));
                intent.putExtra("videoid", i.getStringExtra("id"));

                startActivity(intent);
            }
        });

        card_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
                Intent i = getIntent();
                    int bal = Integer.parseInt(sessionHandlerUser.getUserDetail().getBalance());
                    int price = Integer.parseInt(i.getStringExtra("price"));
                    if (bal < price) {
                        View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Insufficent Wallet Balance!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    } else {
                        String type = "Wallet";
                        String amount = i.getStringExtra("price");
                        String videoid = i.getStringExtra("videoid");
                        ChargeWallet(amount, videoid);

                    }

            }
        });

        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }


    private void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse(intent.getStringExtra("release_date"));
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        txtrelease_date.setText(String.format("%02d", days) + "days  "+ String.format("%02d", hours) + "hr  " + String.format("%02d", minutes) + "min  " + String.format("%02d", seconds) + "sec");
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }


    private void CheckVideoStatus() {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"check_video.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&videoid=" + intent.getStringExtra("id");
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {

                                if (response.getString("video_status").contains("YES")){
                                    btn_buy.setVisibility(View.GONE);
                                    btn_download.setVisibility(View.VISIBLE);
                                }else {


                                }

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                            viewDialog.hideDialog();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }




//    New Download
    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MovieInfoActivity.this);
            progressDialog.setMessage("Downloading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();
                    View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                    TextView text = layout.findViewById(R.id.text);
                    text.setText(getIntent().getStringExtra("title") + "Downloaded Succesfully");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);

                    View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                    TextView text = layout.findViewById(R.id.text);
                    text.setText(getIntent().getStringExtra("title") + "Download Failed!");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if an exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                Log.e("result", "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }



        @Override
        protected Void doInBackground(Void... arg0) {
            Intent intent = getIntent();
            String url_ = intent.getStringExtra("video_url");
            int count = 0;

            try {
                URL url = new URL(url_);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                int lengthOfFile = c.getContentLength();




                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    Log.e("Server returned HTTP " + c.getResponseCode()
//                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

//                    File myDir = getApplicationContext().getDir("AdabulMufrad", Context.MODE_PRIVATE);
                    apkStorage = new File("/data/data/" + getPackageName() + "/files/");

//                    Uri.parse("/data/data/" + getPackageName() + "/app_AdabulMufrad/" + myFile.getName().trim());
                } else
                    Toast.makeText(getApplicationContext(), "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e("result", "Directory Created.");
                }

                outputFile = new File(apkStorage, intent.getStringExtra("title") + ".mp4");//Create Output file in Main File




                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e("result", "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                long total = 0;

                while ((len1 = is.read(buffer)) != -1) {

                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    onProgressUpdate("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("ptogress", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    fos.write(buffer, 0, len1);//Write new file

                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e("result", "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }



    public class CheckForSDCard {


        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(

                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }
    }



    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MovieInfoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(MovieInfoActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            new DownloadingTask().execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MovieInfoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            new DownloadingTask().execute();
        }
    }


    private boolean CheckBalance() {
        String url_ = Config.user_wallet+"?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 0) {

                                sessionHandlerUser.WalletBalance(response.getString("balance"));

//                                WalletBalance = Integer.parseInt(response.getString("balance"));

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
        return true;
    }



    private void ChargeWallet(String amount, String videoid) {
        viewDialog.showDialog();
        class chargee extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                viewDialog.hideDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                viewDialog.hideDialog();
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

                data.put("amount", amount);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                data.put("videoid", videoid);
                String result = rh.sendPostRequest(Config.url + "charge_wallet.php?amount=" + amount + "&userid=" + sessionHandlerUser.getUserDetail().getUserid() + "&videoid=" + videoid,data);

                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }




}




