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

include(":core:util")
include(":core:presentation")
include(":core:build-config")
include(":core:designsystem")
include(":platform-apis:network")
include(":platform-apis:datastore")
