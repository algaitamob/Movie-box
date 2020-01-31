package com.algaita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.adapters.VideosAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView txt;
    TextView one,two,three,four,five,six,seven,eight,nine,zero;
    ImageView clear, add, back;
    EditText edtxt;
    String nos,number="";

    SessionHandlerUser sessionHandlerUser;

    ViewDialog viewDialog;
    RecyclerView theaters_recycleview;
    RecyclerView comingsoon_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    SwipeRefreshLayout mSwipeRefreshLayout;

    //    Theater Recyler
    List<Videos> GetVideosAdapterTheater;
    Videos getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        txt=findViewById(R.id.txt);

        txt.setText("Movies");
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        //Recycleview
        theaters_recycleview =  findViewById(R.id.theaters_recycleview);
        theaters_recycleview.setLayoutManager(new GridLayoutManager(this, 2));
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        GetVideosTheater();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(this, theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(VideosActivity.this, MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterTheater.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterTheater.get(position).getPrice());
                intent.putExtra("downloads", GetVideosAdapterTheater.get(position).getDownloads());
                intent.putExtra("watch", GetVideosAdapterTheater.get(position).getWatch());
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




    }


    private void GetVideosTheater() {
        viewDialog.showDialog();
        mSwipeRefreshLayout.setRefreshing(true);

        GetVideosAdapterTheater.clear();
        Intent intent = getIntent();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "videos_all.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                getVideosAdapterTheater.setTrailer_url(Config.dir_video + json.getString("trailer_url"));
                getVideosAdapterTheater.setVideo_url(Config.dir_video + json.getString("video_url"));
                getVideosAdapterTheater.setPrice(json.getString("price"));
                getVideosAdapterTheater.setWatch(json.getString("watch"));
                getVideosAdapterTheater.setDownloads(json.getString("downloads"));
                getVideosAdapterTheater.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterTheater.setCover(Config.dir_poster + json.getString("cover"));
                getVideosAdapterTheater.setRelease_date(json.getString("release_date"));
                getVideosAdapterTheater.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterTheater.setVideoid(json.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new VideosAdapter(GetVideosAdapterTheater, getApplicationContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    @Override
    public void onRefresh() {
        GetVideosTheater();
    }
}





