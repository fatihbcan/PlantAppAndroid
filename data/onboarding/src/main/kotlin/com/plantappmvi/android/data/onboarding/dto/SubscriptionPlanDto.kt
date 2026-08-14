package com.plantappmvi.android.data.onboarding.dto

import kotlinx.serialization.Serializable

/** Wire/storage shape of a plan. Never leaves the data layer. */
@Serializable
internal data class SubscriptionPlanDto(
    val id: String,
    val period: String,
    val formattedPrice: String,
    val trialDays: Int = 0,
    val discountPercent: Int = 0,
)
