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
import edu.marinValeria.appdummy.screens.detalle.PantallaDetalle
import androidx.navigation.NavHostController
import edu.marinValeria.appdummy.screens.permisos.PantallaGestionPermisos
// R12: aqui viven el NavHost y el NavController
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // R15: el destino activo sale de currentBackStackEntryAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = backStackEntry?.destination

    // la barra solo se ve en las pestañas, no en la bienvenida ni en el detalle
    val mostrarBarra = itemsNavegacionPrincipal.any { item ->
        destinoActual?.hierarchy?.any { it.hasRoute(item.ruta::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                AppDummyBottomBar(
                    items = itemsNavegacionPrincipal,
                    destinoActual = destinoActual,
                    onItemClick = { item ->
                        // R16: estas cuatro opciones conservan el estado de cada pestaña
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
            composable<Favoritos> {
                PantallaFavoritos(
                    onNavegaADetalle = { id -> navController.navigate(Detalle(id = id)) }
                )
            }
            composable<Listado> {
                PantallaListado(
                    onNavegaADetalle = { id -> navController.navigate(Detalle(id = id)) },
                            onNavegaAPermisos = { navController.navigate(Permisos) }
                )
            }
            composable<Detalle> {
                // R12: se vuelve con navigateUp()
                PantallaDetalle(onVolver = { navController.navigateUp() })
            }
            composable<Permisos> {
                PantallaGestionPermisos(onVolver = { navController.navigateUp() })
            }
        }
    }
}