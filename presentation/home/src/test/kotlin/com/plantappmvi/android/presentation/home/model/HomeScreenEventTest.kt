package com.plantappmvi.android.presentation.home.model

import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * Reducers are pure functions of (old state, event), which makes these the
 * cheapest tests in the project — no coroutines, no mocks, no dispatcher.
 */
class HomeScreenEventTest {

    private val initial = HomeScreenState.initial()

    @Test
    fun `LoadStarted raises the flag and clears both failures`() {
        val old = initial.copy(
            isLoading = false,
            questionsFailure = HomeFailure.NETWORK,
            categoriesFailure = HomeFailure.SERVER,
        )

        val new = HomeScreenEvent.LoadStarted.reduce(old)

        new.isLoading shouldBe true
        new.questionsFailure shouldBe null
        new.categoriesFailure shouldBe null
    }

    @Test
    fun `ContentLoaded folds both halves in at once`() {
        val questions = listOf(Question(1, "Watering", "", "", "", 0))
        val categories = listOf(Category(1, "Ferns", ""))

        val new = HomeScreenEvent.ContentLoaded(
            questions = questions,
            questionsFailure = null,
            categories = categories,
            categoriesFailure = null,
        ).reduce(initial)

        new.isLoading shouldBe false
        new.questions shouldBe questions
        new.categories shouldBe categories
    }

    @Test
    fun `a failed half keeps whatever it was already showing`() {
        val old = initial.copy(
            isLoading = false,
            categories = listOf(Category(1, "Ferns", "")),
            questions = listOf(Question(1, "Watering", "", "", "", 0)),
        )

        val new = HomeScreenEvent.ContentLoaded(
            questions = null,
            questionsFailure = HomeFailure.NETWORK,
            categories = listOf(Category(2, "Cacti", "")),
            categoriesFailure = null,
        ).reduce(old)

        // Stale articles beat an empty carousel; the fresh categories land.
        new.questions shouldBe old.questions
        new.questionsFailure shouldBe HomeFailure.NETWORK
        new.categories.single().title shouldBe "Cacti"
    }

    @Test
    fun `SearchQueryChanged moves the field but not the filter`() {
        val new = HomeScreenEvent.SearchQueryChanged("fer").reduce(initial)

        new.query shouldBe "fer"
        new.appliedQuery shouldBe ""
    }

    @Test
    fun `SearchQueryApplied moves the filter`() {
        val old = HomeScreenEvent.SearchQueryChanged("fer").reduce(initial)

        val new = HomeScreenEvent.SearchQueryApplied("fer").reduce(old)

        new.query shouldBe "fer"
        new.appliedQuery shouldBe "fer"
    }

    @Test
    fun `SearchCleared resets both, so no stale filter survives`() {
        val old = initial.copy(query = "fern", appliedQuery = "fern")

        val new = HomeScreenEvent.SearchCleared.reduce(old)

        new.query shouldBe ""
        new.appliedQuery shouldBe ""
    }
}
