import lt.petuska.npm.publish.dsl.NpmAccess.RESTRICTED
import lt.petuska.npm.publish.task.NpmPackageAssembleTask

plugins {
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
    id("lt.petuska.npm.publish")
}

kotlinter {
    disabledRules = arrayOf("no-multi-spaces")
}

tasks.withType<JacocoReport> {
    reports {
        csv.isEnabled = false
        html.isEnabled = true
        xml.isEnabled = true
    }

    classDirectories.setFrom(file("${buildDir}/classes/kotlin/jvm/"))
    sourceDirectories.setFrom(files("src/commonMain", "src/jvmMain"))
    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    dependsOn("jvmTest")
}

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib"))
                implementation("com.squareup.okio:okio-multiplatform:2.6.0")
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
                implementation("io.mockk:mockk:1.10.0")
                implementation("nl.jqno.equalsverifier:equalsverifier:3.4")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

// TODO: Put this in a separate file?
fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}

tasks.withType<NpmPackageAssembleTask> {
    // TODO: change to jsBrowserProductionWebpack for production
    // Webpack outputs artifacts to ${buildDir}/distributions
    dependsOn("jsBrowserDevelopmentWebpack")
}

npmPublishing {
    organization = "juullabs"
    access = NpmAccess.RESTRICTED

    repositories {
        repository("github") {
            access = NpmAccess.RESTRICTED
            registry = uri("https://npm.pkg.github.com")
        }
    }

    publications {
        publication("$name") {
            moduleName = name
            main = "$name.js"

            files {
                // Pick up the Webpack artifacts
                from("$buildDir/distributions")
            }

            dependencies {
                // Peer dependencies: https://docs.npmjs.com/files/package.json#peerdependencies
                // For any peer packages listed here, the consumer of this library will need to perform
                // `npm install <pkg>` to pick up a version that matches the version pattern provided
                npmPeer("kotlin", "^1.4")
            }

            packageJson {
                // TODO: set private = false when working
                private = true
                version = "$rootDir/gradle/gitLatestTag.sh".runCommand()
            }
        }
    }
}
