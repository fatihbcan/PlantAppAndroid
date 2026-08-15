package com.plantappmvi.android.presentation.home.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator

/**
 * Where home can go.
 *
 * The design's bottom bar has five destinations, but only Home has a screen in
 * this case, so none of those are declared here. The one live destination is
 * the paywall, which the "FREE Premium Available" strip opens — home names it
 * without knowing it belongs to another feature, and the composition root
 * resolves it to a route.
 */
interface HomeNavigator : BasicNavigator {
    fun paywall()
}
