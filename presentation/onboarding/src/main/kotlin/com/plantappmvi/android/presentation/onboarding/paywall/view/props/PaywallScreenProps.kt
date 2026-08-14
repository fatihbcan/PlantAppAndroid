package com.plantappmvi.android.presentation.onboarding.paywall.view.props

import androidx.compose.runtime.Immutable
import com.plantappmvi.android.core.designsystem.icon.AppIcons
import com.plantappmvi.android.core.presentation.resource.IconResource
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.domain.onboarding.entities.BillingPeriod
import com.plantappmvi.android.domain.onboarding.entities.SubscriptionPlan
import com.plantappmvi.android.presentation.onboarding.R
import com.plantappmvi.android.presentation.onboarding.paywall.model.PaywallScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class PaywallFeatureProps(
    val icon: IconResource,
    val title: TextResource,
    val body: TextResource,
)

@Immutable
internal data class PaywallPlanProps(
    val id: String,
    val title: TextResource,
    val subtitle: TextResource,
    val badge: TextResource?,
    val isSelected: Boolean,
)

@Immutable
internal data class PaywallScreenProps(
    val showInitialLoading: Boolean,
    val showPlansError: Boolean,
    val errorMessage: TextResource,
    val heroTitle: TextResource,
    val heroTitleEmphasis: TextResource,
    val heroSubtitle: TextResource,
    val features: ImmutableList<PaywallFeatureProps>,
    val plans: ImmutableList<PaywallPlanProps>,
    val ctaText: TextResource,
    val isCtaEnabled: Boolean,
    val isSubmitting: Boolean,
    val legalText: TextResource,
    val footerLinks: ImmutableList<TextResource>,
    val closeLabel: TextResource,
    val onRetryClick: () -> Unit = {},
    val onPlanClick: (String) -> Unit = {},
    val onSubscribeClick: () -> Unit = {},
    val onCloseClick: () -> Unit = {},
) {
    companion object {
        fun preview() = mapStateToProps(
            PaywallScreenState.initial().copy(
                isLoading = false,
                plans = listOf(
                    SubscriptionPlan(
                        id = "monthly",
                        period = BillingPeriod.MONTHLY,
                        formattedPrice = "$2.99",
                    ),
                    SubscriptionPlan(
                        id = "yearly",
                        period = BillingPeriod.YEARLY,
                        formattedPrice = "$529.99",
                        trialDays = 3,
                        discountPercent = 50,
                    ),
                ),
                selectedPlanId = "yearly",
            ),
        )
    }
}

/**
 * All of the paywall's presentation logic in one pure function: which tile is
 * selected, whether the call to action is live, which price the legal line
 * quotes, and the domain→UI mapping of a billing period onto its copy.
 */
internal fun mapStateToProps(
    state: PaywallScreenState,
    onRetryClick: () -> Unit = {},
    onPlanClick: (String) -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
): PaywallScreenProps = PaywallScreenProps(
    showInitialLoading = state.isInitialLoading,
    showPlansError = state.isUnrecoverable,
    errorMessage = TextResource.fromId(R.string.paywall_plans_unavailable),
    heroTitle = TextResource.fromId(R.string.paywall_title),
    heroTitleEmphasis = TextResource.fromId(R.string.app_title),
    heroSubtitle = TextResource.fromId(R.string.paywall_subtitle),
    features = Features,
    plans = state.plans
        .map { plan -> plan.toProps(isSelected = plan.id == state.selectedPlanId) }
        .toImmutableList(),
    ctaText = TextResource.fromId(R.string.paywall_cta),
    isCtaEnabled = state.canSubmit,
    isSubmitting = state.isSubmitting,
    // The quoted price follows the selection, so switching plan rewrites the
    // legal line rather than leaving it describing the other one.
    legalText = TextResource.fromId(
        R.string.paywall_legal,
        state.selectedPlan?.formattedPrice.orEmpty(),
    ),
    footerLinks = persistentListOf(
        TextResource.fromId(R.string.paywall_terms),
        TextResource.fromId(R.string.paywall_privacy),
        TextResource.fromId(R.string.paywall_restore),
    ),
    closeLabel = TextResource.fromId(R.string.paywall_close_semantics),
    onRetryClick = onRetryClick,
    onPlanClick = onPlanClick,
    onSubscribeClick = onSubscribeClick,
    onCloseClick = onCloseClick,
)

private fun SubscriptionPlan.toProps(isSelected: Boolean) = PaywallPlanProps(
    id = id,
    title = when (period) {
        BillingPeriod.MONTHLY -> TextResource.fromId(R.string.paywall_plan_monthly_title)
        BillingPeriod.YEARLY -> TextResource.fromId(R.string.paywall_plan_yearly_title)
    },
    subtitle = when (period) {
        BillingPeriod.MONTHLY ->
            TextResource.fromId(R.string.paywall_plan_monthly_body, formattedPrice)

        BillingPeriod.YEARLY ->
            TextResource.fromId(R.string.paywall_plan_yearly_body, formattedPrice)
    },
    badge = if (hasDiscount) TextResource.fromId(R.string.paywall_plan_badge) else null,
    isSelected = isSelected,
)

private val Features: ImmutableList<PaywallFeatureProps> = persistentListOf(
    PaywallFeatureProps(
        icon = AppIcons.FeatureUnlimited,
        title = TextResource.fromId(R.string.paywall_feature_unlimited_title),
        body = TextResource.fromId(R.string.paywall_feature_unlimited_body),
    ),
    PaywallFeatureProps(
        icon = AppIcons.FeatureFaster,
        title = TextResource.fromId(R.string.paywall_feature_faster_title),
        body = TextResource.fromId(R.string.paywall_feature_faster_body),
    ),
    PaywallFeatureProps(
        icon = AppIcons.FeatureDetailed,
        title = TextResource.fromId(R.string.paywall_feature_detailed_title),
        body = TextResource.fromId(R.string.paywall_feature_detailed_body),
    ),
)
