package com.example.unipr1;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FavoritesManager {
    private final ArrayList<String> favoriteCurrenciesList;
    private final ArrayAdapter<String> adapter;
    private final Database database;

    public FavoritesManager(Context context) {
        favoriteCurrenciesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, favoriteCurrenciesList);
        database = new Database(context);
        loadFavoriteCurrencies();
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    private void saveFavoriteCurrencies(ArrayList<String> favoriteCurrenciesList) {
        database.updateCurrencyPairs(this.favoriteCurrenciesList);
    }

    public void removeCurrencyPair(String currencyPair) {
        favoriteCurrenciesList.remove(currencyPair);
        saveFavoriteCurrencies(favoriteCurrenciesList);
        adapter.notifyDataSetChanged();
        database.deleteCurrencyPair(currencyPair);
        // Mostra una notifica o aggiorna altre parti dell'interfaccia, se necessario
    }

    private void loadFavoriteCurrencies() {
        favoriteCurrenciesList.clear();
        favoriteCurrenciesList.addAll(database.getAllCurrencyPairs());
    }
}

