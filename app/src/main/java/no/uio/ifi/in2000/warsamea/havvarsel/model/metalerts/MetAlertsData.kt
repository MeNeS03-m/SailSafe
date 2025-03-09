package no.uio.ifi.in2000.warsamea.havvarsel.model.metalerts

/*
* De fleste Data filene for APIene våre har en del warnings, dette kommer av at
* vi bruker flere variabelnavn med "_" i seg. Dette liker ikke Kotlin. Vi har
* allikevel valgt å beholde dette ettersom vi brukte funksjonen Kotlin data class
* file from JSON som genererte disse variabelnavnene til oss.
*/
data class MetAlertsData(
    val features: List<Feature>,
    val lang: String,
    val lastChange: String,
    val type: String
)

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String,
    val `when`: When
)

data class Geometry(
    val coordinates: List<List<List<Any>>>,
    val type: String
)

data class Properties(
    val altitude_above_sea_level: Int,
    val area: String,
    val awarenessResponse: String,
    val awarenessSeriousness: String,
    val awareness_level: String,
    val awareness_type: String,
    val ceiling_above_sea_level: Int,
    val certainty: String,
    val consequences: String,
    val contact: String,
    val county: List<Any>,
    val description: String,
    val event: String,
    val eventAwarenessName: String,
    val eventEndingTime: String,
    val geographicDomain: String,
    val id: String,
    val instruction: String,
    val municipality: List<String>,
    val resources: List<Resource>,
    val riskMatrixColor: String,
    val severity: String,
    val status: String,
    val title: String,
    val triggerLevel: String,
    val type: String,
    val web: String
)

data class Resource(
    val description: String,
    val mimeType: String,
    val uri: String
)

data class When(
    val interval: List<String>
)