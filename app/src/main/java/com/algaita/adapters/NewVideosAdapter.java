package com.algaita.adapters;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.algaita.R;
import com.algaita.fragment.DownloadFragment;
import com.algaita.models.ItemVideos;

import me.gujun.android.taggroup.TagGroup;
import modalclass.ShowTimeModalClass;


public class NewVideosAdapter extends RecyclerView.Adapter<NewVideosAdapter.MyViewHolder> {

    Context context;
    private List<ItemVideos> videosList;


    public NewVideosAdapter(ArrayList<ItemVideos> videosList, Context context) {
        this.videosList = videosList;
        this.context = context;
    }

    @Override
    public NewVideosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_videolist, parent, false);



        return new NewVideosAdapter.MyViewHolder(itemView);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final NewVideosAdapter.MyViewHolder holder, final int position) {
        ItemVideos modalClass = videosList.get(position);
        holder.name.setText(modalClass.getMp3Name());

//        holder.tagGroup.setTags(new String[]{"10:20 AM", "01:20 PM", "04:20 PM","07:20 PM","10:20 PM"});


    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;
        TagGroup tagGroup;


        public MyViewHolder(View view) {
            super(view);


            name = (TextView) view.findViewById(R.id.textView_songname);
//            tagGroup = (TagGroup) view.findViewById(R.id.tag_group);

        }

    }
}
