package com.ciclo21.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ciclo21.app.domain.model.CyclePhase

@Composable
fun Ciclo21Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    currentPhase: CyclePhase? = null,
    content: @Composable () -> Unit
) {
    // Cor primária muda para Vermelho se estiver na Menstruação
    val activePrimary = if (currentPhase == CyclePhase.MENSTRUACAO) Menstruacao else iOSBlue

    val iOSDarkColorScheme = darkColorScheme(
        primary = activePrimary,
        background = if (currentPhase == CyclePhase.MENSTRUACAO) Color(0xFF1A0505) else iOSBackgroundDark,
        surface = iOSSecondaryGroupedBackgroundDark,
        onBackground = iOSLabelDark,
        onSurface = iOSLabelDark,
        surfaceVariant = iOSSecondaryBackgroundDark,
        outlineVariant = iOSSeparatorDark
    )

    val iOSLightColorScheme = lightColorScheme(
        primary = activePrimary,
        background = if (currentPhase == CyclePhase.MENSTRUACAO) Color(0xFFFFF1F0) else iOSGroupedBackground,
        surface = iOSSecondaryGroupedBackground,
        onBackground = iOSLabel,
        onSurface = iOSLabel,
        surfaceVariant = iOSSecondaryBackground,
        outlineVariant = iOSSeparator
    )

    val colorScheme = if (darkTheme) iOSDarkColorScheme else iOSLightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Usando a conversão correta para ARGB
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
