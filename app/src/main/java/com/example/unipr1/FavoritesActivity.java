package com.example.unipr1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class FavoritesActivity extends AppCompatActivity {
    private ImageView imageViewHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        imageViewHome.findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(view -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class );
            startActivity(intent);
        });
    }
}
