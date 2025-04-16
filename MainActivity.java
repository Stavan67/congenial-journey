package com.decor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.decor.R;
import com.decor.admin.AdminLoginActivity;
import com.decor.repository.UserRepository;
import com.decor.util.SessionManager;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private UserRepository userRepository;
    private TextView txtWelcome;

    // For admin gesture detection - Triple tap on toolbar
    private long lastTapTime = 0;
    private int tapCount = 0;
    private static final long DOUBLE_TAP_TIMEOUT = 500; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        userRepository = new UserRepository(getApplication());

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // User is not logged in, redirect to login activity
            logoutUser();
            return;
        }

        txtWelcome = findViewById(R.id.txt_welcome);
        String businessName = sessionManager.getBusinessName();
        if (businessName != null && !businessName.isEmpty()) {
            txtWelcome.setText("Welcome, " + businessName);
        }

        // Setup admin gesture (triple tap on toolbar)
        toolbar.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                tapCount++;
                if (tapCount >= 3) {
                    // Reset counter
                    tapCount = 0;
                    // Launch admin login
                    Intent adminIntent = new Intent(MainActivity.this, AdminLoginActivity.class);
                    startActivity(adminIntent);
                }
            } else {
                tapCount = 1;
            }
            lastTapTime = currentTime;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Handle profile action - Navigate to profile screen
            Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            // Handle logout action
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        sessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}