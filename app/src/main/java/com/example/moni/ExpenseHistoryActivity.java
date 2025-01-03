package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseHistoryActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        setupToolbar();
        setupRecyclerView();
        loadExpenseHistory();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Expense History");
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(this, this); // Pass 'this' as the click listener
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenseHistory() {
        new Thread(() -> {
            List<Expense> expenseList = db.expenseDao().getAllExpenses(sessionManager.getUserId());
            runOnUiThread(() -> adapter.setExpenseList(expenseList));
        }).start();
    }

    @Override
    public void onEditClick(Expense expense) {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("EXPENSE_ID", expense.getId());
        intent.putExtra("EXPENSE_AMOUNT", expense.getAmount());
        intent.putExtra("EXPENSE_CATEGORY", expense.getCategory());
        intent.putExtra("EXPENSE_SUBCATEGORY", expense.getSubcategory());
        intent.putExtra("EXPENSE_DATE", expense.getDate());
        intent.putExtra("EXPENSE_DESCRIPTION", expense.getDescription());
        intent.putExtra("EXPENSE_COLOR", expense.getColor());
        intent.putExtra("EXPENSE_CURRENCY", expense.getCurrency());
        intent.putExtra("EXPENSE_IS_RECURRING", expense.isRecurring());
        intent.putExtra("EXPENSE_RECURRING_PERIOD", expense.getRecurringPeriod());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Expense expense) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        db.expenseDao().delete(expense.getId());
                        runOnUiThread(() -> {
                            loadExpenseHistory();
                            Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseHistory(); // Reload expenses when returning to this screen
    }
}