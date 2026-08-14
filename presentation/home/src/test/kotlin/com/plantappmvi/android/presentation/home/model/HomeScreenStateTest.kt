package com.plantappmvi.android.presentation.home.model

import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * The state's getters are where this screen's presentation logic lives, so
 * they are testable exactly like a pure function — no Compose runtime, no
 * Robolectric, no ViewModel.
 */
class HomeScreenStateTest {

    private val categories = listOf(
        Category(1, "Ferns", ""),
        Category(2, "Succulents", ""),
        Category(3, "Flowering Plants", ""),
    )

    private val loaded = HomeScreenState.initial().copy(
        isLoading = false,
        categories = categories,
        questions = listOf(Question(1, "Watering", "", "", "", 0)),
    )

    @Test
    fun `an empty query shows every category`() {
        loaded.visibleCategories shouldBe categories
    }

    @Test
    fun `the filter matches case-insensitively on a substring`() {
        val state = loaded.copy(appliedQuery = "fern")

        state.visibleCategories.map { it.title } shouldBe listOf("Ferns")
    }

    @Test
    fun `a whitespace-only query is not a search`() {
        val state = loaded.copy(appliedQuery = "   ")

        state.isSearching shouldBe false
        state.visibleCategories shouldBe categories
    }

    @Test
    fun `the filter follows the applied query, not the one being typed`() {
        // The field updates on every keystroke; the grid waits for the
        // debounce. Until it elapses the grid must still show the old result.
        val state = loaded.copy(query = "succ", appliedQuery = "")

        state.visibleCategories shouldBe categories
    }

    @Test
    fun `the articles carousel is hidden while searching`() {
        loaded.showsQuestions shouldBe true
        loaded.copy(appliedQuery = "fern").showsQuestions shouldBe false
    }

    @Test
    fun `a search that matches nothing is distinguished from an empty data set`() {
        val noMatch = loaded.copy(appliedQuery = "orchid")
        val noData = HomeScreenState.initial().copy(isLoading = false)

        noMatch.hasNoSearchResults shouldBe true
        noData.hasNoSearchResults shouldBe false
    }

    @Test
    fun `each endpoint's failure is reported independently`() {
        // The whole reason for two failure fields: a dead categories endpoint
        // must not blank a working carousel.
        val state = loaded.copy(
            categories = emptyList(),
            categoriesFailure = HomeFailure.NETWORK,
        )

        state.showsCategoriesError shouldBe true
        state.showsQuestionsError shouldBe false
        state.showsQuestions shouldBe true
    }

    @Test
    fun `a failure with content already on screen does not show an error`() {
        val state = loaded.copy(categoriesFailure = HomeFailure.SERVER)

        // Stale content beats an error message over content the user can read.
        state.showsCategoriesError shouldBe false
    }

    @Test
    fun `initial loading is only true while there is nothing at all to show`() {
        HomeScreenState.initial().isInitialLoading shouldBe true
        loaded.copy(isLoading = true).isInitialLoading shouldBe false
    }

    @Test
    fun `the questions error is suppressed while searching`() {
        val state = loaded.copy(questions = emptyList(), questionsFailure = HomeFailure.NETWORK)

        state.showsQuestionsError shouldBe true
        state.copy(appliedQuery = "fern").showsQuestionsError shouldBe false
    }
}
