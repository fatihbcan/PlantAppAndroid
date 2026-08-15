package com.plantappmvi.android.presentation.onboarding.paywall.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plantappmvi.android.core.designsystem.component.AppIcon
import com.plantappmvi.android.core.designsystem.component.AppLoader
import com.plantappmvi.android.core.designsystem.component.ErrorState
import com.plantappmvi.android.core.designsystem.component.ErrorStateProps
import com.plantappmvi.android.core.designsystem.component.PrimaryButton
import com.plantappmvi.android.core.designsystem.component.PrimaryButtonProps
import com.plantappmvi.android.core.designsystem.icon.AppIcons
import com.plantappmvi.android.core.designsystem.modifier.noRippleClickable
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.designsystem.theme.HeadlineEmphasisWeight
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.asString
import com.plantappmvi.android.core.presentation.view.BasicScreen
import com.plantappmvi.android.presentation.onboarding.R
import com.plantappmvi.android.presentation.onboarding.paywall.view.props.PaywallScreenProps
import com.plantappmvi.android.presentation.onboarding.paywall.view.props.mapStateToProps
import com.plantappmvi.android.presentation.onboarding.paywall.view.ui.PaywallFeatureCard
import com.plantappmvi.android.presentation.onboarding.paywall.view.ui.PaywallPlanTile
import com.plantappmvi.android.presentation.onboarding.paywall.viewmodel.PaywallViewModel

@Composable
fun PaywallRoute() {
    PaywallScreen()
}

@Composable
internal fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
) {
    BasicScreen(viewModel) {
        PaywallContent(
            props = mapStateToProps(
                state = viewModel.state.collectAsStateWithLifecycle().value,
                onRetryClick = viewModel::onRetryClick,
                onPlanClick = viewModel::onPlanClick,
                onSubscribeClick = viewModel::onSubscribeClick,
                onCloseClick = viewModel::onCloseClick,
            ),
        )
    }
}

/**
 * Hero, benefits, plans, call to action and legal footer.
 *
 * The premium palette is used regardless of system brightness — this screen is
 * dark by design in both themes.
 */
@Composable
private fun PaywallContent(
    props: PaywallScreenProps,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.premiumCanvas),
    ) {
        when {
            props.showInitialLoading -> AppLoader(modifier = Modifier.align(Alignment.Center))

            props.showPlansError -> ErrorState(
                props = ErrorStateProps(
                    message = props.errorMessage,
                    onRetry = props.onRetryClick,
                ),
                modifier = Modifier.align(Alignment.Center),
            )

            else -> PaywallBody(props)
        }

        CloseButton(props)
    }
}

@Composable
private fun PaywallBody(props: PaywallScreenProps) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { PaywallHero(props) }

        // The gap above the first tile is the design's own break between the
        // feature strip and the plans, and it is wider than the one between
        // the tiles. Spacing the list evenly instead left the strip crowding
        // the first tile and a stray gap under the last one.
        itemsIndexed(props.plans, key = { _, plan -> plan.id }) { index, plan ->
            PaywallPlanTile(
                props = plan,
                onClick = { props.onPlanClick(plan.id) },
                modifier = Modifier.padding(
                    start = AppTheme.dimens.pageGutter,
                    end = AppTheme.dimens.pageGutter,
                    top = if (index == 0) AppTheme.dimens.spaceXl else AppTheme.dimens.spaceLg,
                ),
            )
        }

        item { PaywallFooter(props) }
    }
}

/**
 * The photo, the title over it, and the feature strip sitting on its lower
 * edge.
 *
 * The design overlaps all three inside the photo's own box rather than
 * stacking them down the page, which is why this is a Box and not a column of
 * sections.
 */
