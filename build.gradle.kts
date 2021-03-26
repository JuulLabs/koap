import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform") version "1.4.21" apply false
    kotlin("js") version "1.4.21" apply false
    kotlin("plugin.serialization") version "1.4.21" apply false
    id("org.jmailen.kotlinter") version "3.2.0" apply false
    id("com.vanniktech.maven.publish") version "0.13.0" apply false
    id("org.jetbrains.dokka") version "1.4.20"
    id("binary-compatibility-validator") version "0.2.3"
    id("lt.petuska.npm.publish") version "1.0.4" apply false
}

allprojects {
    repositories {
        mavenCentral()
        jcenter {
            content {
                // https://youtrack.jetbrains.com/issue/IDEA-261387
                includeModule("org.jetbrains.trove4j", "trove4j")

                // https://github.com/Kotlin/kotlinx.html/issues/173
                includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
            }
        }
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events("started", "passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = FULL
            showExceptions = true
            showStackTraces = true
            showCauses = true
        }
    }
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    val dokkaDir = buildDir.resolve("dokkaHtmlMultiModule")
    outputDirectory.set(dokkaDir)
    doLast {
        dokkaDir.resolve("-modules.html").renameTo(dokkaDir.resolve("index.html"))
    }
}

task<Copy>("assembleGitHubPages") {
    description = "Generates static web site for GitHub Pages."
    group = "Build"

    dependsOn(":webapp:browserDevelopmentWebpack", "dokkaHtmlMultiModule")

    into("$buildDir/gh-pages")
    from("${project(":webapp").buildDir}/distributions") {
        include("**")
    }

    into("docs") {
        from("$buildDir/dokkaHtmlMultiModule") {
            include("**")
        }
    }
}
