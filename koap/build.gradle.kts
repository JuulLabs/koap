plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(square("okio-multiplatform"))
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

task<Exec>("apiTestNpmInstall") {
    description = "Installs the koap api to the test package along with devDependencies"
    group = "Verification"
    workingDir("apiTests")
    commandLine("npm", "install", "--also=dev")
}

task<Exec>("apiTestLint") {
    description = "Runs the linting system for testing against the built out JS api"
    group = "Verification"
    dependsOn("apiTestNpmInstall")
    workingDir("apiTests")
    commandLine("npm", "run", "lint")
}

task<Exec>("apiTest") {
    description = "Runs the validation package for testing against the built out JS api"
    group = "Verification"
    dependsOn("apiTestLint")
    workingDir("apiTests")
    commandLine("npm", "run", "test")
}

tasks.named("check") {
    dependsOn("apiTest")
}
