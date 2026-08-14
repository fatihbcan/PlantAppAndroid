package com.plantappmvi.android.core.designsystem.icon

import com.plantappmvi.android.core.designsystem.R
import com.plantappmvi.android.core.presentation.resource.IconResource

/**
 * The design's own glyphs, exported from the Figma file.
 *
 * None of these exist in Material's icon set — the viewfinder, the lidded jar,
 * the diagnose shield, the garden leaf and the dial are drawn for this product
 * — and approximating them by eye is exactly what made the bottom bar and the
 * paywall's feature strip read wrong in the first pass.
 *
 * They ship at 1x for the file's 360dp frame, hence `drawable-mdpi`: Android
 * then scales them per density exactly as the Flutter build does. A 3x
 * re-export is a straight file swap.
 */
object AppIcons {
    /** Viewfinder around a card — plant identification. */
    val Scan = IconResource(R.drawable.ic_scan)

    /** Dial — faster processing. */
    val Gauge = IconResource(R.drawable.ic_speedometer)

    /** Leaf — the "My Garden" destination. */
    val Leaf = IconResource(R.drawable.ic_nav_garden)

    /** Lidded jar — the "Home" destination. */
    val Pot = IconResource(R.drawable.ic_nav_home)

    /** Shield with a cross — the "Diagnose" destination. */
    val ShieldPlus = IconResource(R.drawable.ic_nav_diagnose)

    /** Bust — the "Profile" destination. */
    val Person = IconResource(R.drawable.ic_nav_profile)

    /** Magnifier — the home search field. */
    val Search = IconResource(R.drawable.ic_search)

    /** Chevron — the premium strip's affordance. */
    val ChevronRight = IconResource(R.drawable.ic_chevron_right)

    /** Cross — the paywall's close control, and clearing the search field. */
    val Close = IconResource(R.drawable.ic_close)

    /**
     * The gilded envelope and its unread counter on the premium strip. Full
     * colour, so it is drawn as exported rather than tinted.
     */
    val EnvelopeBadge = IconResource(R.drawable.ic_envelope_badge, isTintable = false)

    /**
     * The paywall's three feature tiles. The design ships these as complete
     * marks — tinted ground and glyph together — so they are drawn whole
     * rather than rebuilt from a bare glyph on a box of our own.
     */
    val FeatureUnlimited = IconResource(R.drawable.ic_feature_unlimited, isTintable = false)
    val FeatureFaster = IconResource(R.drawable.ic_feature_faster, isTintable = false)
    val FeatureDetailed = IconResource(R.drawable.ic_feature_detailed, isTintable = false)
}
