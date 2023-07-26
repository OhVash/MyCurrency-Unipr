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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Spinner spinnerDays;

    private EditText editTextAmount;

    private TextView textViewResult;
    private TextView textViewCurrencies;
    private TextView textViewChange;

    private Button buttonCalculate;

    private ImageView imageViewSaved;
    private ImageView imageViewAdd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        spinnerDays = findViewById(R.id.spinnerDays);

        editTextAmount = findViewById(R.id.editTextAmount);

        textViewResult = findViewById(R.id.textViewResult);
        textViewCurrencies = findViewById(R.id.textViewCurrencies);
        textViewChange = findViewById(R.id.textViewChange);

        buttonCalculate = findViewById(R.id.buttonCalculate);

        imageViewAdd = findViewById(R.id.imageViewAdd);
        imageViewSaved = findViewById(R.id.imageViewSaved);

        //Thread to get currencies from API
        new Thread(() -> {
            List<String> currencies = getCurrencies();
            runOnUiThread(() -> populateSpinners(currencies));
        }).start();

        // Creazione dell'array di stringhe per i giorni
        String[] daysArray = {"Today", "Yesterday", "2 days ago", "3 days ago", "4 days ago", "5 days ago", "6 days ago", "1 week ago"};

        // Creazione di un ArrayAdapter per collegare l'array allo spinner
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysArray);

        // Specifica il layout per gli elementi della lista
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Collega l'ArrayAdapter allo spinner "spinnerDays"
        spinnerDays.setAdapter(daysAdapter);

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
                // Non fare nulla quando non viene selezionato nulla
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
                // Non fare nulla quando non viene selezionato nulla
            }
        });

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                //getHistoricalExchangeRate(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Niente da fare quando non viene selezionato nulla
            }
        });



    }
//~~~~~~~~~~CLASS AND FUNCTIONS USING API~~~~~~~~~~~~~~~~
private double getExchangeRate(String fromCurrency, String toCurrency) throws IOException, JSONException {
    String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
    String url = "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey + "&currencies=" + toCurrency + "&base_currency=" + fromCurrency;

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
        double exchangeRate = jsonResponse.getJSONObject("data").getDouble(toCurrency);
        return exchangeRate;
    } else {
        throw new IOException("Error! " + responseCode);
    }
}

    private List<Double> getExchangeRateHistory(String fromCurrency, String toCurrency) throws IOException, JSONException {
        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";

        // Calculate the dates for the 7-day interval (from 7 days ago to today)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String toDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        String fromDate = dateFormat.format(calendar.getTime());

        String url = "https://api.freecurrencyapi.com/v1/historical?apikey=" + apiKey
                + "&currencies=" + toCurrency
                + "&base_currency=" + fromCurrency
                + "&date_from=" + fromDate
                + "&date_to=" + toDate;

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
            JSONObject dataObject = jsonResponse.getJSONObject("data");

            List<Double> exchangeRates = new ArrayList<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (int i = 0; i < 7; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                String date = dateFormatter.format(calendar.getTime());
                JSONObject dateObject = dataObject.getJSONObject(date);
                double exchangeRate = dateObject.getDouble(toCurrency);
                exchangeRates.add(exchangeRate);
            }
            Collections.reverse(exchangeRates); // Invert the order to get the exchange rates in chronological order
            return exchangeRates;
        } else {
            throw new IOException("Error! " + responseCode);
        }
    }


    private List<String> getCurrencies() {
        List<String> currencies = new ArrayList<>();
        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey;

        try {
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
                JSONObject jsonObjectCurrencies = jsonResponse.getJSONObject("data");

                Iterator<String> currencyIterator = jsonObjectCurrencies.keys();
                while (currencyIterator.hasNext()) {
                    String currency = currencyIterator.next();
                    currencies.add(currency);
                }
                return currencies;
            } else {
                Log.e("FetchCurrenciesTask", "Error! " + responseCode);
            }
            connection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return currencies;
    }



    private void populateSpinners(List<String> currencies){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private void performConversion() {
        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency = spinnerTo.getSelectedItem().toString();

        Executor executor = Executors.newSingleThreadExecutor();
        Future<Double> exchangeRateFuture = ((ExecutorService) executor).submit(() -> getExchangeRate(fromCurrency, toCurrency));

        executor.execute(() -> {
            try {
                double exchangeRate = exchangeRateFuture.get();
                runOnUiThread(() -> {
                    double amount = Double.parseDouble(editTextAmount.getText().toString());
                    double convertedAmount = amount * exchangeRate;
                    textViewResult.setText(String.format("%.2f", convertedAmount));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executor.execute(() -> {
            try {
                List<Double> exchangeRates = getExchangeRateHistory(fromCurrency, toCurrency);
                StringBuilder historyText = new StringBuilder();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < exchangeRates.size(); i++) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    String date = dateFormatter.format(calendar.getTime());
                    double rate = exchangeRates.get(i);
                    historyText.append(date).append(": ").append(String.format("%.2f", rate)).append("\n");
                }
                runOnUiThread(() -> textViewChange.setText(historyText.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




//~~~~~~FUNCTION TO MANAGE FAVORITES~~~~~~~~~




//~~~~~~~~FUNCTIONS TO SEE HISTORY~~~~~~~~


}