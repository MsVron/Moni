package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OffersActivity extends AppCompatActivity {
    private AppDatabase db;
    private SessionManager sessionManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        setupToolbar();
        setupRecyclerView();
        loadOffers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Special Offers");
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvOffers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Create and set adapter
    }

    private void loadOffers() {
        new Thread(() -> {
            List<Offer> activeOffers = db.offerDao().getActiveOffers(System.currentTimeMillis());
            runOnUiThread(() -> {
                // TODO: Update adapter with offers
            });
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