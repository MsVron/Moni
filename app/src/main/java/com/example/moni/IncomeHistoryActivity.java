package com.example.moni;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeHistoryActivity extends AppCompatActivity {
    private AppDatabase db;
    private SessionManager sessionManager;
    private IncomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_history);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        RecyclerView recyclerView = findViewById(R.id.rvIncome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IncomeAdapter(this);
        recyclerView.setAdapter(adapter);

        loadIncomeHistory();
    }

    private void loadIncomeHistory() {
        new Thread(() -> {
            List<Income> incomeList = db.incomeDao().getAllIncome(sessionManager.getUserId());
            runOnUiThread(() -> adapter.setIncomeList(incomeList));
        }).start();
    }
}