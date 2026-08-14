package com.plantappmvi.android.presentation.home.view.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plantappmvi.android.core.designsystem.component.AppAsyncImage
import com.plantappmvi.android.core.designsystem.component.AppIcon
import com.plantappmvi.android.core.designsystem.component.AppSearchField
import com.plantappmvi.android.core.designsystem.component.AppSearchFieldProps
import com.plantappmvi.android.core.designsystem.icon.AppIcons
import com.plantappmvi.android.core.designsystem.modifier.noRippleClickable
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.resource.asString
import com.plantappmvi.android.presentation.home.R
import com.plantappmvi.android.presentation.home.view.props.CategoryTileProps
import com.plantappmvi.android.presentation.home.view.props.HomeScreenProps
import com.plantappmvi.android.presentation.home.view.props.NavDestinationProps
import com.plantappmvi.android.presentation.home.view.props.QuestionCardProps

/**
 * The band at the top of home: greeting, search, and the two painted leaves
 * tucked in behind them.
 *
 * The band is the page colour, not a tint — what separates it from the content
 * below is the artwork, which is clipped off at the band's lower edge. Both
 * leaves are anchored to that edge and run past it, so the clip cuts them
 * exactly where the design does.
 */
@Composable
internal fun HomeHeader(
    props: HomeScreenProps,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.canvas)
            .clipToBounds(),
    ) {
        val width = maxWidth

        Image(
            painter = painterResource(R.drawable.img_header_leaf_left),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = -width * 0.05f, y = width * 0.14f)
                .width(width * 0.38f),
        )
        Image(
            painter = painterResource(R.drawable.img_header_leaf_right),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = width * 0.06f, y = width * 0.13f)
                .width(width * 0.34f),
        )

        Column(
            modifier = Modifier.padding(
                start = AppTheme.dimens.pageGutter,
                end = AppTheme.dimens.pageGutter,
                top = AppTheme.dimens.spaceXl,
                bottom = AppTheme.dimens.spaceLg,
            ),
        ) {
            Text(
                text = props.greeting.asString(),
                style = AppTheme.typography.bodyLg,
                color = AppTheme.colors.onCanvas,
            )
            Text(
                text = props.salutation.asString(),
                style = AppTheme.typography.titleLg,
                color = AppTheme.colors.onCanvas,
                modifier = Modifier.padding(top = AppTheme.dimens.spaceXxs),
            )
            AppSearchField(
                props = AppSearchFieldProps(
                    query = props.query,
                    placeholder = props.searchHint,
                    onQueryChange = props.onQueryChange,
                    onClear = props.onClearSearch,
                ),
                modifier = Modifier.padding(top = AppTheme.dimens.spaceLg),
            )
        }
    }
}

/**
 * The dark "FREE Premium Available" strip under the search field.
 *
 * Every value here — the flat ground, the two golds, the envelope
 * illustration — comes from the design's own export rather than being matched
 * by eye, including the fact that the second line is *darker* than the title.
 */
@Composable
internal fun HomePremiumBanner(
    props: HomeScreenProps,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
        modifier = modifier
            .fillMaxWidth()
            .height(BannerHeight)
            .clip(AppTheme.shapes.card)
            .background(AppTheme.colors.bannerSurface)
            .semantics { role = Role.Button }
            .padding(horizontal = AppTheme.dimens.spaceLg),
    ) {
        AppIcon(
            icon = AppIcons.EnvelopeBadge,
            size = EnvelopeWidth,
            height = EnvelopeWidth * EnvelopeAspect,
            tint = AppTheme.colors.premiumAccent,
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = props.bannerTitle.asString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.titleSm.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.premiumAccent,
            )
            Text(
                text = props.bannerBody.asString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.bodySm,
                color = AppTheme.colors.premiumAccentMuted,
            )
        }

        AppIcon(
            icon = AppIcons.ChevronRight,
            size = AppTheme.dimens.iconMd,
            tint = AppTheme.colors.premiumAccentMuted,
        )
    }
}

