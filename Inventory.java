package com.decor.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "inventory")
public class Inventory {
    @PrimaryKey
    @NonNull
    private String productId;
    private int currentStock;
    private int minStockLevel;
    private long lastRestockDate;

    // Constructor
    public Inventory(@NonNull String productId, int currentStock, int minStockLevel, long lastRestockDate) {
        this.productId = productId;
        this.currentStock = currentStock;
        this.minStockLevel = minStockLevel;
        this.lastRestockDate = lastRestockDate;
    }

    // Getters and Setters
    @NonNull
    public String getProductId() { return productId; }
    public void setProductId(@NonNull String productId) { this.productId = productId; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public long getLastRestockDate() { return lastRestockDate; }
    public void setLastRestockDate(long lastRestockDate) { this.lastRestockDate = lastRestockDate; }
}
