package com.plantappmvi.android.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic colour roles for the whole app.
 *
 * Composables read these through [AppTheme.colors] and never construct a
 * [Color] themselves, so a palette change is a one-file change.
 *
 * The values are the design file's own exports rather than eyeballed
 * matches — the brand green, the inactive nav grey and the premium strip's
 * two golds in particular were all wrong when measured off screenshots.
 */
@Immutable
data class AppColors(
    /** Primary green used for CTAs, selection and progress. */
    val brand: Color,
    /** Low-emphasis wash of [brand], for tinted backgrounds. */
    val brandMuted: Color,
    /** Page background. */
    val canvas: Color,
    /** Cards and sheets sitting on [canvas]. */
    val surface: Color,
    /** Secondary fills: chips, skeletons, image placeholders. */
    val surfaceMuted: Color,
    /** Primary text and icons on [canvas]/[surface]. */
    val onCanvas: Color,
    /** Secondary text — captions, subtitles. */
    val onCanvasMuted: Color,
    /** Tertiary text — placeholders, disabled labels. */
    val onCanvasSubtle: Color,
    /** Hairline dividers and card borders. */
    val outline: Color,
    /** Emphasised borders: selected states, focused fields. */
    val outlineStrong: Color,
    /**
     * The unselected destinations in the bottom bar. Deliberately a neutral
     * grey rather than a tint of [onCanvas]: in the design the bar's inactive
     * items carry no green at all, which is what makes Home read as current.
     */
    val navInactive: Color,
    /** The "get premium" strip. Flat in the design, not a gradient. */
    val bannerSurface: Color,
    /** Paywall background, dark in both schemes by design. */
    val premiumCanvas: Color,
    /** Cards on [premiumCanvas] — plan tiles, feature tiles. */
    val premiumSurface: Color,
    /** Gold accent used for premium copy and the "get premium" banner. */
    val premiumAccent: Color,
    /**
     * The duller gold the strip uses for its second line and its chevron —
     * darker than [premiumAccent], not lighter.
     */
    val premiumAccentMuted: Color,
    /** Border for unselected plan tiles. */
    val premiumOutline: Color,
    /** Text on premium surfaces. */
    val onPremium: Color,
    /** Secondary text on premium surfaces. */
    val onPremiumMuted: Color,
    /** Error states, in text and on the retry surface. */
    val danger: Color,
    /** Overlay behind images so foreground text stays legible. */
    val scrim: Color,
    val isLight: Boolean,
)

internal val LightAppColors = AppColors(
    brand = Color(0xFF28AF6E),
    brandMuted = Color(0xFFE9F7F0),
    canvas = Color(0xFFF7F7F7),
    surface = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFF4F6F5),
    onCanvas = Color(0xFF13231B),
    onCanvasMuted = Color(0xFF597165),
    onCanvasSubtle = Color(0xFF9DA6A0),
    outline = Color(0xFFEDEFEE),
    outlineStrong = Color(0xFFD3DAD6),
    navInactive = Color(0xFFBDBDBD),
    bannerSurface = Color(0xFF24201A),
    premiumCanvas = Color(0xFF101E17),
    premiumSurface = Color(0xFF1B2C22),
    premiumAccent = Color(0xFFE5C990),
    premiumAccentMuted = Color(0xFFD0B070),
    premiumOutline = Color(0xFF3C4E44),
    onPremium = Color(0xFFFFFFFF),
    onPremiumMuted = Color(0xFFB3BDB7),
    danger = Color(0xFFD1453B),
    scrim = Color(0x66000000),
    isLight = true,
)

internal val DarkAppColors = AppColors(
    brand = Color(0xFF3FD08A),
    brandMuted = Color(0xFF163024),
    canvas = Color(0xFF0B1610),
    surface = Color(0xFF14241B),
    surfaceMuted = Color(0xFF1C3126),
    onCanvas = Color(0xFFF2F6F3),
    onCanvasMuted = Color(0xFFAFBDB5),
    onCanvasSubtle = Color(0xFF7C8B83),
    outline = Color(0xFF223328),
    outlineStrong = Color(0xFF354C3D),
    navInactive = Color(0xFF7C8B83),
    bannerSurface = Color(0xFF24201A),
    premiumCanvas = Color(0xFF0B1610),
    premiumSurface = Color(0xFF16281E),
    premiumAccent = Color(0xFFE5C990),
    premiumAccentMuted = Color(0xFFD0B070),
    premiumOutline = Color(0xFF3C4E44),
    onPremium = Color(0xFFFFFFFF),
    onPremiumMuted = Color(0xFFB3BDB7),
    danger = Color(0xFFF07167),
    scrim = Color(0x80000000),
    isLight = false,
)
