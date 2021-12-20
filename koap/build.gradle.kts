plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

kotlin {
    jvm()
    js(BOTH).browser()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.okio)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("os-browserify", "0.3.0"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.mockk)
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
