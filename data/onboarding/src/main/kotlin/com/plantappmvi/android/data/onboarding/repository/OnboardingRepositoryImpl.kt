package com.plantappmvi.android.data.onboarding.repository

import com.plantappmvi.android.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappmvi.android.data.onboarding.mapper.toEntity
import com.plantappmvi.android.domain.onboarding.data.CompleteOnboardingResult
import com.plantappmvi.android.domain.onboarding.data.GetPlansResult
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult
import com.plantappmvi.android.domain.onboarding.repository.OnboardingRepository
import com.plantappmvi.android.platform.datastore.StorageException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin translation layer: call the source, map DTOs, turn exceptions into
 * result cases. No business rules live here.
 */
@Singleton
internal class OnboardingRepositoryImpl @Inject constructor(
    private val local: OnboardingLocalDataSource,
) : OnboardingRepository {

    override suspend fun readStatus(): OnboardingStatusResult = try {
        if (local.readCompleted()) OnboardingStatusResult.Completed else OnboardingStatusResult.Pending
    } catch (cause: StorageException) {
        OnboardingStatusResult.Unavailable(cause)
    }

    override suspend fun markCompleted(): CompleteOnboardingResult = try {
        local.writeCompleted()
        CompleteOnboardingResult.Success
    } catch (cause: StorageException) {
        CompleteOnboardingResult.Failure(cause)
    }

    override suspend fun getPlans(): GetPlansResult = try {
        GetPlansResult.Success(local.readPlans().map { it.toEntity() })
    } catch (cause: StorageException) {
        GetPlansResult.Failure(cause)
    }
}
