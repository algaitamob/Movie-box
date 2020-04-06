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
import com.algaita.models.Transactions;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    Context context;
    public List<Transactions> transactionsList;


    public TransactionAdapter(List<Transactions> transactionsList, Context context) {
        super();
        this.transactionsList = transactionsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, ref, ondate;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.videoname);
            ref = itemView.findViewById(R.id.ref);
            ondate = itemView.findViewById(R.id.ondate);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactions, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transactions transactions = transactionsList.get(position);
        holder.title.setText(transactions.getTitle());
        holder.ref.setText("Reference: " + transactions.getRef());
        holder.ondate.setText("Date: " + transactions.getOndate());

    }


    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


}
