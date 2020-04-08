package com.algaita;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;

public class MyApplicationContext extends Application {
    private Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setupActivityListener();
        MobileAds.initialize(this,"ca-app-pub-7803700300545861~3517043491");

    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

            }

            @Override
            public void onActivityStarted(Activity activity) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

            }

            @Override
            public void onActivityResumed(Activity activity) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

}
