package com.plantappmvi.android.domain.onboarding.data

import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan

/**
 * Outcome of reading the onboarding-completed flag.
 *
 * [Unavailable] is deliberately its own case rather than an error the caller
 * must handle: the gate treats an unreadable flag as "not completed" and shows
 * onboarding, because repeating onboarding is a far milder failure than
 * locking someone out of the app.
 */
sealed interface OnboardingStatusResult {
    data object Completed : OnboardingStatusResult
    data object Pending : OnboardingStatusResult
    data class Unavailable(val cause: Throwable? = null) : OnboardingStatusResult
}

/** Outcome of marking onboarding finished. */
sealed interface CompleteOnboardingResult {
    data object Success : CompleteOnboardingResult
    data class Failure(val cause: Throwable? = null) : CompleteOnboardingResult
}

/** Outcome of loading the paywall's plan catalogue. */
sealed interface GetPlansResult {
    data class Success(val plans: List<SubscriptionPlan>) : GetPlansResult
    data class Failure(val cause: Throwable? = null) : GetPlansResult
}
