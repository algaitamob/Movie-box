package com.algaita.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.adapters.SeriesAdapter;
import com.algaita.models.Series;
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

public class SeriesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView txt;
    ImageView back;

    SessionHandlerUser sessionHandlerUser;

    ViewDialog viewDialog;
    RecyclerView theaters_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;
    EditText et_search;

    ImageView btn_search;

    SwipeRefreshLayout mSwipeRefreshLayout;

    //    Theater Recyler
    List<Series> GetVideosAdapterTheater;
    Series getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        txt=findViewById(R.id.txt);

        txt.setText("Series");
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search(et_search.getText().toString());
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
                Intent intent = new Intent(getApplication(), SeriesInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
                intent.putExtra("poster", GetVideosAdapterTheater.get(position).getPoster());
                intent.putExtra("total_episode", GetVideosAdapterTheater.get(position).getTotal_episode());
                intent.putExtra("cover", GetVideosAdapterTheater.get(position).getCover());
                intent.putExtra("release_date", GetVideosAdapterTheater.get(position).getRelease_date());
                intent.putExtra("id", GetVideosAdapterTheater.get(position).getId());
                intent.putExtra("size", GetVideosAdapterTheater.get(position).getSize());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));




    }

    private void Search(String toString) {
        viewDialog.showDialog();
        mSwipeRefreshLayout.setRefreshing(true);

        GetVideosAdapterTheater.clear();
        Intent intent = getIntent();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "series_series.php?filter=" + toString, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                GetCard2WebCall(response);
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

    private void GetCard2WebCall(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getVideosAdapterTheater = new Series();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterTheater.setTitle(json.getString("title"));
                getVideosAdapterTheater.setDescription(json.getString("description"));
                getVideosAdapterTheater.setTrailer_url(Config.dir_video + json.getString("trailer_url"));
                getVideosAdapterTheater.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterTheater.setTotal_episode(json.getString("total_episode"));
                getVideosAdapterTheater.setCover(Config.dir_poster +  json.getString("cover"));
                getVideosAdapterTheater.setRelease_date(json.getString("release_date"));
                getVideosAdapterTheater.setId(json.getString("id"));
                getVideosAdapterTheater.setSize(json.getString("size"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new SeriesAdapter(GetVideosAdapterTheater, getApplicationContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }



    private void GetVideosTheater() {
        viewDialog.showDialog();
        mSwipeRefreshLayout.setRefreshing(true);

        GetVideosAdapterTheater.clear();
        Intent intent = getIntent();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "series_all.php", new Response.Listener<JSONArray>() {
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
            getVideosAdapterTheater = new Series();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterTheater.setTitle(json.getString("title"));
                getVideosAdapterTheater.setDescription(json.getString("description"));
                getVideosAdapterTheater.setTrailer_url(Config.dir_video + json.getString("trailer_url"));
                getVideosAdapterTheater.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterTheater.setTotal_episode(json.getString("total_episode"));
                getVideosAdapterTheater.setCover(Config.dir_poster +  json.getString("cover"));
                getVideosAdapterTheater.setRelease_date(json.getString("release_date"));
                getVideosAdapterTheater.setId(json.getString("id"));
                getVideosAdapterTheater.setSize(json.getString("size"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new SeriesAdapter(GetVideosAdapterTheater, getApplicationContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    @Override
    public void onRefresh() {
        GetVideosTheater();
    }


}





