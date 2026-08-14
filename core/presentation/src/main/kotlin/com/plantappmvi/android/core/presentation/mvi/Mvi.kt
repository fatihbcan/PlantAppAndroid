package com.plantappmvi.android.core.presentation.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Marker for a screen's single flat immutable state snapshot. */
interface ScreenState

/**
 * An event knows how to apply itself. There is no central reducer by design:
 * adding a case means adding a class, never editing a growing `when`.
 *
 * [reduce] must be pure — `copy` only. No I/O, no logging, no clock reads.
 */
interface ScreenEvent<S : ScreenState> {
    fun reduce(oldState: S): S
}

interface StateStore<S : ScreenState, E : ScreenEvent<S>> {
    val state: StateFlow<S>
    fun sendEvent(event: E)
}

/**
 * [setState] is `protected open` so a subclass can intercept every transition.
 * That is the seam for logging or a debug time-travel store; nothing uses it
 * yet, and nothing should until there is a reason.
 */
open class DefaultStateStore<S : ScreenState, E : ScreenEvent<S>>(
    initialState: S,
) : StateStore<S, E> {

    private val internalState = MutableStateFlow(initialState)

    override val state: StateFlow<S> = internalState.asStateFlow()

    override fun sendEvent(event: E) {
        setState(event.reduce(internalState.value))
    }

    protected open fun setState(newState: S) {
        internalState.value = newState
    }
}
