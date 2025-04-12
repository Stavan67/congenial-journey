package com.decor.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.decor.entity.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE productId = :productId")
    Product getProductById(String productId);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE category = :category")
    List<Product> getProductsByCategory(String category);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%'")
    List<Product> searchProducts(String searchQuery);
}