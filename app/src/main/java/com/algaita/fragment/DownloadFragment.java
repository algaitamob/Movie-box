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
import android.widget.Toast;


import com.algaita.R;
import com.algaita.RecyclerClickListener;
import com.algaita.ViewDialog;
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

        new LoadSongs().execute();



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(getActivity(), arrayList.get(position).getMp3Url(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("name", arrayList.get(position).getMp3Name());
                intent.putExtra("uri", arrayList.get(position).getMp3Url());
                startActivity(intent);
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
                adapterSongList = new AdapterVIdeoList(getActivity(), arrayList, new RecyclerClickListener() {
                    @Override
                    public void onClick(int position) {
//                        Constant.isOnline = false;
//                        Constant.frag = "download";
//                        Constant.arrayList_play.clear();
//                        Constant.arrayList_play.addAll(arrayList);
//                        Constant.playPos = position;
//                        ((MainActivity) getActivity()).changeText(arrayList.get(position).getMp3Name(), arrayList.get(position).getArtist(), position + 1, arrayList.size(), arrayList.get(position).getDuration(), arrayList.get(position).getBitmap(), "download");
//

                        Toast.makeText(getActivity(), "heeeeee", Toast.LENGTH_LONG).show();
//                        Constant.context = getActivity();
                        Intent intent = new Intent(getActivity(), PlayerService.class);
                        intent.putExtra("name", arrayList.get(position).getMp3Name());
                        intent.putExtra("uri", arrayList.get(position).getMp3Url());
//                        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
//                        getActivity().startService(intent);
                        startActivity(intent);
                    }
                }, "offline");
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


    private void loadDownloaded() {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
        File[] songs = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp4");
            }
        });

        if (songs != null) {
            for (int i = 0; i < songs.length; i++) {

                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(songs[i].getAbsolutePath());
                String title = songs[i].getName();
                String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                duration = JsonUtils.milliSecondsToTimerDownload(Long.parseLong(duration));
                String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String url = songs[i].getAbsolutePath();

                String desc = getString(R.string.title) + " - " + title + "</br>" +
                        getString(R.string.artist) + " - " + artist;

                ItemVideos itemSong = new ItemVideos(String.valueOf(i), title.substring(0, title.length() - 4), url.substring(0, url.length() - 4));
                arrayList.add(itemSong);
            }
        }
    }


}