@Composable
private fun PaywallHero(props: PaywallScreenProps) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(HeroAspectRatio),
    ) {
        Image(
            painter = painterResource(R.drawable.img_paywall_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )

        // The export already fades towards black; this carries the fade the
        // rest of the way into the page colour so there is no visible seam,
        // and darkens the ground the title and cards sit on.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            FadeStart to Color.Transparent,
                            1f to AppTheme.colors.premiumCanvas,
                        ),
                    ),
                ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceXl),
            modifier = Modifier.align(Alignment.BottomStart),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceXs),
                modifier = Modifier.padding(horizontal = AppTheme.dimens.pageGutter),
            ) {
                PremiumTitle(props)
                Text(
                    text = props.heroSubtitle.asString(),
                    style = AppTheme.typography.bodyMd,
                    color = AppTheme.colors.onPremiumMuted,
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
                contentPadding = PaddingValues(horizontal = AppTheme.dimens.pageGutter),
            ) {
                items(props.features) { feature -> PaywallFeatureCard(feature) }
            }
        }
    }
}

/** "PlantApp Premium", with the product name carrying the weight. */
@Composable
private fun PremiumTitle(props: PaywallScreenProps) {
    val title = props.heroTitle.asString()
    val emphasis = props.heroTitleEmphasis.asString()
    val split = title.indexOf(emphasis)

    val annotated = buildAnnotatedString {
        if (split < 0) {
            append(title)
        } else {
            withStyle(SpanStyle(fontWeight = HeadlineEmphasisWeight)) { append(emphasis) }
            append(title.substring(split + emphasis.length))
        }
    }

    Text(
        text = annotated,
        style = AppTheme.typography.displayMd,
        color = AppTheme.colors.onPremium,
        modifier = Modifier.semantics { contentDescription = title },
    )
}

@Composable
private fun PaywallFooter(props: PaywallScreenProps) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(
                start = AppTheme.dimens.pageGutter,
                end = AppTheme.dimens.pageGutter,
                top = AppTheme.dimens.spaceLg,
                bottom = AppTheme.dimens.spaceMd,
            )
            .navigationBarsPadding(),
    ) {
        PrimaryButton(
            props = PrimaryButtonProps(
                text = props.ctaText,
                isEnabled = props.isCtaEnabled,
                isLoading = props.isSubmitting,
                onClick = props.onSubscribeClick,
            ),
        )

        Text(
            text = props.legalText.asString(),
            textAlign = TextAlign.Center,
            style = AppTheme.typography.caption,
            color = AppTheme.colors.onPremiumMuted.copy(alpha = LegalAlpha),
            modifier = Modifier.padding(top = AppTheme.dimens.spaceMd),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
            modifier = Modifier.padding(top = AppTheme.dimens.spaceSm),
        ) {
            props.footerLinks.forEachIndexed { index, link ->
                if (index > 0) {
                    Text(
                        text = "•",
                        style = AppTheme.typography.caption,
                        color = AppTheme.colors.onPremiumMuted.copy(alpha = DotAlpha),
                    )
                }
                Text(
                    text = link.asString(),
                    style = AppTheme.typography.caption,
                    color = AppTheme.colors.onPremiumMuted.copy(alpha = LinkAlpha),
                )
            }
        }
    }
}

/** The control that actually ends onboarding, per the case brief. */
@Composable
private fun BoxScope.CloseButton(props: PaywallScreenProps) {
    val label = props.closeLabel.asString()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .statusBarsPadding()
            .padding(top = AppTheme.dimens.spaceSm, end = AppTheme.dimens.pageGutter)
            .size(CloseButtonSize)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = CloseButtonScrimAlpha))
            .noRippleClickable(onClick = props.onCloseClick)
            .semantics {
                contentDescription = label
                role = Role.Button
            },
    ) {
        AppIcon(
            icon = AppIcons.Close,
            size = AppTheme.dimens.iconSm,
            tint = AppTheme.colors.onPremium,
        )
    }
}

/**
 * The export's own proportions — the design runs it edge to edge and lets the
 * feature strip finish on its bottom edge.
 */
private const val HeroAspectRatio = 375f / 470f

/** Where the fade into the page colour begins, down the hero. */
private const val FadeStart = 0.45f

private const val LegalAlpha = 0.6f
private const val LinkAlpha = 0.85f
private const val DotAlpha = 0.5f
private const val CloseButtonScrimAlpha = 0.45f
private val CloseButtonSize = 32.dp

@DayNightPreviews
@Composable
private fun PaywallContentPreview() = AppTheme {
    PaywallContent(PaywallScreenProps.preview())
}
