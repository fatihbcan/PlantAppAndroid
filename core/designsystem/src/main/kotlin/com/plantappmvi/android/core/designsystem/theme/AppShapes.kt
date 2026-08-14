package com.plantappmvi.android.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape

@Immutable
data class AppShapes(
    val button: Shape,
    val card: Shape,
    val field: Shape,
    val sheet: Shape,
    val chip: Shape,
)

internal val DefaultAppShapes = AppShapes(
    button = RoundedCornerShape(RegularAppDimens.radiusMd),
    card = RoundedCornerShape(RegularAppDimens.radiusMd),
    field = RoundedCornerShape(RegularAppDimens.radiusMd),
    sheet = RoundedCornerShape(
        topStart = RegularAppDimens.radiusXl,
        topEnd = RegularAppDimens.radiusXl,
        bottomStart = RegularAppDimens.spaceXxs,
        bottomEnd = RegularAppDimens.spaceXxs,
    ),
    chip = RoundedCornerShape(RegularAppDimens.radiusSm),
)
