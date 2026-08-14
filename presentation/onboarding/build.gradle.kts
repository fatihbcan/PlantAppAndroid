plugins {
    id("plantappmvi.android.library")
    id("plantappmvi.android.compose")
}

android { namespace = "com.plantappmvi.android.presentation.onboarding" }

dependencies {
    implementation(project(":domain:onboarding"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:presentation"))
}
