buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.js) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.api)
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

    withPluginWhenEvaluated("jacoco") {
        tasks.withType<JacocoReport> {
            group = "Verification"
            description = "Generate JaCoCo test coverage report"

            reports {
                csv.required.set(false)
                html.required.set(true)
                xml.required.set(true)
            }

            classDirectories.setFrom(layout.buildDirectory.file("classes/kotlin/jvm"))
            sourceDirectories.setFrom(layout.projectDirectory.files("src/commonMain", "src/jvmMain"))
            executionData.setFrom(layout.buildDirectory.file("jacoco/jvmTest.exec"))

            dependsOn("jvmTest")
        }

        configure<JacocoPluginExtension> {
            toolVersion = libs.versions.jacoco.get()
        }
    }
}

fun Project.withPluginWhenEvaluated(plugin: String, action: Project.() -> Unit) {
    pluginManager.withPlugin(plugin) { whenEvaluated(action) }
}

// `afterEvaluate` does nothing when the project is already in executed state, so we need a special check for this case.
fun <T> Project.whenEvaluated(action: Project.() -> T) {
    if (state.executed) {
        action()
    } else {
        afterEvaluate { action() }
    }
}

task<Copy>("assembleGitHubPages") {
    description = "Generates static web site for GitHub Pages."
    group = "Build"

    dependsOn(":webapp:browserDistribution", ":koap-core:dokkaHtml")

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
