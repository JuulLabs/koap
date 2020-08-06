package com.juul.koap.multiplatform

actual object LoggerFactory {
    actual fun createLogger(): Logger = JsLogger
}

object JsLogger : Logger {
    override fun logMessage(message: String) {
        console.log(message)
    }
}