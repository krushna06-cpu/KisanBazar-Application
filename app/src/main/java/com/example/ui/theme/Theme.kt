package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightLeafAccent,
    onPrimary = Color(0xFF07210A),
    primaryContainer = ForestGreen,
    onPrimaryContainer = MintCream,
    secondary = GoldenHighlight,
    onSecondary = Color(0xFF261900),
    secondaryContainer = Color(0xFF593D00),
    onSecondaryContainer = SunlitCream,
    tertiary = Color(0xFF4DB6AC),
    background = DeepSoilDark,
    onBackground = MintCream,
    surface = DarkCardSurface,
    onSurface = MintCream,
    surfaceVariant = Color(0xFF213D26),
    onSurfaceVariant = Color(0xFFC8E6C9),
    error = Color(0xFFEF9A9A)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = PureWhite,
    primaryContainer = SoftSageSurface,
    onPrimaryContainer = Color(0xFF092E0D),
    secondary = HarvestGold,
    onSecondary = Color(0xFF1F1400),
    secondaryContainer = SunlitCream,
    onSecondaryContainer = Color(0xFF3E2723),
    tertiary = EmeraldGreen,
    onTertiary = PureWhite,
    background = Color(0xFFF9FBF8),
    onBackground = Color(0xFF141E15),
    surface = PureWhite,
    onSurface = Color(0xFF141E15),
    surfaceVariant = Color(0xFFEAF3E8),
    onSurfaceVariant = Color(0xFF334735),
    error = ErrorRed
)

@Composable
fun KisanBazarTheme(
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
