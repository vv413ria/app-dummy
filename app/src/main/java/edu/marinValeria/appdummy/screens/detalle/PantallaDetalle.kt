package edu.marinValeria.appdummy.screens.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// R13: recibe onVolver como callback, no el NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = DetalleViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titulo = (uiState as? DetalleUiState.Exito)?.libro?.titulo ?: "Detalle"
                    Text(titulo)
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val estado = uiState) {

                is DetalleUiState.Cargando ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is DetalleUiState.Exito -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = estado.libro.titulo,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = {}, label = { Text(estado.libro.autor) })
                            AssistChip(onClick = {}, label = { Text("${estado.libro.year}") })
                        }
                        Text(
                            text = "ISBN ${estado.libro.isbn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilledTonalButton(onClick = { viewModel.toggleLeido() }) {
                                Icon(
                                    if (estado.libro.leido) Icons.Default.BookmarkAdded
                                    else Icons.Default.BookmarkBorder,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (estado.libro.leido) "Leido" else "Marcar leido")
                            }
                            FilledTonalButton(onClick = { viewModel.toggleFavorito() }) {
                                Icon(
                                    if (estado.libro.esFavorito) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (estado.libro.esFavorito) "Favorito" else "Anadir favorito")
                            }
                        }
                    }
                }

                is DetalleUiState.NoEncontrado -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Libro no encontrado")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onVolver) { Text("Volver al catalogo") }
                    }
                }
            }
        }
    }
}