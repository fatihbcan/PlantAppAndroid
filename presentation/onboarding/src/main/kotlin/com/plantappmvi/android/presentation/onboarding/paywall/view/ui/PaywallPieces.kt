package com.plantappmvi.android.presentation.onboarding.paywall.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.plantappmvi.android.core.designsystem.component.AppIcon
import com.plantappmvi.android.core.designsystem.modifier.noRippleClickable
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.resource.asString
import com.plantappmvi.android.presentation.onboarding.paywall.view.props.PaywallFeatureProps
import com.plantappmvi.android.presentation.onboarding.paywall.view.props.PaywallPlanProps

/**
 * One benefit in the strip across the hero's lower edge.
 *
 * The design ships these marks as complete tiles — tinted ground and glyph
 * together — so the icon is drawn whole rather than rebuilt as a glyph on a
 * box of our own.
 */
@Composable
internal fun PaywallFeatureCard(
    props: PaywallFeatureProps,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
        modifier = modifier
            .width(FeatureCardWidth)
            .clip(AppTheme.shapes.card)
            .background(AppTheme.colors.premiumSurface)
            .padding(AppTheme.dimens.spaceLg),
    ) {
        AppIcon(
            icon = props.icon,
            size = AppTheme.dimens.iconMd,
            tint = AppTheme.colors.premiumAccent,
        )
        Text(
            text = props.title.asString(),
            style = AppTheme.typography.titleMd,
            color = AppTheme.colors.onPremium,
        )
        Text(
            text = props.body.asString(),
            style = AppTheme.typography.bodySm,
            color = AppTheme.colors.onPremiumMuted,
        )
    }
}

/** A selectable plan, with the design's discount badge on its top corner. */
@Composable
internal fun PaywallPlanTile(
    props: PaywallPlanProps,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (props.isSelected) AppTheme.colors.brand else AppTheme.colors.premiumOutline
    val borderWidth = if (props.isSelected) AppTheme.dimens.strokeThick else AppTheme.dimens.strokeThin

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.card)
            .background(AppTheme.colors.premiumSurface)
            .border(borderWidth, borderColor, AppTheme.shapes.card)
            .noRippleClickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.dimens.spaceLg,
                    vertical = AppTheme.dimens.spaceMd,
                ),
        ) {
            SelectionDot(isSelected = props.isSelected)

            Column {
                Text(
                    text = props.title.asString(),
                    style = AppTheme.typography.titleSm,
                    color = AppTheme.colors.onPremium,
                )
                Text(
                    text = props.subtitle.asString(),
                    style = AppTheme.typography.bodySm,
                    color = AppTheme.colors.onPremiumMuted,
                    modifier = Modifier.padding(top = AppTheme.dimens.spaceXxs),
                )
            }
        }

        // The design hangs the badge off the tile's top corner rather than
        // setting it in the row beside the copy, where it floated in the
        // middle of the tile's trailing edge.
        props.badge?.let { badge ->
            PlanBadge(
                label = badge.asString(),
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

/** Square where it meets the tile's edges, rounded where it does not. */
@Composable
private fun PlanBadge(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = AppTheme.typography.caption,
        color = AppTheme.colors.onPremium,
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topEnd = AppTheme.dimens.radiusMd,
                    bottomStart = AppTheme.dimens.radiusMd,
                ),
            )
            .background(AppTheme.colors.brand)
            .padding(
                horizontal = AppTheme.dimens.spaceSm,
                vertical = AppTheme.dimens.spaceXs,
            ),
    )
}

/**
 * Selected is a solid green disc with a white centre; unselected is a plain
 * darker disc, not an empty ring — that is what the design draws.
 */
@Composable
private fun SelectionDot(isSelected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(SelectionDotSize)
            .clip(CircleShape)
            .background(
                if (isSelected) {
                    AppTheme.colors.brand
                } else {
                    AppTheme.colors.onPremium.copy(alpha = UnselectedDotAlpha)
                },
            ),
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(SelectionDotCoreSize)
                    .clip(CircleShape)
                    .background(AppTheme.colors.onPremium),
            )
        }
    }
}

private val FeatureCardWidth = 156.dp
private val SelectionDotSize = 24.dp
private val SelectionDotCoreSize = 9.dp
private const val UnselectedDotAlpha = 0.09f
