package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DiscordDarkColorScheme = darkColorScheme(
    primary = DiscordBlurple,
    onPrimary = Color.White,
    primaryContainer = DiscordBlurple.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = DiscordGreen,
    onSecondary = Color.Black,
    secondaryContainer = DiscordGreen.copy(alpha = 0.2f),
    tertiary = DiscordYellow,
    onTertiary = Color.Black,
    background = DiscordBgDeep,
    onBackground = DiscordTextWhite,
    surface = DiscordBgSurface,
    onSurface = DiscordTextWhite,
    surfaceVariant = DiscordBgInput,
    onSurfaceVariant = DiscordTextMuted,
    outline = DiscordBorder,
    error = DiscordRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Discord apps excel in dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DiscordDarkColorScheme,
        typography = Typography,
        content = content
    )
}

