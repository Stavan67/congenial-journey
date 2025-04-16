package com.decor.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.decor.R;
import com.decor.admin.AdminLoginActivity;
import com.decor.entity.User;
import com.decor.repository.UserRepository;
import com.decor.util.FileUtil;
import com.decor.util.SessionManager;

import java.util.Random;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_FILE_PICK = 101;
    private static final String TAG = "SignupActivity";

    private EditText editBusinessName;
    private EditText editYearEstablished;
    private EditText editGstNumber;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editAddress;
    private EditText editReferralCode;
    private Button btnUploadLicense;
    private Button btnSignup;
    private Button btnVerifyPhone;

    private UserRepository userRepository;
    private Uri licenseFileUri = null;
    private String userId = UUID.randomUUID().toString();
    private boolean isPhoneVerified = false;
    private String mockOtp; // Store the mock OTP
    private SessionManager sessionManager;
    private long lastTapTime = 0;
    private int tapCount = 0;
    private static final long DOUBLE_TAP_TIMEOUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);

        userRepository = new UserRepository(getApplication());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registration");
        }

        TextView appTitle = findViewById(R.id.txt_app_title_reg);
        if (appTitle != null) {
            appTitle.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                    tapCount++;
                    if (tapCount >= 3) {
                        // Reset counter
                        tapCount = 0;
                        // Launch admin login
                        Intent adminIntent = new Intent(SignupActivity.this, AdminLoginActivity.class);
                        startActivity(adminIntent);
                    }
                } else {
                    tapCount = 1;
                }
                lastTapTime = currentTime;
            });
        }

        Button btnGoToLogin = findViewById(R.id.btn_goto_login);
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Initialize UI components
        editBusinessName = findViewById(R.id.edit_business_name);
        editYearEstablished = findViewById(R.id.edit_year_established);
        editGstNumber = findViewById(R.id.edit_gst_number);
        editPhone = findViewById(R.id.edit_phone);
        editPhone.setText("+91");
        editPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Prevent deleting the prefix
                return editPhone.getSelectionStart() <= 3 && keyCode == KeyEvent.KEYCODE_DEL;
            }
        });
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91")) {
                    editPhone.setText("+91");
                    editPhone.setSelection(editPhone.getText().length());
                }

                if (s.length() > 13) {
                    editPhone.setText(s.subSequence(0, 13).toString());
                    editPhone.setSelection(editPhone.getText().length());
                }
            }
        });
        editPhone.setSelection(editPhone.getText().length());
        editEmail = findViewById(R.id.edit_email);
        editAddress = findViewById(R.id.edit_address);
        editReferralCode = findViewById(R.id.edit_referral_code);
        btnUploadLicense = findViewById(R.id.btn_upload_license);
        btnSignup = findViewById(R.id.btn_signup);
        btnVerifyPhone = findViewById(R.id.btn_verify_phone);

        btnUploadLicense.setOnClickListener(v -> checkStoragePermissionAndPickFile());
        btnVerifyPhone.setOnClickListener(v -> verifyPhoneNumber());
        btnSignup.setOnClickListener(v -> validateAndSignup());
    }

    private void checkStoragePermissionAndPickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ directly go to file picker (using scoped storage)
            pickFile();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            pickFile();
        }
    }

    // In SignupActivity.java, add this method
    private boolean isValidGSTNumber(String gst) {
        // GST format: 2 letters, 10 digits, 1 letter, 1 digit, 1 letter
        String gstPattern = "^[0-9A-Za-z]{15}$";
        return gst.matches(gstPattern);
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_FILE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_PICK && resultCode == RESULT_OK && data != null) {
            licenseFileUri = data.getData();
            String fileName = FileUtil.getFileName(this, licenseFileUri);
            btnUploadLicense.setText("File selected: " + fileName);
        }
    }

    // In SignupActivity.java, add this method
    private void showOtpNotification(String otp) {
        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "otp_channel",
                    "OTP Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "otp_channel")
                .setSmallIcon(R.drawable.ic_notification) // You need to create this drawable
                .setContentTitle("Your OTP Code")
                .setContentText("Use this code to verify your number: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, request notification permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 102);
        }
    }

    private void verifyPhoneNumber() {
        String phoneNumber = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            editPhone.setError("Phone number is required");
            return;
        }

        if (!phoneNumber.startsWith("+")) {
            editPhone.setError("Include country code (e.g. +91...)");
            return;
        }

        // Disable button to prevent multiple requests
        btnVerifyPhone.setEnabled(false);
        btnVerifyPhone.setText("Sending OTP...");

        // For development/testing - generate a random 6-digit OTP
        mockOtp = String.format("%06d", new Random().nextInt(999999));

        // Simulate network delay for realism (1.5 seconds)
        btnVerifyPhone.postDelayed(() -> {
            showOtpNotification(mockOtp);
            showOtpDialog();

            // Don't re-enable the button, it will be handled in onOtpVerified
        }, 1500);
    }

    private void showOtpDialog() {
        OtpVerificationDialog dialog = new OtpVerificationDialog();
        dialog.setMockOtp(mockOtp);
        dialog.setOtpVerificationListener(new OtpVerificationDialog.OtpVerificationListener() {
            @Override
            public void onOtpVerified(boolean isSuccess) {
                if (isSuccess) {
                    isPhoneVerified = true;
                    btnVerifyPhone.setText("Verified ✓");
                    btnVerifyPhone.setEnabled(false);
                    Toast.makeText(SignupActivity.this, "Phone verified successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // If verification fails, allow retry
                    btnVerifyPhone.setText("Verify Phone");
                    btnVerifyPhone.setEnabled(true);
                    Toast.makeText(SignupActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResendOtp() {
                // Generate a new OTP and resend
                mockOtp = String.format("%06d", new Random().nextInt(999999));
                showOtpNotification(mockOtp);
                Log.d(TAG, "Resent mock OTP: " + mockOtp);
            }
        });
        dialog.show(getSupportFragmentManager(), "OTP_DIALOG");
    }

    private void validateAndSignup() {
        String businessName = editBusinessName.getText().toString().trim();
        String yearStr = editYearEstablished.getText().toString().trim();
        String gstNumber = editGstNumber.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String referralCode = editReferralCode.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(businessName)) {
            editBusinessName.setError("Business name is required");
            return;
        }

        if (TextUtils.isEmpty(yearStr)) {
            editYearEstablished.setError("Year established is required");
            return;
        }

        if (TextUtils.isEmpty(gstNumber)) {
            editGstNumber.setError("GST number is required");
            return;
        }

        if (!isValidGSTNumber(gstNumber)) {
            editGstNumber.setError("Invalid GST number. It must be 15 alphanumeric characters.");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Phone is required");
            return;
        }

        if (!isPhoneVerified) {
            Toast.makeText(this, "Please verify your phone number first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            editAddress.setError("Address is required");
            return;
        }

        if (licenseFileUri == null) {
            Toast.makeText(this, "Please upload business license", Toast.LENGTH_SHORT).show();
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            editYearEstablished.setError("Invalid year format");
            return;
        }

        // Show progress indication
        btnSignup.setText("Creating account...");
        btnSignup.setEnabled(false);

        // Save license file to app's internal storage
        String licensePath = FileUtil.saveFileToInternalStorage(this, licenseFileUri, userId + "_license");

        // Create user object
        User user = new User(
                userId,
                businessName,
                year,
                gstNumber,
                phone,
                email,
                address,
                licensePath,
                referralCode,
                false,
                System.currentTimeMillis()
        );

        // Save user to database
        userRepository.insert(user, new UserRepository.OnDatabaseOperationCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this,
                            "Registration successful! Pending admin approval.",
                            Toast.LENGTH_LONG).show();

                    sessionManager.setRegistrationStatus(SessionManager.REGISTRATION_STATUS_PENDING);

                    // Navigate to pending approval screen
                    Intent intent = new Intent(SignupActivity.this, PendingApprovalActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}