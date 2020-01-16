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
import com.algaita.models.Transactions;
import com.algaita.models.WalletTransactions;

import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.MyViewHolder> {
    Context context;
    public List<WalletTransactions> transactionsList;


    public WalletTransactionAdapter(List<WalletTransactions> transactionsList, Context context) {
        super();
        this.transactionsList = transactionsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView type, ondate, newbalance, currentbalance;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.type);

            newbalance = itemView.findViewById(R.id.new_balance);
            currentbalance = itemView.findViewById(R.id.current_balance);
            ondate = itemView.findViewById(R.id.ondate);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WalletTransactions transactions = transactionsList.get(position);
        holder.type.setText(transactions.getType());
        holder.currentbalance.setText("₦" + transactions.getCurrent_balance());
        holder.newbalance.setText("₦" + transactions.getAmount());
        holder.ondate.setText(transactions.getOndate());

    }


    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


}
