package com.enesbaydar.todolistapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.enesbaydar.todolistapp.ui.data.TodoDao
import com.enesbaydar.todolistapp.ui.data.TodoDatabase
import com.enesbaydar.todolistapp.ui.data.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TodoDatabase::class.java).build()
        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() = runBlocking {
        val todo = TodoEntity(title = "Test Todo", isCompleted = false, description = "test")
        todoDao.insertTodo(todo)
        val allTodos = todoDao.getAllTodos().first()
        assertEquals(1, allTodos.size)
        assertEquals("Test Todo", allTodos[0].title)
    }

    @Test
    @Throws(Exception::class)
    fun updateTodoAndGet() = runBlocking {
        val todo = TodoEntity(title = "Initial Title", isCompleted = false, description = "test")
        todoDao.insertTodo(todo)

        val insertedTodo = todoDao.getAllTodos().first().first()

        val updatedTodo = insertedTodo.copy(title = "Updated Title", isCompleted = true)
        todoDao.updateTodo(updatedTodo)

        val retrievedTodo = todoDao.getAllTodos().first().first()
        assertEquals(insertedTodo.id, retrievedTodo.id)
        assertEquals("Updated Title", retrievedTodo.title)
        assertEquals(true, retrievedTodo.isCompleted)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTodoAndCheck() = runBlocking {
        val todo = TodoEntity(title = "Todo to delete", isCompleted = false, description = "test")
        todoDao.insertTodo(todo)

        val insertedTodo = todoDao.getAllTodos().first().first()
        todoDao.deleteTodo(insertedTodo.id)

        val allTodos = todoDao.getAllTodos().first()
        assertTrue(allTodos.isEmpty())
    }
}
