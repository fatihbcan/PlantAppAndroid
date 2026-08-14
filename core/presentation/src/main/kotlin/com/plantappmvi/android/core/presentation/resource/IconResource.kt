package com.plantappmvi.android.core.presentation.resource

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

/**
 * A drawable a Props class can carry without importing a Compose `Painter`,
 * which is not a stable type and cannot be built outside composition.
 *
 * [isTintable] carries a real distinction from the design file: most of its
 * glyphs are single-colour masters the theme recolours, but the premium
 * strip's envelope and the paywall's three feature marks are full-colour
 * artwork that must be drawn as exported.
 */
@Immutable
data class IconResource(
    @DrawableRes val id: Int,
    val isTintable: Boolean = true,
)

@Composable
fun IconResource.painter(): Painter = painterResource(id)
