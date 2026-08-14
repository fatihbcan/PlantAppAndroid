pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PlantAppMVI"

include(":app")
include(":framework:app")

include(":core:util")
include(":core:presentation")
include(":core:designsystem")
include(":core:build-config")

include(":platform-apis:network")
include(":platform-apis:datastore")

include(":domain:onboarding")
include(":domain:home")

include(":data:onboarding")
include(":data:home")

include(":presentation:onboarding")
include(":presentation:home")
