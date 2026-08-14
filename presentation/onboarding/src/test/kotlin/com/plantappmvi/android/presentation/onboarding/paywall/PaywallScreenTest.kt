package com.plantappmvi.android.presentation.onboarding.paywall

import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.domain.onboarding.entities.BillingPeriod
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan
import com.plantappmvi.android.presentation.onboarding.R
import com.plantappmvi.android.presentation.onboarding.paywall.model.PaywallError
import com.plantappmvi.android.presentation.onboarding.paywall.model.PaywallScreenEvent
import com.plantappmvi.android.presentation.onboarding.paywall.model.PaywallScreenState
import com.plantappmvi.android.presentation.onboarding.paywall.view.props.mapStateToProps
import io.kotest.matchers.shouldBe
import org.junit.Test

private val Monthly = SubscriptionPlan("monthly", BillingPeriod.MONTHLY, "$2.99")
private val Yearly = SubscriptionPlan("yearly", BillingPeriod.YEARLY, "$529.99", 3, 50)
private val Plans = listOf(Monthly, Yearly)

class PaywallScreenStateTest {

    private val loaded = PaywallScreenState.initial()
        .copy(isLoading = false, plans = Plans, selectedPlanId = "yearly")

    @Test
    fun `the selected plan is resolved from its id`() {
        loaded.selectedPlan shouldBe Yearly
    }

    @Test
    fun `an id matching nothing resolves to no plan rather than throwing`() {
        loaded.copy(selectedPlanId = "quarterly").selectedPlan shouldBe null
    }

    @Test
    fun `the call to action is dead until a plan is chosen`() {
        loaded.copy(selectedPlanId = null).canSubmit shouldBe false
        loaded.canSubmit shouldBe true
    }

    @Test
    fun `the call to action is dead while a submission is in flight`() {
        loaded.copy(isSubmitting = true).canSubmit shouldBe false
    }

    @Test
    fun `initial loading only covers the case with nothing to show`() {
        PaywallScreenState.initial().isInitialLoading shouldBe true
        loaded.copy(isLoading = true).isInitialLoading shouldBe false
    }

    @Test
    fun `the screen is only unrecoverable when it failed with no plans at all`() {
        val empty = PaywallScreenState.initial()
            .copy(isLoading = false, error = PaywallError.PLANS_UNAVAILABLE)

        empty.isUnrecoverable shouldBe true
        // A completion failure over a loaded catalogue is not a dead screen.
        loaded.copy(error = PaywallError.COMPLETION_FAILED).isUnrecoverable shouldBe false
    }
}

class PaywallScreenEventTest {

    @Test
    fun `PlansLoaded preselects the plan the design highlights`() {
        val new = PaywallScreenEvent.PlansLoaded(Plans, defaultPlanId = "yearly")
            .reduce(PaywallScreenState.initial())

        new.isLoading shouldBe false
        new.selectedPlanId shouldBe "yearly"
        new.plans shouldBe Plans
    }

    @Test
    fun `a selection the user already made outlives a refresh`() {
        val old = PaywallScreenState.initial().copy(selectedPlanId = "monthly")

        val new = PaywallScreenEvent.PlansLoaded(Plans, defaultPlanId = "yearly").reduce(old)

        new.selectedPlanId shouldBe "monthly"
    }

    @Test
    fun `PlansLoadFailed lowers the flag and records the category`() {
        val new = PaywallScreenEvent.PlansLoadFailed.reduce(PaywallScreenState.initial())

        new.isLoading shouldBe false
        new.error shouldBe PaywallError.PLANS_UNAVAILABLE
    }

    @Test
    fun `SubmissionStarted clears a previous error`() {
        val old = PaywallScreenState.initial().copy(error = PaywallError.PLANS_UNAVAILABLE)

        val new = PaywallScreenEvent.SubmissionStarted.reduce(old)

        new.isSubmitting shouldBe true
        new.error shouldBe null
    }

    @Test
    fun `CompletionFailed records the failure without blocking the exit`() {
        // Nothing in this reducer stops the user leaving: being unable to
        // persist the flag is a milder failure than being trapped in the flow.
        val new = PaywallScreenEvent.CompletionFailed
            .reduce(PaywallScreenState.initial().copy(isSubmitting = true))

        new.isSubmitting shouldBe false
        new.error shouldBe PaywallError.COMPLETION_FAILED
    }
}

class PaywallScreenPropsTest {

    private val loaded = PaywallScreenState.initial()
        .copy(isLoading = false, plans = Plans, selectedPlanId = "yearly")

    @Test
    fun `exactly one tile is marked selected`() {
        val props = mapStateToProps(loaded)

        props.plans.count { it.isSelected } shouldBe 1
        props.plans.single { it.isSelected }.id shouldBe "yearly"
    }

    @Test
    fun `only the discounted plan carries a badge`() {
        val props = mapStateToProps(loaded)

        props.plans.single { it.badge != null }.id shouldBe "yearly"
    }

    @Test
    fun `each billing period gets its own copy`() {
        val props = mapStateToProps(loaded)

        props.plans.single { it.id == "monthly" }.title shouldBe
            TextResource.fromId(R.string.paywall_plan_monthly_title)
        props.plans.single { it.id == "yearly" }.title shouldBe
            TextResource.fromId(R.string.paywall_plan_yearly_title)
    }

    @Test
    fun `the legal line quotes the selected plan's price`() {
        mapStateToProps(loaded).legalText shouldBe
            TextResource.fromId(R.string.paywall_legal, "$529.99")

        // Switching plan rewrites it, rather than leaving it describing the
        // other one.
        mapStateToProps(loaded.copy(selectedPlanId = "monthly")).legalText shouldBe
            TextResource.fromId(R.string.paywall_legal, "$2.99")
    }

    @Test
    fun `the legal line degrades to an empty price rather than a crash`() {
        mapStateToProps(loaded.copy(selectedPlanId = null)).legalText shouldBe
            TextResource.fromId(R.string.paywall_legal, "")
    }
}
