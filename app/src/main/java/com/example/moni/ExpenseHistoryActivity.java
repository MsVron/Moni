package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseHistoryActivity extends AppCompatActivity {
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
        adapter = new ExpenseAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenseHistory() {
        new Thread(() -> {
            List<Expense> expenseList = db.expenseDao().getAllExpenses(sessionManager.getUserId());
            runOnUiThread(() -> adapter.setExpenseList(expenseList));
        }).start();
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