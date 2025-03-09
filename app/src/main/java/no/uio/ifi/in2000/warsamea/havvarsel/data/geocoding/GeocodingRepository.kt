package no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingVariablesAddress(
    val town: String,
    val street: String,
)

data class GeocodingVariablesLocation(
    val lat: Double?,
    val lon: Double?
)

interface GeocodingRepository{
    suspend fun getGeocodingData(lat: Double, lon: Double): GeocodingVariablesAddress
    suspend fun getGeocodingDataByPlaceName(placeName: String): GeocodingVariablesLocation?
}

class NetworkGeocodingRepository(
    private val geocodingDataSource: GeocodingDataSource = GeocodingDataSource()
): GeocodingRepository {
    override suspend fun getGeocodingData(lat: Double, lon: Double): GeocodingVariablesAddress {
        val geocodingData = geocodingDataSource.fetchGeocodingData(lat, lon)
        val addressComponent =
            geocodingData?.results?.getOrNull(0)?.address_components?.getOrNull(2)
        val town = addressComponent?.long_name ?: ""
        val street =
            geocodingData?.results?.getOrNull(0)?.address_components?.getOrNull(1)?.long_name ?: ""
        return GeocodingVariablesAddress(town, street)
    }

    override suspend fun getGeocodingDataByPlaceName(placeName: String): GeocodingVariablesLocation {
        val geocodingData = geocodingDataSource.fetchGeocodingDataByPlaceName(placeName)

        return GeocodingVariablesLocation(
            geocodingData?.results?.get(0)?.geometry?.location?.lat,
            geocodingData?.results?.get(0)?.geometry?.location?.lng
        )
    }
}
