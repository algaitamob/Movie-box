package com.algaita.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.algaita.R;
import com.algaita.RecyclerClickListener;
import com.algaita.models.ItemVideos;


import java.util.ArrayList;



public class AdapterVIdeoList extends RecyclerView.Adapter<AdapterVIdeoList.MyViewHolder> {

    private Context context;
    private ArrayList<ItemVideos> arrayList;
    private ArrayList<ItemVideos> filteredArrayList;
    private RecyclerClickListener recyclerClickListener;
    public DetailsAdapterListener onClickListener;

    private NameFilter filter;
    private String type;

//    private JsonUtils jsonUtils;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song;
        Button btn_delete, btn_play;


        MyViewHolder(View view) {
            super(view);
            textView_song = view.findViewById(R.id.textView_songname);
            btn_delete = view.findViewById(R.id.btn_delete);
            btn_play = view.findViewById(R.id.btn_play);
        }
    }

    public AdapterVIdeoList(Context context, ArrayList<ItemVideos> arrayList, DetailsAdapterListener listener) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.type = type;
        this.onClickListener = listener;
        this.recyclerClickListener = recyclerClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_videolist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.textView_song.setText(arrayList.get(position).getMp3Name());

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
          @Override
         public void onClick(View view) {
              onClickListener.classOnClick(view, position);

          }
        });

        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.favOnclick(v, position);
            }
        });

//        if (!type.equals("offline")) {
//
//        } else {
//
//        }

    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }
    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemVideos> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getMp3Name();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemVideos>) results.values;
            notifyDataSetChanged();
        }
    }


    //region Interface Details listener
    public interface DetailsAdapterListener {

        void classOnClick(View v, int position);
        void favOnclick(View v, int position);



    }
}