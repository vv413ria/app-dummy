package edu.marinValeria.appdummy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.marinValeria.appdummy.navegacion.AppNavigation
import edu.marinValeria.appdummy.ui.theme.AppDummyTheme

//R10: unica Activity de la app, he quitado el Greeting y su Preview porque ahora
// cada pantalla es un destino del NavHost
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDummyTheme {
                AppNavigation()
            }
        }
    }
}