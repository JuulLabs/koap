package com.juul.koap

actual fun formatString(format: String, vararg args: Any?): String = String.format(format, args)

actual fun Long.toHexList(byteCount: Int): List<String> = ((byteCount - 1) downTo 0).map { i ->
    val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
    formatString("%02X", byte)
}