package com.algaita.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayYoutubeActivity extends YouTubeBaseActivity {

    public String iid;
    public static final String YT_API_KEY = "ENTER_YOUR_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_youtube);

        CheckLive();

    }


    private void CheckLive() {
        String url_ = Config.url+"check_live.php";
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 0) {

                                iid = response.getString("link");
                                YouTubePlayerView youTubePlayerView =
                                        (YouTubePlayerView) findViewById(R.id.player);

                                youTubePlayerView.initialize(YT_API_KEY,
                                        new YouTubePlayer.OnInitializedListener() {
                                            @Override
                                            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                                YouTubePlayer youTubePlayer, boolean b) {

                                                // do any work here to cue video, play video, etc.
                                                youTubePlayer.cueVideo(iid);
                                                // or to play immediately
                                                // youTubePlayer.loadVideo("5xVh-7ywKpE");
                                            }
                                            @Override
                                            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                                YouTubeInitializationResult youTubeInitializationResult) {
                                                Toast.makeText(PlayYoutubeActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {

                                AlertDialog alertDialog = new AlertDialog.Builder(PlayYoutubeActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("No Live show now");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
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
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }
}
