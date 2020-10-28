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
