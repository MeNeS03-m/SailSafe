package no.uio.ifi.in2000.warsamea.havvarsel.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.warsamea.havvarsel.R
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.LocationVariables
import no.uio.ifi.in2000.warsamea.havvarsel.ui.DataBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.IconsToDisplay
import no.uio.ifi.in2000.warsamea.havvarsel.ui.InfoBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextForDate
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextForValues
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextToDisplay
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TopAppBarScreen
import no.uio.ifi.in2000.warsamea.havvarsel.ui.UiLine
import no.uio.ifi.in2000.warsamea.havvarsel.ui.ViewModel
import no.uio.ifi.in2000.warsamea.havvarsel.ui.WeatherAlertsBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.GradientType
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.backgroundColor
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.dimens
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.gradientBackground
import java.net.URLEncoder

/*
* Dette er hovedfunksjonen vår som vi sender inn i MainActivity.kt. Denne sørger for å kalle alle funksjonene som vi har laget nedenfor.
* Aller først vil den bruke viewmodel til å kalle funksjonen loadWeatherData for å hente værdata.
* Deretter kaller den på TopAppBarScreen og WeatherAlertsBox fra UtilityComponents.kt og WeatherInfoColumn som er definert lenger nede
* */
@Composable
fun MoreScreen(
    moreViewModel: ViewModel = viewModel(),
    lat: Double,
    lon: Double,
    navController: NavController,
    weatherData: AllAPIVariables?,
    search: Boolean
) {
    val allAPIDataUiState by moreViewModel.weatherApiUiState.collectAsState()
    var showMoreAlerts by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val weatherApiState = moreViewModel.weatherApiUiState.collectAsState().value.weatherApi

    LaunchedEffect(search) {
        moreViewModel.loadWeatherData(lat, lon)
    }

    val json = Json { encodeDefaults = true }
    val weatherDataUiStateJson: String = if (weatherData != null && !search) {
        json.encodeToString(AllAPIVariables.serializer(), weatherData)
    } else if(allAPIDataUiState.weatherApi != null && search) {
        json.encodeToString(AllAPIVariables.serializer(), allAPIDataUiState.weatherApi!!)
    } else { "" }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground(gradientType = GradientType.LOADING_GRADIENT))
                .wrapContentSize(align = Alignment.Center)
        ) {
            CircularProgressIndicator(color = Color.White)
        }

        if (allAPIDataUiState.weatherApi != null || weatherData != null) {
            isLoading = false
        }

    } else {
        Column(
            modifier = Modifier
                .background(colorResource(R.color.backgroundColor))
                .fillMaxSize()
        ) {
            TopAppBarScreen(
                titleLeft = "Tilbake",
                titleRight = "Båtskjerm",
                iconLeft = Icons.AutoMirrored.Filled.ArrowBack,
                iconRightId = R.drawable.boat_svgrepo_com,
                onLeftClicked = { navController.popBackStack() },
                onRightClicked = { navController.navigate("boatScreen/${URLEncoder.encode(weatherDataUiStateJson, "UTF-8")}")},
                shouldShowRightIcon = if(search) weatherApiState?.ocean?.hourForHour.isNullOrEmpty().not() else weatherData?.ocean?.hourForHour.isNullOrEmpty().not()
            )
            WeatherAlertsBox(
                allAPI = if(search) allAPIDataUiState.weatherApi else weatherData,
                showAlerts = showMoreAlerts,
                onToggle = { showMoreAlerts = !showMoreAlerts }
            )
            WeatherInfoColumn(
                moreViewModel,
                if(search) allAPIDataUiState.weatherApi else weatherData
            )
        }
    }
}

