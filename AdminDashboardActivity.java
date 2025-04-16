package com.decor.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.decor.R;
import com.decor.entity.User;
import com.decor.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPendingUsers;
    private UserRepository userRepository;
    private PendingUserAdapter pendingUserAdapter;
    private TextView txtNoPendingUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        userRepository = new UserRepository(getApplication());

        // Initialize views
        recyclerViewPendingUsers = findViewById(R.id.recycler_pending_users);
        txtNoPendingUsers = findViewById(R.id.txt_no_pending_users);

        // Set up recycler view
        recyclerViewPendingUsers.setLayoutManager(new LinearLayoutManager(this));
        pendingUserAdapter = new PendingUserAdapter(new ArrayList<>(), this::approveUser, this::rejectUser);
        recyclerViewPendingUsers.setAdapter(pendingUserAdapter);

        // Load pending users
        loadPendingUsers();
    }

    private void loadPendingUsers() {
        userRepository.getUsersByApprovalStatus(false, users -> {
            runOnUiThread(() -> {
                if (users != null && !users.isEmpty()) {
                    pendingUserAdapter.updateUsers(users);
                    txtNoPendingUsers.setVisibility(View.GONE);
                    recyclerViewPendingUsers.setVisibility(View.VISIBLE);
                } else {
                    txtNoPendingUsers.setVisibility(View.VISIBLE);
                    recyclerViewPendingUsers.setVisibility(View.GONE);
                }
            });
        });
    }

    private void approveUser(User user) {
        user.setApproved(true);
        userRepository.update(user, () -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "User approved successfully", Toast.LENGTH_SHORT).show();
                sendApprovalNotification(user);
                loadPendingUsers(); // Refresh the list
            });
        });
    }

    private void rejectUser(User user) {
        // Delete the user from the database
        userRepository.delete(user, () -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "User rejected", Toast.LENGTH_SHORT).show();
                sendRejectionNotification(user);
                loadPendingUsers(); // Refresh the list
            });
        });
    }

    private void sendApprovalNotification(User user) {
        // In a real app, you would send a push notification to the user's device
        // For this example, we'll just log it
        Log.d("AdminDashboard", "Approval notification sent to user: " + user.getEmail());

        // You could also send an email notification here using JavaMail or a third-party service
        // sendEmailNotification(user.getEmail(), "Account Approved",
        //     "Your account registration has been approved. You can now log in to the app.");
    }

    private void sendRejectionNotification(User user) {
        // In a real app, you would send a push notification to the user's device
        Log.d("AdminDashboard", "Rejection notification sent to user: " + user.getEmail());

        // You could also send an email notification here
        // sendEmailNotification(user.getEmail(), "Account Rejected",
        //     "We're sorry, but your account registration has been rejected. Please contact our support team for assistance.");
    }

    // Interface to handle approval
    public interface UserApprovalListener {
        void onApprove(User user);
    }

    public interface UserRejectionListener {
        void onReject(User user);
    }

    public interface LicenseViewListener {
        void onViewLicense(User user);
    }

    // Add PendingUserAdapter class as inner class (in real app, better as separate file)
    private static class PendingUserAdapter extends RecyclerView.Adapter<PendingUserAdapter.UserViewHolder> {
        private List<User> users;
        private UserRejectionListener rejectionListener;
        private UserApprovalListener approvalListener;
        private LicenseViewListener licenseViewListener;

        public PendingUserAdapter(List<User> users, UserApprovalListener approvalListener,
                                  UserRejectionListener rejectionListener) {
            this.users = users;
            this.approvalListener = approvalListener;
            this.rejectionListener = rejectionListener;
            this.licenseViewListener = null;
        }

        public void updateUsers(List<User> newUsers) {
            this.users = newUsers;
            notifyDataSetChanged();
        }
        public void setLicenseViewListener(LicenseViewListener listener) {
            this.licenseViewListener = listener;
        }

        @Override
        public UserViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pending_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        // In AdminDashboardActivity.java, modify the UserViewHolder class
        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView txtBusinessName;
            TextView txtYearEstablished;
            TextView txtGstNumber;
            TextView txtPhone;
            TextView txtEmail;
            TextView txtAddress;
            TextView txtReferralCode;
            Button btnApprove;
            Button btnReject;
            Button btnViewLicense;


            UserViewHolder(View itemView) {
                super(itemView);
                txtBusinessName = itemView.findViewById(R.id.txt_business_name);
                txtYearEstablished = itemView.findViewById(R.id.txt_year_established);
                txtGstNumber = itemView.findViewById(R.id.txt_gst_number);
                txtPhone = itemView.findViewById(R.id.txt_phone);
                txtEmail = itemView.findViewById(R.id.txt_email);
                txtAddress = itemView.findViewById(R.id.txt_address);
                txtReferralCode = itemView.findViewById(R.id.txt_referral_code);
                btnApprove = itemView.findViewById(R.id.btn_approve);
                btnReject = itemView.findViewById(R.id.btn_reject);
                btnViewLicense = itemView.findViewById(R.id.btn_view_license);
            }
        }

        // Update onBindViewHolder
        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.txtBusinessName.setText("Business: " + user.getBusinessName());
            holder.txtYearEstablished.setText("Est. Year: " + user.getYearEstablished());
            holder.txtGstNumber.setText("GST: " + user.getGstNumber());
            holder.txtPhone.setText("Phone: " + user.getPhone());
            holder.txtEmail.setText("Email: " + user.getEmail());
            holder.txtAddress.setText("Address: " + user.getAddress());

            String referral = user.getReferralCode();
            if (referral != null && !referral.isEmpty()) {
                holder.txtReferralCode.setText("Referral: " + referral);
                holder.txtReferralCode.setVisibility(View.VISIBLE);
            } else {
                holder.txtReferralCode.setVisibility(View.GONE);
            }

            holder.btnApprove.setOnClickListener(v -> {
                if (approvalListener != null) {
                    approvalListener.onApprove(user);
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                if (rejectionListener != null) {
                    rejectionListener.onReject(user);
                }
            });

            holder.btnViewLicense.setOnClickListener(v -> {
                if (licenseViewListener != null) {
                    licenseViewListener.onViewLicense(user);
                }
            });
        }
    }
}