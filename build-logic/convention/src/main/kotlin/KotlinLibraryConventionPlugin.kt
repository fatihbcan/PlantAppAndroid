import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * Applied to every module under `domain`, and to any data module with no
 * Android dependency.
 *
 * This uses `java-library`, NOT the Android plugin — which is what makes
 * `import android.*` a compile error in the domain layer rather than a review
 * comment. It is the highest-leverage line in the whole build setup.
 */
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("java-library")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("plantappmvi.detekt")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(
                JavaLanguageVersion.of(libs.findVersion("java").get().toString()),
            )
        }

        // JUnit 4, not the JUnit Platform: the version catalog's test bundle
        // is JUnit 4 plus Kotest *assertions* (which are runner-agnostic), and
        // this is also what AGP gives the Android modules — so every module in
        // the build runs tests the same way.
        tasks.withType<Test>().configureEach { testLogging { showStandardStreams = false } }

        dependencies {
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            add("implementation", libs.findLibrary("javax-inject").get())
            add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
            add("testImplementation", libs.findBundle("test-jvm").get())
        }
    }
}
