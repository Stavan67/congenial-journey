package com.decor.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.decor.R;
import com.decor.util.SessionManager;

public class RejectedActivity extends AppCompatActivity {

    private static final String SUPPORT_PHONE = "+91 1234567890";
    private static final String SUPPORT_EMAIL = "support@yourcompany.com";

    private Button btnCallSupport;
    private Button btnEmailSupport;
    private Button btnRegisterAgain;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected);

        sessionManager = new SessionManager(this);

        // Initialize views
        TextView txtSupportPhone = findViewById(R.id.txt_support_phone);
        TextView txtSupportEmail = findViewById(R.id.txt_support_email);
        btnCallSupport = findViewById(R.id.btn_call_support);
        btnEmailSupport = findViewById(R.id.btn_email_support);
        btnRegisterAgain = findViewById(R.id.btn_register_again);

        // Set support contact info
        txtSupportPhone.setText("Phone: " + SUPPORT_PHONE);
        txtSupportEmail.setText("Email: " + SUPPORT_EMAIL);

        // Set click listeners
        btnCallSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + SUPPORT_PHONE.replace(" ", "")));
            startActivity(intent);
        });

        btnEmailSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + SUPPORT_EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Registration Rejection Inquiry");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI would like to inquire about my rejected registration on your app. Can you provide more information?\n\nThank you.");
            startActivity(intent);
        });

        btnRegisterAgain.setOnClickListener(v -> {
            // Reset registration status
            sessionManager.setRegistrationStatus(SessionManager.REGISTRATION_STATUS_NONE);
            // Go to signup
            Intent intent = new Intent(RejectedActivity.this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to previous screen
        moveTaskToBack(true);
    }
}