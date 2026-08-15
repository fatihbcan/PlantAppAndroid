package com.plantappmvi.android.presentation.home.view.props

import androidx.compose.runtime.Immutable
import com.plantappmvi.android.core.designsystem.icon.AppIcons
import com.plantappmvi.android.core.designsystem.R as DesignSystemR
import com.plantappmvi.android.core.presentation.resource.IconResource
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question
import com.plantappmvi.android.presentation.home.R
import com.plantappmvi.android.presentation.home.model.HomeFailure
import com.plantappmvi.android.presentation.home.model.HomeScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class QuestionCardProps(
    val id: Int,
    val title: TextResource,
    val imageUrl: String,
)

@Immutable
internal data class CategoryTileProps(
    val id: Int,
    val title: TextResource,
    val imageUrl: String,
    val semanticsLabel: TextResource,
)

@Immutable
internal data class NavDestinationProps(
    val icon: IconResource,
    val label: TextResource,
    val isCurrent: Boolean,
)

@Immutable
internal data class HomeScreenProps(
    val showInitialLoading: Boolean,
    val greeting: TextResource,
    val salutation: TextResource,
    val searchHint: TextResource,
    val query: String,
    val bannerTitle: TextResource,
    val bannerBody: TextResource,
    val questions: ImmutableList<QuestionCardProps>,
    val showQuestions: Boolean,
    val showQuestionsError: Boolean,
    val questionsErrorMessage: TextResource,
    val categories: ImmutableList<CategoryTileProps>,
    val showCategoriesError: Boolean,
    val categoriesErrorMessage: TextResource,
    val emptySearchMessage: TextResource?,
    val destinations: ImmutableList<NavDestinationProps>,
    val scanLabel: TextResource,
    val onRefresh: () -> Unit = {},
    val onQueryChange: (String) -> Unit = {},
    val onClearSearch: () -> Unit = {},
    val onPremiumBannerClick: () -> Unit = {},
) {
    companion object {
        fun preview() = mapStateToProps(
            HomeScreenState.initial().copy(
                isLoading = false,
                questions = listOf(
                    Question(
                        id = 1,
                        title = "How to water your plant",
                        subtitle = "",
                        imageUrl = "",
                        articleUrl = "",
                    ),
                    Question(
                        id = 2,
                        title = "Light for beginners",
                        subtitle = "",
                        imageUrl = "",
                        articleUrl = "",
                        order = 1,
                    ),
                ),
                categories = listOf(
                    Category(id = 1, title = "Ferns", imageUrl = ""),
                    Category(id = 2, title = "Succulents", imageUrl = ""),
                    Category(id = 3, title = "Cacti", imageUrl = ""),
                    Category(id = 4, title = "Herbs", imageUrl = ""),
                ),
            ),
        )
    }
}

/**
 * Every presentation decision this screen makes, in one pure function:
 * which sections show, which failure message each one gets, and the
 * domain→UI mapping of a category or article onto its card.
 */
internal fun mapStateToProps(
    state: HomeScreenState,
    onRefresh: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onPremiumBannerClick: () -> Unit = {},
): HomeScreenProps = HomeScreenProps(
    showInitialLoading = state.isInitialLoading,
    greeting = TextResource.fromId(R.string.home_greeting),
    salutation = TextResource.fromId(R.string.home_salutation),
    searchHint = TextResource.fromId(R.string.home_search_hint),
    query = state.query,
    bannerTitle = TextResource.fromId(R.string.home_premium_banner_title),
    bannerBody = TextResource.fromId(R.string.home_premium_banner_body),
    questions = state.questions.map { it.toProps() }.toImmutableList(),
    showQuestions = state.showsQuestions,
    showQuestionsError = state.showsQuestionsError,
    questionsErrorMessage = state.questionsFailure.toMessage(R.string.home_questions_error),
    categories = state.visibleCategories.map { it.toProps() }.toImmutableList(),
    showCategoriesError = state.showsCategoriesError,
    categoriesErrorMessage = state.categoriesFailure.toMessage(R.string.home_categories_error),
    emptySearchMessage = if (state.hasNoSearchResults) {
        TextResource.fromId(R.string.home_search_empty, state.appliedQuery)
    } else {
        null
    },
    destinations = Destinations,
    scanLabel = TextResource.fromId(R.string.nav_scan),
    onRefresh = onRefresh,
    onQueryChange = onQueryChange,
    onClearSearch = onClearSearch,
    onPremiumBannerClick = onPremiumBannerClick,
)

private fun Question.toProps() = QuestionCardProps(
    id = id,
    title = TextResource.fromString(title),
    imageUrl = imageUrl,
)

private fun Category.toProps() = CategoryTileProps(
    id = id,
    title = TextResource.fromString(title),
    imageUrl = imageUrl,
    semanticsLabel = TextResource.fromId(R.string.home_category_item_semantics, title),
)

/**
 * A transport failure gets the shared "no connection" line; everything else
 * gets the section's own wording, because the user can act on the first and
 * cannot on the second.
 */
private fun HomeFailure?.toMessage(fallbackId: Int): TextResource = when (this) {
    HomeFailure.NETWORK -> TextResource.fromId(DesignSystemR.string.error_no_connection)
    else -> TextResource.fromId(fallbackId)
}

/**
 * The design's five destinations. Only Home has a screen in this case, so the
 * other four render and are marked unselected rather than posing as buttons
 * that silently do nothing — the raised scan control is the bar's one live
 * affordance.
 */
private val Destinations: ImmutableList<NavDestinationProps> = persistentListOf(
    NavDestinationProps(AppIcons.Pot, TextResource.fromId(R.string.nav_home), isCurrent = true),
    NavDestinationProps(
        AppIcons.ShieldPlus,
        TextResource.fromId(R.string.nav_diagnose),
        isCurrent = false,
    ),
    NavDestinationProps(
        AppIcons.Leaf,
        TextResource.fromId(R.string.nav_my_garden),
        isCurrent = false,
    ),
    NavDestinationProps(
        AppIcons.Person,
        TextResource.fromId(R.string.nav_profile),
        isCurrent = false,
    ),
)
