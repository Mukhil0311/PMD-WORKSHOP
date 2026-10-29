
package com.example.workshop;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class AddCountryActivity extends Activity {

    EditText country, currency;
    Button saveButton;
    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);

        country = findViewById(R.id.country_edittext);
        currency = findViewById(R.id.currency_edittext);
        saveButton = findViewById(R.id.add_record);

        manager = new DBManager(this);

        saveButton.setOnClickListener(v -> {
            String c = country.getText().toString().trim();
            String cur = currency.getText().toString().trim();

            if (c.isEmpty() || cur.isEmpty()) {
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.open();
            boolean result = manager.insert(c, cur);
            manager.close();

            if (result) {
                Toast.makeText(this, "Country Added", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
