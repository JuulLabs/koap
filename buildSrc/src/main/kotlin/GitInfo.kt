import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = listOf( "sh", "-c", command)
        standardOutput = byteOut
    }
    return byteOut.toString()
}

fun Project.gitLatestTag(): String =
        runCommand("git describe --tags --abbrev=0").trim()
