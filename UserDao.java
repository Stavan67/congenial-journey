package com.decor.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.decor.entity.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(String userId);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE isApproved = :isApproved")
    List<User> getUsersByApprovalStatus(boolean isApproved);

    @Query("SELECT * FROM users WHERE referralCode = :referralCode")
    List<User> getUsersByReferralCode(String referralCode);


}

