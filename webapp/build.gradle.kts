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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
                implementation("com.squareup.okio:okio-js:2.9.0")
                implementation(npm("cbor", "6.0.1"))
            }
        }
    }
}
