package com.algaita.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.ViewDialog;
import com.algaita.adapters.CastAdapter;
import com.algaita.models.Cast;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.khizar1556.mkvideoplayer.MKPlayer;
import com.wooplr.spotlight.SpotlightView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class MovieInfoActivity extends AppCompatActivity {
    private MKPlayer player;

    Activity context;
    CoordinatorLayout layout_bg;
    ProgressBar progressBar = null;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
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
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private Handler handler;
    private Runnable runnable;
    TextView txttitle, txtrelease_date, txtprice, txtdescription, txtinfo, total_download, total_watch, total_size, btn_trailer, btn_buy, btn_download, btn_watch, btn_play;
    ImageView poster, poster_bg;
    FloatingActionButton btn_feedback;
    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ViewDialog viewDialog;
    private ImageView play;

//    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
//    int id = 1;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder build;
    final int id = 101;
    private AdView mAdView;

    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7803700300545861/7367824445");

        player=new MKPlayer(this);

        context = this;
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
        total_size = findViewById(R.id.total_size);
        play = findViewById(R.id.play);
        img_play = findViewById(R.id.play);
        btn_feedback = findViewById(R.id.feedback);
        btn_play = findViewById(R.id.btn_play);

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFeedback();
            }
        });

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        cast_recycleview =  findViewById(R.id.cast_recycleview);
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
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
//                poster_bg.setVisibility(View.GONE);
//                videoView.setVisibility(View.VISIBLE);
//                img_play.setVisibility(View.GONE);
//
//                try {
//                    Uri uri = Uri.parse(intent.getStringExtra("trailer_url"));
//                    videoView.setVideoURI(uri);
//                    videoView.requestFocus();
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    videoView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));
//                    progressBar.setVisibility(View.VISIBLE);
//                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            // TODO Auto-generated method stub
//                            mp.start();
//                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                                @Override
//                                public void onVideoSizeChanged(MediaPlayer mp, int arg1,
//                                                               int arg2) {
//                                    // TODO Auto-generated method stub
//                        progressBar.setVisibility(View.GONE);
//                                    mp.start();
//                                }
//                            });
//                        }
//                    });
//                    videoView.start();
//
//                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            img_play.setVisibility(View.VISIBLE);
//
//                        }
//                    });
//                }catch (Exception e){
//
//                    e.printStackTrace();
//                }
//                player.onComplete(new Runnable() {
//                    @Override
//                    public void run() {
//                        //callback when video is finish
//                        Toast.makeText(getApplicationContext(), "video play completed",Toast.LENGTH_SHORT).show();
//                    }
//                }).onInfo(new MKPlayer.OnInfoListener() {
//                    @Override
//                    public void onInfo(int what, int extra) {
//                        switch (what) {
//                            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                //do something when buffering start
//                                break;
//                            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
//                                //do something when buffering end
//                                break;
//                            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
//                                //download speed
//                                break;
//                            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                                //do something when video rendering
//                                break;
//                        }
//                    }
//                }).onError(new MKPlayer.OnErrorListener() {
//                    @Override
//                    public void onError(int what, int extra) {
//                        Toast.makeText(getApplicationContext(), "video play error",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                player.setPlayerCallbacks(new MKPlayer.playerCallbacks() {
//                    @Override
//                    public void onNextClick() {
//                        String url = getIntent().getStringExtra("trailer_url");
//
//                        player.play(url);
//                        player.setTitle(getIntent().getStringExtra("title"));
//                    }
//
//                    @Override
//                    public void onPreviousClick() {
//                        String url = getIntent().getStringExtra("trailer_url");
//
//                        player.play(url);
//                        player.setTitle(getIntent().getStringExtra("title"));
//                /*String url = ((EditText) findViewById(R.id.et_url)).getText().toString();
//                MKPlayerActivity.configPlayer(videoplayer.this).setTitle(url).play(url);*/
//                    }
//                });
//                String url=getIntent().getStringExtra("uri");
//                player.play(url);
//                player.setTitle(getIntent().getStringExtra("title"));

            }
        });
        btn_trailer = findViewById(R.id.trailer);
        String t = getIntent().getStringExtra("trailer_url");
