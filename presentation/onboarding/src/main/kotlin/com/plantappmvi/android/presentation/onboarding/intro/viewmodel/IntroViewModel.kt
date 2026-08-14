package com.plantappmvi.android.presentation.onboarding.intro.viewmodel

import com.plantappmvi.android.core.presentation.viewmodel.BasicViewModel
import com.plantappmvi.android.presentation.onboarding.intro.model.IntroScreenEvent
import com.plantappmvi.android.presentation.onboarding.intro.model.IntroScreenState
import com.plantappmvi.android.presentation.onboarding.intro.model.IntroScreenStateStore
import com.plantappmvi.android.presentation.onboarding.intro.navigation.IntroNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Drives the three intro pages and decides when the flow moves on.
 *
 * It owns no I/O: the completion flag is written by the paywall, because the
 * case ends onboarding at the paywall's close button rather than here.
 *
 * Note what this does *not* carry. The Flutter build of this screen needs an
 * `isFinished` flag in state plus a `finishConsumed` event to clear it,
 * because a Bloc has no way to navigate. Here the ViewModel holds an injected
 * [IntroNavigator], so advancing to the paywall is a call rather than a flag
 * the view has to notice and acknowledge.
 */
@HiltViewModel
internal class IntroViewModel @Inject constructor(
    stateStore: IntroScreenStateStore,
    override val navigator: IntroNavigator,
) : BasicViewModel<IntroScreenState, IntroScreenEvent>(stateStore) {

    fun onNextClick() {
        val current = state.value
        if (current.isLastPage) {
            navigator.paywall()
        } else {
            sendEvent(IntroScreenEvent.PageChanged(current.pageIndex + 1))
        }
    }

    fun onPageSwiped(page: Int) = sendEvent(IntroScreenEvent.PageChanged(page))

    /**
     * Back walks the pages before it leaves the flow, which is what a user
     * swiping forward through onboarding expects the gesture to undo.
     */
    override fun goBack() {
        val current = state.value
        if (current.pageIndex > 0) {
            sendEvent(IntroScreenEvent.PageChanged(current.pageIndex - 1))
        } else {
            super.goBack()
        }
    }
}
