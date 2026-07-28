@file:OptIn(
    org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class,
    org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class,
)

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    id("org.jmailen.kotlinter")
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(17)

    abiValidation()

    jvm()

    js().browser()
    wasmJs().browser()

    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.okio.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmTest.dependencies {
            implementation(libs.equalsverifier)
        }
    }
}

dokka {
    moduleName.set("KoAP")
    pluginsConfiguration.html {
        footerMessage.set("(c) JUUL Labs, Inc.")
    }
}
