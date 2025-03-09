package no.uio.ifi.in2000.warsamea.havvarsel.ui.home

import android.Manifest
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.warsamea.havvarsel.R
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.ui.ViewModel
import no.uio.ifi.in2000.warsamea.havvarsel.ui.symbol.AlertsSymbol.Companion.getSymbol
import no.uio.ifi.in2000.warsamea.havvarsel.ui.symbol.WeatherSymbol
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.GradientType
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.backgroundColor
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.dimens
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.gradientBackground
import java.net.URLEncoder

/*
* Denne komposable funksjonen er hovedskjermen for appen, hvor brukeren kan interagere med et kart
* og se værdata basert på valgt sted. NavController brukes for navigasjon mellom skjermer i appen,
* og HomeViewModel for å håndtere tilstandslogikk og datahenting.
*
* WeatherInfoCard er funskjonen som brukes for å vise det lille infokortet som dukker på skjermen
* ved interaksjon med kartet.
*/
@Composable
fun HomeScreen(navController: NavController, homeViewModel: ViewModel = viewModel()) {
    val weatherDataUiState by homeViewModel.weatherApiUiState.collectAsState()
    var lat by remember { mutableDoubleStateOf(59.91) }
    var lon by remember { mutableDoubleStateOf(10.75) }
    var isMapClicked by remember { mutableStateOf(false) }
    var isInfoCardExpanded by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Vis kartet
        MapScreen(
            onMapClicked = { latLng ->
                lat = latLng.latitude
                lon = latLng.longitude
                homeViewModel.loadWeatherData(lat, lon)
                isMapClicked = true
                // Utvid infokortet når en ny plassering er valgt
                isInfoCardExpanded = true
            },
            onLocationButtonClicked = {
                isInfoCardExpanded = false
            }
        )
        // Søkefelt for sted eller koordinater
        SearchBarSample(navController = navController, homeViewModel = homeViewModel)

        // Værinformasjonskortbeholder
        if (weatherDataUiState.weatherApi != null && isMapClicked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                WeatherInfoCard(
                    locationName = homeViewModel.getLocationName(weatherDataUiState.weatherApi),
                    weatherDataUiState = weatherDataUiState.weatherApi,
                    navController = navController,
                    isExpanded = isInfoCardExpanded, // Pass den utvidede tilstanden
                    onExpandToggle = { expanded ->
                        isInfoCardExpanded = expanded // Oppdater den utvidede tilstanden
                    }
                )
            }
        }
    }
}

