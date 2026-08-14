plugins {
    id("plantappmvi.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android { namespace = "com.plantappmvi.android.data.home" }

dependencies {
    implementation(project(":domain:home"))
    implementation(project(":core:util"))
    implementation(project(":platform-apis:network"))
}
