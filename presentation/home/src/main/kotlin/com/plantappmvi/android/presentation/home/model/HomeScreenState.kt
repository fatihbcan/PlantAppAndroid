package com.plantappmvi.android.presentation.home.model

import com.plantappmvi.android.core.presentation.mvi.ScreenState
import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question

/** UI-facing failure categories, mapped from the domain result types. */
internal enum class HomeFailure { NETWORK, SERVER, PARSE, UNKNOWN }

/**
 * Flat fields, not a sealed `Loading | Success | Error`.
 *
 * This screen is routinely several of those at once: refreshing while showing
 * a populated grid while the articles endpoint is down. A sealed hierarchy
 * cannot express that without lying, which is why the two endpoints also get a
 * failure field each rather than sharing one.
 */
internal data class HomeScreenState(
    val isLoading: Boolean,
    val questions: List<Question>,
    val categories: List<Category>,
    /** What the field shows — updated on every keystroke. */
    val query: String,
    /** What the grid filters by — updated after the debounce. */
    val appliedQuery: String,
    val questionsFailure: HomeFailure?,
    val categoriesFailure: HomeFailure?,
) : ScreenState {

    /**
     * Categories narrowed by the search field.
     *
     * Filtering lives here rather than in a composable, so it is unit-testable
     * with no Compose runtime at all.
     */
    val visibleCategories: List<Category>
        get() {
            val needle = appliedQuery.trim()
            if (needle.isEmpty()) return categories
            return categories.filter { it.title.contains(needle, ignoreCase = true) }
        }

    val isSearching: Boolean get() = appliedQuery.isNotBlank()

    /**
     * The articles carousel is hidden while searching — the design shows
     * search results as a category list only.
     */
    val showsQuestions: Boolean get() = !isSearching && questions.isNotEmpty()

    val showsQuestionsError: Boolean
        get() = !isSearching && questions.isEmpty() && questionsFailure != null

    val showsCategoriesError: Boolean
        get() = categories.isEmpty() && categoriesFailure != null

    /** True when there is genuinely nothing on screen yet. */
    val isInitialLoading: Boolean
        get() = isLoading && questions.isEmpty() && categories.isEmpty()

    /** A search that matched nothing, as opposed to an empty data set. */
    val hasNoSearchResults: Boolean get() = isSearching && visibleCategories.isEmpty()

    companion object {
        fun initial() = HomeScreenState(
            isLoading = true,
            questions = emptyList(),
            categories = emptyList(),
            query = "",
            appliedQuery = "",
            questionsFailure = null,
            categoriesFailure = null,
        )
    }
}
