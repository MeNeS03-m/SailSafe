package no.uio.ifi.in2000.warsamea.havvarsel.data.metalerts

import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.warsamea.havvarsel.model.metalerts.MetAlertsData

@Serializable
data class MetAlertsVariables(
    val area: String?,
    val awarenessSeriousness: String?,
    val awarenessLevel: String?,
    val awarenessType: String?,
    val certainty: String?,
    val consequences: String?,
    val description: String?,
    val eventAwarenessName: String?,
    val instruction: String?,
    val title: String?,
    val resources: List<String>?,
    val web: String?,
    val timePeriod: List<String>?
)

interface MetAlertsRepository {
    suspend fun getMetAlertsData(lat: Double, lon: Double): MetAlertsVariables?
}

class NetworkMetAlertsRepository(
    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource()
): MetAlertsRepository {
    override suspend fun getMetAlertsData(lat: Double, lon: Double): MetAlertsVariables? {
        val metAlertsData: MetAlertsData? = metAlertsDataSource.fetchMetAlertsData(lat, lon)
        return if(metAlertsData?.features?.isEmpty() == true){
            null //Sjekker om det finnes farevarsel i homescreen. If null, IKKe farevarsel
        } else { //Dette er farevarselet
            MetAlertsVariables(
                metAlertsData?.features?.get(0)?.properties?.area,
                metAlertsData?.features?.get(0)?.properties?.awarenessSeriousness,
                metAlertsData?.features?.get(0)?.properties?.awareness_level,
                metAlertsData?.features?.get(0)?.properties?.awareness_type,
                metAlertsData?.features?.get(0)?.properties?.certainty,
                metAlertsData?.features?.get(0)?.properties?.consequences,
                metAlertsData?.features?.get(0)?.properties?.description,
                metAlertsData?.features?.get(0)?.properties?.eventAwarenessName,
                metAlertsData?.features?.get(0)?.properties?.instruction,
                metAlertsData?.features?.get(0)?.properties?.title,
                metAlertsData?.features?.get(0)?.properties?.resources?.map { it.uri },
                metAlertsData?.features?.get(0)?.properties?.web,
                metAlertsData?.features?.get(0)?.`when`?.interval
            )
        }
    }
}

suspend fun main() {
    val net = NetworkMetAlertsRepository()
    val res = net.getMetAlertsData(63.0,4.0)
    println(res.toString())
}