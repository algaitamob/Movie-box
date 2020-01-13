package com.algaita.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapter.Cast_RecycleviewAdapter;
import com.algaita.R;
import com.algaita.sessions.SessionHandlerUser;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import modalclass.CastModalClass;

public class MovieInfoActivity extends AppCompatActivity {



    private Handler handler;
    private Runnable runnable;


    RecyclerView cast_recycleview;

    Cast_RecycleviewAdapter cast_recycleviewAdapter;

    private ArrayList<CastModalClass> castArrayList;

    TextView txttitle, txtrelease_date, txtprice, txtdescription, btn_trailer, btn_buy;

    ImageView poster, poster_bg;

    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_info);
        sessionHandlerUser = new SessionHandlerUser(this);
        Intent intent = getIntent();
        txttitle = findViewById(R.id.title);
        txtdescription = findViewById(R.id.description);
        txtprice = findViewById(R.id.price);
        txtrelease_date = findViewById(R.id.release_date);
        poster = findViewById(R.id.poster);
        poster_bg = findViewById(R.id.poster_bg);


        final VideoView videoView =(VideoView)findViewById(R.id.vdVw);
        //Set MediaController  to enable play, pause, forward, etc options.


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
//        txtrelease_date.setText(intent.getStringExtra("release_date"));
        txttitle.setText(intent.getStringExtra("title"));


        // Cast  - adding data in array list

        castArrayList = new ArrayList<>();
        castArrayList.add(new CastModalClass(R.drawable.cast1,"Michael B.\n" + "Jordan"));
        castArrayList.add(new CastModalClass(R.drawable.cast2,"Sylvester \nStallone"));
        castArrayList.add(new CastModalClass(R.drawable.cast3,"Tessa\n" + "Thompson"));
        castArrayList.add(new CastModalClass(R.drawable.cast4,"Dolph\n" + "Lundgren"));
        castArrayList.add(new CastModalClass(R.drawable.cast1,"Michael B.\n" + "Jordan"));
        castArrayList.add(new CastModalClass(R.drawable.cast2,"Sylvester \nStallone"));
        castArrayList.add(new CastModalClass(R.drawable.cast3,"Tessa\n" + "Thompson"));
        castArrayList.add(new CastModalClass(R.drawable.cast4,"Dolph\n" + "Lundgren"));

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }



//    ButtomSheetPayment

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_select_payment_method, null);

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


}
