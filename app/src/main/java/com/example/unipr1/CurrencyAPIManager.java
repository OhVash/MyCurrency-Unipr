package com.example.unipr1;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

public class CurrencyAPIManager {
    private OkHttpClient client;
    public CurrencyAPIManager() {
        client = new OkHttpClient();
    }
    public List<String> getCurrencies() {
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

    public double getExchangeRate(String fromCurrency, String toCurrency) throws IOException, JSONException {
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

    public List<Double> getExchangeRateHistory(String fromCurrency, String toCurrency) {
        List<Double> exchangeRates = new ArrayList<>();

        String apiKey = "34dTPuf6QD2PLFPEsxOmHe9QOzVEEjYCd5FKFdlo";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date in UTC timezone
        Calendar calendar = Calendar.getInstance();
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
}
