package com.plantappmvi.android.presentation.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.plantappmvi.android.core.presentation.viewmodel.BasicViewModel
import com.plantappmvi.android.domain.home.data.GetCategoriesResult
import com.plantappmvi.android.domain.home.data.GetQuestionsResult
import com.plantappmvi.android.domain.home.usecase.GetHomeContentUseCase
import com.plantappmvi.android.presentation.home.model.HomeFailure
import com.plantappmvi.android.presentation.home.model.HomeScreenEvent
import com.plantappmvi.android.presentation.home.model.HomeScreenState
import com.plantappmvi.android.presentation.home.model.HomeScreenStateStore
import com.plantappmvi.android.presentation.home.navigation.HomeNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Loads the two home collections and owns the search query.
 *
 * Where the Flutter build declares a `bloc_concurrency` transformer per event,
 * this does the same two jobs with coroutines directly:
 *
 * - **`droppable()`** for load and refresh becomes [loadJob]'s active check, so
 *   a mashed retry cannot fan out into overlapping requests.
 * - **`restartable()` behind a debounce** for search becomes a
 *   [MutableStateFlow] with [debounce] and [collectLatest], which drops a
 *   stale query the same way.
 *
 * Structured concurrency then adds what Bloc cannot: `viewModelScope` really
 * cancels an in-flight request when the screen goes away, so none of the
 * "is the sink still open" guarding the Flutter version needs is required here.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    stateStore: HomeScreenStateStore,
    override val navigator: HomeNavigator,
    private val getHomeContent: GetHomeContentUseCase,
) : BasicViewModel<HomeScreenState, HomeScreenEvent>(stateStore) {

    private var loadJob: Job? = null
    private val queryInput = MutableStateFlow("")

    init {
        load()
        viewModelScope.launch {
            queryInput
                .debounce(SearchDebounce)
                .collectLatest { sendEvent(HomeScreenEvent.SearchQueryApplied(it)) }
        }
    }

    fun onRefresh() = load()

    /**
     * The premium strip is the one place home leaves itself.
     *
     * Guarded by [launchNavigationOnce] rather than a plain call: the strip is
     * a wide target in a scrolling list, and a double tap would otherwise push
     * two paywalls. The guard re-arms on the next `onResume`, so coming back
     * from the paywall leaves the strip live again.
     */
    fun onPremiumBannerClick() {
        launchNavigationOnce {
            navigator.paywall()
            true
        }
    }

    fun onQueryChange(query: String) {
        if (query == state.value.query) return
        sendEvent(HomeScreenEvent.SearchQueryChanged(query))
        queryInput.value = query
    }

    fun onClearSearch() {
        sendEvent(HomeScreenEvent.SearchCleared)
        queryInput.value = ""
    }

    private fun load() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            sendEvent(HomeScreenEvent.LoadStarted)

            val content = getHomeContent()
            val questions = content.questions
            val categories = content.categories

            sendEvent(
                HomeScreenEvent.ContentLoaded(
                    questions = (questions as? GetQuestionsResult.Success)?.questions,
                    questionsFailure = (questions as? GetQuestionsResult.Error)?.toFailure(),
                    categories = (categories as? GetCategoriesResult.Success)?.categories,
                    categoriesFailure = (categories as? GetCategoriesResult.Error)?.toFailure(),
                ),
            )
        }
    }
}

private fun GetQuestionsResult.Error.toFailure(): HomeFailure = when (this) {
    is GetQuestionsResult.Error.Network -> HomeFailure.NETWORK
    is GetQuestionsResult.Error.Server -> HomeFailure.SERVER
    is GetQuestionsResult.Error.Parse -> HomeFailure.PARSE
    is GetQuestionsResult.Error.Unknown -> HomeFailure.UNKNOWN
}

private fun GetCategoriesResult.Error.toFailure(): HomeFailure = when (this) {
    is GetCategoriesResult.Error.Network -> HomeFailure.NETWORK
    is GetCategoriesResult.Error.Server -> HomeFailure.SERVER
    is GetCategoriesResult.Error.Parse -> HomeFailure.PARSE
    is GetCategoriesResult.Error.Unknown -> HomeFailure.UNKNOWN
}

/**
 * Search is local, so this buys no network savings — but it still spares a
 * filter pass and a recomposition per keystroke, and it is what a server-side
 * search would need.
 */
private val SearchDebounce = 250.milliseconds
