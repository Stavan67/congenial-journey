package com.decor.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.decor.R;
import com.decor.admin.AdminLoginActivity;
import com.decor.entity.User;
import com.decor.repository.UserRepository;
import com.decor.util.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText editPhone;
    private EditText editPassword;
    private Button btnLogin;
    private TextView txtSignUp;
    private UserRepository userRepository;
    private SessionManager sessionManager;

    // For admin gesture detection
    private long lastTapTime = 0;
    private int tapCount = 0;
    private static final long DOUBLE_TAP_TIMEOUT = 500; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(getApplication());
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            String userId = sessionManager.getUserId();
            checkUserStatus(userId);
            return;
        }

        // Initialize views
        editPhone = findViewById(R.id.edit_login_phone);
        editPassword = findViewById(R.id.edit_login_password);
        btnLogin = findViewById(R.id.btn_login);
        txtSignUp = findViewById(R.id.txt_signup);

        btnLogin.setOnClickListener(v -> attemptLogin());
        txtSignUp.setOnClickListener(v -> navigateToSignup());

        // Set up hidden admin access through gesture (triple tap on app title)
        TextView appTitle = findViewById(R.id.txt_app_title);
        appTitle.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                tapCount++;
                if (tapCount >= 3) {
                    // Reset counter
                    tapCount = 0;
                    // Launch admin login
                    Intent adminIntent = new Intent(LoginActivity.this, AdminLoginActivity.class);
                    startActivity(adminIntent);
                }
            } else {
                tapCount = 1;
            }
            lastTapTime = currentTime;
        });
    }

    private void attemptLogin() {
        // Reset errors
        editPhone.setError(null);
        editPassword.setError(null);

        // Get values
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // In a real app, you would hash the password and validate against stored hash
        // For this demo, we're just checking if fields are non-empty and find user by phone

        // Validate fields
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Phone number is required");
            focusView = editPhone;
            cancel = true;
        }

        // For this demo, we'll treat phone number as login credential
        // In production, you should implement proper password management

        if (cancel) {
            // There was an error; focus the first form field with an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Look up user by phone
            userRepository.getAllUsers(users -> {
                User foundUser = null;
                for (User user : users) {
                    if (user.getPhone().equals(phone)) {
                        foundUser = user;
                        break;
                    }
                }

                if (foundUser != null) {
                    // User found, check approval status and navigate accordingly
                    if (foundUser.isApproved()) {
                        // Save user session
                        sessionManager.createLoginSession(foundUser.getUserId(), foundUser.getBusinessName());

                        // Navigate to main activity
                        runOnUiThread(() -> {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        // User not approved yet
                        runOnUiThread(() -> {
                            Intent intent = new Intent(LoginActivity.this, PendingApprovalActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                } else {
                    // User not found
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Account not found. Please register.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    private void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void checkUserStatus(String userId) {
        userRepository.getUserById(userId, user -> {
            if (user != null) {
                runOnUiThread(() -> {
                    Intent intent;
                    if (user.isApproved()) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, PendingApprovalActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });
            } else {
                // Invalid session, clear it
                sessionManager.logoutUser();
            }
        });
    }
}