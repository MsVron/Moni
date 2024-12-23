package com.example.moni;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIncomeActivity extends AppCompatActivity {
    private EditText etAmount, etDate, etDescription;
    private AutoCompleteTextView spinnerType;
    private AppDatabase db;
    private Calendar calendar;

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
        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Setup income type spinner
        String[] incomeTypes = {"Salary", "Freelance", "Investment", "Business", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                incomeTypes
        );
        spinnerType.setAdapter(adapter);

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Set current date as default
        updateDateLabel();

        // Set click listener to save income
        btnSave.setOnClickListener(v -> saveIncome());
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

    private void saveIncome() {
        try {
            String amountStr = etAmount.getText().toString();
            String type = spinnerType.getText().toString();
            String date = etDate.getText().toString();
            String description = etDescription.getText().toString();

            if (amountStr.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            SessionManager sessionManager = new SessionManager(this);
            int userId = sessionManager.getUserId(); // Get the user ID from session

            Income income = new Income(userId, amount, type, date, description); // Include user ID

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
