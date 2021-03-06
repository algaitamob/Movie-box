package com.algaita.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.MovieInfoActivity;
import com.algaita.activities.RecyclerTouchListener;
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

public class MyVideosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private BaseActivity baseActivity;

    ImageView img_user;
    RecyclerView theaters_recycleview;
    RecyclerView comingsoon_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recyler
    List<Videos> GetVideosAdapterTheater;
    Videos getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    SwipeRefreshLayout mSwipeRefreshLayout;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_myvideos, container, false);


        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //Recycleview
        theaters_recycleview =  view.findViewById(R.id.theaters_recycleview);
        theaters_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        GetVideosTheater();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterTheater.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterTheater.get(position).getPrice());
                intent.putExtra("downloads", GetVideosAdapterTheater.get(position).getDownloads());
                intent.putExtra("watch", GetVideosAdapterTheater.get(position).getWatch());
                intent.putExtra("poster", GetVideosAdapterTheater.get(position).getPoster());
                intent.putExtra("cover", GetVideosAdapterTheater.get(position).getCover());
                intent.putExtra("release_date", GetVideosAdapterTheater.get(position).getRelease_date());
                intent.putExtra("status", "out");
                intent.putExtra("id", GetVideosAdapterTheater.get(position).getVideoid());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


        return view;

    }



    private void GetVideosTheater() {
        viewDialog.showDialog();
        mSwipeRefreshLayout.setRefreshing(true);
        GetVideosAdapterTheater.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "myvideos.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mSwipeRefreshLayout.setRefreshing(false);
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

        recyclerViewAdapterTheater = new VideosAdapter(GetVideosAdapterTheater, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        GetVideosTheater();
    }
}
