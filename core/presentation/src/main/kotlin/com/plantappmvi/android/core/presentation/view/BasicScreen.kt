package com.plantappmvi.android.core.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.plantappmvi.android.core.presentation.mvi.ScreenEvent
import com.plantappmvi.android.core.presentation.mvi.ScreenState
import com.plantappmvi.android.core.presentation.viewmodel.BasicViewModel
import com.plantappmvi.android.core.presentation.viewmodel.SystemBackHandler

/**
 * Every screen wraps its content in this, so lifecycle forwarding and back
 * handling are never re-implemented per screen.
 */
@Composable
fun BasicScreen(
    viewModel: BasicViewModel<out ScreenState, out ScreenEvent<out ScreenState>>,
    content: @Composable () -> Unit,
) {
    LifecycleEffect(viewModel)
    SystemBackEffect(viewModel)
    content()
}

@Composable
fun LifecycleEffect(observer: LifecycleObserver) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(observer, lifecycle) {
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}

@Composable
fun SystemBackEffect(handler: SystemBackHandler) {
    BackHandler { handler.onSystemBack() }
}
