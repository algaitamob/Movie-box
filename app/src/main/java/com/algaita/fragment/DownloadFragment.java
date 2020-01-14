package com.algaita.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.algaita.R;
import com.algaita.RecyclerClickListener;
import com.algaita.ViewDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment {

//
//    RecyclerView recyclerView;
//    RecyclerView.Adapter recylerviewadapter;
//    ArrayList<ItemVideos> arrayList;
//    AdapterVIdeoList adapterSongList;
//    LinearLayoutManager linearLayoutManager;

    RecyclerView theaters_recycleview;
    ArrayList<Downloads> GetVideosAdapterTheater;
    Downloads getVideosAdapterTheater;
    RecyclerView.Adapter recyclerViewAdapterTheater;


    ViewDialog viewDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);

        viewDialog = new ViewDialog(getActivity());

        //Recycleview
        theaters_recycleview =  rootView.findViewById(R.id.recyclerView_downloads);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        theaters_recycleview.setLayoutManager(new GridLayoutManager(getContext(), 2));

        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetVideosAdapterTheater = new ArrayList<>();

        loadDownloaded();

        return rootView;
    }

//    private void GetDownloads() {
//
//    }
//
//    private class LoadSongs extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            viewDialog.showDialog();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            loadDownloaded();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (getActivity() != null) {
//                viewDialog.hideDialog();
//
//                recylerviewadapter = new NewVideosAdapter(arrayList, getContext());
//
////                adapterSongList = new NewVideosAdapter(arrayList, getActivity());
//                if (arrayList.size() == 0) {
//                    recyclerView.setVisibility(View.GONE);
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//                super.onPostExecute(s);
//            }
//        }
//    }

    private void loadDownloaded() {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
        File[] songs = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".alg");
            }
        });



        if (songs != null) {
            for (int i = 0; i < songs.length; i++){
                getVideosAdapterTheater = new Downloads();
                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(songs[i].getAbsolutePath());


                getVideosAdapterTheater.setTitle(songs[i].getName());
                getVideosAdapterTheater.setmyUrl(songs[i].getAbsolutePath());
                GetVideosAdapterTheater.add(getVideosAdapterTheater);
            }

            recyclerViewAdapterTheater = new VideoDownloadAdapter(GetVideosAdapterTheater, getActivity());
            theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
            recyclerViewAdapterTheater.notifyDataSetChanged();

        }

    }




}
