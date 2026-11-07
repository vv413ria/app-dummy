package edu.marinValeria.appdummy.screens.favoritos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.marinValeria.appdummy.data.model.Libro
import edu.marinValeria.appdummy.data.repository.LibrosRepository
import edu.marinValeria.appdummy.screens.listado.ItemLibro
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Usa la misma instancia del repositorio que el listado, por eso un favorito
// que marco en una pantalla aparece al momento en la otra
class FavoritosViewModel(private val repository: LibrosRepository) : ViewModel() {

    val favoritos: StateFlow<List<Libro>> = repository.libros
        .map { lista -> lista.filter { it.esFavorito } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFavorito(id: Int) {
        repository.alternarFavorito(id)
    }

    fun toggleLeido(id: Int) {
        repository.alternarLeido(id)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoritosViewModel(LibrosRepository.instancia) }
        }
    }
}

// R18: solo los libros marcados como favoritos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritos(
    onNavegaADetalle: (Int) -> Unit = {},
    viewModel: FavoritosViewModel = viewModel(factory = FavoritosViewModel.Factory)
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favoritos") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (favoritos.isEmpty()) {
                // R18: estado vacio cuando no hay ningun favorito
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Todavia no tienes libros favoritos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoritos, key = { it.id }) { libro ->
                        ItemLibro(
                            libro = libro,
                            onClick = onNavegaADetalle,
                            onToggleLeido = { viewModel.toggleLeido(it) },
                            onToggleFavorito = { viewModel.toggleFavorito(it) }
                        )
                    }
                }
            }
        }
    }
}