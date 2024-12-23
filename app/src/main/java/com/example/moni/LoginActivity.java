package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Move this up

        // Initialize both database and session manager early
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        // Check login state after initialization
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add debug logging
        Log.d(TAG, "Attempting login with email: " + email);

        new Thread(() -> {
            try {
                User user = db.userDao().getUser(email, password);
                runOnUiThread(() -> {
                    if (user != null) {
                        try {
                            Log.d(TAG, "User found, setting login state");
                            sessionManager.setLogin(true, user.getEmail(), user.getName());
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting login state: ", e);
                            Toast.makeText(this, "Error during login: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Database error: ", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Database error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}