package edu.marinValeria.appdummy.screens.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import edu.marinValeria.appdummy.data.model.Libro
import edu.marinValeria.appdummy.data.repository.LibrosRepository
import edu.marinValeria.appdummy.navegacion.Detalle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// R17: estados propios de la pantalla de detalle
sealed class DetalleUiState {
    data object Cargando : DetalleUiState()
    data class Exito(val libro: Libro) : DetalleUiState()
    data object NoEncontrado : DetalleUiState()
}

class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LibrosRepository
) : ViewModel() {

    // R17: el id no se pasa a mano, se saca de la ruta con SavedStateHandle
    private val libroId: Int = savedStateHandle.toRoute<Detalle>().id

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            // observarLibro devuelve un Flow, asi que si marco favorito
            // desde aqui la pantalla se actualiza sola
            repository.observarLibro(libroId).collect { libro ->
                _uiState.value =
                    if (libro != null) DetalleUiState.Exito(libro)
                    else DetalleUiState.NoEncontrado
            }
        }
    }

    fun toggleFavorito() {
        repository.alternarFavorito(libroId)
    }

    fun toggleLeido() {
        repository.alternarLeido(libroId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DetalleViewModel(
                    // createSavedStateHandle() da acceso a los argumentos de la ruta
                    savedStateHandle = createSavedStateHandle(),
                    repository = LibrosRepository.instancia
                )
            }
        }
    }
}