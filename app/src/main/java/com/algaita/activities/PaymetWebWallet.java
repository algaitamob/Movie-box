package com.algaita.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.ViewDialog;
import com.algaita.sessions.SessionHandlerUser;
import com.algaita.utils.ProjectUtils;

public class PaymetWebWallet extends AppCompatActivity {
    SessionHandlerUser sessionHandlerUser;
    private Context mContext;
    private WebView wvPayment;
    private ImageView IVback;
    private String PAYMENT_FAIL;
    private String PAYMENT_SUCCESS;

    ViewDialog viewDialog;


    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymet_web);
        mContext = PaymetWebWallet.this;
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        viewDialog = new ViewDialog(this);

        Intent intent = getIntent();

        String payment = intent.getStringExtra("type");
        if (payment.contains("paystack")){
            PAYMENT_FAIL = Config.url + "credit/fail.php";
            PAYMENT_SUCCESS = Config.url + "credit/success.php";
            url = Config.url + "credit/add_to_wallet.php?userid="+sessionHandlerUser.getUserDetail().getUserid() +  "&amount=" + intent.getStringExtra("amount");
        }else{
            PAYMENT_FAIL = Config.url + "airtime/fail.php";
            PAYMENT_SUCCESS = Config.url + "airtime/success.php";
            url = Config.url + "airtime/add_to_wallet.php?userid="+sessionHandlerUser.getUserDetail().getUserid() + "&amount=" + intent.getStringExtra("amount");
        }

        wvPayment = findViewById(R.id.wvPayment);


        WebSettings settings = wvPayment.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        wvPayment.loadUrl(url);

        wvPayment.setWebViewClient(new SSLTolerentWebViewClient());
        init();
    }

    private void init() {
        IVback = (ImageView) findViewById(R.id.IVback);
        IVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_event));
                finish();
            }
        });
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            viewDialog.hideDialog();
            // this will ignore the Ssl error and will go forward to your site
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            viewDialog.showDialog();
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            viewDialog.showDialog();

            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ProjectUtils.pauseProgressDialog();
            //Page load finished
            if (url.equals(PAYMENT_SUCCESS)) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Payment was successfully");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                super.onPageFinished(view, PAYMENT_SUCCESS);
                Intent intent = new Intent(PaymetWebWallet.this, BaseActivity.class);
                startActivity(intent);
                finish();
                wvPayment.clearCache(true);

                wvPayment.clearHistory();

                wvPayment.destroy();
            } else if (url.equals(PAYMENT_FAIL)) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Payment fail");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
//                ProjectUtils.showToast(mContext, "Payment fail.");
                //view.loadUrl("https://www.youtube.com");
                super.onPageFinished(view, PAYMENT_FAIL);
                Intent intent = new Intent(PaymetWebWallet.this, BaseActivity.class);
                startActivity(intent);
                finish();

                wvPayment.clearCache(true);

                wvPayment.clearHistory();

                wvPayment.destroy();
            } else {
                super.onPageFinished(view, url);
            }

            viewDialog.hideDialog();

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO Auto-generated method stub

            try {
                wvPayment.stopLoading();
            } catch (Exception e) {
            }

            if (wvPayment.canGoBack()) {
                wvPayment.goBack();
            }

            wvPayment.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(PaymetWebWallet.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });
            alertDialog.show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


}
