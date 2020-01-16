package com.algaita.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.R;
import com.algaita.ViewDialog;
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
import java.util.HashMap;
import java.util.List;

public class AddWalletBalanceActivity extends AppCompatActivity {
    TextView txt;
    TextView one,two,three,four,five,six,seven,eight,nine,zero;
    ImageView clear, add, back;
    EditText edtxt;
    String nos,number="";

    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ViewDialog viewDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet_balance);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());


        edtxt=findViewById(R.id.edtxt);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txt=findViewById(R.id.txt);

        txt.setText("Wallet");
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = edtxt.getText().toString();

                if(amount.isEmpty()){

                }else{
//
                    showBottomSheetDialog();
                }

            }
        });

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }




//    Bottom Sheet Pay
    private void showBottomSheetDialog() {
    if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    final View view = getLayoutInflater().inflate(R.layout.sheet_select_payment_method, null);

    (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mBottomSheetDialog.hide();
        }
    });

        LinearLayout card_airtime, card_credit;
        CardView  card_wallet;
        card_credit = view.findViewById(R.id.card_credit);
        card_airtime = view.findViewById(R.id.card_airtime);
        card_wallet = view.findViewById(R.id.card_wallet_remove);

        card_wallet.setVisibility(View.GONE);
        card_airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(AddWalletBalanceActivity.this, PaymetWebWallet.class);
                intent.putExtra("type", "airtime");
                intent.putExtra("amount", edtxt.getText().toString());
//                intent.putExtra("videoid", i.getStringExtra("id"));
                startActivity(intent);
            }
        });

        card_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                Intent intent = new Intent(AddWalletBalanceActivity.this, PaymetWebWallet.class);
                intent.putExtra("type", "paystack");
                intent.putExtra("amount", edtxt.getText().toString());
//                intent.putExtra("videoid", i.getStringExtra("id"));

                startActivity(intent);
            }
        });

    mBottomSheetDialog = new BottomSheetDialog(this);
    mBottomSheetDialog.setContentView(view);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    // set background transparent
    ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    mBottomSheetDialog.show();
    mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mBottomSheetDialog = null;
        }
    });

}




}
