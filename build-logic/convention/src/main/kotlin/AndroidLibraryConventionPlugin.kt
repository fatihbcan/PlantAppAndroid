import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** Applied to every module under `data`, `presentation`, `core` and `platform-apis`. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("dagger.hilt.android.plugin")
        pluginManager.apply("plantappmvi.detekt")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        extensions.configure<LibraryExtension> {
            compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

            defaultConfig {
                minSdk = libs.findVersion("minSdk").get().toString().toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            lint {
                warningsAsErrors = true
                abortOnError = true
                lintConfig = file("${rootProject.projectDir}/config/quality/lint/lint.xml")
            }

            testOptions.unitTests.isIncludeAndroidResources = true
        }

        // Kotlin must be pinned to the same target as Java above, or it
        // silently follows whichever JDK happens to be running Gradle. That
        // makes the build depend on the developer's setup: it passes on a
        // JDK 17 command line and fails in Android Studio, whose bundled JDK
        // is 21, with "Inconsistent JVM-target compatibility" from every KSP
        // task. `withType` rather than the Kotlin extension because the KSP
        // tasks are KotlinCompile instances too, and they are the ones that
        // trip the check first.
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        }

        dependencies {
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())
            add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
            add("testImplementation", libs.findBundle("test-jvm").get())
            add("androidTestImplementation", libs.findBundle("test-android").get())
        }
    }
}
