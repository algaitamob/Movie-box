package com.algaita.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import adapter.ComingSoon_RecycleviewAdapter;
//import adapter.Theater_RecycleviewAdapter;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.adapters.ComingVideosAdapter;
import com.algaita.adapters.VideosAdapter;
import com.algaita.models.ComingVideos;
import com.algaita.models.Videos;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modalclass.CoomingSoonModalClass;
import modalclass.TheaterModalClass;

public class MainActivity extends AppCompatActivity {


    Spinner sp_city;
    ImageView img_user;
    RecyclerView theaters_recycleview;
    RecyclerView comingsoon_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;


//    Theater_RecycleviewAdapter theater_recycleviewAdapter;
//    ComingSoon_RecycleviewAdapter comingsoon_recycleviewAdapter;

//    Theater Recyler
    List<Videos> GetVideosAdapterTheater;
    Videos getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;
//    Coming Soon Recycler
    List<ComingVideos> GetVideosAdapterComingsoon;
    ComingVideos getVideosAdapterComingsoon;
    RecyclerView.Adapter recyclerViewAdapterComingSoon;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionHandlerUser = new SessionHandlerUser(this);
        viewDialog = new ViewDialog(this);

        img_user = findViewById(R.id.profile);

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Recycleview
        theaters_recycleview =  findViewById(R.id.theaters_recycleview);
        comingsoon_recycleview =  findViewById(R.id.comingsoon_recycleview);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        theaters_recycleview.setLayoutManager(new GridLayoutManager(this, 2));
        comingsoon_recycleview.setLayoutManager(new GridLayoutManager(this, 2));

//        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        comingsoon_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();
        GetVideosAdapterComingsoon = new ArrayList<>();



        GetvideosComingSoon();
        GetVideosTheater();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterTheater.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterTheater.get(position).getPrice());
                intent.putExtra("poster", GetVideosAdapterTheater.get(position).getPoster());
                intent.putExtra("release_date", GetVideosAdapterTheater.get(position).getRelease_date());
                intent.putExtra("status", "out");
                intent.putExtra("id", GetVideosAdapterTheater.get(position).getVideoid());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
        comingsoon_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), comingsoon_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(MainActivity.this, MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterComingsoon.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterComingsoon.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterComingsoon.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterComingsoon.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterComingsoon.get(position).getPrice());
                intent.putExtra("poster", GetVideosAdapterComingsoon.get(position).getPoster());
                intent.putExtra("release_date", GetVideosAdapterComingsoon.get(position).getRelease_date());
                intent.putExtra("status", "coming");
                intent.putExtra("id", GetVideosAdapterComingsoon.get(position).getVideoid());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


;


    }

    private void GetVideosTheater() {
        viewDialog.showDialog();
        GetVideosAdapterTheater.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "videos.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getVideosAdapterTheater = new Videos();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterTheater.setTitle(json.getString("title"));
                getVideosAdapterTheater.setDescription(json.getString("description"));
                getVideosAdapterTheater.setTrailer_url(json.getString("trailer_url"));
                getVideosAdapterTheater.setVideo_url(json.getString("video_url"));
                getVideosAdapterTheater.setPrice(json.getString("price"));
                getVideosAdapterTheater.setPoster(json.getString("poster"));
                getVideosAdapterTheater.setRelease_date(json.getString("release_date"));
                getVideosAdapterTheater.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterTheater.setVideoid(Integer.parseInt(json.getString("id")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new VideosAdapter(GetVideosAdapterTheater, this);
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    private void GetvideosComingSoon() {
//        viewDialog.showDialog();
        GetVideosAdapterComingsoon.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "coming_videos.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                viewDialog.hideDialog();
                GetCardWebCall2(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall2(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getVideosAdapterComingsoon = new ComingVideos();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterComingsoon.setTitle(json.getString("title"));
                getVideosAdapterComingsoon.setDescription(json.getString("description"));
                getVideosAdapterComingsoon.setTrailer_url(json.getString("trailer_url"));
                getVideosAdapterComingsoon.setVideo_url(json.getString("video_url"));
                getVideosAdapterComingsoon.setPrice(json.getString("price"));
                getVideosAdapterComingsoon.setPoster(json.getString("poster"));
                getVideosAdapterComingsoon.setRelease_date(json.getString("release_date"));
                getVideosAdapterComingsoon.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterComingsoon.setVideoid(Integer.parseInt(json.getString("id")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterComingsoon.add(getVideosAdapterComingsoon);
        }

        recyclerViewAdapterComingSoon = new ComingVideosAdapter(GetVideosAdapterComingsoon, this);
        comingsoon_recycleview.setAdapter(recyclerViewAdapterComingSoon);
        recyclerViewAdapterComingSoon.notifyDataSetChanged();
    }



}
