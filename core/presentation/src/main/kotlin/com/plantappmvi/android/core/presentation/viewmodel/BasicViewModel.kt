package com.plantappmvi.android.core.presentation.viewmodel

import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantappmvi.android.core.presentation.mvi.ScreenEvent
import com.plantappmvi.android.core.presentation.mvi.ScreenState
import com.plantappmvi.android.core.presentation.mvi.StateStore
import com.plantappmvi.android.core.presentation.navigation.BasicNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/** Routes the system back gesture through the ViewModel rather than the UI. */
interface SystemBackHandler {
    fun onSystemBack()
}

/**
 * `by stateStore` is what makes `state` and `sendEvent` available with zero
 * boilerplate while keeping state ownership swappable in a test.
 */
abstract class BasicViewModel<S : ScreenState, E : ScreenEvent<S>>(
    stateStore: StateStore<S, E>,
) : ViewModel(),
    DefaultLifecycleObserver,
    SystemBackHandler,
    StateStore<S, E> by stateStore {

    protected abstract val navigator: BasicNavigator

    private val navigationOnceState = MutableStateFlow(true)

    override fun onSystemBack() = goBack()

    protected open fun goBack() = navigator.back()

    @CallSuper
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        navigationOnceState.value = true
    }

    /**
     * Runs [navigationAction] at most once until the next `onResume`.
     *
     * This exists for a real bug class: a user double-taps a button whose
     * handler awaits a use case, and two identical destinations get pushed.
     * Return `false` from the action if navigation did *not* happen, to re-arm.
     */
    protected fun launchNavigationOnce(navigationAction: suspend () -> Boolean): Job? =
        navigationOnceState.compareAndSet(expect = true, update = false)
            .takeIf { it }
            ?.let {
                viewModelScope.launch {
                    if (!navigationAction()) navigationOnceState.value = true
                }
            }
}
