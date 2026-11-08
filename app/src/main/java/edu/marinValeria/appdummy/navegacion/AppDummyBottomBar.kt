package edu.marinValeria.appdummy.navegacion

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy

// R15: la barra se construye recorriendo la lista de items
@Composable
fun AppDummyBottomBar(
    items: List<ItemNavegacion<*>>,
    destinoActual: NavDestination?,
    onItemClick: (ItemNavegacion<*>) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            //comprueba si la ruta del item es la del destino actual
            val seleccionado = destinoActual?.hierarchy?.any { it.hasRoute(item.ruta::class) } == true

            NavigationBarItem(
                selected = seleccionado,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        if (seleccionado) item.iconoSeleccionado else item.icono,
                        contentDescription = item.etiqueta
                    )
                },
                label = { Text(item.etiqueta) }
            )
        }
    }
}