package no.uio.ifi.in2000.warsamea.havvarsel.model.locationforecast

/*
* De fleste Data filene for APIene våre har en del warnings, dette kommer av at
* vi bruker flere variabelnavn med "_" i seg. Dette liker ikke Kotlin. Vi har
* allikevel valgt å beholde dette ettersom vi brukte funksjonen Kotlin data class
* file from JSON som genererte disse variabelnavnene til oss.
*/
data class LocationData(
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
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val air_temperature_max: String,
    val air_temperature_min: String,
    val air_temperature_percentile_10: String,
    val air_temperature_percentile_90: String,
    val cloud_area_fraction: String,
    val cloud_area_fraction_high: String,
    val cloud_area_fraction_low: String,
    val cloud_area_fraction_medium: String,
    val dew_point_temperature: String,
    val fog_area_fraction: String,
    val precipitation_amount: String,
    val precipitation_amount_max: String,
    val precipitation_amount_min: String,
    val probability_of_precipitation: String,
    val probability_of_thunder: String,
    val relative_humidity: String,
    val ultraviolet_index_clear_sky: String,
    val wind_from_direction: String,
    val wind_speed: String,
    val wind_speed_of_gust: String,
    val wind_speed_percentile_10: String,
    val wind_speed_percentile_90: String
)
data class TimeSeries(
    val time: String,
    val data: TimeSeriesData
)
data class TimeSeriesData (
    val instant: InstantData,
    val next_12_hours: Next12HoursData,
    val next_1_hours: Next1HoursData,
    val next_6_hours: Next6HoursData
)
data class InstantData(
    val details: InstantDetails
)
data class InstantDetails(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double, //JA
    val air_temperature_percentile_10: Double,
    val air_temperature_percentile_90: Double,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double,
    val cloud_area_fraction_low: Double,
    val cloud_area_fraction_medium: Double,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double,
    val relative_humidity: Double,
    val ultraviolet_index_clear_sky: Double,
    val wind_from_direction: Double, //JA
    val wind_speed: Double, //JA
    val wind_speed_of_gust: Double, //JA
    val wind_speed_percentile_10: Double,
    val wind_speed_percentile_90: Double
)
data class Next12HoursData(
    val summary: Summary,
    val details: Next12HoursDetails
)
data class Summary(
    val symbol_code: String,
    val symbol_confidence: String
)
data class Next12HoursDetails(
    val probability_of_precipitation: Double
)
data class Next1HoursData(
    val summary: Summary1And6,
    val details: Next1HoursDetails
)
data class Next1HoursDetails(
    val precipitation_amount: Double,
    val precipitation_amount_max: Double,
    val precipitation_amount_min: Double,
    val probability_of_precipitation: Double,
    val probability_of_thunder: Double
)
data class Next6HoursData(
    val summary: Summary1And6,
    val details: Next6HoursDetails
)

data class Summary1And6(
    val symbol_code: String
)
data class Next6HoursDetails(
    val air_temperature_max: Double,
    val air_temperature_min: Double,
    val precipitation_amount: Double,
    val precipitation_amount_max: Double,
    val precipitation_amount_min: Double,
    val probability_of_precipitation: Double
)