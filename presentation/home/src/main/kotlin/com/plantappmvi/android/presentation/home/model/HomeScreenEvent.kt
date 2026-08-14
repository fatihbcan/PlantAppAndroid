package com.plantappmvi.android.presentation.home.model

import com.plantappmvi.android.core.presentation.mvi.ScreenEvent
import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question

internal sealed interface HomeScreenEvent : ScreenEvent<HomeScreenState> {

    data object LoadStarted : HomeScreenEvent {
        override fun reduce(oldState: HomeScreenState) = oldState.copy(
            isLoading = true,
            questionsFailure = null,
            categoriesFailure = null,
        )
    }

    /**
     * Both halves land in one event.
     *
     * They are fetched in parallel and fail independently, so folding them in
     * separately would emit an intermediate state where one list is fresh and
     * the other is still the previous load's — a flicker with no meaning.
     */
    data class ContentLoaded(
        val questions: List<Question>?,
        val questionsFailure: HomeFailure?,
        val categories: List<Category>?,
        val categoriesFailure: HomeFailure?,
    ) : HomeScreenEvent {
        override fun reduce(oldState: HomeScreenState) = oldState.copy(
            isLoading = false,
            // A failed half keeps whatever it was already showing: stale
            // content beats an empty section.
            questions = questions ?: oldState.questions,
            categories = categories ?: oldState.categories,
            questionsFailure = questionsFailure,
            categoriesFailure = categoriesFailure,
        )
    }

    /** The user typed. Applied to the field immediately, to the filter later. */
    data class SearchQueryChanged(val query: String) : HomeScreenEvent {
        override fun reduce(oldState: HomeScreenState) = oldState.copy(query = query)
    }

    /** The debounce elapsed and the filter may catch up with the field. */
    data class SearchQueryApplied(val query: String) : HomeScreenEvent {
        override fun reduce(oldState: HomeScreenState) = oldState.copy(appliedQuery = query)
    }

    data object SearchCleared : HomeScreenEvent {
        override fun reduce(oldState: HomeScreenState) =
            oldState.copy(query = "", appliedQuery = "")
    }
}
