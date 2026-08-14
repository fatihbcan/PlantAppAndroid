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

include(":core:util :core:presentation :core:build-config :core:designsystem :platform-apis:network :platform-apis:datastore")
include(":domain:onboarding")
include(":data:onboarding")
include(":presentation:onboarding")
