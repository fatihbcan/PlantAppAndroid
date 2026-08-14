package com.plantappmvi.android.domain.onboarding.entities

/** How often a [SubscriptionPlan] renews. */
enum class BillingPeriod { MONTHLY, YEARLY }

/**
 * A purchasable plan offered on the paywall.
 *
 * Prices arrive pre-formatted for the store locale — the domain never does
 * currency formatting, and the UI never does arithmetic on money.
 */
data class SubscriptionPlan(
    val id: String,
    val period: BillingPeriod,
    val formattedPrice: String,
    val trialDays: Int = 0,
    val discountPercent: Int = 0,
) {
    /** Whether to show the "save X%" badge. */
    val hasDiscount: Boolean get() = discountPercent > 0

    /** Whether the call to action should promise a free trial. */
    val hasTrial: Boolean get() = trialDays > 0
}
