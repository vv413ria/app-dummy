package edu.marinValeria.appdummy.screens.listado

import edu.marinValeria.appdummy.data.repository.LibrosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibrosViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun alCargarPasaDeCargandoAExito() = runTest(dispatcher) {
        val viewModel = LibrosViewModel(LibrosRepository())
        assertTrue(viewModel.uiState.value is LibrosUiState.Cargando)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado is LibrosUiState.Exito)
        assertEquals(5, (estado as LibrosUiState.Exito).libros.size)
    }


    @Test
    fun laBusquedaFiltraLosLibros() = runTest(dispatcher) {
        val viewModel = LibrosViewModel(LibrosRepository())
        advanceUntilIdle()
        viewModel.actualizarBusqueda("hail")
        advanceUntilIdle()
        val estado = viewModel.uiState.value as LibrosUiState.Exito
        assertEquals(1, estado.libros.size)
        assertEquals("Proyecto Hail Mary", estado.libros.first().titulo)
    }
}