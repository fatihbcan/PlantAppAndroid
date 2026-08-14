plugins { id("plantappmvi.kotlin.library") }

dependencies {
    // The dispatcher qualifiers are part of this module's public surface, so
    // downstream modules must see `javax.inject` through it.
    api(libs.javax.inject)
}
