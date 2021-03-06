package com.algaita.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.ComingSoonActivity;
//import com.algaita.activities.MainActivity;
import com.algaita.activities.FreeVideosActivity;
import com.algaita.activities.MovieInfoActivity;
import com.algaita.activities.RecyclerTouchListener;
import com.algaita.activities.SeriesActivity;
import com.algaita.activities.SeriesInfoActivity;
import com.algaita.activities.VideosActivity;
import com.algaita.activities.ViewPagerAdapter;
import com.algaita.adapters.ComingVideosAdapter;
import com.algaita.adapters.SeriesAdapter;
import com.algaita.adapters.VideosAdapter;
import com.algaita.models.ComingVideos;
import com.algaita.models.Series;
import com.algaita.models.Videos;
import com.algaita.sessions.SessionHandlerUser;
import com.algaita.utils.SliderUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment{
    private View view;
    private BaseActivity baseActivity;

    ImageView img_user;
    RecyclerView theaters_recycleview;
    RecyclerView comingsoon_recycleview;
    RecyclerView series_recycleview;
    RecyclerView free_recycleview;
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

    // Free Recycler
    List<Videos> GetVideosAdapterFree;
    Videos getVideosAdapterFree;
    RecyclerView.Adapter recyclerViewAdapterFree;


    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView txt_more_vides, txt_more_series, txt_more_coming, txt_more_free;




    //

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.



    //    Sliders
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    RequestQueue rq;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;

    private AdView mAdView;
    private AdView mAdView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());
//
//        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
//
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark);


        txt_more_vides = view.findViewById(R.id.more_videos);
        txt_more_series = view.findViewById(R.id.more_series);
        txt_more_coming = view.findViewById(R.id.more_coming);
        txt_more_free = view.findViewById(R.id.more_free);
                //Recycleview
        theaters_recycleview =  view.findViewById(R.id.theaters_recycleview);
        comingsoon_recycleview =  view.findViewById(R.id.comingsoon_recycleview);
        series_recycleview =  view.findViewById(R.id.series_recycleview);
        free_recycleview =  view.findViewById(R.id.free_recycleview);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        theaters_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        comingsoon_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        series_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        free_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));

//        series_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        comingsoon_recycleview.setItemAnimator(new DefaultItemAnimator());
        series_recycleview.setItemAnimator(new DefaultItemAnimator());
        free_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();
        GetVideosAdapterComingsoon = new ArrayList<>();
        GetSeriesAdapter = new ArrayList<>();
        GetVideosAdapterFree = new ArrayList<>();

        if (isNetworkAvailable() == true){
            GetvideosComingSoon();
            GetVideosTheater();
            GetSeries();
            GetVideosFree();
        }

        mAdView = view.findViewById(R.id.adView);
        mAdView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
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
                intent.putExtra("info", GetVideosAdapterTheater.get(position).getInfo());
                intent.putExtra("size", GetVideosAdapterTheater.get(position).getSize());
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


        free_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), free_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), MovieInfoActivity.class);
                intent.putExtra("title", GetVideosAdapterFree.get(position).getTitle());
                intent.putExtra("description", GetVideosAdapterFree.get(position).getDescription());
                intent.putExtra("trailer_url", GetVideosAdapterFree.get(position).getTrailer_url());
                intent.putExtra("video_url", GetVideosAdapterFree.get(position).getVideo_url());
                intent.putExtra("price", GetVideosAdapterFree.get(position).getPrice());
                intent.putExtra("downloads", GetVideosAdapterFree.get(position).getDownloads());
                intent.putExtra("watch", GetVideosAdapterFree.get(position).getWatch());
                intent.putExtra("poster", GetVideosAdapterFree.get(position).getPoster());
                intent.putExtra("cover", GetVideosAdapterFree.get(position).getCover());
                intent.putExtra("info", GetVideosAdapterFree.get(position).getInfo());
                intent.putExtra("size", GetVideosAdapterFree.get(position).getSize());
                intent.putExtra("release_date", GetVideosAdapterFree.get(position).getRelease_date());
                intent.putExtra("status", "out");
                intent.putExtra("id", GetVideosAdapterFree.get(position).getVideoid());
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
                intent.putExtra("downloads", GetVideosAdapterComingsoon.get(position).getDownlaod());
                intent.putExtra("watch", GetVideosAdapterComingsoon.get(position).getWatch());
                intent.putExtra("poster", GetVideosAdapterComingsoon.get(position).getPoster());
                intent.putExtra("cover", GetVideosAdapterComingsoon.get(position).getCover());
                intent.putExtra("info", GetVideosAdapterComingsoon.get(position).getInfo());
                intent.putExtra("size", GetVideosAdapterComingsoon.get(position).getSize());
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
                intent.putExtra("size", GetSeriesAdapter.get(position).getSize());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


        txt_more_vides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VideosActivity.class);
                startActivity(intent);
            }
        });

        txt_more_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SeriesActivity.class);
                startActivity(intent);
            }
        });
        txt_more_coming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ComingSoonActivity.class);
                startActivity(intent);
            }
        });
        txt_more_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FreeVideosActivity.class);
                startActivity(intent);
            }
        });




