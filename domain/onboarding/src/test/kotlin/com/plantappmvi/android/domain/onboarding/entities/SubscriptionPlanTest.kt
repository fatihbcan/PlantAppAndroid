package com.plantappmvi.android.domain.onboarding.entities

import io.kotest.matchers.shouldBe
import org.junit.Test

class SubscriptionPlanTest {

    @Test
    fun `a plan with no discount does not claim one`() {
        val plan = SubscriptionPlan("monthly", BillingPeriod.MONTHLY, "$2.99")

        plan.hasDiscount shouldBe false
        plan.hasTrial shouldBe false
    }

    @Test
    fun `a discounted trial plan reports both`() {
        val plan = SubscriptionPlan(
            id = "yearly",
            period = BillingPeriod.YEARLY,
            formattedPrice = "$529.99",
            trialDays = 3,
            discountPercent = 50,
        )

        plan.hasDiscount shouldBe true
        plan.hasTrial shouldBe true
    }
}
