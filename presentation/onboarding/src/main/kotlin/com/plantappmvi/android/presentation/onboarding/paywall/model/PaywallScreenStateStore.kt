package com.plantappmvi.android.presentation.onboarding.paywall.model

import com.plantappmvi.android.core.presentation.mvi.DefaultStateStore
import javax.inject.Inject

internal class PaywallScreenStateStore @Inject constructor() :
    DefaultStateStore<PaywallScreenState, PaywallScreenEvent>(
        initialState = PaywallScreenState.initial(),
    )
