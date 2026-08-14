package com.plantappmvi.android.data.home.repository

import com.plantappmvi.android.data.home.datasource.HomeRemoteDataSource
import com.plantappmvi.android.data.home.mapper.toCategoryEntities
import com.plantappmvi.android.data.home.mapper.toQuestionEntities
import com.plantappmvi.android.domain.home.data.GetCategoriesResult
import com.plantappmvi.android.domain.home.data.GetQuestionsResult
import com.plantappmvi.android.domain.home.repository.HomeRepository
import com.plantappmvi.android.platform.network.AppException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin translation layer: call the source, map DTOs, turn exceptions into
 * result cases. No business rules live here, and nothing throws past it.
 */
@Singleton
internal class HomeRepositoryImpl @Inject constructor(
    private val remote: HomeRemoteDataSource,
) : HomeRepository {

    override suspend fun getCategories(): GetCategoriesResult = try {
        GetCategoriesResult.Success(remote.fetchCategories().toCategoryEntities())
    } catch (cause: AppException) {
        when (cause) {
            is AppException.Network -> GetCategoriesResult.Error.Network(cause)
            is AppException.Server -> GetCategoriesResult.Error.Server(cause.statusCode, cause)
            is AppException.Parse -> GetCategoriesResult.Error.Parse(cause)
            is AppException.Unknown -> GetCategoriesResult.Error.Unknown(cause)
        }
    }

    override suspend fun getQuestions(): GetQuestionsResult = try {
        GetQuestionsResult.Success(remote.fetchQuestions().toQuestionEntities())
    } catch (cause: AppException) {
        when (cause) {
            is AppException.Network -> GetQuestionsResult.Error.Network(cause)
            is AppException.Server -> GetQuestionsResult.Error.Server(cause.statusCode, cause)
            is AppException.Parse -> GetQuestionsResult.Error.Parse(cause)
            is AppException.Unknown -> GetQuestionsResult.Error.Unknown(cause)
        }
    }
}
