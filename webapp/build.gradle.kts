plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }

        val jsMain by getting {
            dependencies {
                implementation(project(":koap-core"))
                implementation(libs.kotlinx.coroutines.js)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.okio.js)
                implementation(npm("buffer", "6.0.3"))
                implementation(npm("cbor", "8.1.0"))
                implementation(npm("process", "0.11.10"))
                implementation(npm("stream-browserify", "3.0.0"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
