package com.algaita.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.algaita.PreventScreenshot;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.player.CustomVideoPlayer;

public class PlayerService extends AppCompatActivity {
    CustomVideoPlayer customVideoPlayer;
    ProgressBar progressBar = null;
    ViewDialog viewDialog;


    @Override
    public  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_player);



        viewDialog = new ViewDialog(this);
        Intent intent = getIntent();
        progressBar =  findViewById(R.id.progressbar);

        VideoView videoView = findViewById(R.id.vdVw);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.setSecure(true);
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


    }





}
