package com.example.stayeaseapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Caribbean Blue Palette
val CaribbeanBlue = Color(0xFF00B4D8)
val CaribbeanBlueLight = Color(0xFF62EFFF)
val CaribbeanBlueDark = Color(0xFF0077B6)

val AquaGreen = Color(0xFF48CAE4)
val AquaGreenLight = Color(0xFF90E0EF)
val AquaGreenDark = Color(0xFF0096C7)

val SoftSand = Color(0xFFFFE066)

val White = Color(0xFFFFFFFF)
val LightBlueSurface = Color(0xFFF0FBFF)
val RedError = Color(0xFFF44336)
val Black = Color(0xFF000000)

private val LightColorScheme = lightColorScheme(
    primary = CaribbeanBlue,
    onPrimary = White,
    primaryContainer = CaribbeanBlueLight,
    onPrimaryContainer = Black,

    secondary = AquaGreen,
    onSecondary = Black,
    secondaryContainer = AquaGreenLight,
    onSecondaryContainer = Black,

    tertiary = SoftSand,
    onTertiary = Black,
    tertiaryContainer = White,
    onTertiaryContainer = Black,

    background = White,
    surface = LightBlueSurface,
    onBackground = Black,
    onSurface = Black,

    error = RedError,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = CaribbeanBlueLight,
    onPrimary = Black,
    primaryContainer = CaribbeanBlueDark,
    onPrimaryContainer = White,

    secondary = AquaGreenLight,
    onSecondary = Black,
    secondaryContainer = AquaGreenDark,
    onSecondaryContainer = White,

    tertiary = SoftSand,
    onTertiary = Black,
    tertiaryContainer = CaribbeanBlueDark,
    onTertiaryContainer = White,

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = White,
    onSurface = White,

    error = RedError,
    onError = Black
)

@Composable
fun StayEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Customize if needed
        content = content
    )
}
