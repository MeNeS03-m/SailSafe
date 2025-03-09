package no.uio.ifi.in2000.warsamea.havvarsel.model.geocoding

/*
* De fleste Data filene for APIene våre har en del warnings, dette kommer av at
* vi bruker flere variabelnavn med "_" i seg. Dette liker ikke Kotlin. Vi har
* allikevel valgt å beholde dette ettersom vi brukte funksjonen Kotlin data class
* file from JSON som genererte disse variabelnavnene til oss.
*/

data class GeocodingData(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)

data class PlusCode(
    val compound_code: String,
    val global_code: String
)

data class Result(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val types: List<String>
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)

data class Bounds(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Northeast(
    val lat: Double,
    val lng: Double
)

data class Southwest(
    val lat: Double,
    val lng: Double
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)
