plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
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
