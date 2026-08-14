package com.plantappmvi.android.framework.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.navigation.NavigationCommand
import com.plantappmvi.android.core.presentation.navigation.NavigationManager
import com.plantappmvi.android.framework.app.navigation.AppNavHost

/**
 * The app's whole UI, so the `app` module stays a manifest and an Activity.
 *
 * This is also the only place a `NavController` is touched: the navigation bus
 * is collected here and turned into real calls, which is what keeps every
 * ViewModel free of Android navigation types.
 */
@Composable
fun AppRoot(
    navController: NavHostController,
    navigationManager: NavigationManager,
    onFinish: () -> Unit,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Acquired here rather than taken as a parameter: this ViewModel is an
    // implementation detail of the composition root, and hoisting it into the
    // signature would force it public for no caller's benefit.
    val viewModel: AppViewModel = hiltViewModel()
    val startRoute by viewModel.startRoute.collectAsStateWithLifecycle()

    LaunchedEffect(startRoute) {
        if (startRoute != null) onReady()
    }

    LaunchedEffect(navController) {
        navigationManager.commands.collect { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> navController.navigate(
                    command.directions.route,
                ) {
                    launchSingleTop = command.directions.isSingleTop
                    command.directions.popUpToRoute?.let { route ->
                        popUpTo(route) { inclusive = command.directions.isPopUpToInclusive }
                    }
                }

                is NavigationCommand.Back -> {
                    val directions = command.directions
                    val popped = if (directions == null) {
                        navController.popBackStack()
                    } else {
                        navController.popBackStack(directions.route, directions.isInclusive)
                    }
                    // Back from the root screen leaves the app rather than
                    // sitting on an empty stack.
                    if (!popped) onFinish()
                }

                NavigationCommand.Finish -> onFinish()
            }
        }
    }

    AppTheme {
        startRoute?.let { route ->
            AppNavHost(
                navController = navController,
                startRoute = route,
                modifier = modifier,
            )
        }
    }
}
