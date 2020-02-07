package com.algaita.activities;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;

import com.algaita.R;
import com.algaita.ViewDialog;

public class PlayerService extends Activity implements  OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
        OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaPlayerControl{
    String url;
    MediaPlayer mMediaPlayer ;
    SurfaceView mSurfaceView ;
    SurfaceHolder holder ;
//    private ConstantAnchorMediaController controller = null;
    ProgressBar progressBar1 ;
    MediaController mcontroller ;
    Handler handler ;

    PlayerDialog playerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        url = getIntent().getStringExtra("uri");
        progressBar1 = findViewById(R.id.progressbar);
        SurfaceView v =  findViewById(R.id.surface_view);
        handler = new Handler();
        holder = v.getHolder();
        holder.addCallback(this);
        v.setSecure(true);

        progressBar1.setVisibility(View.VISIBLE);
        playVideo();

    }
    private void playVideo() {
        try {

            mcontroller = new MediaController(this);
            mMediaPlayer = MediaPlayer.create(this, Uri.parse(url));
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mcontroller.show();
        return false;
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mMediaPlayer.setDisplay(holder);
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        progressBar1.setVisibility(View.GONE);
        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(findViewById(R.id.surface_view));
        mcontroller.setEnabled(true);

        handler.post(new Runnable() {
            public void run() {
                mcontroller.show();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub
    }
    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    @Override
    public void onBackPressed() {
        mMediaPlayer.stop();
        super.onBackPressed();
    }
}