package com.android.car.automotiveapp.core.designsystem.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = AutomotiveAccent,
    onPrimary = Color(0xFF0B0D0F),
    primaryContainer = AutomotiveAccentContainer,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF80DEEA),
    onSecondary = Color(0xFF00363A),
    background = AutomotiveDarkSurface,
    onBackground = Color(0xFFECEFF1),
    surface = AutomotiveDarkCard,
    onSurface = Color(0xFFECEFF1),
    surfaceVariant = Color(0xFF262C34),
    onSurfaceVariant = Color(0xFFB0BEC5),
    outline = Color(0xFF37474F),
    error = Color(0xFFFF5252),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00838F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF004D5A),
    secondary = Color(0xFF00695C),
    onSecondary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF191C1E),
    surface = Color.White,
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFE0E3E8),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFF74777F),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun AutomotiveTheme(
    darkTheme: Boolean = true, // Default to automotive dark cockpit aesthetic
    dynamicColor: Boolean = false, // Preserve high-contrast automotive color scheme
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
