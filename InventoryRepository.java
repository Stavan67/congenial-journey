package com.decor.repository;

import android.app.Application;

import com.decor.AppDatabase;
import com.decor.dao.InventoryDao;
import com.decor.entity.Inventory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryRepository {
    private InventoryDao inventoryDao;
    private ExecutorService executorService;

    public InventoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        inventoryDao = database.inventoryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Inventory inventory, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            inventoryDao.insert(inventory);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void update(Inventory inventory, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            inventoryDao.update(inventory);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void getInventoryForProduct(String productId, OnInventoryLoadCallback callback) {
        executorService.execute(() -> {
            Inventory inventory = inventoryDao.getInventoryForProduct(productId);
            if (callback != null) {
                callback.onInventoryLoaded(inventory);
            }
        });
    }

    public void getAllInventory(OnInventoryListLoadCallback callback) {
        executorService.execute(() -> {
            List<Inventory> inventoryList = inventoryDao.getAllInventory();
            if (callback != null) {
                callback.onInventoryListLoaded(inventoryList);
            }
        });
    }

    public void getLowStockItems(OnInventoryListLoadCallback callback) {
        executorService.execute(() -> {
            List<Inventory> lowStockItems = inventoryDao.getLowStockItems();
            if (callback != null) {
                callback.onInventoryListLoaded(lowStockItems);
            }
        });
    }

    public void restockProduct(String productId, int quantity, long timestamp, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            inventoryDao.restockProduct(productId, quantity, timestamp);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public interface OnDatabaseOperationCallback {
        void onSuccess();
    }

    public interface OnInventoryLoadCallback {
        void onInventoryLoaded(Inventory inventory);
    }

    public interface OnInventoryListLoadCallback {
        void onInventoryListLoaded(List<Inventory> inventoryList);
    }
}