package com.algaita.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algaita.R;
import com.algaita.models.Downloads;
import com.algaita.models.Videos;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class VideoDownloadAdapter extends RecyclerView.Adapter<VideoDownloadAdapter.MyViewHolder> {
    Context context;
    public ArrayList<Downloads> downloadsList;


    public VideoDownloadAdapter(ArrayList<Downloads> downloadsList, Context context) {
        super();
        this.downloadsList = downloadsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_songname);


        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_videolist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Downloads downloads = downloadsList.get(position);
        holder.title.setText(downloads.getTitle());


    }


    @Override
    public int getItemCount() {
        return downloadsList.size();
    }


}
