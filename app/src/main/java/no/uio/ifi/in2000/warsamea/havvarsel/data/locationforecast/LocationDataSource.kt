package no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.warsamea.havvarsel.model.locationforecast.LocationData

class LocationDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
            defaultRequest {
            }
        }
    }

    suspend fun fetchLocationForecastData(lat: Double, lon: Double): LocationData? {
        return try {
            val httpResponse: HttpResponse =
                client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}") {
                    headers {
                        append("X-Gravitee-API-Key", "15b54b6c-0fb4-4cfa-9332-f0c86bf3822c")
                    }
                }
            if (httpResponse.status.isSuccess()) {
                httpResponse.body<LocationData>()
            } else {
                println("Feil ved henting av værdata: ${httpResponse.status}")
                null
            }
        } catch (e: Exception) {
            println("Feil ved henting av værdata: ${e.message}")
            null
        }
    }
}

    suspend fun main() {
    val oslo = LocationDataSource()
    val osloData = oslo.fetchLocationForecastData(59.87, 10.76)
    println(osloData.toString())
}

