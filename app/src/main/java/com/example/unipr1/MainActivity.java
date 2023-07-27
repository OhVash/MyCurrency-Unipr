package com.example.unipr1;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class MainActivity extends AppCompatActivity {
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText editTextAmount;
    private TextView textViewResult;
    private TextView textViewCurrencies;
    private TextView textViewHistory;
    private Button buttonCalculate;
    private ImageView imageViewSaved;
    private ImageView imageViewAdd;
    private CurrencyAPIManager currencyAPIManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        editTextAmount = findViewById(R.id.editTextAmount);
        textViewResult = findViewById(R.id.textViewResult);
        textViewCurrencies = findViewById(R.id.textViewCurrencies);
        textViewHistory = findViewById(R.id.textViewHistory);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        imageViewAdd = findViewById(R.id.imageViewAdd);
        imageViewSaved = findViewById(R.id.imageViewSaved);
        currencyAPIManager = new CurrencyAPIManager();

        new Thread(() -> {
            List<String> currencies = currencyAPIManager.getCurrencies();
            runOnUiThread(() -> populateSpinners(currencies));
        }).start();

        //when pressed perform conversion
        buttonCalculate.setOnClickListener(v -> performConversion());

        //listener per "spinnerFrom"
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fromCurrency = spinnerFrom.getSelectedItem().toString();
                String toCurrency = spinnerTo.getSelectedItem().toString();
                String showCurrencies = fromCurrency + "/" + toCurrency;
                textViewCurrencies.setText(showCurrencies);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing if nothing pressed
            }
        });

        //listener per spinner "spinnerTo"
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fromCurrency = spinnerFrom.getSelectedItem().toString();
                String toCurrency = spinnerTo.getSelectedItem().toString();
                textViewCurrencies.setText(fromCurrency + "/" + toCurrency);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing if nothing pressed
            }
        });

        imageViewSaved.setOnClickListener(view -> {
            // Log.d("ciao", "hai cliccato");
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }
    /*
     * populate spinner is to put the data from the api into the spinners
     */
    private void populateSpinners(List<String> currencies){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private String buildHistoryText(List<Double> exchangeRates) {
        StringBuilder historyText = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < exchangeRates.size(); i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String date = dateFormatter.format(calendar.getTime());
            double rate = exchangeRates.get(i);
            historyText.append(date).append(": ").append(String.format("%.10f", rate)).append("\n");
        }
        return historyText.toString();
    }
    /*
     * performConversion uses the chosen amount of money and convert it into the selected currency and
     * shows it into the editText
     */
    @SuppressLint("DefaultLocale")
    private void performConversion() {
        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency = spinnerTo.getSelectedItem().toString();

        String amountStr = editTextAmount.getText().toString();

        //Checkinput
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Inserisci una quantità valida.", Toast.LENGTH_SHORT).show();
            return;
        }
        Executor executor = Executors.newSingleThreadExecutor();

        CompletableFuture<Double> exchangeRateFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return currencyAPIManager.getExchangeRate(fromCurrency, toCurrency);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }, executor);
        
        CompletableFuture<List<Double>> exchangeRateHistoryFuture = CompletableFuture.supplyAsync(() -> currencyAPIManager.getExchangeRateHistory(fromCurrency, toCurrency), executor);

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(exchangeRateFuture, exchangeRateHistoryFuture);

        combinedFuture.thenRunAsync(() -> {
            try {
                double exchangeRate = exchangeRateFuture.get();
                List<Double> exchangeRateHistory = exchangeRateHistoryFuture.get();

                runOnUiThread(() -> {
                    double amount = Double.parseDouble(editTextAmount.getText().toString());
                    double convertedAmount = amount * exchangeRate;
                    textViewResult.setText(String.format("%.2f", convertedAmount));

                    String historyText = buildHistoryText(exchangeRateHistory);
                    textViewHistory.setText(historyText);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}