package com.example.trabalho2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Restaurante::class, Categoria::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun restauranteDao(): RestauranteDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "paladar_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
