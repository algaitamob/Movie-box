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
import com.algaita.models.Notifications;
import com.algaita.models.WalletTransactions;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    Context context;
    public List<Notifications> transactionsList;


    public NotificationsAdapter(List<Notifications> transactionsList, Context context) {
        super();
        this.transactionsList = transactionsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, ondate;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            ondate = itemView.findViewById(R.id.ondate);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notifications transactions = transactionsList.get(position);
        holder.title.setText(transactions.getTitle());
        holder.ondate.setText(transactions.getOndate());

    }


    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


}
