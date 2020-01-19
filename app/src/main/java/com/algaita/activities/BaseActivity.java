package com.algaita.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.BuildConfig;
import com.algaita.Config;
import com.algaita.MySingleton;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.ViewDialog;
import com.algaita.fragment.DownloadFragment;
import com.algaita.fragment.HomeFragment;
import com.algaita.fragment.MyVideosFragment;
import com.algaita.fragment.SettingsFragment;
import com.algaita.fragment.TransactionFragment;
import com.algaita.fragment.WalletFragment;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.javiersantos.appupdater.AppUpdater;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class BaseActivity extends AppCompatActivity {
    private String TAG = BaseActivity.class.getSimpleName();

    private FrameLayout frame;
    private View contentView;
    public NavigationView navigationView;
    public RelativeLayout header;
    public DrawerLayout drawer;
    public View navHeader;
    public ImageView menuLeftIV, ivFilter;
    Context mContext;
    TextView txtprofile;
    ViewDialog viewDialog;

    public static final String TAG_DOWNLOADS = "downloads";
    public static final String TAG_HOME = "home";
    public static final String TAG_MYVIDEOS = "my videos";
    public static final String TAG_TRANSACTION = "transactions";
    public static final String TAG_SETTING = "setting";
    public static final String TAG_WALLET = "wallet";

    public static String CURRENT_TAG = TAG_HOME;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private static final float END_SCALE = 0.8f;
    InputMethodManager inputManager;
//    Home home = null;
    private boolean shouldLoadHomeFragOnBackPress = true;
    String type = "";

    EditText et_search;
    ImageView btn_search;

    private TextView tvName, tvWalletBalance, tvOther, tvEnglish;

    SessionHandlerUser sessionHandlerUser;
    private LinearLayout llProfileClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();
        Sentry.init("https://8363b9dd7a5f4c71a6aac7e0b5e4d79b@sentry.io/1522542", new AndroidSentryClientFactory(this));

        mContext = BaseActivity.this;
        mHandler = new Handler();
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionHandlerUser = new SessionHandlerUser(this);

        viewDialog = new ViewDialog(this);
        frame =  findViewById(R.id.frame);
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        contentView = findViewById(R.id.content);
        menuLeftIV =  findViewById(R.id.menuLeftIV);
        ivFilter =  findViewById(R.id.profile);
        et_search =  findViewById(R.id.et_search);
        btn_search =  findViewById(R.id.btn_search);


        navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.tvName);
        txtprofile = navHeader.findViewById(R.id.img_profile);
        tvWalletBalance = navHeader.findViewById(R.id.tvWalletBalance);

        CheckBalance();
        tvEnglish = navHeader.findViewById(R.id.tvEnglish);
        tvOther = navHeader.findViewById(R.id.tvOther);
        tvOther = navHeader.findViewById(R.id.tvOther);
        llProfileClick = navHeader.findViewById(R.id.llProfileClick);


//        tvWalletBalance.setText(sessionHandlerUser.getUserDetail().getEmail());
        tvName.setText(sessionHandlerUser.getUserDetail().getFullname());

        char first = sessionHandlerUser.getUserDetail().getFullname().charAt(0);
        txtprofile.setText(""+first);


        if (savedInstanceState == null) {
            if (type != null) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment(new HomeFragment(), CURRENT_TAG);

            } else {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment(new HomeFragment(), CURRENT_TAG);
            }


        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filter = et_search.getText().toString();
                if (filter.isEmpty()){

                }else{
                    Intent intent = new Intent(BaseActivity.this, SearchActivity.class);
                    intent.putExtra("filter", filter);
                    startActivity(intent);
                }
            }
        });

        menuLeftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerOpen();
            }
        });

        setUpNavigationView();

        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {

                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                     }
                                 }
        );

    }



    private void loadHomeFragment(final Fragment fragment, final String TAG) {

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, TAG);
                fragmentTransaction.commitAllowingStateLoss();
                ivFilter.setVisibility(View.VISIBLE);

            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        drawer.closeDrawers();

        invalidateOptionsMenu();
    }




    public void drawerOpen() {

        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }



    private void setUpNavigationView() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        ivFilter.setVisibility(View.VISIBLE);
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragmentTransaction.replace(R.id.frame, new HomeFragment());
                        break;
                    case R.id.nav_downloads:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DOWNLOADS;
                        fragmentTransaction.replace(R.id.frame, new DownloadFragment());
                        break;

                    case R.id.nav_myvideos:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MYVIDEOS;
                        fragmentTransaction.replace(R.id.frame, new MyVideosFragment());
                        break;


                    case R.id.nav_transactions:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_TRANSACTION;
                        fragmentTransaction.replace(R.id.frame, new TransactionFragment());
                        break;

                    case R.id.nav_setting:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTING;
                        fragmentTransaction.replace(R.id.frame, new SettingsFragment());
                        break;

                    case R.id.nav_wallet:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_WALLET;
                        fragmentTransaction.replace(R.id.frame, new WalletFragment());
                        break;

                    case R.id.nav_feedback:
                        showDialogFeedback();
                        break;

                    case R.id.nav_help:
                        showDialogHelp();
                        break;

                    case R.id.nav_share:

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Saukar Da Manhajar Algaita Dub Studio, don Masge Kallon Sabbin Fasssara Cikin Sauki: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;

                    case R.id.nav_logout:
                        sessionHandlerUser.logoutUser();
                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        ivFilter.setVisibility(View.VISIBLE);
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragmentTransaction.replace(R.id.frame, new HomeFragment());
                        break;

                }
                fragmentTransaction.commitAllowingStateLoss();
                drawer.closeDrawers();

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //   loadHomeFragment();

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment(new HomeFragment(), CURRENT_TAG);
                return;
            }
        }

        //super.onBackPressed();
        clickDone();
    }


    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.closeMsg))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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

                                tvWalletBalance.setText("₦" + response.getString("balance"));
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }



    private void showDialogFeedback() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText text;
        text = dialog.findViewById(R.id.message);



        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SendFeedBack(text.getText().toString());
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }



    private void showDialogHelp() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_help);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;



        ((View) dialog.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void SendFeedBack(String toString) {
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
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("message", toString);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                String result = rh.sendPostRequest(Config.url + "feedback.php",data);

                return result;
            }
        }

        chargee ui = new chargee();
        ui.execute();
    }


}
