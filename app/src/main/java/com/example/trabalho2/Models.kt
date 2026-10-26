package com.example.trabalho2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurantes")
data class Restaurante(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val nota: Double,
    val preco: String,
    val categoriaId: Int,
    val descricao: String,
    val favorito: Boolean = false
)

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String
)