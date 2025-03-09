package no.uio.ifi.in2000.warsamea.havvarsel.data

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding.GeocodingVariablesAddress
import no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding.GeocodingVariablesLocation
import no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding.NetworkGeocodingRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.LocationVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.NetworkLocationRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts.MetAlertsVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts.NetworkMetAlertsRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast.NetworkOceanDataRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast.OceanVariables

@Serializable
data class AllAPIVariables(
    val location: LocationVariables?,
    val ocean: OceanVariables?,
    val alerts: MetAlertsVariables?,
    val place: GeocodingVariablesAddress?
)

data class AllAPIVariablesLocation(
    val geolocation: GeocodingVariablesLocation
)

interface AllAPIRepository {
    suspend fun getAllAPI(lat: Double, lon: Double): AllAPIVariables
    suspend fun getAllAPIByPlaceName(locationName: String): AllAPIVariablesLocation
}

class NetworkAllAPIRepository(
    private val networkMetAlertsRepository: NetworkMetAlertsRepository = NetworkMetAlertsRepository(),
    private val networkOceanDataRepository: NetworkOceanDataRepository = NetworkOceanDataRepository(),
    private val networkLocationRepository: NetworkLocationRepository = NetworkLocationRepository(),
    private val networkGeocodingRepository: NetworkGeocodingRepository = NetworkGeocodingRepository(),

    ) : AllAPIRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllAPI(lat: Double, lon: Double): AllAPIVariables {
        try {
            val locationVariables = networkLocationRepository.getLocationData(lat, lon)
            val oceanVariables = networkOceanDataRepository.getOceanData(lat, lon)
            val metAlertsVariables = networkMetAlertsRepository.getMetAlertsData(lat, lon)
            val geocodingVariables = networkGeocodingRepository.getGeocodingData(lat, lon)
            return AllAPIVariables(
                locationVariables,
                oceanVariables,
                metAlertsVariables,
                geocodingVariables
            )
        } catch (e: Exception) {
            // Håndter feil ved henting av data her
            println("Feil ved henting av all API-data: ${e.message}")
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllAPIByPlaceName(locationName: String): AllAPIVariablesLocation {
        try {
            val geocodingData = networkGeocodingRepository.getGeocodingDataByPlaceName(locationName)
            return getAllAPI(geocodingData.lat!!, geocodingData.lon!!).run {
                AllAPIVariablesLocation(
                    geolocation = GeocodingVariablesLocation(
                        geocodingData.lat,
                        geocodingData.lon
                    )
                )
            }
        } catch (e: Exception) {
            // Håndter feil ved henting av data her
            println("Feil ved henting av all API-data for stedet $locationName: ${e.message}")
            throw e
        }
    }
}