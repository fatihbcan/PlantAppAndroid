plugins {
    id("plantappmvi.android.library")
    id("plantappmvi.android.compose")
}

android { namespace = "com.plantappmvi.android.presentation.home" }

dependencies {
    implementation(project(":domain:home"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:presentation"))
    implementation(libs.coil.compose)
}
