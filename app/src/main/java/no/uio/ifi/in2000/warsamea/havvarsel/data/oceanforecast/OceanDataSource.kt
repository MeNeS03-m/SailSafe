package no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast

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
import no.uio.ifi.in2000.warsamea.havvarsel.model.oceanforecast.OceanData

class OceanDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
            defaultRequest {
            }
        }
    }

    suspend fun fetchOceanForecastData(lat: Double, lon: Double): OceanData? {
        return try {
            val httpResponse: HttpResponse =
                client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete?lat=${lat}&lon=${lon}") {
                    headers {
                        append("X-Gravitee-API-Key", "15b54b6c-0fb4-4cfa-9332-f0c86bf3822c")
                    }
                }
            if (httpResponse.status.isSuccess()) {
                httpResponse.body<OceanData>()
            } else {
                // Håndter feilmeldinger her
                println("Feil ved henting av værdata: ${httpResponse.status}")
                null
            }
        } catch (e: Exception) {
            // Håndter unntak her
            println("Feil ved henting av værdata: ${e.message}")
            null
        }
    }
}