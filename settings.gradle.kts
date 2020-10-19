pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://dl.bintray.com/mpetuska/lt.petuska")
        }
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "binary-compatibility-validator" ->
                    useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
                "lt.petuska.npm.publish" ->
                    useModule("lt.petuska.npm.publish:lt.petuska.npm.publish.gradle.plugin:${requested.version}")
            }
        }
    }
}

include(":koap")
