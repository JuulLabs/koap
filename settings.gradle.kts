rootProject.name = "koap"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(
    ":koap-core",
    ":webapp",
)
