package com.juul.koap

actual fun createHash(vararg values: Any): String {
    return hash(values)
}
