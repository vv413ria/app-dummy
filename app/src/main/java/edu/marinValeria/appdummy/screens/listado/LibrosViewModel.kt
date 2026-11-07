package edu.marinValeria.appdummy.screens.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.marinValeria.appdummy.data.model.Libro
import edu.marinValeria.appdummy.data.repository.LibrosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibrosViewModel(
    private val repository: LibrosRepository
) : ViewModel() {

    // Backing property: privado y modificable / público de solo lectura
    private val _uiState = MutableStateFlow<LibrosUiState>(LibrosUiState.Cargando)
    val uiState: StateFlow<LibrosUiState> = _uiState.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _autorSeleccionado = MutableStateFlow(TODOS)
    val autorSeleccionado: StateFlow<String> = _autorSeleccionado.asStateFlow()

    // Lista de autores para los chips del filtro
    val autores: StateFlow<List<String>> = repository.libros
        .map { lista -> listOf(TODOS) + lista.map { it.autor }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(TODOS))

    // Eventos de un solo disparo: replay = 0, no se repiten al rotar
    private val _eventos = MutableSharedFlow<LibrosEvento>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventos: SharedFlow<LibrosEvento> = _eventos.asSharedFlow()

    private var trabajoCarga: Job? = null

    init {
        cargarLibros()
    }

    fun cargarLibros() {
        trabajoCarga?.cancel()
        trabajoCarga = viewModelScope.launch {
            _uiState.value = LibrosUiState.Cargando
            try {
                repository.cargarLibros()
                combine(repository.libros, _busqueda, _autorSeleccionado) { libros, texto, autor ->
                    LibrosUiState.Exito(libros = filtrar(libros, texto, autor))
                }.collect { _uiState.value = it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = LibrosUiState.Error(e.message ?: "No se pudieron cargar los libros")
            }
        }
    }

    private fun filtrar(libros: List<Libro>, texto: String, autor: String): List<Libro> =
        libros.filter { libro ->
            val coincideTexto = texto.isBlank() || libro.titulo.contains(texto, ignoreCase = true)
            val coincideAutor = autor == TODOS || libro.autor == autor
            coincideTexto && coincideAutor
        }

    // La pantalla solo llama a estas funciones
    fun actualizarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun seleccionarAutor(autor: String) {
        _autorSeleccionado.value = autor
    }

    fun toggleFavorito(id: Int) {
        val libro = repository.alternarFavorito(id) ?: return
        val mensaje = if (libro.esFavorito) "\"${libro.titulo}\" añadido a favoritos"
        else "\"${libro.titulo}\" eliminado de favoritos"
        viewModelScope.launch { _eventos.emit(LibrosEvento.MostrarSnackbar(mensaje)) }
    }

    fun toggleLeido(id: Int) {
        val libro = repository.alternarLeido(id) ?: return
        val mensaje = if (libro.leido) "\"${libro.titulo}\" marcado como leído"
        else "\"${libro.titulo}\" marcado como no leído"
        viewModelScope.launch { _eventos.emit(LibrosEvento.MostrarSnackbar(mensaje)) }
    }

    companion object {
        const val TODOS = "Todos"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { LibrosViewModel(LibrosRepository.instancia) }
        }
    }
}