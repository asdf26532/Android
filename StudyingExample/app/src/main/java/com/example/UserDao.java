package com.example;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void setInsertUser(User user);

    @Update
    void setUpdateUser(User user);

    @Delete
    void setDeleteUser(User user);

    // 조회
    @Query("select * from User")
    List<User> getUserAll();



}
