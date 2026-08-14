plugins {
    id("plantappmvi.android.library")
    id("plantappmvi.android.compose")
}

android { namespace = "com.plantappmvi.android.core.presentation" }

dependencies {
    // `api`, not `implementation`: every feature's ViewModel and Screen types
    // appear in the public signatures this module hands out.
    api(project(":core:util"))
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.core.ktx)
}
