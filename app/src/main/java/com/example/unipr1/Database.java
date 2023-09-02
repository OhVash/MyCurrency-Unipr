package com.example.unipr1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favorites";
    private static final String COLUMN_CURRENCY_PAIR = "currency_pair";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // creazione tabella
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_CURRENCY_PAIR + " TEXT PRIMARY KEY)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // eliminazione della tabella precedente e creazione della nuova
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addCurrencyPair(String currencyPair) {
        // oggetto per la scrittura
        SQLiteDatabase db = this.getWritableDatabase();
        // oggetto per contenere la coppia delle valute
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENCY_PAIR, currencyPair);
        // inserisco il record nella tabella
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAllCurrencyPairs() {
        // creazione lista e lettura di tutti i record dal database
        ArrayList<String> currencyPairsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(COLUMN_CURRENCY_PAIR);
            if (columnIndex >= 0) {
                while (cursor.moveToNext()) {
                    // aggiungo le coppie di valute alla lista
                    String currencyPair = cursor.getString(columnIndex);
                    currencyPairsList.add(currencyPair);
                }
            }
            cursor.close();
        }
        db.close();
        return currencyPairsList;
    }

    public void deleteCurrencyPair(String currencyPair) {
        SQLiteDatabase db = this.getWritableDatabase();
        // elimino il record dalla tabella
        db.delete(TABLE_NAME, COLUMN_CURRENCY_PAIR + "=?", new String[]{currencyPair});
        db.close();
    }
}


