package edu.marinValeria.appdummy.data.model

data class Libro(
    val id: Int = 0,
    val titulo: String = "",
    val autor: String = "",
    val year: Int? = null,
    val isbn: String = "",
    val cover: String = "",
    val esFavorito: Boolean = false,
    val leido: Boolean = false
)