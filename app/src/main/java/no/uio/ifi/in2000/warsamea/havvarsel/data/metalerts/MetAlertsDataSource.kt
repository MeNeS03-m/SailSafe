package no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts

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
import no.uio.ifi.in2000.warsamea.havvarsel.model.metalerts.MetAlertsData

class MetAlertsDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
            defaultRequest {
            }
        }
    }

    suspend fun fetchMetAlertsData(lat: Double, lon: Double): MetAlertsData? {
        return try {
            val httpResponse: HttpResponse =
                client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/2.0/current.json?lat=$lat&lon=$lon") {
                    headers {
                        append("X-Gravitee-API-Key", "15b54b6c-0fb4-4cfa-9332-f0c86bf3822c")
                    }
                }
            if (httpResponse.status.isSuccess()) {
                httpResponse.body<MetAlertsData>()
            } else {
                // Håndter feilmeldinger her
                println("Feil ved henting av varsler: ${httpResponse.status}")
                null
            }
        } catch (e: Exception) {
            // Håndter unntak her
            println("Feil ved henting av varsler: ${e.message}")
            null
        }
    }
}
