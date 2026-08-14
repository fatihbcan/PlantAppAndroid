package com.plantappmvi.android.core.designsystem.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * A tap with no ripple, for glyphs and artwork the design draws without a
 * touch surface. Everything with a real surface should keep its ripple.
 */
@Composable
fun Modifier.noRippleClickable(
    role: Role = Role.Button,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onClick = onClick,
    )
}
