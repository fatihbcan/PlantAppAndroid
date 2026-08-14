package com.plantappmvi.android.framework.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plantappmvi.android.presentation.home.view.HomeRoute
import com.plantappmvi.android.presentation.onboarding.intro.view.IntroRoute
import com.plantappmvi.android.presentation.onboarding.paywall.view.PaywallRoute

/**
 * The whole route table.
 *
 * Each destination is a feature's public `Route` composable and nothing else —
 * no arguments are threaded through here, because no screen in this app takes
 * any.
 */
@Composable
internal fun AppNavHost(
    navController: NavHostController,
    startRoute: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier,
    ) {
        composable(AppRoutes.INTRO) { IntroRoute() }
        composable(AppRoutes.PAYWALL) { PaywallRoute() }
        composable(AppRoutes.HOME) { HomeRoute() }
    }
}
