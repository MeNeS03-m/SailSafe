package no.uio.ifi.in2000.warsamea.havvarsel.unit_test_repository.unit_test_all_repositories
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.NetworkAllAPIRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.NetworkLocationRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts.NetworkMetAlertsRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast.NetworkOceanDataRepository
import org.junit.Test

class UnitTestAllRepositories {
    @Test
    fun getAllAPIReturnsValidData() = runBlocking {
        // Arrange
        val lat = 59.90 // Latitude for Oslo
        val lon = 10.75 // Longitude for Oslo

        val repository: AllAPIRepository = NetworkAllAPIRepository(
            NetworkMetAlertsRepository(),
            NetworkOceanDataRepository(),
            NetworkLocationRepository()
        )

        // Act
        val result: AllAPIVariables = repository.getAllAPI(lat, lon)

        // Assert
        assertNotNull(result)
        assertNotNull(result.location)
        assertNotNull(result.ocean)
        assertNotNull(result.alerts)
    }
}