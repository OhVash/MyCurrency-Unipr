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
        // riempio la lista con il contenuto nel database
        favoriteCurrenciesList.addAll(database.getAllCurrencyPairs());
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void removeCurrencyPair(String currencyPair) {
        // elimino da lista, database e aggiorno
        favoriteCurrenciesList.remove(currencyPair);
        database.deleteCurrencyPair(currencyPair);
        database.updateCurrencyPairs(favoriteCurrenciesList);
        adapter.notifyDataSetChanged();

    }
}

