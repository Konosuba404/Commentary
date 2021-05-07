package com.example.commentary.sqlRoom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT password FROM User WHERE username = :input_username")
    String loadPasswordWhereUsername(String input_username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User ... users);
}
