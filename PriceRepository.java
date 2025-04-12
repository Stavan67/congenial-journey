package com.decor.repository;

import android.app.Application;

import com.decor.AppDatabase;
import com.decor.dao.PriceDao;
import com.decor.entity.Price;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PriceRepository {
    private PriceDao priceDao;
    private ExecutorService executorService;

    public PriceRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        priceDao = database.priceDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Price price, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            priceDao.insert(price);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void update(Price price, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            priceDao.update(price);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void getCurrentPriceForProduct(String productId, OnPriceLoadCallback callback) {
        executorService.execute(() -> {
            Price price = priceDao.getCurrentPriceForProduct(productId);
            if (callback != null) {
                callback.onPriceLoaded(price);
            }
        });
    }

    public void getPriceHistoryForProduct(String productId, OnPriceListLoadCallback callback) {
        executorService.execute(() -> {
            List<Price> priceHistory = priceDao.getPriceHistoryForProduct(productId);
            if (callback != null) {
                callback.onPriceListLoaded(priceHistory);
            }
        });
    }

    public void deactivateOldPrices(String productId, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            priceDao.deactivateOldPrices(productId);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    /**
     * Helper method to update price with proper deactivation of old prices
     */
    public void updateProductPrice(Price newPrice, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            // First deactivate all current prices for this product
            priceDao.deactivateOldPrices(newPrice.getProductId());
            // Then insert the new price (which should have isCurrentPrice=true)
            priceDao.insert(newPrice);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public interface OnDatabaseOperationCallback {
        void onSuccess();
    }

    public interface OnPriceLoadCallback {
        void onPriceLoaded(Price price);
    }

    public interface OnPriceListLoadCallback {
        void onPriceListLoaded(List<Price> prices);
    }
}