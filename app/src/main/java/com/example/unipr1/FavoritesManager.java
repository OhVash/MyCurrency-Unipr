package com.example.unipr1;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FavoritesManager {
    private Context context;
    private ArrayList<String> favoriteCurrenciesList;
    private ArrayAdapter<String> adapter;
    private Database database;

    public FavoritesManager(Context context) {
        this.context = context;
        favoriteCurrenciesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, favoriteCurrenciesList);
        database = new Database(context);
        loadFavoriteCurrencies();
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void addCurrencyPair(String currencyPair) {
        if (!favoriteCurrenciesList.contains(currencyPair)) {
            favoriteCurrenciesList.add(currencyPair);
            adapter.notifyDataSetChanged();
            database.addCurrencyPair(currencyPair);
        }
    }

    private void saveFavoriteCurrencies(ArrayList<String> favoriteCurrenciesList) {
        database.updateCurrencyPairs(this.favoriteCurrenciesList);
    }

    public void removeCurrencyPair(String currencyPair) {
        favoriteCurrenciesList.remove(currencyPair);
        saveFavoriteCurrencies(favoriteCurrenciesList);
        adapter.notifyDataSetChanged();
        // Mostra una notifica o aggiorna altre parti dell'interfaccia, se necessario
    }

    private void loadFavoriteCurrencies() {
        favoriteCurrenciesList.clear();
        favoriteCurrenciesList.addAll(database.getAllCurrencyPairs());
    }
}

