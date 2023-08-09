package com.example.unipr1;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    private CurrencyAPIManager currencyAPIManager;
    private FavoritesManager favoritesManager;
    private String fromCurrency;
    private String toCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout dell'activity
        spinnerFrom = findViewById(R.id.spinnerFrom); // spinner da cui scegliere la valuta di partenza
        spinnerTo = findViewById(R.id.spinnerTo); // spinner da cui scegliere la valuta in cui convertire
        editTextAmount = findViewById(R.id.editTextAmount); // editText in cui inserire la somma da convertire
        textViewResult = findViewById(R.id.textViewResult); // textView che mostra il risultato della conversione
        textViewCurrencies = findViewById(R.id.textViewCurrencies); // mostra le currencies attualmente selezionate
        textViewHistory = findViewById(R.id.textViewHistory); // mostra il tasso di cambio storico nei 7 giorni precedenti
        Button buttonCalculate = findViewById(R.id.buttonCalculate); // se premuto effettua la conversione
        ImageView imageViewAdd = findViewById(R.id.imageViewAdd); // se premuto aggiunge le valute ai preferiti
        ImageView imageViewSaved = findViewById(R.id.imageViewSaved); // se premuto apre la favorites activity
        ImageView imageViewSwitch = findViewById(R.id.imageViewSwitch); // se premuto inverte l'attuale selezione delle valute
        currencyAPIManager = new CurrencyAPIManager(); // oggetto utilizzato per la gestione dell'API e le relative funzioni
        // private ArrayList<String> favoriteCurrenciesList = new ArrayList<>();
        favoritesManager = new FavoritesManager(this);
        // favoriteCurrenciesList = database.getAllCurrencyPairs(); // lista aggiornata derivata dal database

        new Thread(() -> { // thread asincrono per l'acquisizione tramite API delle valute da inserire negli spinner
            List<String> currencies = currencyAPIManager.getCurrencies();
            runOnUiThread(() -> populateSpinners(currencies)); // chiama la funzione populateSpinners per l'inserimento
        }).start();

        //when pressed perform conversion
        buttonCalculate.setOnClickListener(v -> performConversion()); // click sul bottone chiama la funzione performConversion

        //listener per "spinnerFrom"
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrency = spinnerFrom.getSelectedItem().toString(); // assegna la valuta selezionata a fromCurrency come stringa
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Non fare nulla
            }
        });

        //listener per spinner "spinnerTo"
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrency = spinnerTo.getSelectedItem().toString(); // assegna la valuta selezionata a fromCurrency come stringa
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Non fare nulla
            }
        });

        imageViewAdd.setOnClickListener(view -> { // Chiamata alla funzione per aggiungere le valute preferite
            favoritesManager.addCurrencyToFavorites(fromCurrency, toCurrency);
        });

        imageViewSaved.setOnClickListener(view -> { // apre la favoritesActivity tramite intent
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            favoritesLauncher.launch(intent);
        });

        imageViewSwitch.setOnClickListener(view -> { // se premuto scambia la selezione delle valute
            swapCurrencies();
        });
    }
    /*
     * populateSpinners serve per riempire gli spinners con le valute fornite dall'API
     */
    private void populateSpinners(List<String> currencies){
        // collegamento lista - spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,currencies);
        // impostazione del layout per la vista dello spinner quando selezionato
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // applicazione dell'adapter ai due spinner
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void swapCurrencies(){
        // variabili per salvare posizione nello spinner
        int fromCurrencyPosition = spinnerFrom.getSelectedItemPosition();
        int toCurrencyPosition = spinnerTo.getSelectedItemPosition();
        // invertire la selezione tramite le posizioni salvate
        spinnerFrom.setSelection(toCurrencyPosition);
        spinnerTo.setSelection(fromCurrencyPosition);
        // aggiorno le variabili fromCurrency e toCurrency per una corretta visualizzazione
        fromCurrency = spinnerFrom.getSelectedItem().toString();
        toCurrency = spinnerTo.getSelectedItem().toString();
        // aggiorno anche direttamente la conversione
        performConversion();
    }

    @SuppressLint("DefaultLocale")
    private String buildHistoryText(List<Double> exchangeRates) {
        // creazione oggetto StringBuilder per la concatenazione di stringhe e formattazione data
        StringBuilder historyText = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        // ciclo per assegnare ad ogni data il tasso di cambio corrispondente
        for (int i = 0; i < exchangeRates.size(); i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String date = dateFormatter.format(calendar.getTime());
            double rate = exchangeRates.get(i);
            historyText.append(date).append(": ").append(String.format("%.10f", rate)).append("\n");
        }
        return historyText.toString();
    }
    /*
     * performConversion prende il valore inserito nella textEdit e lo converte nella valuta scelta, inoltre mostra i tassi
     * di cambio dei giorni precedenti
     */
    @SuppressLint("DefaultLocale")
    private void performConversion() {
        // leggo la quantità inserita (la assegno string e double per fare un doppio check)
        String amountStr = editTextAmount.getText().toString();

        //Check del'input, se è vuoto o minore uguale a zero, avviso l'utente di inserire una quantità valida
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Inserisci una quantità valida. Default: 1", Toast.LENGTH_SHORT).show();
            editTextAmount.setText("1");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        // costruzione stringa che mostra le valute selezionate (al fine dei tassi storici)
        String showCurrencies = fromCurrency + "/" + toCurrency + " in the past days: ";
        textViewCurrencies.setText(showCurrencies);
        // creo un executor, thread per le prossime istruzioni asincrone
        Executor executor = Executors.newSingleThreadExecutor();
        // operazione asincrona, che chiama la getExchangeRate, al fine di ottenere il tasso di cambio
        CompletableFuture<Double> exchangeRateFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return currencyAPIManager.getExchangeRate(fromCurrency, toCurrency);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }, executor);
        // operazione asincrona, come prima ma ottiene il tasso di cambio storico
        CompletableFuture<List<Double>> exchangeRateHistoryFuture = CompletableFuture.supplyAsync(() -> currencyAPIManager.getExchangeRateHistory(fromCurrency, toCurrency), executor);
        // check se le due operazioni precedenti sono state completatate
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(exchangeRateFuture, exchangeRateHistoryFuture);
        // dopodiche svolgo le seguenti operazioni: calcolo conversione, costruzione stringhe per la visualizzazione tassi storici.
        combinedFuture.thenRunAsync(() -> {
            try {
                // salvo tasso corrente in una variabile, e quelli passati in una lista
                double exchangeRate = exchangeRateFuture.get();
                List<Double> exchangeRateHistory = exchangeRateHistoryFuture.get();

                runOnUiThread(() -> {
                    // conversione e stampa del risultato nella textView
                    double convertedAmount = amount * exchangeRate;
                    textViewResult.setText(String.format("%.2f", convertedAmount));
                    // costruzione testo e stampa nella textView
                    String historyText = buildHistoryText(exchangeRateHistory);
                    textViewHistory.setText(historyText);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private final ActivityResultLauncher<Intent> favoritesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { // permettere di tornare alla main activity dopo aver cliccato su una coppia di valute nei preferiti
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String selectedCurrencyPair = result.getData().getStringExtra("selectedCurrencyPair");
                    if (selectedCurrencyPair != null) {
                        selectCurrencyPairInSpinner(selectedCurrencyPair);
                    }
                }
            }
    );
    private void selectCurrencyPairInSpinner(String currencyPair) {
        // assegno la coppia selezionata agli spinner
        String[] currencies = currencyPair.split("/");
        fromCurrency = currencies[0];
        toCurrency = currencies[1];
        // prendo l'indice delle currency
        int fromCurrencyIndex = getIndexFromSpinner(spinnerFrom, fromCurrency);
        int toCurrencyIndex = getIndexFromSpinner(spinnerTo, toCurrency);
        // controllo che sia valido e aggiorno lo spinner
        if (fromCurrencyIndex >= 0 && toCurrencyIndex >= 0) {
            spinnerFrom.setSelection(fromCurrencyIndex);
            spinnerTo.setSelection(toCurrencyIndex);
        }
        // faccio direttamente la conversione con le valute scelte
        performConversion();
    }

    // funzione utilizzata dal metodo precedente, per prendere l'indice della valuta nello spinner
    private int getIndexFromSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }
}