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

/** A selectable plan, with the design's discount badge on its trailing edge. */
@Composable
internal fun PaywallPlanTile(
    props: PaywallPlanProps,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (props.isSelected) AppTheme.colors.brand else AppTheme.colors.premiumOutline
    val borderWidth = if (props.isSelected) AppTheme.dimens.strokeThick else AppTheme.dimens.strokeThin

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.card)
            .background(AppTheme.colors.premiumSurface)
            .border(borderWidth, borderColor, AppTheme.shapes.card)
            .noRippleClickable(onClick = onClick)
            .padding(AppTheme.dimens.spaceLg),
    ) {
        SelectionDot(isSelected = props.isSelected)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = props.title.asString(),
                style = AppTheme.typography.titleSm,
                color = AppTheme.colors.onPremium,
            )
            Text(
                text = props.subtitle.asString(),
                style = AppTheme.typography.bodySm,
                color = AppTheme.colors.onPremiumMuted,
            )
        }

        props.badge?.let { badge ->
            Text(
                text = badge.asString(),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.premiumCanvas,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.dimens.radiusSm))
                    .background(AppTheme.colors.brand)
                    .padding(
                        horizontal = AppTheme.dimens.spaceSm,
                        vertical = AppTheme.dimens.spaceXs,
                    ),
            )
        }
    }
}

@Composable
private fun SelectionDot(isSelected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(SelectionDotSize)
            .clip(CircleShape)
            .border(
                width = AppTheme.dimens.strokeThin,
                color = if (isSelected) AppTheme.colors.brand else AppTheme.colors.premiumOutline,
                shape = CircleShape,
            ),
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(SelectionDotCoreSize)
                    .clip(CircleShape)
                    .background(AppTheme.colors.brand),
            )
        }
    }
}

private val FeatureCardWidth = 156.dp
private val SelectionDotSize = 20.dp
private val SelectionDotCoreSize = 10.dp
