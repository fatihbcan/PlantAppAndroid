package com.plantappmvi.android.presentation.onboarding.intro.view.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.designsystem.theme.HeadlineEmphasisWeight
import com.plantappmvi.android.presentation.onboarding.R

/**
 * Intro headline with one emphasised phrase, as in the design.
 *
 * The emphasis is two things at once: a heavier weight, and the design's
 * hand-drawn stroke sitting under the phrase.
 *
 * The stroke's position comes from the laid-out text rather than from fixed
 * offsets, so it follows the phrase wherever the line break puts it — at a
 * larger font scale, in a narrower window, or after translation. Compose can
 * inline a drawable through an `inlineContent` placeholder instead, but a
 * placeholder takes its own box in the line and would push the phrase sideways;
 * the design's stroke overshoots the phrase to the right and hangs below the
 * baseline without touching the line box at all.
 */
@Composable
internal fun IntroHeadline(
    text: String,
    highlight: String?,
    isHighlightUnderlined: Boolean,
    modifier: Modifier = Modifier,
) {
    val style = AppTheme.typography.displayLg
    val color = AppTheme.colors.onCanvas
    val start = if (highlight.isNullOrEmpty()) -1 else text.indexOf(highlight)
    val end = if (start < 0) -1 else start + highlight!!.length

    var phraseBounds by remember(text, highlight) { mutableStateOf<Rect?>(null) }

    val annotated = buildAnnotatedString {
        if (start < 0) {
            append(text)
        } else {
            append(text.substring(0, start))
            withStyle(SpanStyle(fontWeight = HeadlineEmphasisWeight)) {
                append(text.substring(start, end))
            }
            append(text.substring(end))
        }
    }

    Box(modifier = modifier.semantics { contentDescription = text }) {
        Text(
            text = annotated,
            style = style,
            color = color,
            onTextLayout = { layout ->
                phraseBounds = if (start >= 0) layout.boundsOf(start, end) else null
            },
        )

        val bounds = phraseBounds
        if (isHighlightUnderlined && bounds != null) {
            val density = LocalDensity.current
            val fontSize = with(density) { style.fontSize.toPx() }

            Image(
                painter = painterResource(R.drawable.img_headline_underline),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier
                    .offset(
                        x = with(density) { bounds.left.toDp() },
                        y = with(density) { (bounds.bottom - fontSize * StrokeRise).toDp() },
                    )
                    .size(
                        width = with(density) {
                            (bounds.width + fontSize * StrokeOvershoot).toDp()
                        },
                        height = with(density) { (fontSize * StrokeHeight).toDp() },
                    ),
            )
        }
    }
}

/**
 * The phrase's rectangle, unioned across the characters it spans so a phrase
 * that wraps still yields one box rather than a stroke under only its tail.
 */
private fun TextLayoutResult.boundsOf(start: Int, end: Int): Rect? {
    if (start < 0 || end > layoutInput.text.length || start >= end) return null
    return (start until end)
        .map(::getBoundingBox)
        .reduceOrNull { acc, box -> acc.expandToInclude(box) }
}

private fun Rect.expandToInclude(other: Rect) = Rect(
    left = minOf(left, other.left),
    top = minOf(top, other.top),
    right = maxOf(right, other.right),
    bottom = maxOf(bottom, other.bottom),
)

/** All three taken from the design, as multiples of the headline's font size. */
private const val StrokeHeight = 0.95f
private const val StrokeOvershoot = 1.6f
private const val StrokeRise = 0.43f
