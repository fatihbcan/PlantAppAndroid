package com.plantappmvi.android.framework.app.navigation

/**
 * Every route in the app, declared once, here.
 *
 * A feature never sees one of these constants. It declares a `Navigator`
 * interface saying *where* it can go, and this module decides *how* — which is
 * what lets `presentation:onboarding` and `presentation:home` stay ignorant of
 * each other's existence.
 */
internal object AppRoutes {
    const val INTRO = "intro"
    const val PAYWALL = "paywall"
    const val HOME = "home"
}
