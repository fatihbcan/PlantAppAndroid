package com.plantappmvi.android.presentation.home.model

import com.plantappmvi.android.core.presentation.mvi.DefaultStateStore
import javax.inject.Inject

internal class HomeScreenStateStore @Inject constructor() :
    DefaultStateStore<HomeScreenState, HomeScreenEvent>(
        initialState = HomeScreenState.initial(),
    )
