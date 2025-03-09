package no.uio.ifi.in2000.warsamea.havvarsel.ui

import no.uio.ifi.in2000.warsamea.havvarsel.ui.symbol.AlertsSymbol.Companion.getSymbol
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import coil.compose.rememberImagePainter
import no.uio.ifi.in2000.warsamea.havvarsel.R
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.data.locationforecast.LocationVariables
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.GradientType
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.backgroundColor
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.dimens
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.gradientBackground
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
* WeatherAlertsBox er en generalisert funksjon i som viser værvarsler på en dynamisk og informativ måte.
* Denne komponenten reagerer på værdata, hvor brukere kan se varseltype og nivå med tilhørende ikoner.
* Når brukerne aktiverer detaljevisning ved å klikke på et toggle-ikon, presenteres de med utdypende
* informasjon om varselet, inkludert anbefalinger, konsekvenser, og detaljerte områdebeskrivelser.
*
* Dette sikrer at brukeren får en klar og lettforståelig oversikt over værsituasjonen,
* tilpasset til å være intuitiv og brukervennlig.
*
* Denne funksjonen blir brukt i både MoreScrren.kt og BoatScreen.kt
*/
@Composable
fun WeatherAlertsBox(
    allAPI: AllAPIVariables?,
    showAlerts: Boolean,
    onToggle: () -> Unit
) {
    val context = LocalContext.current

    if (allAPI?.alerts != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.dimens.smallmorescreen1)
                .background(backgroundColor),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                val awarenessType = allAPI.alerts.awarenessType
                val awarenessLevel = allAPI.alerts.awarenessLevel
                val iconResourceId = getSymbol(awarenessLevel, awarenessType)
                if (iconResourceId != null) {
                    Image(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = "Alert Icon",
                        modifier = Modifier.size(MaterialTheme.dimens.iconSizealertsMoreScreen)
                    )
                }
                Icon(
                    imageVector = if (showAlerts) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                    contentDescription = "Toggle Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.iconSizeMoreScreen)
                        .clickable { onToggle() }
                )
            }
        }
    }

    if (showAlerts) {
        allAPI?.alerts?.let { alert ->
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .padding(MaterialTheme.dimens.mediummorescreen1)
                    .verticalScroll(rememberScrollState())
            ) {
                // Anbefalinger
                Text(
                    text = "Anbefalinger",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                )
                alert.instruction?.split(".")?.forEachIndexed { index, sentence ->
                    val isLastSentence = index == alert.instruction.split(".").size - 1
                    Text(
                        text = if (isLastSentence) sentence else "• $sentence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(
                                bottom = MaterialTheme.dimens.smallmorescreen1,
                                start = MaterialTheme.dimens.mediummorescreen1
                            )
                    )
                }

                // Konsekvenser
                Text(
                    text = "Konsekvenser",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(
                            bottom = MaterialTheme.dimens.smallmorescreen1,
                            top = MaterialTheme.dimens.mediummorescreen1
                        )
                )
                alert.consequences?.split(".")?.forEachIndexed { index, sentence ->
                    val isLastSentence = index == alert.consequences.split(".").size - 1
                    Text(
                        text = if (isLastSentence) sentence else "• $sentence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(
                                bottom = MaterialTheme.dimens.smallmorescreen1,
                                start = MaterialTheme.dimens.mediummorescreen1
                            )
                    )
                }

                Text(
                    text = "Beskrivelse",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                )

                Text(
                    text = alert.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                )

                Text(
                    text = "Område",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                )

                Text(
                    text = alert.area ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                )
                Text(
                    text = "[met.no]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                        .clickable {
                            alert.web.let { url ->
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(url)
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            }
                        }
                )

                val imageUri =
                    alert.resources?.getOrNull(1)// Henter URIen til det andre bildet hvis det finnes

                // Legg til ImageView for å vise bildet av området
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Område",
                    modifier = Modifier
                        .fillMaxSize()
                        .height(MaterialTheme.dimens.imageHeight)
                        .width(MaterialTheme.dimens.imageWidth)
                        .padding(bottom = MaterialTheme.dimens.smallmorescreen1),
                    contentScale = ContentScale.Crop
                )
            }

            // Konverterer ISO 8601-format til en lokal dato
            fun convertToLocalDateTime(isoDateString: String?): LocalDateTime? {
                return try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.parse(isoDateString, DateTimeFormatter.ISO_DATE_TIME)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            // Formatterer datoen til ønsket format
            fun formatDateTime(dateTime: LocalDateTime?): String? {
                if (dateTime == null) return null
                val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter.ofPattern("EEEE d. MMMM 'kl.' HH:mm", Locale("no"))
                } else {
                    null
                }
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dateTime.format(formatter)
                } else {
                    null
                }
            }

            val startDate = convertToLocalDateTime(alert.timePeriod?.getOrNull(0))
            val endDate = convertToLocalDateTime(alert.timePeriod?.getOrNull(1))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Tidsperiode:\n")
                    }
                    append("${formatDateTime(startDate)} - \n")
                    append("|\n") // Linjeskille med "|"
                    append("|\n") // Linjeskille med "|"
                    append("\u25BC\n") // Pilen nedover
                    if (endDate != null) {
                        append("${formatDateTime(endDate)}")
                    } else {
                        append("faren pågår")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
            )

            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(MaterialTheme.dimens.mediummorescreen1)
            ) {
                // Vis informasjon om faregradene
                alert.awarenessLevel?.split(";")?.let { levels ->
                    val (degree, _, _) = levels
                    val dangerDegree = listOf(
                        Triple("2", "Gult -", Color.Yellow),
                        Triple(
                            "3",
                            "Oransje - stor",
                            Color(0xFFFFA500)
                        ), // Her bruker jeg oransje farge for nivå 3
                        Triple("4", "Rødt - ekstrem", Color.Red)
                    )

                    dangerDegree.forEach { (degreeLevel, text, color) ->
                        val active = degree == degreeLevel
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.iconSizeMoreScreen)
                                    .background(color)
                                    .padding(MaterialTheme.dimens.extraSmallmorescreen)
                            ) {
                                // Legg til sjekkmark hvis boksen er aktiv
                                if (active) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.matchParentSize() // Endret fra fillMaxSize til matchParentSize
                                    )
                                }
                            }

                            Text(
                                text = "$text ${alert.title?.split(",")?.get(0)?.lowercase()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = MaterialTheme.dimens.smallmorescreen1)
                            )
                        }
                    }
                }
            }

            val yrUrl = stringResource(R.string.farevarsel_hjelp)

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("Les mer om nivåene på ")
                    }
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append("hjelp.yr.no")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = MaterialTheme.dimens.smallmorescreen1)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(yrUrl))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
            )
        }
    }
}

