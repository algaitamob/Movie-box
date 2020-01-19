package com.algaita.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        GetVideosTheater();
        GetVideosTheater1();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String name = GetVideosAdapterTheater.get(position).getTitle();
                String amount = GetVideosAdapterTheater.get(position).getAmount();
                String ondate = GetVideosAdapterTheater.get(position).getOndate();
                String ref = GetVideosAdapterTheater.get(position).getRef();
                String type = GetVideosAdapterTheater.get(position).getType();
                showDialogReceipt(name, amount, ondate, ref, type);
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


    private void GetVideosTheater1() {
//        viewDialog.showDialog();
//        GetVideosAdapterTheater.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "transactions_wallet.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                GetCardWebCall1(response);
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

    private void GetCardWebCall1(JSONArray array) {
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

    private void showDialogReceipt(String name, String amount, String ondate, String ref, String type) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_payment_receipt);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView tname, tamount, tondate, tref, ttype;

        tname = dialog.findViewById(R.id.name);
        tamount = dialog.findViewById(R.id.amount);
        tondate = dialog.findViewById(R.id.date);
        ttype = dialog.findViewById(R.id.payment_type);
        tref = dialog.findViewById(R.id.ref);

        tname.setText(name);
        tamount.setText("₦"+amount);
        tondate.setText(ondate);
        ttype.setText(type);
        tref.setText(ref);

        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
