package com.decor.repository;

import android.app.Application;
import android.os.AsyncTask;
import com.decor.AppDatabase;
import com.decor.dao.UserDao;
import com.decor.entity.User;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(User user, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            userDao.insert(user);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void update(User user, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            userDao.update(user);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void delete(User user, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            userDao.delete(user);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void getUserById(String userId, OnUserLoadCallback callback) {
        executorService.execute(() -> {
            User user = userDao.getUserById(userId);
            if (callback != null) {
                callback.onUserLoaded(user);
            }
        });
    }

    public void getAllUsers(OnUsersLoadCallback callback) {
        executorService.execute(() -> {
            List<User> users = userDao.getAllUsers();
            if (callback != null) {
                callback.onUsersLoaded(users);
            }
        });
    }

    public interface OnDatabaseOperationCallback {
        void onSuccess();
    }

    // Add this method to UserRepository.java
    public void getUsersByApprovalStatus(boolean approvalStatus, OnUsersLoadCallback callback) {
        executorService.execute(() -> {
            List<User> users = userDao.getUsersByApprovalStatus(approvalStatus);
            if (callback != null) {
                callback.onUsersLoaded(users);
            }
        });
    }

    public interface OnUserLoadCallback {
        void onUserLoaded(User user);
    }

    public interface OnUsersLoadCallback {
        void onUsersLoaded(List<User> users);
    }
}
