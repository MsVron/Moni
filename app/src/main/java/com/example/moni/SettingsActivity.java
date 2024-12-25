package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // Setup currency spinner
        AutoCompleteTextView spinnerDefaultCurrency = findViewById(R.id.spinnerDefaultCurrency);
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies);
        spinnerDefaultCurrency.setAdapter(currencyAdapter);

        // Set current default currency
        String currentCurrency = sessionManager.getDefaultCurrency();
        if (currentCurrency != null && !currentCurrency.isEmpty()) {
            spinnerDefaultCurrency.setText(currentCurrency, false);
        }

        // Save currency button
        MaterialButton btnSaveCurrency = findViewById(R.id.btnSaveCurrency);
        btnSaveCurrency.setOnClickListener(v -> {
            String selectedCurrency = spinnerDefaultCurrency.getText().toString();
            if (!selectedCurrency.isEmpty()) {
                sessionManager.setDefaultCurrency(selectedCurrency);
                finish();
            }
        });

        // Logout button
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}