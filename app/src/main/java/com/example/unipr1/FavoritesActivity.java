package com.example.unipr1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;


public class FavoritesActivity extends AppCompatActivity {
    private FavoritesManager favoritesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ListView listViewFavorites = findViewById(R.id.listViewFavorites);
        Database database = new Database(this);
        favoritesManager = new FavoritesManager(this);
        listViewFavorites.setAdapter(favoritesManager.getAdapter());

        ArrayList<String> favoriteCurrenciesList = database.getAllCurrencyPairs();

        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(view -> {
            finish(); // Chiude l'attuale FavoritesActivity e torna alla MainActivity precedente
        });

        // listener sugli elementi della ListView per selezionare la coppia di valute
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            String currencyPair = favoriteCurrenciesList.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedCurrencyPair", currencyPair);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        listViewFavorites.setOnItemLongClickListener((parent, view, position, id) -> {
            // mostra avviso di conferma di eliminazione se tengo premuto
            showDeleteConfirmation(position);
            return true;
        });

    }

    private void showDeleteConfirmation(int position) {
        // alert per confermare o meno la modifica
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi eliminare questa valuta dai preferiti?")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    // ottengo l'elemento selezionato dalla lista e lo rimuovo
                    String currencyPair = favoritesManager.getAdapter().getItem(position);
                    if (currencyPair != null) {
                        favoritesManager.removeCurrencyPair(currencyPair);
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
