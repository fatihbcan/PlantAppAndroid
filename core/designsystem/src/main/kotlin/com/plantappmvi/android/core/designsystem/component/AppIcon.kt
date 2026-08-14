package com.plantappmvi.android.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import com.plantappmvi.android.core.presentation.resource.IconResource
import com.plantappmvi.android.core.presentation.resource.painter

/**
 * Draws one [IconResource] at [size], tinted [tint] where the glyph allows it.
 *
 * Icons are always labelled by the control around them, so this never carries
 * a content description of its own — announcing the drawing would duplicate
 * the label a screen reader already reads.
 */
@Composable
fun AppIcon(
    icon: IconResource,
    size: Dp,
    tint: Color,
    modifier: Modifier = Modifier,
    height: Dp = size,
) {
    Image(
        painter = icon.painter(),
        contentDescription = null,
        colorFilter = if (icon.isTintable) ColorFilter.tint(tint) else null,
        modifier = modifier.size(width = size, height = height),
    )
}
