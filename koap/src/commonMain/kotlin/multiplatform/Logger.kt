package com.juul.koap.multiplatform

expect object LoggerFactory {
    fun createLogger(): Logger
}

interface Logger {
    fun logMessage(message: String)
}
