package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private AppDatabase db;
    private DrawerLayout drawerLayout;

    // UI Elements
    private TextView tvWelcome;
    private TextView tvBalance;
    private CardView cardIncome;
    private CardView cardIncomeHistory;
    private CardView cardExpense;
    private FloatingActionButton fabAddTransaction;

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

        initializeViews();
        setupToolbarAndDrawer();
        setupClickListeners();
        updateWelcomeAndBalance();
    }

    private void initializeViews() {
        // Find all views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvBalance = findViewById(R.id.tvBalance);
        cardIncome = findViewById(R.id.cardIncome);
        cardIncomeHistory = findViewById(R.id.cardIncomeHistory);
        cardExpense = findViewById(R.id.cardExpense);
        fabAddTransaction = findViewById(R.id.fabAddTransaction);
        drawerLayout = findViewById(R.id.drawerLayout);
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size); // Using default menu icon for now
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_logout) {
                sessionManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        cardIncome.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncomeActivity.class)));

        cardIncomeHistory.setOnClickListener(v ->
                startActivity(new Intent(this, IncomeHistoryActivity.class)));

        cardExpense.setOnClickListener(v ->
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show());

        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncomeActivity.class)));
    }

    private void updateWelcomeAndBalance() {
        tvWelcome.setText("Welcome, " + sessionManager.getUserName() + "!");
        updateBalance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    private void updateBalance() {
        new Thread(() -> {
            try {
                int userId = sessionManager.getUserId();
                double totalIncome = db.incomeDao().getTotalIncome(userId);
                runOnUiThread(() -> {
                    String formattedBalance = String.format("$%.2f", totalIncome);
                    tvBalance.setText(formattedBalance);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error updating balance", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}