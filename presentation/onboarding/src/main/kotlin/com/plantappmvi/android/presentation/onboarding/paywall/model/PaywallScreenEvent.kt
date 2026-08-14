package com.plantappmvi.android.presentation.onboarding.paywall.model

import com.plantappmvi.android.core.presentation.mvi.ScreenEvent
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan

internal sealed interface PaywallScreenEvent : ScreenEvent<PaywallScreenState> {

    data object LoadStarted : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) =
            oldState.copy(isLoading = true, error = null)
    }

    data class PlansLoaded(
        val plans: List<SubscriptionPlan>,
        val defaultPlanId: String?,
    ) : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) = oldState.copy(
            isLoading = false,
            plans = plans,
            // A selection the user already made outlives a refresh.
            selectedPlanId = oldState.selectedPlanId ?: defaultPlanId,
            error = null,
        )
    }

    data object PlansLoadFailed : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) =
            oldState.copy(isLoading = false, error = PaywallError.PLANS_UNAVAILABLE)
    }

    data class PlanSelected(val planId: String) : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) =
            oldState.copy(selectedPlanId = planId)
    }

    data object SubmissionStarted : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) =
            oldState.copy(isSubmitting = true, error = null)
    }

    data object CompletionFailed : PaywallScreenEvent {
        override fun reduce(oldState: PaywallScreenState) =
            oldState.copy(isSubmitting = false, error = PaywallError.COMPLETION_FAILED)
    }
}
