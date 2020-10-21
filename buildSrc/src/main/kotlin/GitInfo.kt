import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = listOf("sh", "-c", command)
        standardOutput = byteOut
    }
    return byteOut.toString()
}

// Returns the most recent tag (not necessarily the highest release version tag)
fun Project.gitMostRecentTag(): String =
    runCommand("git describe --tags --abbrev=0").trim()
