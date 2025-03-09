package no.uio.ifi.in2000.warsamea.havvarsel.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.NetworkAllAPIRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding.GeocodingRepository
import no.uio.ifi.in2000.warsamea.havvarsel.data.geocoding.NetworkGeocodingRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class WeatherAPIUiState(
    val weatherApi: AllAPIVariables? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val isDataLoaded: Boolean = false
)

class ViewModel : ViewModel() {
    private val allAPIRepository: AllAPIRepository = NetworkAllAPIRepository()
    private val geocodingRepository: GeocodingRepository = NetworkGeocodingRepository()

    private val _weatherApiUiState = MutableStateFlow(WeatherAPIUiState())
    val weatherApiUiState: StateFlow<WeatherAPIUiState> = _weatherApiUiState


    /*
    * Denne funksjonen sørger for å laste ned værdata ved hjelp av UiStatene våre som er definert ovenfor.
    * Her sender vi inn koordinater som den bruker for å hente værdata. Funksjonen kaller så på
    * getAllAPI som ligger i AllAPIRepository.kt, gjennon variablen allAPIRepository som bruker koordinater for å hente værdata for alle APIene
    * som skal brukes
    */
    fun loadWeatherData(lat: Double, lon: Double, forceUpdate: Boolean = false) {
        viewModelScope.launch {
            // Sjekk om data for disse koordinatene allerede er lastet og om en oppdatering er påkrevd
            if (!forceUpdate && _weatherApiUiState.value.isDataLoaded &&
                _weatherApiUiState.value.lat == lat && _weatherApiUiState.value.lon == lon) {
                return@launch  // Ingen ny lasting nødvendig
            }

            try {
                val weatherData = allAPIRepository.getAllAPI(lat, lon)
                _weatherApiUiState.value = WeatherAPIUiState(weatherData, lat, lon, true)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load weather data", e)
            }
        }
    }

    /*
    * Denne funksjonen gjør i prinsippet det samme som funksjonen over, men i stedet for å hente data
    * på koordinater henter denne data ved å søke på navn. Den benytter seg av funksjonen getGeocodingDataByPlaceName
    * ved å sende inn navnet på stedet som ligger i GeoCodingRepository.kt. Dette gjør gjennom variablen geocodingRepository.
    * */
    fun loadWeatherDataByPlaceName(placeName: String) {
        viewModelScope.launch {
            try {
                val geocodingData = geocodingRepository.getGeocodingDataByPlaceName(placeName)
                if (geocodingData != null) {
                    loadWeatherData(geocodingData.lat!!, geocodingData.lon!!)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Kunne ikke laste inn værdata etter stedsnavn", e)
            }
        }
    }

    /*
    * Denne funksjonen sørger for å hente ut navnet på stedet vi har søkt etter. Dette gjør den
    * ved å bruke variabelen weatherDataUiState og deretter kaller weatherApi som gir oss tilgang til
    * bynavn og gatenavn
    */
    fun getLocationName(weatherDataUiState: AllAPIVariables?): String {
        val town = weatherDataUiState?.place?.town ?: ""
        val street = weatherDataUiState?.place?.street ?: ""
        return if (town.isNotEmpty()) {
            "$town\n$street"
        } else if (street.isNotEmpty()){
            street
        } else {
            "Ukjent lokasjon"
        }
    }

    fun isValidCoordinates(text: String): Boolean {
        // Regex-mønster for samsvarende gyldig koordinatformat
        val coordinatePattern = Regex("^\\d{1,2}\\.\\d+\\s\\d{1,3}\\.\\d+$")

        // Sjekk om teksten stemmer med mønsteret
        if (!coordinatePattern.matches(text)) {
            return false
        }

        // Del teksten inn i bredde- og lengdegrad
        val coordinates = text.split(Regex("\\s"))
        if (coordinates.size != 2) return false

        // Konverter strenger til double
        val lat = coordinates[0].toDoubleOrNull()
        val lon = coordinates[1].toDoubleOrNull()

        // Sjekk om konverteringen var vellykket og verdiene er innenfor gyldige områder
        if (lat == null || lon == null) {
            return false
        }
        if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
            return false
        }

        return true
    }


    /*
    * Denne funksjonen brukes for å gi bruker en enkel oppfatning av hvilken retning
    * vinden beveger seg mot. Den tar for seg gradene som vi får gjennom APIet og ut ifra
    * disse bestemmer hvilken retning dette tilsvarer.
    */
    fun windDirection(degrees: Double): String {
        val direction = when (degrees) {
            in 0.0..11.25, in 348.75..360.0 -> "Nord"
            in 11.25..33.75 -> "Nord-nordøst"
            in 33.75..56.25 -> "Nordøst"
            in 56.25..78.75 -> "Øst-nordøst"
            in 78.75..101.25 -> "Øst"
            in 101.25..123.75 -> "Øst-sørøst"
            in 123.75..146.25 -> "Sørøst"
            in 146.25..168.75 -> "Sør-sørøst"
            in 168.75..191.25 -> "Sør"
            in 191.25..213.75 -> "Sør-sørvest"
            in 213.75..236.25 -> "Sørvest"
            in 236.25..258.75 -> "Vest-sørvest"
            in 258.75..281.25 -> "Vest"
            in 281.25..303.75 -> "Vest-nordvest"
            in 303.75..326.25 -> "Nordvest"
            in 326.25..348.75 -> "Nord-nordvest"
            else -> "Ukjent" // Dette fanger opp eventuelle rare tilfeller
        }
        return direction
    }

    /*
    * Denne funksjonen gir oss neste hele 6. times klokkeslett. Den brukes i værvarselet hvor vi
    * viser værdata med 6-timers intervaller. Dette var en nyttig funksjon for å hele intervallet
    * som dataen gjelder for
    */
    fun nextTime(time: String?): String {

        val hours = time?.substring(0, 2)?.toInt()

        if (hours != null) {
            return when{
                hours < 6 -> "06"
                hours < 12 -> "12"
                hours < 18 -> "18"
                else -> "00" // Etter kl 18 og før midnatt går vi til "00"
            }
        }
        return "00"
    }

    /*
    * Denne funksjonen henter brukerens gjeldende lokasjonen til brukeren med høy nøyaktighet
    */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { loc ->
                continuation.resume(loc)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }

            // Avslutt fusedLocationClient når suspendCancellableCoroutine fullføres
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
            }
        }
    }
}
