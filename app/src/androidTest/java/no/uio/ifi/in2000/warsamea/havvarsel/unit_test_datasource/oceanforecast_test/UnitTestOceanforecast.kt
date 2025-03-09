package no.uio.ifi.in2000.warsamea.havvarsel.unit_test_datasource.oceanforecast_test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast.OceanDataSource
import org.junit.Test


class UnitTestOceanforecast {
    @Test
    fun fetchOceanForecastDataNullValue() = runBlocking {
        val oceanDataSource = OceanDataSource()
        val result = oceanDataSource.fetchOceanForecastData(59.87, 10.76)
        assert(result != null)
    }

    @Test
    fun fetchOceanForecastDataType() = runBlocking {
        val oceanDataSource = OceanDataSource()
        val result = oceanDataSource.fetchOceanForecastData(59.87, 10.76)
        assertEquals("Point", result?.geometry?.type)
    }

    @Test
    fun fetchOceanForecastDataUnit() = runBlocking {
        val oceanDataSource = OceanDataSource()
        val result = oceanDataSource.fetchOceanForecastData(59.87, 10.76)
        assertEquals("m/s", result?.properties?.meta?.units?.sea_water_speed)
    }
}
