package com.plantappmvi.android.data.onboarding.repository

import com.plantappmvi.android.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappmvi.android.data.onboarding.dto.SubscriptionPlanDto
import com.plantappmvi.android.domain.onboarding.data.CompleteOnboardingResult
import com.plantappmvi.android.domain.onboarding.data.GetPlansResult
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult
import com.plantappmvi.android.domain.onboarding.entities.BillingPeriod
import com.plantappmvi.android.platform.datastore.StorageException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** One test per branch of every result the repository can return. */
class OnboardingRepositoryImplTest {

    private val local: OnboardingLocalDataSource = mockk()
    private val repository = OnboardingRepositoryImpl(local)

    @Test
    fun `readStatus reports completed when the flag is set`() = runTest {
        coEvery { local.readCompleted() } returns true

        repository.readStatus() shouldBe OnboardingStatusResult.Completed
    }

    @Test
    fun `readStatus reports pending when the flag is unset`() = runTest {
        coEvery { local.readCompleted() } returns false

        repository.readStatus() shouldBe OnboardingStatusResult.Pending
    }

    @Test
    fun `readStatus reports unavailable rather than throwing when storage fails`() = runTest {
        coEvery { local.readCompleted() } throws StorageException("unreadable")

        repository.readStatus().shouldBeInstanceOf<OnboardingStatusResult.Unavailable>()
    }

    @Test
    fun `markCompleted writes the flag and reports success`() = runTest {
        coJustRun { local.writeCompleted() }

        repository.markCompleted() shouldBe CompleteOnboardingResult.Success
        coVerify(exactly = 1) { local.writeCompleted() }
    }

    @Test
    fun `markCompleted reports failure rather than throwing when the write fails`() = runTest {
        coEvery { local.writeCompleted() } throws StorageException("read only")

        repository.markCompleted().shouldBeInstanceOf<CompleteOnboardingResult.Failure>()
    }

    @Test
    fun `getPlans maps the catalogue to entities`() = runTest {
        coEvery { local.readPlans() } returns listOf(
            SubscriptionPlanDto("yearly", "yearly", "$529.99", trialDays = 3, discountPercent = 50),
        )

        val result = repository.getPlans()

        result.shouldBeInstanceOf<GetPlansResult.Success>()
        result.plans.single().period shouldBe BillingPeriod.YEARLY
        result.plans.single().hasDiscount shouldBe true
    }

    @Test
    fun `getPlans reports failure rather than throwing when the source fails`() = runTest {
        coEvery { local.readPlans() } throws StorageException("gone")

        repository.getPlans().shouldBeInstanceOf<GetPlansResult.Failure>()
    }
}
