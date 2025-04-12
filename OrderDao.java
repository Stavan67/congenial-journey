package com.decor.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.decor.entity.Order;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    Order getOrderById(String orderId);

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();

    @Query("SELECT * FROM orders WHERE dealerId = :dealerId")
    List<Order> getOrdersByDealer(String dealerId);

    @Query("SELECT * FROM orders WHERE status = :status")
    List<Order> getOrdersByStatus(String status);

    @Query("SELECT * FROM orders WHERE orderDate BETWEEN :startDate AND :endDate")
    List<Order> getOrdersByDateRange(long startDate, long endDate);

    @Query("UPDATE orders SET status = :newStatus WHERE orderId = :orderId")
    void updateOrderStatus(String orderId, String newStatus);
}
