package com.plantappmvi.android.presentation.onboarding.intro.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plantappmvi.android.core.designsystem.component.PrimaryButton
import com.plantappmvi.android.core.designsystem.component.PrimaryButtonProps
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.asString
import com.plantappmvi.android.core.presentation.view.BasicScreen
import com.plantappmvi.android.presentation.onboarding.intro.view.props.IntroPageProps
import com.plantappmvi.android.presentation.onboarding.intro.view.props.IntroScreenProps
import com.plantappmvi.android.presentation.onboarding.intro.view.props.mapStateToProps
import com.plantappmvi.android.presentation.onboarding.intro.view.ui.IntroArtworkView
import com.plantappmvi.android.presentation.onboarding.intro.view.ui.IntroHeadline
import com.plantappmvi.android.presentation.onboarding.intro.view.ui.IntroLegalText
import com.plantappmvi.android.presentation.onboarding.intro.view.ui.IntroPageDots
import com.plantappmvi.android.presentation.onboarding.intro.viewmodel.IntroViewModel

/** The composition root's only entry point into this screen. */
@Composable
fun IntroRoute() {
    IntroScreen()
}

@Composable
internal fun IntroScreen(
    viewModel: IntroViewModel = hiltViewModel(),
) {
    BasicScreen(viewModel) {
        IntroContent(
            props = mapStateToProps(
                state = viewModel.state.collectAsStateWithLifecycle().value,
                onNextClick = viewModel::onNextClick,
                onPageSwiped = viewModel::onPageSwiped,
            ),
        )
    }
}

@Composable
private fun IntroContent(
    props: IntroScreenProps,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { props.pages.size })

    // The pager owns a scroll position, which is view state, not screen state.
    // These two effects keep it and the single source of truth in step: a
    // settled swipe reports up, and a page change from the call to action
    // animates down.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect(props.onPageSwiped)
    }
    LaunchedEffect(props.currentPage) {
        if (pagerState.currentPage != props.currentPage) {
            pagerState.animateScrollToPage(props.currentPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvas)
            .statusBarsPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            IntroPageBody(props.pages[page])
        }

        PrimaryButton(
            props = PrimaryButtonProps(
                text = props.currentCta,
                onClick = props.onNextClick,
            ),
            modifier = Modifier.padding(horizontal = AppTheme.dimens.pageGutter),
        )

        IntroFooter(props)
    }
}

/**
 * The footer holds a fixed height whichever page is showing, so the call to
 * action sits at the same point on all three rather than jumping as the
 * footer swaps between the consent line and the dots.
 */
@Composable
private fun IntroFooter(props: IntroScreenProps) {
    val legal = props.currentLegal

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppTheme.dimens.spaceLg)
            .height(FooterHeight)
            .navigationBarsPadding(),
    ) {
        when {
            legal != null -> IntroLegalText(
                text = legal.asString(),
                underlinedPhrases = props.underlinedLegalPhrases.map { it.asString() },
                // Held to the design's measure so the sentence breaks after
                // "PlantID" rather than stretching to the gutters.
                modifier = Modifier.widthIn(max = LegalMaxWidth),
            )

            props.showsPageIndicator -> IntroPageDots(
                count = props.indicatorCount,
                activeIndex = props.indicatorIndex,
                label = props.indicatorLabel.asString(),
            )
        }
    }
}

/**
 * One intro page: copy on top, artwork filling what is left below it.
 *
 * The copy takes its natural height and the artwork absorbs the rest, which is
 * what keeps the headline pinned near the top of a tall screen instead of
 * floating down with the illustration.
 */
@Composable
private fun IntroPageBody(page: IntroPageProps) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
            modifier = Modifier.padding(
                start = AppTheme.dimens.pageGutter,
                end = AppTheme.dimens.pageGutter,
                top = AppTheme.dimens.spaceXl,
            ),
        ) {
            IntroHeadline(
                text = page.title.asString(),
                highlight = page.highlight?.asString(),
                isHighlightUnderlined = page.isHighlightUnderlined,
            )
            page.body?.let {
                Text(
                    text = it.asString(),
                    style = AppTheme.typography.bodyLg,
                    color = AppTheme.colors.onCanvasMuted,
                )
            }
        }

        IntroArtworkView(
            artwork = page.artwork,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Room for two lines of consent copy — the taller of the two footers. */
private val FooterHeight = 34.dp
private val LegalMaxWidth = 236.dp

private class IntroPagePreviewProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(0, 1, 2)
}

@DayNightPreviews
@Composable
private fun IntroContentPreview(
    @PreviewParameter(IntroPagePreviewProvider::class) page: Int,
) = AppTheme {
    IntroContent(IntroScreenProps.preview().copy(currentPage = page))
}
