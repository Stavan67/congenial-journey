package com.decor.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.decor.R;
import com.decor.entity.User;
import com.decor.repository.UserRepository;
import com.decor.util.FileUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_FILE_PICK = 101;

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
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signup);

        userRepository = new UserRepository(getApplication());

        // Initialize UI components
        editBusinessName = findViewById(R.id.edit_business_name);
        editYearEstablished = findViewById(R.id.edit_year_established);
        editGstNumber = findViewById(R.id.edit_gst_number);
        editPhone = findViewById(R.id.edit_phone);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            pickFile();
        }
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

    private void verifyPhoneNumber() {
        String phoneNumber = editPhone.getText().toString().trim();
        if (!phoneNumber.startsWith("+")) { // Enforce country code
            editPhone.setError("Include country code (e.g. +91...)");
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto-verify without SMS (e.g. on same device)
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String vId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = vId;
                        resendToken = token;
                        showOtpDialog(); // Show OTP input dialog
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showOtpDialog() {
        OtpVerificationDialog dialog = new OtpVerificationDialog();
        dialog.setVerificationId(verificationId);
        dialog.setOtpVerificationListener(new OtpVerificationDialog.OtpVerificationListener() {
            @Override
            public void onOtpVerified(String verificationId, String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithCredential(credential);
            }

            @Override
            public void onResendOtp() {
                resendVerificationCode(editPhone.getText().toString().trim());
            }
        });
        dialog.show(getSupportFragmentManager(), "OTP_DIALOG");
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setForceResendingToken(resendToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SignupActivity.this, "Resend failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = newVerificationId;
                        resendToken = token;
                        Toast.makeText(SignupActivity.this, "New OTP sent!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Phone verified! Now save user data to your Room DB
                        isPhoneVerified = true;
                        btnVerifyPhone.setText("Verified ✓");
                        btnVerifyPhone.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
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

        int year = Integer.parseInt(yearStr);

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

                    // Navigate to pending approval screen or login
                    Intent intent = new Intent(SignupActivity.this, PendingApprovalActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}