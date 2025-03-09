package no.uio.ifi.in2000.warsamea.havvarsel.ui.theme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.colorResource
import no.uio.ifi.in2000.warsamea.havvarsel.R

enum class GradientType {
    LOADING_GRADIENT,
    INFO_GRADIENT,
    UV_GRADIENT,
    HUMIDITY_GRADIENT,
    WEATHER_GRADIENT,
    BOAT_GRADIENT,
    FOG_GRADIENT,
    WAVES_GRADIENT,
    WIND_GRADIENT
}

@Composable
fun gradientBackground(gradientType: GradientType): Brush {
    return when (gradientType) {
        GradientType.LOADING_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.backgroundColor),
            500.0f to colorResource(R.color.lightblueColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.INFO_GRADIENT -> Brush.horizontalGradient(
            0.0f to colorResource(R.color.darkerlightblueColor),
            1.0f to colorResource(R.color.lightblueColor),
            startX = 0.0f,
            endX = 1000.0f
        )
        GradientType.UV_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.darkerlightblueColor),
            500.0f to colorResource(R.color.lightblueColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.HUMIDITY_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.lightblueColor),
            500.0f to colorResource(R.color.darkerlightblueColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.WEATHER_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.darkBlueColor),
            500.0f to  colorResource(R.color.darkBlackColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.BOAT_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.darkBlueColor),
            500.0f to  colorResource(R.color.darkBlackColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.FOG_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.blueGradientColor),
            500.0f to colorResource(R.color.lightblueColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.WAVES_GRADIENT -> Brush.linearGradient(
            0.0f to colorResource(R.color.lightblueColor),
            500.0f to colorResource(R.color.blueGradientColor),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        GradientType.WIND_GRADIENT -> Brush.horizontalGradient(
            0.0f to colorResource(R.color.blueGradientColor),
            1.0f to colorResource(R.color.lightblueColor),
            startX = 0.0f,
            endX = 1000.0f
        )
    }
}