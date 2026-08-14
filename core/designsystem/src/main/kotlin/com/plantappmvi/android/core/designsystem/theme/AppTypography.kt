package com.plantappmvi.android.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plantappmvi.android.core.designsystem.R

/**
 * Rubik, the design's typeface, bundled rather than linked so the app renders
 * identically to the Figma frames instead of falling back to Roboto.
 */
internal val RubikFontFamily = FontFamily(
    Font(R.font.rubik_light, FontWeight.Light),
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_semibold, FontWeight.SemiBold),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_extrabold, FontWeight.ExtraBold),
)

/**
 * Named text styles, keyed by role rather than by size.
 *
 * Styles carry no colour: colour comes from [AppColors] at the call site, so
 * the same style works on light, dark and premium surfaces.
 */
@Immutable
data class AppTypography(
    /** Onboarding headline. */
    val displayLg: TextStyle,
    /** Paywall headline. */
    val displayMd: TextStyle,
    /** Home greeting, section headings. */
    val titleLg: TextStyle,
    /** Card titles. */
    val titleMd: TextStyle,
    /** Plan tile titles. */
    val titleSm: TextStyle,
    val bodyLg: TextStyle,
    val bodyMd: TextStyle,
    val bodySm: TextStyle,
    /** Field labels and chips. */
    val label: TextStyle,
    /** Legal copy, image credits. */
    val caption: TextStyle,
    val button: TextStyle,
)

/**
 * The weight the design uses for the emphasised half of a headline.
 *
 * Public because it is a design-system decision applied inside a feature's own
 * `AnnotatedString`, which is the one place a caller legitimately needs a raw
 * weight rather than a whole style.
 */
val HeadlineEmphasisWeight = FontWeight.ExtraBold

internal val DefaultAppTypography = AppTypography(
    displayLg = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 27.sp,
        lineHeight = 34.6.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.8).sp,
    ),
    displayMd = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 27.sp,
        lineHeight = 32.4.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.6).sp,
    ),
    titleLg = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.9).sp,
    ),
    titleMd = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 16.sp,
        lineHeight = 20.8.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
    ),
    titleSm = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 15.sp,
        lineHeight = 19.5.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
    ),
    bodyLg = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.3).sp,
    ),
    bodyMd = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.2).sp,
    ),
    bodySm = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.2.sp,
        fontWeight = FontWeight.Normal,
    ),
    label = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 13.sp,
        lineHeight = 16.9.sp,
        fontWeight = FontWeight.Medium,
    ),
    caption = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 11.sp,
        lineHeight = 14.9.sp,
        fontWeight = FontWeight.Normal,
    ),
    button = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
    ),
)
