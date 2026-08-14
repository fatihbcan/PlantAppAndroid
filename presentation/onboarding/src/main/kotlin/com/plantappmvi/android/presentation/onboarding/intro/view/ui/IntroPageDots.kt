package com.plantappmvi.android.presentation.onboarding.intro.view.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.plantappmvi.android.core.designsystem.theme.AppTheme

/**
 * Page position indicator for the intro pager.
 *
 * The design uses round dots that change size and tone, not a stretching pill
 * — the current page is a larger, near-black dot among smaller grey ones.
 */
@Composable
internal fun IntroPageDots(
    count: Int,
    activeIndex: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DotGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics { contentDescription = label },
    ) {
        repeat(count) { index ->
            val isActive = index == activeIndex
            val size by animateDpAsState(
                targetValue = if (isActive) ActiveSize else InactiveSize,
                label = "introDotSize",
            )
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        if (isActive) {
                            AppTheme.colors.onCanvas
                        } else {
                            AppTheme.colors.onCanvas.copy(alpha = InactiveAlpha)
                        },
                    ),
            )
        }
    }
}

private val ActiveSize = 10.dp
private val InactiveSize = 6.dp
private val DotGap = 10.dp
private const val InactiveAlpha = 0.18f
