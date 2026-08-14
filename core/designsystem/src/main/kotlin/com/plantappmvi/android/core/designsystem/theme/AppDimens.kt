package com.plantappmvi.android.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The spacing, radius and sizing scale.
 *
 * Every gap, padding and corner in the app comes from here. A literal `16.dp`
 * in a composable is a bug — use `AppTheme.dimens`.
 */
@Immutable
data class AppDimens(
    val spaceXxs: Dp,
    val spaceXs: Dp,
    val spaceSm: Dp,
    val spaceMd: Dp,
    val spaceLg: Dp,
    val spaceXl: Dp,
    val spaceXxl: Dp,
    val radiusSm: Dp,
    val radiusMd: Dp,
    val radiusLg: Dp,
    val radiusXl: Dp,
    val strokeThin: Dp,
    val strokeThick: Dp,
    /** Minimum height of a tappable control — also the accessibility floor. */
    val controlHeight: Dp,
    val iconSm: Dp,
    val iconMd: Dp,
    /** Horizontal page padding. */
    val pageGutter: Dp,
)

internal val RegularAppDimens = AppDimens(
    spaceXxs = 2.dp,
    spaceXs = 4.dp,
    spaceSm = 8.dp,
    spaceMd = 12.dp,
    spaceLg = 16.dp,
    spaceXl = 24.dp,
    spaceXxl = 32.dp,
    radiusSm = 6.dp,
    radiusMd = 12.dp,
    radiusLg = 16.dp,
    radiusXl = 24.dp,
    strokeThin = 1.dp,
    strokeThick = 2.dp,
    controlHeight = 56.dp,
    iconSm = 16.dp,
    iconMd = 24.dp,
    pageGutter = 24.dp,
)

/** Tighter scale for short viewports — small phones, landscape. */
internal val CompactAppDimens = AppDimens(
    spaceXxs = 2.dp,
    spaceXs = 3.dp,
    spaceSm = 6.dp,
    spaceMd = 10.dp,
    spaceLg = 12.dp,
    spaceXl = 16.dp,
    spaceXxl = 20.dp,
    radiusSm = 6.dp,
    radiusMd = 12.dp,
    radiusLg = 14.dp,
    radiusXl = 20.dp,
    strokeThin = 1.dp,
    strokeThick = 2.dp,
    controlHeight = 48.dp,
    iconSm = 16.dp,
    iconMd = 22.dp,
    pageGutter = 20.dp,
)

/**
 * Below this height the onboarding pages stop fitting at the regular scale,
 * so the theme swaps in [CompactAppDimens]. Measured against the design's own
 * 360×800 frame.
 */
internal val CompactHeightBreakpoint = 700.dp
