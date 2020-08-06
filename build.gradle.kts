buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    kotlin("multiplatform") version "1.4.10" apply false
    id("org.jmailen.kotlinter") version "2.2.0" apply false
    id("com.vanniktech.maven.publish") version "0.13.0" apply false
    id("org.jetbrains.dokka") version "1.4.10"
    id("binary-compatibility-validator") version "0.2.3"
}

allprojects {
    repositories {
        jcenter()
    }
}

subprojects {
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
