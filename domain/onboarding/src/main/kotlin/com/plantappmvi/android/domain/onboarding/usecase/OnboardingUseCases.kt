package com.plantappmvi.android.domain.onboarding.usecase

import com.plantappmvi.android.domain.onboarding.data.CompleteOnboardingResult
import com.plantappmvi.android.domain.onboarding.data.GetPlansResult
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult
import com.plantappmvi.android.domain.onboarding.repository.OnboardingRepository
import javax.inject.Inject

/**
 * Reads whether onboarding has already been completed on this device.
 *
 * The composition root calls this once at startup to pick the start
 * destination, which is this architecture's equivalent of a route guard.
 */
class GetOnboardingStatusUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): OnboardingStatusResult = repository.readStatus()
}

/**
 * Marks the onboarding flow finished, so the gate stops routing here.
 *
 * Called when the paywall's close button is tapped — the case defines that
 * tap, not a purchase, as the end of onboarding.
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): CompleteOnboardingResult = repository.markCompleted()
}

/** Loads the plans offered on the paywall. */
class GetSubscriptionPlansUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): GetPlansResult = repository.getPlans()
}
