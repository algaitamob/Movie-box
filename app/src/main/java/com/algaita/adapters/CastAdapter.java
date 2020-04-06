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
import com.algaita.models.Cast;
import com.bumptech.glide.Glide;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder> {
    Context context;
    public List<Cast> castList;


    public CastAdapter(List<Cast> castList, Context context) {
        super();
        this.castList = castList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;

        ImageView img;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cast_name);
            img = itemView.findViewById(R.id.cast_image);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.name.setText(cast.getCast_name());
        Glide.with(context)
                .load(cast.getImg())
//                .centerCrop(150, 150)
//                .resize(150, 150)
//                .fit()
                .override(500,500)
                .placeholder(R.drawable.imgloader)
//                .centerInside()
                .error(R.drawable.applogo)
                .into(holder.img);

//


    }


    @Override
    public int getItemCount() {
        return castList.size();
    }


}
