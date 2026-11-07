package edu.marinValeria.appdummy.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.marinValeria.appdummy.screens.bienvenida.PantallaBienvenida
import edu.marinValeria.appdummy.screens.favoritos.PantallaFavoritos
import edu.marinValeria.appdummy.screens.listado.PantallaListado
import androidx.navigation.compose.composable
import edu.marinValeria.appdummy.screens.detalle.PantallaDetalle

// R12: aqui viven el NavHost y el NavController
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // R15: el destino activo sale de currentBackStackEntryAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = backStackEntry?.destination
    // la barra solo aparece en las pestañas, no en la bienvenida
    val mostrarBarra = destinoActual?.hierarchy?.any { destino ->
        itemsNavegacionPrincipal.any { destino.hasRoute(it.ruta::class) }
    } == true

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                AppDummyBottomBar(
                    items = itemsNavegacionPrincipal,
                    destinoActual = destinoActual,
                    onItemClick = { item ->
                        // R16: estas cuatro opciones son las que hacen que
                        // al volver a una pestaña se recupere su scroll
                        navController.navigate(item.ruta) {
                            popUpTo<Listado> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Inicio,   //R14
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Inicio> {
                PantallaBienvenida(
                    onEntrar = {
                        // R14: con inclusive = true, Inicio sale de la pila
                        //así el boton Atras desde el listado cierra la app.
                        navController.navigate(Listado) {
                            popUpTo<Inicio> { inclusive = true }
                        }
                    }
                )
            }
            composable<Listado> {
                PantallaListado()
            }
            composable<Favoritos> {
                PantallaFavoritos(
                    onNavegaADetalle = { id -> navController.navigate(Detalle(id = id)) }
                )
            }
            composable<Listado> {
                PantallaListado(
                    onNavegaADetalle = { id -> navController.navigate(Detalle(id = id)) }
                )
            }

            composable<Detalle> {
                // R12: se vuelve con navigateUp()
                PantallaDetalle(onVolver = { navController.navigateUp() })
            }
        }
    }
}