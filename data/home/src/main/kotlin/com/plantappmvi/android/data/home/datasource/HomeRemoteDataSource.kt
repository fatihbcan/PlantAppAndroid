package com.plantappmvi.android.data.home.datasource

import com.plantappmvi.android.data.home.dto.CategoriesResponseDto
import com.plantappmvi.android.data.home.dto.CategoryDto
import com.plantappmvi.android.data.home.dto.QuestionDto
import com.plantappmvi.android.platform.network.AppException
import kotlinx.serialization.SerializationException
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The Retrofit contract for the case API.
 *
 * Both endpoints answer with `content-type: text/plain` despite returning
 * JSON. Retrofit chooses its converter by the declared return type rather
 * than by that header, so kotlinx-serialization parses them anyway — the
 * Flutter build of this app needs a whole interceptor to work around the same
 * server behaviour.
 */
internal interface HomeApi {
    @GET("getCategories")
    suspend fun getCategories(): CategoriesResponseDto

    @GET("getQuestions")
    suspend fun getQuestions(): List<QuestionDto>
}

internal interface HomeRemoteDataSource {
    suspend fun fetchCategories(): List<CategoryDto>

    suspend fun fetchQuestions(): List<QuestionDto>
}

/**
 * Transport failures arrive already translated by the network module's
 * interceptor. This class adds only the parse step, so a payload of the wrong
 * shape becomes an [AppException.Parse] rather than a raw serialization error
 * leaking upward.
 */
@Singleton
internal class HomeRemoteDataSourceImpl @Inject constructor(
    retrofit: Retrofit,
) : HomeRemoteDataSource {

    private val api: HomeApi = retrofit.create(HomeApi::class.java)

    override suspend fun fetchCategories(): List<CategoryDto> = try {
        api.getCategories().data
    } catch (cause: SerializationException) {
        throw AppException.Parse(cause)
    }

    override suspend fun fetchQuestions(): List<QuestionDto> = try {
        api.getQuestions()
    } catch (cause: SerializationException) {
        throw AppException.Parse(cause)
    }
}