//        Log.d("mytrailer", t);
//        if (t.equals("")){
//            btn_trailer.setVisibility(View.GONE);
//        }


        if (t.contains("null")) {
            btn_trailer.setVisibility(GONE);
//            return; // or break, continue, throw
        }

        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                intent.putExtra("uri", getIntent().getStringExtra("trailer_url"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                startActivity(intent);
            }
        });
        btn_buy = findViewById(R.id.buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        btn_watch = findViewById(R.id.watch);
        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIntent().getStringExtra("price").startsWith("0")) {

                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            String type = "watch";
                            Update(type);
                            Intent intent = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                            intent.putExtra("uri", getIntent().getStringExtra("video_url"));
                            intent.putExtra("title", getIntent().getStringExtra("title"));
                            startActivity(intent);

                            // Code to be executed when an ad request fails.
                        }

                        @Override
                        public void onAdOpened() {
                            // Code to be executed when the ad is displayed.
                        }

                        @Override
                        public void onAdLeftApplication() {
                            // Code to be executed when the user has left the app.
                        }

                        @Override
                        public void onAdClosed() {
                            String type = "watch";
                            Update(type);
                            Intent intent = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                            intent.putExtra("uri", getIntent().getStringExtra("video_url"));
                            intent.putExtra("title", getIntent().getStringExtra("title"));
                            startActivity(intent);
                            // Code to be executed when when the interstitial ad is closed.
                        }
                    });


                }else{
                    String type = "watch";
                    Update(type);
                    Intent intent = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                    intent.putExtra("uri", getIntent().getStringExtra("video_url"));
                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    startActivity(intent);

                }



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
            mAdView.setVisibility(View.VISIBLE);
            btn_buy.setVisibility(GONE);
            btn_download.setVisibility(View.VISIBLE);
            btn_watch.setVisibility(View.VISIBLE);
            btn_trailer.setVisibility(GONE);
            CheckFile();

        }else{
            CheckVideoStatus();

        }

        Glide.with(this)
                .load(intent.getStringExtra("poster"))
                .placeholder(R.drawable.imgloader)
                .error(R.drawable.applogo)
                .into(poster);

        Glide.with(this)
                .load(intent.getStringExtra("cover"))
                .placeholder(R.drawable.imgloader)
                .error(R.drawable.applogo)
                .into(poster_bg);

        String status = intent.getStringExtra("status");
        if (status.contains("coming")){
            countDownStart();
            btn_buy.setText("Coming Soon!");
            btn_buy.setEnabled(false);
            btn_watch.setVisibility(GONE);
        }else{
            txtrelease_date.setText(intent.getStringExtra("release_date"));
        }
        txttitle.setText(intent.getStringExtra("title"));
        txtdescription.setText(intent.getStringExtra("description"));
        txtinfo.setText(intent.getStringExtra("info"));
        total_watch.setText(intent.getStringExtra("watch"));
        total_download.setText(intent.getStringExtra("downloads"));
        total_size.setText("SIZE: "+intent.getStringExtra("size"));
        txtprice.setText("₦" + intent.getStringExtra("price"));




        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIntent().getStringExtra("price").startsWith("0")) {

                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Intent intent1 = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                            intent1.putExtra("uri", "/data/data/" + getPackageName() + "/files/" + getIntent().getStringExtra("title") + ".mp4");
                            intent1.putExtra("title", getIntent().getStringExtra("title"));
                            startActivity(intent1);

                            // Code to be executed when an ad request fails.
                        }

                        @Override
                        public void onAdOpened() {
                            // Code to be executed when the ad is displayed.
                        }

                        @Override
                        public void onAdLeftApplication() {
                            // Code to be executed when the user has left the app.
                        }

                        @Override
                        public void onAdClosed() {
                            Intent intent1 = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                            intent1.putExtra("uri", "/data/data/" + getPackageName() + "/files/" + getIntent().getStringExtra("title") + ".mp4");
                            intent1.putExtra("title", getIntent().getStringExtra("title"));
                            startActivity(intent1);
                            // Code to be executed when when the interstitial ad is closed.
                        }
                    });


                }else{
                    Intent intent1 = new Intent(MovieInfoActivity.this, VideoPlayer.class);
                    intent1.putExtra("uri", "/data/data/" + getPackageName() + "/files/" + getIntent().getStringExtra("title") + ".mp4");
                    intent1.putExtra("title", getIntent().getStringExtra("title"));
                    startActivity(intent1);

                }
            }
        });
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
        showT();
        card_airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(MovieInfoActivity.this, PaymetWeb.class);
                intent.putExtra("type", "airtime");
                intent.putExtra("amount", i.getStringExtra("price"));
                intent.putExtra("videoid", i.getStringExtra("id"));
