package no.uio.ifi.in2000.warsamea.havvarsel.unit_test_datasource.metalerts_test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts.MetAlertsDataSource
import org.junit.Test

class UnitTestMetAlerts {
    @Test
    fun fetchMetAlertsDataNullValue() = runBlocking {
        val alertsDataSource = MetAlertsDataSource()
        val result = alertsDataSource.fetchMetAlertsData(59.87, 10.76)
        assert(result != null)
    }

    @Test
    fun fetchMetAlertsDataType() = runBlocking {
        val alertsDataSource = MetAlertsDataSource()
        val result = alertsDataSource.fetchMetAlertsData(59.87, 10.76)
        assertEquals("Polygon", result?.features?.get(0)?.geometry?.type)
    }
}