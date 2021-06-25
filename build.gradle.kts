import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform") version "1.5.10" apply false
    kotlin("js") version "1.5.10" apply false
    kotlin("plugin.serialization") version "1.5.10" apply false
    id("org.jmailen.kotlinter") version "3.2.0" apply false
    id("com.vanniktech.maven.publish") version "0.14.0" apply false
    id("org.jetbrains.dokka") version "1.4.30"
    id("binary-compatibility-validator") version "0.2.3"
}

allprojects {
    repositories {
        mavenCentral()
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

task<Copy>("assembleGitHubPages") {
    description = "Generates static web site for GitHub Pages."
    group = "Build"

    dependsOn(":webapp:browserDevelopmentWebpack", ":koap:dokkaHtml")

    into("$buildDir/gh-pages")
    from("${project(":webapp").buildDir}/distributions") {
        include("**")
    }

    into("docs") {
        from("${project(":koap").buildDir}/dokka/html") {
            include("**")
        }
    }
}
