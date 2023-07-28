package com.example.unipr1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {
    private ImageView imageViewHome;
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteCurrenciesList);


        imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(view -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class );
            startActivity(intent);
        });


        // Aggiungi un listener agli elementi della ListView per selezionare la valuta nello spinner della MainActivity
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            String currencyPair = favoriteCurrenciesList.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedCurrencyPair", currencyPair);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

    }
}
