package edu.marinValeria.appdummy.navegacion

import kotlinx.serialization.Serializable

// R11: cada destino es una clase o un objeto, no un String,
//así el compilador comprueba los argumentos y no hay errores de tecleo.

@Serializable
data object Inicio

@Serializable
data object Listado

@Serializable
data class Detalle(val id: Int)

@Serializable
data object Favoritos
