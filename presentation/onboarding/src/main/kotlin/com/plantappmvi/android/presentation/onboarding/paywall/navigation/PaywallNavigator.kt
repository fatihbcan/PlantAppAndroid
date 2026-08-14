package com.plantappmvi.android.presentation.onboarding.paywall.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator

/**
 * Where the paywall can go.
 *
 * `home()` is the one navigation in this app that must clear the back stack:
 * the case requires that a user who finishes onboarding never re-enters it,
 * and the composition root implements that by popping the whole flow rather
 * than pushing home on top of it.
 */
interface PaywallNavigator : BasicNavigator {
    fun home()
}
