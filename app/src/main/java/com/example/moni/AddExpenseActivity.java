package com.example.moni;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;  // Add this import
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText etAmount, etDate, etDescription;
    private AutoCompleteTextView spinnerType, spinnerCurrency;
    private AppDatabase db;
    private Calendar calendar;
    private SessionManager sessionManager;
    private String selectedColor = "#FF4444";
    private View lastSelectedColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = AppDatabase.getInstance(this);
        calendar = Calendar.getInstance();
        sessionManager = new SessionManager(this);

        setupToolbar();
        initializeViews();
        setupSpinners();
        setupDatePicker();
        setupColorSelection();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Expense");
        }
    }

    private void initializeViews() {
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void setupSpinners() {
        // Setup expense type spinner
        String[] expenseTypes = {"Food", "Transport", "Shopping", "Bills", "Entertainment", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, expenseTypes);
        spinnerType.setAdapter(typeAdapter);

        // Setup currency spinner with default selection
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Set default currency from settings
        String defaultCurrency = sessionManager.getDefaultCurrency();
        spinnerCurrency.setText(defaultCurrency, false);
    }

    private void setupDatePicker() {
        updateDateLabel();
        etDate.setOnClickListener(v -> showDatePicker());
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
        lastSelectedColorView = initialColorView;
        selectedColor = colors[0];

        // Setup click listeners
        for (int i = 0; i < colorViews.length; i++) {
            int finalI = i;
            View colorView = findViewById(colorViews[i]);
            findViewById(checkViews[i]).setVisibility(i == 0 ? View.VISIBLE : View.GONE);

            colorView.setOnClickListener(v -> {
                // Reset last selection
                if (lastSelectedColorView != null) {
                    lastSelectedColorView.setScaleX(1.0f);
                    lastSelectedColorView.setScaleY(1.0f);
                    for (int checkId : checkViews) {
                        findViewById(checkId).setVisibility(View.GONE);
                    }
                }

                // Scale up selected color
                v.setScaleX(1.1f);
                v.setScaleY(1.1f);
                lastSelectedColorView = v;

                // Show check icon
                findViewById(checkViews[finalI]).setVisibility(View.VISIBLE);

                selectedColor = colors[finalI];
            });
        }
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

    private void saveExpense() {
        try {
            String amountStr = etAmount.getText().toString();
            String type = spinnerType.getText().toString();
            String currency = spinnerCurrency.getText().toString();
            String date = etDate.getText().toString();
            String description = etDescription.getText().toString();

            if (amountStr.isEmpty() || type.isEmpty() || currency.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            int userId = sessionManager.getUserId();

            Expense expense = new Expense(userId, amount, type, date, description, selectedColor, currency);

            new Thread(() -> {
                db.expenseDao().insert(expense);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
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