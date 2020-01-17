package com.algaita.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.algaita.R;
import com.algaita.RecyclerClickListener;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.MovieInfoActivity;
import com.algaita.activities.PlayerService;
import com.algaita.activities.RecyclerTouchListener;
import com.algaita.activities.ShowTimeActivity;
import com.algaita.adapters.AdapterVIdeoList;
import com.algaita.adapters.DownloadsAdapter;
import com.algaita.adapters.NewVideosAdapter;
import com.algaita.adapters.TransactionAdapter;
import com.algaita.adapters.VideoDownloadAdapter;
import com.algaita.adapters.VideosAdapter;
import com.algaita.models.Downloads;
import com.algaita.models.ItemVideos;
import com.algaita.models.Transactions;
import com.algaita.player.CustomVideoPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment {


    RecyclerView recyclerView;
    ArrayList<ItemVideos> arrayList;
    public static AdapterVIdeoList adapterSongList;
    LinearLayoutManager linearLayoutManager;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;


    ViewDialog viewDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
        viewDialog = new ViewDialog(getActivity());
        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_downloads);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        checkPermissions();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = new Intent(getActivity(), PlayerService.class);
//                intent.putExtra("name", arrayList.get(position).getMp3Name());
//                intent.putExtra("uri", arrayList.get(position).getMp3Url());
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        return rootView;
    }



    private class LoadSongs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            viewDialog.showDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            loadDownloaded();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (getActivity() != null) {
                viewDialog.hideDialog();
//                adapterSongList = new AdapterVIdeoList(getActivity(), arrayList, new RecyclerClickListener() {
//                    @Override
//                    public void onClick(int position) {
//
//                    }
//                }, "offline");

                adapterSongList = new AdapterVIdeoList(getActivity(), arrayList, new AdapterVIdeoList.DetailsAdapterListener() {
                    @Override
                    public void classOnClick(View v, int position) {
//                        String id = GetFoodAdaper1.get(position).getId();
//                        AddToCart(id);

//                        File dir = new File(arrayList.get(position).getMp3Url());
//                        File file = new File(arrayList.get(position).getMp3Url());
//                        boolean deleted = file.delete();

                        new File(arrayList.get(position).getMp3Url()).delete();

//                        arrayList.get(position).getMp3Url();
                        Toast.makeText(getContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(getActivity(), BaseActivity.class);
                                        startActivity(intent);

                    }

                    @Override
                    public void favOnclick(View v, int position) {
                        Intent intent = new Intent(getActivity(), PlayerService.class);
                        intent.putExtra("name", arrayList.get(position).getMp3Name());
                        intent.putExtra("uri", arrayList.get(position).getMp3Url());
                        startActivity(intent);
                    }

                });
                recyclerView.setAdapter(adapterSongList);
                if (arrayList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(s);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }

    private void loadDownloaded() {

        File root = new File("/data/data/" + getActivity().getPackageName() + "/files/");
//        File[] songs = root.listFiles();
        File[] songs = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp4");
            }
        });

        if (songs != null) {
            for (int i = 0; i < songs.length; i++) {
                String title = songs[i].getName();
                String url = songs[i].getAbsolutePath();

                ItemVideos itemSong = new ItemVideos(String.valueOf(i), title.substring(0, title.length() - 4), url);
                arrayList.add(itemSong);
            }
        }
    }


    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            new LoadSongs().execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            new LoadSongs().execute();
        }
    }




}
