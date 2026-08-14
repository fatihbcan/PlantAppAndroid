plugins {
    id("plantappmvi.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android { namespace = "com.plantappmvi.android.platform.network" }

dependencies {
    // Data modules declare their own Retrofit service interfaces, so the
    // Retrofit and serialization types are part of this module's API.
    api(libs.bundles.retrofit.stack)
    implementation(project(":core:util"))
}
