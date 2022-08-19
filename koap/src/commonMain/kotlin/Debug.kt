package com.juul.koap

private val hexArray = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
)

private fun Byte.toHexString(): String {
    val left = hexArray[(0xF0 and this.toInt()) shr 4]
    val right = hexArray[0x0F and this.toInt()]
    return "$left$right"
}

internal fun ByteArray.toHexString(): String = joinToString(" ") { it.toHexString() }

internal fun Int.toHexString(byteCount: Int = Int.SIZE_BYTES): String =
    toLong().toHexString(byteCount)

private fun Long.toHexList(
    byteCount: Int = Long.SIZE_BYTES,
): List<String> = ((byteCount - 1) downTo 0).map { i ->
    val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
    byte.toHexString()
}

internal fun Long.toHexString(
    byteCount: Int = Long.SIZE_BYTES,
): String = toHexList(byteCount).joinToString(" ")

internal fun Int.debugString(
    byteCount: Int = Int.SIZE_BYTES,
): String = "$this (${this.toHexString(byteCount)})"

internal fun Long.debugTokenString(): String {
    if (this == 0L) return "0"
    val hex = toHexList(Long.SIZE_BYTES).dropWhile { it == "00" }.joinToString(" ")
    return "$this ($hex)"
}
