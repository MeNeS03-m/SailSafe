package no.uio.ifi.in2000.warsamea.havvarsel

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.warsamea.havvarsel.data.AllAPIVariables
import no.uio.ifi.in2000.warsamea.havvarsel.ui.boat.BoatScreen
import no.uio.ifi.in2000.warsamea.havvarsel.ui.home.HomeScreen
import no.uio.ifi.in2000.warsamea.havvarsel.ui.more.MoreScreen
import no.uio.ifi.in2000.warsamea.havvarsel.ui.theme.HavvarselTheme
import java.net.URLDecoder


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialiser Places
        Places.initialize(applicationContext, "AIzaSyBwUOUwUrv6mqU9NOFe9Ot_kcvKmkPFpm4")
        placesClient = Places.createClient(this) // Opprett PlacesClient

        setContent {
            HavvarselTheme {
                SailSafe()
            }
        }
    }
}

@Composable
private fun SailSafe() {
    val navController = rememberNavController()
    val json = Json { encodeDefaults = true }

    // Definer navArguments for latitude og longitude
    val locationArguments = listOf(
        navArgument("latitude") { type = NavType.FloatType },
        navArgument("longitude") { type = NavType.FloatType }
    )

    // Definer navArguments for locationName
    val locationArgumentsName = listOf(navArgument("weatherDataUiStateJson"){ type = NavType.StringType })
    // NavHost for screen navigation
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController = navController) }
        composable(
            route = "moreScreen/{weatherDataUiStateJson}",
            arguments = locationArgumentsName
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("weatherDataUiStateJson") ?: ""
            val decodedJson = URLDecoder.decode(encodedJson, "UTF-8")
            val weatherData = json.decodeFromString(AllAPIVariables.serializer(), decodedJson)
            MoreScreen(navController = navController, weatherData = weatherData, lat = 59.00, lon = 10.00, search = false)
        }
        composable(
            route = "moreScreenFromSearch/{latitude}/{longitude}",
            arguments = locationArguments
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0
            MoreScreen(navController = navController, weatherData = null, lat = latitude, lon = longitude, search = true)
        }
        composable(
            route = "boatScreen/{weatherDataUiStateJson}",
            arguments = locationArgumentsName
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("weatherDataUiStateJson") ?: ""
            val decodedJson = URLDecoder.decode(encodedJson, "UTF-8")
            val weatherData = json.decodeFromString(AllAPIVariables.serializer(), decodedJson)
            BoatScreen(navController = navController, weatherData = weatherData)
        }
    }
}