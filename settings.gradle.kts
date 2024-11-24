pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        mavenLocal() // to publish plugin locally
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "lib-test-app"
include(":app")
include(":logger")
