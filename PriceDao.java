package com.decor.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.decor.entity.Price;
import java.util.List;

@Dao
public interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Price price);

    @Update
    void update(Price price);

    @Query("SELECT * FROM prices WHERE productId = :productId AND isCurrentPrice = 1")
    Price getCurrentPriceForProduct(String productId);

    @Query("SELECT * FROM prices WHERE productId = :productId ORDER BY effectiveFrom DESC")
    List<Price> getPriceHistoryForProduct(String productId);

    @Query("UPDATE prices SET isCurrentPrice = 0 WHERE productId = :productId")
    void deactivateOldPrices(String productId);
}