/*
* Denne funksjonen lager selve layouten til skjermen vår.
* Det er denne som sørger for å vise navnet på stedet på toppen, deretter en instans av funksjonen InfoBox som du kan finne i UtilityComponents.kt
* For denne skjermen får vi temperatur, vind og vindkast og nedbørsmengde i InfoBox.
*
* Deretter vises samtlige databokser gjennom funksjonen DataBox i UtilityComponents.kt som viser data i sanntid for UV-indeks og fuktighet
* Vi kaller også på WeatherBox funksjonen som gir oss selve værmeldinga
* Denne funksjonen sørger for å flette all denne dataen sammen i en enkel layout
*/
@Composable
private fun WeatherInfoColumn(
    moreViewModel: ViewModel,
    weatherData: AllAPIVariables?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(
                horizontal = MaterialTheme.dimens.mediummorescreen1,
                vertical = MaterialTheme.dimens.smallmorescreen1
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.smallmorescreen1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = moreViewModel.getLocationName(weatherData),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
            )
        }

        item {
            InfoBox(
                title = "Været nå",
                iconDataPairs = listOf(
                    Pair(R.drawable.sunny_icon, "${weatherData?.location?.airTemperature ?: "-"} °C"),
                    Pair(R.drawable.air_icon, "${weatherData?.location?.windSpeed ?: "-"} (${weatherData?.location?.windSpeedOfGust ?: "-"}) m/s"),
                    Pair(R.drawable.rain, String.format("%.1f", weatherData?.location?.precipitationAmount ?: 0.0) + " mm")
                ),
                modifier = Modifier.padding(all = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.smallmorescreen1,
                        vertical = MaterialTheme.dimens.extraSmallmorescreen,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                    DataBox(
                        label = "UV-indeks",
                        value = weatherData?.location?.hourForHour?.get(0)?.ultravioletIndexClearSky.toString(),
                        iconId = R.drawable.uv_symbol,
                        gradientType = GradientType.UV_GRADIENT,
                        modifier = Modifier
                            .weight(1f) // Gir like mye plass til hver boks
                            .padding(end = 4.dp) // Liten avstand mellom boksene
                    )
                    DataBox(
                        label = "Fuktighet",
                        value = "${weatherData?.location?.hourForHour?.get(0)?.humidity} %",
                        iconId = R.drawable.humidity,
                        gradientType = GradientType.HUMIDITY_GRADIENT,
                        modifier = Modifier
                            .weight(1f) // Gir like mye plass til hver boks
                            .padding(start = 4.dp) // Liten avstand mellom boksene
                    )
                }
            }

        item {
            Weatherbox(
                weatherData = weatherData?.location,
                moreViewModel = moreViewModel
            )
        }
    }
}

/*
* Denne funksjonen sørger for å få frem selve værmeldinga som vises i små bokser på skjermen.
* Værmeldingen vises fro de 10 neste dagene med 6-timers intervaller.
* Her får vi data for temperatur, vind og nedbørsmengde.
*/
@Composable
private fun Weatherbox(
    weatherData: LocationVariables?,
    moreViewModel: ViewModel
) {
    var todayDate = weatherData?.sixHours?.get(0)?.date
    var first = true

    weatherData?.sixHours?.forEach { it ->
        if (first || (it.time == "00" && it.date != todayDate)) {
            todayDate = it.date
            first = false
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = MaterialTheme.dimens.extraSmallmorescreen,
                        bottom = MaterialTheme.dimens.extraSmallmorescreen,
                        start = MaterialTheme.dimens.moreMove
                    ),
            ) {
                TextToDisplay(title = "Temperatur")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen3))
                TextToDisplay(title = "Vind")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen1))
                TextToDisplay(title = "Nedbør")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen2))
            }
            Box(
                modifier = Modifier
                    .background(
                        gradientBackground(gradientType = GradientType.WEATHER_GRADIENT),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(
                        vertical = MaterialTheme.dimens.smallmorescreen3,
                        horizontal = MaterialTheme.dimens.smallmorescreen3
                    )
                    .fillMaxWidth() // Fyll bredden av foreldreelementet
            ) {

                Column (modifier = Modifier.fillMaxSize()) {

                    Row {
                        TextForDate(title = todayDate)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen1.times(2)))
                        IconsToDisplay(iconId = R.drawable.temp_icon)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen2))
                        IconsToDisplay(iconId = R.drawable.air_icon)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen2))
                        IconsToDisplay(iconId = R.drawable.rain)
                    }

                    weatherData.sixHours.forEach {
                        if (todayDate == it.date) {
                            Row {
                                UiLine()
                            }
                            Row {
                                TextForValues(title = "${it.time} - ${moreViewModel.nextTime(it.time)}")
                                Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen3.times(2)))
                                TextForValues(title = "${it.airTemperature}°C", modifier = Modifier.weight(7f))
                                TextForValues(title = "${it.windSpeed} m/s", modifier = Modifier.weight(7f))
                                if (it.precipitationAmountMax != null) {
                                    TextForValues(title = "${it.precipitationAmountMax} mm", modifier = Modifier.weight(7f))
                                } else {
                                    TextForValues(title = "0.0 mm", modifier = Modifier.weight(7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
