package com.decor.repository;

import android.app.Application;

import com.decor.AppDatabase;
import com.decor.dao.ProductDao;
import com.decor.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
    private ProductDao productDao;
    private ExecutorService executorService;

    public ProductRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        productDao = database.productDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Product product, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            productDao.insert(product);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void update(Product product, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            productDao.update(product);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void delete(Product product, OnDatabaseOperationCallback callback) {
        executorService.execute(() -> {
            productDao.delete(product);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void getProductById(String productId, OnProductLoadCallback callback) {
        executorService.execute(() -> {
            Product product = productDao.getProductById(productId);
            if (callback != null) {
                callback.onProductLoaded(product);
            }
        });
    }

    public void getAllProducts(OnProductListLoadCallback callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.getAllProducts();
            if (callback != null) {
                callback.onProductListLoaded(products);
            }
        });
    }

    public void getProductsByCategory(String category, OnProductListLoadCallback callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.getProductsByCategory(category);
            if (callback != null) {
                callback.onProductListLoaded(products);
            }
        });
    }

    public void searchProducts(String searchQuery, OnProductListLoadCallback callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.searchProducts(searchQuery);
            if (callback != null) {
                callback.onProductListLoaded(products);
            }
        });
    }

    public interface OnDatabaseOperationCallback {
        void onSuccess();
    }

    public interface OnProductLoadCallback {
        void onProductLoaded(Product product);
    }

    public interface OnProductListLoadCallback {
        void onProductListLoaded(List<Product> products);
    }
}