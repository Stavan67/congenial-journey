package com.decor.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import com.decor.Converters;

import java.util.List;
import java.util.Map;

@Entity(tableName = "orders")
@TypeConverters(Converters.class)
public class Order {
    @PrimaryKey
    @NonNull
    private String orderId;
    private String dealerId;
    private long orderDate;
    private double totalAmount;
    private String status;
    private List<Map<String, Object>> items;
    private String shippingAddress;

    // Constructor
    public Order(@NonNull String orderId, String dealerId, long orderDate,
                 double totalAmount, String status, List<Map<String, Object>> items,
                 String shippingAddress) {
        this.orderId = orderId;
        this.dealerId = dealerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    @NonNull
    public String getOrderId() { return orderId; }
    public void setOrderId(@NonNull String orderId) { this.orderId = orderId; }

    public String getDealerId() { return dealerId; }
    public void setDealerId(String dealerId) { this.dealerId = dealerId; }

    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
