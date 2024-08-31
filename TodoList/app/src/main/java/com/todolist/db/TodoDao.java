package com.todolist.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface TodoDao {

    // get All
    @Query("SELECT * FROM todoentity")
    fun getAllTodo() : List<TodoEntity>

    // insert todo
    @Insert
    fun insert(todo : TodoEntity)

    // delete todo
    @Delete
    fun delete(todo : TodoEntity)
}
