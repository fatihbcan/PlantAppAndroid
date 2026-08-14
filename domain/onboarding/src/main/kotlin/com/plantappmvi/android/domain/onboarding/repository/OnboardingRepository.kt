package com.plantappmvi.android.domain.onboarding.repository

import com.plantappmvi.android.domain.onboarding.data.CompleteOnboardingResult
import com.plantappmvi.android.domain.onboarding.data.GetPlansResult
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult

/**
 * Persistence and content for the onboarding flow.
 *
 * Implemented in `data`. Nothing here knows about DataStore, and no
 * implementation may throw across this boundary — failures come back as a
 * case of the operation's result type.
 */
interface OnboardingRepository {
    /** Whether the user already finished onboarding on this device. */
    suspend fun readStatus(): OnboardingStatusResult

    /** Records that onboarding is finished. Idempotent. */
    suspend fun markCompleted(): CompleteOnboardingResult

    /** The plans shown on the paywall. */
    suspend fun getPlans(): GetPlansResult
}
