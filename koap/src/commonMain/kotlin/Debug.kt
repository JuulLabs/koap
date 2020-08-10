package com.juul.koap


internal expect fun Byte.toHexString(): String

internal fun ByteArray.toHexString(): String = joinToString(" ") { it.toHexString() }

internal fun Int.toHexString(byteCount: Int = Int.SIZE_BYTES): String =
    toLong().toHexString(byteCount)

private fun Long.toHexList(
    byteCount: Int = Long.SIZE_BYTES
): List<String> = ((byteCount - 1) downTo 0).map { i ->
    val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
    byte.toHexString()
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