/*
* DataBox er en generel komponent, designet for å vise nøkkeldata på en visuelt tiltalende måte.
* Denne funksjonen kombinerer en tekstetikett, en verdi, og et ikon, alle satt mot en bakgrunn med gradient.
* Komponenten er strukturert som en Box med en Column og Row for å organisere innholdet sentrert.
*
* Ikonet representerer datatypen mens teksten gir en klar beskrivelse og verdi.
*
* Denne modulære oppbyggingen gjør DataBox ideell for gjenbruk over forskjellige deler av appen,
* der konsistent og effektiv informasjonsfremvisning er nødvendig.
*
* Denne funksjonen blir også brukt flere steder i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun DataBox(
    label: String,
    value: String,
    iconId: Int,
    gradientType: GradientType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                gradientBackground(gradientType = gradientType),
                shape = MaterialTheme.shapes.medium
            )
            .padding(MaterialTheme.dimens.medium1)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(MaterialTheme.dimens.iconSizeHeader)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen1))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.mediummorescreen1))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.titleSmall.fontSize),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/*
* DataBoxWindDirection er en tilpasset versjon av DataBox komponenten, spesifikt utviklet for å adressere
* visningsutfordringer med vindretningsikonet. Lignende DataBox, kombinerer denne komponenten grafiske og
* tekstlige elementer for å presentere vindretning. På grunn av tekniske problemer med pilikonets rotasjonsfunksjonalitet,
* ble det nødvendig å lage en egen funksjon for å håndtere dette spesifikke datavisningsbehovet. Vi tror det hadde vært mulig
* å få til en løsning med DataBox funksjonen over, men pga knapt med tid og mer fokus på andre deler av prosjektet ble vi nødt
* til å benytte oss av denne.
*
* Komponenten inneholder et kompassikon og en roterende pil som angir vindretning, med tekstlig beskrivelse og numerisk
* verdi for en klar og direkte visning av dataene. Denne modifikasjonen sikrer korrekt visning og brukerforståelse av
* dynamisk vindretning i værapplikasjonen.
*
* Her bruker vi funksjonen windDirection fra ViewModel.kt for å gi brukeren en enkel oppfatning av hvilken retningen vinden blåser mot
*
* Denne blir kun brukt i BoatScreen.kt
*/
@Composable
fun DataBoxWindDirection(
    weatherData: LocationVariables?,
    boatViewModel: ViewModel,
    modifier: Modifier = Modifier
) {
    val windDirection = weatherData?.hourForHour?.get(0)?.windFromDirection ?: 0 // vindretning

    Box(
        modifier = modifier
            .background(
                gradientBackground(gradientType = GradientType.WIND_GRADIENT),
                shape = MaterialTheme.shapes.medium
            )
            .padding(MaterialTheme.dimens.boxBoatScreen)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.compass_alt_1_svgrepo_com),
                    contentDescription = "Wind direction",
                    tint = Color.White,
                    modifier = Modifier.size(MaterialTheme.dimens.smallmorescreen3)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen1))
                Text(
                    text = "Vindretning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
            Text(
                text = boatViewModel.windDirection(windDirection.toDouble()), // Oppdater kall til å sende direkte double
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.titleSmall.fontSize),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
            Icon(
                painter = painterResource(R.drawable.arrow),
                contentDescription = "Wind direction",
                tint = Color.White,
                modifier = Modifier
                    .size(MaterialTheme.dimens.iconSize)
                    .padding(end = MaterialTheme.dimens.smallmorescreen1)
                    .rotate(windDirection.toFloat() - 90)
            )
        }
    }
}

