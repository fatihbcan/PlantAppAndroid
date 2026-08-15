package com.plantappmvi.android.core.presentation.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Where a screen may go. Each feature declares its own sub-interface listing
 * its destinations; the composition root implements it and is the only place
 * that knows a route string.
 */
interface BasicNavigator {
    fun back()
}

/**
 * A destination plus how to get there.
 *
 * [popUpToRoute] with [isPopUpToInclusive] is how a flow replaces part of the
 * stack rather than growing it. [isClearingBackStack] is the stronger form —
 * [route] becomes the only entry left — for a destination that has to be the
 * new root no matter which flow reached it. It takes precedence over
 * [popUpToRoute].
 */
data class NavigationDirections(
    val route: String,
    val popUpToRoute: String? = null,
    val isPopUpToInclusive: Boolean = false,
    val isSingleTop: Boolean = false,
    val isClearingBackStack: Boolean = false,
)

data class NavigationBackDirections(
    val route: String,
    val isInclusive: Boolean = false,
)

sealed interface NavigationCommand {
    data class NavigateTo(val directions: NavigationDirections) : NavigationCommand
    data class Back(val directions: NavigationBackDirections?) : NavigationCommand
    data object Finish : NavigationCommand
}

/**
 * The single navigation bus. The host Activity collects [commands] and drives
 * the real `NavController`; feature code never touches one.
 */
interface NavigationManager {
    val commands: SharedFlow<NavigationCommand>
    fun navigateTo(directions: NavigationDirections)
    fun navigateBack(directions: NavigationBackDirections? = null)
    fun finish()
}
