package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity implements OffersAdapter.OnOfferClickListener {
    private AppDatabase db;
    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private OffersAdapter adapter;

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
        adapter = new OffersAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadOffers() {
        Log.d("OffersActivity", "Starting to load offers");
        new Thread(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                Log.d("OffersActivity", "Current time: " + currentTime);

                List<Offer> activeOffers = db.offerDao().getActiveOffers(currentTime);
                Log.d("OffersActivity", "Loaded offers: " + activeOffers.size());

                if (!activeOffers.isEmpty()) {
                    for (Offer offer : activeOffers) {
                        Log.d("OffersActivity", "Offer: " +
                                "\nID: " + offer.getId() +
                                "\nTitle: " + offer.getTitle() +
                                "\nEndTime: " + offer.getEndTime() +
                                "\nIsActive: " + offer.isActive());
                    }
                }

                runOnUiThread(() -> {
                    if (activeOffers.isEmpty()) {
                        Toast.makeText(this, "No active offers available", Toast.LENGTH_SHORT).show();
                    }
                    adapter.setOffers(activeOffers);
                });
            } catch (Exception e) {
                Log.e("OffersActivity", "Error loading offers: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onOfferClick(Offer offer) {
        List<Offer> singleOfferList = new ArrayList<>();
        singleOfferList.add(offer);

        OfferDialog dialog = new OfferDialog(this, singleOfferList, new OfferDialog.OnOfferActionListener() {
            @Override
            public void onLearnMore(Offer offer) {
                if ("SUBSCRIPTION".equals(offer.getType())) {
                    startActivity(new Intent(OffersActivity.this, SubscriptionActivity.class));
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
