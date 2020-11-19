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

tasks.register("apiTestNpmInstall") {

    description = "Builds the JS Koap package and installs it to the test suite for use"
    group = "Verification"
    dependsOn("assembleJsNpmPublication")

    doLast {
        val response = runCommand("cd apiTests && npm install --force file://../build/publications/npm/js")
        println("$response")
    }
}

tasks.register("apiTest") {

    description = "Runs the validation package for testing against the built out JS api"
    group = "Verification"
    dependsOn("apiTestNpmInstall")

    doLast{
        val response = runCommand("cd apiTests && npm run test")
        println("$response")
    }
}

tasks.register("testLint") {

    description = "Runs the linting system for testing against the built out JS api"
    group = "Verification"

    doLast {
        val response = runCommand("cd apiTests && npm run lint")
        println("$response")
    }
}

tasks.named("check") {
    dependsOn("testLint")
}
