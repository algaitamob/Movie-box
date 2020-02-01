package com.algaita.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
import com.algaita.adapters.CastAdapter;
import com.algaita.adapters.SeriesVideosAdapter;
import com.algaita.models.Cast;
import com.algaita.models.SeriesVideos;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modalclass.CastModalClass;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MovieInfoActivity extends AppCompatActivity {



    CoordinatorLayout layout_bg;

    ProgressBar progressBar = null;


    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String urlString;
    Dialog downloadDialog;
    int WalletBalance;


    RecyclerView cast_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Series Recycler
    List<Cast> GetCastAdapter;
    Cast getCastAdapter;
    RecyclerView.Adapter recyclerViewAdapterSeries;

    private static final String SHOWCASE_ID = "Buy";

    private LinearLayout card_airtime, card_credit, card_wallet;

    ImageView img_play;
    // Progress Dialog
    private ProgressDialog pDialog;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})

    private Handler handler;
    private Runnable runnable;
    TextView txttitle, txtrelease_date, txtprice, txtdescription, txtinfo, total_download, total_watch, btn_trailer, btn_buy, btn_download, btn_watch;
    ImageView poster, poster_bg;
    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ViewDialog viewDialog;

    private ImageView play;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        sessionHandlerUser = new SessionHandlerUser(this);
        viewDialog = new ViewDialog(this);

        progressBar =  findViewById(R.id.progressbar);


        layout_bg = findViewById(R.id.layout_bg);
        final Intent intent = getIntent();
        txttitle = findViewById(R.id.title);
        txtdescription = findViewById(R.id.description);
        txtinfo = findViewById(R.id.info);
        txtprice = findViewById(R.id.price);
        txtrelease_date = findViewById(R.id.release_date);
        poster = findViewById(R.id.poster);
        poster_bg = findViewById(R.id.poster_bg);
        total_download = findViewById(R.id.total_download);
        total_watch = findViewById(R.id.total_watch);


        play = findViewById(R.id.play);
        img_play = findViewById(R.id.play);


        cast_recycleview =  findViewById(R.id.cast_recycleview);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MovieInfoActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        cast_recycleview.setLayoutManager(layoutManager);
        cast_recycleview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        cast_recycleview.setItemAnimator(new DefaultItemAnimator());

        GetCastAdapter = new ArrayList<>();

        GetCast();

        cast_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), cast_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));



        CheckBalance();
        final VideoView videoView = findViewById(R.id.vdVw);
        //Set MediaController  to enable play, pause, forward, etc options.

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                poster_bg.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                img_play.setVisibility(View.GONE);

                try {
                    Uri uri = Uri.parse(intent.getStringExtra("trailer_url"));
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();


                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    videoView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));



                    progressBar.setVisibility(View.VISIBLE);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.start();
                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                               int arg2) {
                                    // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                                    mp.start();
                                }
                            });
                        }
                    });
                    videoView.start();

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
//                           Toast.makeText(getApplicationContext(), "Video completed", Toast.LENGTH_LONG).show();
                            img_play.setVisibility(View.VISIBLE);

                        }
                    });
                }catch (Exception e){

                    e.printStackTrace();
                }


            }
        });
        btn_trailer = findViewById(R.id.trailer);
        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                poster_bg.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                img_play.setVisibility(View.GONE);

               try {
                   Uri uri = Uri.parse(intent.getStringExtra("trailer_url"));
                   videoView.setVideoURI(uri);
                   videoView.requestFocus();


                   DisplayMetrics metrics = new DisplayMetrics();
                   getWindowManager().getDefaultDisplay().getMetrics(metrics);
                   videoView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));


                   progressBar.setVisibility(View.VISIBLE);
                   videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                       @Override
                       public void onPrepared(MediaPlayer mp) {
                           // TODO Auto-generated method stub
                           mp.start();
                           mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                               @Override
                               public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                              int arg2) {
                                   // TODO Auto-generated method stub
                                   progressBar.setVisibility(View.GONE);
                                   mp.start();
                               }
                           });
                       }
                   });
                   videoView.start();

                   videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                       @Override
                       public void onCompletion(MediaPlayer mp) {
//                           Toast.makeText(getApplicationContext(), "Video completed", Toast.LENGTH_LONG).show();
                        img_play.setVisibility(View.VISIBLE);

                       }
                   });
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
//


            }
        });

        btn_watch = findViewById(R.id.watch);
        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "watch";
                Update(type);
                Intent intent = new Intent(MovieInfoActivity.this, PlayerService.class);
                intent.putExtra("uri", getIntent().getStringExtra("video_url"));
                startActivity(intent);

            }
        });

        btn_download = findViewById(R.id.download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();


            }
        });


        if(getIntent().getStringExtra("price").startsWith("0")){
            btn_buy.setVisibility(View.GONE);
            btn_download.setVisibility(View.VISIBLE);
            btn_watch.setVisibility(View.VISIBLE);
            btn_trailer.setVisibility(View.GONE);
        }else{
            CheckVideoStatus();

        }

        Glide.with(this)
                .load(intent.getStringExtra("poster"))
                .placeholder(R.drawable.imgloader)
                .error(R.drawable.icon)
                .into(poster);

        Glide.with(this)
                .load(intent.getStringExtra("cover"))
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
        txtinfo.setText(intent.getStringExtra("info"));
        total_watch.setText(intent.getStringExtra("watch"));
        total_download.setText(intent.getStringExtra("downloads"));
        txtprice.setText("₦" + intent.getStringExtra("price"));


        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }



