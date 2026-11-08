# AppDummy v2 — G-APPDUMMY-V2
Valeria Marín Ferrándiz · 10640502

## Qué hace
AppDummy es un catálogo de libros. Muestra un listado en grid con búsqueda por
título y filtro por autor, permite marcar cada libro como favorito o leído, y
consultar su detalle. Tiene una pestaña de favoritos y una pantalla de gestión del
permiso de cámara. Toda la app vive en una sola Activity con Navigation Compose.

## Arquitectura

UI (@Composable)  →  ViewModel  →  Repository  →  datos estáticos

- `data/model/Libro.kt` — modelo de datos de un libro.
- `data/repository/LibrosRepository.kt` — única fuente de verdad. Guarda la lista en
  un StateFlow y expone las operaciones de favorito y leído.
- `screens/listado/LibrosUiState.kt` — sealed class con Cargando, Exito y Error, y
  los eventos de un solo disparo (LibrosEvento).
- `screens/listado/LibrosViewModel.kt` — estado del listado con backing property,
  búsqueda, filtro y eventos por SharedFlow.
- `screens/listado/PantallaListado.kt` — grid, buscador y chips. Solo notifica al
  ViewModel.
- `screens/detalle/DetalleViewModel.kt` — DetalleUiState y lectura del id de la ruta
  mediante SavedStateHandle.
- `screens/detalle/PantallaDetalle.kt` — ficha del libro.
- `screens/favoritos/PantallaFavoritos.kt` — libros marcados, con estado vacío.
- `screens/permisos/PantallaGestionPermisos.kt` — permiso de cámara.
- `navegacion/` — rutas tipadas, NavHost y barra inferior.

## Grafo de navegación

Inicio (PantallaBienvenida) — destino inicial
  └─ navigate(Listado) con popUpTo<Inicio> { inclusive = true }

Listado ──navigate(Detalle(id))──► Detalle ──navigateUp()──► Listado
Listado ──navigate(Permisos)────► Permisos ──navigateUp()──► Listado
Listado ◄──── barra inferior ────► Favoritos ──► Detalle(id)

Argumentos: solo `Detalle` lleva uno, `id: Int`, que viaja en la propia ruta
gracias a @Serializable y lo recoge el DetalleViewModel con SavedStateHandle.

Pila: al entrar desde la bienvenida se elimina Inicio con inclusive = true, así que
Atrás desde el listado cierra la app. Al cambiar de pestaña se usa
popUpTo<Listado> { saveState = true }, launchSingleTop y restoreState, de modo que
no se apilan destinos y se recupera el scroll.

## Dependencias añadidas

| Librería | Para qué |
|---|---|
| lifecycle-viewmodel-compose | obtener el ViewModel desde un Composable |
| lifecycle-runtime-compose | collectAsStateWithLifecycle |
| navigation-compose | NavHost y navegación tipada |
| kotlinx-serialization-json + plugin de serialización | rutas @Serializable |
| kotlinx-coroutines-test | pruebas del ViewModel |

## Decisiones propias


1. Dónde vive el estado de favorito y leído. R7 pide que favorito y leído se
    gestionen desde el ViewModel, y el ejemplo del Tema 2 los cambia con _uiState.update 
    sobre el estado del propio ViewModel. Yo lo hice distinto: la pantalla sigue avisando
    solo al ViewModel, pero este delega en LibrosRepository, que es quien guarda la lista
    en un StateFlow.Lo decidí porque el listado, el detalle y favoritos necesitan los 
    mismos libros, y si cada ViewModel tuviera su copia, al marcar un favorito en una 
    pantalla las otras no se enterarían. El A1 del bloque 2 lo dice igual: el repositorio 
    debe ser la única fuente de verdad, y tener copias independientes es un antipatrón.

2. R16 pide usar popUpTo, launchSingleTop, saveState y restoreState, pero no dice a qué destino desapilar. 
   El ejemplo del Tema 3 usa findStartDestination(), que devuelve el primer destino del grafo. 
   En su caso funciona porque su grafo arranca en Listado, pero el mío arranca en Inicio y R14 
   obliga a eliminarlo de la pila al entrar. Entonces, findStartDestination() apuntaría a un destino 
   que ya no está en la pila: no desapilaría nada, saveState no guardaría nada, y restoreState no 
   tendría qué restaurar, así que se perdería el scroll. Por eso desapilo hasta Listado, que sí sigue 
   en la pila.

3. Al probar el cambio de pestañas vi que la primera vez que volvía al catálogo el scroll no se quedaba
   donde lo había dejado, aunque la segunda vez sí. El motivo era que el AsyncImage solo tenía definido 
   el ancho: hasta que Coil no termina de descargar la portada, Compose no sabe cuánto mide la imagen, 
   así que las celdas no tienen un alto estable y el grid va cambiando de tamaño mientras cargan. Al 
   restaurar el scroll sobre un grid que todavía se estaba midiendo, la posición no cuadraba. Le puse 
   180dp de alto fijo a la imagen y se arregló, porque ahora la celda ocupa lo mismo haya cargado la 
   portada o no.

## Limitaciones conocidas

- La v1 no se entregó y el proyecto parte de la versión de referencia publicada, 
  por eso el git_log.txt no contiene una historia real de la v1
- Los datos son estáticos y no persisten: al cerrar la app se pierden los cambios
  de favorito y leído.