package com.algaita.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.LoginActivity;
//import com.algaita.activities.MainActivity;
import com.algaita.activities.MovieInfoActivity;
import com.algaita.activities.RecyclerTouchListener;
import com.algaita.activities.SeriesInfoActivity;
import com.algaita.adapters.ComingVideosAdapter;
import com.algaita.adapters.SeriesAdapter;
import com.algaita.adapters.VideosAdapter;
import com.algaita.models.ComingVideos;
import com.algaita.models.Series;
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

public class HomeFragment extends Fragment {
    private View view;
    private BaseActivity baseActivity;

    ImageView img_user;
    RecyclerView theaters_recycleview;
    RecyclerView comingsoon_recycleview;
    RecyclerView series_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recyler
    List<Videos> GetVideosAdapterTheater;
    Videos getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;
    //    Coming Soon Recycler
    List<ComingVideos> GetVideosAdapterComingsoon;
    ComingVideos getVideosAdapterComingsoon;
    RecyclerView.Adapter recyclerViewAdapterComingSoon;

    //    Series Recycler
    List<Series> GetSeriesAdapter;
    Series getSeriesAdapter;
    RecyclerView.Adapter recyclerViewAdapterSeries;


    //

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());

        //Recycleview
        theaters_recycleview =  view.findViewById(R.id.theaters_recycleview);
        comingsoon_recycleview =  view.findViewById(R.id.comingsoon_recycleview);
        series_recycleview =  view.findViewById(R.id.series_recycleview);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        theaters_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        comingsoon_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        series_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));

//        series_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        comingsoon_recycleview.setItemAnimator(new DefaultItemAnimator());
        series_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();
        GetVideosAdapterComingsoon = new ArrayList<>();
        GetSeriesAdapter = new ArrayList<>();



        GetvideosComingSoon();
        GetVideosTheater();
        GetSeries();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterTheater.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterTheater.get(position).getPrice());
                intent.putExtra("poster", GetVideosAdapterTheater.get(position).getPoster());
                intent.putExtra("cover", GetVideosAdapterTheater.get(position).getCover());
                intent.putExtra("release_date", GetVideosAdapterTheater.get(position).getRelease_date());
                intent.putExtra("status", "out");
                intent.putExtra("id", GetVideosAdapterTheater.get(position).getVideoid());
//                Toast.makeText(getActivity(), GetVideosAdapterTheater.get(position).getPoster(), Toast.LENGTH_LONG).show();


                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
        comingsoon_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), comingsoon_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(getContext(), MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterComingsoon.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterComingsoon.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterComingsoon.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterComingsoon.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterComingsoon.get(position).getPrice());
                intent.putExtra("poster", GetVideosAdapterComingsoon.get(position).getPoster());
                intent.putExtra("cover", GetVideosAdapterComingsoon.get(position).getCover());
                intent.putExtra("release_date", GetVideosAdapterComingsoon.get(position).getRelease_date());
                intent.putExtra("status", "coming");
                intent.putExtra("id", GetVideosAdapterComingsoon.get(position).getVideoid());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        series_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), series_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(getContext(), SeriesInfoActivity.class);
                intent.putExtra("title", GetSeriesAdapter.get(position).getTitle());
                intent.putExtra("description", GetSeriesAdapter.get(position).getDescription());
                intent.putExtra("trailer_url", GetSeriesAdapter.get(position).getTrailer_url());
                intent.putExtra("poster", GetSeriesAdapter.get(position).getPoster());
                intent.putExtra("total_episode", GetSeriesAdapter.get(position).getTotal_episode());
                intent.putExtra("cover", GetSeriesAdapter.get(position).getCover());
                intent.putExtra("release_date", GetSeriesAdapter.get(position).getRelease_date());
                intent.putExtra("id", GetSeriesAdapter.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));



        return view;

    }

    private void GetSeries() {

        //        viewDialog.showDialog();
        GetSeriesAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "series.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GetCardWebCall3(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall3(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getSeriesAdapter = new Series();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getSeriesAdapter.setTitle(json.getString("title"));
                getSeriesAdapter.setDescription(json.getString("description"));
                getSeriesAdapter.setTrailer_url(Config.dir_video + json.getString("trailer_url"));
                getSeriesAdapter.setPoster(Config.dir_poster + json.getString("poster"));
                getSeriesAdapter.setTotal_episode(json.getString("total_episode"));
                getSeriesAdapter.setCover(Config.dir_poster +  json.getString("cover"));
                getSeriesAdapter.setRelease_date(json.getString("release_date"));
                getSeriesAdapter.setId(json.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetSeriesAdapter.add(getSeriesAdapter);
        }

        recyclerViewAdapterSeries = new SeriesAdapter(GetSeriesAdapter, getContext());
        series_recycleview.setAdapter(recyclerViewAdapterSeries);
        recyclerViewAdapterSeries.notifyDataSetChanged();
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
        requestQueue = Volley.newRequestQueue(getContext());
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
                getVideosAdapterTheater.setTrailer_url(Config.dir_video +json.getString("trailer_url"));
                getVideosAdapterTheater.setVideo_url(Config.dir_video  + json.getString("video_url"));
                getVideosAdapterTheater.setPrice(json.getString("price"));
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

        recyclerViewAdapterTheater = new VideosAdapter(GetVideosAdapterTheater, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    private void GetvideosComingSoon() {
//        viewDialog.showDialog();
        GetVideosAdapterComingsoon.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "coming_videos.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GetCardWebCall2(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                viewDialog.hideDialog();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
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
                getVideosAdapterComingsoon.setTrailer_url(Config.dir_video +json.getString("trailer_url"));
                getVideosAdapterComingsoon.setVideo_url(Config.dir_video + json.getString("video_url"));
                getVideosAdapterComingsoon.setPrice(json.getString("price"));
                getVideosAdapterComingsoon.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterComingsoon.setCover(Config.dir_poster + json.getString("cover"));
                getVideosAdapterComingsoon.setRelease_date(json.getString("release_date"));
                getVideosAdapterComingsoon.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterComingsoon.setVideoid(json.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterComingsoon.add(getVideosAdapterComingsoon);
        }

        recyclerViewAdapterComingSoon = new ComingVideosAdapter(GetVideosAdapterComingsoon, getContext());
        comingsoon_recycleview.setAdapter(recyclerViewAdapterComingSoon);
        recyclerViewAdapterComingSoon.notifyDataSetChanged();
    }
}
