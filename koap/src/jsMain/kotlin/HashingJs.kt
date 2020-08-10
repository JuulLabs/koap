package com.juul.koap

actual fun createHash(vararg values: Any): String {
    return com.juul.koap.multiplatform.hash(values)
}