//  ButtomSheetPayment
    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_select_payment_method, null);


        card_credit = view.findViewById(R.id.card_credit);
        card_airtime = view.findViewById(R.id.card_airtime);
        card_wallet = view.findViewById(R.id.card_wallet);

        showTutor(500);

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
//                mBottomSheetDialog.hide();
                Intent i = getIntent();
                    int bal = Integer.parseInt(sessionHandlerUser.getUserDetail().getBalance());
                    int price = Integer.parseInt(i.getStringExtra("price"));
                    if (bal < price) {
                        View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Insufficient Wallet Balance!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    } else {
                        String type = "Wallet";
//                        String amount = i.getStringExtra("price");
//                        String videoid = i.getStringExtra("videoid");

//                        ChargeWallet(amount, videoid);
                        Intent ii = getIntent();
                        Intent intent = new Intent(MovieInfoActivity.this, ChargeWallet.class);
                        intent.putExtra("amount", ii.getStringExtra("price"));
                        intent.putExtra("videoid", ii.getStringExtra("id"));
                        startActivity(intent);


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



    private void GetCast() {
//        viewDialog.showDialog();
        GetCastAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "cast_video.php?videoid=" + getIntent().getStringExtra("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                viewDialog.hideDialog();
                GetCardWebCall3(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall3(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getCastAdapter = new Cast();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getCastAdapter.setCast_name(json.getString("name"));
                getCastAdapter.setImg(Config.dir_cast + json.getString("img"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetCastAdapter.add(getCastAdapter);
        }

        recyclerViewAdapterSeries = new CastAdapter(GetCastAdapter, getApplicationContext());
        cast_recycleview.setAdapter(recyclerViewAdapterSeries);
        recyclerViewAdapterSeries.notifyDataSetChanged();
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
                                    btn_watch.setVisibility(View.VISIBLE);
                                    btn_trailer.setVisibility(View.GONE);
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
            new DownloadFileFromURL().execute(getIntent().getStringExtra("video_url"));

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MovieInfoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

            new DownloadFileFromURL().execute(getIntent().getStringExtra("video_url"));


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






    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading... Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String folder = "/data/data/" + getPackageName() + "/files/";

                File directory = new File(folder);


                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(folder + getIntent().getStringExtra("title") + ".mp4");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);

            String type = "downloads";
            Update(type);

            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(getIntent().getStringExtra("title") + "  - Downloaded Successfully!");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

        }

    }

    private void Update(String type) {
//        viewDialog.showDialog();
        class chargee extends AsyncTask<Bitmap,Void,String> {

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
//                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
//                TextView text = layout.findViewById(R.id.text);
//                text.setText(s);
//                Toast toast = new Toast(getApplicationContext());
//                toast.setDuration(Toast.LENGTH_LONG);
//                toast.setView(layout);
//                toast.show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("type", type);
                data.put("videoid", getIntent().getStringExtra("id"));
                String result = rh.sendPostRequest(Config.url + "update_download_watch.php",data);

                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }





    private void showTutor(int millis){
        new MaterialShowcaseView.Builder(this)
                .setTarget(card_airtime)
                .setTitleText("Attention")
                .setDismissText("GOT IT!")
                .setContentText("Payment with Recharge Card is for Nigerians only!")
                .setDelay(millis)
                .singleUse(SHOWCASE_ID)
                .show();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
////        finish();
//
////        CheckVideoStatus();
//    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}




