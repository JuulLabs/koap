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

    macosX64()
    macosArm64()
    iosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.okio.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-common"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.equalsverifier)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
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
