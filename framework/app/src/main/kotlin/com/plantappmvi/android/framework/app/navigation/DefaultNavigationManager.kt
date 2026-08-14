package com.plantappmvi.android.framework.app.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator
import com.plantappmvi.android.core.presentation.navigation.NavigationBackDirections
import com.plantappmvi.android.core.presentation.navigation.NavigationCommand
import com.plantappmvi.android.core.presentation.navigation.NavigationDirections
import com.plantappmvi.android.core.presentation.navigation.NavigationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultNavigationManager @Inject constructor() : NavigationManager {

    // Buffered rather than replayed: a command issued while the Activity is
    // rebuilding must still arrive, but replaying one after a configuration
    // change would navigate twice.
    private val internalCommands = MutableSharedFlow<NavigationCommand>(
        extraBufferCapacity = COMMAND_BUFFER,
    )

    override val commands: SharedFlow<NavigationCommand> = internalCommands.asSharedFlow()

    override fun navigateTo(directions: NavigationDirections) {
        internalCommands.tryEmit(NavigationCommand.NavigateTo(directions))
    }

    override fun navigateBack(directions: NavigationBackDirections?) {
        internalCommands.tryEmit(NavigationCommand.Back(directions))
    }

    override fun finish() {
        internalCommands.tryEmit(NavigationCommand.Finish)
    }

    private companion object {
        const val COMMAND_BUFFER = 8
    }
}

/** `back()`, implemented once, delegated to by every NavigatorImpl. */
internal class DefaultBasicNavigator @Inject constructor(
    private val navigationManager: NavigationManager,
) : BasicNavigator {
    override fun back() = navigationManager.navigateBack()
}
