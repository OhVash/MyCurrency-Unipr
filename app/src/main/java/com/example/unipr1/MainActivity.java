package com.example.unipr1;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        //Thread to get currencies from API
        new Thread(() -> {
            List<String> currencies = getCurrencies();
            runOnUiThread(() -> populateSpinners(currencies));
        }).start();

        //when pressed perform conversion
        buttonCalculate.setOnClickListener(v -> performConversion());

        //listener per "spinnerFrom"
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }
    /*
     * getCurrencies is to get the name of the currencies from the API
     */
    private List<String> getCurrencies() {
        List<String> currencies = new ArrayList<>();
        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e("FetchCurrenciesTask", "Error! " + response.code());
                return currencies;
            }

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONObject jsonObjectCurrencies = jsonResponse.getJSONObject("data");

            Iterator<String> currencyIterator = jsonObjectCurrencies.keys();
            while (currencyIterator.hasNext()) {
                String currency = currencyIterator.next();
                currencies.add(currency);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return currencies;
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

    /*
     * getExchangeRate is used to get the requested exchange rate based on the selected currencies
     */
    private double getExchangeRate(String fromCurrency, String toCurrency) throws IOException, JSONException {
        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey="
                + apiKey + "&currencies="
                + toCurrency + "&base_currency="
                + fromCurrency;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("data").getDouble(toCurrency);
        } else {
            throw new IOException("Error! " + responseCode);
        }
    }

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

    private List<Double> getExchangeRateHistory(String fromCurrency, String toCurrency) {
        List<Double> exchangeRates = new ArrayList<>();

        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date in UTC timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String toDate = dateFormat.format(calendar.getTime());

        // Go back 7 days from the current date
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String fromDate = dateFormat.format(calendar.getTime());
        String url = "https://api.freecurrencyapi.com/v1/historical?apikey=" + apiKey
                + "&currencies=" + toCurrency
                + "&base_currency=" +fromCurrency
                + "&date_from=" + fromDate
                + "&date_to=" + toDate;
        Log.d(url , url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e("FetchExchangeRateHistory", "Error! " + response.code());
                return exchangeRates;
            }

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONObject dataObject = jsonResponse.getJSONObject("data");

            for (int i = 0; i < 7; i++) {
                calendar.setTime(dateFormat.parse(fromDate));
                calendar.add(Calendar.DAY_OF_YEAR, i);
                String date = dateFormat.format(calendar.getTime());
                JSONObject dateObject = dataObject.getJSONObject(date);
                double exchangeRate = dateObject.getDouble(toCurrency);
                exchangeRates.add(exchangeRate);
            }
            Collections.reverse(exchangeRates); // Invert the order to get rates in chronological order
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return exchangeRates;
    }
    /*
     * performConversion uses the chosen amount of money and convert it into the selected currency and
     * shows it into the editText
     */
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
                return getExchangeRate(fromCurrency, toCurrency);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, executor);
        
        CompletableFuture<List<Double>> exchangeRateHistoryFuture = CompletableFuture.supplyAsync(() -> getExchangeRateHistory(fromCurrency, toCurrency), executor);

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