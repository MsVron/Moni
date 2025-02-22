package com.example.moni;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppDatabase db;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private EditText etOfferTitle, etOfferDescription, etOfferEndDate;
    private ImageView ivOfferImage;
    private Calendar endDateTime = Calendar.getInstance();
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        initializeViews();
        setupToolbarAndDrawer();
        setupListeners();
    }


    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);

        // Offer Management
        etOfferTitle = findViewById(R.id.etOfferTitle);
        etOfferDescription = findViewById(R.id.etOfferDescription);
        etOfferEndDate = findViewById(R.id.etOfferEndDate);
        ivOfferImage = findViewById(R.id.ivOfferImage);
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_view_offers) {
                startActivity(new Intent(this, OffersActivity.class));
            } else if (id == R.id.nav_admin_logout) {
                sessionManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupListeners() {
        // Date and time picker for offer end time
        etOfferEndDate.setOnClickListener(v -> showDateTimePicker());

        // Image picker for offer banner
        ivOfferImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Save offer button
        findViewById(R.id.btnSaveOffer).setOnClickListener(v -> saveOffer());
    }

    private void showDateTimePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            endDateTime.set(Calendar.YEAR, year);
            endDateTime.set(Calendar.MONTH, month);
            endDateTime.set(Calendar.DAY_OF_MONTH, day);

            new TimePickerDialog(this, (view1, hour, minute) -> {
                endDateTime.set(Calendar.HOUR_OF_DAY, hour);
                endDateTime.set(Calendar.MINUTE, minute);
                updateEndDateDisplay();
            }, endDateTime.get(Calendar.HOUR_OF_DAY),
                    endDateTime.get(Calendar.MINUTE), true).show();
        }, endDateTime.get(Calendar.YEAR),
                endDateTime.get(Calendar.MONTH),
                endDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateEndDateDisplay() {
        String dateTimeStr = android.text.format.DateFormat.format(
                "MM/dd/yyyy HH:mm", endDateTime.getTime()).toString();
        etOfferEndDate.setText(dateTimeStr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Correct permission flags
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

                ivOfferImage.setImageURI(selectedImageUri);
            }
        }
    }


    private void saveOffer() {
        String title = etOfferTitle.getText().toString();
        String description = etOfferDescription.getText().toString();
        long endTime = endDateTime.getTimeInMillis();

        if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Offer offer = new Offer(title, description, endTime);
        offer.setImageUrl(selectedImageUri.toString());
        offer.setActive(true);

        Log.d("AdminDashboard", "Saving offer: " +
                "\nTitle: " + title +
                "\nDescription: " + description +
                "\nEndTime: " + endTime +
                "\nImageUrl: " + selectedImageUri +
                "\nIsActive: " + offer.isActive());

        new Thread(() -> {
            db.offerDao().insert(offer);
            runOnUiThread(() -> {
                clearOfferFields();
                Toast.makeText(this, "Offer saved successfully", Toast.LENGTH_SHORT).show();
                try {
                    List<Offer> offers = db.offerDao().getActiveOffers(System.currentTimeMillis());
                    Log.d("AdminDashboard", "Total active offers after save: " + offers.size());
                } catch (Exception e) {
                    Log.e("AdminDashboard", "Error checking offers: " + e.getMessage());
                }
            });
        }).start();
    }

    private void clearOfferFields() {
        etOfferTitle.setText("");
        etOfferDescription.setText("");
        etOfferEndDate.setText("");
        ivOfferImage.setImageResource(android.R.drawable.ic_menu_camera);
        selectedImageUri = null;
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