/*
* TopAppBarScreen er en komponent i designet for å fungere som en topp navigasjonslinje for en app.
* Denne baren er strukturert med titler og ikoner på både venstre og høyre side, der brukeren kan
* interagere med disse ikonene gjennom klikkbare handlinger. Venstre side av baren inneholder vanligvis en tilbake-knapp,
* mens høyre side kan konfigureres til å vise et ekstra ikon basert på shouldShowRightIcon parameteren.
*
* Komponenten bruker en gradientbakgrunn for visuell appell og tekst sammen med ikoner for å fremheve interaktiviteten.
* Denne funksjonen gjør TopAppBarScreen ideell for applikasjoner som krever en klar, funksjonell,
* og estetisk tiltalende brukeropplevelse på tvers av forskjellige skjermer.
*
* Dette er navigasjonen vår så denne forekommer i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun TopAppBarScreen(
    titleLeft: String,
    titleRight: String,
    iconLeft: ImageVector,
    iconRightId: Int,
    onLeftClicked: () -> Unit,
    onRightClicked: () -> Unit,
    shouldShowRightIcon: Boolean, // Kontrollerer visning av høyre ikon
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(gradientBackground(gradientType = GradientType.WIND_GRADIENT)) // Sett bakgrunnsfarge til blå
    ) {
        // Venstre hjørne for tilbakeknapp
        Box(
            modifier = Modifier
                .padding(MaterialTheme.dimens.smallmorescreen1)
                .size(MaterialTheme.dimens.mediummorescreen3)
                .background(Color.White, shape = CircleShape) // Bruk CircleShape for en sirkel
                .clickable(onClick = onLeftClicked),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconLeft,
                contentDescription = titleLeft,
                tint = Color.Black,
                modifier = Modifier.size(MaterialTheme.dimens.mediummorescreen3)
            )
        }
        Text(
            text = titleLeft,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Left,
            color = Color.White,
            modifier = Modifier.padding(start = MaterialTheme.dimens.smallmorescreen1)
        )

        // Høyre hjørne for kart eller båt
        if (shouldShowRightIcon) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.dimens.smallmorescreen1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = titleRight,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Right,
                    color = Color.White,
                    modifier = Modifier.padding(end = MaterialTheme.dimens.smallmorescreen1)
                )
                Box(
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.smallmorescreen1)
                        .size(MaterialTheme.dimens.mediummorescreen3)
                        .background(Color.White, shape = CircleShape)
                        .clickable(onClick = onRightClicked),
                    contentAlignment = Alignment.Center
                ) {
                    val imagePainter = painterResource(id = iconRightId)
                    Image(
                        painter = imagePainter,
                        contentDescription = titleRight,
                        alignment = Alignment.Center,
                        modifier = Modifier.size(MaterialTheme.dimens.mediummorescreen2)
                    )
                }
            }
        }
    }
}

/*
* Funksjonen splitDataIntoValueAndUnit bruker regulære uttrykk for å dele en streng inn i to deler,
* verdien og enheten, basert på enhetens format (som "°C", "m/s", "mm").
*
* Dette gjør det mulig å håndtere og vise tall og tilhørende enheter separat i brukergrensesnittet.
*
* Denne funksjonen bruker vi igjen i InfoBox funksjonen for å minimere størrelsen på symbolene i unitRegex.
* Dette for å sikre at InfoBox ikke blir for trang og full av data
*/
fun splitDataIntoValueAndUnit(data: String): Pair<String, String> {
    // Regex for å finne og skille enheter og tekst i parenteser
    val unitRegex = Regex("(°C| m/s| mm$| m$| \\(.*?\\))")
    val matchResult = unitRegex.find(data)
    val unitStart = matchResult?.range?.first ?: data.length
    val valuePart = data.substring(0, unitStart)
    val unitPart = data.substring(unitStart)
    return Pair(valuePart, unitPart)
}

