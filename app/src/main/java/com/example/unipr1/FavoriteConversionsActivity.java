package com.example.unipr1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoriteConversionsActivity extends AppCompatActivity {
    private ListView listViewConversions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        listViewConversions.findViewById(R.id.listViewConversions);

    }

    public interface OnConversionSelectedListener {
        void onConversionSelected(String fromCurrency, String toCurrency);
    }



}


