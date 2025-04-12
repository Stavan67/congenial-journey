package com.decor.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.decor.R;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;

    // In a real app, you would use a proper authentication system
    // This is just a simple example
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Login");
        }

        // Initialize views
        editUsername = findViewById(R.id.edit_admin_username);
        editPassword = findViewById(R.id.edit_admin_password);
        btnLogin = findViewById(R.id.btn_admin_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Check if already logged in
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("admin_logged_in", false);
        if (isLoggedIn) {
            navigateToAdminDashboard();
        }
    }

    private void attemptLogin() {
        // Reset errors
        editUsername.setError(null);
        editPassword.setError(null);

        // Get values
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Check for empty fields
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            editUsername.setError("Username is required");
            focusView = editUsername;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password is required");
            focusView = editPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Simple authentication check
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                // Save login state
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean("admin_logged_in", true).apply();
                navigateToAdminDashboard();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToAdminDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}