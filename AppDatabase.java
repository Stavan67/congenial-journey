package com.decor;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.decor.dao.InventoryDao;
import com.decor.dao.OrderDao;
import com.decor.dao.PriceDao;
import com.decor.dao.ProductDao;
import com.decor.dao.UserDao;
import com.decor.entity.Inventory;
import com.decor.entity.Order;
import com.decor.entity.Price;
import com.decor.entity.Product;
import com.decor.entity.User;

@Database(entities = {User.class, Product.class, Inventory.class, Price.class, Order.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "app_database";
    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract InventoryDao inventoryDao();
    public abstract PriceDao priceDao();
    public abstract OrderDao orderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
