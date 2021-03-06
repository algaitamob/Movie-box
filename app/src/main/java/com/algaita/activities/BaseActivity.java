package com.algaita.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
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
import com.algaita.fragment.NotificationFragment;
import com.algaita.fragment.SettingsFragment;
import com.algaita.fragment.TransactionFragment;
import com.algaita.fragment.WalletFragment;
import com.algaita.sessions.SessionHandlerUser;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.javiersantos.appupdater.AppUpdater;
import com.wooplr.spotlight.SpotlightView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;


public class BaseActivity extends AppCompatActivity {
    private String TAG = BaseActivity.class.getSimpleName();
    private static final String SHOWCASE_ID = "Simple Showcase";
    private static final String SHOWCASE_ID3 = "VideoList";
    private FrameLayout frame;
    private View contentView;
    public NavigationView navigationView;
    public RelativeLayout header;
    public DrawerLayout drawer;
    public View navHeader;
    public ImageView menuLeftIV, ivFilter, notify;
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
    private boolean shouldLoadHomeFragOnBackPress = true;
    String type = "";
    EditText et_search;
    ImageView btn_search;
    private TextView tvName, tvWalletBalance;
    SessionHandlerUser sessionHandlerUser;
    private LinearLayout llProfileClick, shape;
    private FloatingActionButton fab;
//    boolean connected = false;
    private String verison = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_base);
//        AppUpdater appUpdater = new AppUpdater(this);
//        appUpdater.start();

//        CheckNetwork();
        fab = findViewById(R.id.fab);
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
        ivFilter =  findViewById(R.id.movie);
        notify =  findViewById(R.id.notify);
        et_search =  findViewById(R.id.et_search);
        shape =  findViewById(R.id.shape);
        btn_search =  findViewById(R.id.btn_search);
        navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.tvName);
        txtprofile = navHeader.findViewById(R.id.img_profile);
        tvWalletBalance = navHeader.findViewById(R.id.tvWalletBalance);
        CheckBalance();

        if (sessionHandlerUser.isLoggedIn()){

        }else{
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, LiveChat.class);
                startActivity(intent);
            }
        });
//        showT();
        showT5();
//        llProfileClick = navHeader.findViewById(R.id.llProfileClick);

        tvName.setText(sessionHandlerUser.getUserDetail().getFullname());
        char first = sessionHandlerUser.getUserDetail().getFullname().charAt(0);
        txtprofile.setText(""+first);

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, PlayYoutubeActivity.class);
                startActivity(intent);
            }
        });

//        notify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(BaseActivity.this, PlayYoutubeActivity.class);
//                startActivity(intent);
//            }
//        });
        if (savedInstanceState == null) {
            if (type != null) {
                    if (isNetworkAvailable() == true){
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        loadHomeFragment(new HomeFragment(), CURRENT_TAG);
                    }else{
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DOWNLOADS;
                        loadHomeFragment(new DownloadFragment(), CURRENT_TAG);
                    }

            } else {
                if (isNetworkAvailable() == true){
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment(new HomeFragment(), CURRENT_TAG);
                }else{
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_DOWNLOADS;
                    loadHomeFragment(new DownloadFragment(), CURRENT_TAG);
                }
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



        checkUpdates();

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

    private void loadDownloadFragment(final Fragment fragment, final String TAG) {
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
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        if (isNetworkAvailable() == true) {
                            fragmentTransaction.replace(R.id.frame, new HomeFragment());
                        }else{
                            fragmentTransaction.replace(R.id.frame, new DownloadFragment());
                        }
                        break;
                    case R.id.nav_downloads:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DOWNLOADS;
                        fragmentTransaction.replace(R.id.frame, new DownloadFragment());
                        break;

                    case R.id.nav_myvideos:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MYVIDEOS;
                        fragmentTransaction.replace(R.id.frame, new MyVideosFragment());
                        break;


                    case R.id.nav_transactions:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_TRANSACTION;
                        fragmentTransaction.replace(R.id.frame, new TransactionFragment());
                        break;

                    case R.id.nav_setting:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTING;
                        fragmentTransaction.replace(R.id.frame, new SettingsFragment());
                        break;

                    case R.id.nav_wallet:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_WALLET;
                        fragmentTransaction.replace(R.id.frame, new WalletFragment());
                        break;

                    case R.id.nav_notification:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_WALLET;
                        fragmentTransaction.replace(R.id.frame, new NotificationFragment());
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
                                "Saukar Da Manhajar Algaita Movie Box, don Masge Kallon Sabbin Fasssara Cikin Sauki: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
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
                        if (isNetworkAvailable() == true){
                            navItemIndex = 0;
                            CURRENT_TAG = TAG_HOME;
                            fragmentTransaction.replace(R.id.frame, new HomeFragment());
                        }else{
                            navItemIndex = 0;
                            CURRENT_TAG = TAG_HOME;
                            fragmentTransaction.replace(R.id.frame, new DownloadFragment());
                        }
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
                if (isNetworkAvailable() == true) {
                    loadHomeFragment(new HomeFragment(), CURRENT_TAG);
                }else{
                    loadHomeFragment(new DownloadFragment(), CURRENT_TAG);

                }
                return;
            }
        }
        clickDone();
//        finish();
    }
//
    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.oldicon)
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrayRequest.setRetryPolicy(policy);
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

    private void showT5(){
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Search")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Search an Amazing Movie!\n")
                .maskColor(Color.parseColor("#dc000000"))
                .target(shape)
                .lineAnimDuration(400)
                .lineAndArcColor(R.color.colorPrimary)
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1") //UNIQUE ID
                .show();
    }

    private void showT(){
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Movies")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Show Movies List!\n")
                .maskColor(Color.parseColor("#dc000000"))
                .target(ivFilter)
                .lineAnimDuration(400)
                .lineAndArcColor(R.color.colorPrimary)
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("2") //UNIQUE ID
                .show();
    }

    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(400);
            slide.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }




    private void checkUpdates() {
        viewDialog.showDialog();
        String url_ = Config.url + "update.php";
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {
                                if(response.getString("updates").contains("1")){
                                    if(response.getString("version").contains(verison)){
                                        AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
                                        alertDialog.setTitle("Attention!");
                                        alertDialog.setMessage("New Update Available! Update Algaita Movie Box App now");
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "UPDATE APP", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                        startActivity(getIntent());
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }


                                            }
                                        });
                                        alertDialog.show();
                                    }

                                }else{
//                                    loadDashboard();

                                }


                            } else {

//                                loadDashboard();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }



}
