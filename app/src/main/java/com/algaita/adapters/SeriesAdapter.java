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
import com.algaita.models.Series;
import com.bumptech.glide.Glide;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.MyViewHolder> {
    Context context;
    public List<Series> seriesList;


    public SeriesAdapter(List<Series> seriesList, Context context) {
        super();
        this.seriesList = seriesList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, price, release_date;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
//            price = itemView.findViewById(R.id.price);
            release_date = itemView.findViewById(R.id.release_date);
            poster = itemView.findViewById(R.id.poster);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_series, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Series series = seriesList.get(position);
        holder.title.setText(series.getTitle());

//        holder.price.setText("₦" + videos.getPrice());
        Glide.with(context)
                .load(series.getPoster())
//                .centerCrop(150, 150)
//                .resize(150, 150)
//                .fit()
                .override(500,500)
                .placeholder(R.drawable.imgloader)
//                .centerInside()
                .error(R.drawable.applogo)
                .into(holder.poster);

//        Picasso.with(context).load(videos.getPoster()).placeholder(R.drawable.progress_animation).into(holder.poster);
        holder.release_date.setText(series.getRelease_date());



    }


    @Override
    public int getItemCount() {
        return seriesList.size();
    }





}
