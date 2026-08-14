package com.plantappmvi.android.presentation.onboarding.paywall.model

import com.plantappmvi.android.core.presentation.mvi.ScreenState
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan

/**
 * Why the paywall could not do what was asked, as a UI-facing category.
 *
 * The ViewModel maps domain result cases onto this, so nothing in the view
 * layer ever switches on a domain type.
 */
internal enum class PaywallError { PLANS_UNAVAILABLE, COMPLETION_FAILED }

internal data class PaywallScreenState(
    val isLoading: Boolean,
    val plans: List<SubscriptionPlan>,
    val selectedPlanId: String?,
    val isSubmitting: Boolean,
    val error: PaywallError?,
) : ScreenState {

    /** The plan the call to action would purchase, or null while plans load. */
    val selectedPlan: SubscriptionPlan? get() = plans.firstOrNull { it.id == selectedPlanId }

    val hasPlans: Boolean get() = plans.isNotEmpty()

    /** True only while there is nothing at all to render. */
    val isInitialLoading: Boolean get() = isLoading && !hasPlans

    /** The whole screen is unusable — as opposed to a failure over content. */
    val isUnrecoverable: Boolean
        get() = !hasPlans && error == PaywallError.PLANS_UNAVAILABLE

    /** The call to action is live once a plan is chosen and nothing is in flight. */
    val canSubmit: Boolean get() = selectedPlan != null && !isSubmitting

    companion object {
        fun initial() = PaywallScreenState(
            isLoading = true,
            plans = emptyList(),
            selectedPlanId = null,
            isSubmitting = false,
            error = null,
        )
    }
}
