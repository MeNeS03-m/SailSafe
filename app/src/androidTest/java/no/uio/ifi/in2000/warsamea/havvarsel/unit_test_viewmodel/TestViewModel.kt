package no.uio.ifi.in2000.warsamea.havvarsel.unit_test_viewmodel

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.warsamea.havvarsel.ui.ViewModel
import org.junit.Test

class TestViewModel {
    @Test
    fun getViewModelWeatherDataByPlaceName() = runBlocking {
        val homeViewModel = ViewModel()
        homeViewModel.loadWeatherDataByPlaceName("Oslo")
        delay(5000)
        val test = homeViewModel.weatherApiUiState.value.weatherApi?.place?.town
        assertEquals("Sentrum", test)
    }

    @Test
    fun getViewModeWeatherData() = runBlocking {
        val homeViewModel = ViewModel()
        homeViewModel.loadWeatherData(59.90 , 10.75)
        delay(5000)
        val test = homeViewModel.weatherApiUiState.value.weatherApi?.place?.town
        assertEquals("Norway", test)
    }
}