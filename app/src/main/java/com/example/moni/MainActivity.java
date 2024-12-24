package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        // If not logged in, go to login screen
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set welcome message
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Welcome, " + sessionManager.getUserName() + "!");

        // Initialize balance display
        TextView tvBalance = findViewById(R.id.tvBalance);
        updateBalance();

        // Setup income card click
        CardView cardIncome = findViewById(R.id.cardIncome);
        cardIncome.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncomeActivity.class))
        );

        // Setup income history card click
        CardView cardIncomeHistory = findViewById(R.id.cardIncomeHistory); // Ensure this ID exists in your XML
        cardIncomeHistory.setOnClickListener(v ->
                startActivity(new Intent(this, IncomeHistoryActivity.class))
        );

        // Setup expense card click
        CardView cardExpense = findViewById(R.id.cardExpense);
        cardExpense.setOnClickListener(v ->
                // TODO: Implement expense activity
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Setup logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Setup FAB
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);
        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncomeActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    private void updateBalance() {
        new Thread(() -> {
            try {
                int userId = sessionManager.getUserId();  // Get user ID
                double totalIncome = db.incomeDao().getTotalIncome(userId);  // Use the user ID to get the total income
                runOnUiThread(() -> {
                    TextView tvBalance = findViewById(R.id.tvBalance);
                    String formattedBalance = String.format("$%.2f", totalIncome);
                    tvBalance.setText(formattedBalance);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
