package com.plantappmvi.android.presentation.onboarding.intro.model

import com.plantappmvi.android.core.presentation.mvi.DefaultStateStore
import javax.inject.Inject

internal class IntroScreenStateStore @Inject constructor() :
    DefaultStateStore<IntroScreenState, IntroScreenEvent>(
        initialState = IntroScreenState.initial(),
    )
