package com.algaita.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.ViewDialog;
import com.algaita.sessions.SessionHandlerUser;

import java.util.HashMap;

public class ChargeWallet extends AppCompatActivity {
    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_paymet_web);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(this);
        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String videoid = intent.getStringExtra("videoid");
        ChargeWallett(amount, videoid);
    }

    private void ChargeWallett(String amount, String videoid) {
        viewDialog.showDialog();
        class chargee extends AsyncTask<Bitmap,Void,String> {
            RequestHandler rh = new RequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                viewDialog.hideDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                viewDialog.hideDialog();
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                onBackPressed();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("amount", amount);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                data.put("videoid", videoid);
                String result = rh.sendPostRequest(Config.url + "charge_wallet.php?amount=" + amount + "&userid=" + sessionHandlerUser.getUserDetail().getUserid() + "&videoid=" + videoid,data);
                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }


    @Override
    // This method is used to detect back button
    public void onBackPressed() {

        setResult(RESULT_OK);
            // Let the system handle the back button
            super.onBackPressed();



    }


}
