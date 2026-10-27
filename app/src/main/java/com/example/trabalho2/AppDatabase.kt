package com.example.trabalho2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

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
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            db.execSQL("INSERT INTO categorias (nome, descricao) VALUES ('Italiano', 'Restaurantes especializados em culinária italiana.')")
                            db.execSQL("INSERT INTO categorias (nome, descricao) VALUES ('Pizza', 'Restaurantes especializados em pizzas.')")
                            db.execSQL("INSERT INTO categorias (nome, descricao) VALUES ('Romântico', 'Restaurantes indicados para encontros e ocasiões especiais.')")

                            db.execSQL("INSERT INTO restaurantes (nome, nota, preco, categoriaId, descricao, favorito) VALUES ('Carlo Ristorante', 4.6, '\$\$\$', 1, 'Restaurante italiano com ambiente sofisticado.', 0)")
                            db.execSQL("INSERT INTO restaurantes (nome, nota, preco, categoriaId, descricao, favorito) VALUES ('Mada Pizza & Vinho', 4.4, '\$\$\$', 2, 'Pizzaria com opções de pizzas e vinhos.', 0)")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}