/*
* InfoBox er en komponent spesialdesignet for å presentere informasjon på en organisert og estetisk tiltalende måte,
* bruker den til å vise en liste med ikon- og dataverdipar. Den starter med en tittel, og under vises en rekke ikoner
* sammen med tilhørende data. Hver ikon og datakombinasjon vises horisontalt, og dataene blir oppdelt i verdi og enhet for klarhet.
*
* Denne funksjonaliteten gjør det enkelt for brukere å raskt identifisere og tolke informasjonen.
* InfoBox benytter seg av en gradientbakgrunn og harmoniserer med appens design gjennom konsekvent bruk av
* Material Theme-styling, og er dermed ideell for presentasjoner som krever både visuell appell og informasjonseffektivitet.
*
* Denne funksjonen blir brukt i MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun InfoBox(
    title: String,
    iconDataPairs: List<Pair<Int, String>>,  // Liste av ikonressurs IDer og deres tilhørende verdier
    modifier: Modifier = Modifier
) {
    Row {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = MaterialTheme.dimens.extraSmallmorescreen)
        )
    }
    Box(
        modifier = modifier
            .background(
                gradientBackground(gradientType = GradientType.INFO_GRADIENT),
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.dimens.smallmorescreen2)
                .padding(top = MaterialTheme.dimens.smallmorescreen1)
        ) {
            iconDataPairs.forEach { (iconId, data) ->
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.iconSizeHeader)
                )
                // Behandler og deler data og enheter
                val (valuePart, unitPart) = splitDataIntoValueAndUnit(data)
                Text(
                    text = valuePart,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = MaterialTheme.dimens.extraSmallmorescreen),
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )
                Text(
                    text = unitPart,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize // Mindre skrift for enhetene inkludert parenteser
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.smallmorescreen1))
            }
        }
    }
}

/*
* Viser en valgfri tekststreng med standard skriftstil og størrelse fra MaterialTheme.
*
* Brukes i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun TextToDisplay(
    title: String?
){
    if (title != null) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
    }
}

/*
* Spesialiserer visning av datotekster med økt skriftstørrelse og ekstra bunn-padding.
*
* Brukes i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun TextForDate(
    title: String?
){
    if (title != null) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = MaterialTheme.dimens.extraSmallmorescreen),
            fontSize = MaterialTheme.typography.headlineMedium.fontSize
        )
    }
}

/*
* Presenterer numeriske eller tekstverdier med tilpassbar formatering og padding gjennom en modifikatorparameter.
*
* Brukes i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun TextForValues(
    title: String?,
    modifier: Modifier = Modifier // Ta inn modifikatoren som en parameter og bruk den direkte
){
    Text(
        text = title ?: "", // Håndter null-verdier med en tom streng
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White,
        modifier = modifier
            .padding(bottom = MaterialTheme.dimens.extraSmallmorescreen), // Legg til padding til den eksisterende modifikatoren
        fontSize = MaterialTheme.typography.labelMedium.fontSize
    )
}

/*
* Tegner en horisontal linje over hele bredden for å visuelt skille innholdselementer.
*
* Brukes i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun UiLine() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(MaterialTheme.dimens.extraExtraSmallmorescreen)
    ) {
        drawLine(
            color = backgroundColor,
            strokeWidth = 5f,
            start = Offset(0f, 0f),
            end = Offset(size.width, y = 0f)
        )
    }
}

/*
* Viser et ikon basert på en ressurs-ID, med definert størrelse og fargetilpasning.
*
* Brukes i både MoreScreen.kt og BoatScreen.kt
*/
@Composable
fun IconsToDisplay(
    iconId: Int,
) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = "Waves icon",
        tint = Color.White,
        modifier = Modifier
            .size(MaterialTheme.dimens.iconSizealertsMoreScreen)
            .padding(bottom = MaterialTheme.dimens.extraSmall)
    )
}
