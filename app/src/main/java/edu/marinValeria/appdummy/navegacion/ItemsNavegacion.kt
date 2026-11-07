package edu.marinValeria.appdummy.navegacion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Book
import androidx.compose.ui.graphics.vector.ImageVector

// R15
data class ItemNavegacion<T : Any>(
    val etiqueta: String,
    val ruta: T,
    val icono: ImageVector,
    val iconoSeleccionado: ImageVector = icono
)

// barra generada a partir de esta lista
val itemsNavegacionPrincipal = listOf(
    ItemNavegacion(
        etiqueta = "Catalogo",
        ruta = Listado,
        icono = Icons.Outlined.Book,
        iconoSeleccionado = Icons.Filled.Book
    ),
    ItemNavegacion(
        etiqueta = "Favoritos",
        ruta = Favoritos,
        icono = Icons.Default.FavoriteBorder,
        iconoSeleccionado = Icons.Filled.Favorite
    )
)