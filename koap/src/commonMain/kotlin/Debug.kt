package com.juul.koap

internal fun ByteArray.toHexString(): String {
    return joinToString(" ") {
        formatString("%02X", it)
    }
}

internal fun Int.toHexString(byteCount: Int = Int.SIZE_BYTES): String =
    toLong().toHexString(byteCount)

private fun Long.toHexList(
    byteCount: Int = Long.SIZE_BYTES
): List<String> = ((byteCount - 1) downTo 0).map { i ->
    val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
    formatString("%02X", byte)
}

internal fun Long.toHexString(
    byteCount: Int = Long.SIZE_BYTES
): String = toHexList(byteCount).joinToString(" ")

internal fun Int.debugString(
    byteCount: Int = Int.SIZE_BYTES
): String = "$this (${this.toHexString(byteCount)})"

internal fun Long.debugTokenString(): String {
    if (this == 0L) return "0"
    val hex = toHexList(Long.SIZE_BYTES).dropWhile { it == "00" }.joinToString(" ")
    return "$this ($hex)"
}

expect fun formatString(format: String, vararg args: Any?): String
