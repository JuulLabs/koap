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
            }
        }
    }
}
