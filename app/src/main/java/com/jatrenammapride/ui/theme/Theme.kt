package com.jatrenammapride.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Saffron,
    onPrimary = White,
    primaryContainer = LightSaffron,
    onPrimaryContainer = DarkBrown,
    secondary = DeepRed,
    onSecondary = White,
    secondaryContainer = DeepRed.copy(alpha = 0.15f),
    onSecondaryContainer = DeepRed,
    tertiary = Gold,
    onTertiary = DarkBrown,
    tertiaryContainer = Gold.copy(alpha = 0.3f),
    onTertiaryContainer = DarkBrown,
    background = Cream,
    onBackground = DarkBrown,
    surface = White,
    onSurface = DarkBrown,
    surfaceVariant = LightSaffron,
    onSurfaceVariant = DarkBrown.copy(alpha = 0.7f),
    outline = DarkBrown.copy(alpha = 0.3f),
    error = DeepRed
)

@Composable
fun JatreNammaPrideTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