/*
* Funksjonen WeatherInfoCard er designet for å vise detaljert værinformasjon på en brukervennlig måte.
* Komponenten bruker en Column for å organisere innhold vertikalt, delt inn i to hovedseksjoner
* gjennom Box-komponenter. Den øvre boksen fungerer som et headerområde som viser stedsnavnet,
* en utvid/kollaps-ikon som reagerer på brukerinteraksjon for å vise eller skjule detaljert
* værdata, og tilbyr en navigasjonsmekanisme ved å klikke på teksten for mer detaljert informasjon.
*
* Den nedre boksen vises kun når informasjonen er utvidet og inneholder dynamisk generert værdata som temperatur,
* vindhastighet, og mer, alt presentert med relevante ikoner.
*
* Denne tilnærmingen gjør det enkelt for brukeren å få en rask oversikt samt dybdeinformasjon om værforholdene,
* og styrer også interaktiv navigasjon basert på brukerens valg.
*/
@Composable
private fun WeatherInfoCard(
    locationName: String,
    weatherDataUiState: AllAPIVariables?,
    isExpanded: Boolean,
    onExpandToggle: (Boolean) -> Unit,
    navController: NavController
) {
    val json = Json { encodeDefaults = true }
    val weatherDataUiStateJson: String = if (weatherDataUiState != null) {
        json.encodeToString(AllAPIVariables.serializer(), weatherDataUiState)
    } else {
        ""
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Øvre boks for header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
                .background(
                    (gradientBackground(gradientType = GradientType.LOADING_GRADIENT)),
                    shape = MaterialTheme.shapes.medium.copy(
                        topEnd = CornerSize(MaterialTheme.dimens.large),
                        topStart = CornerSize(MaterialTheme.dimens.large),
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                )
                .padding(MaterialTheme.dimens.small1),
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                tint = Color.White,
                contentDescription = "Disband",
                modifier = Modifier
                    .size(MaterialTheme.dimens.medium2)
                    .align(Alignment.TopCenter)
                    .rotate(if (isExpanded) 0f else 180f) // Roter pilikonet hvis klikket
                    .clickable {
                        onExpandToggle(!isExpanded) // Veksle mellom utvidet tilstand
                    }
            )
            Text(
                text = locationName,
                fontWeight = FontWeight.ExtraBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = MaterialTheme.dimens.small3)
                    .align(Alignment.Center)
                    .clickable {
                        navController.navigate(
                            "moreScreen/${
                                URLEncoder.encode(
                                    weatherDataUiStateJson,
                                    "UTF-8"
                                )
                            }"
                        )
                    }

            )
            Text(
                text = "Trykk for mer info",
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clickable {
                        navController.navigate(
                            "moreScreen/${
                                URLEncoder.encode(
                                    weatherDataUiStateJson,
                                    "UTF-8"
                                )
                            }"
                        )
                    }

            )
            // Rad for værikon basert på awarenessType og awarenessLevel
            weatherDataUiState?.let { weatherData ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = MaterialTheme.dimens.medium1, bottom = MaterialTheme.dimens.small1)
                ) {
                    // Vis passende værikon basert på awarenessType og awarenessLevel
                    val awarenessType = weatherData.alerts?.awarenessType
                    val awarenessLevel = weatherData.alerts?.awarenessLevel
                    val iconResourceId = getSymbol(awarenessLevel, awarenessType)

                    if (awarenessType != null && awarenessLevel != null) {
                        Log.d(
                            "WeatherAlert",
                            "Farevarsel i området - Type: $awarenessType, Nivå: $awarenessLevel"
                        )
                    } else {
                        Log.d("WeatherAlert", "Ingen farevarsel i området")
                    }

                    if (iconResourceId != null) {
                        Image(
                            painter = painterResource(id = iconResourceId),
                            contentDescription = "Vær ikon",
                            alignment = Alignment.CenterStart,
                            modifier = Modifier
                                .size(MaterialTheme.dimens.iconSize)
                        )
                    }
                }
            }
        }
        //Nedre boks for værdata
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .background(
                        (gradientBackground(gradientType = GradientType.BOAT_GRADIENT)),
                    )
                    .padding(MaterialTheme.dimens.small1)
                    .heightIn(
                        min = 0.dp,
                        Dp.Unspecified
                    )
            ) {
                weatherDataUiState?.let { weatherData ->
                    Column {
                        // Temperaturrad
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = MaterialTheme.dimens.small1,
                                    bottom = MaterialTheme.dimens.small1)
                        ) {
                            // Finn riktig enum-verdi basert på symbolkoden
                            val symbol =
                                WeatherSymbol.fromSymbolCode(weatherData.location?.iconHours)
                            Log.d(
                                "WeatherInfoCard",
                                "Symbol code: ${weatherData.location?.iconHours}, Symbol: $symbol"
                            )
                            val symbolResourceId = symbol.resourceId
                            Image(
                                painter = painterResource(id = symbolResourceId),
                                contentDescription = "Weather Symbol",
                                modifier = Modifier.size(MaterialTheme.dimens.iconSize)
                            )
                            Text(
                                text = "${weatherData.location?.airTemperature} °C",
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 125.dp)
                            )
                        }
                        // Linje mellom radene
                        Row {
                            Canvas(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)) {
                                drawLine(
                                    color = backgroundColor,
                                    strokeWidth = 5f,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, y = 0f)
                                )
                            }
                        }
                        // Vindhastighetsrad
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.air_icon),
                                contentDescription = "Wind speed",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.iconSize)
                            )
                            Text(
                                text = "${weatherData.location?.windSpeed} m/s",
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 68.dp)
                                    .weight(1f) // Bruk vekt for å fylle resten av tilgjengelig plass
                            )
                            val windDirection = weatherData.location?.hourForHour?.get(0)?.windFromDirection ?: 0// vindretning
                            // Henter vindretningen, default til 0 hvis det ikke er tilgjengelig
                            Icon(
                                painter = painterResource(R.drawable.arrow), // Endre dette til ID-en for pilikonet ditt
                                contentDescription = "Wind direction",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.iconSize) // Endre størrelsen etter behov
                                    .padding(end = 8.dp) // Legg til padding for å justere avstanden
                                    .rotate(windDirection.toFloat() - 90) // Roter pilikonet basert på vindretningen
                                // Gir feil data hvis ikke - 90
                            )
                        }

                        // Linje mellom radene
                        Row {
                            Canvas(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)) {
                                drawLine(
                                    color = backgroundColor,
                                    strokeWidth = 5f,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, y = 0f)
                                )
                            }
                        }

                        // Bølgehøyde eller regnrad
                        val seaSurfaceWaveHeight = weatherData.ocean?.seaSurfaceWaveHeight
                        if (seaSurfaceWaveHeight != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.waves_icon),
                                    contentDescription = "waves",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(MaterialTheme.dimens.iconSize)
                                )
                                Text(
                                    text = "$seaSurfaceWaveHeight m",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 125.dp)
                                )
                            }
                        } else {
                            val rain = weatherData.location?.precipitationAmount
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rain),
                                    contentDescription = "Rain",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(MaterialTheme.dimens.iconSize)
                                        .padding(bottom = MaterialTheme.dimens.small1)
                                )
                                Text(
                                    text = "${"%.1f".format(rain)} mm",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 125.dp)
                                )
                            }
                        }

                        // Linje mellom radene
                        Row {
                            Canvas(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)) {
                                drawLine(
                                    strokeWidth = 5f,
                                    color = backgroundColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, y = 0f)
                                )
                            }
                        }

                        // Vanntemp eller UV-indeksrad
                        val seaWaterTemperature = weatherData.ocean?.seaWaterTemperature
                        if (seaWaterTemperature != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.water_temp),
                                    contentDescription = "waves",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(MaterialTheme.dimens.iconSize)
                                )
                                Text(
                                    text = "$seaWaterTemperature °C",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 125.dp)
                                )
                            }
                        } else {
                            val uvIndex = weatherData.location!!.ultravioletIndexClearSky
                            val uvLevelText = when (uvIndex) {
                                in 0.0..2.9 -> "lavt nivå"
                                in 3.0..5.9 -> "moderat"
                                in 6.0..7.9 -> "høyt nivå"
                                in 8.0..10.9 -> "svært høy"
                                else -> "ekstremt nivå"
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.uv_symbol),
                                    contentDescription = "UV index",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(MaterialTheme.dimens.iconSize)
                                )
                                Text(
                                    text = "$uvIndex",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 125.dp)
                                )
                                Text(
                                    text = uvLevelText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = MaterialTheme.dimens.small1), // Juster padding her for å justere avstanden
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/*
* MapScreen er en composable funksjon som viser et interaktivt kart hvor brukeren kan velge
* en lokasjon ved å klikke på kartet. Denne handlingen vil utløse onMapClicked, som er en
* funksjon som håndterer videre logikk basert på brukerens valg av lokasjon. Funksjonen henter også
* inn lokasjonen til brukeren og sentrerer kartet over brukerens posisjon. Hvis man ikke vil gi tillatelsen
* til lokasjonen, så vil kartet sentreres over Oslo.
*/
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onMapClicked: (LatLng) -> Unit,
    onLocationButtonClicked: () -> Unit
) {
    val homeViewModel: ViewModel = viewModel()
    val context = LocalContext.current
    val oslo = LatLng(59.91, 10.75)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val coroutineScope = rememberCoroutineScope()

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var showMap by remember { mutableStateOf(false) }

    // Initialiser cameraPositionState med standardposisjon over Oslo
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(oslo, 12f)
    }

    // Request location permission when the screen is composed
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // Update camera position based on permission state
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            val location = homeViewModel.getCurrentLocation(context)
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 14f)
            }
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(oslo, 12f)
        }
        // Delay showing the map to wait for location update
        kotlinx.coroutines.delay(2500) // Wait for 2 seconds
        showMap = true
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        )
    }

    val properties = remember {
        MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = locationPermissionState.status.isGranted
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapClick = { latLng ->
                markerPosition = latLng
                onMapClicked(latLng)
            }
        )

        // Vis markør ved den valgte punktet hvis markerPosition er ikke null
        markerPosition?.let { _ ->
            Box(
                modifier = Modifier
                    .offset(
                        (-MaterialTheme.dimens.small3),
                        (-MaterialTheme.dimens.medium3)
                    ) // Juster markørposisjonen
                    .align(Alignment.Center)
            ) {
                // Bruk ikon for markør
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Marker klikk punkt",
                    modifier = Modifier.size(MaterialTheme.dimens.medium3),
                    tint = colorResource(R.color.backgroundColor)
                )
            }
        }

        // Lokasjonsknapp for å sentrere kartet på brukerens posisjon
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    onLocationButtonClicked()
                    if (locationPermissionState.status.isGranted) {
                        val location = homeViewModel.getCurrentLocation(context)
                        location?.let {
                            val userLocation = LatLng(it.latitude, it.longitude)
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))
                            markerPosition = null
                        }
                    } else {
                        // Be om tillatelse igjen hvis brukeren trykker på knappen og tillatelsen ikke er gitt
                        locationPermissionState.launchPermissionRequest()
                        markerPosition = null
                    }
                }
            },
            modifier = Modifier
                .padding(end = MaterialTheme.dimens.small2,
                    top = MaterialTheme.dimens.large + MaterialTheme.dimens.small3)
                .size(MaterialTheme.dimens.medium3)
                .clip(CircleShape)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "Min lokasjon knapp",
                tint = colorResource(R.color.backgroundColor),
                modifier = Modifier
                    .size(MaterialTheme.dimens.medium2)
            )
        }
    }
    // Sentrer kameraet rundt markørposisjonen
    LaunchedEffect(markerPosition) {
        markerPosition?.let { position ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(position, 12f))
        }
    }
}

