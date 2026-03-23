package com.enesbaydar.todolistapp.ui.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java






    @Database(entities = [TodoEntity::class], version = 1)
    abstract class TodoDatabase : RoomDatabase() {
        abstract fun todoDao(): TodoDao

        companion object {
            @Volatile
            private var INSTANCE: TodoDatabase? = null
            fun getDatabase(context: Context): TodoDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDatabase::class.java,
                        "todo_database"
                    ).build()
                    INSTANCE = instance
                    instance
                }

            }
        }
    }


