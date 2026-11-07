package edu.marinValeria.appdummy.screens.listado

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import edu.marinValeria.appdummy.R
import edu.marinValeria.appdummy.data.model.Libro
import androidx.compose.runtime.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado(
    onNavegaADetalle: (Int) -> Unit = {},
    viewModel: LibrosViewModel = viewModel(factory = LibrosViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val autorSeleccionado by viewModel.autorSeleccionado.collectAsStateWithLifecycle()
    val autores by viewModel.autores.collectAsStateWithLifecycle()
    //R8
    val snackbarHostState = remember { SnackbarHostState() }


    // R8 escucha los eventos del VM una sola vez
    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LibrosEvento.MostrarSnackbar -> snackbarHostState.showSnackbar(evento.mensaje)
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("AppDummy") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // R6: 3 estados de uiState
            when (val estado = uiState) {

                LibrosUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is LibrosUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(estado.mensaje, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarLibros() }) { Text("Reintentar") }
                    }
                }

                is LibrosUiState.Exito -> {
                    Column {
                        //R7: la pantalla solo avisará al ViewModel del texto nuevo
                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = { viewModel.actualizarBusqueda(it) }, //avisa al ViewModel de lo que se escribe, no lo gaurda la pantalla
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar libros...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                AnimatedVisibility(visible = busqueda.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.actualizarBusqueda("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                                    }
                                }
                            }, singleLine = true)  // el buscador ocupa una sola linea y no crece al escribir

                        // R7: filtro por autor
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(autores) { autor ->
                                FilterChip(
                                    selected = autor == autorSeleccionado,
                                    onClick = { viewModel.seleccionarAutor(autor) },
                                    label = { Text(autor) }
                                )
                            }
                        }
                        if (estado.libros.isEmpty()) { //si esta vacio se muestra un icono y pinta un aviso
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Sin resultados para \"$busqueda\"",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            //si hay libros, se pintan en un grid de dos columnas
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(estado.libros, key = { it.id }) { libro ->
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
        }
}
}
@Composable
fun ItemLibro(
    libro: Libro,
    onClick: (Int) -> Unit,
    onToggleLeido: (Int) -> Unit,
    onToggleFavorito: (Int) -> Unit
) {
    Card(
        onClick = { onClick(libro.id) },
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(libro.titulo, style = MaterialTheme.typography.titleSmall)

            if (Patterns.WEB_URL.matcher(libro.cover).matches())
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(libro.cover)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Portada de ${libro.titulo}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            else //Si la url no es válida saldra la imagen por defecto
                AsyncImage(
                    model = R.drawable.nocover,
                    contentDescription = "Portada de ${libro.titulo}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            Text(
                text = "${libro.autor} • ${libro.year}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { onToggleLeido(libro.id) }) {
                    Icon(
                        imageVector = if (libro.leido) Icons.Default.BookmarkAdded
                        else Icons.Default.BookmarkBorder,
                        contentDescription = if (libro.leido) "Quitar leído" else "Marcar como leído",
                        tint = if (libro.leido) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onToggleFavorito(libro.id) }) {
                    Icon(
                        imageVector = if (libro.esFavorito) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (libro.esFavorito) "Quitar favorito" else "Añadir favorito",
                        tint = if (libro.esFavorito) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}