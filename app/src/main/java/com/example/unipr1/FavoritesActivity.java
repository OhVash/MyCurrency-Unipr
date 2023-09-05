package com.example.unipr1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {
    private FavoritesManager favoritesManager;
    private RecyclerView recyclerViewFavorites; // Aggiunto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites); // Inizializzato

        Database database = new Database(this);
        favoritesManager = new FavoritesManager(this);

        // Imposta il layout manager per la RecyclerView (es. LinearLayoutManager)
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Imposta l'adapter personalizzato sulla RecyclerView
        recyclerViewFavorites.setAdapter(favoritesManager.getAdapter());

        ArrayList<String> favoriteCurrenciesList = database.getAllCurrencyPairs();

        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(view -> {
            finish(); // Chiude l'attuale FavoritesActivity e torna alla MainActivity precedente
        });

        // Aggiorna il codice relativo al click sugli elementi della RecyclerView
        recyclerViewFavorites.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewFavorites,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String currencyPair = favoriteCurrenciesList.get(position);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("selectedCurrencyPair", currencyPair);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                // mostra avviso di conferma di eliminazione se tengo premuto
                                showDeleteConfirmation(position);
                            }
                        })
        );
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
                        recyclerViewFavorites.getAdapter().notifyItemRemoved(position); // Aggiornamento della RecyclerView
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

