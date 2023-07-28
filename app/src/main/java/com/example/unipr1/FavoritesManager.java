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


    private void loadFavoriteCurrencies() {
        favoriteCurrenciesList.clear();
        favoriteCurrenciesList.addAll(database.getAllCurrencyPairs());
    }
}

