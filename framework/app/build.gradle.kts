plugins {
    id("plantappmvi.android.library")
    id("plantappmvi.android.compose")
}

android { namespace = "com.plantappmvi.android.framework.app" }

/**
 * The composition root. This is the ONLY module allowed to depend on
 * everything — it owns the domain→data bindings, the route constants, the
 * NavHost and every NavigatorImpl. Features stay ignorant of each other
 * precisely because this module is not.
 */
dependencies {
    api(project(":core:presentation"))
    api(project(":core:designsystem"))

    implementation(project(":core:util"))

    implementation(project(":domain:onboarding"))
    implementation(project(":domain:home"))

    implementation(project(":data:onboarding"))
    implementation(project(":data:home"))

    implementation(project(":presentation:onboarding"))
    implementation(project(":presentation:home"))

    implementation(project(":platform-apis:network"))
    implementation(project(":platform-apis:datastore"))
}
