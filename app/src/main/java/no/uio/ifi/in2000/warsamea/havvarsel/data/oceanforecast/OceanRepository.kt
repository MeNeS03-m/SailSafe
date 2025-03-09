package no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.warsamea.havvarsel.model.oceanforecast.OceanData
import no.uio.ifi.in2000.warsamea.havvarsel.model.oceanforecast.TimeSeries
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Serializable
data class OceanVariables(
    val date: String?,
    val time: String?,
    val seaSurfaceWaveHeight: Double?,
    val seaWaterSpeed: Double?,
    val seaWaterToDirection: Double?,
    val seaWaterTemperature: Double?,
    val hourForHour: MutableList<OcVar>?
)

@Serializable
data class OcVar(
    val date: String?,
    val time: String?,
    val seaSurfaceWaveHeight: Double?,
    val seaWaterSpeed: Double?,
    val seaWaterTemperature: Double?,
    val seaWaterToDirection: Double?
)

interface OceanRepository {
    suspend fun getOceanData(lat: Double, lon: Double): OceanVariables?
}

class NetworkOceanDataRepository(
    private val oceanDataSource: OceanDataSource = OceanDataSource()
): OceanRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getOceanData(lat: Double, lon: Double): OceanVariables? {
        val oceanData: OceanData? = oceanDataSource.fetchOceanForecastData(lat, lon)
        return if(oceanData?.properties?.timeseries?.isEmpty() == true){
            null
        } else {
            val index = oceanData?.properties?.timeseries.let { currentIndex(it) }
            OceanVariables(
                oceanData?.properties?.timeseries?.get(index)?.time?.let { convertDate(it, false) },
                oceanData?.properties?.timeseries?.get(index)?.time?.let { convertDate(it, true) },
                oceanData?.properties?.timeseries?.get(index)?.data?.instant?.details?.sea_surface_wave_height,
                oceanData?.properties?.timeseries?.get(index)?.data?.instant?.details?.sea_water_speed,
                oceanData?.properties?.timeseries?.get(index)?.data?.instant?.details?.sea_water_to_direction,
                oceanData?.properties?.timeseries?.get(index)?.data?.instant?.details?.sea_water_temperature,
                getHourForHour(oceanData?.properties?.timeseries)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getHourForHour(alerts: List<TimeSeries>?): MutableList<OcVar> {
    val longTerm: MutableList<OcVar> = mutableListOf()
    val indexAlerts = currentIndex(alerts) //Dette er indeksen i timeseries som er for nåværende klokkeslett. Noen ganger er endepunktet 2-3 timer bak
    alerts?.size?.minus(1)?.let {
        alerts.subList(indexAlerts+1, it).forEach{ element ->
            longTerm.add(
                OcVar(
                    convertDate(element.time, false),
                    convertDate(element.time, true),
                    element.data.instant.details.sea_surface_wave_height,
                    element.data.instant.details.sea_water_speed,
                    element.data.instant.details.sea_water_temperature,
                    element.data.instant.details.sea_water_to_direction
                ))
        }
    }
    return longTerm
}
@RequiresApi(Build.VERSION_CODES.O)
private fun convertDate(date: String, time: Boolean): String {
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME //Dato-type for input

    val locale = Locale("nb", "NO") // For norsk månedsnavn (Bokmål)

    var outputFormatter = DateTimeFormatter.ofPattern("dd. MMMM", locale) //Dato-type for output
    if (time) outputFormatter = DateTimeFormatter.ofPattern("HH") //HH står for at man bare skal få time

    return LocalDateTime.parse(date, inputFormatter).format(outputFormatter)
}
@RequiresApi(Build.VERSION_CODES.O)
private fun currentIndex(alerts: List<TimeSeries>?): Int {
    val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) //Henter time for nåværende klokkeslett
    alerts?.forEachIndexed { index, element ->
        if (convertDate(element.time, true).toInt() == time) {
            return index
        }
    }
    return 0
}
