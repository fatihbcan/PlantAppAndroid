package com.plantappmvi.android.domain.home.entities

/** A "get started" article card on home. */
data class Question(
    val id: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val articleUrl: String,
    val order: Int = 0,
) {
    val hasImage: Boolean get() = imageUrl.isNotEmpty()
}
