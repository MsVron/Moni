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



public class AddIncomeActivity extends AppCompatActivity {
    private EditText etAmount, etDate, etDescription;
    private AutoCompleteTextView spinnerCategory, spinnerSubcategory, spinnerCurrency, spinnerRecurringPeriod;
    private com.google.android.material.switchmaterial.SwitchMaterial switchRecurring;
    private View layoutRecurringPeriod;
    private AppDatabase db;
    private Calendar calendar;
    private String selectedColor = "#FF4444";
    private View lastSelectedColorView;
    private int incomeId = -1;



    private void populateFieldsForEditing() {
        etAmount.setText(String.valueOf(getIntent().getDoubleExtra("AMOUNT", 0)));
        spinnerCategory.setText(getIntent().getStringExtra("CATEGORY"), false);
        spinnerSubcategory.setText(getIntent().getStringExtra("SUBCATEGORY"), false);
        etDate.setText(getIntent().getStringExtra("DATE"));
        etDescription.setText(getIntent().getStringExtra("DESCRIPTION"));
        selectedColor = getIntent().getStringExtra("COLOR");
        spinnerCurrency.setText(getIntent().getStringExtra("CURRENCY"), false);
        switchRecurring.setChecked(getIntent().getBooleanExtra("IS_RECURRING", false));
        if (switchRecurring.isChecked()) {
            spinnerRecurringPeriod.setText(getIntent().getStringExtra("RECURRING_PERIOD"), false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        db = AppDatabase.getInstance(this);
        calendar = Calendar.getInstance();

        initializeViews();
        setupSpinners();
        setupDatePicker();
        setupColorSelection();
        setupRecurringIncome();

        // Check if we're editing an existing income
        if (getIntent().hasExtra("INCOME_ID")) {
            incomeId = getIntent().getIntExtra("INCOME_ID", -1);
            populateFieldsForEditing();
        }
    }

    private void initializeViews() {
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerType);
        spinnerSubcategory = findViewById(R.id.spinnerSubcategory);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerRecurringPeriod = findViewById(R.id.spinnerRecurringPeriod);
        switchRecurring = findViewById(R.id.switchRecurring);
        layoutRecurringPeriod = findViewById(R.id.layoutRecurringPeriod);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveIncome());
    }

    private void setupSpinners() {
        // Setup income categories
        String[] categories = new String[IncomeCategory.values().length];
        for (int i = 0; i < IncomeCategory.values().length; i++) {
            categories[i] = IncomeCategory.values()[i].getCategoryName();
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // Setup category selection listener
        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            IncomeCategory selectedCategory = IncomeCategory.values()[position];
            updateSubcategories(selectedCategory);
        });

        // Setup currency spinner
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Set default currency
        SessionManager sessionManager = new SessionManager(this);
        spinnerCurrency.setText(sessionManager.getDefaultCurrency(), false);

        // Setup recurring period spinner
        String[] recurringPeriods = {"Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, recurringPeriods);
        spinnerRecurringPeriod.setAdapter(periodAdapter);
    }

    private void updateSubcategories(IncomeCategory category) {
        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, category.getSubcategories());
        spinnerSubcategory.setAdapter(subcategoryAdapter);
        spinnerSubcategory.setText("", false);
    }

    private void setupRecurringIncome() {
        switchRecurring.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRecurringPeriod.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                spinnerRecurringPeriod.setText("", false);
            }
        });
    }

    private void setupDatePicker() {
        updateDateLabel();
        etDate.setOnClickListener(v -> showDatePicker());
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
            String category = spinnerCategory.getText().toString();
            String subcategory = spinnerSubcategory.getText().toString();
            String currency = spinnerCurrency.getText().toString();
            String date = etDate.getText().toString();
            String description = etDescription.getText().toString();
            boolean isRecurring = switchRecurring.isChecked();
            String recurringPeriod = isRecurring ? spinnerRecurringPeriod.getText().toString() : null;

            if (amountStr.isEmpty() || category.isEmpty() || subcategory.isEmpty()
                    || currency.isEmpty() || (isRecurring && recurringPeriod == null)) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            SessionManager sessionManager = new SessionManager(this);
            int userId = sessionManager.getUserId();

            Income income = new Income(
                    userId,
                    amount,
                    category,
                    subcategory,
                    date,
                    description,
                    selectedColor,
                    currency,
                    isRecurring,
                    recurringPeriod
            );

            // Check if this is an update scenario
            if (incomeId != -1) {
                income.setId(incomeId); // Ensure Income class has a setId method
            }

            new Thread(() -> {
                if (incomeId != -1) {
                    // Update existing income
                    db.incomeDao().update(income); // Ensure IncomeDao has an update method
                } else {
                    // Insert new income
                    db.incomeDao().insert(income);
                }

                runOnUiThread(() -> {
                    String message = incomeId != -1 ? "Income updated successfully" : "Income saved successfully";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }

}