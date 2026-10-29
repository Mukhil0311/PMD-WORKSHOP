package com.example.workshop;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;

public class ModifyCountryActivity extends Activity {

    EditText country, currency;
    Button updateButton, deleteButton;
    DBManager manager;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_country);

        country = findViewById(R.id.country_edittext);
        currency = findViewById(R.id.currency_edittext);
        updateButton = findViewById(R.id.btn_update);
        deleteButton = findViewById(R.id.btn_delete);

        id = getIntent().getIntExtra("id", -1);
        manager = new DBManager(this);

        manager.open();

        Cursor cursor = manager.db.query(
                DatabaseHelper.TABLE_NAME,
                null,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            country.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COUNTRY)));
            currency.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CURRENCY)));
        }

        cursor.close();
        manager.close();

        updateButton.setOnClickListener(v -> {
            String c = country.getText().toString().trim();
            String cur = currency.getText().toString().trim();

            if (c.isEmpty() || cur.isEmpty()) {
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.open();
            boolean result = manager.update(id, c, cur);
            manager.close();

            Toast.makeText(this, result ? "Updated Successfully" : "Update Failed",
                    Toast.LENGTH_SHORT).show();

            if (result) finish();
        });

        deleteButton.setOnClickListener(v -> {
            manager.open();
            boolean result = manager.delete(id);
            manager.close();

            Toast.makeText(this, result ? "Deleted Successfully" : "Delete Failed",
                    Toast.LENGTH_SHORT).show();

            if (result) finish();
        });
    }
}
