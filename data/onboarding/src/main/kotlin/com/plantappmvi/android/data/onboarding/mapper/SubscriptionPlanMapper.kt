package com.plantappmvi.android.data.onboarding.mapper

import com.plantappmvi.android.data.onboarding.dto.SubscriptionPlanDto
import com.plantappmvi.android.domain.onboarding.entities.BillingPeriod
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan

/** DTO → entity, applied at the repository boundary. */
internal fun SubscriptionPlanDto.toEntity(): SubscriptionPlan = SubscriptionPlan(
    id = id,
    period = period.toBillingPeriod(),
    formattedPrice = formattedPrice,
    trialDays = trialDays,
    discountPercent = discountPercent,
)

/**
 * An unrecognised period falls back to monthly rather than throwing: a new
 * plan type added upstream should not blank the paywall.
 */
private fun String.toBillingPeriod(): BillingPeriod = when (lowercase()) {
    "yearly", "annual" -> BillingPeriod.YEARLY
    else -> BillingPeriod.MONTHLY
}
