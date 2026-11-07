package edu.marinValeria.appdummy.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.marinValeria.appdummy.screens.bienvenida.PantallaBienvenida
import edu.marinValeria.appdummy.screens.listado.PantallaListado

// R12: aqui viven el NavHost y el NavController
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Inicio   // R14: la PantallaBienvenida es el destino inicial
    ) {
        composable<Inicio> {
            PantallaBienvenida(
                onEntrar = {
                    // R14: con inclusive = true, Inicio sale de la pila,
                    // así el boton Atras desde el listado cierra la app.
                    navController.navigate(Listado) {
                        popUpTo<Inicio> { inclusive = true }
                    }
                }
            )
        }

        composable<Listado> {
            PantallaListado()
        }
    }
}