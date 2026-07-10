package com.mnmyounus.yfdw.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = YfdwPrimary,
    secondary = YfdwSecondary,
    background = YfdwBackground,
    surface = YfdwSurface
)

private val LightColors = lightColorScheme(
    primary = YfdwPrimary,
    secondary = YfdwSecondary
)

@Composable
fun YfdwTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = YfdwTypography, content = content)
}
