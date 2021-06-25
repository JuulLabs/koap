fun kotlinx(
    artifact: String,
    version: String? = null
): String {
    val v = version ?: when (val module = artifact.substringBefore("-")) {
        "serialization" -> "1.0.1"
        "coroutines" -> "1.4.2"
        else -> error("Missing version for kotlinx.$module")
    }
    return "org.jetbrains.kotlinx:kotlinx-$artifact:$v"
}

fun square(
    artifact: String,
    version: String? = null
): String {
    val module = artifact.substringBefore("-")
    val v = version ?: when (module) {
        "okio" -> "3.0.0-alpha.6"
        else -> error("Version must be provided for unknown Square module '$module'")
    }
    return "com.squareup.$module:$artifact:$v"
}

fun mockk(
    version: String = "1.10.0"
): String = "io.mockk:mockk:$version"

fun equalsverifier(
    version: String = "3.4"
): String = "nl.jqno.equalsverifier:equalsverifier:$version"
