package com.plantappmvi.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.plantappmvi.android.core.presentation.navigation.NavigationManager
import com.plantappmvi.android.framework.app.AppRoot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The app's single Activity, and deliberately almost empty: everything it
 * shows comes from `framework:app`, which is the only module that knows the
 * whole graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // The start destination depends on a flag read from disk. Holding the
        // splash until it resolves is what stops onboarding flashing up for a
        // frame in front of a user who already finished it.
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            val navController = rememberNavController()

            AppRoot(
                navController = navController,
                navigationManager = navigationManager,
                onFinish = ::finish,
                onReady = { isReady = true },
            )
        }
    }
}