/*
* Jetpack Compose-funksjonen SearchBarSample, benytter en kombinasjon av state management
* og API-integrasjon for å gi en interaktiv søkeopplevelse. Funksjonen håndterer brukerinput via
* en SearchBar, hvor brukere kan skrive innG stedsnavn eller koordinater. State-variabler som text, isError,
* og errorMessage lagres og oppdateres dynamisk for å bevare tilstand gjennom konfigurasjonsendringer og gi
* tilbakemeldinger til brukeren ved feil. PlacesClient brukes til å hente forslag og stedsdetaljer fra Google Places API, // muligens fjernes
* og integrasjonen håndterer både suksessfulle og feilslåtte API-kall ved å vise passende feilmeldinger eller
* navigere til en detaljert værskjerm basert på søkte koordinater. Denne funksjonaliteten er innkapslet i en
* Box som bruker material design prinsipper fra MaterialTheme for å sikre en konsistent og brukervennlig UI.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarSample(navController: NavController, homeViewModel: ViewModel = viewModel()) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var placesClient: PlacesClient? by remember { mutableStateOf(null) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }
    val context = LocalContext.current
    var predictions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }

    // LaunchedEffect for å tilbakestille tekst ved retur fra MoreScreen
    LaunchedEffect(navController.currentBackStackEntryAsState().value) {
        if (navController.currentBackStackEntry?.destination?.route == "home") {
            text = "" // Tilbakestill tekst ved retur til HomeScreen
        }
    }

    LaunchedEffect(key1 = Unit) {
        placesClient = Places.createClient(context)
    }

    fun setIsError(value: Boolean) {
        isError = value
    }

    fun setErrorMessage(message: String?) {
        errorMessage = message
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimens.small3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Search bar
            SearchBar(
                query = text,
                onQueryChange = { query ->
                    text = query
                    setIsError(false) // Tilbakestill feiltilstand når spørringen endres
                    setErrorMessage(null) // Fjern feilmelding når spørringen endres

                    // Hent alternativer når søket endres
                    if (query.isNotEmpty()) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(sessionToken)
                            .setQuery(query)
                            .build()

                        placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
                            predictions = response.autocompletePredictions
                        }?.addOnFailureListener { exception ->
                            setIsError(true)
                            setErrorMessage("Kunne ikke finne stedsforslag: ${exception.message}")
                        }
                    } else {
                        predictions = emptyList()
                    }
                },
                onSearch = {
                    // Perform search action
                    handleSearch(navController, homeViewModel, text, placesClient, sessionToken, ::setIsError, ::setErrorMessage)
                    active = false
                },
                active = active,
                onActiveChange = {
                    active = it
                    if (!active) {
                        text = "" // Tøm tekst når søkefeltet er deaktivert
                        setIsError(false) // Tilbakestill feiltilstand ved avbryting av søk
                        setErrorMessage(null) // Fjern feilmelding når du avbryter søk
                        predictions = emptyList() // Slett alternativer når søkefeltet er deaktivert
                    }
                },
                placeholder = { Text("Søk sted, eller trykk på kart") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (active) {
                        IconButton(onClick = {
                            active = false
                            text = "" // Tøm tekst når avbryt knappen klikkes
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    }
                },
            ) {
                // Vis alternativer for autofullføring
                if (predictions.isNotEmpty() && active) {
                    LazyColumn {
                        items(predictions) { prediction ->
                            ListItem(
                                headlineContent = { Text(prediction.getFullText(null).toString()) },
                                modifier = Modifier.clickable {
                                    text = prediction.getFullText(null).toString()
                                    predictions = emptyList()
                                    handleSearch(navController, homeViewModel, text, placesClient, sessionToken, ::setIsError, ::setErrorMessage)
                                    active = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun handleSearch(
    navController: NavController,
    homeViewModel: ViewModel,
    query: String,
    placesClient: PlacesClient?,
    sessionToken: AutocompleteSessionToken,
    setIsError: (Boolean) -> Unit,
    setErrorMessage: (String?) -> Unit
) {
    if (homeViewModel.isValidCoordinates(query)) {
        val coordinates = query.split(Regex("[,\\s]+"))
        if (coordinates.size == 2) {
            val lat = coordinates[0].toDoubleOrNull() ?: 0.0
            val lon = coordinates[1].toDoubleOrNull() ?: 0.0
            navController.navigate("moreScreenFromSearch/$lat/$lon")
        } else {
            setIsError(true)
            setErrorMessage("Ugyldige koordinater")
        }
    } else {
        // Håndteres når du søker etter stedsnavn
        val locationName = query.trim()
        if (locationName.isNotEmpty()) {
            // Bruk Google Places API for å få plass alternativer
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(locationName)
                .build()

            placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
                if (response != null && response.autocompletePredictions.isNotEmpty()) {
                    val placeId = response.autocompletePredictions[0].placeId
                    // Bruk steds-ID-en for å hente posisjonsdetaljer
                    placesClient.fetchPlace(FetchPlaceRequest.newInstance(placeId, listOf(
                        Place.Field.LAT_LNG))).addOnSuccessListener { fetchPlaceResponse ->
                        val latLng = fetchPlaceResponse.place.latLng
                        if (latLng != null) {
                            val lat = latLng.latitude
                            val lon = latLng.longitude
                            navController.navigate("moreScreenFromSearch/$lat/$lon")
                        } else {
                            setIsError(true)
                            setErrorMessage("Kunne ikke hente plasseringskoordinater")
                        }
                    }.addOnFailureListener { exception ->
                        setIsError(true)
                        setErrorMessage("Kunne ikke hente stedsdetaljer: ${exception.message}")
                    }
                } else {
                    setIsError(true)
                    setErrorMessage("Fant ingen samsvarende steder")
                }
            }?.addOnFailureListener { exception ->
                setIsError(true)
                setErrorMessage("Kunne ikke finne stedsforslag: ${exception.message}")
            }
        } else {
            setIsError(true)
            setErrorMessage("Vennligst skriv inn et sted")
        }
    }
}