//                startActivity(intent);
                startActivityForResult(intent,0);

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
                        mBottomSheetDialog.hide();
                        String type = "Wallet";
                        Intent ii = getIntent();
                        Intent intent = new Intent(MovieInfoActivity.this, ChargeWallet.class);
                        intent.putExtra("amount", ii.getStringExtra("price"));
                        intent.putExtra("videoid", ii.getStringExtra("id"));
//                        startActivity(intent);
                        startActivityForResult(intent,0);


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
        GetCastAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "cast_video.php?videoid=" + getIntent().getStringExtra("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GetCardWebCall3(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                                    btn_buy.setVisibility(GONE);
                                    btn_download.setVisibility(View.VISIBLE);
                                    btn_watch.setVisibility(View.VISIBLE);
                                    btn_trailer.setVisibility(GONE);
                                    CheckFile();
                                }else {

                                    CheckFile();

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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrayRequest.setRetryPolicy(policy);
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
//            BackTasks bt=new BackTasks();
//            bt.execute(getIntent().getStringExtra("video_url"));
//            NewDownloader(getIntent().getStringExtra("video_url"));

//            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(MovieInfoActivity.this);
//            mBuilder.setContentTitle("Download")
//                    .setContentText("Download in progress")
//                    .setSmallIcon(R.drawable.applogo);
            new DownloadFileFromURL().execute(getIntent().getStringExtra("video_url"));


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MovieInfoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(MovieInfoActivity.this);
//            mBuilder.setContentTitle("Download")
//                    .setContentText("Download in progress")
//                    .setSmallIcon(R.drawable.applogo);
            new DownloadFileFromURL().execute(getIntent().getStringExtra("video_url"));

//            BackTask bt=new BackTask();
//            bt.execute(getIntent().getStringExtra("video_url"));
//            new SeriesInfoActivity.DownloadFileFromURL().execute(getIntent().getStringExtra("video_url"));
//            BackTasks bt=new BackTasks();
//            bt.execute(getIntent().getStringExtra("video_url"));
//            NewDownloader(getIntent().getStringExtra("video_url"));

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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrayRequest.setRetryPolicy(policy);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
        return true;
    }
    /**
     * Showing Dialog
     * */

    private void Update(String type) {
        class chargee extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();

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
                data.put("type", type);
                data.put("videoid", getIntent().getStringExtra("id"));
                String result = rh.sendPostRequest(Config.url + "update_download_watch.php",data);

                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }






    private void showT(){
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("PAYMENT")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Payment with Recharge Card is for Nigerians only!\n")
                .maskColor(Color.parseColor("#dc000000"))
                .target(card_airtime)
                .lineAnimDuration(400)
                .lineAndArcColor(R.color.colorPrimary)
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("3") //UNIQUE ID
                .show();
    }

//
//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        finish();
//        startActivity(getIntent());
//    }
//
//





    private void showDialogFeedback() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText text;
        text = dialog.findViewById(R.id.message);

        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SendFeedBack(text.getText().toString());
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }



    private void SendFeedBack(String toString) {
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
                data.put("message", toString);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                String result = rh.sendPostRequest(Config.url + "feedback.php",data);

                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){
            // refresh
            finish();
            startActivity(getIntent());
        }
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

            View layout = getLayoutInflater().inflate(R.layout.toast_custom, findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(getIntent().getStringExtra("title") + "  - Downloaded Successfully!");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

        }



    }








    public void CheckFile() {

        String folder = "/data/data/" + getPackageName() + "/files/" + getIntent().getStringExtra("title") + ".mp4";
        File file = new File(folder);
        if (file.exists()) {
            btn_download.setVisibility(GONE);
            btn_play.setVisibility(View.VISIBLE);
            btn_watch.setVisibility(GONE);
        }

    }


}




