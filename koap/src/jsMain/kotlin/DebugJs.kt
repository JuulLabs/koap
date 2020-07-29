package com.juul.koap

actual fun formatString(format: String, vararg args: Any?): String {
    // TODO: Javascript String.format() equivalent
    return args?.contentToString()
}
