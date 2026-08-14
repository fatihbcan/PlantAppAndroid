import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Layered on top of [AndroidLibraryConventionPlugin] for any module that draws.
 *
 * `kotlinx-collections-immutable` is added here on purpose: Props classes
 * require `ImmutableList`, so every Compose module needs it.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        extensions.configure<LibraryExtension> { buildFeatures.compose = true }

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("implementation", libs.findBundle("compose").get())
            add("implementation", libs.findLibrary("compose-navigation").get())
            add("implementation", libs.findLibrary("compose-hilt-navigation").get())
            add("implementation", libs.findLibrary("kotlinx-collections-immutable").get())
            add("debugImplementation", libs.findBundle("compose-debug").get())
            // Versioned by the BOM above, so it can only be added here.
            add("androidTestImplementation", libs.findLibrary("compose-ui-test-junit4").get())
        }
    }
}
