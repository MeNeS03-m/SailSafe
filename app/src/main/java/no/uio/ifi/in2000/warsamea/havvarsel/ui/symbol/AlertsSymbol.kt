package no.uio.ifi.in2000.warsamea.havvarsel.ui.symbol

import no.uio.ifi.in2000.warsamea.havvarsel.R

class AlertsSymbol {
    companion object {
        fun getSymbol(awarenessLevel: String?, awarenessType: String?): Int? {
            // Deler awarenessType ved semikolon for å få awareness_level og awareness_type
            val type = awarenessType?.split(";")?.getOrNull(1)?.trim()
            val level = awarenessLevel?.get(0)?.digitToIntOrNull()

            return when {
                level != null && type != null -> getSymbolResource(level, type)
                else -> null
            }
        }

        private fun getSymbolResource(awarenessLevel: Int, type: String): Int? {
            return when (awarenessLevel) {
                2 -> when (type) {
                    "avalanches" -> R.drawable.icon_warning_avalanches_yellow
                    "snow-ice" -> R.drawable.icon_warning_snow_yellow
                    "drivingconditions" -> R.drawable.icon_warning_drivingconditions_yellow
                    "flood" -> R.drawable.icon_warning_flood_yellow
                    "forest-fire" -> R.drawable.icon_warning_forestfire_yellow
                    "wind" -> R.drawable.icon_warning_wind_yellow
                    "ice" -> R.drawable.icon_warning_ice_yellow
                    "generic" -> R.drawable.icon_warning_generic_yellow
                    "landslide" -> R.drawable.icon_warning_landslide_yellow
                    "polarlow" -> R.drawable.icon_warning_polarlow_yellow
                    "rain" -> R.drawable.icon_warning_rain_yellow
                    "rainflood" -> R.drawable.icon_warning_rainflood_yellow
                    "stormsurge" -> R.drawable.icon_warning_stormsurge_yellow
                    "lightning" -> R.drawable.icon_warning_lightning_yellow
                    else -> null
                }
                3 -> when (type) {
                    "avalanches" -> R.drawable.icon_warning_avalanches_orange
                    "snow-ice" -> R.drawable.icon_warning_snow_orange
                    "drivingconditions" -> R.drawable.icon_warning_drivingconditions_orange
                    "flood" -> R.drawable.icon_warning_flood_orange
                    "forest-fire" -> R.drawable.icon_warning_forestfire_orange
                    "wind" -> R.drawable.icon_warning_wind_orange
                    "ice" -> R.drawable.icon_warning_ice_orange
                    "generic" -> R.drawable.icon_warning_generic_orange
                    "landslide" -> R.drawable.icon_warning_landslide_orange
                    "polarlow" -> R.drawable.icon_warning_polarlow_orange
                    "rain" -> R.drawable.icon_warning_rain_orange
                    "rainflood" -> R.drawable.icon_warning_rainflood_orange
                    "stormsurge" -> R.drawable.icon_warning_stormsurge_orange
                    "lightning" -> R.drawable.icon_warning_lightning_orange
                    else -> null
                }
                4 -> when (type) {
                    "avalanches" -> R.drawable.icon_warning_avalanches_red
                    "snow-ice" -> R.drawable.icon_warning_snow_red
                    "drivingconditions" -> R.drawable.icon_warning_drivingconditions_red
                    "flood" -> R.drawable.icon_warning_flood_red
                    "forest-fire" -> R.drawable.icon_warning_forestfire_red
                    "wind" -> R.drawable.icon_warning_wind_red
                    "ice" -> R.drawable.icon_warning_ice_red
                    "generic" -> R.drawable.icon_warning_generic_red
                    "landslide" -> R.drawable.icon_warning_landslide_red
                    "polarlow" -> R.drawable.icon_warning_polarlow_red
                    "rain" -> R.drawable.icon_warning_rain_red
                    "rainflood" -> R.drawable.icon_warning_rainflood_red
                    "stormsurge" -> R.drawable.icon_warning_stormsurge_red
                    "lightning" -> R.drawable.icon_warning_lightning_red
                    else -> null
                }
                else -> null
            }
        }
    }
}
