package com.plantappmvi.android.domain.onboarding.usecase

import com.plantappmvi.android.domain.onboarding.data.CompleteOnboardingResult
import com.plantappmvi.android.domain.onboarding.data.GetPlansResult
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult
import com.plantappmvi.android.domain.onboarding.entities.BillingPeriod
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan
import com.plantappmvi.android.domain.onboarding.repository.OnboardingRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * One test per branch of each result type. Adding a failure mode to a result
 * without adding a test here leaves a reachable path unexercised.
 */
class OnboardingUseCasesTest {

    private val repository: OnboardingRepository = mockk()

    @Test
    fun `GetOnboardingStatus returns completed when the flag is set`() = runTest {
        coEvery { repository.readStatus() } returns OnboardingStatusResult.Completed

        GetOnboardingStatusUseCase(repository)() shouldBe OnboardingStatusResult.Completed
    }

    @Test
    fun `GetOnboardingStatus returns pending when the flag is unset`() = runTest {
        coEvery { repository.readStatus() } returns OnboardingStatusResult.Pending

        GetOnboardingStatusUseCase(repository)() shouldBe OnboardingStatusResult.Pending
    }

    @Test
    fun `GetOnboardingStatus surfaces an unreadable flag rather than guessing`() = runTest {
        val cause = IllegalStateException("disk")
        coEvery { repository.readStatus() } returns OnboardingStatusResult.Unavailable(cause)

        val result = GetOnboardingStatusUseCase(repository)()

        result shouldBe OnboardingStatusResult.Unavailable(cause)
    }

    @Test
    fun `CompleteOnboarding reports success`() = runTest {
        coEvery { repository.markCompleted() } returns CompleteOnboardingResult.Success

        CompleteOnboardingUseCase(repository)() shouldBe CompleteOnboardingResult.Success
    }

    @Test
    fun `CompleteOnboarding reports a write failure`() = runTest {
        val cause = IllegalStateException("read only")
        coEvery { repository.markCompleted() } returns CompleteOnboardingResult.Failure(cause)

        CompleteOnboardingUseCase(repository)() shouldBe CompleteOnboardingResult.Failure(cause)
    }

    @Test
    fun `GetSubscriptionPlans returns the catalogue`() = runTest {
        val plans = listOf(SubscriptionPlan("monthly", BillingPeriod.MONTHLY, "$2.99"))
        coEvery { repository.getPlans() } returns GetPlansResult.Success(plans)

        GetSubscriptionPlansUseCase(repository)() shouldBe GetPlansResult.Success(plans)
    }

    @Test
    fun `GetSubscriptionPlans reports a failure`() = runTest {
        coEvery { repository.getPlans() } returns GetPlansResult.Failure()

        GetSubscriptionPlansUseCase(repository)() shouldBe GetPlansResult.Failure()
    }
}
