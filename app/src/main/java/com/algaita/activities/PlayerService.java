package com.algaita.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.algaita.R;
import com.algaita.player.CustomVideoPlayer;

public class PlayerService extends AppCompatActivity {
    CustomVideoPlayer customVideoPlayer;

    @Override
    public  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_player);

//        CustomVideoPlayer customVideoPlayer = findViewById(R.id.customVideoPlayer);

        Intent intent = getIntent();
//        customVideoPlayer.setMediaUrl(intent.getStringExtra("uri"));

        VideoView videoView =(VideoView)findViewById(R.id.vdVw);

        Toast.makeText(getApplicationContext(), intent.getStringExtra("uri"), Toast.LENGTH_LONG).show();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(intent.getStringExtra("uri") + ".mp4");
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();


    }
}
