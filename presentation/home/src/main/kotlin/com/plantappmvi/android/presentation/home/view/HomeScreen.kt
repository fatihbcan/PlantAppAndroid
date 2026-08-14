package com.plantappmvi.android.presentation.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plantappmvi.android.core.designsystem.component.AppLoader
import com.plantappmvi.android.core.designsystem.component.ErrorState
import com.plantappmvi.android.core.designsystem.component.ErrorStateProps
import com.plantappmvi.android.core.designsystem.modifier.fullBleed
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.asString
import com.plantappmvi.android.core.presentation.view.BasicScreen
import com.plantappmvi.android.presentation.home.view.props.HomeScreenProps
import com.plantappmvi.android.presentation.home.view.props.mapStateToProps
import com.plantappmvi.android.presentation.home.view.ui.CategoryTile
import com.plantappmvi.android.presentation.home.view.ui.HomeBottomBar
import com.plantappmvi.android.presentation.home.view.ui.HomeHeader
import com.plantappmvi.android.presentation.home.view.ui.HomePremiumBanner
import com.plantappmvi.android.presentation.home.view.ui.QuestionCard
import com.plantappmvi.android.presentation.home.view.ui.QuestionCardAspect
import com.plantappmvi.android.presentation.home.view.ui.QuestionCardWidthFactor
import com.plantappmvi.android.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    BasicScreen(viewModel) {
        HomeContent(
            props = mapStateToProps(
                state = viewModel.state.collectAsStateWithLifecycle().value,
                onRefresh = viewModel::onRefresh,
                onQueryChange = viewModel::onQueryChange,
                onClearSearch = viewModel::onClearSearch,
            ),
        )
    }
}

/**
 * Greeting, search, premium banner, article carousel and the categories grid,
 * all inside one scroll view, with the design's bar pinned below them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    props: HomeScreenProps,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvas)
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (props.showInitialLoading) {
                AppLoader(modifier = Modifier.align(Alignment.Center))
            } else {
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = props.onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    HomeSections(props)
                }
            }
        }

        HomeBottomBar(props, modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun HomeSections(props: HomeScreenProps) {
    val gutter = AppTheme.dimens.pageGutter
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * QuestionCardWidthFactor

    LazyVerticalGrid(
        // Two columns on a phone, three once there is room for them.
        columns = GridCells.Fixed(if (screenWidth > WideBreakpoint) 3 else 2),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceLg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceLg),
        // One gutter for the whole page, declared once. The bands that have to
        // reach the screen edges escape it with `fullBleed` rather than the
        // page giving up and using a second scroll container.
        contentPadding = PaddingValues(
            start = gutter,
            end = gutter,
            bottom = AppTheme.dimens.spaceXxl,
        ),
        modifier = Modifier.fillMaxSize(),
    ) {
        fullWidth { HomeHeader(props, modifier = Modifier.fullBleed(gutter)) }
        fullWidth { HomePremiumBanner(props) }

        questionsSection(props, gutter, cardWidth)
        categoriesSection(props, gutter)
    }
}

/** The "Get Started" carousel, or the message that says why it is missing. */
private fun LazyGridScope.questionsSection(
    props: HomeScreenProps,
    gutter: Dp,
    cardWidth: Dp,
) {
    if (props.showQuestions) {
        fullWidth {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
                contentPadding = PaddingValues(horizontal = gutter),
                modifier = Modifier
                    .fullBleed(gutter)
                    .fillMaxWidth()
                    .height(cardWidth * QuestionCardAspect),
            ) {
                items(props.questions, key = { it.id }) { question ->
                    QuestionCard(props = question, width = cardWidth)
                }
            }
        }
    }

    if (props.showQuestionsError) {
        fullWidth {
            ErrorState(
                ErrorStateProps(
                    message = props.questionsErrorMessage,
                    onRetry = props.onRefresh,
                ),
            )
        }
    }
}

/**
 * The grid, or — in place of it — the section's own failure, or the note that
 * a search matched nothing. Only one of the three is ever on screen.
 */
private fun LazyGridScope.categoriesSection(props: HomeScreenProps, gutter: Dp) {
    when {
        props.showCategoriesError -> fullWidth {
            ErrorState(
                ErrorStateProps(
                    message = props.categoriesErrorMessage,
                    onRetry = props.onRefresh,
                ),
            )
        }

        props.emptySearchMessage != null -> fullWidth {
            Text(
                text = props.emptySearchMessage.asString(),
                textAlign = TextAlign.Center,
                style = AppTheme.typography.bodyMd,
                color = AppTheme.colors.onCanvasMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gutter),
            )
        }

        else -> items(props.categories, key = { it.id }) { category ->
            // Very nearly square, as in the design — an early pass made these
            // half as tall, which left the artwork in them looking cramped.
            CategoryTile(props = category, modifier = Modifier.aspectRatio(CategoryTileAspect))
        }
    }
}

/**
 * A row that spans every column.
 *
 * The header, the banner and the carousel are full-bleed sections rather than
 * cells, but they scroll with the grid — so they are grid items with a full
 * span rather than a separate scroll container stacked above it, which would
 * give the page two independent scroll positions.
 */
private fun LazyGridScope.fullWidth(content: @Composable () -> Unit) =
    item(span = { GridItemSpan(maxLineSpan) }) { content() }

private const val CategoryTileAspect = 0.98f
private val WideBreakpoint = 600.dp

@DayNightPreviews
@Composable
private fun HomeContentPreview() = AppTheme {
    HomeContent(HomeScreenProps.preview())
}
