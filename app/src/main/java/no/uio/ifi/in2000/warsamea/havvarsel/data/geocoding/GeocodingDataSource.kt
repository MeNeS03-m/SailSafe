package no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.warsamea.havvarsel.model.geocoding.GeocodingData
import java.net.URLEncoder

class GeocodingDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchGeocodingData(lat: Double, lon: Double): GeocodingData? {
        try {
            val url =
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lon&key="
            val httpResponse: HttpResponse = client.get(url)

            return when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    httpResponse.body<GeocodingData>()
                }

                else -> {
                    // Håndter feilmeldinger her
                    println("Feil ved henting av geokodingdata: ${httpResponse.status}")
                    null
                }
            }
        } catch (e: Exception) {
            // Håndter unntak her
            println("Feil ved henting av geokodingdata: ${e.message}")
            return null
        }
    }

    suspend fun fetchGeocodingDataByPlaceName(placeName: String): GeocodingData? {
        try {
            val encodedPlaceName = withContext(Dispatchers.IO) {
                URLEncoder.encode(placeName, "UTF-8")
            }
            val url =
                "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedPlaceName&key=AIzaSyBwUOUwUrv6mqU9NOFe9Ot_kcvKmkPFpm4"
            val httpResponse: HttpResponse = client.get(url)

            return when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    httpResponse.body<GeocodingData>()
                }

                else -> {
                    // Håndter feilmeldinger her
                    println("Feil ved henting av geokodingdata for $placeName: ${httpResponse.status}")
                    null
                }
            }
        } catch (e: Exception) {
            // Håndter unntak her
            println("Feil ved henting av geokodingdata for $placeName: ${e.message}")
            return null
        }
    }
}