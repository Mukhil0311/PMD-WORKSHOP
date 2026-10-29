# PMD-WORKSHOP

```
NAME : **MUKHIL S**
REG NO: **212225040263**
```

## AIM
To Develop an android application to create records that store Country names and their respective currencies in the form of a ListView using SQLite DataBase.

Application consists of 5 classes:
```
DatabaseHelper.java
DBManager.java
CountryListActivity.java
AddCountryActivity.java 
ModifyCountryActivity.java
```

### Instructions:

1.Create ListView to display the country name and currencey.

2.Adding a new record to the database.

3.Modifying a record in sqlite database.

4.Updating and Deleting a record.

### activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```
### Main_activity.java
```java
package com.example.workshop;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```

## AddCountryActivity.java

```java

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
```

### ModifycountryActivity.java
```java
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
```

### CountryListActivity.java

```java

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
```

### Databasehelper.java

```java

package com.example.workshop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "CountryDB";
    public static final String TABLE_NAME = "Country";
    public static final String COL_ID = "id";
    public static final String COL_COUNTRY = "country";
    public static final String COL_CURRENCY = "currency";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COUNTRY + " TEXT, " +
                COL_CURRENCY + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
```

### DBManager.java

```java

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
```
### OUTPUT
<img width="1920" height="1080" alt="Screenshot 2026-10-29 194108" src="https://github.com/user-attachments/assets/9346dfa6-9efc-4284-b7d6-c1ba4e4c3c6b" />

<img width="1920" height="1080" alt="Screenshot 2026-10-29 194120" src="https://github.com/user-attachments/assets/8ff8738a-2336-421b-ab0c-f6dee0534295" />

<img width="1920" height="1080" alt="Screenshot 2026-10-29 194211" src="https://github.com/user-attachments/assets/cf38f1ee-6ff7-4076-a239-3dbfc7835f19" />

### RESULT

Thus the android application has been created and executed successfully