/** One article card in the horizontal "Get Started" carousel. */
@Composable
internal fun QuestionCard(
    props: QuestionCardProps,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val title = props.title.asString()

    Box(
        modifier = modifier
            .size(width = width, height = width * QuestionCardAspect)
            .clip(AppTheme.shapes.card)
            .semantics {
                contentDescription = title
                role = Role.Button
            },
    ) {
        AppAsyncImage(url = props.imageUrl, modifier = Modifier.fillMaxSize())

        // The design's scrim is a deep, late fade — the photo stays clear down
        // to two thirds of the card and then goes almost black behind the
        // title, rather than being greyed all over.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.6f to Color.Transparent,
                            0.8f to Color.Black.copy(alpha = 0.54f),
                            1f to Color.Black.copy(alpha = 0.80f),
                        ),
                    ),
                ),
        )

        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.titleSm,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(AppTheme.dimens.spaceLg),
        )
    }
}

/** One cell of the categories grid: title on the left, artwork on the right. */
@Composable
internal fun CategoryTile(
    props: CategoryTileProps,
    modifier: Modifier = Modifier,
) {
    val label = props.semanticsLabel.asString()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.card)
            // White cells with a hairline edge, as in the design — the tint
            // that was here first made the grid read as a block of chips.
            .background(AppTheme.colors.surface)
            .border(AppTheme.dimens.strokeThin, AppTheme.colors.outline, AppTheme.shapes.card)
            .semantics {
                contentDescription = label
                role = Role.Button
            },
    ) {
        // The artwork sits in the lower right and is allowed to run to the
        // cell's edges rather than being inset with the title.
        AppAsyncImage(
            url = props.imageUrl,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = AppTheme.dimens.spaceXxl, top = AppTheme.dimens.spaceXl)
                .fillMaxSize(),
        )

        Text(
            text = props.title.asString(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.titleMd.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.onCanvas,
            modifier = Modifier.padding(AppTheme.dimens.spaceLg),
        )
    }
}

/**
 * The five-destination bar from the design, with the scan control raised in
 * the middle.
 */
@Composable
internal fun HomeBottomBar(
    props: HomeScreenProps,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Room above the bar for the scan button to sit proud of it.
            .height(BarHeight + ScanOverhang),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(BarHeight)
                .background(AppTheme.colors.surface),
        ) {
            Destination(props.destinations[0], Modifier.weight(1f))
            Destination(props.destinations[1], Modifier.weight(1f))
            // The gap the scan button sits in — one column wide, so the four
            // labels stay on the design's fifths.
            Box(modifier = Modifier.weight(1f))
            Destination(props.destinations[2], Modifier.weight(1f))
            Destination(props.destinations[3], Modifier.weight(1f))
        }

        ScanButton(
            label = props.scanLabel.asString(),
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun Destination(props: NavDestinationProps, modifier: Modifier = Modifier) {
    val tint = if (props.isCurrent) AppTheme.colors.brand else AppTheme.colors.navInactive
    val label = props.label.asString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceXs),
        modifier = modifier.semantics { contentDescription = label },
    ) {
        AppIcon(icon = props.icon, size = AppTheme.dimens.iconMd, tint = tint)
        Text(text = label, style = AppTheme.typography.bodySm, color = tint)
    }
}

/**
 * The design rings this in a lighter green so it reads as lifted off the bar
 * rather than punched through it.
 */
@Composable
private fun ScanButton(label: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(ScanButtonSize)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(AppTheme.colors.brand.copy(alpha = ScanRingAlpha))
            .padding(ScanRingWidth)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(AppTheme.colors.brand)
            .noRippleClickable { }
            .semantics { contentDescription = label },
    ) {
        AppIcon(icon = AppIcons.Scan, size = ScanGlyphSize, tint = AppTheme.colors.onPremium)
    }
}

private val BannerHeight = 66.dp
private val EnvelopeWidth = 44.dp

/** Height over width of the exported illustration, badge included. */
private const val EnvelopeAspect = 48f / 52f

/**
 * Card height over width — the design's own proportion. The carousel sizes the
 * width from the viewport so a sliver of the next card shows, which signals
 * horizontal scroll without needing an affordance.
 */
internal const val QuestionCardAspect = 0.685f
internal const val QuestionCardWidthFactor = 0.66f

private val BarHeight = 64.dp
private val ScanOverhang = 24.dp
private val ScanButtonSize = 64.dp
private val ScanRingWidth = 4.dp
private val ScanGlyphSize = 26.dp
private const val ScanRingAlpha = 0.35f
