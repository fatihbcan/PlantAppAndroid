package com.plantappmvi.android.framework.app.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator
import com.plantappmvi.android.core.presentation.navigation.NavigationDirections
import com.plantappmvi.android.core.presentation.navigation.NavigationManager
import com.plantappmvi.android.presentation.home.navigation.HomeNavigator
import com.plantappmvi.android.presentation.onboarding.intro.navigation.IntroNavigator
import com.plantappmvi.android.presentation.onboarding.paywall.navigation.PaywallNavigator
import javax.inject.Inject

internal class IntroNavigatorImpl @Inject constructor(
    private val navigationManager: NavigationManager,
    basicNavigator: DefaultBasicNavigator,
) : IntroNavigator, BasicNavigator by basicNavigator {

    override fun paywall() =
        navigationManager.navigateTo(NavigationDirections(AppRoutes.PAYWALL))
}

internal class PaywallNavigatorImpl @Inject constructor(
    private val navigationManager: NavigationManager,
    basicNavigator: DefaultBasicNavigator,
) : PaywallNavigator, BasicNavigator by basicNavigator {

    /**
     * The case's one genuinely stateful navigation rule: a user who completes
     * onboarding must never re-enter it.
     *
     * Popping the intro route inclusively empties the whole flow behind home,
     * so the system back gesture leaves the app rather than walking back into
     * the paywall. The persisted flag then covers the next cold start; this
     * covers the current session.
     */
    override fun home() = navigationManager.navigateTo(
        NavigationDirections(
            route = AppRoutes.HOME,
            popUpToRoute = AppRoutes.INTRO,
            isPopUpToInclusive = true,
            isSingleTop = true,
        ),
    )
}

internal class HomeNavigatorImpl @Inject constructor(
    basicNavigator: DefaultBasicNavigator,
) : HomeNavigator, BasicNavigator by basicNavigator
