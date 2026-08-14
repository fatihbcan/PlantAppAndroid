package com.plantappmvi.android.domain.home.usecase

import com.plantappmvi.android.domain.home.data.GetCategoriesResult
import com.plantappmvi.android.domain.home.data.GetQuestionsResult
import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question
import com.plantappmvi.android.domain.home.repository.HomeRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetHomeContentUseCaseTest {

    private val repository: HomeRepository = mockk()
    private val useCase = GetHomeContentUseCase(
        getCategories = GetCategoriesUseCase(repository),
        getQuestions = GetQuestionsUseCase(repository),
    )

    @Test
    fun `both endpoints are fetched in parallel, not one after the other`() = runTest {
        // Deliberately slow stubs. An instantly-completing mock never overlaps
        // with anything, so a sequential implementation would pass this test
        // for the wrong reason.
        coEvery { repository.getCategories() } coAnswers {
            delay(ENDPOINT_DELAY_MS)
            GetCategoriesResult.Success(emptyList())
        }
        coEvery { repository.getQuestions() } coAnswers {
            delay(ENDPOINT_DELAY_MS)
            GetQuestionsResult.Success(emptyList())
        }

        val start = currentTime
        useCase()
        val elapsed = currentTime - start

        elapsed shouldBe ENDPOINT_DELAY_MS
    }

    @Test
    fun `a failure in one endpoint leaves the other intact`() = runTest {
        val categories = listOf(Category(1, "Ferns", ""))
        coEvery { repository.getCategories() } returns GetCategoriesResult.Success(categories)
        coEvery { repository.getQuestions() } returns GetQuestionsResult.Error.Network()

        val content = useCase()

        content.categories shouldBe GetCategoriesResult.Success(categories)
        content.questions shouldBe GetQuestionsResult.Error.Network()
    }

    @Test
    fun `both results come back when both succeed`() = runTest {
        val questions = listOf(Question(1, "Watering", "", "", "", 0))
        coEvery { repository.getCategories() } returns GetCategoriesResult.Success(emptyList())
        coEvery { repository.getQuestions() } returns GetQuestionsResult.Success(questions)

        val content = useCase()

        content.questions shouldBe GetQuestionsResult.Success(questions)
        content.categories shouldBe GetCategoriesResult.Success(emptyList())
    }

    private companion object {
        const val ENDPOINT_DELAY_MS = 500L
    }
}
