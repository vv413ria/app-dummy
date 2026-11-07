package edu.marinValeria.appdummy.screens.listado

import edu.marinValeria.appdummy.data.model.Libro

sealed class LibrosUiState {
    data object Cargando : LibrosUiState()
    data class Exito(val libros: List<Libro>) : LibrosUiState()
    data class Error(val mensaje: String) : LibrosUiState()
}

//R8: Eventos de un solo disparo: se muestran una vez y no se repiten al rotar, emitida por shareflow para mostrar snackbar
sealed class LibrosEvento {
    data class MostrarSnackbar(val mensaje: String) : LibrosEvento()
}