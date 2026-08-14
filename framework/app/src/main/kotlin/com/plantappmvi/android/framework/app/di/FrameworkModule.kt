package com.plantappmvi.android.framework.app.di

import android.util.Log
import com.plantappmvi.android.core.presentation.navigation.NavigationManager
import com.plantappmvi.android.core.util.coroutines.DefaultDispatcher
import com.plantappmvi.android.core.util.coroutines.IoDispatcher
import com.plantappmvi.android.core.util.coroutines.MainDispatcher
import com.plantappmvi.android.core.util.logging.Logger
import com.plantappmvi.android.framework.app.navigation.DefaultNavigationManager
import com.plantappmvi.android.framework.app.navigation.HomeNavigatorImpl
import com.plantappmvi.android.framework.app.navigation.IntroNavigatorImpl
import com.plantappmvi.android.framework.app.navigation.PaywallNavigatorImpl
import com.plantappmvi.android.presentation.home.navigation.HomeNavigator
import com.plantappmvi.android.presentation.onboarding.intro.navigation.IntroNavigator
import com.plantappmvi.android.presentation.onboarding.paywall.navigation.PaywallNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Every `Navigator` implementation in the app, bound in one place.
 *
 * This is the binding set the blueprint insists must live in the composition
 * root, and the reason is visible here: `IntroNavigatorImpl` knows the paywall
 * route and `PaywallNavigatorImpl` knows the home route, so this module is the
 * only thing in the build that knows both features exist.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationBindingModule {

    @Binds
    @Singleton
    fun bindNavigationManager(impl: DefaultNavigationManager): NavigationManager

    @Binds
    fun bindIntroNavigator(impl: IntroNavigatorImpl): IntroNavigator

    @Binds
    fun bindPaywallNavigator(impl: PaywallNavigatorImpl): PaywallNavigator

    @Binds
    fun bindHomeNavigator(impl: HomeNavigatorImpl): HomeNavigator

    @Binds
    @Singleton
    fun bindLogger(impl: AndroidLogger): Logger
}

@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

internal class AndroidLogger @Inject constructor() : Logger {
    override fun debug(message: String) {
        Log.d(TAG, message)
    }

    override fun warn(message: String, cause: Throwable?) {
        Log.w(TAG, message, cause)
    }

    override fun error(message: String, cause: Throwable?) {
        Log.e(TAG, message, cause)
    }

    private companion object {
        const val TAG = "PlantAppMVI"
    }
}
