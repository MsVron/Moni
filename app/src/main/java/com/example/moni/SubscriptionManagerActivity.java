package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import com.example.moni.AppDatabase;
import com.example.moni.SubscriptionPlan;


public class SubscriptionManagerActivity extends AppCompatActivity
        implements SubscriptionManagerAdapter.OnPlanActionListener {

    private AppDatabase db;
    private SubscriptionManagerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_manager);

        db = AppDatabase.getInstance(this);

        setupToolbar();
        setupRecyclerView();
        setupAddButton();
        loadSubscriptionPlans();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Subscription Plans");
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvSubscriptionPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubscriptionManagerAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupAddButton() {
        findViewById(R.id.btnAddPlan).setOnClickListener(v ->
                showEditDialog(null)); // null means new plan
    }

    private void loadSubscriptionPlans() {
        new Thread(() -> {
            List<SubscriptionPlan> plans = db.subscriptionPlanDao().getActivePlans();
            runOnUiThread(() -> adapter.setPlans(plans));
        }).start();
    }

    @Override
    public void onEditPlan(SubscriptionPlan plan) {
        showEditDialog(plan);
    }

    @Override
    public void onDeletePlan(SubscriptionPlan plan) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this subscription plan?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        plan.setActive(false);
                        db.subscriptionPlanDao().update(plan);
                        runOnUiThread(this::loadSubscriptionPlans);
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(SubscriptionPlan plan) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_subscription_plan, null);
        EditText etTitle = view.findViewById(R.id.etPlanTitle);
        EditText etPrice = view.findViewById(R.id.etPlanPrice);
        EditText etDescription = view.findViewById(R.id.etPlanDescription);

        if (plan != null) {
            etTitle.setText(plan.getTitle());
            etPrice.setText(String.format(Locale.US, "%.2f", plan.getPrice()));
            etDescription.setText(plan.getDescription());
        }

        new AlertDialog.Builder(this)
                .setTitle(plan == null ? "Add Plan" : "Edit Plan")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String priceStr = etPrice.getText().toString();
                    String description = etDescription.getText().toString();

                    if (title.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        new Thread(() -> {
                            if (plan == null) {
                                SubscriptionPlan newPlan = new SubscriptionPlan(title, price, description);
                                db.subscriptionPlanDao().insert(newPlan);
                            } else {
                                plan.setTitle(title);
                                plan.setPrice(price);
                                plan.setDescription(description);
                                db.subscriptionPlanDao().update(plan);
                            }
                            runOnUiThread(this::loadSubscriptionPlans);
                        }).start();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                    }
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
}