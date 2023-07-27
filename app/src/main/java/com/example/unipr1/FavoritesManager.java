package com.example.unipr1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FavoritesManager extends AppCompatActivity {
    private ImageView imageViewHome;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_main);

        imageViewHome.findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(view -> {
            Intent intent = new Intent(FavoritesManager.this, MainActivity.class );
            startActivity(intent);
        });
    }

}
