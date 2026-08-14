package com.plantappmvi.android.data.onboarding.datasource

import com.plantappmvi.android.data.onboarding.dto.SubscriptionPlanDto
import com.plantappmvi.android.platform.datastore.KeyValueStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device state and static content for the onboarding flow.
 *
 * The plan catalogue is local by design: the case exposes no billing API, so
 * the paywall is fed from here. Swapping in a real store SDK later means
 * replacing this class, not the repository and not the ViewModel.
 */
internal interface OnboardingLocalDataSource {
    suspend fun readCompleted(): Boolean

    suspend fun writeCompleted()

    suspend fun readPlans(): List<SubscriptionPlanDto>
}

@Singleton
internal class OnboardingLocalDataSourceImpl @Inject constructor(
    private val store: KeyValueStore,
) : OnboardingLocalDataSource {

    override suspend fun readCompleted(): Boolean = store.readBoolean(COMPLETED_KEY) ?: false

    override suspend fun writeCompleted() = store.writeBoolean(COMPLETED_KEY, value = true)

    override suspend fun readPlans(): List<SubscriptionPlanDto> = CATALOGUE

    companion object {
        const val COMPLETED_KEY = "onboarding.completed"

        private val CATALOGUE = listOf(
            SubscriptionPlanDto(
                id = "monthly",
                period = "monthly",
                formattedPrice = "$2.99",
            ),
            SubscriptionPlanDto(
                id = "yearly",
                period = "yearly",
                formattedPrice = "$529.99",
                trialDays = 3,
                discountPercent = 50,
            ),
        )
    }
}
