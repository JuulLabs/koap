plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    id("org.jmailen.kotlinter")
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.kover)
}

kotlin {
    jvm()

    js().browser()
    wasmJs().browser()

    macosX64()
    macosArm64()
    iosArm64()

    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.okio.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.equalsverifier)
            }
        }
    }
}

dokka {
    moduleName.set("KoAP")
    pluginsConfiguration.html {
        footerMessage.set("(c) JUUL Labs, Inc.")
    }
}
