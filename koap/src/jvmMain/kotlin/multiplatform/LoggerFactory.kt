package com.juul.koap.multiplatform

actual object LoggerFactory {
    actual fun createLogger(): Logger = JvmLogger
}

object JvmLogger : Logger {
    override fun logMessage(message: String) {
        println(message)
    }
}