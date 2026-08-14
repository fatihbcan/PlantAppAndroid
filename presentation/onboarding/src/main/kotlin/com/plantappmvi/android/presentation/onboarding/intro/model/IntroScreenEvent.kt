package com.plantappmvi.android.presentation.onboarding.intro.model

import com.plantappmvi.android.core.presentation.mvi.ScreenEvent

internal sealed interface IntroScreenEvent : ScreenEvent<IntroScreenState> {

    /** The visible page changed, by swipe or by the call to action. */
    data class PageChanged(val page: Int) : IntroScreenEvent {
        override fun reduce(oldState: IntroScreenState): IntroScreenState {
            val clamped = page.coerceIn(0, oldState.pageCount - 1)
            return if (clamped == oldState.pageIndex) {
                oldState
            } else {
                oldState.copy(pageIndex = clamped)
            }
        }
    }
}
