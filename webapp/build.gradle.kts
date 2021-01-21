plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":koap"))
                implementation(kotlinx("coroutines-core-js"))
                implementation(kotlinx("serialization-json"))
                implementation(square("okio-js"))
                implementation(npm("cbor", "6.0.1"))
            }
        }
    }
}
