package com.plantappmvi.android.presentation.onboarding.intro.model

import com.plantappmvi.android.core.presentation.mvi.ScreenState

internal const val INTRO_PAGE_COUNT = 3

internal data class IntroScreenState(
    val pageIndex: Int,
    val pageCount: Int,
) : ScreenState {

    val isLastPage: Boolean get() = pageIndex == pageCount - 1

    /** Page dots are only drawn from the second page onward in the design. */
    val showsPageIndicator: Boolean get() = pageIndex != 0

    /**
     * Which dot is filled. The design's three dots cover the two onboarding
     * pages and the paywall that follows them — the welcome page sits before
     * the run and shows no dots at all, so the first dot belongs to page two.
     */
    val indicatorIndex: Int get() = pageIndex - 1

    /** 1-based position, for the screen-reader announcement. */
    val humanPageNumber: Int get() = pageIndex + 1

    companion object {
        fun initial() = IntroScreenState(pageIndex = 0, pageCount = INTRO_PAGE_COUNT)
    }
}
