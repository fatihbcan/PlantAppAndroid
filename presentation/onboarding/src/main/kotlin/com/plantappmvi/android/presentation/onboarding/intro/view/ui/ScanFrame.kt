package com.plantappmvi.android.presentation.onboarding.intro.view.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * The viewfinder over the welcome plant and the phone's camera preview.
 *
 * Drawn rather than exported: the design stretches the same mark to a
 * different aspect on each screen, which a bitmap cannot follow without
 * distorting its own stroke weight. Four corner brackets, each a fixed
 * fraction of the shorter side, so the corners stay square at any aspect.
 */
@Composable
internal fun ScanFrame(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = StrokeWidthDp.toPx()
        val corner = minOf(size.width, size.height) * CornerFraction
        val radius = strokeWidth * RadiusFactor
        val inset = strokeWidth / 2

        drawPath(
            path = cornerBrackets(size, corner, radius, inset),
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

private fun cornerBrackets(size: Size, corner: Float, radius: Float, inset: Float) = Path().apply {
    val left = inset
    val top = inset
    val right = size.width - inset
    val bottom = size.height - inset

    // Top-left
    moveTo(left, top + corner)
    lineTo(left, top + radius)
    quadraticTo(left, top, left + radius, top)
    lineTo(left + corner, top)

    // Top-right
    moveTo(right - corner, top)
    lineTo(right - radius, top)
    quadraticTo(right, top, right, top + radius)
    lineTo(right, top + corner)

    // Bottom-right
    moveTo(right, bottom - corner)
    lineTo(right, bottom - radius)
    quadraticTo(right, bottom, right - radius, bottom)
    lineTo(right - corner, bottom)

    // Bottom-left
    moveTo(left + corner, bottom)
    lineTo(left + radius, bottom)
    quadraticTo(left, bottom, left, bottom - radius)
    lineTo(left, bottom - corner)
}

private val StrokeWidthDp = 3.dp
private const val CornerFraction = 0.22f
private const val RadiusFactor = 3f
