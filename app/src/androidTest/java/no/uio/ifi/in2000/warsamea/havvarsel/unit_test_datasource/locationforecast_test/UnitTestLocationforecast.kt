package no.uio.ifi.in2000.warsamea.havvarsel.unit_test_datasource.locationforecast_test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.LocationDataSource
import org.junit.Test


class UnitTestLocationforecast {
    @Test
    fun fetchLocationForecastDataNullValue() = runBlocking {
        val locationDataSource = LocationDataSource()
        val result = locationDataSource.fetchLocationForecastData(59.87, 10.76)
        assert(result != null)
    }

    @Test
    fun fetchLocationForecastDataType() = runBlocking {
        val locationDataSource = LocationDataSource()
        val result = locationDataSource.fetchLocationForecastData(59.87, 10.76)
        assertEquals("Point", result?.geometry?.type)
    }

    @Test
    fun fetchLocationForecastDataUnit() = runBlocking {
        val locationDataSource = LocationDataSource()
        val result = locationDataSource.fetchLocationForecastData(59.87, 10.76)
        assertEquals("hPa", result?.properties?.meta?.units?.air_pressure_at_sea_level)
    }
}

