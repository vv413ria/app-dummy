package edu.marinValeria.appdummy.data.repository

import edu.marinValeria.appdummy.data.model.Libro
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class LibrosRepository {

    // La lista vive aquí: única fuente de verdad para listado, detalle y favoritos
    private val _libros = MutableStateFlow(
        listOf(
            Libro(
                id = 1,
                titulo = "Proyecto Hail Mary",
                autor = "Andy Weir",
                year = 2021,
                isbn = "9788418037016",
                cover = "https://covers.openlibrary.org/b/isbn/9788418037016-L.jpg",
                esFavorito = true,
                leido = false
            ),
            Libro(
                id = 2,
                titulo = "Juego de tronos",
                autor = "George R.R. Martin",
                year = 1996,
                isbn = "9780307951182",
                cover = "https://covers.openlibrary.org/b/isbn/9780307951182-L.jpg",
                esFavorito = true,
                leido = true
            ),
            Libro(
                id = 3,
                titulo = "Festín de cuervos",
                autor = "George R.R. Martin",
                year = 2005,
                isbn = "9780307951212",
                esFavorito = false,
                leido = false
            ),
            Libro(
                id = 4,
                titulo = "Cementerio de Animales",
                autor = "Stephen King",
                year = 1983,
                isbn = "9788401499845",
                esFavorito = false,
                leido = true
            ),
            Libro(
                id = 5,
                titulo = "El juego de Ender",
                autor = "Orson Scott Card",
                year = 1985,
                isbn = "9788498720068",
                esFavorito = false,
                leido = true
            )
        )
    )
    val libros: StateFlow<List<Libro>> = _libros.asStateFlow()

    private var cargado = false

    // Simula la carga (solo la primera vez, para no volver a "Cargando")
    suspend fun cargarLibros() {
        if (!cargado) {
            delay(1000)
            cargado = true
        }
    }

    // Un solo libro, para la pantalla de detalle
    fun observarLibro(id: Int): Flow<Libro?> =
        _libros.map { lista -> lista.firstOrNull { it.id == id } }

    // Devuelven el libro ya cambiado (o null si no existe)
    fun alternarFavorito(id: Int): Libro? = actualizar(id) { it.copy(esFavorito = !it.esFavorito) }

    fun alternarLeido(id: Int): Libro? = actualizar(id) { it.copy(leido = !it.leido) }

    private fun actualizar(id: Int, cambio: (Libro) -> Libro): Libro? {
        var resultado: Libro? = null
        _libros.update { lista ->
            lista.map { libro ->
                if (libro.id == id) cambio(libro).also { resultado = it } else libro
            }
        }
        return resultado
    }

    companion object {
        // Una única instancia compartida (sin Hilt ni Koin)
        val instancia: LibrosRepository by lazy { LibrosRepository() }
    }
}