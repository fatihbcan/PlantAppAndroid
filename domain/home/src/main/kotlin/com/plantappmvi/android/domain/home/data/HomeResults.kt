package com.plantappmvi.android.domain.home.data

import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.domain.home.entities.Question

/**
 * Outcome of loading the category grid.
 *
 * One result type per operation, consumed with an exhaustive `when` and no
 * `else` branch: adding a failure mode later becomes a compile error at every
 * call site rather than slipping through silently.
 */
sealed interface GetCategoriesResult {
    data class Success(val categories: List<Category>) : GetCategoriesResult

    sealed interface Error : GetCategoriesResult {
        val cause: Throwable?

        data class Network(override val cause: Throwable? = null) : Error
        data class Server(val statusCode: Int, override val cause: Throwable? = null) : Error
        data class Parse(override val cause: Throwable? = null) : Error
        data class Unknown(override val cause: Throwable? = null) : Error
    }
}

/** Outcome of loading the "get started" articles. */
sealed interface GetQuestionsResult {
    data class Success(val questions: List<Question>) : GetQuestionsResult

    sealed interface Error : GetQuestionsResult {
        val cause: Throwable?

        data class Network(override val cause: Throwable? = null) : Error
        data class Server(val statusCode: Int, override val cause: Throwable? = null) : Error
        data class Parse(override val cause: Throwable? = null) : Error
        data class Unknown(override val cause: Throwable? = null) : Error
    }
}
