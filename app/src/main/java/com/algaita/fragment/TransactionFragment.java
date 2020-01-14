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

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.MovieInfoActivity;
import com.algaita.activities.RecyclerTouchListener;
import com.algaita.adapters.TransactionAdapter;
import com.algaita.adapters.VideosAdapter;
import com.algaita.models.Transactions;
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

public class TransactionFragment extends Fragment {
    private View view;
    private BaseActivity baseActivity;

    ImageView img_user;
    RecyclerView theaters_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recyler
    List<Transactions> GetVideosAdapterTheater;
    Transactions getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transactions, container, false);


        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());


        //Recycleview
        theaters_recycleview =  view.findViewById(R.id.theaters_recycleview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        theaters_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));

        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        GetVideosTheater();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = new Intent(getContext(), MovieInfoActivity.class);
//                intent.putExtra("title", GetVideosAdapterTheater.get(position).getTitle());
//                intent.putExtra("description", GetVideosAdapterTheater.get(position).getDescription());
//                intent.putExtra("trailer_url", GetVideosAdapterTheater.get(position).getTrailer_url());
//                intent.putExtra("video_url", GetVideosAdapterTheater.get(position).getVideo_url());
//                intent.putExtra("price", GetVideosAdapterTheater.get(position).getPrice());
//                intent.putExtra("poster", GetVideosAdapterTheater.get(position).getPoster());
//                intent.putExtra("release_date", GetVideosAdapterTheater.get(position).getRelease_date());
//                intent.putExtra("status", "out");
//                intent.putExtra("id", GetVideosAdapterTheater.get(position).getVideoid());
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


        return view;

    }



    private void GetVideosTheater() {
        viewDialog.showDialog();
        GetVideosAdapterTheater.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "transactions.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
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
            getVideosAdapterTheater = new Transactions();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getVideosAdapterTheater.setTitle(json.getString("title"));
                getVideosAdapterTheater.setAmount(json.getString("amount"));
                getVideosAdapterTheater.setOndate(json.getString("ondate"));
                getVideosAdapterTheater.setRef(json.getString("ref"));
                getVideosAdapterTheater.setType(json.getString("payment_type"));
                getVideosAdapterTheater.setStatus(json.getString("status"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new TransactionAdapter(GetVideosAdapterTheater, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }

}
