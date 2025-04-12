package com.decor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.decor.R;

public class PendingApprovalActivity extends AppCompatActivity {

    private Button btnContactSupport;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_approval);

        TextView txtMessage = findViewById(R.id.txt_pending_message);
        btnContactSupport = findViewById(R.id.btn_contact_support);
        btnExit = findViewById(R.id.btn_exit);

        txtMessage.setText("Your registration is being reviewed by our administrators. " +
                "You will be notified once your account is approved.");

        btnContactSupport.setOnClickListener(v -> {
            // Launch email intent
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@yourcompany.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Account Approval Inquiry");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI recently signed up for your app and am waiting for approval. Could you please check my application status?\n\nThank you.");

            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (android.content.ActivityNotFoundException ex) {
                // No email client
            }
        });

        btnExit.setOnClickListener(v -> finishAffinity());
    }
}
