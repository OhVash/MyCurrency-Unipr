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

        // Aggiungi un listener agli elementi della ListView per selezionare la valuta nello spinner della MainActivity
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            String currencyPair = favoriteCurrenciesList.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedCurrencyPair", currencyPair);
            setResult(RESULT_OK, resultIntent);
            finish();
        });


        listViewFavorites.setOnItemLongClickListener((parent, view, position, id) -> {
            // Mostra una conferma di eliminazione (puoi utilizzare un AlertDialog)
            showDeleteConfirmation(position);
            return true; // Restituisci true per indicare che l'evento è stato gestito
        });

    }

    private void showDeleteConfirmation(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi eliminare questa valuta dai preferiti?")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    // Ottieni l'elemento selezionato dalla lista e rimuovilo dalla lista dei preferiti
                    String currencyPair = favoritesManager.getAdapter().getItem(position);
                    if (currencyPair != null) {
                        favoritesManager.removeCurrencyPair(currencyPair);
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
