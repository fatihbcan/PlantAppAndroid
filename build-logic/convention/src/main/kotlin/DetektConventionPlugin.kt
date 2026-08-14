import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("io.gitlab.arturbosch.detekt")

        extensions.configure<DetektExtension> {
            parallel = true
            buildUponDefaultConfig = true
            config.setFrom(files("${rootProject.projectDir}/config/quality/detekt/detekt.yml"))
            // No baseline. Ever. A baseline is debt that never gets paid down.
        }
    }
}
