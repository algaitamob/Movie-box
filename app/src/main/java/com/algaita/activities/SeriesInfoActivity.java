package com.algaita.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.List;

public class SeriesInfoActivity extends AppCompatActivity {


    // Progress Dialog
    private ProgressDialog pDialog;
    ProgressBar progressBar = null;


    ImageView img_play;

    Button bbtn_buy, bbtn_download, bbtn_watch;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String urlString;
    Dialog downloadDialog;
    int WalletBalance;
    RecyclerView series_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Series Recycler
    List<SeriesVideos> GetSeriesAdapter;
    SeriesVideos getSeriesAdapter;
    RecyclerView.Adapter recyclerViewAdapterSeries;


    RecyclerView cast_recycleview;

    //    Series Recycler
    List<Cast> GetCastAdapter;
    Cast getCastAdapter;
    RecyclerView.Adapter recyclerViewAdapterCast;


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})

    private Handler handler;
    private Runnable runnable;
    ProgressDialog progressDialog;


    TextView txttitle, txtrelease_date, txtprice, txtdescription, btn_trailer, btn_buy, btn_download;
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
        setContentView(R.layout.activity_series_info);
        sessionHandlerUser = new SessionHandlerUser(this);
        viewDialog = new ViewDialog(this);
        final Intent intent = getIntent();
        txttitle = findViewById(R.id.title);
        txtdescription = findViewById(R.id.description);
        txtprice = findViewById(R.id.price);
        txtrelease_date = findViewById(R.id.release_date);
        poster = findViewById(R.id.poster);
        poster_bg = findViewById(R.id.poster_bg);
        play = findViewById(R.id.play);

        progressBar =  findViewById(R.id.progressbar);


//        img_play = findViewById(R.id.play);

        checkPermissions();
        series_recycleview =  findViewById(R.id.movie_recycleview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SeriesInfoActivity.this);

        series_recycleview.setLayoutManager(layoutManager);
        series_recycleview.setItemAnimator(new DefaultItemAnimator());

        GetSeriesAdapter = new ArrayList<>();

        GetSeries();

        series_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), series_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String id, title, price, video_url;
                id = GetSeriesAdapter.get(position).getId();
                title = GetSeriesAdapter.get(position).getTitle();
                price = GetSeriesAdapter.get(position).getPrice();
                video_url = GetSeriesAdapter.get(position).getVideo_url();

                CheckVideoStatus(id, title, price, video_url);
//                showDialogPay(id, title, price, video_url);



            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


        cast_recycleview =  findViewById(R.id.cast_recycleview);


        cast_recycleview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

//        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(SeriesInfoActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        cast_recycleview.setLayoutManager(layoutManager1);
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
        final VideoView videoView =(VideoView)findViewById(R.id.vdVw);
        //Set MediaController  to enable play, pause, forward, etc options.

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                poster_bg.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);

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
                play.setVisibility(View.GONE);


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

        txtrelease_date.setText(intent.getStringExtra("release_date"));

        txttitle.setText(intent.getStringExtra("title"));
        txtdescription.setText(intent.getStringExtra("description"));
        txtprice.setText(intent.getStringExtra("total_episode"));


        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }



