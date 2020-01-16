package com.algaita.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.activities.AddWalletBalanceActivity;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.RecyclerTouchListener;
import com.algaita.adapters.TransactionAdapter;
import com.algaita.adapters.WalletTransactionAdapter;
import com.algaita.models.Transactions;
import com.algaita.models.WalletTransactions;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
    private View view;
    private BaseActivity baseActivity;

    ImageView img_user;
    RecyclerView theaters_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recylerview
    List<WalletTransactions> GetVideosAdapterTheater;
    WalletTransactions getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    LinearLayout card_add_wallet_balance;

    TextView txtbalance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false);


        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());

        txtbalance = view.findViewById(R.id.balance);

        card_add_wallet_balance = view.findViewById(R.id.card_add_money);

        card_add_wallet_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddWalletBalanceActivity.class);
                startActivity(intent);
            }
        });
        CheckBalance();




        //Recycleview
        theaters_recycleview =  view.findViewById(R.id.theaters_recycleview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        GetVideosTheater();
        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                String name = GetVideosAdapterTheater.get(position).getTitle();
                String amount = GetVideosAdapterTheater.get(position).getAmount();
                String ondate = GetVideosAdapterTheater.get(position).getOndate();
                String ref = GetVideosAdapterTheater.get(position).getRef();
                String type = GetVideosAdapterTheater.get(position).getType();
                showDialogReceipt(amount, ondate, ref, type);
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
        jsonArrayRequest = new JsonArrayRequest(Config.url + "wallet_transactions.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
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
            getVideosAdapterTheater = new WalletTransactions();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
//                getVideosAdapterTheater.setTitle(json.getString("title"));
                getVideosAdapterTheater.setAmount(json.getString("amount"));
                getVideosAdapterTheater.setOndate(json.getString("ondate"));
                getVideosAdapterTheater.setRef(json.getString("ref"));
                getVideosAdapterTheater.setType(json.getString("type"));
                getVideosAdapterTheater.setCurrent_balance(json.getString("current_balance"));
                getVideosAdapterTheater.setStatus(json.getString("status"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetVideosAdapterTheater.add(getVideosAdapterTheater);
        }

        recyclerViewAdapterTheater = new WalletTransactionAdapter(GetVideosAdapterTheater, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }

    private void showDialogReceipt(String amount, String ondate, String ref, String type) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_wallet_receipt);
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

//        tname.setText(name);
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


    private void CheckBalance() {
        String url_ = Config.user_wallet+"?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 0) {

                                txtbalance.setText("₦" + response.getString("balance"));
                                sessionHandlerUser.WalletBalance(response.getString("balance"));

                            } else {

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
        MySingleton.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
    }

}
