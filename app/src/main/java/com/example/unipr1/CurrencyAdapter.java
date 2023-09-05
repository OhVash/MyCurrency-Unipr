package com.example.unipr1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private ArrayList<String> favoriteCurrenciesList;
    private Context context;

    public CurrencyAdapter(Context context, ArrayList<String> favoriteCurrenciesList) {
        this.favoriteCurrenciesList = favoriteCurrenciesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currencyPair = favoriteCurrenciesList.get(position);
        holder.currencyPairTextView.setText(currencyPair);
    }
    // Aggiungi questo metodo per ottenere l'elemento in una posizione specifica
    public String getItem(int position) {
        return favoriteCurrenciesList.get(position);
    }
    @Override
    public int getItemCount() {
        return favoriteCurrenciesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView currencyPairTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            currencyPairTextView = itemView.findViewById(R.id.currencyPairTextView);
        }
    }
}
