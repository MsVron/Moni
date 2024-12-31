package com.example.moni;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword); // Initialize etConfirmPassword
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private String capitalizeWords(String str) {
        if (str.isEmpty()) return str;
        String[] words = str.toLowerCase().split("\\s");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
    private void attemptRegister() {
        // Get the input values
        final String inputName = capitalizeWords(etName.getText().toString().trim());
        final String inputEmail = etEmail.getText().toString().trim();
        final String inputPassword = etPassword.getText().toString().trim();
        final String inputConfirmPassword = etConfirmPassword.getText().toString().trim();

        if (inputName.isEmpty() || inputEmail.isEmpty() || inputPassword.isEmpty() || inputConfirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email
        if (!isValidEmail(inputEmail)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password length
        if (inputPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!inputPassword.equals(inputConfirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate name
        if (inputName.length() < 2) {
            Toast.makeText(this, "Name must be at least 2 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sanitize inputs before passing to thread
        final String sanitizedName = sanitizeInput(inputName);
        final String sanitizedEmail = sanitizeInput(inputEmail);
        final String hashedPassword = hashPassword(inputPassword);

        new Thread(() -> {
            try {
                // Check for existing user
                User existingUser = db.userDao().getUserByEmail(sanitizedEmail);
                if (existingUser != null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Create new user with sanitized and hashed values
                User newUser = new User(sanitizedEmail, hashedPassword, sanitizedName);
                db.userDao().insert(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in registration: ", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Registration error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email != null && email.matches(emailPattern);
    }

    private String sanitizeInput(String input) {
        // Remove potentially harmful SQL special characters
        return input.replaceAll("[;'\"\\\\]", "");
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error hashing password: ", e);
            return password; // Fallback to plain password if hashing fails
        }
    }
}
