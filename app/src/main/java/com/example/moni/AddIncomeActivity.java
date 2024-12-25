package com.example.moni;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.moni.AppDatabase;  // Update with your actual package name
import com.example.moni.Income;  // Update with your actual package name
import com.example.moni.SessionManager;

public class AddIncomeActivity extends AppCompatActivity {
    private EditText etAmount, etDate, etDescription;
    private AutoCompleteTextView spinnerType, spinnerCurrency;
    private AppDatabase db;
    private Calendar calendar;
    private String selectedColor = "#FF4444";

    private View lastSelectedColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        db = AppDatabase.getInstance(this);
        calendar = Calendar.getInstance();

        // Initialize views
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCurrency = findViewById(R.id.spinnerCurrency); // Currency spinner
        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Setup income type spinner
        String[] incomeTypes = {"Salary", "Freelance", "Investment", "Business", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, incomeTypes);
        spinnerType.setAdapter(adapter);

        // Setup currency spinner
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currencies);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Retrieve and set the default currency from session manager
        SessionManager sessionManager = new SessionManager(this);
        String defaultCurrency = sessionManager.getDefaultCurrency();
        spinnerCurrency.setText(defaultCurrency, false);

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Set current date as default
        updateDateLabel();

        // Set click listener to save income
        btnSave.setOnClickListener(v -> saveIncome());

        // Setup color selection
        setupColorSelection();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabel() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void setupColorSelection() {
        int[] colorViews = {
                R.id.colorRed,
                R.id.colorBlue,
                R.id.colorGreen,
                R.id.colorPurple,
                R.id.colorOrange
        };

        int[] checkViews = {
                R.id.checkRed,
                R.id.checkBlue,
                R.id.checkGreen,
                R.id.checkPurple,
                R.id.checkOrange
        };

        String[] colors = {
                "#FF4444",
                "#4444FF",
                "#44FF44",
                "#9944FF",
                "#FFAA44"
        };

        // Set initial selection
        View initialColorView = findViewById(R.id.colorRed);
        ImageView initialCheckView = findViewById(R.id.checkRed);
        initialColorView.setScaleX(1.1f);
        initialColorView.setScaleY(1.1f);
        initialCheckView.setVisibility(View.VISIBLE);
        lastSelectedColorView = initialColorView;
        selectedColor = colors[0];

        // Hide all check icons initially except the first one
        for (int i = 1; i < checkViews.length; i++) {
            findViewById(checkViews[i]).setVisibility(View.GONE);
        }

        // Setup click listeners
        for (int i = 0; i < colorViews.length; i++) {
            int finalI = i;
            View colorView = findViewById(colorViews[i]);
            ImageView checkView = findViewById(checkViews[i]);

            colorView.setOnClickListener(v -> {
                // Reset last selection
                if (lastSelectedColorView != null) {
                    lastSelectedColorView.setScaleX(1.0f);
                    lastSelectedColorView.setScaleY(1.0f);
                    // Find and hide the last check icon
                    for (int checkId : checkViews) {
                        findViewById(checkId).setVisibility(View.GONE);
                    }
                }

                // Scale up selected color (smaller scale factor)
                v.setScaleX(1.1f);
                v.setScaleY(1.1f);
                lastSelectedColorView = v;

                // Show check icon for selected color
                checkView.setVisibility(View.VISIBLE);

                selectedColor = colors[finalI];
            });
        }
    }

    private void saveIncome() {
        try {
            String amountStr = etAmount.getText().toString();
            String type = spinnerType.getText().toString();
            String currency = spinnerCurrency.getText().toString(); // Get selected currency
            String date = etDate.getText().toString();
            String description = etDescription.getText().toString();

            if (amountStr.isEmpty() || type.isEmpty() || currency.isEmpty() || selectedColor == null) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            SessionManager sessionManager = new SessionManager(this);
            int userId = sessionManager.getUserId(); // Get the user ID from session

            Income income = new Income(userId, amount, type, date, description, selectedColor, currency); // Include color and currency

            // Insert income into database
            new Thread(() -> {
                db.incomeDao().insert(income);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Income saved successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after saving
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }
}
