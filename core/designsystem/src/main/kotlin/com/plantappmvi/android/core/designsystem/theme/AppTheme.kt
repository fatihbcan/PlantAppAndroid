package com.plantappmvi.android.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

private val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided — wrap the content in AppTheme { }")
}
private val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("No AppTypography provided — wrap the content in AppTheme { }")
}
private val LocalAppDimens = staticCompositionLocalOf<AppDimens> {
    error("No AppDimens provided — wrap the content in AppTheme { }")
}
private val LocalAppShapes = staticCompositionLocalOf<AppShapes> {
    error("No AppShapes provided — wrap the content in AppTheme { }")
}

/**
 * The only way any composable reads a colour, a size, a type style or a shape.
 *
 * Material is wrapped rather than replaced, so raw Material components still
 * work underneath — but nothing in the app should reach for one directly.
 */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current

    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current

    val dimens: AppDimens
        @Composable @ReadOnlyComposable get() = LocalAppDimens.current

    val shapes: AppShapes
        @Composable @ReadOnlyComposable get() = LocalAppShapes.current
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (isDarkTheme) DarkAppColors else LightAppColors

    // Responsiveness is a token decision, not a per-screen one: a short
    // viewport gets the tighter scale everywhere at once, so onboarding still
    // fits on a small phone without any screen special-casing itself.
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val dimens = if (screenHeight < CompactHeightBreakpoint) CompactAppDimens else RegularAppDimens

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides DefaultAppTypography,
        LocalAppDimens provides dimens,
        LocalAppShapes provides DefaultAppShapes,
    ) {
        MaterialTheme(
            colorScheme = if (isDarkTheme) {
                darkColorScheme(
                    primary = colors.brand,
                    onPrimary = colors.onPremium,
                    background = colors.canvas,
                    onBackground = colors.onCanvas,
                    surface = colors.surface,
                    onSurface = colors.onCanvas,
                    error = colors.danger,
                )
            } else {
                lightColorScheme(
                    primary = colors.brand,
                    onPrimary = colors.onPremium,
                    background = colors.canvas,
                    onBackground = colors.onCanvas,
                    surface = colors.surface,
                    onSurface = colors.onCanvas,
                    error = colors.danger,
                )
            },
            content = content,
        )
    }
}
