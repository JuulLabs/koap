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
            authToken = "notarealtoken"
            version = "0.0.1-placeholderversion1"
        }
    }
}

task<Exec>("apiTestNpmInstall") {
    description = "Builds the JS Koap package and installs it to the test suite for use"
    group = "Verification"
    dependsOn("assembleJsNpmPublication")
    workingDir("apiTests")
    commandLine("npm", "install", "--force", "file://../build/publications/npm/js")
}

task<Exec>("apiTest") {
    description = "Runs the validation package for testing against the built out JS api"
    group = "Verification"
    dependsOn("apiTestNpmInstall")
    workingDir("apiTests")
    commandLine("npm", "run", "test")
}

task<Exec>("apiTestLintBuild") {
    description = "Installs the koap api to the test package along with devDependencies"
    group = "Verification"
    dependsOn("assembleJsNpmPublication")
    workingDir("apiTests")
    commandLine("npm", "install", "--also=dev")
}

task<Exec>("apiTestLint") {
    description = "Runs the linting system for testing against the built out JS api"
    group = "Verification"
    dependsOn("apiTestLintBuild")
    workingDir("apiTests")
    commandLine("npm", "run", "lint")
}

tasks.named("check") {
    dependsOn("apiTestLint")
}
