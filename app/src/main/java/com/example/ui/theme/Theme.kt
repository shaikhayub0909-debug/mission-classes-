package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MissionBluePrimary,
    onPrimary = Color.White,
    primaryContainer = MissionBlueContainer,
    onPrimaryContainer = MissionBlueDark,
    secondary = MissionGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = MissionGoldContainer,
    onSecondaryContainer = MissionGoldDark,
    tertiary = MissionEmeraldSuccess,
    onTertiary = Color.White,
    tertiaryContainer = MissionEmeraldContainer,
    onTertiaryContainer = Color(0xFF064E3B),
    background = MissionSlateBackground,
    onBackground = MissionSlateTextPrimary,
    surface = MissionSlateSurface,
    onSurface = MissionSlateTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = MissionSlateTextSecondary,
    outline = MissionSlateBorder,
    error = MissionRoseError,
    onError = Color.White,
    errorContainer = MissionRoseContainer,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = MissionBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = MissionGoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = MissionGoldContainer,
    tertiary = MissionEmeraldSuccess,
    onTertiary = Color.White,
    background = MissionDarkBackground,
    onBackground = MissionDarkTextPrimary,
    surface = MissionDarkSurface,
    onSurface = MissionDarkTextPrimary,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = MissionDarkTextSecondary,
    outline = MissionDarkBorder,
    error = MissionRoseError,
    onError = Color.White
)

@Composable
fun MissionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MissionTheme(darkTheme, content)
