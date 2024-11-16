package com.example.snapstock1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LavenderMist,
    secondary = MauvePink,
    tertiary = JetBlack
)

private val LightColorScheme = lightColorScheme(
    primary = LavenderMist,
    secondary = MauvePink,
    tertiary = JetBlack
)
// Определение цветовой схемы
private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6)
)

@Composable
fun SnapStockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
