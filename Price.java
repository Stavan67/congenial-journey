package com.decor.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "prices")
public class Price {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String productId;
    private double basePrice;
    private double dealerPrice;
    private long effectiveFrom;
    private boolean isCurrentPrice;

    // Constructor
    public Price(@NonNull String productId, double basePrice, double dealerPrice,
                 long effectiveFrom, boolean isCurrentPrice) {
        this.productId = productId;
        this.basePrice = basePrice;
        this.dealerPrice = dealerPrice;
        this.effectiveFrom = effectiveFrom;
        this.isCurrentPrice = isCurrentPrice;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getProductId() { return productId; }
    public void setProductId(@NonNull String productId) { this.productId = productId; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getDealerPrice() { return dealerPrice; }
    public void setDealerPrice(double dealerPrice) { this.dealerPrice = dealerPrice; }

    public long getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(long effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public boolean isCurrentPrice() { return isCurrentPrice; }
    public void setCurrentPrice(boolean currentPrice) { isCurrentPrice = currentPrice; }
}
