package com.plantappmvi.android.domain.home.usecase

import com.plantappmvi.android.domain.home.data.GetCategoriesResult
import com.plantappmvi.android.domain.home.data.GetQuestionsResult
import com.plantappmvi.android.domain.home.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): GetCategoriesResult = repository.getCategories()
}

class GetQuestionsUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): GetQuestionsResult = repository.getQuestions()
}

/** Both halves of the home screen, fetched at once. */
data class HomeContent(
    val categories: GetCategoriesResult,
    val questions: GetQuestionsResult,
)

/**
 * Loads both of home's collections in parallel.
 *
 * Parallelism belongs in a use case, not in the ViewModel: this is the piece
 * of orchestration that would otherwise be duplicated by first load and
 * pull-to-refresh. The two results stay separate all the way out, because the
 * endpoints fail independently and a dead grid should not blank a working
 * carousel.
 */
class GetHomeContentUseCase @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val getQuestions: GetQuestionsUseCase,
) {
    suspend operator fun invoke(): HomeContent = coroutineScope {
        val categories = async { getCategories() }
        val questions = async { getQuestions() }
        HomeContent(categories = categories.await(), questions = questions.await())
    }
}
