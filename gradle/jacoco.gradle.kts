tasks.withType<JacocoReport> {
    reports {
        csv.required.set(false)
        html.required.set(true)
        xml.required.set(true)
    }

    classDirectories.setFrom(file("${buildDir}/classes/kotlin/jvm/"))
    sourceDirectories.setFrom(files("src/commonMain", "src/jvmMain"))
    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    dependsOn("jvmTest")
}
