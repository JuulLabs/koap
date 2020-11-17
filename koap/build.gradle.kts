plugins {
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
    id("lt.petuska.npm.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(okio("okio-multiplatform"))
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
                implementation(mockk())
                implementation(equalsverifier())
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

npmPublishing {
    organization = "juullabs"

    repositories {
        repository("github") {
            access = RESTRICTED
            registry = uri("https://npm.pkg.github.com")
        }
    }
}

tasks.register("run-js-validation-testing") {

    description = "Runs the package for testing"
    group = "Verification"
    dependsOn("assemble")

    doLast {
        var response = runCommand("../gradlew -Pnpm.publish.repository.github.authToken=fakingit -Pnpm.publish.version=0.0.3-test24 assemble && cd apiTests && npm uninstall @juullabs/koap && npm install file://../build/publications/npm/js && npm run test")
        println("$response")
    }
}