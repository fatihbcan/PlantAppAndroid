package com.plantappmvi.android.domain.home.entities

/** A plant category tile on the home grid. */
data class Category(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rank: Int = 0,
) {
    val hasImage: Boolean get() = imageUrl.isNotEmpty()
}
