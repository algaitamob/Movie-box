package com.algaita.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.algaita.R;
import com.algaita.fragment.DownloadFragment;
import com.algaita.fragment.HomeFragment;
import com.algaita.fragment.MyVideosFragment;
import com.algaita.fragment.TransactionFragment;
import com.algaita.sessions.SessionHandlerUser;

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

    public static final String TAG_DOWNLOADS = "downloads";
    public static final String TAG_HOME = "home";
    public static final String TAG_MYVIDEOS = "my videos";
    public static final String TAG_TRANSACTION = "transactions";

    public static String CURRENT_TAG = TAG_HOME;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private static final float END_SCALE = 0.8f;
    InputMethodManager inputManager;
//    Home home = null;
    private boolean shouldLoadHomeFragOnBackPress = true;
    String type = "";

    private TextView tvName, tvEmail, tvOther, tvEnglish;

    SessionHandlerUser sessionHandlerUser;
    private LinearLayout llProfileClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mContext = BaseActivity.this;
        mHandler = new Handler();
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionHandlerUser = new SessionHandlerUser(this);

        frame = (FrameLayout) findViewById(R.id.frame);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        contentView = findViewById(R.id.content);
        menuLeftIV = (ImageView) findViewById(R.id.menuLeftIV);
        ivFilter = (ImageView) findViewById(R.id.profile);


        navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.tvName);
        txtprofile = navHeader.findViewById(R.id.img_profile);
        tvEmail = navHeader.findViewById(R.id.tvEmail);

        tvEnglish = navHeader.findViewById(R.id.tvEnglish);
        tvOther = navHeader.findViewById(R.id.tvOther);
        tvOther = navHeader.findViewById(R.id.tvOther);
        llProfileClick = navHeader.findViewById(R.id.llProfileClick);


        tvEmail.setText(sessionHandlerUser.getUserDetail().getEmail());
        tvName.setText(sessionHandlerUser.getUserDetail().getFullname());

        char first = sessionHandlerUser.getUserDetail().getFullname().charAt(0);
        txtprofile.setText(""+first);


        if (savedInstanceState == null) {
            if (type != null) {
                if (type.equalsIgnoreCase("10007")) {
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment(new DownloadFragment(), CURRENT_TAG);
                } else {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_DOWNLOADS;
                    loadHomeFragment(new HomeFragment(), CURRENT_TAG);
                }
            } else {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment(new HomeFragment(), CURRENT_TAG);
            }


        }

        menuLeftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerOpen();
            }
        });

        setUpNavigationView();
//        Menu menu = navigationView.getMenu();
//
//        changeColorItem(menu, R.id.nav_home_features);
//        changeColorItem(menu, R.id.nav_bookings_and_job);
//        changeColorItem(menu, R.id.nav_personal);
//        changeColorItem(menu, R.id.other);
//
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem mi = menu.getItem(i);
//            SubMenu subMenu = mi.getSubMenu();
//            if (subMenu != null && subMenu.size() > 0) {
//                for (int j = 0; j < subMenu.size(); j++) {
//                    MenuItem subMenuItem = subMenu.getItem(j);
//                    applyCustomFont(subMenuItem);
//                }
//            }
//            applyCustomFont(mi);
//        }


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
                        fragmentTransaction.replace(R.id.frame, new DownloadFragment());
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


}
