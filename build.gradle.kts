buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        // Workaround for:
        // > Incompatible version of Kotlin metadata.
        // > Maximal supported Kotlin metadata version: 1.5.1,
        // > com/juul/koap/ByteArrayReader Kotlin metadata version: 1.7.1.
        // > As a workaround, it is possible to manually update 'kotlinx-metadata-jvm' version in your project.
        //
        // todo: Remove when binary-compatibility-validator bundles support for metadata 1.7.x.
        classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.js) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binary.compatibility.validator)
}

allprojects {
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

task<Copy>("assembleGitHubPages") {
    description = "Generates static web site for GitHub Pages."
    group = "Build"

    dependsOn(":webapp:browserDevelopmentWebpack", ":koap:dokkaHtml")

    into("$buildDir/gh-pages")
    from("${project(":webapp").buildDir}/developmentExecutable") {
        include("**")
    }

    into("docs") {
        from("${project(":koap").buildDir}/dokka/html") {
            include("**")
        }
    }
}