//    ButtomSheetPayment
    private void showBottomSheetDialog(String id, String price) {
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
                Intent intent = new Intent(SeriesInfoActivity.this, PaymetWeb.class);
                intent.putExtra("type", "airtime");
                intent.putExtra("amount", price);
                intent.putExtra("videoid", id);
                startActivity(intent);
            }
        });

        card_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(SeriesInfoActivity.this, PaymetWeb.class);
                intent.putExtra("type", "paystack");
                intent.putExtra("amount", price);
                intent.putExtra("videoid", id);

                startActivity(intent);
            }
        });

        card_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mBottomSheetDialog.hide();
                Intent i = getIntent();
                    int bal = Integer.parseInt(sessionHandlerUser.getUserDetail().getBalance());
                    int pricee = Integer.parseInt(price);
                    if (bal < pricee) {
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
                        Intent intent = new Intent(SeriesInfoActivity.this, ChargeWallet.class);
                        intent.putExtra("amount", price);
                        intent.putExtra("videoid", id);
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




    private void CheckVideoStatus(String id, String title, String price, String video_url) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"check_series.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&videoid=" + id;
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {

                                if (response.getString("series_status").contains("YES")){
//                                    bbtn_buy.setVisibility(View.GONE);
//                                    bbtn_download.setVisibility(View.VISIBLE);
                                    String type = "YES";
//                                    Toast.makeText(getApplicationContext(), video_url, Toast.LENGTH_LONG).show();

//                                    bbtn_download.setVisibility(View.GONE);
//                                    bbtn_buy.setVisibility(View.VISIBLE);
                                    showDialogPay(id, title, price, type, video_url);
                                }else {
                                    String type = "NO";
                                    showDialogPay(id, title, price, type, video_url);


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
                        String type = "NO";
                        showDialogPay(id, title, price, type, video_url);
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }




    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(SeriesInfoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(SeriesInfoActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
//            new DownloadingTask().execute();
//            new DownloadFile().execute(video_url);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(SeriesInfoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//            new DownloadingTask().execute();
//            new DownloadFile().execute(getIntent().getStringExtra("video_url"));
//            new DownloadFile().execute(video_url);


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


    private void GetCast() {
//        viewDialog.showDialog();
        GetCastAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "cast_video.php?videoid=" + getIntent().getStringExtra("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                viewDialog.hideDialog();
                GetCardWebCall4(response);
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

    private void GetCardWebCall4(JSONArray array) {
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

        recyclerViewAdapterCast = new CastAdapter(GetCastAdapter, getApplicationContext());
        cast_recycleview.setAdapter(recyclerViewAdapterCast);
        recyclerViewAdapterCast.notifyDataSetChanged();
    }


    private void GetSeries() {
                viewDialog.showDialog();
        GetSeriesAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "series_video.php?seriesid=" + getIntent().getStringExtra("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                GetCardWebCall3(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall3(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getSeriesAdapter = new SeriesVideos();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getSeriesAdapter.setTitle(json.getString("title"));
                getSeriesAdapter.setPrice(json.getString("price"));
                getSeriesAdapter.setVideo_url(Config.dir_video + json.getString("video_url"));
                getSeriesAdapter.setRelease_date(json.getString("release_date"));
                getSeriesAdapter.setId(json.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetSeriesAdapter.add(getSeriesAdapter);
        }

        recyclerViewAdapterSeries = new SeriesVideosAdapter(GetSeriesAdapter, getApplicationContext());
        series_recycleview.setAdapter(recyclerViewAdapterSeries);
        recyclerViewAdapterSeries.notifyDataSetChanged();
    }




    private void showDialogPay(String id, String title, String price, String type, String video_url) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_series_check_payment);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView name;
        name = dialog.findViewById(R.id.name);
        bbtn_buy = dialog.findViewById(R.id.btn_buy);
        bbtn_download = dialog.findViewById(R.id.btn_download);
        bbtn_watch = dialog.findViewById(R.id.btn_watch);

        name.setText(getIntent().getStringExtra("title") + " - " + title);

        if (price.startsWith("0")){
                bbtn_download.setVisibility(View.VISIBLE);
                bbtn_buy.setVisibility(View.GONE);
        }else{

            if (type.contains("YES")){
                bbtn_download.setVisibility(View.VISIBLE);
                bbtn_watch.setVisibility(View.VISIBLE);
                bbtn_buy.setVisibility(View.GONE);
            }else{
                bbtn_download.setVisibility(View.GONE);
                bbtn_watch.setVisibility(View.GONE);
                bbtn_buy.setVisibility(View.VISIBLE);
            }



        }


        bbtn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showBottomSheetDialog(id, price);
            }
        });

        bbtn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DownloadFileFromURL().execute(video_url);
            }
        });


        bbtn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeriesInfoActivity.this, PlayerService.class);
                intent.putExtra("uri", video_url);
                startActivity(intent);
            }
        });


        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);

            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(getIntent().getStringExtra("title") + "  - Downloaded Successfully!");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }

    }


    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }



}




