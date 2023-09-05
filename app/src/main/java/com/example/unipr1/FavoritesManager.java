package com.example.unipr1;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoritesManager {
    private final ArrayList<String> favoriteCurrenciesList;
    private final CurrencyAdapter adapter; // Cambia il tipo a CurrencyAdapter
    private final Database database;
    private final Context context;

    public FavoritesManager(Context activityContext) {
        favoriteCurrenciesList = new ArrayList<>();
        adapter = new CurrencyAdapter(activityContext, favoriteCurrenciesList); // Usa CurrencyAdapter
        database = new Database(activityContext);
        context = activityContext;
        // riempio la lista con il contenuto nel database
        favoriteCurrenciesList.addAll(database.getAllCurrencyPairs());
    }

    public CurrencyAdapter getAdapter() { // Cambia il tipo del ritorno a CurrencyAdapter
        return adapter;
    }

    public void addCurrencyToFavorites(String fromCurrency, String toCurrency) {
        String currencyPair = fromCurrency + "/" + toCurrency;

        if (!favoriteCurrenciesList.contains(currencyPair)) {
            favoriteCurrenciesList.add(currencyPair);
            database.addCurrencyPair(currencyPair);
            adapter.notifyDataSetChanged(); // Aggiorna l'adapter
            Toast.makeText(context, "Valuta aggiunta ai preferiti", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Questa valuta è già nei preferiti", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeCurrencyPair(String currencyPair) {
        // elimino da lista, database e aggiorno
        favoriteCurrenciesList.remove(currencyPair);
        database.deleteCurrencyPair(currencyPair);
        adapter.notifyDataSetChanged();
    }
}
