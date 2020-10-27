plugins {
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    java // Needed by JaCoCo for multiplatform projects.
    jacoco
    id("com.vanniktech.maven.publish")
    id("lt.petuska.npm.publish")
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
                implementation("com.squareup.okio:okio-multiplatform:2.9.0")
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

npmPublishing {
    organization = "juullabs"

    repositories {
        repository("github") {
            access = RESTRICTED
            registry = uri("https://npm.pkg.github.com")
        }
    }
}
