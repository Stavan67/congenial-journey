package com.decor.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.decor.R;

public class OtpVerificationDialog extends DialogFragment {

    private EditText editOtp;
    private TextView txtResend;
    private TextView txtTimer;
    private String mockOtp;
    private OtpVerificationListener listener;
    private CountDownTimer resendTimer;

    public interface OtpVerificationListener {
        void onOtpVerified(boolean isSuccess);
        void onResendOtp();
    }

    public void setOtpVerificationListener(OtpVerificationListener listener) {
        this.listener = listener;
    }

    public void setMockOtp(String mockOtp) {
        this.mockOtp = mockOtp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp_verification, null);

        editOtp = view.findViewById(R.id.edit_otp);
        Button btnVerify = view.findViewById(R.id.btn_verify_otp);
        txtResend = view.findViewById(R.id.txt_resend_otp);

        // Find the timer TextView - you may need to add this to your layout
        txtTimer = view.findViewById(R.id.txt_timer);
        if (txtTimer == null) {
            // If timer is not in your layout, you can just use the resend TextView
            txtTimer = txtResend;
        }

        btnVerify.setOnClickListener(v -> {
            String otp = editOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                editOtp.setError("OTP is required");
                return;
            }

            // Verify OTP using direct comparison with mock OTP
            if (listener != null) {
                boolean isSuccess = otp.equals(mockOtp);
                listener.onOtpVerified(isSuccess);
                if (isSuccess) {
                    dismiss();
                } else {
                    editOtp.setError("Invalid OTP");
                    editOtp.requestFocus();
                }
            }
        });

        txtResend.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResendOtp();
                startResendTimer();
            }
        });

        // Start the resend timer when the dialog is shown
        startResendTimer();

        builder.setView(view)
                .setTitle("Verify Phone Number")
                .setMessage("A verification code has been sent to your phone. Please enter it below.");

        return builder.create();
    }

    private void startResendTimer() {
        // Disable resend button during countdown
        txtResend.setEnabled(false);

        // Start 60-second countdown timer
        if (resendTimer != null) {
            resendTimer.cancel();
        }

        resendTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Resend OTP in " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                txtResend.setEnabled(true);
                txtResend.setText("Resend OTP");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resendTimer != null) {
            resendTimer.cancel();
            resendTimer = null;
        }
    }
}
