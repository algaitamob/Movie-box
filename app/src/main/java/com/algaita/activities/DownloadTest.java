package com.algaita.activities;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.algaita.NLService;
import com.algaita.R;

public class DownloadTest extends AppCompatActivity {

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    Button btn;
    int id = 1;
    String urlsToDownload[] = { "https://paymable.ng/demo/images/slider/1.png", "https://paymable.ng/demo/images/slider/1.png",
            "https://paymable.ng/demo/images/slider/2.png", "http://www.devoxx.be/wp-content/uploads/2014/10/android.png",
            "http://www.devoxx.be/wp-content/uploads/2014/10/android.png" };
    int counter = 0;
    private NotificationReceiver nReceiver;
    ArrayList<AsyncTask<String, String, Void>> arr;

    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getExtras().getString(NLService.NOT_EVENT_KEY);
            Log.i("NotificationReceiver", "NotificationReceiver onReceive : " + event);
            if (event.trim().contentEquals(NLService.NOT_REMOVED)) {
                killTasks();
            }
        }
    }

    private void killTasks() {
        if (null != arr & arr.size() > 0) {
            for (AsyncTask<String, String, Void> a : arr) {
                if (a != null) {
                    Log.i("NotificationReceiver", "Killing download thread");
                    a.cancel(true);
                }
            }
            mNotifyManager.cancelAll();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        btn = findViewById(R.id.button);


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Downloading images...").setContentText("Download in progress").setSmallIcon(R.drawable.applogo);
        // Start a lengthy operation in a background thread
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(id, mBuilder.build());
        mBuilder.setAutoCancel(true);

        arr = new ArrayList<AsyncTask<String, String, Void>>();
        int incr;
        for (incr = 0; incr < urlsToDownload.length; incr++) {
            ImageDownloader imageDownloader = new ImageDownloader();
            imageDownloader.execute(urlsToDownload[incr]);
            arr.add(imageDownloader);
        }

        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();

        // check to see if the enabledNotificationListeners String contains our
        // package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app
            // the Notification access permission
            // Check if notification is enabled for this application
            Log.i("ACC", "Dont Have Notification access");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            Log.i("ACC", "Have Notification access");
        }

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NLService.NOT_TAG);
        registerReceiver(nReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killTasks();
        unregisterReceiver(nReceiver);
    }

    private void downloadImagesToSdCard(String downloadUrl, String imageName) {
        FileOutputStream fos;
        InputStream inputStream = null;

        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(sdCard, "DemoDownload");

            /* if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File(myDir, fname);
            Log.d("file===========path", "" + file);
            if (file.exists())
                file.delete();

            /* Open a connection */
            URLConnection ucon = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();

            /*
             * if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             * inputStream = httpConn.getInputStream(); }
             */

            fos = new FileOutputStream(file);
            // int totalSize = httpConn.getContentLength();
            // int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                // downloadedSize += bufferLength;
                // Log.i("Progress:", "downloadedSize:" + downloadedSize +
                // "totalSize:" + totalSize);
            }
            inputStream.close();
            fos.close();
            Log.d("test", "Image Saved in sdcard..");
        } catch (IOException io) {
            inputStream = null;
            fos = null;
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private class ImageDownloader extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... param) {
            downloadImagesToSdCard(param[0], "Image" + counter + ".png");
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("Async-Example", "onPostExecute Called");

            float len = urlsToDownload.length;
            // When the loop is finished, updates the notification
            if (counter >= len - 1) {
                mBuilder.setContentTitle("Done.");
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            } else {
                int per = (int) (((counter + 1) / len) * 100f);
                Log.i("Counter", "Counter : " + counter + ", per : " + per);
                mBuilder.setContentText("Downloaded (" + per + "/100");
                mBuilder.setProgress(100, per, false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(id, mBuilder.build());
            }
            counter++;

        }

    }
}