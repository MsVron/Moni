package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SettingsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private AppDatabase db;
    private EditText etNewName, etCurrentPassword, etNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        setupViews();
        setupCurrencySpinner();
        setupButtons();
    }

    private void setupViews() {
        // Setup toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        etNewName = findViewById(R.id.etNewName);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);

        etNewName.setText(sessionManager.getUserName());
    }

    private void setupCurrencySpinner() {
        AutoCompleteTextView spinnerDefaultCurrency = findViewById(R.id.spinnerDefaultCurrency);
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies);
        spinnerDefaultCurrency.setAdapter(currencyAdapter);

        String currentCurrency = sessionManager.getDefaultCurrency();
        if (currentCurrency != null && !currentCurrency.isEmpty()) {
            spinnerDefaultCurrency.setText(currentCurrency, false);
        }

        MaterialButton btnSaveCurrency = findViewById(R.id.btnSaveCurrency);
        btnSaveCurrency.setOnClickListener(v -> {
            String selectedCurrency = spinnerDefaultCurrency.getText().toString();
            if (!selectedCurrency.isEmpty()) {
                sessionManager.setDefaultCurrency(selectedCurrency);
                Toast.makeText(this, "Currency updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtons() {
        findViewById(R.id.btnSaveName).setOnClickListener(v -> updateName());
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> updatePassword());

        // Setup logout button if needed
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

    private void updateName() {
        String newName = capitalizeWords(etNewName.getText().toString().trim());
        if (newName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        new Thread(() -> {
            db.userDao().updateName(userId, newName);
            sessionManager.updateUserName(newName);
            runOnUiThread(() -> Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void updatePassword() {
        String currentPass = etCurrentPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Please fill all password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            String hashedCurrentPass = hashPassword(currentPass);
            User user = db.userDao().getUser(sessionManager.getUserEmail(), hashedCurrentPass);

            if (user != null) {
                String hashedNewPass = hashPassword(newPass);
                db.userDao().updatePassword(user.getId(), hashedNewPass);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String capitalizeWords(String str) {
        if (str.isEmpty()) return str;
        String[] words = str.toLowerCase().split("\\s");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
}