package com.algaita;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

/**
 * Prevent screenshot on multiple levels.
 */
public final class PreventScreenshot {

    private PreventScreenshot() {
    }

    public static void on(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static void on(Activity activity) {
        PreventScreenshot.on(activity.getWindow());
    }

    public static void on(Fragment fragment) {
        PreventScreenshot.on(fragment.getActivity());
    }

    public static void on(WindowManager.LayoutParams layoutParams) {
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_SECURE;
    }

    public static void on(Dialog dialog) {
        on(dialog.getWindow());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void on(SurfaceView surfaceView) {
        surfaceView.setSecure(true);
    }

}