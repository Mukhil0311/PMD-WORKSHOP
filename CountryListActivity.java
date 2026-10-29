
package com.example.workshop;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import android.view.View;

import java.util.ArrayList;

public class CountryListActivity extends Activity {

    ListView listView;
    View addButton;
    DBManager manager;
    ArrayList<String> countries;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        listView = findViewById(R.id.list_view);
        addButton = findViewById(R.id.add_record_fab);

        manager = new DBManager(this);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCountryActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = countries.get(position);
            int countryId = Integer.parseInt(item.split(" - ")[0]);

            Intent intent = new Intent(this, ModifyCountryActivity.class);
            intent.putExtra("id", countryId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.open();
        countries = manager.getAllCountries();
        manager.close();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                countries
        );

        listView.setAdapter(adapter);
    }
}
