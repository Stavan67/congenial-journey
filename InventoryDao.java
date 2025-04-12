package com.decor.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.decor.entity.Inventory;
import java.util.List;

@Dao
public interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Inventory inventory);

    @Update
    void update(Inventory inventory);

    @Query("SELECT * FROM inventory WHERE productId = :productId")
    Inventory getInventoryForProduct(String productId);

    @Query("SELECT * FROM inventory")
    List<Inventory> getAllInventory();

    @Query("SELECT * FROM inventory WHERE currentStock < minStockLevel")
    List<Inventory> getLowStockItems();

    @Query("UPDATE inventory SET currentStock = currentStock + :quantity, lastRestockDate = :timestamp WHERE productId = :productId")
    void restockProduct(String productId, int quantity, long timestamp);
}