package com.algaita.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algaita.R;
import com.algaita.models.Videos;
import com.bumptech.glide.Glide;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {
    Context context;
    public List<Videos> videosList;


    public VideosAdapter(List<Videos> videosList, Context context) {
        super();
        this.videosList = videosList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, price, release_date;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            release_date = itemView.findViewById(R.id.release_date);
            poster = itemView.findViewById(R.id.poster);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thaters, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Videos videos = videosList.get(position);
        holder.title.setText(videos.getTitle());
        holder.release_date.setText(videos.getRelease_date());

        if(videos.getPrice().startsWith("0")){
            holder.price.setText("FREE");
        }else{
            holder.price.setText("₦" + videos.getPrice());
        }
        Glide.with(context)
                .load(videos.getPoster())
//                .centerCrop(150, 150)
//                .resize(150, 150)
//                .fit()
//                .override(500,500)
                .placeholder(R.drawable.imgloader)
//                .centerInside()
                .error(R.drawable.applogo)
                .into(holder.poster);

//
//        Picasso.with(context).load(videos.getPoster()).placeholder(R.mipmap.ic_launcher).fit().into(holder.poster, new Callback() {
//            @Override public void onSuccess() {}
//            @Override public void onError() {}
//        });


//        Picasso.with(context).load(videos.getPoster()).placeholder(R.drawable.progress_animation).into(holder.poster);

    }


    @Override
    public int getItemCount() {
        return videosList.size();
    }


}
