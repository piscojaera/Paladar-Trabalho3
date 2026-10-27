package com.example.trabalho2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RestauranteDao {
    @Insert
    suspend fun inserir(restaurante: Restaurante)

    @Query("SELECT * FROM restaurantes")
    suspend fun buscarTodos(): List<Restaurante>

    @Query("SELECT * FROM restaurantes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Restaurante?

    @Query("SELECT * FROM restaurantes WHERE categoriaId = :categoriaId")
    suspend fun buscarPorCategoria(categoriaId: Int): List<Restaurante>

    @Query("SELECT * FROM restaurantes WHERE favorito = 1")
    suspend fun buscarFavoritos(): List<Restaurante>

    @Update
    suspend fun atualizar(restaurante: Restaurante)

    @Delete
    suspend fun deletar(restaurante: Restaurante)
}

@Dao
interface CategoriaDao {
    @Insert
    suspend fun inserir(categoria: Categoria)

    @Query("SELECT * FROM categorias")
    suspend fun buscarTodos(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Categoria?

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun deletar(categoria: Categoria)
}