plugins {
    id("plantappmvi.android.library")
    id("plantappmvi.android.compose")
}

android { namespace = "com.plantappmvi.android.core.designsystem" }

dependencies {
    // Component Props are built from TextResource / IconResource, which this
    // module re-exports to every feature that draws.
    api(project(":core:presentation"))
    implementation(libs.coil.compose)
}
