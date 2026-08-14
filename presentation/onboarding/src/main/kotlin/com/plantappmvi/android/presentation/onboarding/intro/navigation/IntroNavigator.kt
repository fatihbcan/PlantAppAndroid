package com.plantappmvi.android.presentation.onboarding.intro.navigation

import com.plantappmvi.android.core.presentation.navigation.BasicNavigator

/** Where the intro pages can go. Implemented in the composition root. */
interface IntroNavigator : BasicNavigator {
    fun paywall()
}
