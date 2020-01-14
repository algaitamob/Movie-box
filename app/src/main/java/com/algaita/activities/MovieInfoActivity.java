package com.algaita.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.Cast_RecycleviewAdapter;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})

    private Handler handler;
    private Runnable runnable;


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
                MediaController mediaController= new MediaController(getApplicationContext());
                mediaController.setAnchorView(videoView);
                Uri uri = Uri.parse(intent.getStringExtra("trailer_url"));
                //Starting VideView By Setting MediaController and URI
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();
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
//                .centerCrop(150, 150)
//                .resize(150, 150)
//                .fit()
                .placeholder(R.drawable.imgloader)
//                .centerInside()
                .error(R.drawable.icon)
                .into(poster);

        Glide.with(this)
                .load(intent.getStringExtra("poster"))
//                .centerCrop(150, 150)
//                .resize(150, 150)
//                .fit()
                .placeholder(R.drawable.imgloader)
//                .centerInside()
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

        LinearLayout card_airtime, card_credit;
        card_credit = view.findViewById(R.id.card_credit);
        card_airtime = view.findViewById(R.id.card_airtime);

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
                        txtrelease_date.setText("Kwana: "+String.format("%02d", days) + "Sa'a: "+ String.format("%02d", hours) + "Minti: " + String.format("%02d", minutes) + "Daqiqa: " + String.format("%02d", seconds));
                    } else {
//                        tvEventStart.setVisibility(View.VISIBLE);
//                        tvEventStart.setText("The event started!");
//                        textViewGone();
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


    private void download() {

            Intent intent = getIntent();
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
            if (!root.exists()) {
                root.mkdirs();
            }

            File file = new File(root, intent.getStringExtra("title") + ".alg");

            if (!file.exists()) {

                String url = intent.getStringExtra("video_url");
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Downloading - " +  intent.getStringExtra("title"));
                request.setTitle(intent.getStringExtra("title"));
                // in order for this if to run, you must use the android 3.2 to compile your app

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//                request.setDestinationInExternalPublicDir(getString(R.string.download_desti), Constant.arrayList_play.get(Constant.playPos).getMp3Name() + ".mp3");
                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + intent.getStringExtra("title") + ".alg"));

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
//                        String json = JsonUtils.getJSONString(Constant.URL_DOWNLOAD_COUNT + Constant.arrayList_play.get(viewpager.getCurrentItem()).getId());
//                        Log.e("aaa - ", json);
                        return null;
                    }
                }.execute();
            } else {
                Toast.makeText(MovieInfoActivity.this, "Already Download", Toast.LENGTH_SHORT).show();
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

                download();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MovieInfoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            download();
        }
    }


}




