package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import java.util.stream.Collectors;

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

        // If admin, go to admin dashboard
        if (sessionManager.isAdmin()) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupToolbarAndDrawer();
        setupNavigationDrawer();
        setupClickListeners();
        updateWelcomeAndBalance();
    }

    private void initializeViews() {
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
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_offers) {
                startActivity(new Intent(this, OffersActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_logout) {
                showLogoutConfirmationDialog();
                drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });
    }

    private void showLogoutConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logout();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void setupClickListeners() {
        cardIncome.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncomeActivity.class)));

        cardIncomeHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        cardExpense.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class)));

        fabAddTransaction.setOnClickListener(v -> {
            AddTransactionBottomSheet bottomSheet = new AddTransactionBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "addTransaction");
        });
    }

    private void updateWelcomeAndBalance() {
        tvWelcome.setText("Welcome, " + sessionManager.getUserName() + "!");
        updateBalance();
    }

    private void updateBalance() {
        new Thread(() -> {
            try {
                int userId = sessionManager.getUserId();
                double totalIncome = db.incomeDao().getTotalIncome(userId);
                double totalExpense = db.expenseDao().getTotalExpense(userId);
                double balance = totalIncome - totalExpense;

                runOnUiThread(() -> {
                    String formattedBalance = String.format("$%.2f", balance);
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

    private void checkForActiveOffers() {
        new Thread(() -> {
            try {
                List<Offer> activeOffers = db.offerDao().getActiveOffers(System.currentTimeMillis());
                // Filter out offers that have been seen
                List<Offer> unseenOffers = activeOffers.stream()
                        .filter(offer -> !sessionManager.hasSeenOffer(offer.getId()))
                        .collect(Collectors.toList());

                if (!unseenOffers.isEmpty()) {
                    runOnUiThread(() -> showOfferDialog(unseenOffers));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    private void showOfferDialog(List<Offer> offers) {
        OfferDialog dialog = new OfferDialog(this, offers, new OfferDialog.OnOfferActionListener() {
            @Override
            public void onLearnMore(Offer offer) {
                sessionManager.markOfferAsSeen(offer.getId());
            }

            @Override
            public void onDismiss(Offer offer) {
                sessionManager.markOfferAsSeen(offer.getId());
            }
        });
        dialog.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
        updateWelcomeAndBalance();

        // Check if we should reset offers due to time period
        if (sessionManager.shouldResetOffers()) {
            sessionManager.clearSeenOffers();
        }

        // Now check for active offers
        checkForActiveOffers();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
