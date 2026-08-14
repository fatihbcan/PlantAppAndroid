package com.plantappmvi.android.data.home.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `getCategories` wraps its payload in a Strapi-style envelope. */
@Serializable
internal data class CategoriesResponseDto(
    val data: List<CategoryDto> = emptyList(),
)

/** Wire shape of one category. Never leaves the data layer. */
@Serializable
internal data class CategoryDto(
    val id: Int,
    val title: String? = null,
    val rank: Int? = null,
    /** Nullable in the payload — some categories ship without artwork. */
    val image: CategoryImageDto? = null,
)

@Serializable
internal data class CategoryImageDto(
    val url: String? = null,
)

/**
 * Wire shape of one "get started" article.
 *
 * `getQuestions` returns a bare JSON array, so there is no envelope here.
 */
@Serializable
internal data class QuestionDto(
    val id: Int,
    val title: String? = null,
    val subtitle: String? = null,
    @SerialName("image_uri") val imageUri: String? = null,
    val uri: String? = null,
    val order: Int? = null,
)
