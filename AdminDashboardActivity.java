package com.decor.admin;

import android.os.Bundle;
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
        pendingUserAdapter = new PendingUserAdapter(new ArrayList<>(), this::approveUser);
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
                loadPendingUsers(); // Refresh the list
            });
        });
    }

    // Interface to handle approval
    public interface UserApprovalListener {
        void onApprove(User user);
    }

    // Add PendingUserAdapter class as inner class (in real app, better as separate file)
    private static class PendingUserAdapter extends RecyclerView.Adapter<PendingUserAdapter.UserViewHolder> {
        private List<User> users;
        private UserApprovalListener listener;

        public PendingUserAdapter(List<User> users, UserApprovalListener listener) {
            this.users = users;
            this.listener = listener;
        }

        public void updateUsers(List<User> newUsers) {
            this.users = newUsers;
            notifyDataSetChanged();
        }

        @Override
        public UserViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pending_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.txtBusinessName.setText(user.getBusinessName());
            holder.txtPhone.setText("Phone: " + user.getPhone());
            holder.txtEmail.setText("Email: " + user.getEmail());
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(user);
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView txtBusinessName;
            TextView txtPhone;
            TextView txtEmail;
            Button btnApprove;

            UserViewHolder(View itemView) {
                super(itemView);
                txtBusinessName = itemView.findViewById(R.id.txt_business_name);
                txtPhone = itemView.findViewById(R.id.txt_phone);
                txtEmail = itemView.findViewById(R.id.txt_email);
                btnApprove = itemView.findViewById(R.id.btn_approve);
            }
        }
    }
}
