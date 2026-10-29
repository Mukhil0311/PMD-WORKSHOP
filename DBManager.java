
package com.example.workshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBManager {

    DatabaseHelper helper;
    SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DatabaseHelper(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public boolean insert(String country, String currency) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_COUNTRY, country);
        values.put(DatabaseHelper.COL_CURRENCY, currency);

        return db.insert(DatabaseHelper.TABLE_NAME, null, values) != -1;
    }

    public ArrayList<String> getAllCountries() {
        ArrayList<String> list = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COUNTRY));
            String currency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CURRENCY));

            list.add(id + " - " + country + " - " + currency);
        }

        cursor.close();
        return list;
    }

    public boolean update(int id, String country, String currency) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_COUNTRY, country);
        values.put(DatabaseHelper.COL_CURRENCY, currency);

        return db.update(DatabaseHelper.TABLE_NAME, values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean delete(int id) {
        return db.delete(DatabaseHelper.TABLE_NAME,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }
}
