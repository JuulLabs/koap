plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin {
    js(LEGACY) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":koap"))
                implementation(libs.kotlinx.coroutines.js)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.okio.js)
                implementation(npm("cbor", "6.0.1"))
                implementation(npm("stream-browserify", "3.0.0"))
                implementation(npm("util", "0.12.4"))
                implementation(npm("buffer", "6.0.3"))
                implementation(npm("os-browserify", "0.3.0"))
            }
        }
    }
}
