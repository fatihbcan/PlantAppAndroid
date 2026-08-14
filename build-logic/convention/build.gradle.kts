plugins { `kotlin-dsl` }

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get())) }
}

gradlePlugin {
    plugins {
        register("kotlinLibrary") {
            id = "plantappmvi.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "plantappmvi.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "plantappmvi.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("detekt") {
            id = "plantappmvi.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}
