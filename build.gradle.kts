buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.api)
    alias(libs.plugins.kover)
}

allprojects {
    group = "com.juul.koap"

    repositories {
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events("started", "passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showStackTraces = true
            showCauses = true
        }
    }
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
    ignoredProjects += "webapp"
}

task<Copy>("assembleGitHubPages") {
    description = "Generates static web site for GitHub Pages."
    group = "Build"

    dependsOn(":webapp:jsBrowserDistribution", ":koap-core:dokkaHtml")

    into("$buildDir/gh-pages")
    from("${project(":webapp").buildDir}/dist/js/productionExecutable") {
        include("**")
    }

    into("docs") {
        from("${project(":koap-core").buildDir}/dokka/html") {
            include("**")
        }
    }
}
