package com.decor.repository;

import android.app.Application;

import com.decor.AppDatabase;
import com.decor.dao.OrderDao;
import com.decor.entity.Order;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private OrderDao orderDao;
    private ExecutorService executorService;

    public OrderRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        orderDao = database.orderDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Order order, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            orderDao.insert(order);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void update(Order order, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            orderDao.update(order);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void getOrderById(String orderId, OnOrderLoadCallback callback) {
        executorService.execute(() -> {
            Order order = orderDao.getOrderById(orderId);
            if (callback != null) {
                callback.onOrderLoaded(order);
            }
        });
    }

    public void getAllOrders(OnOrderListLoadCallback callback) {
        executorService.execute(() -> {
            List<Order> orders = orderDao.getAllOrders();
            if (callback != null) {
                callback.onOrderListLoaded(orders);
            }
        });
    }

    public void getOrdersByDealer(String dealerId, OnOrderListLoadCallback callback) {
        executorService.execute(() -> {
            List<Order> orders = orderDao.getOrdersByDealer(dealerId);
            if (callback != null) {
                callback.onOrderListLoaded(orders);
            }
        });
    }

    public void getOrdersByStatus(String status, OnOrderListLoadCallback callback) {
        executorService.execute(() -> {
            List<Order> orders = orderDao.getOrdersByStatus(status);
            if (callback != null) {
                callback.onOrderListLoaded(orders);
            }
        });
    }

    public void getOrdersByDateRange(long startDate, long endDate, OnOrderListLoadCallback callback) {
        executorService.execute(() -> {
            List<Order> orders = orderDao.getOrdersByDateRange(startDate, endDate);
            if (callback != null) {
                callback.onOrderListLoaded(orders);
            }
        });
    }

    public void updateOrderStatus(String orderId, String newStatus, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            orderDao.updateOrderStatus(orderId, newStatus);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public interface OnDatabaseOperationCallback {
        void onSuccess();
    }

    public interface OnOrderLoadCallback {
        void onOrderLoaded(Order order);
    }

    public interface OnOrderListLoadCallback {
        void onOrderListLoaded(List<Order> orders);
    }
}