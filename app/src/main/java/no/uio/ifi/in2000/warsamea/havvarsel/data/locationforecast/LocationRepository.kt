package no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.warsamea.havvarsel.model.locationforecast.TimeSeries
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Serializable
data class LocationVariables(
    val date: String,
    val time: String,
    val airTemperature: Double,
    val windFromDirection: Double,
    val windSpeed: Double,
    val windSpeedOfGust: Double,
    val precipitationAmount: Double,
    val precipitationAmountMax: Double,
    val precipitationAmountMin: Double,
    val iconHours: String,
    val fogAreaFraction: Double,
    val ultravioletIndexClearSky: Double,
    val humidity: Double,
    val hourForHour: MutableList<LocVar>,
    val sixHours: MutableList<LocVar>,
)

@Serializable
data class LocVar(
    val date: String?,
    val time: String?,
    val airTemperature: Double?,
    val windFromDirection: Double?,
    val windSpeed: Double?,
    val windSpeedOfGust: Double?,
    val precipitationAmount: Double?,
    val precipitationAmountMax: Double?,
    val precipitationAmountMin: Double?,
    val iconHours: String?,
    val fogAreaFraction: Double,
    val ultravioletIndexClearSky: Double,
    val humidity: Double
)

interface LocationRepository {
    suspend fun getLocationData(lat: Double, lon: Double): LocationVariables?
}

class NetworkLocationRepository(
    private val locationDataSource: LocationDataSource = LocationDataSource()
): LocationRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLocationData(lat: Double, lon: Double): LocationVariables? {
        val locationData = locationDataSource.fetchLocationForecastData(lat, lon)
        val indexAlerts = giveCorrectTime(locationData?.properties?.timeseries) //Skal brukes for å få værmelding for nåværende klokkesletts time
        return locationData?.properties?.timeseries?.get(indexAlerts)?.let {
            LocationVariables(
                convertDate(it.time, false),
                convertDate(locationData.properties.timeseries[indexAlerts].time, true),
                locationData.properties.timeseries[indexAlerts].data.instant.details.air_temperature,
                locationData.properties.timeseries[indexAlerts].data.instant.details.wind_from_direction,
                locationData.properties.timeseries[indexAlerts].data.instant.details.wind_speed,
                locationData.properties.timeseries[indexAlerts].data.instant.details.wind_speed_of_gust,
                locationData.properties.timeseries[indexAlerts].data.next_6_hours.details.air_temperature_max -
                        locationData.properties.timeseries[indexAlerts].data.next_6_hours.details.air_temperature_min,
                locationData.properties.timeseries[indexAlerts].data.next_6_hours.details.air_temperature_max,
                locationData.properties.timeseries[indexAlerts].data.next_6_hours.details.air_temperature_min,
                locationData.properties.timeseries[indexAlerts].data.next_6_hours.summary.symbol_code,
                locationData.properties.timeseries[indexAlerts].data.instant.details.fog_area_fraction,
                locationData.properties.timeseries[indexAlerts].data.instant.details.ultraviolet_index_clear_sky,
                locationData.properties.timeseries[indexAlerts].data.instant.details.relative_humidity,
                getHourForHour(locationData.properties.timeseries),
                getSixHours(locationData.properties.timeseries)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getHourForHour(alerts: List<TimeSeries>): MutableList<LocVar> {
    val longTerm: MutableList<LocVar> = mutableListOf()
    val indexAlerts = giveCorrectTime(alerts)
    val sixHourIndex = getSixHoursIndex(alerts)
    alerts.subList(indexAlerts + 1, sixHourIndex).forEach { longTerm.add(makeLocVar(it)) }
    return longTerm
}


@RequiresApi(Build.VERSION_CODES.O)
private fun getSixHours(alerts: List<TimeSeries>): MutableList<LocVar> {
    val longTerm: MutableList<LocVar> = mutableListOf()
    val indexAlerts = giveCorrectTime(alerts)
    val sixHourIndex = getSixHoursIndex(alerts)
    var diff = 0
    for((index, element) in alerts.subList(indexAlerts, sixHourIndex).withIndex()) {
        if(index == 0){
            val convert = convertDate(element.time, true).toInt()
            val sixes = listOf(6, 12, 18, 24)
            sixes.forEach { number ->
                if(number-convert in 1..5){
                    diff = number-convert
                }
            }
            longTerm.add(makeLocVar(element))
        }
        if(index == diff){
            longTerm.add(makeLocVar(element))
            diff += 6
        }
    }
    alerts.subList(sixHourIndex, alerts.size-1).forEach{ longTerm.add(makeLocVar(it)) }
    if (longTerm[0] == longTerm[1]) longTerm.removeAt(0)

    return longTerm
}

@RequiresApi(Build.VERSION_CODES.O)
private fun makeLocVar(element: TimeSeries): LocVar {
    val next1HoursData = element.data.next_1_hours
    val summary1And6 = next1HoursData?.summary?.symbol_code ?: "" // Trenger for å laste inn data
    val details = next1HoursData?.details // Trenger for å laste inn data

    return LocVar(
        convertDate(element.time, false),
        convertDate(element.time, true),
        element.data.instant.details.air_temperature,
        element.data.instant.details.wind_from_direction,
        element.data.instant.details.wind_speed,
        element.data.instant.details.wind_speed_of_gust,
        details?.precipitation_amount,
        details?.precipitation_amount_max,
        details?.precipitation_amount_min,
        summary1And6,
        element.data.instant.details.fog_area_fraction,
        element.data.instant.details.ultraviolet_index_clear_sky,
        element.data.instant.details.relative_humidity
    )
}


@RequiresApi(Build.VERSION_CODES.O)
private fun getSixHoursIndex(alerts: List<TimeSeries>): Int {
    var indexAlerts = 0
    for ((index) in alerts.withIndex()) {
        if (index == 0) continue
        if (convertDate(alerts[index].time, true).toInt() - convertDate(alerts[index - 1].time, true).toInt() > 1) {
            indexAlerts = index - 1
            break
        }
    }
    return indexAlerts
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
private fun giveCorrectTime(alerts: List<TimeSeries>?): Int {
    val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) //Henter time for nåværende klokkeslett
    var indexAlerts = 0
    if (alerts != null) {
        for ((index, element) in alerts.withIndex()) {
            if (convertDate(element.time, true).toInt() == time) {
                indexAlerts = index
                break
            }
        }
    } else {
        Log.e("LocationRepository", "Alerts list is null")
        indexAlerts = -1
    }
    return indexAlerts
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val network = NetworkLocationRepository() //Denne koden skal brukes i vm
    val result: LocationVariables? = network.getLocationData(59.87, 10.76)
    println("Dato: ${result!!.date}\nKlokkeslett: ${result.time}")
    println("Temperaturen er: ${result.airTemperature}")
    /*resultat.timeForTime.forEach { element ->
        println()
        println("Dato: ${element.dato}\nKlokkeslett: ${element.klokke}\nVindstyrke: ${element.wind_speed}")
        println("Temperaturen er: ${element.air_temperature}")
    }*/

    result.sixHours.forEach { element ->
        println()
        println("Dato: ${element.date}\nKlokkeslett: ${element.time}\nVindstyrke: ${element.windSpeed}")
        println("Temperaturen er: ${element.airTemperature}")

        println(result.sixHours[2].date)
        println(result.sixHours[2].time)
    }

    println("Antall varsel i time for time og sekstimers")
    println(result.hourForHour.size)
    println(result.sixHours.size)
}