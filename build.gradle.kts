plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
}

// Aggregate test tasks — let CI run fast, pure checks before slow ones.
// Paths are listed explicitly rather than derived from `subprojects`, which
// would break configuration-on-demand and the configuration cache.
val domainTestTasks = listOf(
    ":domain:onboarding:test",
    ":domain:home:test",
)

val dataTestTasks = listOf(
    ":data:onboarding:test",
    ":data:home:test",
    ":platform-apis:network:testDebugUnitTest",
    ":platform-apis:datastore:testDebugUnitTest",
)

val presentationTestTasks = listOf(
    ":presentation:onboarding:testDebugUnitTest",
    ":presentation:home:testDebugUnitTest",
)

tasks.register("runDomainTests") {
    group = "verification"
    description = "Unit tests for all domain modules"
    dependsOn(domainTestTasks)
}

tasks.register("runDataTests") {
    group = "verification"
    description = "Unit tests for all data and platform-api modules"
    dependsOn(dataTestTasks)
}

tasks.register("runPresentationTests") {
    group = "verification"
    description = "Reducer and mapStateToProps tests for all presentation modules"
    dependsOn(presentationTestTasks)
}

tasks.register("runLocalTests") {
    group = "verification"
    description = "Every JVM unit test in the project"
    dependsOn("runDomainTests", "runDataTests", "runPresentationTests")
}
