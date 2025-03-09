package no.uio.ifi.in2000.warsamea.havvarsel.ui.boat

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.warsamea.havvarsel.R
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.LocationVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.oceanforecast.OceanVariables
import no.uio.ifi.in2000.warsamea.havvarsel.ui.DataBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.DataBoxWindDirection
import no.uio.ifi.in2000.warsamea.havvarsel.ui.IconsToDisplay
import no.uio.ifi.in2000.warsamea.havvarsel.ui.InfoBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextForDate
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextForValues
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TextToDisplay
import no.uio.ifi.in2000.warsamea.havvarsel.ui.TopAppBarScreen
import no.uio.ifi.in2000.warsamea.havvarsel.ui.UiLine
import no.uio.ifi.in2000.warsamea.havvarsel.ui.WeatherAlertsBox
import no.uio.ifi.in2000.warsamea.havvarsel.ui.ViewModel
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.GradientType
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.backgroundColor
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.dimens
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.gradientBackground

/*
* Dette er hovedfunksjonen vår som vi sender inn i MainActivity.kt. Denne sørger for å kalle alle funksjonene som vi har laget nedenfor.
* Aller først vil den bruke viewmodel til å kalle funksjonen loadWeatherData for å hente værdata.
* Deretter kaller den på TopAppBarScreen og WeatherAlertsBox fra UtilityComponents.kt og WeatherInfoColumn som er definert lenger nede
* */
@Composable
fun BoatScreen(
    boatViewModel: ViewModel = viewModel(),
    navController: NavController,
    weatherData: AllAPIVariables?
) {
    var showMoreAlerts by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) } // Loading state

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground(gradientType = GradientType.LOADING_GRADIENT))
                .wrapContentSize(align = Alignment.Center)
        ) {
            CircularProgressIndicator(color = Color.White)
        }

        if (weatherData != null) {
            isLoading = false
        }
    } else {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ) {
            TopAppBarScreen(
                titleLeft = "Tilbake",
                titleRight = "Kartskjerm",
                iconLeft = Icons.AutoMirrored.Filled.ArrowBack,
                iconRightId = R.drawable.map_location_pin_svgrepo_com,
                onLeftClicked = { navController.popBackStack() },
                onRightClicked = { navController.navigate("home") },
                shouldShowRightIcon = true
            )

            WeatherAlertsBox(
                allAPI = weatherData,
                showAlerts = showMoreAlerts,
                onToggle = { showMoreAlerts = !showMoreAlerts }
            )

            WeatherBoatBoxesColumn(
                boatViewModel,
                weatherData
            )
        }
    }
}

/*
* Denne funksjonen lager selve layouten til skjermen vår.
* Det er denne som sørger for å vise navnet på stedet på toppen, deretter en instans av funksjonen InfoBox som du kan finne i UtilityComponents.kt
* For denne skjermen får vi sjøtemperatur, vind og bølgehøyde i InfoBox.
*
* Deretter vises samtlige databokser gjennom funksjonen DataBox i UtilityComponents.kt som viser data i sanntid for tåke, bølgehastighet og vindretning
* Merk at databoksen for vindretning er en egendefinert funksjon kalt DataBoxWinddirection i UtilityComponents.kt
*
* Vi kaller også på BoatWeatherbox funksjonen som gir oss selve værmeldinga
*
* Denne funksjonen sørger for å flette all denne dataen sammen i en enkel layout
*/
@Composable
private fun WeatherBoatBoxesColumn(
    boatViewModel: ViewModel,
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
                text = boatViewModel.getLocationName(weatherData),
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
                    Pair(R.drawable.water_temp, "${weatherData?.ocean?.seaWaterTemperature ?: "-"} °C"),
                    Pair(R.drawable.air_icon, "${weatherData?.location?.windSpeed ?: "-"} (${weatherData?.location?.windSpeedOfGust ?: "-"}) m/s"),
                    Pair(R.drawable.waves_icon, "${weatherData?.ocean?.seaSurfaceWaveHeight ?: "-"} m")
                ),
                modifier = Modifier.padding(all = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimens.smallmorescreen1,
                        vertical = MaterialTheme.dimens.extraSmallmorescreen
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DataBox(
                    label = "Tåke",
                    value = "${weatherData?.location?.hourForHour?.get(0)?.fogAreaFraction.toString()} %",
                    iconId = R.drawable.fog,
                    gradientType = GradientType.FOG_GRADIENT,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                DataBox(
                    label = "Hastighet",
                    value = "${weatherData?.ocean?.hourForHour?.get(0)?.seaWaterSpeed.toString()} m/s",
                    iconId = R.drawable.water_svgrepo_com,
                    gradientType = GradientType.WAVES_GRADIENT,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimens.smallmorescreen1,
                        vertical = MaterialTheme.dimens.extraSmallmorescreen
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DataBoxWindDirection(
                    weatherData = weatherData?.location,
                    boatViewModel = boatViewModel,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = MaterialTheme.dimens.small1)
                )
            }
        }

        item {
            BoatWeatherbox(
                weatherData = weatherData?.location,
                oceanData = weatherData?.ocean
            )
        }
    }
}

/*
* Denne funksjonen sørger for å få frem selve værmeldinga som vises i små bokser på skjermen.
* Værmeldingen vises for 52 neste timer
* Her får vi data for sjøtemperatur, vind og bølgehøyde.
*/
@Composable
private fun BoatWeatherbox(
    weatherData: LocationVariables?,
    oceanData: OceanVariables?,
) {
    var todayDate = weatherData?.sixHours?.get(0)?.date
    var first = true

    weatherData?.hourForHour?.forEach {
        if (first || (it.time == "00" && it.date != todayDate)) {
            todayDate = it.date
            first = false
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = MaterialTheme.dimens.extraSmallmorescreen,
                        bottom = MaterialTheme.dimens.extraSmallmorescreen,
                        start = MaterialTheme.dimens.boatMove
                    ),

                ) {
                TextToDisplay(title = "Vanntemperatur")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen3))
                TextToDisplay(title = "Vind")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen3))
                TextToDisplay(title = "Bølgehøyde")
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen2))
            }
            Box(
                modifier = Modifier
                    .background(
                        gradientBackground(gradientType = GradientType.BOAT_GRADIENT),
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
                        IconsToDisplay(iconId = R.drawable.water_temp)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen2))
                        IconsToDisplay(iconId = R.drawable.air_icon)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.mediummorescreen2))
                        IconsToDisplay(iconId = R.drawable.waves_icon)
                    }

                    for ((index, oceanTime) in oceanData?.hourForHour?.withIndex()!!) {
                        if (weatherData.hourForHour.size == index) break
                        if (todayDate == oceanTime.date) {
                            Row {
                                UiLine()
                            }
                            Row {
                                TextForValues(title = oceanTime.time)
                                Spacer(modifier = Modifier.width(MaterialTheme.dimens.imageWidth)) // Legg til et mellomrom
                                TextForValues(title = "${oceanTime.seaWaterTemperature} °C", modifier = Modifier.weight(2f))
                                TextForValues(title = " ${weatherData.hourForHour[index].windSpeed} m/s ", modifier = Modifier.weight(2f))
                                TextForValues(title = "   ${oceanTime.seaSurfaceWaveHeight} m", modifier = Modifier.weight(2f))
                            }
                        }
                    }
                }
            }
        }
    }
}
