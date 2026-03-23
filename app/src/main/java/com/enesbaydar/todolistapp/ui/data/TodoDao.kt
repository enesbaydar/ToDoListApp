package com.enesbaydar.todolistapp.ui.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {

        @Query("SELECT * FROM todo_table")
        fun getAllTodos(): Flow<List<TodoEntity>>

        @Insert
        suspend fun insertTodo(todo: TodoEntity): Long

        @Update
        suspend fun updateTodo(todo: TodoEntity): Int

        @Query("DELETE FROM todo_table WHERE id = :id")
        suspend fun deleteTodo(id: Int): Int

    }
