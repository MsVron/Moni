package com.example.moni;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

public class SubscriptionActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        sessionManager = new SessionManager(this);

        setupToolbar();
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Premium Plans");
        }
    }

    private void setupButtons() {
        MaterialButton btnBasic = findViewById(R.id.btnSubscribeBasic);
        MaterialButton btnPro = findViewById(R.id.btnSubscribePro);

        btnBasic.setOnClickListener(v -> {
            // TODO: Implement payment processing
            Toast.makeText(this, "Basic subscription - Coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnPro.setOnClickListener(v -> {
            // TODO: Implement payment processing
            Toast.makeText(this, "Pro subscription - Coming soon!", Toast.LENGTH_SHORT).show();
        });
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