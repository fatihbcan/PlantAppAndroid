package com.plantappmvi.android.core.designsystem.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

/**
 * Lets a child draw [horizontal] past each side of its parent's padding.
 *
 * A scrolling page usually wants one horizontal gutter, applied once as the
 * list's content padding — but a full-bleed band inside it (artwork that runs
 * to the screen edges, a carousel that peeks the next card) has to escape that
 * gutter without leaving the list, or the page ends up with two independent
 * scroll positions.
 *
 * The child is measured wider and shifted left; the layout still reports the
 * original width, so everything after it is placed as though nothing happened.
 */
fun Modifier.fullBleed(horizontal: Dp): Modifier = layout { measurable, constraints ->
    val extra = horizontal.roundToPx() * 2
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = (constraints.minWidth + extra).coerceAtLeast(0),
            maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth + extra else constraints.maxWidth,
        ),
    )
    layout(placeable.width - extra, placeable.height) {
        placeable.place(-horizontal.roundToPx(), 0)
    }
}
