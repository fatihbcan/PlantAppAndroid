plugins {
    id("plantappmvi.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android { namespace = "com.plantappmvi.android.data.onboarding" }

dependencies {
    implementation(project(":domain:onboarding"))
    implementation(project(":core:util"))
    implementation(project(":platform-apis:datastore"))
    implementation(libs.kotlinx.serialization.json)
}
