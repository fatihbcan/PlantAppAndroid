plugins { id("plantappmvi.android.library") }

android { namespace = "com.plantappmvi.android.platform.datastore" }

dependencies {
    api(libs.datastore.preferences)
    implementation(project(":core:util"))
}
