package com.decor.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.decor.R;

public class OtpVerificationDialog extends DialogFragment {

    private EditText editOtp;
    private String verificationId;
    private OtpVerificationListener listener;

    public interface OtpVerificationListener {
        void onOtpVerified(String verificationId, String otp);
        void onResendOtp();
    }

    public void setOtpVerificationListener(OtpVerificationListener listener) {
        this.listener = listener;
    }
    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp_verification, null);

        editOtp = view.findViewById(R.id.edit_otp);
        Button btnVerify = view.findViewById(R.id.btn_verify_otp);
        TextView txtResend = view.findViewById(R.id.txt_resend_otp);

        btnVerify.setOnClickListener(v -> {
            String otp = editOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                editOtp.setError("OTP is required");
                return;
            }

            if (listener != null && verificationId != null) {
                // Pass both verificationId and otp
                listener.onOtpVerified(verificationId, otp);
                dismiss();
            }
        });

        txtResend.setOnClickListener(v -> {
            // In a real app, this would trigger resending the OTP
            Toast.makeText(getContext(), "OTP resent!", Toast.LENGTH_SHORT).show();
        });

        builder.setView(view)
                .setTitle("Verify Phone Number")
                .setMessage("A verification code has been sent to your phone. Please enter it below.");

        return builder.create();
    }
}
