package edu.marinValeria.appdummy.screens.listado

import edu.marinValeria.appdummy.data.model.Libro

sealed class LibrosUiState {
    data object Cargando : LibrosUiState()
    data class Exito(val libros: List<Libro>) : LibrosUiState()
    data class Error(val mensaje: String) : LibrosUiState()
}

// Eventos de un solo disparo (SharedFlow)
sealed class LibrosEvento {
    data class MostrarSnackbar(val mensaje: String) : LibrosEvento()
}