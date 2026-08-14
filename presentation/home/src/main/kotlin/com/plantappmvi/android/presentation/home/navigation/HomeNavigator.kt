package com.plantappmvi.android.presentation.home.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator

/**
 * Home declares no destinations of its own.
 *
 * The design's bottom bar has five, but only Home has a screen in this case,
 * so there is nothing yet to navigate to. The interface still exists because
 * that is where a destination will be declared when one arrives — and because
 * `back()` from [BasicNavigator] is what closes the app from the root screen.
 */
interface HomeNavigator : BasicNavigator