//        Slider ViewPager

        sliderImg = new ArrayList<>();

        viewPager =  view.findViewById(R.id.viewPager);

        sliderDotspanel = view.findViewById(R.id.SliderDots);

        if (isNetworkAvailable() == true){
            sendRequest();
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                for(int i = 0; i< dotscount; i++){
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
//                }
//                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {

            /*After setting the adapter use the timer */
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == 4-1) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }catch (Exception e){


        }


        return view;

    }

    private void GetSeries() {
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
                getSeriesAdapter.setSize(json.getString("size"));

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
//        mSwipeRefreshLayout.setRefreshing(true);
        viewDialog.showDialog();
        GetVideosAdapterTheater.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "videos.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
//                mSwipeRefreshLayout.setRefreshing(false);
                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
//                mSwipeRefreshLayout.setRefreshing(false);

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
                getVideosAdapterTheater.setWatch(json.getString("watch"));
                getVideosAdapterTheater.setDownloads(json.getString("downloads"));
                getVideosAdapterTheater.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterTheater.setCover(Config.dir_poster + json.getString("cover"));
                getVideosAdapterTheater.setRelease_date(json.getString("release_date"));
                getVideosAdapterTheater.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterTheater.setVideoid(json.getString("id"));
                getVideosAdapterTheater.setInfo(json.getString("info"));
                getVideosAdapterTheater.setSize(json.getString("size"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new VideosAdapter(GetVideosAdapterTheater, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    private void GetVideosFree() {
//        mSwipeRefreshLayout.setRefreshing(true);
//        viewDialog.showDialog();
        GetVideosAdapterFree.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "free_videos.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                viewDialog.hideDialog();
//                mSwipeRefreshLayout.setRefreshing(false);
                GetCardWebCall1(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                viewDialog.hideDialog();
//                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall1(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getVideosAdapterFree = new Videos();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterFree.setTitle(json.getString("title"));
                getVideosAdapterFree.setDescription(json.getString("description"));
                getVideosAdapterFree.setTrailer_url(Config.dir_video +json.getString("trailer_url"));
                getVideosAdapterFree.setVideo_url(Config.dir_video  + json.getString("video_url"));
                getVideosAdapterFree.setPrice(json.getString("price"));
                getVideosAdapterFree.setWatch(json.getString("watch"));
                getVideosAdapterFree.setDownloads(json.getString("downloads"));
                getVideosAdapterFree.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterFree.setCover(Config.dir_poster + json.getString("cover"));
                getVideosAdapterFree.setRelease_date(json.getString("release_date"));
                getVideosAdapterFree.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterFree.setVideoid(json.getString("id"));
                getVideosAdapterFree.setInfo(json.getString("info"));
                getVideosAdapterFree.setSize(json.getString("size"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterFree.add(getVideosAdapterFree);
        }

        recyclerViewAdapterFree = new VideosAdapter(GetVideosAdapterFree, getContext());
        free_recycleview.setAdapter(recyclerViewAdapterFree);
        recyclerViewAdapterFree.notifyDataSetChanged();

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
                getVideosAdapterComingsoon.setWatch(json.getString("watch"));
                getVideosAdapterComingsoon.setDownlaod(json.getString("downloads"));
                getVideosAdapterComingsoon.setPoster(Config.dir_poster + json.getString("poster"));
                getVideosAdapterComingsoon.setCover(Config.dir_poster + json.getString("cover"));
                getVideosAdapterComingsoon.setRelease_date(json.getString("release_date"));
                getVideosAdapterComingsoon.setStatus(Integer.parseInt(json.getString("sstatus")));
                getVideosAdapterComingsoon.setVideoid(json.getString("id"));
                getVideosAdapterComingsoon.setInfo(json.getString("info"));
                getVideosAdapterComingsoon.setSize(json.getString("size"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterComingsoon.add(getVideosAdapterComingsoon);
        }

        recyclerViewAdapterComingSoon = new ComingVideosAdapter(GetVideosAdapterComingsoon, getContext());
        comingsoon_recycleview.setAdapter(recyclerViewAdapterComingSoon);
        recyclerViewAdapterComingSoon.notifyDataSetChanged();
    }





    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.url + "image_slides.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){

                    SliderUtils sliderUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        sliderUtils.setSliderImageUrl(Config.dir_poster + jsonObject.getString("image_url"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);

                }

                viewPagerAdapter = new ViewPagerAdapter(sliderImg, getActivity());

                viewPager.setAdapter(viewPagerAdapter);

                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int i = 0; i < dotscount; i++){

                    dots[i] = new ImageView(getActivity());
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequest.setRetryPolicy(policy);
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }

//    @Override
//    public void onRefresh() {
//        GetSeries();
//        GetVideosTheater();
//        GetvideosComingSoon();
//        sendRequest();
//    }
//


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }




}
