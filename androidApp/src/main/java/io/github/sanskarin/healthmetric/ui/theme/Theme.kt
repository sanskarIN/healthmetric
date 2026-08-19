package io.github.sanskarin.healthmetric.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.sanskarin.healthmetric.data.AppThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF006B66),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9DF2EA),
    onPrimaryContainer = Color(0xFF00201E),
    secondary = Color(0xFF4A635F),
    background = Color(0xFFF5FBF9),
    surface = Color(0xFFF5FBF9),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81D5CE),
    onPrimary = Color(0xFF003734),
    primaryContainer = Color(0xFF00504C),
    onPrimaryContainer = Color(0xFF9DF2EA),
    secondary = Color(0xFFB1CCC7),
    background = Color(0xFF0E1514),
    surface = Color(0xFF0E1514),
    error = Color(0xFFFFB4AB),
)

@Composable
fun HealthMetricTheme(
    themeMode: AppThemeMode,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content,
    )
}
