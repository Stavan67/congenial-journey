package com.decor.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import com.decor.Converters;

import java.util.Map;

@Entity(tableName = "products")
@TypeConverters(Converters.class)
public class Product {
    @PrimaryKey
    @NonNull
    private String productId;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private Map<String, String> specifications;

    // Constructor
    public Product(@NonNull String productId, String name, String category,
                   String description, String imageUrl, Map<String, String> specifications) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.specifications = specifications;
    }

    // Getters and Setters
    @NonNull
    public String getProductId() { return productId; }
    public void setProductId(@NonNull String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Map<String, String> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, String> specifications) { this.specifications = specifications; }
}

