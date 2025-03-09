package no.uio.ifi.in2000.warsamea.havvarsel.model.oceanforecast

/*
* De fleste Data filene for APIene våre har en del warnings, dette kommer av at
* vi bruker flere variabelnavn med "_" i seg. Dette liker ikke Kotlin. Vi har
* allikevel valgt å beholde dette ettersom vi brukte funksjonen Kotlin data class
* file from JSON som genererte disse variabelnavnene til oss.
*/
data class OceanData(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

data class Properties(
    val meta: Meta,
    val timeseries: List<TimeSeries>
)

data class Meta(
    val updated_at: String,
    val units: Units
)

data class Units(
    val sea_surface_wave_from_direction: String,
    val sea_surface_wave_height: String,
    val sea_water_speed: String,
    val sea_water_temperature: String,
    val sea_water_to_direction: String
)

data class TimeSeries(
    val time: String,
    val data: TimeSeriesData
)

data class TimeSeriesData(
    val instant: InstantData
)

data class InstantData(
    val details: InstantDetails
)

data class InstantDetails(
    val sea_surface_wave_from_direction: Double,
    val sea_surface_wave_height: Double,
    val sea_water_speed: Double,
    val sea_water_temperature: Double,
    val sea_water_to_direction: Double
)